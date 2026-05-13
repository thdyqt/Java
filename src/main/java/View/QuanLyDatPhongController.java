package View;

import BusinessBLL.DatPhongBLL;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class QuanLyDatPhongController {
    @FXML private VBox mainPane;
    @FXML private TextField txtSearch;
    @FXML private DatePicker dpFromDate, dpToDate;
    @FXML private HBox statusFilterBox;

    @FXML private TableView<BookingRow> tvDatPhong;
    @FXML private TableColumn<BookingRow, String> colMaDat, colKhachHang, colSDT, colPhong, colCheckIn, colCheckOut, colTienCoc, colTrangThai;

    @FXML private Button btnBook, btnEditBooking, btnQuickCheckIn, btnQuickCheckOut, btnCancelBooking;

    private ObservableList<BookingRow> masterData = FXCollections.observableArrayList();
    private FilteredList<BookingRow> filteredData;
    private ToggleGroup statusGroup = new ToggleGroup();

    @FXML
    public void initialize() {
        Others.animateTableRows(tvDatPhong);
        setupColumns();
        setupButtons();
        setupStatusTabs();
        setupListeners();
        loadData();
    }

    private void setupColumns() {
        tvDatPhong.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colMaDat.setCellValueFactory(d -> new SimpleStringProperty("HD-" + d.getValue().maDat));
        colKhachHang.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().hoTen));
        colSDT.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().sdt));
        colPhong.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().dsPhong != null ? d.getValue().dsPhong : ""));
        colCheckIn.setCellValueFactory(d -> new SimpleStringProperty(Others.formatDateTime(d.getValue().ngayIn)));
        colCheckOut.setCellValueFactory(d -> new SimpleStringProperty(Others.formatDateTime(d.getValue().ngayOut)));
        colTienCoc.setCellValueFactory(d -> new SimpleStringProperty(Others.formatPrice(d.getValue().tienCoc)));
        colTrangThai.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().trangThai));

        colMaDat.prefWidthProperty().bind(tvDatPhong.widthProperty().multiply(0.08));
        colKhachHang.prefWidthProperty().bind(tvDatPhong.widthProperty().multiply(0.20));
        colSDT.prefWidthProperty().bind(tvDatPhong.widthProperty().multiply(0.12));
        colPhong.prefWidthProperty().bind(tvDatPhong.widthProperty().multiply(0.15));
        colCheckIn.prefWidthProperty().bind(tvDatPhong.widthProperty().multiply(0.12));
        colCheckOut.prefWidthProperty().bind(tvDatPhong.widthProperty().multiply(0.12));
        colTienCoc.prefWidthProperty().bind(tvDatPhong.widthProperty().multiply(0.10));
        colTrangThai.prefWidthProperty().bind(tvDatPhong.widthProperty().multiply(0.11));
    }

    private void setupButtons() {
        Others.playButtonAnimation(btnBook);
        Others.playButtonAnimation(btnEditBooking);
        Others.playButtonAnimation(btnQuickCheckIn);
        Others.playButtonAnimation(btnQuickCheckOut);
        Others.playButtonAnimation(btnCancelBooking);
    }

    private void setupStatusTabs() {
        statusFilterBox.getChildren().clear();
        String[] statuses = {"Tất cả", "Chờ nhận phòng", "Đang ở", "Đã trả phòng", "Đã hủy"};

        for (String status : statuses) {
            ToggleButton btn = new ToggleButton(status);
            btn.setToggleGroup(statusGroup);
            btn.setMinWidth(Region.USE_PREF_SIZE);

            String activeColor = getStatusColor(status);

            String baseStyle = "-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-cursor: hand; -fx-padding: 8 18; -fx-background-radius: 8; -fx-font-size: 13px;";

            String activeStyle = "-fx-background-color: " + activeColor + "; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-padding: 8 18; -fx-background-radius: 8; -fx-font-size: 13px; " +
                    "-fx-effect: dropshadow(three-pass-box, " + activeColor + "66, 10, 0, 0, 4);";

            btn.setStyle(baseStyle);

            btn.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    btn.setStyle(activeStyle);
                    applyFilters();
                } else {
                    btn.setStyle(baseStyle);
                }
            });

            statusFilterBox.getChildren().add(btn);

            if (status.equals("Tất cả")) {
                btn.setSelected(true);
            }
        }
    }

    private String getStatusColor(String status) {
        return switch (status) {
            case "Tất cả" -> "#3b82f6";
            case "Chờ nhận phòng" -> "#f59e0b";
            case "Đang ở" -> "#10b981";
            case "Đã trả phòng" -> "#64748b";
            case "Đã hủy" -> "#ef4444";
            default -> "#3b82f6";
        };
    }

    private void setupListeners() {
        txtSearch.textProperty().addListener((obs, old, newVal) -> applyFilters());
        dpFromDate.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        dpToDate.valueProperty().addListener((obs, old, newVal) -> applyFilters());

        tvDatPhong.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String st = newSelection.trangThai;
                btnEditBooking.setDisable(!st.equals("Chờ nhận phòng"));
                btnQuickCheckIn.setDisable(!st.equals("Chờ nhận phòng"));
                btnCancelBooking.setDisable(!st.equals("Chờ nhận phòng"));
                btnQuickCheckOut.setDisable(!st.equals("Đang ở"));
            } else {
                btnEditBooking.setDisable(true);
                btnQuickCheckIn.setDisable(true);
                btnQuickCheckOut.setDisable(true);
                btnCancelBooking.setDisable(true);
            }
        });
    }

    @FXML
    private void loadData() {
        masterData.clear();
        List<Map<String, Object>> data = DatPhongBLL.getAllBookingsWithDetails();

        for (Map<String, Object> row : data) {
            masterData.add(new BookingRow(
                    (Integer) row.get("MaDatPhong"),
                    (String) row.get("HoTen"),
                    (String) row.get("SoDienThoai"),
                    (String) row.get("DanhSachPhong"),
                    (LocalDateTime) row.get("NgayIn"),
                    (LocalDateTime) row.get("NgayOut"),
                    (Double) row.get("TienCoc"),
                    (String) row.get("TrangThai")
            ));
        }

        filteredData = new FilteredList<>(masterData, p -> true);
        SortedList<BookingRow> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tvDatPhong.comparatorProperty());
        tvDatPhong.setItems(sortedData);

        tvDatPhong.getSelectionModel().clearSelection();
        applyFilters();
    }

    private void applyFilters() {
        if (filteredData == null) return;

        String keyword = txtSearch.getText().toLowerCase().trim();
        LocalDate fromDate = dpFromDate.getValue();
        LocalDate toDate = dpToDate.getValue();

        ToggleButton selectedTab = (ToggleButton) statusGroup.getSelectedToggle();
        String activeStatus = selectedTab != null ? selectedTab.getText() : "Tất cả";

        filteredData.setPredicate(row -> {
            // 1. Lọc theo trạng thái
            if (!activeStatus.equals("Tất cả") && !row.trangThai.equals(activeStatus)) return false;

            // 2. Lọc theo ngày Check-in
            if (fromDate != null && row.ngayIn.toLocalDate().isBefore(fromDate)) return false;
            if (toDate != null && row.ngayIn.toLocalDate().isAfter(toDate)) return false;

            // 3. Lọc theo từ khóa (Tìm theo Tên hoặc Số điện thoại)
            if (keyword.isEmpty()) return true;
            return row.hoTen.toLowerCase().contains(keyword) ||
                    (row.sdt != null && row.sdt.contains(keyword));
        });
    }

    @FXML void handleNewBooking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CheckInView.fxml"));
            Parent root = loader.load();

            CheckInController controller = loader.getController();
            controller.setPhongData(null);

            Stage stage = new Stage();
            stage.setTitle("Lập Đơn Đặt Phòng Mới");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void handleEditBooking() {
        BookingRow selected = tvDatPhong.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CheckInView.fxml"));
            Parent root = loader.load();

            CheckInController controller = loader.getController();
            controller.setPhongData(null);
            controller.loadExistBooking(selected.getMaDat());

            Stage stage = new Stage();
            stage.setTitle("Chỉnh Sửa Đơn Đặt Trước");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void handleQuickCheckIn() {
        BookingRow selected = tvDatPhong.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        boolean confirm = Others.showCustomConfirm("Xác nhận", "Khách hàng " + selected.hoTen + " đã đến nhận phòng?", "Xác nhận", "Hủy");
        if (confirm) {
            DatPhongBLL.quickCheckIn(selected.maDat);
            Others.showAlert(mainPane, "Đã check-in thành công!", false);
            loadData();
        }
    }

    @FXML void handleQuickCheckOut() {
        BookingRow selected = tvDatPhong.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChiTietPhongView.fxml"));
            Parent root = loader.load();

            ChiTietPhongController controller = loader.getController();
            controller.loadBookingData(selected.maDat);

            Stage stage = new Stage();
            stage.setTitle("Quản lý Thanh Toán");
            stage.setScene(new Scene(root, 1200, 800));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void handleCancelBooking() {
        BookingRow selected = tvDatPhong.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        boolean confirm = Others.showCustomConfirm("Cảnh báo", "Bạn có chắc chắn muốn HỦY đơn đặt phòng của " + selected.hoTen + " không?", "Đồng ý", "Đóng");
        if (confirm) {
            DatPhongBLL.changeStatus(selected.maDat, "Đã hủy");
            Others.showAlert(mainPane, "Đã hủy đơn đặt phòng!", false);
            loadData();
        }
    }

    public static class BookingRow {
        int maDat;
        String hoTen, sdt, dsPhong, trangThai;
        LocalDateTime ngayIn, ngayOut;
        double tienCoc;

        public BookingRow(int maDat, String hoTen, String sdt, String dsPhong, LocalDateTime ngayIn, LocalDateTime ngayOut, double tienCoc, String trangThai) {
            this.maDat = maDat; this.hoTen = hoTen; this.sdt = sdt; this.dsPhong = dsPhong;
            this.ngayIn = ngayIn; this.ngayOut = ngayOut; this.tienCoc = tienCoc; this.trangThai = trangThai;
        }

        public int getMaDat() {
            return maDat;
        }

        public void setMaDat(int maDat) {
            this.maDat = maDat;
        }

        public String getHoTen() {
            return hoTen;
        }

        public void setHoTen(String hoTen) {
            this.hoTen = hoTen;
        }

        public String getSdt() {
            return sdt;
        }

        public void setSdt(String sdt) {
            this.sdt = sdt;
        }

        public String getDsPhong() {
            return dsPhong;
        }

        public void setDsPhong(String dsPhong) {
            this.dsPhong = dsPhong;
        }

        public String getTrangThai() {
            return trangThai;
        }

        public void setTrangThai(String trangThai) {
            this.trangThai = trangThai;
        }

        public LocalDateTime getNgayIn() {
            return ngayIn;
        }

        public void setNgayIn(LocalDateTime ngayIn) {
            this.ngayIn = ngayIn;
        }

        public LocalDateTime getNgayOut() {
            return ngayOut;
        }

        public void setNgayOut(LocalDateTime ngayOut) {
            this.ngayOut = ngayOut;
        }

        public double getTienCoc() {
            return tienCoc;
        }

        public void setTienCoc(double tienCoc) {
            this.tienCoc = tienCoc;
        }
    }
}