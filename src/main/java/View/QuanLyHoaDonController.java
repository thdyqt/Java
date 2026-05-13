package View;

import BusinessBLL.HoaDonBLL;
import DataDAL.HoaDonDAL;
import EntitiesDTO.HoaDon;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class QuanLyHoaDonController {
    @FXML private VBox mainPane;
    @FXML private TextField txtSearch;
    @FXML private DatePicker dpTuNgay, dpDenNgay;
    @FXML private TableView<InvoiceRow> tvHoaDon;
    @FXML private TableColumn<InvoiceRow, String> colMaHD, colTenKhach, colSDT, colNgayThanhToan, colPhuongThuc, colTongTien;
    @FXML private Button btnViewDetails, btnResetFilter;

    private ObservableList<InvoiceRow> masterData = FXCollections.observableArrayList();
    private FilteredList<InvoiceRow> filteredData;

    @FXML
    public void initialize() {
        Others.animateTableRows(tvHoaDon);
        setupColumns();
        setupButtons();
        setupListeners();
        loadData();
    }

    private void setupColumns() {
        tvHoaDon.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colMaHD.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().maHDStr));
        colTenKhach.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tenKhach));
        colSDT.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().sdt));
        colNgayThanhToan.setCellValueFactory(d -> new SimpleStringProperty(Others.formatDateTime(d.getValue().ngayLap)));
        colPhuongThuc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().phuongThuc));
        colTongTien.setCellValueFactory(d -> new SimpleStringProperty(Others.formatPrice(d.getValue().tongTien)));
    }

    private void setupButtons() {
        Others.playButtonAnimation(btnResetFilter);
        Others.playButtonAnimation(btnViewDetails);
    }

    private void setupListeners() {
        txtSearch.textProperty().addListener((o, old, newVal) -> applyFilters());
        dpTuNgay.valueProperty().addListener((o, old, newVal) -> applyFilters());
        dpDenNgay.valueProperty().addListener((o, old, newVal) -> applyFilters());

        btnViewDetails.setDisable(true);
        tvHoaDon.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            btnViewDetails.setDisable(newVal == null);
        });
    }

    private void loadData() {
        masterData.clear();
        List<HoaDonDAL.HoaDonViewModel> list = HoaDonBLL.getDanhSachHoaDon();
        for (HoaDonDAL.HoaDonViewModel hd : list) {
            masterData.add(new InvoiceRow(hd.maHoaDon, hd.tenKhachHang, hd.soDienThoai, hd.ngayThanhToan, hd.tongThanhToan, hd.phuongThuc));
        }

        filteredData = new FilteredList<>(masterData, p -> true);
        SortedList<InvoiceRow> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tvHoaDon.comparatorProperty());
        tvHoaDon.setItems(sortedData);
    }

    private void applyFilters() {
        if (filteredData == null) return;
        String keyword = txtSearch.getText().toLowerCase().trim();
        LocalDate tuNgay = dpTuNgay.getValue();
        LocalDate denNgay = dpDenNgay.getValue();

        filteredData.setPredicate(row -> {
            boolean matchKeyword = keyword.isEmpty() ||
                    row.maHDStr.toLowerCase().contains(keyword) ||
                    row.tenKhach.toLowerCase().contains(keyword);

            LocalDate rowDate = row.ngayLap.toLocalDate();
            boolean matchDate = true;
            if (tuNgay != null && rowDate.isBefore(tuNgay)) matchDate = false;
            if (denNgay != null && rowDate.isAfter(denNgay)) matchDate = false;

            return matchKeyword && matchDate;
        });
    }

    @FXML void handleResetFilter() {
        txtSearch.clear();
        dpTuNgay.setValue(null);
        dpDenNgay.setValue(null);
    }

    @FXML void handleViewDetails() {
        InvoiceRow selected = tvHoaDon.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            HoaDon hdFull = HoaDonBLL.getInvoiceById(selected.maHoaDon);
            if (hdFull == null) {
                Others.showAlert(mainPane, "Không tìm thấy dữ liệu hóa đơn này!", true);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HoaDonView.fxml")); // Hoặc tên file FXML chứa HoaDonController của bạn
            Parent root = loader.load();

            HoaDonController controller = loader.getController();
            controller.setInvoiceData(hdFull);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết Hóa Đơn");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Lỗi khi mở hóa đơn điện tử!", true);
        }
    }

    public static class InvoiceRow {
        int maHoaDon;
        String maHDStr;
        String tenKhach;
        String sdt;
        LocalDateTime ngayLap;
        double tongTien;
        String phuongThuc;

        public InvoiceRow(int maHoaDon, String tenKhach, String sdt, LocalDateTime ngayLap, double tongTien, String phuongThuc) {
            this.maHoaDon = maHoaDon;
            this.maHDStr = "HD-" + String.format("%04d", maHoaDon);
            this.tenKhach = tenKhach;
            this.sdt = sdt;
            this.ngayLap = ngayLap;
            this.tongTien = tongTien;
            this.phuongThuc = phuongThuc;
        }
    }
}