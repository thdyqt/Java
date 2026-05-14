package Controller;

import BusinessBLL.NhanVienBLL;
import EntitiesDTO.NhanVien;
import Utilities.Others;
import Utilities.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NhanVienFormController {
    @FXML private VBox mainPane;
    @FXML private Label lblTitle, lblDesc, lblMatKhau, lblTrangThai;
    @FXML private Button btnSave, btnCancel;
    @FXML private TextField txtHoTen, txtSDT, txtUsername;
    @FXML private PasswordField txtMatKhau;
    @FXML private ComboBox<String> cbChucVu, cbTrangThai;

    @FXML private RowConstraints rowMatKhau, rowTrangThai;

    private NhanVien currentNV;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupConstraints();

        Others.playButtonAnimation(btnSave);
        Others.playButtonAnimation(btnCancel);
        rowTrangThai.setPrefHeight(0);
        rowTrangThai.setMaxHeight(0);
        lblTrangThai.setVisible(false);
        cbTrangThai.setVisible(false);
    }

    private void setupComboBoxes() {
        cbChucVu.setItems(FXCollections.observableArrayList("Lễ tân", "Admin"));
        cbTrangThai.setItems(FXCollections.observableArrayList("Đang làm việc", "Đã nghỉ việc"));
        cbTrangThai.getSelectionModel().select(0);
    }

    private void setupConstraints() {
        Others.setMaxLength(txtHoTen, 100);

        Others.setMaxLength(txtSDT, 10);
        Others.setNumericOnly(txtSDT);

        Others.setMaxLength(txtUsername, 50);
    }

    public void setNhanVien(NhanVien nv) {
        if (nv == null) return;
        this.currentNV = nv;
        this.isEditMode = true;

        lblTitle.setText("CẬP NHẬT NHÂN VIÊN");
        txtHoTen.setText(nv.getHoTen());
        txtSDT.setText(nv.getSoDienThoai());
        txtUsername.setText(nv.getTenDangNhap());
        cbChucVu.getSelectionModel().select(nv.getChucVu());
        cbTrangThai.getSelectionModel().select(nv.getTrangThai().getText());

        lblMatKhau.setVisible(false);
        lblMatKhau.setManaged(false);
        txtMatKhau.setVisible(false);
        txtMatKhau.setManaged(false);

        lblTrangThai.setVisible(true);
        lblTrangThai.setManaged(true);
        cbTrangThai.setVisible(true);
        cbTrangThai.setManaged(true);

        setupPermission();
    }

    private void setupPermission() {
        NhanVien sessionUser = UserSession.getInstance().getNhanVien();
        boolean isAdmin = "Admin".equals(sessionUser.getChucVu());

        boolean isSelf = (currentNV != null) && (sessionUser.getMaNhanVien() == currentNV.getMaNhanVien());

        if (!isAdmin) {
            cbChucVu.setDisable(true);
            cbTrangThai.setDisable(true);
            txtUsername.setDisable(true);
            lblTitle.setText("HỒ SƠ CÁ NHÂN");
        } else if (isSelf) {
            cbChucVu.setDisable(true);
            cbTrangThai.setDisable(true);
        } else {
            cbChucVu.setDisable(false);
            cbTrangThai.setDisable(false);
            txtUsername.setDisable(false);
        }
    }

    @FXML
    void handleSave() {
        if (txtHoTen.getText().trim().isEmpty() || txtUsername.getText().trim().isEmpty()) {
            Others.showAlert(mainPane, "Họ tên và Tên đăng nhập là bắt buộc!", true);
            return;
        }

        String result;
        if (isEditMode) {
            currentNV.setHoTen(Others.standardizeName(txtHoTen.getText().trim()));
            currentNV.setSoDienThoai(txtSDT.getText().trim());
            currentNV.setTenDangNhap(txtUsername.getText().trim());
            currentNV.setChucVu(cbChucVu.getValue());
            currentNV.setTrangThai(NhanVien.TrangThaiNhanVien.fromString(cbTrangThai.getValue()));

            result = NhanVienBLL.updateNhanVien(currentNV);
        } else {
            String inputPass = txtMatKhau.getText().trim();
            if (inputPass.isEmpty()) {
                inputPass = "123456";
            }

            NhanVien newNV = new NhanVien();
            newNV.setHoTen(Others.standardizeName(txtHoTen.getText().trim()));
            newNV.setSoDienThoai(txtSDT.getText().trim());
            newNV.setChucVu(cbChucVu.getValue());
            newNV.setTenDangNhap(txtUsername.getText().trim());
            newNV.setMatKhau(inputPass);
            newNV.setTrangThai(NhanVien.TrangThaiNhanVien.DANG_LAM_VIEC);

            result = NhanVienBLL.insertNhanVien(newNV);
        }

        if ("SUCCESS".equals(result)) {
            Others.showAlert(mainPane, "Lưu thông tin nhân viên thành công!", false);
            closeStage();
        } else {
            Others.showAlert(mainPane, result, true);
        }
    }

    public void setViewOnlyMode() {
        txtHoTen.setEditable(false);
        txtSDT.setEditable(false);
        txtUsername.setEditable(false);

        cbChucVu.setDisable(true);
        cbTrangThai.setDisable(true);

        btnSave.setVisible(false);
        btnSave.setManaged(false);

        btnCancel.setText("ĐÓNG");

        lblTitle.setText("HỒ SƠ CÁ NHÂN (CHỈ XEM)");
        lblDesc.setText("");
    }

    @FXML
    void handleCancel() {
        closeStage();
    }

    private void closeStage() {
        ((Stage) mainPane.getScene().getWindow()).close();
    }
}