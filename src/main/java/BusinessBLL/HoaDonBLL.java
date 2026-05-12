package BusinessBLL;

import DataDAL.HoaDonDAL;
import EntitiesDTO.ChiTietDatPhong;
import EntitiesDTO.HoaDon;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HoaDonBLL {
    public static int xuLyThanhToan(int maDatPhong, int maNhanVien, double tienPhong,
                                    double tienDV, double phuThu, double giamGia,
                                    double tienCoc, double tongThanhToan, String phuongThuc) {

        HoaDon hd = new HoaDon();
        hd.setMaDatPhong(maDatPhong);
        hd.setMaNhanVien(maNhanVien);
        hd.setNgayThanhToan(LocalDateTime.now());
        hd.setTongTienPhong(tienPhong);
        hd.setTongTienDichVu(tienDV);
        hd.setPhuThu(phuThu);
        hd.setGiamGia(giamGia);
        hd.setTongThanhToan(tongThanhToan);
        hd.setPhuongThucThanhToan(phuongThuc);

        return HoaDonDAL.saveInvoiceTransaction(hd);
    }

    public static HoaDon getInvoiceById(int maHoaDon) {
        return HoaDonDAL.getInvoiceById(maHoaDon);
    }

    public static void tinhTienPhongThucTe(LocalDateTime thoiGianVao, LocalDateTime thoiGianRa, List<ChiTietDatPhong> danhSachPhong) {
        if (thoiGianVao == null || thoiGianRa == null || danhSachPhong == null) {
            return;
        }

        long soGioO = Duration.between(thoiGianVao, thoiGianRa).toHours();
        if (soGioO < 1) {
            soGioO = 1;
        }

        for (ChiTietDatPhong phong : danhSachPhong) {
            double tienPhong = 0;
            double donGiaGoc = phong.getGiaThucTe();

            if (soGioO <= 4) {
                // Khách thuê theo giờ (Dưới 4 tiếng)
                // Giờ đầu tiên tính 30% giá phòng, các giờ tiếp theo tính 15%
                tienPhong = donGiaGoc * 0.3 + (soGioO - 1) * (donGiaGoc * 0.15);
            } else {
                // Khách thuê qua đêm / dài ngày (Trên 4 tiếng)
                long soNgay = soGioO / 24;
                long gioLe = soGioO % 24;

                tienPhong = soNgay * donGiaGoc;

                // Xử lý phụ thu giờ lẻ dôi ra
                if (gioLe > 0 && gioLe <= 6) {
                    tienPhong += donGiaGoc * 0.5; // Lố dưới 6 tiếng: Phụ thu 50% tiền phòng
                } else if (gioLe > 6) {
                    tienPhong += donGiaGoc;       // Lố trên 6 tiếng: Phụ thu thành 1 ngày trọn vẹn
                }
            }

            phong.setTienPhongThucTe(tienPhong);
        }
    }
}
