package Controller;

import BusinessBLL.LoaiPhongBLL;
import EntitiesDTO.LoaiPhong;
import Utilities.Others;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.text.DecimalFormat;
import java.util.List;

public class QuanLyLoaiPhongController {
    @FXML private HBox mainPane;
    @FXML private TableView<LoaiPhong> tvLoaiPhong;
    @FXML private TableColumn<LoaiPhong, String> colTen, colGia, colSoNguoi;

    @FXML private Label lblFormTitle;
    @FXML private TextField txtTenLoai, txtGia, txtSoNguoi;
    @FXML private TextArea txtMoTa;
    @FXML private Button btnSave, btnClear;

    private ObservableList<LoaiPhong> masterData = FXCollections.observableArrayList();
    private LoaiPhong currentSelected = null;

    @FXML
    public void initialize() {
        Others.animateTableRows(tvLoaiPhong);

        // 1. RÀNG BUỘC Ô SỐ NGƯỜI (Chỉ nhập số, Max 2 chữ số)
        Others.setNumericOnly(txtSoNguoi);
        Others.setMaxLength(txtSoNguoi, 2);

        // 2. RÀNG BUỘC Ô ĐƠN GIÁ (Max 10 chữ số, tự động format tiền tệ)
        // Khi đang gõ: chặn chữ, chặn ký tự lạ, giữ tối đa 10 số
        txtGia.textProperty().addListener((obs, oldVal, newVal) -> {
            if (txtGia.isFocused()) {
                String raw = newVal.replaceAll("[^\\d]", "");
                if (raw.length() > 10) {
                    raw = raw.substring(0, 10);
                }
                if (!newVal.equals(raw)) {
                    txtGia.setText(raw);
                }
            }
        });

        // Khi click ra ngoài / click vào: Xử lý hiển thị "500.000đ"
        txtGia.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (isFocused) {
                // Xóa định dạng để dễ sửa
                String raw = txtGia.getText().replaceAll("[^\\d]", "");
                txtGia.setText(raw);
            } else {
                // Format thành tiền có chấm và 'đ'
                String raw = txtGia.getText().replaceAll("[^\\d]", "");
                if (!raw.isEmpty()) {
                    try {
                        long val = Long.parseLong(raw);
                        DecimalFormat formatter = new DecimalFormat("#,###");
                        txtGia.setText(formatter.format(val).replace(",", ".") + "đ");
                    } catch(Exception e){}
                } else {
                    txtGia.setText("");
                }
            }
        });

        setupColumns();
        setButtons();
        setupListeners();
        loadData();
    }

    private void setupColumns() {
        tvLoaiPhong.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colTen.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTenLoaiPhong()));
        colGia.setCellValueFactory(d -> new SimpleStringProperty(Others.formatPrice(d.getValue().getDonGia())));
        colSoNguoi.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSoNguoiToiDa() + " Người"));
    }

    private void setButtons() {
        Others.playButtonAnimation(btnSave);
        Others.playButtonAnimation(btnClear);
    }

    private void setupListeners() {
        tvLoaiPhong.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentSelected = newVal;
                lblFormTitle.setText("CẬP NHẬT LOẠI PHÒNG");
                lblFormTitle.setStyle("-fx-text-fill: #f59e0b;"); // Đổi sang màu vàng khi sửa

                txtTenLoai.setText(newVal.getTenLoaiPhong());

                // Hiển thị giá tiền dạng format đẹp (VD: 500.000đ) khi click vào 1 dòng
                DecimalFormat formatter = new DecimalFormat("#,###");
                txtGia.setText(formatter.format(newVal.getDonGia()).replace(",", ".") + "đ");

                txtSoNguoi.setText(String.valueOf(newVal.getSoNguoiToiDa()));
                txtMoTa.setText(newVal.getMoTa());
            }
        });
    }

    private void loadData() {
        masterData.clear();
        List<LoaiPhong> list = LoaiPhongBLL.getAllLoaiPhong();
        masterData.addAll(list);
        tvLoaiPhong.setItems(masterData);
    }

    @FXML
    void handleClear() {
        currentSelected = null;
        tvLoaiPhong.getSelectionModel().clearSelection();

        lblFormTitle.setText("THÊM LOẠI PHÒNG MỚI");
        lblFormTitle.setStyle("-fx-text-fill: #3b82f6;"); // Màu xanh khi thêm mới

        txtTenLoai.clear();
        txtGia.clear();
        txtSoNguoi.clear();
        txtMoTa.clear();
    }

    @FXML
    void handleSave() {
        String ten = txtTenLoai.getText().trim();
        // Lột bỏ chữ 'đ' và dấu '.' trước khi kiểm tra rỗng
        String giaStr = txtGia.getText().replaceAll("[^\\d]", "").trim();
        String soNguoiStr = txtSoNguoi.getText().replaceAll("[^\\d]", "").trim();

        if (ten.isEmpty() || giaStr.isEmpty() || soNguoiStr.isEmpty()) {
            Others.showAlert(mainPane, "Vui lòng nhập đủ Tên, Giá và Số người!", true);
            return;
        }

        try {
            double donGia = Double.parseDouble(giaStr);
            int soNguoi = Integer.parseInt(soNguoiStr);

            LoaiPhong lp = new LoaiPhong();
            lp.setTenLoaiPhong(ten);
            lp.setDonGia(donGia);
            lp.setSoNguoiToiDa(soNguoi);
            lp.setMoTa(txtMoTa.getText().trim());

            boolean isEdit = (currentSelected != null);
            if (isEdit) {
                lp.setMaLoaiPhong(currentSelected.getMaLoaiPhong());
            }

            String result = BusinessBLL.LoaiPhongBLL.saveLoaiPhong(lp, isEdit);
            if ("SUCCESS".equals(result)) {
                Others.showAlert(mainPane, isEdit ? "Cập nhật thành công!" : "Thêm mới thành công!", false);
                loadData();
                handleClear();
            } else {
                Others.showAlert(mainPane, result, true);
            }

        } catch (NumberFormatException e) {
            Others.showAlert(mainPane, "Lỗi: Đơn giá và Số người phải là số hợp lệ!", true);
        }
    }
}