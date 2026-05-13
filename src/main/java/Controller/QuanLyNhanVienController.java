package Controller;

import BusinessBLL.NhanVienBLL;
import EntitiesDTO.NhanVien;
import Utilities.Others;
import Utilities.UserSession;
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

public class QuanLyNhanVienController {
    @FXML private VBox mainPane;
    @FXML private TextField txtSearch;
    @FXML private CheckBox chkShowRetired;
    @FXML private TableView<StaffRow> tvNhanVien;
    @FXML private TableColumn<StaffRow, String> colMaNV, colHoTen, colChucVu, colUsername, colSDT, colTrangThai;
    @FXML private Button btnAdd, btnEdit, btnResetPass, btnStop, btnRestore;
    @FXML private ComboBox<String> cbSort;

    private SortedList<StaffRow> sortedData;
    private ObservableList<StaffRow> masterData = FXCollections.observableArrayList();
    private FilteredList<StaffRow> filteredData;

    @FXML
    public void initialize() {
        Others.animateTableRows(tvNhanVien);
        setupColumns();
        setupButtons();
        setupComboBox();
        setupListeners();
        loadData();
    }

    private void setupColumns() {
        tvNhanVien.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colMaNV.setCellValueFactory(d -> new SimpleStringProperty("NV-" + d.getValue().getMaNV()));
        colHoTen.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getHoTen()));
        colChucVu.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getChucVu()));
        colUsername.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        colSDT.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSdt()));
        colTrangThai.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTrangThai()));
    }

    private void setupButtons() {
        Others.playButtonAnimation(btnAdd);
        Others.playButtonAnimation(btnEdit);
        Others.playButtonAnimation(btnStop);
        Others.playButtonAnimation(btnRestore);
        Others.playButtonAnimation(btnResetPass);
    }

    private void setupComboBox() {
        cbSort.setItems(FXCollections.observableArrayList(
                "Mã nhân viên tăng dần",
                "Mã nhân viên giảm dần",
                "Tên A -> Z",
                "Tên Z -> A",
                "Chức vụ"
        ));
        cbSort.getSelectionModel().select(0);
        cbSort.valueProperty().addListener((obs, oldVal, newVal) -> applySorting());
    }

    private void setupListeners() {
        txtSearch.textProperty().addListener((o, old, newVal) -> applyFilters());
        chkShowRetired.selectedProperty().addListener((o, old, newVal) -> applyFilters());

        tvNhanVien.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            boolean isSelected = (newVal != null);

            btnEdit.setDisable(!isSelected);
            btnResetPass.setDisable(!isSelected);

            if (isSelected) {
                boolean isMe = newVal.getMaNV() == UserSession.getInstance().getMaNhanVien();
                boolean isAlreadyRetired = "Đã nghỉ việc".equals(newVal.getTrangThai());
                btnStop.setDisable(isMe || isAlreadyRetired);
                btnRestore.setDisable(!isAlreadyRetired);
            } else {
                btnStop.setDisable(true);
                btnRestore.setDisable(true);
            }
        });
    }

    private void loadData() {
        masterData.clear();
        List<NhanVien> list = NhanVienBLL.getAllNhanVien();
        for (NhanVien nv : list) {
            masterData.add(new StaffRow(nv.getMaNhanVien(), nv.getHoTen(), nv.getChucVu(), nv.getTenDangNhap(), nv.getSoDienThoai(), nv.getTrangThai().getText()));
        }
        filteredData = new FilteredList<>(masterData, p -> true);
        sortedData = new SortedList<>(filteredData);
        tvNhanVien.setItems(sortedData);
        applyFilters();
        applySorting();
    }

    private void applyFilters() {
        if (filteredData == null) return;

        String keyword = txtSearch.getText().toLowerCase().trim();
        boolean isShowOnlyRetired = chkShowRetired.isSelected();

        filteredData.setPredicate(row -> {
            boolean matchStatus;
            if (isShowOnlyRetired) {
                matchStatus = "Đã nghỉ việc".equals(row.getTrangThai());
            } else {
                matchStatus = "Đang làm việc".equals(row.getTrangThai());
            }

            if (!matchStatus) return false;

            if (keyword.isEmpty()) return true;

            return row.getHoTen().toLowerCase().contains(keyword) ||
                    row.getChucVu().toLowerCase().contains(keyword) ||
                    row.getUsername().toLowerCase().contains(keyword) ||
                    row.getSdt().contains(keyword);
        });
    }

    private void applySorting() {
        if (sortedData == null || cbSort.getValue() == null) return;

        String sortMode = cbSort.getValue();
        sortedData.setComparator((nv1, nv2) -> {
            switch (sortMode) {
                case "Mã nhân viên tăng dần":
                    return Integer.compare(nv1.getMaNV(), nv2.getMaNV());
                case "Mã nhân viên giảm dần":
                    return Integer.compare(nv2.getMaNV(), nv1.getMaNV());
                case "Tên A -> Z":
                    return Others.getFirstName(nv1.getHoTen()).compareToIgnoreCase(Others.getFirstName(nv2.getHoTen()));
                case "Tên Z -> A":
                    return Others.getFirstName(nv2.getHoTen()).compareToIgnoreCase(Others.getFirstName(nv1.getHoTen()));
                case "Chức vụ":
                    return nv1.getChucVu().compareToIgnoreCase(nv2.getChucVu());
                default:
                    return 0;
            }
        });
    }

    @FXML
    void handleAddNew() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NhanVienForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm nhân viên mới");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
            loadData();

        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Không thể mở form thêm nhân viên!", true);
        }
    }

    @FXML
    void handleEdit() {
        StaffRow selectedRow = tvNhanVien.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            Others.showAlert(mainPane, "Vui lòng chọn nhân viên cần sửa!", true);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NhanVienForm.fxml"));
            Parent root = loader.load();

            NhanVienFormController controller = loader.getController();
            NhanVien selectedNV = NhanVienBLL.getAllNhanVien().stream()
                    .filter(nv -> nv.getMaNhanVien() == selectedRow.getMaNV())
                    .findFirst().orElse(null);

            if (selectedNV != null) {
                controller.setNhanVien(selectedNV);

                Stage stage = new Stage();
                stage.setTitle("Chỉnh sửa thông tin nhân viên");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));

                stage.showAndWait();
                loadData();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Lỗi khi tải thông tin nhân viên!", true);
        }
    }

    @FXML void handleResetPassword() {
        StaffRow selected = tvNhanVien.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        boolean confirm = Others.showCustomConfirm("Xác nhận", "Đặt lại mật khẩu mặc định (123456) cho " + selected.getHoTen() + "?", "Đặt lại", "Hủy");
        if (confirm) {
            if (NhanVienBLL.changePassword(selected.getMaNV(), "123456")) {
                Others.showAlert(mainPane, "Đã đặt lại mật khẩu thành công!", false);
            }
        }
    }

    @FXML void handleStop() {
        StaffRow selected = tvNhanVien.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        if (Others.showCustomConfirm("Xác nhận", "Cho nhân viên " + selected.getHoTen() + " nghỉ việc?\nTài khoản này sẽ không thể đăng nhập.", "Xác nhận", "Hủy")) {
            if (NhanVienBLL.stopNhanVien(selected.getMaNV())) {
                loadData();
                Others.showAlert(mainPane, "Đã cập nhật trạng thái nghỉ việc.", false);
            }
        }
    }

    @FXML void handleRestore() {
        StaffRow selected = tvNhanVien.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        if (Others.showCustomConfirm("Xác nhận", "Cho nhân viên " + selected.getHoTen() + " quay trở lại việc?", "Xác nhận", "Hủy")) {
            if (NhanVienBLL.restoreNhanVien(selected.getMaNV())) {
                loadData();
                Others.showAlert(mainPane, "Đã cập nhật trạng thái đang làm việc.", false);
            }
        }
    }

    public static class StaffRow {
        private int maNV;
        private String hoTen, chucVu, username, sdt, trangThai;
        public StaffRow(int maNV, String hoTen, String chucVu, String username, String sdt, String trangThai) {
            this.maNV = maNV; this.hoTen = hoTen; this.chucVu = chucVu; this.username = username; this.sdt = sdt; this.trangThai = trangThai;
        }
        public int getMaNV() { return maNV; }
        public String getHoTen() { return hoTen; }
        public String getChucVu() { return chucVu; }
        public String getUsername() { return username; }
        public String getSdt() { return sdt; }
        public String getTrangThai() { return trangThai; }
    }
}