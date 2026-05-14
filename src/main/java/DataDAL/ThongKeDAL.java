package DataDAL;

import Utilities.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThongKeDAL {
    public static double[] getTongQuan(LocalDate tuNgay, LocalDate denNgay) {
        double[] result = new double[4];

        String sql = "SELECT SUM(TongTienPhong + TongTienDichVu + PhuThu - GiamGia) as Tong, " +
                "SUM(TongTienPhong) as TienPhong, " +
                "SUM(TongTienDichVu) as TienDV, " +
                "COUNT(MaHoaDon) as SoLuong " +
                "FROM HoaDon WHERE DATE(NgayThanhToan) >= ? AND DATE(NgayThanhToan) <= ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result[0] = rs.getDouble("Tong");
                result[1] = rs.getDouble("TienPhong");
                result[2] = rs.getDouble("TienDV");
                result[3] = rs.getDouble("SoLuong");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public static Map<String, Double> getDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql = "SELECT DATE(NgayThanhToan) as Ngay, SUM(TongThanhToan) as DoanhThu " +
                "FROM HoaDon WHERE DATE(NgayThanhToan) >= ? AND DATE(NgayThanhToan) <= ? " +
                "GROUP BY DATE(NgayThanhToan) ORDER BY Ngay ASC";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("Ngay"), rs.getDouble("DoanhThu"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    public static Map<String, Integer> getThongKeLoaiPhong(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT lp.TenLoaiPhong, COUNT(ctdp.MaPhong) AS SoLuotDat " +
                "FROM HoaDon hd " +
                "JOIN DatPhong dp ON hd.MaDatPhong = dp.MaDatPhong " +
                "JOIN ChiTietDatPhong ctdp ON dp.MaDatPhong = ctdp.MaDatPhong " +
                "JOIN Phong p ON ctdp.MaPhong = p.MaPhong " +
                "JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " +
                "WHERE DATE(hd.NgayThanhToan) >= ? AND DATE(hd.NgayThanhToan) <= ? " +
                "GROUP BY lp.TenLoaiPhong " +
                "ORDER BY SoLuotDat DESC LIMIT 5";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("TenLoaiPhong"), rs.getInt("SoLuotDat"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    public static Map<String, Integer> getThongKeDichVu(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT dv.TenDichVu, SUM(sddv.SoLuong) AS TongSoLuong " +
                "FROM HoaDon hd " +
                "JOIN DatPhong dp ON hd.MaDatPhong = dp.MaDatPhong " +
                "JOIN SuDungDichVu sddv ON dp.MaDatPhong = sddv.MaDatPhong " +
                "JOIN DichVu dv ON sddv.MaDichVu = dv.MaDichVu " +
                "WHERE DATE(hd.NgayThanhToan) >= ? AND DATE(hd.NgayThanhToan) <= ? " +
                "GROUP BY dv.TenDichVu " +
                "ORDER BY TongSoLuong DESC LIMIT 5";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("TenDichVu"), rs.getInt("TongSoLuong"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }
}