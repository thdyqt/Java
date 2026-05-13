package View;

import BusinessBLL.KhachHangBLL;
import EntitiesDTO.KhachHang;
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

public class QuanLyKhachHangController {
    @FXML private VBox mainPane;
    @FXML private TextField txtSearch;
    @FXML private TableView<CustomerRow> tvKhachHang;
    @FXML private TableColumn<CustomerRow, String> colMaKH, colHoTen, colCCCD, colSDT, colEmail, colDiaChi;
    @FXML private Button btnAdd, btnEdit, btnViewHistory;

    private ObservableList<CustomerRow> masterData = FXCollections.observableArrayList();
    private FilteredList<CustomerRow> filteredData;

    @FXML
    public void initialize() {
        Others.animateTableRows(tvKhachHang);
        setupColumns();
        setupButtons();
        setupListeners();
        loadData();
    }

    private void setupColumns() {
        tvKhachHang.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colMaKH.setCellValueFactory(d -> new SimpleStringProperty("KH-" + d.getValue().getMaKH()));
        colHoTen.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getHoTen()));
        colCCCD.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCccd()));
        colSDT.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSdt()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail() != null ? d.getValue().getEmail() : ""));
        colDiaChi.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDiaChi() != null ? d.getValue().getDiaChi() : ""));

        colMaKH.prefWidthProperty().bind(tvKhachHang.widthProperty().multiply(0.08));
        colHoTen.prefWidthProperty().bind(tvKhachHang.widthProperty().multiply(0.22));
        colCCCD.prefWidthProperty().bind(tvKhachHang.widthProperty().multiply(0.15));
        colSDT.prefWidthProperty().bind(tvKhachHang.widthProperty().multiply(0.12));
        colEmail.prefWidthProperty().bind(tvKhachHang.widthProperty().multiply(0.18));
        colDiaChi.prefWidthProperty().bind(tvKhachHang.widthProperty().multiply(0.25));
    }

    private void setupButtons() {
        Others.playButtonAnimation(btnAdd);
        Others.playButtonAnimation(btnEdit);
        Others.playButtonAnimation(btnViewHistory);

        btnEdit.setDisable(true);
        btnViewHistory.setDisable(true);
    }

    private void setupListeners() {
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        tvKhachHang.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                btnEdit.setDisable(false);
                btnViewHistory.setDisable(false);
            } else {
                btnEdit.setDisable(true);
                btnViewHistory.setDisable(true);
            }
        });
    }

    @FXML
    private void loadData() {
        masterData.clear();
        List<KhachHang> list = KhachHangBLL.getAllCustomers();

        for (KhachHang kh : list) {
            masterData.add(new CustomerRow(
                    kh.getMaKhachHang(),
                    kh.getHoTen(),
                    kh.getCccdPassport(),
                    kh.getSoDienThoai(),
                    kh.getEmail(),
                    kh.getDiaChi()
            ));
        }

        filteredData = new FilteredList<>(masterData, p -> true);

        SortedList<CustomerRow> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tvKhachHang.comparatorProperty());

        tvKhachHang.setItems(sortedData);
        tvKhachHang.getSelectionModel().clearSelection();

        applyFilters();
    }

    private void applyFilters() {
        if (filteredData == null) return;

        String keyword = txtSearch.getText().toLowerCase().trim();

        filteredData.setPredicate(row -> {
            if (keyword.isEmpty()) return true;

            return row.getHoTen().toLowerCase().contains(keyword) ||
                    (row.getSdt() != null && row.getSdt().contains(keyword)) ||
                    (row.getCccd() != null && row.getCccd().toLowerCase().contains(keyword)) ||
                    (row.getEmail() != null && row.getEmail().toLowerCase().contains(keyword));
        });
    }

    @FXML
    void handleAddNewCustomer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/KhachHangForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm Khách Hàng Mới");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void handleEditCustomer() {
        CustomerRow selected = tvKhachHang.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/KhachHangForm.fxml"));
            Parent root = loader.load();

            KhachHangFormController controller = loader.getController();

            KhachHang kh = new KhachHang(selected.getMaKH(), selected.getHoTen(),
                    selected.getCccd(), selected.getSdt(),
                    selected.getEmail(), selected.getDiaChi());
            controller.setCustomerData(kh);

            Stage stage = new Stage();
            stage.setTitle("Cập Nhật Thông Tin Khách Hàng");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void handleViewBookingHistory() {
        CustomerRow selected = tvKhachHang.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LichSuKhachHangView.fxml"));
            Parent root = loader.load();

            LichSuKhachHangController controller = loader.getController();
            controller.loadHistory(selected.getMaKH(), selected.getHoTen());

            Stage stage = new Stage();
            stage.setTitle("Lịch Sử Lưu Trú - " + selected.getHoTen());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Không thể mở màn hình lịch sử!", true);
        }
    }

    public static class CustomerRow {
        private int maKH;
        private String hoTen, cccd, sdt, email, diaChi;

        public CustomerRow(int maKH, String hoTen, String cccd, String sdt, String email, String diaChi) {
            this.maKH = maKH;
            this.hoTen = hoTen;
            this.cccd = cccd;
            this.sdt = sdt;
            this.email = email;
            this.diaChi = diaChi;
        }

        public int getMaKH() { return maKH; }
        public String getHoTen() { return hoTen; }
        public String getCccd() { return cccd; }
        public String getSdt() { return sdt; }
        public String getEmail() { return email; }
        public String getDiaChi() { return diaChi; }
    }
}