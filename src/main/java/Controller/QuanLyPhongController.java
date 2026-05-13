package Controller;

import BusinessBLL.PhongBLL;
import DataDAL.PhongDAL;
import Utilities.Others;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class QuanLyPhongController {
    @FXML private VBox mainPane;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbFilterLoaiPhong;
    @FXML private TableView<RoomRow> tvPhong;
    @FXML private TableColumn<RoomRow, String> colSoPhong, colLoaiPhong, colDonGia, colSucChua, colTrangThai;
    @FXML private Button btnAdd, btnEdit, btnToggleStatus, btnQuanLyLoaiPhong;

    private ObservableList<RoomRow> masterData = FXCollections.observableArrayList();
    private FilteredList<RoomRow> filteredData;

    @FXML
    public void initialize() {
        Others.animateTableRows(tvPhong);
        setupColumns();
        setupButtons();
        setupListeners();
        loadData();
    }

    private void setupColumns() {
        tvPhong.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colSoPhong.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().soPhong));
        colLoaiPhong.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tenLoaiPhong));
        colDonGia.setCellValueFactory(d -> new SimpleStringProperty(Others.formatPrice(d.getValue().donGia)));
        colSucChua.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().sucChua + " Người"));
        colTrangThai.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().trangThai));
    }

    private void setupButtons() {
        Others.playButtonAnimation(btnAdd);
        Others.playButtonAnimation(btnEdit);
        Others.playButtonAnimation(btnQuanLyLoaiPhong);
        Others.playButtonAnimation(btnToggleStatus);
    }

    private void setupListeners() {
        txtSearch.textProperty().addListener((o, old, newVal) -> applyFilters());
        cbFilterLoaiPhong.valueProperty().addListener((o, old, newVal) -> applyFilters());

        btnEdit.setDisable(true);
        btnToggleStatus.setDisable(true);

        tvPhong.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            boolean isSelected = (newVal != null);
            btnEdit.setDisable(!isSelected);

            if (isSelected) {
                String currentStatus = newVal.trangThai;

                if ("Đang có khách".equals(currentStatus)) {
                    btnToggleStatus.setDisable(true);
                    btnToggleStatus.setText("🚫 ĐANG CÓ KHÁCH");
                    btnToggleStatus.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
                }

                else if ("Bảo trì".equals(currentStatus)) {
                    btnToggleStatus.setDisable(false);
                    btnToggleStatus.setText("✅ MỞ KINH DOANH");
                    btnToggleStatus.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 8;");
                }

                else {
                    btnToggleStatus.setDisable(false);
                    btnToggleStatus.setText("🔒 TẠM ĐÓNG PHÒNG");
                    btnToggleStatus.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 8;");
                }
            } else {
                btnToggleStatus.setDisable(true);
                btnToggleStatus.setText("🔒 TẠM ĐÓNG PHÒNG");
                btnToggleStatus.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
            }
        });
    }

    private void loadData() {
        masterData.clear();
        List<PhongDAL.PhongViewModel> list = PhongBLL.getDanhSachPhongFull();
        for (PhongDAL.PhongViewModel p : list) {
            masterData.add(new RoomRow(p.maPhong, p.soPhong, p.tenLoaiPhong, p.donGia, p.soNguoiToiDa, p.trangThai));
        }

        List<String> loaiPhongs = list.stream().map(p -> p.tenLoaiPhong).distinct().collect(Collectors.toList());
        loaiPhongs.add(0, "Tất cả");
        cbFilterLoaiPhong.setItems(FXCollections.observableArrayList(loaiPhongs));
        cbFilterLoaiPhong.getSelectionModel().select(0);

        filteredData = new FilteredList<>(masterData, p -> true);
        SortedList<RoomRow> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tvPhong.comparatorProperty());
        tvPhong.setItems(sortedData);
    }

    private void applyFilters() {
        if (filteredData == null) return;
        String keyword = txtSearch.getText().toLowerCase().trim();
        String loaiPhong = cbFilterLoaiPhong.getValue();

        filteredData.setPredicate(row -> {
            boolean matchKeyword = keyword.isEmpty() || row.soPhong.toLowerCase().contains(keyword);
            boolean matchType = loaiPhong == null || loaiPhong.equals("Tất cả") || row.tenLoaiPhong.equals(loaiPhong);
            return matchKeyword && matchType;
        });
    }

    @FXML
    void handleAddNew() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PhongForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm phòng mới");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Không thể mở form thêm phòng!", true);
        }
    }

    @FXML
    void handleEdit() {
        RoomRow selected = tvPhong.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Others.showAlert(mainPane, "Vui lòng chọn phòng cần sửa!", true);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PhongForm.fxml"));
            Parent root = loader.load();

            PhongFormController controller = loader.getController();
            controller.setPhong(selected.maPhong);

            Stage stage = new Stage();
            stage.setTitle("Chỉnh sửa thông tin phòng");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Lỗi khi tải thông tin phòng!", true);
        }
    }

    @FXML
    void handleToggleStatus() {
        RoomRow selected = tvPhong.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if ("Đang có khách".equals(selected.trangThai)) {
            Others.showAlert(mainPane, "Phòng đang có khách, không thể tạm đóng!", true);
            return;
        }

        String newStatus;
        String confirmMsg;

        if ("Bảo trì".equals(selected.trangThai)) {
            newStatus = "Trống";
            confirmMsg = "Mở kinh doanh lại phòng " + selected.soPhong + "?";
        } else {
            newStatus = "Bảo trì";
            confirmMsg = "Xác nhận tạm đóng phòng " + selected.soPhong + "?\nLễ tân sẽ không thấy phòng này trên sơ đồ.";
        }

        if (Others.showCustomConfirm("Thay đổi trạng thái kinh doanh", confirmMsg, "Xác nhận", "Hủy bỏ")) {
            if (BusinessBLL.PhongBLL.updateRoomStatus(selected.soPhong, newStatus)) { // [cite: 31]
                Others.showAlert(mainPane, "Đã cập nhật trạng thái phòng thành công!", false);
                loadData();
            } else {
                Others.showAlert(mainPane, "Lỗi hệ thống, không thể cập nhật!", true);
            }
        }
    }

    @FXML void handleManageRoomType() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoaiPhongView.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Quản lý danh mục Loại Phòng");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));

            stage.showAndWait();
            loadData();

        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Lỗi khi mở giao diện Quản lý Loại phòng!", true);
        }
    }

    public static class RoomRow {
        int maPhong;
        String soPhong, tenLoaiPhong, trangThai;
        double donGia;
        int sucChua;

        public RoomRow(int maPhong, String soPhong, String tenLoaiPhong, double donGia, int sucChua, String trangThai) {
            this.maPhong = maPhong; this.soPhong = soPhong; this.tenLoaiPhong = tenLoaiPhong;
            this.donGia = donGia; this.sucChua = sucChua; this.trangThai = trangThai;
        }
    }
}