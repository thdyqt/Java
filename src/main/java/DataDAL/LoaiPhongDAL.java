package DataDAL;

import EntitiesDTO.LoaiPhong;
import Utilities.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LoaiPhongDAL {
    public static List<LoaiPhong> getAllLoaiPhong() {
        List<LoaiPhong> list = new ArrayList<>();
        String sql = "SELECT * FROM LoaiPhong";
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new LoaiPhong(rs.getInt("MaLoaiPhong"), rs.getString("TenLoaiPhong"),
                        rs.getDouble("DonGia"), rs.getInt("SoNguoiToiDa"), rs.getString("MoTa")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static boolean isTenLoaiPhongExists(String tenLoaiPhong, int excludeId) {
        String sql = "SELECT COUNT(*) FROM LoaiPhong WHERE TenLoaiPhong = ? AND MaLoaiPhong != ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenLoaiPhong);
            stmt.setInt(2, excludeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean insertLoaiPhong(LoaiPhong lp) {
        String sql = "INSERT INTO LoaiPhong (TenLoaiPhong, DonGia, SoNguoiToiDa, MoTa) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lp.getTenLoaiPhong());
            stmt.setDouble(2, lp.getDonGia());
            stmt.setInt(3, lp.getSoNguoiToiDa());
            stmt.setString(4, lp.getMoTa());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean updateLoaiPhong(LoaiPhong lp) {
        String sql = "UPDATE LoaiPhong SET TenLoaiPhong = ?, DonGia = ?, SoNguoiToiDa = ?, MoTa = ? WHERE MaLoaiPhong = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lp.getTenLoaiPhong());
            stmt.setDouble(2, lp.getDonGia());
            stmt.setInt(3, lp.getSoNguoiToiDa());
            stmt.setString(4, lp.getMoTa());
            stmt.setInt(5, lp.getMaLoaiPhong());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
