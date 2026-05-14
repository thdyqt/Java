package Controller;

import BusinessBLL.PhongBLL;
import EntitiesDTO.Phong;
import Utilities.Others;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class SoDoPhongController implements Initializable {
    @FXML
    private BorderPane mainPane;

    @FXML
    private FlowPane roomGrid;

    @FXML
    private HBox filterBox;

    private List<Phong> roomList = new ArrayList<>();
    private ToggleGroup floorGroup = new ToggleGroup();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roomList = PhongBLL.getAllRooms();
        createFloorFilter();
        getFilteredRooms("Tất cả");
    }

    private void createFloorFilter() {
        filterBox.getChildren().clear();

        ToggleButton btnTatCa = new ToggleButton("Tất cả");
        btnTatCa.getStyleClass().add("floor-tab");
        btnTatCa.setToggleGroup(floorGroup);
        btnTatCa.setSelected(true);
        btnTatCa.setOnAction(e -> handleFilterFloor(btnTatCa, "Tất cả"));
        filterBox.getChildren().add(btnTatCa);

        Set<Integer> floorList = new TreeSet<>();
        for (Phong p : roomList) {
            int floor = getFloorNumber(p.getSoPhong());
            if (floor > 0) {
                floorList.add(floor);
            }
        }

        for (int floor : floorList) {
            String floorName = "Tầng " + floor;
            ToggleButton btnTang = new ToggleButton(floorName);
            btnTang.getStyleClass().add("floor-tab");
            btnTang.setToggleGroup(floorGroup);
            btnTang.setOnAction(e -> handleFilterFloor(btnTang, floorName));

            filterBox.getChildren().add(btnTang);
        }
    }

    private int getFloorNumber(String soPhong) {
        try {
            if (soPhong != null && soPhong.length() >= 3) {
                String chuoiTang = soPhong.substring(0, soPhong.length() - 2);
                return Integer.parseInt(chuoiTang);
            }
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    private void handleFilterFloor(ToggleButton clickedBtn, String tieuChi) {
        if (!clickedBtn.isSelected()) {
            clickedBtn.setSelected(true);
            return;
        }
        getFilteredRooms(tieuChi);
    }

    private void getFilteredRooms(String tieuChi) {
        roomGrid.getChildren().clear();

        for (Phong p : roomList) {
            int soTang = getFloorNumber(p.getSoPhong());
            String tangCuaPhong = "Tầng " + soTang;

            if (tieuChi.equals("Tất cả") || tieuChi.equals(tangCuaPhong)) {
                VBox roomCard = createRoomCard(p);
                roomGrid.getChildren().add(roomCard);
            }
        }
    }

    private VBox createRoomCard(Phong p) {
        VBox card = new VBox();
        card.getStyleClass().add("room-card");

        card.setUserData(p);

        String statusClass = "";
        String iconText = "";
        switch (p.getTrangThai()) {
            case "Trống":
                statusClass = "status-trong";
                iconText = "\uD83D\uDD11";
                break;
            case "Đang có khách":
                statusClass = "status-cokhach";
                iconText = "\uD83D\uDECC";
                break;
            case "Đang dọn dẹp":
                statusClass = "status-dondep";
                iconText = "\uD83D\uDCA7";
                break;
            case "Bảo trì":
            default:
                statusClass = "status-baotri";
                iconText = "\uD83D\uDEE0";
                break;
        }
        card.getStyleClass().add(statusClass);
        card.setOnMouseClicked(this::handleRoomClick);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblSoPhong = new Label(p.getSoPhong());
        lblSoPhong.getStyleClass().add("room-number");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblIcon = new Label(iconText);
        lblIcon.getStyleClass().add("room-icon");

        header.getChildren().addAll(lblSoPhong, spacer, lblIcon);

        String tenLoai = "STANDARD";
        if (p.getMaLoaiPhong() == 2) tenLoai = "DELUXE";
        if (p.getMaLoaiPhong() == 3) tenLoai = "SUITE";

        Label lblType = new Label(tenLoai);
        lblType.getStyleClass().add("room-type");

        card.getChildren().addAll(header, lblType);

        ContextMenu contextMenu = new ContextMenu();

        MenuItem itemLichSu = new MenuItem("Xem lịch đặt phòng");
        Label iconLichSu = new Label("📅");
        itemLichSu.setGraphic(iconLichSu);
        itemLichSu.setOnAction(e -> showLichSuPopup(p.getSoPhong()));

        MenuItem itemDatPhong = new MenuItem("Đặt phòng trước (Reservation)");
        Label iconDatPhong = new Label("✍️");
        itemDatPhong.setGraphic(iconDatPhong);

        itemDatPhong.setOnAction(e -> openDialog("/CheckInView.fxml", "Đặt phòng trước: Phòng " + p.getSoPhong(), p));

        contextMenu.getItems().addAll(itemDatPhong, itemLichSu);

        card.setOnContextMenuRequested(e ->
                contextMenu.show(card, e.getScreenX(), e.getScreenY())
        );

        return card;
    }

    private void openDialog(String fxmlPath, String title, Phong selectedRoom) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ChiTietPhongController) {
                int activeBookingId = BusinessBLL.DatPhongBLL.getActiveBookingIdByRoomId(selectedRoom.getMaPhong());

                if (activeBookingId != -1) {
                    ((ChiTietPhongController) controller).loadBookingData(activeBookingId);
                } else {
                    Others.showAlert(mainPane, "Lỗi dữ liệu: Không tìm thấy hồ sơ đặt phòng của phòng này!", true);
                    return;
                }
            }
            else if (controller instanceof CheckInController) {
                ((CheckInController)controller).setPhongData(selectedRoom);
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            roomList = PhongBLL.getAllRooms();
            ToggleButton currentFloor = (ToggleButton) floorGroup.getSelectedToggle();
            getFilteredRooms(currentFloor.getText());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Không tìm thấy file giao diện: " + fxmlPath);
        }
    }

    @FXML
    void handleRoomClick(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        VBox clickedRoom = (VBox) event.getSource();
        Phong p = (Phong) clickedRoom.getUserData();

        if (p.getTrangThai().equals("Trống")) {
            openDialog("/CheckInView.fxml", "Check-in: Phòng " + p.getSoPhong(), p);
        }
        else if (p.getTrangThai().equals("Đang có khách")) {
            openDialog("/ChiTietPhongView.fxml", "Quản lý phòng " + p.getSoPhong(), p);
        }
        else {
            if (Others.showCustomConfirm("Xác nhận", "Phòng " + p.getSoPhong() + " đã dọn dẹp hoặc bảo trì xong?", "Có", "Không")) {
                boolean success = PhongBLL.updateRoomStatus(p.getSoPhong(), "Trống");

                if (success) {
                    Others.showAlert(mainPane, "Đã cập nhật trạng thái phòng " + p.getSoPhong() + " thành công!", false);
                    roomList = PhongBLL.getAllRooms();
                    ToggleButton currentFloor = (ToggleButton) floorGroup.getSelectedToggle();
                    getFilteredRooms(currentFloor.getText());
                } else {
                    Others.showAlert(mainPane, "Cập nhật trạng thái phòng " + p.getSoPhong() + " thất bại!", true);
                }
            }
        }
    }

    private void showLichSuPopup(String soPhong) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LichSuDatView.fxml"));
            Parent root = loader.load();

            LichSuDatController controller = loader.getController();
            controller.loadData(soPhong);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Lịch đặt phòng " + soPhong);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}