package BusinessBLL;

import DataDAL.HoaDonDAL;
import EntitiesDTO.ChiTietDatPhong;
import EntitiesDTO.HoaDon;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class HoaDonBLL {
    public static List<HoaDonDAL.HoaDonViewModel> getDanhSachHoaDon() {
        return HoaDonDAL.getDanhSachHoaDon();
    }

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

    // Đổi kiểu trả về thành mảng double: result[0] = Phụ Thu, result[1] = Giảm Giá
    public static double[] tinhTienPhongThucTe(LocalDateTime thoiGianVao, LocalDateTime thoiGianRaDuKien, LocalDateTime thoiGianRaThucTe, List<ChiTietDatPhong> danhSachPhong) {
        if (thoiGianVao == null || thoiGianRaDuKien == null || thoiGianRaThucTe == null || danhSachPhong == null) {
            return new double[]{0, 0};
        }

        boolean isTheoNgay = (thoiGianRaDuKien.getHour() == 12 && thoiGianRaDuKien.getMinute() == 0);
        boolean isQuaDem = (thoiGianRaDuKien.getHour() == 8 && thoiGianRaDuKien.getMinute() == 0);
        boolean isTheoGio = !isTheoNgay && !isQuaDem;

        double tongPhuThu = 0;
        double tongGiamGia = 0;

        for (ChiTietDatPhong phong : danhSachPhong) {
            double donGiaGoc = phong.getGiaThucTe();
            double tienPhongGoc = 0;

            // 1. TÍNH TIỀN PHÒNG GỐC THEO DỰ KIẾN (Để hiển thị cứng trên Bill)
            if (isTheoGio) {
                long expectedMins = Duration.between(thoiGianVao, thoiGianRaDuKien).toMinutes();
                if (expectedMins < 60) expectedMins = 60;
                long expectedHours = (long) Math.ceil(expectedMins / 60.0);
                tienPhongGoc = donGiaGoc * (expectedHours * 0.15);
            } else {
                double heSo = 1.0;
                if (isTheoNgay) {
                    long days = ChronoUnit.DAYS.between(thoiGianVao.toLocalDate(), thoiGianRaDuKien.toLocalDate());
                    heSo = days > 0 ? days : 1;
                } else if (isQuaDem) {
                    heSo = 0.8;
                }
                tienPhongGoc = donGiaGoc * heSo;
            }

            // Luôn khóa cứng giá tiền phòng hiển thị là giá lúc đặt
            phong.setTienPhongThucTe(Math.round(tienPhongGoc));

            // 2. TÍNH PHỤ THU (Khách trả trễ giờ - Áp dụng mọi hình thức)
            if (thoiGianRaThucTe.isAfter(thoiGianRaDuKien)) {
                long lateMinutes = Duration.between(thoiGianRaDuKien, thoiGianRaThucTe).toMinutes();
                long lateHours = (long) Math.ceil(lateMinutes / 60.0);
                tongPhuThu += (donGiaGoc * 0.10) * lateHours; // Phạt 10%/giờ
            }

            // 3. TÍNH GIẢM GIÁ (Khách trả sớm - Chỉ áp dụng linh động cho Theo Giờ)
            if (isTheoGio && thoiGianRaThucTe.isBefore(thoiGianRaDuKien)) {
                long actualMins = Duration.between(thoiGianVao, thoiGianRaThucTe).toMinutes();
                if (actualMins < 60) actualMins = 60; // Tính tối thiểu 1 giờ
                long actualHours = (long) Math.ceil(actualMins / 60.0);
                double tienThucTeHienTai = donGiaGoc * (actualHours * 0.15);

                // Độ chênh lệch giữa tiền khách đã đặt và tiền thực tế ở -> Cho vào mục Giảm Giá
                double chenhLech = tienPhongGoc - tienThucTeHienTai;
                if (chenhLech > 0) {
                    tongGiamGia += chenhLech;
                }
            }
        }

        return new double[]{Math.round(tongPhuThu), Math.round(tongGiamGia)};
    }
}