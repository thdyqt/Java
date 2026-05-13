package View;

import BusinessBLL.KhachHangBLL;
import EntitiesDTO.KhachHang;
import Utilities.Others;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class KhachHangFormController {
    @FXML private VBox mainPane;
    @FXML private Label lblTitle;
    @FXML private TextField txtHoTen, txtCCCD, txtSDT, txtEmail;
    @FXML private TextArea txtDiaChi;
    @FXML private Button btnSave, btnCancel;

    private int editingMaKH = -1;

    @FXML
    public void initialize() {
        setupConstraints();
        Others.playButtonAnimation(btnSave);
        Others.playButtonAnimation(btnCancel);
    }

    private void setupConstraints() {
        Others.setMaxLength(txtHoTen, 100);
        Others.setMaxLength(txtCCCD, 20);
        Others.setMaxLength(txtSDT, 15);
        Others.setNumericOnly(txtSDT);
        Others.setMaxLength(txtEmail, 100);
    }

    public void setCustomerData(KhachHang kh) {
        if (kh == null) return;

        this.editingMaKH = kh.getMaKhachHang();
        lblTitle.setText("CẬP NHẬT KHÁCH HÀNG");

        txtHoTen.setText(kh.getHoTen());
        txtCCCD.setText(kh.getCccdPassport());
        txtSDT.setText(kh.getSoDienThoai());
        txtEmail.setText(kh.getEmail());
        txtDiaChi.setText(kh.getDiaChi());

        txtCCCD.setDisable(true);
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (txtHoTen.getText().trim().isEmpty() || txtCCCD.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty()) {
            Others.showAlert(mainPane, "Vui lòng điền đầy đủ các thông tin bắt buộc (*)", true);
            return;
        }

        KhachHang kh = new KhachHang();
        kh.setHoTen(Others.standardizeName(txtHoTen.getText().trim()));
        kh.setCccdPassport(txtCCCD.getText().trim());
        kh.setSoDienThoai(txtSDT.getText().trim());
        kh.setEmail(txtEmail.getText().trim());
        kh.setDiaChi(txtDiaChi.getText().trim());

        boolean isSuccess;
        if (editingMaKH == -1) {
            if (KhachHangBLL.getCustomerByCCCD(kh.getCccdPassport()) != null) {
                Others.showAlert(mainPane, "Số CCCD/Passport này đã tồn tại trong hệ thống!", true);
                return;
            }
            int newId = KhachHangBLL.insertCustomer(kh);
            isSuccess = (newId > 0);
        } else {
            kh.setMaKhachHang(editingMaKH);
            isSuccess = KhachHangBLL.updateCustomer(kh);
        }

        if (isSuccess) {
            Others.showAlert(mainPane, "Lưu thông tin khách hàng thành công!", false);
            ((Stage) mainPane.getScene().getWindow()).close();
        } else {
            Others.showAlert(mainPane, "Đã có lỗi xảy ra khi lưu vào Database!", true);
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        ((Stage) mainPane.getScene().getWindow()).close();
    }
}