package Controller;

import BusinessBLL.NhanVienBLL;
import Utilities.Others;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private HBox loginBox;

    @FXML
    private StackPane mainPane;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private Button btnTogglePassword;

    @FXML
    private Button btnLogin;

    private boolean isPasswordVisible = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Others.playButtonAnimation(btnLogin);
        Others.playFormAnimation(loginBox);

        Others.setMaxLength(txtUsername, 20);
        Others.setMaxLength(txtPassword, 20);

        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());

        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(" ")) {
                txtUsername.setText(newValue.replaceAll(" ", ""));
            }
        });

        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(" ")) {
                txtPassword.setText(newValue.replaceAll(" ", ""));
            }
        });
    }

    @FXML
    void handleTogglePassword(ActionEvent event) {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            txtPasswordVisible.setVisible(true);
            txtPassword.setVisible(false);
            btnTogglePassword.setText("🙈");
        } else {
            txtPasswordVisible.setVisible(false);
            txtPassword.setVisible(true);
            btnTogglePassword.setText("👁");
        }
    }

    @FXML
    void handleLogin(ActionEvent event) throws IOException {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Others.showAlert(mainPane, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", true);
            return;
        }

        if (NhanVienBLL.checkLogin(username, password) != null) {
            Others.showAlert(mainPane, "Đăng nhập thành công! Đang chuyển trang...", false);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Frame.fxml"));
                Parent root = loader.load();

                btnLogin.getScene().setRoot(root);

            } catch (Exception e) {
                e.printStackTrace();
                Others.showAlert(mainPane, "Không thể chuyển sang giao diện chính!", true);
            }
        } else {
            Others.showAlert(mainPane, "Tên đăng nhập hoặc mật khẩu không chính xác!", true);
        }
    }
}