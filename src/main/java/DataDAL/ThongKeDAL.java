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
        String sql = "SELECT SUM(TongThanhToan) as Tong, SUM(TongTienPhong) as TienPhong, " +
                "SUM(TongTienDichVu) as TienDV, COUNT(MaHoaDon) as SoLuong " +
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
                result[3] = rs.getDouble("SoLuong"); // Cast double để dùng chung mảng
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
}