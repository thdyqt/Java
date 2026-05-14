package Utilities;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import javax.swing.text.Utilities;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class Others {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private static final NumberFormat priceFormatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    // CHUẨN HÓA GIÁ TIỀN
    public static String formatPrice(double price) {
        return priceFormatter.format(price) + " đ";
    }

    public static void setCurrencyFormatting(TextField textField) {
        textField.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (isFocused) {
                String raw = textField.getText().replaceAll("[^\\d]", "");
                textField.setText(raw);
            } else {
                String raw = textField.getText().replaceAll("[^\\d]", "");
                if (!raw.isEmpty()) {
                    try {
                        long val = Long.parseLong(raw);
                        DecimalFormat formatter = new DecimalFormat("#,###");
                        textField.setText(formatter.format(val).replace(",", ".") + "đ");
                    } catch (NumberFormatException e) {
                        textField.setText("0đ");
                    }
                } else {
                    textField.setText("0đ");
                }
            }
        });
    }

    // ĐỊNH DẠNG THỜI GIAN
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "---";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    // SET ĐỘ DÀI TỐI ĐA CHO TEXTFIELD
    public static void setMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(oldValue);
            }
        });
    }


    // RÀNG BUỘC NHẬP SỐ CHO TEXTFIELD
    public static void setNumericOnly(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d.]", ""));
            }
        });
    }

    // CHUẨN HÓA HỌ VÀ TÊN (KHI THÊM NHÂN VIÊN)
    public static String standardizeName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }

        String cleanedString = fullName.trim().replaceAll("\\s+", " ");
        String[] words = cleanedString.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String firstLetter = word.substring(0, 1).toUpperCase();
            String remainingLetters = word.substring(1).toLowerCase();
            result.append(firstLetter).append(remainingLetters).append(" ");
        }

        return result.toString().trim();
    }

    // LẤY TÊN THAY VÌ HỌ
    public static String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts[parts.length - 1];
    }

    // CHAY THOI GIAN
    public static void startClock(Label label) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a   |   dd/MM/yyyy");

        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    label.setText(LocalDateTime.now().format(formatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public static void showVietQR(int amount, String orderInfo, String confirmBtnText, Region mainPane, Runnable onSuccess) {
        try {
            String bankID = "MB";
            String accountNo = "000002907";
            String accountName = URLEncoder.encode("PHAN THANH DUY", "UTF-8").replace("+", "%20");

            String addInfo = URLEncoder.encode(orderInfo, "UTF-8").replace("+", "%20");

            String qrUrl = String.format("https://img.vietqr.io/image/%s-%s-compact2.png?amount=%d&addInfo=%s&accountName=%s",
                    bankID, accountNo, amount, addInfo, accountName);

            Image qrImage = new Image(qrUrl, false);

            if (qrImage.isError()) {
                throw new Exception("Không thể tải ảnh QR từ máy chủ!");
            }

            ImageView imageView = new ImageView(qrImage);
            imageView.setFitWidth(500);
            imageView.setFitHeight(500);
            imageView.setPreserveRatio(true);

            Stage qrStage = new Stage();
            qrStage.initModality(Modality.APPLICATION_MODAL);
            qrStage.setTitle("Thanh toán Chuyển khoản");
            qrStage.setMaximized(true);

            VBox root = new VBox(30);
            root.setAlignment(Pos.CENTER);
            root.setStyle("-fx-background-color: #F8FAFC;");

            Label lblTitle = new Label("QUÉT MÃ ĐỂ THANH TOÁN");
            lblTitle.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

            Label lblAmount = new Label("Số tiền cần chuyển: " + formatPrice(amount));
            lblAmount.setStyle("-fx-font-size: 28px; -fx-text-fill: #EF4444; -fx-font-weight: bold;");

            HBox btnBox = new HBox(20);
            btnBox.setAlignment(javafx.geometry.Pos.CENTER);

            Button btnCancel = new Button("Đóng / Hủy");
            btnCancel.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #EF4444; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 10; -fx-cursor: hand;");

            Button btnConfirm = new Button(confirmBtnText);
            btnConfirm.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 10; -fx-cursor: hand;");

            btnBox.getChildren().addAll(btnCancel, btnConfirm);
            root.getChildren().addAll(lblTitle, lblAmount, imageView, btnBox);

            Scene scene = new Scene(root);
            qrStage.setScene(scene);

            btnCancel.setOnAction(e -> qrStage.close());

            btnConfirm.setOnAction(e -> {
                qrStage.close();
                Platform.runLater(onSuccess);
            });

            qrStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            if (mainPane != null) {
                showAlert(mainPane, "Lỗi khi tạo mã QR. Vui lòng kiểm tra mạng!", true);
            }
        }
    }

    // ANIMATION KHI ẤN NÚT
    public static void playButtonAnimation(Node node){
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100),node);
        scaleDown.setToX(0.95);
        scaleDown.setToY(0.95);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100),node);
        scaleUp.setToX(1);
        scaleUp.setToY(1);

        node.setOnMouseEntered(mouseEvent -> {
            scaleUp.stop();
            scaleDown.playFromStart();
        });

        node.setOnMouseExited(mouseEvent -> {
            scaleDown.stop();
            scaleUp.playFromStart();
        });

        ScaleTransition scaleDown2 = new ScaleTransition(Duration.millis(50),node);
        scaleDown2.setToX(0.90);
        scaleDown2.setToY(0.90);

        ScaleTransition scaleUp2 = new ScaleTransition(Duration.millis(50),node);
        scaleUp2.setToX(1);
        scaleUp2.setToY(1);

        node.setOnMousePressed(mouseEvent -> {
            scaleUp2.stop();
            scaleDown2.playFromStart();
        });

        node.setOnMouseReleased(mouseEvent -> {
            scaleDown2.stop();
            scaleUp2.playFromStart();
        });
    }

    // ANIMATION KHI FORM HIỂN THỊ
    public static void playFormAnimation(Node formNode) {
        formNode.setOpacity(0);
        formNode.setTranslateY(50);

        FadeTransition fade = new FadeTransition(Duration.millis(1500), formNode);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition translate = new TranslateTransition(Duration.millis(1500), formNode);
        translate.setFromY(80);
        translate.setToY(0);

        ParallelTransition pt = new ParallelTransition(fade, translate);
        pt.play();
    }

    // ANIMATION KHI BẢNG HIỂN THỊ
    public static <T> void animateTableRows(TableView<T> tableView) {
        tableView.setRowFactory(tv -> {
            javafx.scene.control.TableRow<T> row = new javafx.scene.control.TableRow<>();

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && oldItem == null) {
                    row.setOpacity(0);
                    row.setTranslateX(50);

                    FadeTransition fade = new FadeTransition(Duration.millis(500), row);
                    fade.setToValue(1);

                    TranslateTransition slide = new TranslateTransition(Duration.millis(500), row);
                    slide.setToX(0);

                    ParallelTransition pt = new ParallelTransition(fade, slide);
                    pt.setInterpolator(Interpolator.EASE_OUT);

                    long delay = Math.min(row.getIndex() * 70, 800);
                    pt.setDelay(Duration.millis(delay));

                    pt.play();
                }
            });
            return row;
        });
    }

    // HIỂN THỊ THÔNG BÁO (NOTIFICATIONS)
    private static Label currentToast;
    private static SequentialTransition currentToastAnimation;

    public static void showAlert(Node node, String message, boolean isError) {
        if (node == null || node.getScene() == null) return;
        Pane rootPane = (Pane) node.getScene().getRoot();

        if (currentToast != null) {
            if (currentToastAnimation != null) {
                currentToastAnimation.stop();
            }
            rootPane.getChildren().remove(currentToast);
        }

        Label toast = new Label(message);
        currentToast = toast;

        String bgColor = isError ? "#E53935" : "#43A047";
        toast.setStyle("-fx-background-color: " + bgColor + "; " +
                "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; " +
                "-fx-padding: 12 25; -fx-background-radius: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        toast.setManaged(false);
        rootPane.getChildren().add(toast);
        toast.applyCss();
        toast.autosize();

        toast.layoutXProperty().bind(rootPane.widthProperty().subtract(toast.widthProperty()).divide(2));
        toast.setLayoutY(0);
        toast.setTranslateY(-50);
        toast.setOpacity(0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), toast);
        slideIn.setToY(30);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), toast);
        fadeIn.setToValue(1);
        ParallelTransition showAnim = new ParallelTransition(slideIn, fadeIn);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), toast);
        slideOut.setToY(-30);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setToValue(0);
        ParallelTransition hideAnim = new ParallelTransition(slideOut, fadeOut);

        hideAnim.setDelay(Duration.seconds(2.5));
        hideAnim.setOnFinished(e -> {
            rootPane.getChildren().remove(toast);
            currentToast = null;
        });

        currentToastAnimation = new SequentialTransition(showAnim, hideAnim);
        currentToastAnimation.play();
    }

    // HIỂN THỊ HỘP THOẠI XÁC NHẬN (YES/NO)
    public static boolean showCustomConfirm(String title, String content, String btnYesText, String btnNoText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        StackPane iconPane = new StackPane();
        Circle bg = new Circle(22, Color.web("#FEF3C7"));
        Label exclamation = new Label("!");
        exclamation.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #D97706;");
        iconPane.getChildren().addAll(bg, exclamation);
        alert.setGraphic(iconPane);

        ButtonType buttonYes = new ButtonType(btnYesText, ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType(btnNoText, ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(Others.class.getResource("/style.css").toExternalForm());
            dialogPane.getStyleClass().add("modern-alert");
        } catch (Exception e) {
            System.out.println("Không tìm thấy file CSS cho Alert ở đường dẫn /GUI/style.css");
        }

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonYes;
    }
}
