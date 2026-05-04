package View;

import BusinessBLL.NhanVienBLL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Login {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    private Button btnLogin;

    @FXML
    void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        lblError.setText("");

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        if (NhanVienBLL.checkLogin(username, password) != null) {
            lblError.setStyle("-fx-text-fill: #4CAF50;");
            lblError.setText("Đăng nhập thành công! Đang chuyển trang...");

            // Viết code mở form Quản Lý (Main Form) ở đây sau
        } else {
            lblError.setStyle("-fx-text-fill: #d32f2f;");
            lblError.setText("Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
    }
}