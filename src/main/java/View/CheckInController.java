package View;

import BusinessBLL.DatPhongBLL;
import BusinessBLL.PhongBLL;
import EntitiesDTO.Phong;
import Utilities.Others;
import Utilities.UserSession;
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
    @FXML private DatePicker dpCheckOut;
    @FXML private FlowPane fpExtraRooms;

    private Phong currentRoom;

    public void setPhongData(Phong p) {
        this.currentRoom = p;
        lblSoPhong.setText(p.getSoPhong());

        String loai = "STANDARD";
        if (p.getMaLoaiPhong() == 2) loai = "DELUXE";
        if (p.getMaLoaiPhong() == 3) loai = "SUITE";
        lblLoaiPhong.setText(loai);

        dpCheckOut.setValue(LocalDate.now().plusDays(1));

        cbHinhThuc.getItems().addAll("Theo ngày", "Qua đêm", "Theo giờ");
        cbHinhThuc.setValue("Theo ngày");
        txtSoGioThue.setDisable(true);

        cbHinhThuc.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Theo giờ".equals(newVal)) {
                txtSoGioThue.setDisable(false);
                dpCheckOut.setDisable(true);

            } else if ("Qua đêm".equals(newVal)) {
                txtSoGioThue.setDisable(true);
                txtSoGioThue.clear();
                dpCheckOut.setValue(LocalDate.now().plusDays(1));
                dpCheckOut.setDisable(true);

            } else {
                txtSoGioThue.setDisable(true);
                txtSoGioThue.clear();
                dpCheckOut.setDisable(false);
            }
        });

        Others.setMaxLength(txtHoTen, 100);
        Others.setMaxLength(txtSDT, 10); Others.setNumericOnly(txtSDT);
        Others.setMaxLength(txtCCCD, 20);
        Others.setMaxLength(txtEmail, 100);
        Others.setMaxLength(txtDiaChi, 255);
        Others.setMaxLength(txtTienCoc, 10); Others.setNumericOnly(txtTienCoc);
        Others.setMaxLength(txtSoGioThue, 3); Others.setNumericOnly(txtSoGioThue);

        loadExtraEmptyRooms(p.getSoPhong());
    }

    private void loadExtraEmptyRooms(String primaryRoomNumber) {
        if (fpExtraRooms == null) return;

        fpExtraRooms.getChildren().clear();
        List<Phong> allRooms = PhongBLL.getAllRooms();

        for (Phong room : allRooms) {
            if ("Trống".equals(room.getTrangThai()) && !room.getSoPhong().equals(primaryRoomNumber)) {
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

        String hoTenChuan = Others.standardizeName(txtHoTen.getText());
        txtHoTen.setText(hoTenChuan);

        LocalDate ngayCheckIn = LocalDate.now();
        LocalDate ngayCheckOut = dpCheckOut.getValue();

        if (ngayCheckOut == null || ngayCheckOut.isBefore(ngayCheckIn)) {
            Others.showAlert(mainPane, "Ngày trả phòng không được nhỏ hơn ngày hôm nay!", true);
            return;
        }

        List<String> danhSachPhongChon = new ArrayList<>();
        danhSachPhongChon.add(currentRoom.getSoPhong());

        if (fpExtraRooms != null) {
            for (Node node : fpExtraRooms.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox cb = (CheckBox) node;
                    if (cb.isSelected()) {
                        danhSachPhongChon.add((String) cb.getUserData());
                    }
                }
            }
        }

        LocalDateTime thoiGianCheckOut = null;

        if ("Theo ngày".equals(hinhThuc)) {
            thoiGianCheckOut = dpCheckOut.getValue().atTime(12, 0);

        } else if ("Qua đêm".equals(hinhThuc)) {
            thoiGianCheckOut = dpCheckOut.getValue().atTime(8, 0);

        } else if ("Theo giờ".equals(hinhThuc)) {
            int soGio = Integer.parseInt(txtSoGioThue.getText());
            thoiGianCheckOut = LocalDateTime.now().plusHours(soGio);
        }

        for (String soPhong : danhSachPhongChon) {
            boolean daCoNguoiDat = DatPhongBLL.checkDateConflict(soPhong, ngayCheckIn, ngayCheckOut);
            if (daCoNguoiDat) {
                Others.showAlert(mainPane, "Phòng " + soPhong + " đã có người đặt trước trong thời gian này!", true);
                return;
            }
        }

        String sdt = txtSDT.getText();
        String cccd = txtCCCD.getText();
        String email = txtEmail.getText();
        String diaChi = txtDiaChi.getText();
        double tienCoc = txtTienCoc.getText().isEmpty() ? 0 : Double.parseDouble(txtTienCoc.getText());
        int employeeId = UserSession.getInstance().getMaNhanVien();

        boolean isBookingSaved = DatPhongBLL.processCheckIn(
                hoTenChuan, sdt, cccd, email, diaChi,
                danhSachPhongChon, employeeId,
                LocalDateTime.now(),
                thoiGianCheckOut,
                tienCoc
        );

        if (!isBookingSaved) {
            Others.showAlert(mainPane, "Lỗi khi tạo hóa đơn nhận phòng!", true);
            return;
        }

        for (String soPhong : danhSachPhongChon) {
            PhongBLL.updateRoomStatus(soPhong, "Đang có khách");
        }

        Others.showAlert(mainPane, "Check-in thành công " + danhSachPhongChon.size() + " phòng!", false);
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }
}