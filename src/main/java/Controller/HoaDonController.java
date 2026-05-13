package Controller;

import EntitiesDTO.DatPhong; // Bổ sung import DatPhong
import EntitiesDTO.HoaDon;
import EntitiesDTO.KhachHang;
import EntitiesDTO.ChiTietDatPhong;
import BusinessBLL.DatPhongBLL; // Bổ sung import DatPhongBLL
import BusinessBLL.KhachHangBLL;
import BusinessBLL.ChiTietDatPhongBLL;
import Utilities.Others;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class HoaDonController {
    @FXML private Label lblMaHoaDon, lblTenKhach, lblNgayThanhToan, lblDanhSachPhong;
    @FXML private Label lblTongPhong, lblTongDV, lblPhuThu, lblGiamGia, lblTongThanhToan;
    @FXML private Button btnExit;
    @FXML private TableView<InvoiceRow> tvChiTiet;
    @FXML private TableColumn<InvoiceRow, String> colNoiDung, colThanhTien;

    @FXML
    public void initialize() {
        Others.playButtonAnimation(btnExit);
        tvChiTiet.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colNoiDung.prefWidthProperty().bind(tvChiTiet.widthProperty().multiply(0.65));
        colThanhTien.prefWidthProperty().bind(tvChiTiet.widthProperty().multiply(0.34));
    }

    public void setInvoiceData(HoaDon hd) {
        lblMaHoaDon.setText("Số: HD-" + String.format("%04d", hd.getMaHoaDon()));
        lblNgayThanhToan.setText(Others.formatDateTime(hd.getNgayThanhToan()));

        DatPhong dp = DatPhongBLL.getBookingById(hd.getMaDatPhong());
        if (dp != null) {
            KhachHang kh = KhachHangBLL.getCustomerById(dp.getMaKhachHang());
            if (kh != null) {
                lblTenKhach.setText(kh.getHoTen());
            }
        }

        List<ChiTietDatPhong> rooms = ChiTietDatPhongBLL.getChiTietPhongTheoDoan(hd.getMaDatPhong());
        lblDanhSachPhong.setText(String.join(", ", rooms.stream().map(ChiTietDatPhong::getSoPhong).toList()));

        tvChiTiet.setItems(FXCollections.observableArrayList(
                new InvoiceRow("Tổng tiền lưu trú", hd.getTongTienPhong()),
                new InvoiceRow("Tổng chi phí dịch vụ", hd.getTongTienDichVu())
        ));

        colNoiDung.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name));
        colThanhTien.setCellValueFactory(d -> new SimpleStringProperty(Others.formatPrice(d.getValue().amount)));

        lblTongPhong.setText(Others.formatPrice(hd.getTongTienPhong()));
        lblTongDV.setText(Others.formatPrice(hd.getTongTienDichVu()));
        lblPhuThu.setText(Others.formatPrice(hd.getPhuThu()));
        lblGiamGia.setText(Others.formatPrice(hd.getGiamGia()));
        lblTongThanhToan.setText(Others.formatPrice(hd.getTongThanhToan()));

        if (hd.getPhuThu() == 0) {
            lblPhuThu.getParent().setVisible(false);
            lblPhuThu.getParent().setManaged(false);
        }
    }

    @FXML void handleClose() {
        ((Stage) lblMaHoaDon.getScene().getWindow()).close();
    }

    public static class InvoiceRow {
        String name;
        double amount;
        public InvoiceRow(String n, double a) {
            this.name = n;
            this.amount = a;
        }
    }
}