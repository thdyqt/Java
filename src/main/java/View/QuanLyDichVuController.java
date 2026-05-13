package View;

import BusinessBLL.DichVuBLL;
import EntitiesDTO.DichVu;
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

public class QuanLyDichVuController {
    @FXML private VBox mainPane;
    @FXML private TextField txtSearch;
    @FXML private CheckBox chkShowHidden;

    @FXML private TableView<DichVuRow> tvDichVu;
    @FXML private TableColumn<DichVuRow, String> colMaDV, colTenDV, colDonGia, colTrangThai;

    @FXML private Button btnAdd, btnEdit, btnStop, btnRestore;

    private ObservableList<DichVuRow> masterData = FXCollections.observableArrayList();
    private FilteredList<DichVuRow> filteredData;

    @FXML
    public void initialize() {
        Others.animateTableRows(tvDichVu);
        setupColumns();
        setupButtons();
        setupListeners();
        loadData();
    }

    private void setupColumns() {
        tvDichVu.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colMaDV.setCellValueFactory(d -> new SimpleStringProperty("DV-" + d.getValue().getMaDV()));
        colTenDV.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTenDV()));
        colDonGia.setCellValueFactory(d -> new SimpleStringProperty(Others.formatPrice(d.getValue().getDonGia())));
        colTrangThai.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTrangThai()));
    }

    private void setupButtons() {
        Others.playButtonAnimation(btnAdd);
        Others.playButtonAnimation(btnEdit);
        Others.playButtonAnimation(btnRestore);
        Others.playButtonAnimation(btnStop);
    }

    private void setupListeners() {
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        chkShowHidden.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        btnEdit.setDisable(true);
        btnStop.setDisable(true);
        btnRestore.setDisable(true);

        tvDichVu.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal == null) {
                btnEdit.setDisable(true);
                btnStop.setDisable(true);
                btnRestore.setDisable(true);
            } else {
                btnEdit.setDisable(false);
                boolean isNgungBan = "Ngừng kinh doanh".equals(newVal.getTrangThai());

                btnStop.setDisable(isNgungBan);
                btnRestore.setDisable(!isNgungBan);
            }
        });
    }

    private void loadData() {
        masterData.clear();
        List<DichVu> list = DichVuBLL.getAllDichVu();
        for (DichVu dv : list) {
            masterData.add(new DichVuRow(
                    dv.getMaDichVu(),
                    dv.getTenDichVu(),
                    dv.getDonGia(),
                    dv.getTrangThai().getText()
            ));
        }

        filteredData = new FilteredList<>(masterData, p -> true);
        SortedList<DichVuRow> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tvDichVu.comparatorProperty());
        tvDichVu.setItems(sortedData);
        tvDichVu.getSelectionModel().clearSelection();

        applyFilters();
    }

    private void applyFilters() {
        if (filteredData == null) return;

        String keyword = txtSearch.getText().toLowerCase().trim();
        boolean isShowOnlyHidden = chkShowHidden.isSelected();

        filteredData.setPredicate(row -> {
            boolean isMatchStatus;
            if (isShowOnlyHidden) {
                isMatchStatus = "Ngừng kinh doanh".equals(row.getTrangThai());
            } else {
                isMatchStatus = "Đang bán".equals(row.getTrangThai());
            }

            if (!isMatchStatus) return false;

            if (keyword.isEmpty()) return true;
            return row.getTenDV().toLowerCase().contains(keyword);
        });
    }

    @FXML void handleAddNew() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DichVuForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm Dịch Vụ Mới");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void handleEdit() {
        DichVuRow selected = tvDichVu.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DichVuForm.fxml"));
            Parent root = loader.load();

            DichVuFormController controller = loader.getController();
            DichVu dv = new DichVu(selected.getMaDV(), selected.getTenDV(), selected.getDonGia(), DichVu.TrangThaiDichVu.fromString(selected.getTrangThai()));
            controller.setDichVuData(dv);

            Stage stage = new Stage();
            stage.setTitle("Cập Nhật Dịch Vụ");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void handleStop() {
        DichVuRow selected = tvDichVu.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        boolean confirm = Others.showCustomConfirm("Xác nhận ngừng",
                "Bạn có chắc muốn ngừng kinh doanh: " + selected.getTenDV() + "?\n(Lễ tân sẽ không thể thêm món này vào hóa đơn mới nữa)", "Đồng ý", "Hủy");

        if (confirm) {
            if (DichVuBLL.stopDichVu(selected.getMaDV())) {
                Others.showAlert(mainPane, "Đã chuyển sang trạng thái Ngừng kinh doanh.", false);
                loadData();
            } else {
                Others.showAlert(mainPane, "Thao tác thất bại!", true);
            }
        }
    }

    @FXML void handleRestore() {
        DichVuRow selected = tvDichVu.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        boolean confirm = Others.showCustomConfirm("Xác nhận mở lại",
                "Mở bán trở lại dịch vụ: " + selected.getTenDV() + "?", "Đồng ý", "Hủy");

        if (confirm) {
            if (DichVuBLL.restoreDichVu(selected.getMaDV())) {
                Others.showAlert(mainPane, "Khôi phục dịch vụ thành công!", false);
                loadData();
            } else {
                Others.showAlert(mainPane, "Thao tác thất bại!", true);
            }
        }
    }

    public static class DichVuRow {
        private int maDV;
        private String tenDV;
        private double donGia;
        private String trangThai;

        public DichVuRow(int maDV, String tenDV, double donGia, String trangThai) {
            this.maDV = maDV; this.tenDV = tenDV; this.donGia = donGia; this.trangThai = trangThai;
        }
        public int getMaDV() { return maDV; }
        public String getTenDV() { return tenDV; }
        public double getDonGia() { return donGia; }
        public String getTrangThai() { return trangThai; }
    }
}