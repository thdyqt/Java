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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.*;
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
        System.out.println("Mở popup xem thông tin nhân viên");
    }

    @FXML
    void handleChangePassword(ActionEvent event) {
        System.out.println("Mở popup đổi mật khẩu");
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
    }

    @FXML
    void showDichVuView(ActionEvent event) {
        setActiveMenu(btnDichVu);
    }

    @FXML
    void showHoaDonView(ActionEvent event) {
        setActiveMenu(btnHoaDon);
    }

    @FXML
    void showNhanVienView(ActionEvent event) {
        setActiveMenu(btnNhanVien);
    }
}