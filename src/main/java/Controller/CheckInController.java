package Controller;

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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckInController {
    @FXML private VBox mainPane;
    @FXML private Label lblSoPhong, lblLoaiPhong;
    @FXML private TextField txtHoTen, txtSDT, txtCCCD, txtEmail, txtDiaChi, txtTienCoc, txtSoGioThue;
    @FXML private ComboBox<String> cbHinhThuc;
    @FXML private DatePicker dpCheckIn, dpCheckOut;
    @FXML private ComboBox<Integer> cbGioCheckIn, cbPhutCheckIn;
    @FXML private FlowPane fpExtraRooms;
    @FXML private Button btnCancel, btnSave;

    private Phong currentRoom;
    private boolean isReservationMode = false;
    private int editingMaDatPhong = -1;
    private List<String> oldDanhSachPhong = new ArrayList<>();

    public void setPhongData(Phong p) {
        this.currentRoom = p;
        this.isReservationMode = (p == null);

        setupRoomInfo(p);
        setupButtons();
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

    private void setupButtons() {
        if (btnSave != null) Others.playButtonAnimation(btnSave);
        if (btnCancel != null) Others.playButtonAnimation(btnCancel);
    }

    private void initDefaultDateTime() {
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(1));

        cbGioCheckIn.getItems().clear();
        cbPhutCheckIn.getItems().clear();
        for (int i = 0; i < 24; i++) cbGioCheckIn.getItems().add(i);
        for (int i = 0; i < 60; i += 5) cbPhutCheckIn.getItems().add(i);

        int currentHour = LocalDateTime.now().getHour();
        cbGioCheckIn.setValue(currentHour);
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
                    Platform.runLater(() -> dpCheckIn.setValue(oldVal != null ? oldVal : LocalDate.now()));
                }
            }
        });

        dpCheckOut.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (dpCheckIn.getValue() != null && newVal.isBefore(dpCheckIn.getValue())) {
                    Others.showAlert(mainPane, "Ngày trả phòng không được phép trước ngày nhận phòng!", true);
                    Platform.runLater(() -> dpCheckOut.setValue(dpCheckIn.getValue().plusDays(1)));
                    return;
                }

                if ("Theo ngày".equals(cbHinhThuc.getValue()) && newVal.isEqual(LocalDate.now())) {
                    if (LocalTime.now().isAfter(LocalTime.of(12, 0))) {
                        Others.showAlert(mainPane, "Hiện tại đã quá 12:00, không thể chọn trả phòng vào 12:00 hôm nay. \nVui lòng chọn từ ngày mai!", true);
                        Platform.runLater(() -> dpCheckOut.setValue(LocalDate.now().plusDays(1)));
                    }
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
            if (isReservationMode) {
                dpCheckIn.setDisable(false);
                cbGioCheckIn.setDisable(false);
                cbPhutCheckIn.setDisable(false);
            } else {
                dpCheckIn.setDisable(true);
                cbGioCheckIn.setDisable(true);
                cbPhutCheckIn.setDisable(true);
            }

            if ("Theo giờ".equals(newVal)) {
                txtSoGioThue.setDisable(false);
                dpCheckOut.setDisable(true);
            } else if ("Qua đêm".equals(newVal)) {
                txtSoGioThue.setDisable(true);
                txtSoGioThue.clear();
                dpCheckOut.setDisable(true);
                cbGioCheckIn.setValue(22);
            } else {
                txtSoGioThue.setDisable(true);
                txtSoGioThue.clear();
                dpCheckOut.setDisable(false);
                cbGioCheckIn.setValue(14);
            }
        });
    }

    private void setupInputConstraints() {
        Others.setMaxLength(txtHoTen, 100);
        Others.setMaxLength(txtSDT, 10); Others.setNumericOnly(txtSDT);
        Others.setMaxLength(txtCCCD, 20);
        Others.setMaxLength(txtTienCoc, 10); Others.setNumericOnly(txtTienCoc);
        Others.setMaxLength(txtSoGioThue, 3); Others.setNumericOnly(txtSoGioThue);

        if (!isReservationMode) {
            LocalDateTime now = LocalDateTime.now();
            dpCheckIn.setValue(now.toLocalDate());
            cbGioCheckIn.setValue(now.getHour());
            cbPhutCheckIn.setValue(now.getMinute());

            dpCheckIn.setDisable(true);
            cbGioCheckIn.setDisable(true);
            cbPhutCheckIn.setDisable(true);

            dpCheckIn.setStyle("-fx-opacity: 0.8;");
        } else {
            dpCheckIn.setDisable(false);
            cbGioCheckIn.setDisable(false);
            cbPhutCheckIn.setDisable(false);
        }
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
                }
            }
        });
    }

    private void loadExtraEmptyRooms(String primaryRoomNumber) {
        if (fpExtraRooms == null) return;
        fpExtraRooms.getChildren().clear();
        List<Phong> allRooms = PhongBLL.getAllRooms();
        for (Phong room : allRooms) {
            if ("Trống".equals(room.getTrangThai()) && (primaryRoomNumber == null || !room.getSoPhong().equals(primaryRoomNumber))) {
                String type = room.getMaLoaiPhong() == 2 ? "DELUXE" : (room.getMaLoaiPhong() == 3 ? "SUITE" : "STANDARD");
                CheckBox cb = new CheckBox("Phòng " + room.getSoPhong() + " (" + type + ")");
                cb.setUserData(room.getSoPhong());
                cb.setStyle("-fx-cursor: hand; -fx-text-fill: #34495e; -fx-font-size: 13px;");
                fpExtraRooms.getChildren().add(cb);
            }
        }
    }

    public void loadExistBooking(int maDatPhong) {
        this.editingMaDatPhong = maDatPhong;
        Map<String, Object> data = DatPhongBLL.getBookingFullInfo(maDatPhong);
        if (data.isEmpty()) return;

        txtHoTen.setText((String) data.get("HoTen"));
        txtSDT.setText((String) data.get("SoDienThoai"));
        txtCCCD.setText((String) data.get("CCCD"));
        txtEmail.setText((String) data.get("Email"));
        txtDiaChi.setText((String) data.get("DiaChi"));
        txtTienCoc.setText(String.valueOf(data.get("TienCoc")).replace(".0", ""));

        LocalDateTime in = (LocalDateTime) data.get("NgayIn");
        LocalDateTime out = (LocalDateTime) data.get("NgayOut");
        dpCheckIn.setValue(in.toLocalDate());
        cbGioCheckIn.setValue(in.getHour());
        cbPhutCheckIn.setValue(in.getMinute());
        dpCheckOut.setValue(out.toLocalDate());

        String dsPhongStr = (String) data.get("DanhSachPhong");
        if (dsPhongStr != null) {
            oldDanhSachPhong = java.util.Arrays.asList(dsPhongStr.split(", "));
            for (Node node : fpExtraRooms.getChildren()) {
                if (node instanceof CheckBox cb && oldDanhSachPhong.contains((String) cb.getUserData())) {
                    cb.setSelected(true);
                }
            }
        }
    }

    @FXML
    void handleConfirmCheckIn(ActionEvent event) {
        if (txtHoTen.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty() || txtCCCD.getText().trim().isEmpty()) {
            Others.showAlert(mainPane, "Vui lòng nhập đầy đủ thông tin (*)", true);
            return;
        }

        try {
            String hinhThuc = cbHinhThuc.getValue();

            LocalDateTime thoiGianCheckIn;
            if (!isReservationMode) {
                thoiGianCheckIn = LocalDateTime.now();
            } else {
                LocalDate ngayCheckIn = dpCheckIn.getValue();
                Integer gio = cbGioCheckIn.getValue() != null ? cbGioCheckIn.getValue() : 14;
                Integer phut = cbPhutCheckIn.getValue() != null ? cbPhutCheckIn.getValue() : 0;
                thoiGianCheckIn = ngayCheckIn.atTime(gio, phut);

                if (thoiGianCheckIn.isBefore(LocalDateTime.now().minusMinutes(10))) {
                    Others.showAlert(mainPane, "Thời gian nhận phòng đặt trước không thể nằm trong quá khứ!", true);
                    return;
                }
            }

            LocalDateTime thoiGianCheckOut = null;
            if ("Theo ngày".equals(hinhThuc)) {
                thoiGianCheckOut = dpCheckOut.getValue().atTime(12, 0);
            } else if ("Qua đêm".equals(hinhThuc)) {
                thoiGianCheckOut = thoiGianCheckIn.toLocalDate().plusDays(1).atTime(8, 0);
            } else {
                String soGioStr = txtSoGioThue.getText().trim();
                if (soGioStr.isEmpty()) {
                    Others.showAlert(mainPane, "Vui lòng nhập số giờ thuê!", true);
                    return;
                }
                int soGio = Integer.parseInt(soGioStr);
                if (soGio <= 0) {
                    Others.showAlert(mainPane, "Số giờ thuê phải lớn hơn 0!", true);
                    return;
                }
                thoiGianCheckOut = thoiGianCheckIn.plusHours(soGio);
            }

            if (thoiGianCheckOut != null && (thoiGianCheckOut.isBefore(thoiGianCheckIn) || thoiGianCheckOut.isEqual(thoiGianCheckIn))) {
                if ("Theo ngày".equals(hinhThuc)) {
                    Others.showAlert(mainPane, "Thời gian trả phòng (12:00 hôm nay) đã qua hoặc trùng với giờ nhận. \nVui lòng chọn ngày trả phòng là ngày mai!", true);
                } else {
                    Others.showAlert(mainPane, "Thời gian trả phòng phải sau thời gian nhận phòng!", true);
                }
                return;
            }

            List<String> danhSachPhongChon = new ArrayList<>();
            if (currentRoom != null) danhSachPhongChon.add(currentRoom.getSoPhong());
            for (Node node : fpExtraRooms.getChildren()) {
                if (node instanceof CheckBox cb && cb.isSelected()) danhSachPhongChon.add((String) cb.getUserData());
            }

            if (danhSachPhongChon.isEmpty()) {
                Others.showAlert(mainPane, "Vui lòng chọn ít nhất 1 phòng!", true);
                return;
            }

            for (String soPhong : danhSachPhongChon) {
                if (BusinessBLL.DatPhongBLL.checkDateConflict(soPhong, thoiGianCheckIn, thoiGianCheckOut, editingMaDatPhong)) {
                    Others.showAlert(mainPane, "Phòng " + soPhong + " đã có người đặt trong khoảng thời gian này!", true);
                    return;
                }
            }

            String trangThaiDon;
            if (isReservationMode) {
                trangThaiDon = "Chờ nhận phòng";
            } else {
                trangThaiDon = thoiGianCheckIn.isAfter(LocalDateTime.now().plusMinutes(15)) ? "Chờ nhận phòng" : "Đang ở";
            }

            double tienCoc = 0;
            String tienCocStr = txtTienCoc.getText().trim();
            if (!tienCocStr.isEmpty()) {
                tienCoc = Double.parseDouble(tienCocStr);
                if (tienCoc < 0) {
                    Others.showAlert(mainPane, "Tiền cọc không được là số âm!", true);
                    return;
                }
            }

            boolean isSuccess;
            if (editingMaDatPhong != -1) {
                isSuccess = BusinessBLL.DatPhongBLL.updateBooking(editingMaDatPhong, Others.standardizeName(txtHoTen.getText()), txtSDT.getText(), txtCCCD.getText(), txtEmail.getText(), txtDiaChi.getText(), danhSachPhongChon, thoiGianCheckIn, thoiGianCheckOut, tienCoc);
            } else {
                isSuccess = BusinessBLL.DatPhongBLL.processCheckIn(Others.standardizeName(txtHoTen.getText()), txtSDT.getText(), txtCCCD.getText(), txtEmail.getText(), txtDiaChi.getText(), danhSachPhongChon, Utilities.UserSession.getInstance().getNhanVien().getMaNhanVien(), thoiGianCheckIn, thoiGianCheckOut, tienCoc, trangThaiDon);

                if (isSuccess && "Đang ở".equals(trangThaiDon)) {
                    for (String soPhong : danhSachPhongChon) {
                        BusinessBLL.PhongBLL.updateRoomStatus(soPhong, "Đang có khách");
                    }
                }
            }

            if (isSuccess) {
                Others.showAlert(mainPane, "Lưu thông tin thành công!", false);
                ((Stage) mainPane.getScene().getWindow()).close();
            } else {
                Others.showAlert(mainPane, "Có lỗi xảy ra khi lưu dữ liệu!", true);
            }

        } catch (NumberFormatException e) {
            Others.showAlert(mainPane, "Lỗi: Số giờ thuê hoặc Tiền cọc phải là chữ số hợp lệ!", true);
        }
    }

    @FXML void handleCancel(ActionEvent event) {
        ((Stage) mainPane.getScene().getWindow()).close();
    }
}