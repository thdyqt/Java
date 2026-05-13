package View;

import BusinessBLL.NhanVienBLL;
import EntitiesDTO.NhanVien;
import Utilities.IContentArea;
import Utilities.Others;
import Utilities.UserSession;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Frame implements Initializable {
    @FXML
    private BorderPane mainPane;

    @FXML
    private StackPane contentArea;

    @FXML
    private AnchorPane sidebar;

    @FXML
    private Label lblClock;

    @FXML
    private MenuButton menuUser;

    @FXML
    private Button btnDatPhong, btnDichVu, btnHoaDon, btnKhachHang, btnNhanVien, btnPhong;

    @FXML
    private Region slideIndicator;

    private NhanVien currentStaff;
    public static Frame instance;
    private Button[] menuButtons;
    private boolean isSidebarVisible = true;
    private Timeline sidebarTimeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadForm();
        Others.startClock(lblClock);

        currentStaff = UserSession.getInstance().getNhanVien();
        menuUser.setText("Xin chào, " + currentStaff.getHoTen() + " (" + currentStaff.getChucVu() + ")");

        if (!"Admin".equals(currentStaff.getChucVu())) {
            btnDichVu.setVisible(false);
            btnDichVu.setManaged(false);

            btnNhanVien.setVisible(false);
            btnNhanVien.setManaged(false);
        }

//        menuInfo.setOnAction(event -> openProfileDialog(true));
//        menuEditAcc.setOnAction(event -> openProfileDialog(false));

        menuButtons = new Button[]{btnPhong, btnDatPhong, btnKhachHang, btnDichVu, btnHoaDon, btnNhanVien};
        Platform.runLater(() -> setActiveMenu(btnPhong));
        showPhongView(null);
    }

    public void loadForm() {
        instance = this;

        mainPane.setOpacity(0);
        mainPane.setScaleX(0.95);
        mainPane.setScaleY(0.95);

        FadeTransition fade = new FadeTransition(Duration.millis(500), mainPane);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(500), mainPane);
        scale.setToX(1.0);
        scale.setToY(1.0);

        ParallelTransition pt = new ParallelTransition(fade, scale);
        pt.setInterpolator(Interpolator.EASE_OUT);
        pt.play();
    }

    // TOPBAR
    @FXML
    void handleToggleSidebar(ActionEvent event) {
        if (sidebarTimeline != null && sidebarTimeline.getStatus() == javafx.animation.Animation.Status.RUNNING) {
            return;
        }
        sidebarTimeline = new Timeline();

        if (isSidebarVisible) {
            KeyValue kvWidth = new KeyValue(sidebar.prefWidthProperty(), 0, Interpolator.EASE_BOTH);
            KeyValue kvMinWidth = new KeyValue(sidebar.minWidthProperty(), 0, Interpolator.EASE_BOTH);
            KeyValue kvOpacity = new KeyValue(sidebar.opacityProperty(), 0, Interpolator.EASE_BOTH);

            KeyFrame kf = new KeyFrame(Duration.millis(350), kvWidth, kvMinWidth, kvOpacity);
            sidebarTimeline.getKeyFrames().add(kf);

            sidebarTimeline.setOnFinished(e -> {
                sidebar.setVisible(false);
                sidebar.setManaged(false);
            });

            isSidebarVisible = false;
        } else {
            sidebar.setVisible(true);
            sidebar.setManaged(true);

            KeyValue kvWidth = new KeyValue(sidebar.prefWidthProperty(), 270, Interpolator.EASE_BOTH);
            KeyValue kvMinWidth = new KeyValue(sidebar.minWidthProperty(), 270, Interpolator.EASE_BOTH);
            KeyValue kvOpacity = new KeyValue(sidebar.opacityProperty(), 1, Interpolator.EASE_BOTH);

            KeyFrame kf = new KeyFrame(Duration.millis(350), kvWidth, kvMinWidth, kvOpacity);
            sidebarTimeline.getKeyFrames().add(kf);

            isSidebarVisible = true;
        }

        sidebarTimeline.play();
    }

    @FXML
    void handleViewProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NhanVienForm.fxml"));
            Parent root = loader.load();

            NhanVienFormController controller = loader.getController();
            controller.setNhanVien(UserSession.getInstance().getNhanVien());

            controller.setViewOnlyMode();
            Stage stage = new Stage();
            stage.setTitle("Thông tin cá nhân");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Không thể hiển thị hồ sơ cá nhân!", true);
        }
    }

    @FXML
    void handleChangePassword(ActionEvent event) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Bảo mật tài khoản");
        stage.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Label lblTitle = new Label("ĐỔI MẬT KHẨU");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        String inputStyle = "-fx-pref-height: 40; -fx-background-radius: 8; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-color: #f8fafc; -fx-font-size: 14px;";

        PasswordField txtOld = new PasswordField();
        txtOld.setPromptText("Mật khẩu hiện tại");
        txtOld.setStyle(inputStyle);

        PasswordField txtNew = new PasswordField();
        txtNew.setPromptText("Mật khẩu mới (ít nhất 6 ký tự)");
        txtNew.setStyle(inputStyle);

        PasswordField txtConfirm = new PasswordField();
        txtConfirm.setPromptText("Xác nhận mật khẩu mới");
        txtConfirm.setStyle(inputStyle);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 12px;");
        lblError.setWrapText(true);

        Button btnSave = new Button("✔ CẬP NHẬT");
        Others.playButtonAnimation(btnSave);
        btnSave.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-pref-height: 40; -fx-pref-width: 130;");

        Button btnCancel = new Button("HỦY BỎ");
        Others.playButtonAnimation(btnCancel);
        btnCancel.setStyle("-fx-background-color: white; -fx-text-fill: #475569; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand; -fx-pref-height: 40; -fx-pref-width: 100;");

        btnCancel.setOnAction(e -> stage.close());

        btnSave.setOnAction(e -> {
            String oldPass = txtOld.getText();
            String newPass = txtNew.getText();
            String confirmPass = txtConfirm.getText();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                lblError.setText("Vui lòng điền đầy đủ các trường!");
                return;
            }

            if (!oldPass.equals(UserSession.getInstance().getMatKhau())) {
                lblError.setText("Mật khẩu hiện tại không chính xác!");
                txtOld.requestFocus();
                return;
            }

            if (newPass.length() < 6) {
                lblError.setText("Mật khẩu mới phải có ít nhất 6 ký tự!");
                txtNew.requestFocus();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                lblError.setText("Mật khẩu xác nhận không khớp!");
                txtConfirm.clear();
                txtConfirm.requestFocus();
                return;
            }

            if (NhanVienBLL.changePassword(UserSession.getInstance().getMaNhanVien(), newPass)) {
                UserSession.getInstance().setMatKhau(newPass);
                stage.close();
                Others.showAlert(mainPane, "Đổi mật khẩu thành công!", false);
            } else {
                lblError.setText("Có lỗi xảy ra, không thể cập nhật!");
            }
        });

        HBox btnBox = new HBox(15, btnCancel, btnSave);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        root.getChildren().addAll(lblTitle, txtOld, txtNew, txtConfirm, lblError, btnBox);

        Scene scene = new Scene(root, 400, 340);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    void handleLogout(ActionEvent event) {
        boolean isConfirm = Others.showCustomConfirm(
                "Kết thúc ca trực",
                "Bạn đang yêu cầu đăng xuất khỏi hệ thống POS.\nBạn có chắc chắn muốn thoát không?",
                "Đăng xuất", "Hủy bỏ"
        );

        if (isConfirm) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                mainPane.getScene().setRoot(loader.load());

                NhanVienBLL.logout();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // SIDEBAR
    public void setActiveMenu(Button activeButton) {
        if (menuButtons == null) return;

        for (Button btn : menuButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("active-menu");
            }
        }

        if (activeButton != null) {
            activeButton.getStyleClass().add("active-menu");
            TranslateTransition transition = new TranslateTransition(Duration.millis(250), slideIndicator);
            double targetY = activeButton.getBoundsInParent().getMinY();
            slideIndicator.setPrefHeight(activeButton.getHeight());

            transition.setToY(targetY);
            transition.setInterpolator(Interpolator.EASE_BOTH);
            transition.play();
        }
    }

    public void switchForm(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Node node = loader.load();
            Object controller = loader.getController();
            if (controller instanceof IContentArea ctrl) {
                ctrl.setContentArea(this.contentArea);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);
        } catch (Exception e) {
            System.out.println("Lỗi khi tải trang: " + fxmlFileName);
            e.printStackTrace();
        }
    }

    @FXML
    void showPhongView(ActionEvent event) {
        setActiveMenu(btnPhong);
        switchForm("/SoDoPhongView.fxml");
    }

    @FXML
    void showDatPhongView(ActionEvent event) {
        setActiveMenu(btnDatPhong);
        switchForm("/QuanLyDatPhongView.fxml");
    }

    @FXML
    void showKhachHangView(ActionEvent event) {
        setActiveMenu(btnKhachHang);
        switchForm("/KhachHangView.fxml");
    }

    @FXML
    void showDichVuView(ActionEvent event) {
        if ("Admin".equals(UserSession.getInstance().getChucVu())) {
            setActiveMenu(btnDichVu);
            switchForm("/DichVuView.fxml");
        }
    }

    @FXML
    void showHoaDonView(ActionEvent event) {
        setActiveMenu(btnHoaDon);
        switchForm("/QuanLyHoaDonView.fxml");
    }

    @FXML
    void showNhanVienView(ActionEvent event) {
        if ("Admin".equals(UserSession.getInstance().getChucVu())) {
            setActiveMenu(btnNhanVien);
            switchForm("/NhanVienView.fxml");
        }
    }
}