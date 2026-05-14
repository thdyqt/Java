package DataDAL;

import EntitiesDTO.HoaDon;
import Utilities.DBHelper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class HoaDonDAL {
    public static class HoaDonViewModel {
        public int maHoaDon;
        public String tenKhachHang;
        public String soDienThoai;
        public LocalDateTime ngayThanhToan;
        public double tongThanhToan;
        public String phuongThuc;

        public HoaDonViewModel(int maHoaDon, String tenKhachHang, String soDienThoai, LocalDateTime ngayThanhToan, double tongThanhToan, String phuongThuc) {
            this.maHoaDon = maHoaDon;
            this.tenKhachHang = tenKhachHang;
            this.soDienThoai = soDienThoai;
            this.ngayThanhToan = ngayThanhToan;
            this.tongThanhToan = tongThanhToan;
            this.phuongThuc = phuongThuc;
        }
    }

    public static List<HoaDonViewModel> getDanhSachHoaDon() {
        List<HoaDonViewModel> list = new java.util.ArrayList<>();
        String sql = "SELECT hd.MaHoaDon, kh.HoTen, kh.SoDienThoai, hd.NgayThanhToan, hd.TongThanhToan, hd.PhuongThucThanhToan " +
                "FROM HoaDon hd " +
                "JOIN DatPhong dp ON hd.MaDatPhong = dp.MaDatPhong " +
                "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                "ORDER BY hd.NgayThanhToan DESC";
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new HoaDonViewModel(
                        rs.getInt("MaHoaDon"),
                        rs.getString("HoTen"),
                        rs.getString("SoDienThoai"),
                        rs.getTimestamp("NgayThanhToan").toLocalDateTime(),
                        rs.getDouble("TongThanhToan"),
                        rs.getString("PhuongThucThanhToan")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static int saveInvoiceTransaction(HoaDon hd) {
        String sqlHD = "INSERT INTO HoaDon (MaDatPhong, MaNhanVien, NgayThanhToan, TongTienPhong, " +
                "TongTienDichVu, PhuThu, GiamGia, TongThanhToan, PhuongThucThanhToan) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlDP = "UPDATE DatPhong SET TrangThai = 'Đã trả phòng' WHERE MaDatPhong = ?";
        String sqlPhong = "UPDATE Phong SET TrangThai = 'Đang dọn dẹp' WHERE MaPhong IN (" +
                "SELECT MaPhong FROM ChiTietDatPhong WHERE MaDatPhong = ?)";

        try (Connection conn = DBHelper.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int newInvoiceId = -1;

                try (PreparedStatement psHD = conn.prepareStatement(sqlHD, Statement.RETURN_GENERATED_KEYS)) {
                    psHD.setInt(1, hd.getMaDatPhong());
                    psHD.setInt(2, hd.getMaNhanVien());
                    psHD.setTimestamp(3, Timestamp.valueOf(hd.getNgayThanhToan()));
                    psHD.setDouble(4, hd.getTongTienPhong());
                    psHD.setDouble(5, hd.getTongTienDichVu());
                    psHD.setDouble(6, hd.getPhuThu());
                    psHD.setDouble(7, hd.getGiamGia());
                    psHD.setDouble(8, hd.getTongThanhToan());
                    psHD.setString(9, hd.getPhuongThucThanhToan());
                    psHD.executeUpdate();

                    ResultSet rs = psHD.getGeneratedKeys();
                    if (rs.next()) {
                        newInvoiceId = rs.getInt(1);
                    }
                }

                try (PreparedStatement psDP = conn.prepareStatement(sqlDP)) {
                    psDP.setInt(1, hd.getMaDatPhong());
                    psDP.executeUpdate();
                }

                try (PreparedStatement psP = conn.prepareStatement(sqlPhong)) {
                    psP.setInt(1, hd.getMaDatPhong());
                    psP.executeUpdate();
                }

                conn.commit();
                return newInvoiceId;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static HoaDon getInvoiceById(int maHoaDon) {
        String sql = "SELECT * FROM HoaDon WHERE MaHoaDon = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maHoaDon);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(rs.getInt("MaHoaDon"));
                hd.setMaDatPhong(rs.getInt("MaDatPhong"));
                hd.setMaNhanVien(rs.getInt("MaNhanVien"));
                hd.setNgayThanhToan(rs.getTimestamp("NgayThanhToan").toLocalDateTime());
                hd.setTongTienPhong(rs.getDouble("TongTienPhong"));
                hd.setTongTienDichVu(rs.getDouble("TongTienDichVu"));
                hd.setPhuThu(rs.getDouble("PhuThu"));
                hd.setGiamGia(rs.getDouble("GiamGia"));
                hd.setTongThanhToan(rs.getDouble("TongThanhToan"));
                hd.setPhuongThucThanhToan(rs.getString("PhuongThucThanhToan"));
                return hd;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
