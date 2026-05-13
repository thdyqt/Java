package DataDAL;

import EntitiesDTO.DichVu;
import Utilities.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DichVuDAL {
    public static List<DichVu> getAllDichVu() {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu ORDER BY MaDichVu DESC";
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new DichVu(
                        rs.getInt("MaDichVu"),
                        rs.getString("TenDichVu"),
                        rs.getDouble("DonGia"),
                        DichVu.TrangThaiDichVu.fromString(rs.getString("TrangThai")) // Ép kiểu SQL sang Enum
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static List<DichVu> getActiveDichVu() {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu WHERE TrangThai = 'Đang bán' ORDER BY TenDichVu ASC";
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new DichVu(
                        rs.getInt("MaDichVu"),
                        rs.getString("TenDichVu"),
                        rs.getDouble("DonGia"),
                        DichVu.TrangThaiDichVu.DANG_BAN
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static boolean insertDichVu(DichVu dv) {
        String sql = "INSERT INTO DichVu (TenDichVu, DonGia, TrangThai) VALUES (?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dv.getTenDichVu());
            stmt.setDouble(2, dv.getDonGia());
            stmt.setString(3, dv.getTrangThai().getText()); // Lấy chuỗi từ Enum đưa xuống SQL
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateDichVu(DichVu dv) {
        String sql = "UPDATE DichVu SET TenDichVu = ?, DonGia = ?, TrangThai = ? WHERE MaDichVu = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dv.getTenDichVu());
            stmt.setDouble(2, dv.getDonGia());
            stmt.setString(3, dv.getTrangThai().getText());
            stmt.setInt(4, dv.getMaDichVu());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean stopDichVu(int maDichVu) {
        String sql = "UPDATE DichVu SET TrangThai = 'Ngừng kinh doanh' WHERE MaDichVu = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maDichVu);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean restoreDichVu(int maDichVu) {
        String sql = "UPDATE DichVu SET TrangThai = 'Đang bán' WHERE MaDichVu = ?";
        try (Connection conn = DBHelper.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maDichVu);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}