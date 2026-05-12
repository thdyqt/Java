package View;

import BusinessBLL.DatPhongBLL;
import BusinessBLL.KhachHangBLL;
import BusinessBLL.PhongBLL;
import EntitiesDTO.KhachHang;
import EntitiesDTO.Phong;
import Utilities.Others;
import Utilities.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckInController {
    @FXML private VBox mainPane;
    @FXML private Label lblSoPhong, lblLoaiPhong;
    @FXML private TextField txtHoTen, txtSDT, txtCCCD, txtEmail, txtDiaChi, txtTienCoc, txtSoGioThue;
    @FXML private ComboBox<String> cbHinhThuc;
    @FXML private DatePicker dpCheckIn, dpCheckOut;
    @FXML private ComboBox<Integer> cbGioCheckIn, cbPhutCheckIn;
    @FXML private FlowPane fpExtraRooms;

    private Phong currentRoom;

    public void setPhongData(Phong p) {
        this.currentRoom = p;

        setupRoomInfo(p);
        initDefaultDateTime();
        setupDateTimeValidators();
        setupRentalTypeLogic();
        setupInputConstraints();
        setupCustomerAutoFill();

        loadExtraEmptyRooms(p != null ? p.getSoPhong() : null);
    }

    private void setupRoomInfo(Phong p) {
        if (p != null) {
            lblSoPhong.setText(p.getSoPhong());
            String loai = "STANDARD";
            if (p.getMaLoaiPhong() == 2) loai = "DELUXE";
            if (p.getMaLoaiPhong() == 3) loai = "SUITE";
            lblLoaiPhong.setText(loai);
        } else {
            lblSoPhong.setText("Tùy chọn");
            lblLoaiPhong.setText("---");
        }
    }

    private void initDefaultDateTime() {
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(1));

        cbGioCheckIn.getItems().clear();
        cbPhutCheckIn.getItems().clear();
        for (int i = 0; i < 24; i++) cbGioCheckIn.getItems().add(i);
        for (int i = 0; i < 60; i += 5) cbPhutCheckIn.getItems().add(i);

        cbGioCheckIn.setValue(14);
        cbPhutCheckIn.setValue(0);
    }

    private void setupDateTimeValidators() {
        dpCheckIn.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (dpCheckOut.getValue() == null || dpCheckOut.getValue().isBefore(newVal)) {
                    dpCheckOut.setValue(newVal.plusDays(1));
                }

                if (newVal.isBefore(LocalDate.now())) {
                    Others.showAlert(mainPane, "Ngày nhận phòng không được chọn trong quá khứ!", true);
                    Platform.runLater(() -> {
                        if (oldVal != null && !oldVal.isBefore(LocalDate.now())) {
                            dpCheckIn.setValue(oldVal);
                        } else {
                            dpCheckIn.setValue(LocalDate.now());
                        }
                    });
                }
            }
        });

        dpCheckOut.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dpCheckIn.getValue() != null) {
                // Chặn ngày Checkout trước Checkin
                if (newVal.isBefore(dpCheckIn.getValue())) {
                    Others.showAlert(mainPane, "Ngày trả phòng không được phép trước ngày nhận phòng!", true);
                    Platform.runLater(() -> {
                        if (oldVal != null && !oldVal.isBefore(dpCheckIn.getValue())) {
                            dpCheckOut.setValue(oldVal);
                        } else {
                            dpCheckOut.setValue(dpCheckIn.getValue().plusDays(1));
                        }
                    });
                }
            }
        });
    }

    private void setupRentalTypeLogic() {
        cbHinhThuc.getItems().clear();
        cbHinhThuc.getItems().addAll("Theo ngày", "Qua đêm", "Theo giờ");
        cbHinhThuc.setValue("Theo ngày");
        txtSoGioThue.setDisable(true);

        cbHinhThuc.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Theo giờ".equals(newVal)) {
                txtSoGioThue.setDisable(false);
                dpCheckIn.setDisable(true);
                dpCheckOut.setDisable(true);
                cbGioCheckIn.setDisable(true);
                cbPhutCheckIn.setDisable(true);
                dpCheckIn.setValue(LocalDate.now());
            } else if ("Qua đêm".equals(newVal)) {
                txtSoGioThue.setDisable(true);
                txtSoGioThue.clear();
                dpCheckIn.setDisable(true);
                dpCheckOut.setDisable(true);
                cbGioCheckIn.setDisable(true);
                cbPhutCheckIn.setDisable(true);
                dpCheckIn.setValue(LocalDate.now());
                dpCheckOut.setValue(LocalDate.now().plusDays(1));
            } else {
                txtSoGioThue.setDisable(true);
                txtSoGioThue.clear();
                dpCheckIn.setDisable(false);
                dpCheckOut.setDisable(false);
                cbGioCheckIn.setDisable(false);
                cbPhutCheckIn.setDisable(false);
            }
        });
    }

    private void setupInputConstraints() {
        Others.setMaxLength(txtHoTen, 100);
        Others.setMaxLength(txtSDT, 10);
        Others.setNumericOnly(txtSDT);
        Others.setMaxLength(txtCCCD, 20);
        Others.setMaxLength(txtEmail, 100);
        Others.setMaxLength(txtDiaChi, 255);
        Others.setMaxLength(txtTienCoc, 10);
        Others.setNumericOnly(txtTienCoc);
        Others.setMaxLength(txtSoGioThue, 3);
        Others.setNumericOnly(txtSoGioThue);
    }

    private void setupCustomerAutoFill() {
        txtCCCD.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 9 || newValue.length() == 12) {
                KhachHang kh = KhachHangBLL.getCustomerByCCCD(newValue);
                if (kh != null) {
                    txtHoTen.setText(kh.getHoTen());
                    txtSDT.setText(kh.getSoDienThoai());
                    txtEmail.setText(kh.getEmail());
                    txtDiaChi.setText(kh.getDiaChi());
                    Others.showAlert(mainPane, "👋 Chào mừng khách quen quay lại: " + kh.getHoTen(), false);
                }
            }
        });
    }

    private void loadExtraEmptyRooms(String primaryRoomNumber) {
        if (fpExtraRooms == null) return;
        fpExtraRooms.getChildren().clear();
        List<Phong> allRooms = PhongBLL.getAllRooms();

        for (Phong room : allRooms) {
            if ("Trống".equals(room.getTrangThai())) {
                if (primaryRoomNumber != null && room.getSoPhong().equals(primaryRoomNumber)) {
                    continue; // Bỏ qua phòng chính (nếu có)
                }

                String type = room.getMaLoaiPhong() == 2 ? "DELUXE" : (room.getMaLoaiPhong() == 3 ? "SUITE" : "STANDARD");
                CheckBox cb = new CheckBox("Phòng " + room.getSoPhong() + " (" + type + ")");
                cb.setUserData(room.getSoPhong());
                cb.setStyle("-fx-cursor: hand; -fx-text-fill: #34495e; -fx-font-size: 13px;");
                fpExtraRooms.getChildren().add(cb);
            }
        }
    }

    @FXML
    void handleConfirmCheckIn(ActionEvent event) {
        if (txtHoTen.getText().isEmpty() || txtSDT.getText().isEmpty() || txtCCCD.getText().isEmpty()) {
            Others.showAlert(mainPane, "Vui lòng nhập đầy đủ thông tin bắt buộc (*)", true);
            return;
        }

        String hinhThuc = cbHinhThuc.getValue();
        if ("Theo giờ".equals(hinhThuc) && txtSoGioThue.getText().isEmpty()) {
            Others.showAlert(mainPane, "Vui lòng nhập số giờ muốn thuê!", true);
            return;
        }

        LocalDate ngayCheckIn = dpCheckIn.getValue();
        LocalDate ngayCheckOut = dpCheckOut.getValue();

        if (ngayCheckIn == null || ngayCheckIn.isBefore(LocalDate.now())) {
            Others.showAlert(mainPane, "Ngày nhận phòng không hợp lệ!", true);
            return;
        }

        List<String> danhSachPhongChon = new ArrayList<>();

        if (currentRoom != null) {
            danhSachPhongChon.add(currentRoom.getSoPhong());
        }

        if (fpExtraRooms != null) {
            for (Node node : fpExtraRooms.getChildren()) {
                if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                    danhSachPhongChon.add((String) node.getUserData());
                }
            }
        }

        if (danhSachPhongChon.isEmpty()) {
            Others.showAlert(mainPane, "Vui lòng chọn ít nhất 1 phòng để đặt!", true);
            return;
        }

        LocalDateTime thoiGianCheckIn;
        LocalDateTime thoiGianCheckOut = null;

        if ("Theo ngày".equals(hinhThuc)) {
            thoiGianCheckIn = ngayCheckIn.atTime(cbGioCheckIn.getValue(), cbPhutCheckIn.getValue());
            thoiGianCheckOut = ngayCheckOut.atTime(12, 0);
        } else if ("Qua đêm".equals(hinhThuc)) {
            thoiGianCheckIn = LocalDateTime.now();
            thoiGianCheckOut = ngayCheckOut.atTime(8, 0);
        } else {
            thoiGianCheckIn = LocalDateTime.now();
            int soGio = Integer.parseInt(txtSoGioThue.getText());
            thoiGianCheckOut = LocalDateTime.now().plusHours(soGio);
        }

        if (thoiGianCheckIn.isBefore(LocalDateTime.now().minusMinutes(5))) {
            Others.showAlert(mainPane, "Thời gian nhận phòng không hợp lệ (đang nằm trong quá khứ)! Vui lòng chỉnh lại Giờ/Phút.", true);

            cbGioCheckIn.setStyle("-fx-border-color: red; -fx-border-radius: 4;");
            cbPhutCheckIn.setStyle("-fx-border-color: red; -fx-border-radius: 4;");

            cbGioCheckIn.setOnMouseClicked(e -> cbGioCheckIn.setStyle(""));
            cbPhutCheckIn.setOnMouseClicked(e -> cbPhutCheckIn.setStyle(""));

            return;
        }

        for (String soPhong : danhSachPhongChon) {
            if (DatPhongBLL.checkDateConflict(soPhong, ngayCheckIn, ngayCheckOut)) {
                Others.showAlert(mainPane, "Phòng " + soPhong + " đã có người đặt trước trong thời gian này!", true);
                return;
            }
        }

        // Xác định trạng thái đơn dựa trên ngày nhận phòng
        String trangThaiDon = ngayCheckIn.equals(LocalDate.now()) ? "Đang ở" : "Chờ nhận phòng";
        double tienCoc = txtTienCoc.getText().isEmpty() ? 0 : Double.parseDouble(txtTienCoc.getText());

        // Lưu đơn hàng
        boolean isBookingSaved = DatPhongBLL.processCheckIn(
                Others.standardizeName(txtHoTen.getText()), txtSDT.getText(), txtCCCD.getText(),
                txtEmail.getText(), txtDiaChi.getText(), danhSachPhongChon,
                UserSession.getInstance().getMaNhanVien(), thoiGianCheckIn, thoiGianCheckOut,
                tienCoc, trangThaiDon
        );

        if (isBookingSaved) {
            if ("Đang ở".equals(trangThaiDon)) {
                for (String soPhong : danhSachPhongChon) {
                    PhongBLL.updateRoomStatus(soPhong, "Đang có khách");
                }
            }

            String msg = trangThaiDon.equals("Đang ở") ? "Check-in thành công!" : "Đã lập đơn đặt trước thành công!";
            Others.showAlert(mainPane, msg, false);
            ((Stage) mainPane.getScene().getWindow()).close();
        } else {
            Others.showAlert(mainPane, "Lỗi khi lưu thông tin đặt phòng!", true);
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        ((Stage) mainPane.getScene().getWindow()).close();
    }
}