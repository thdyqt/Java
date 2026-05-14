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

    // Khai báo thêm 2 element mới từ file FXML
    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private Button btnTogglePassword;

    @FXML
    private Button btnLogin;

    // Biến lưu trạng thái hiển thị mật khẩu
    private boolean isPasswordVisible = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Others.playButtonAnimation(btnLogin);
        Others.playFormAnimation(loginBox);

        Others.setMaxLength(txtUsername, 20);
        Others.setMaxLength(txtPassword, 20);

        // Ràng buộc (bind) dữ liệu giữa 2 ô ẩn và hiện để chúng luôn đồng bộ nội dung với nhau
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

    // Hàm xử lý khi click vào icon con mắt
    @FXML
    void handleTogglePassword(ActionEvent event) {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            // Hiện mật khẩu
            txtPasswordVisible.setVisible(true);
            txtPassword.setVisible(false);
            btnTogglePassword.setText("🙈"); // Đổi icon thành mắt nhắm
        } else {
            // Ẩn mật khẩu
            txtPasswordVisible.setVisible(false);
            txtPassword.setVisible(true);
            btnTogglePassword.setText("👁"); // Đổi icon về mắt mở
        }
    }

    @FXML
    void handleLogin(ActionEvent event) throws IOException {
        String username = txtUsername.getText().trim();
        // Do đã bindBidirectional nên txtPassword luôn chứa mật khẩu chính xác (dù đang nhập ở ô hiện hay ô ẩn)
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