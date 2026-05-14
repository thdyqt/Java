package Controller;

import BusinessBLL.*;
import EntitiesDTO.*;
import Utilities.Others;
import Utilities.UserSession;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

public class ChiTietPhongController {
    @FXML private VBox mainPane;

    // Thông tin khách hàng
    @FXML private Label lblHoTen, lblSDT, lblCCCD, lblEmail, lblDiaChi;

    // Thông tin thời gian
    @FXML private Label lblNgayDat, lblNgayVao, lblNgayRa, lblDanhSachPhong;

    // Thống kê chi phí
    @FXML private Label lblTienPhong, lblTienDichVu, lblTienCoc, lblTongThanhToan;

    @FXML private TextField txtPhuThu, txtGiamGia;
    @FXML private ComboBox<String> cbPhuongThuc;
    @FXML private Button btnAddService, btnCheckout;

    // Bảng chi tiết tiền từng phòng
    @FXML private TableView<ChiTietDatPhong> tvChiTietPhong;
    @FXML private TableColumn<ChiTietDatPhong, String> colSoPhong, colLoaiPhong, colTienPhongLe;

    // Bảng dịch vụ
    @FXML private TableView<SuDungDichVu> tvDichVu;
    @FXML private TableColumn<SuDungDichVu, Number> colSTT;
    @FXML private TableColumn<SuDungDichVu, String> colTenDV, colDonGia, colThanhTien, colThoiGian;
    @FXML private TableColumn<SuDungDichVu, Integer> colSoLuong;

    private int currentBookingId;
    private DatPhong currentBooking;
    private double totalRoomMoney = 0;
    private double totalServiceMoney = 0;

    @FXML
    public void initialize() {
        Others.playButtonAnimation(btnCheckout);
        Others.playButtonAnimation(btnAddService);

        cbPhuongThuc.getItems().addAll("Tiền mặt", "Chuyển khoản");
        cbPhuongThuc.setValue("Tiền mặt");

        cbPhuongThuc.setPrefWidth(200);
        cbPhuongThuc.setMaxWidth(200);

        colSoPhong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSoPhong()));
        colLoaiPhong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLoaiPhong()));
        colTienPhongLe.setCellValueFactory(cellData -> new SimpleStringProperty(Others.formatPrice(cellData.getValue().getTienPhongThucTe())));

        colSoPhong.prefWidthProperty().bind(tvChiTietPhong.widthProperty().multiply(0.30));
        colLoaiPhong.prefWidthProperty().bind(tvChiTietPhong.widthProperty().multiply(0.30));
        colTienPhongLe.prefWidthProperty().bind(tvChiTietPhong.widthProperty().multiply(0.395));

        colSTT.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(tvDichVu.getItems().indexOf(column.getValue()) + 1));
        colTenDV.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenDichVu()));
        colSoLuong.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSoLuong()));
        colDonGia.setCellValueFactory(cellData -> {
            double donGia = cellData.getValue().getThanhTien() / cellData.getValue().getSoLuong();
            return new SimpleStringProperty(Others.formatPrice(donGia));
        });
        colThanhTien.setCellValueFactory(cellData -> new SimpleStringProperty(Others.formatPrice(cellData.getValue().getThanhTien())));
        colThoiGian.setCellValueFactory(cellData -> new SimpleStringProperty(Others.formatDateTime(cellData.getValue().getThoiGianSuDung())));

        colSTT.prefWidthProperty().bind(tvDichVu.widthProperty().multiply(0.05));
        colTenDV.prefWidthProperty().bind(tvDichVu.widthProperty().multiply(0.25));
        colDonGia.prefWidthProperty().bind(tvDichVu.widthProperty().multiply(0.15));
        colSoLuong.prefWidthProperty().bind(tvDichVu.widthProperty().multiply(0.08));
        colThanhTien.prefWidthProperty().bind(tvDichVu.widthProperty().multiply(0.20));
        colThoiGian.prefWidthProperty().bind(tvDichVu.widthProperty().multiply(0.265));

        Others.setMaxLength(txtPhuThu, 10);
        Others.setMaxLength(txtGiamGia, 10);
        Others.setNumericOnly(txtPhuThu);
        Others.setNumericOnly(txtGiamGia);

        Others.setCurrencyFormatting(txtPhuThu);
        Others.setCurrencyFormatting(txtGiamGia);

        txtPhuThu.textProperty().addListener((obs, oldVal, newVal) -> calculateFinalTotal());
        txtGiamGia.textProperty().addListener((obs, oldVal, newVal) -> calculateFinalTotal());

        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemXoa = new MenuItem("🗑 Xóa dịch vụ này");
        itemXoa.setOnAction(e -> handleDeleteService());
        contextMenu.getItems().add(itemXoa);
        tvDichVu.setContextMenu(contextMenu);
    }

    public void loadBookingData(int bookingId) {
        this.currentBookingId = bookingId;

        this.currentBooking = DatPhongBLL.getBookingById(bookingId);
        if (currentBooking == null) return;

        KhachHang kh = KhachHangBLL.getCustomerById(currentBooking.getMaKhachHang());
        if (kh != null) {
            lblHoTen.setText(kh.getHoTen());
            lblSDT.setText("📞 " + kh.getSoDienThoai());
            lblCCCD.setText("🆔 " + kh.getCccdPassport());
            lblEmail.setText("📧 " + (kh.getEmail().isEmpty() ? "N/A" : kh.getEmail()));
            lblDiaChi.setText("📍 " + (kh.getDiaChi().isEmpty() ? "N/A" : kh.getDiaChi()));
        }

        LocalDateTime thoiGianRaDuKien = currentBooking.getNgayCheckOutDuKien();

        lblNgayDat.setText("Ngày đặt đơn: " + Others.formatDateTime(currentBooking.getNgayDat()));
        lblNgayVao.setText("Giờ vào thực tế: " + Others.formatDateTime(currentBooking.getNgayCheckInDuKien()));
        lblNgayRa.setText("Giờ ra dự kiến: " + Others.formatDateTime(thoiGianRaDuKien));
        lblTienCoc.setText(Others.formatPrice(currentBooking.getTienCoc()));

        List<ChiTietDatPhong> roomDetails = ChiTietDatPhongBLL.getChiTietPhongTheoDoan(bookingId);

        double[] extras = HoaDonBLL.tinhTienPhongThucTe(
                currentBooking.getNgayCheckInDuKien(),
                thoiGianRaDuKien,
                LocalDateTime.now(),
                roomDetails
        );

        double phuThuTuDong = extras[0];
        double giamGiaTuDong = extras[1];

        tvChiTietPhong.setItems(FXCollections.observableArrayList(roomDetails));
        totalRoomMoney = roomDetails.stream().mapToDouble(ChiTietDatPhong::getTienPhongThucTe).sum();
        lblTienPhong.setText(Others.formatPrice(totalRoomMoney));
        lblDanhSachPhong.setText("Phòng đoàn: " + String.join(", ", roomDetails.stream().map(ChiTietDatPhong::getSoPhong).toList()));

        DecimalFormat df = new DecimalFormat("#,###");

        if (phuThuTuDong > 0) {
            txtPhuThu.setText(df.format(phuThuTuDong).replace(",", ".") + "đ");
            txtPhuThu.setTooltip(new Tooltip("Khoản phạt do khách trả phòng quá giờ"));
            txtPhuThu.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2px; -fx-border-radius: 4px;");

            Others.showAlert(mainPane,
                    "⚠️ KHÁCH TRẢ PHÒNG QUÁ GIỜ:\n" +
                            "Hệ thống đã tự động cộng tiền phạt vào ô PHỤ THU: " + Others.formatPrice(phuThuTuDong),
                    false
            );
        } else {
            txtPhuThu.setText("");
            txtPhuThu.setTooltip(null);
            txtPhuThu.setStyle("");
        }

        if (giamGiaTuDong > 0) {
            txtGiamGia.setText(df.format(giamGiaTuDong).replace(",", ".") + "đ");
            txtGiamGia.setTooltip(new Tooltip("Được hoàn tiền do thanh toán sớm (Thuê theo giờ)"));
            txtGiamGia.setStyle("-fx-border-color: #10b981; -fx-border-width: 2px; -fx-border-radius: 4px;");

            Others.showAlert(mainPane,
                    "💸 KHÁCH THANH TOÁN SỚM:\n" +
                            "Giá tiền phòng gốc được giữ nguyên để lưu hóa đơn.\n" +
                            "Số tiền phòng chênh lệch được hệ thống đẩy vào mục GIẢM GIÁ: " + Others.formatPrice(giamGiaTuDong),
                    false
            );
        } else {
            txtGiamGia.setText("");
            txtGiamGia.setTooltip(null);
            txtGiamGia.setStyle("");
        }

        List<SuDungDichVu> services = SuDungDichVuBLL.getServiceByBookingId(bookingId);
        tvDichVu.setItems(FXCollections.observableArrayList(services));
        totalServiceMoney = services.stream().mapToDouble(SuDungDichVu::getThanhTien).sum();
        lblTienDichVu.setText(Others.formatPrice(totalServiceMoney));

        calculateFinalTotal();
    }

    private void calculateFinalTotal() {
        try {
            String rawPhuThu = txtPhuThu.getText().replaceAll("[^\\d]", "");
            String rawGiamGia = txtGiamGia.getText().replaceAll("[^\\d]", "");

            double phuThu = rawPhuThu.isEmpty() ? 0 : Double.parseDouble(rawPhuThu);
            double giamGia = rawGiamGia.isEmpty() ? 0 : Double.parseDouble(rawGiamGia);
            double tienCoc = currentBooking != null ? currentBooking.getTienCoc() : 0;

            double finalTotal = (totalRoomMoney + totalServiceMoney + phuThu) - (tienCoc + giamGia);

            if (finalTotal < 0) finalTotal = 0;
            lblTongThanhToan.setText(Others.formatPrice(finalTotal));

        } catch (NumberFormatException e) {
            lblTongThanhToan.setText("Đang tính...");
        }
    }

    @FXML
    void handleAddService() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ThemDichVuView.fxml"));
            Parent root = loader.load();

            ThemDichVuController controller = loader.getController();
            controller.setBookingId(this.currentBookingId);

            Stage stage = new Stage();
            stage.setTitle("Thêm Dịch Vụ Mới");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            if (controller.isSuccess()) {
                loadBookingData(this.currentBookingId);
                Others.showAlert(mainPane, "Đã thêm dịch vụ thành công!", false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Không thể mở form thêm dịch vụ!", true);
        }
    }

    private void handleDeleteService() {
        SuDungDichVu selected = tvDichVu.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Others.showAlert(mainPane, "Vui lòng chọn một dịch vụ để xóa!", true);
            return;
        }

        boolean isConfirm = Others.showCustomConfirm(
                "Xác nhận xóa",
                "Bạn có chắc chắn muốn xóa dịch vụ: " + selected.getTenDichVu() + " khỏi danh sách?",
                "Đồng ý", "Hủy"
        );

        if (isConfirm) {
            boolean success = SuDungDichVuBLL.deleteService(selected.getMaSuDung());
            if (success) {
                loadBookingData(this.currentBookingId);
                Others.showAlert(mainPane, "Đã xóa dịch vụ thành công!", false);
            } else {
                Others.showAlert(mainPane, "Lỗi khi xóa dịch vụ khỏi hệ thống!", true);
            }
        }
    }

    @FXML
    void handleCheckOut() {
        String rawPhuThu = txtPhuThu.getText().replaceAll("[^\\d]", "");
        String rawGiamGia = txtGiamGia.getText().replaceAll("[^\\d]", "");

        double phuThu = rawPhuThu.isEmpty() ? 0 : Double.parseDouble(rawPhuThu);
        double giamGia = rawGiamGia.isEmpty() ? 0 : Double.parseDouble(rawGiamGia);

        double finalTotal = (totalRoomMoney + totalServiceMoney + phuThu) - (currentBooking.getTienCoc() + giamGia);
        if (finalTotal < 0) finalTotal = 0;

        double finalTotal1 = finalTotal;
        Runnable processSavingInvoice = () -> {
            int newInvoiceId = HoaDonBLL.xuLyThanhToan(
                    currentBookingId,
                    UserSession.getInstance().getMaNhanVien(),
                    totalRoomMoney, totalServiceMoney, phuThu, giamGia,
                    currentBooking.getTienCoc(), finalTotal1, cbPhuongThuc.getValue()
            );

            if (newInvoiceId > 0) {
                showInvoicePopup(newInvoiceId);
                Others.showAlert(mainPane, "Thanh toán thành công! Hóa đơn đã được lưu.", false);
                ((Stage) mainPane.getScene().getWindow()).close();
            } else {
                Others.showAlert(mainPane, "Lỗi hệ thống khi xử lý hóa đơn!", true);
            }
        };

        if ("Chuyển khoản".equals(cbPhuongThuc.getValue())) {

            String noiDungChuyenKhoan = "Thanh toán tiền phòng mã " + currentBookingId;
            Others.showVietQR(
                    (int) finalTotal,
                    noiDungChuyenKhoan,
                    "Xác nhận đã nhận tiền",
                    mainPane,
                    processSavingInvoice
            );

        } else {
            processSavingInvoice.run();
        }
    }

    private void showInvoicePopup(int invoiceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HoaDonView.fxml"));
            Parent root = loader.load();

            HoaDon hd = HoaDonBLL.getInvoiceById(invoiceId);

            HoaDonController controller = loader.getController();
            controller.setInvoiceData(hd);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Hóa đơn điện tử");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}