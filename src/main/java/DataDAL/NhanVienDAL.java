package DataDAL;

import EntitiesDTO.NhanVien;
import Utilities.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAL {
    public static NhanVien findActiveStaffByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM NhanVien WHERE TenDangNhap = ? AND MatKhau = ? AND TrangThai = 'Đang làm việc'";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new NhanVien(
                            rs.getInt("MaNhanVien"),
                            rs.getString("HoTen"),
                            rs.getString("ChucVu"),
                            rs.getString("TenDangNhap"),
                            rs.getString("MatKhau"),
                            rs.getString("SoDienThoai"),
                            NhanVien.TrangThaiNhanVien.fromString(rs.getString("TrangThai"))
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<NhanVien> getAllNhanVien() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY MaNhanVien ASC";
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new NhanVien(
                        rs.getInt("MaNhanVien"),
                        rs.getString("HoTen"),
                        rs.getString("ChucVu"),
                        rs.getString("TenDangNhap"),
                        rs.getString("MatKhau"),
                        rs.getString("SoDienThoai"),
                        NhanVien.TrangThaiNhanVien.fromString(rs.getString("TrangThai"))
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static boolean checkUsernameExists(String username, int excludeId) {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE TenDangNhap = ? AND MaNhanVien != ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, excludeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean checkPhoneExists(String phone, int excludeId) {
        if (phone == null || phone.trim().isEmpty()) return false;

        String sql = "SELECT COUNT(*) FROM NhanVien WHERE SoDienThoai = ? AND MaNhanVien != ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            stmt.setInt(2, excludeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean insertNhanVien(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (HoTen, ChucVu, TenDangNhap, MatKhau, SoDienThoai, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nv.getHoTen());
            stmt.setString(2, nv.getChucVu());
            stmt.setString(3, nv.getTenDangNhap());
            stmt.setString(4, nv.getMatKhau());
            stmt.setString(5, nv.getSoDienThoai());
            stmt.setString(6, nv.getTrangThai().getText());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean updateNhanVien(NhanVien nv) {
        String sql = "UPDATE NhanVien SET HoTen = ?, ChucVu = ?, TenDangNhap = ?, SoDienThoai = ?, TrangThai = ? WHERE MaNhanVien = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nv.getHoTen());
            stmt.setString(2, nv.getChucVu());
            stmt.setString(3, nv.getTenDangNhap());
            stmt.setString(4, nv.getSoDienThoai());
            stmt.setString(5, nv.getTrangThai().getText());
            stmt.setInt(6, nv.getMaNhanVien());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean updatePassword(int maNhanVien, String newPassword) {
        String sql = "UPDATE NhanVien SET MatKhau = ? WHERE MaNhanVien = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, maNhanVien);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean stopNhanVien(int maNhanVien) {
        String sql = "UPDATE NhanVien SET TrangThai = 'Đã nghỉ việc' WHERE MaNhanVien = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maNhanVien);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean restoreNhanVien(int maNhanVien) {
        String sql = "UPDATE NhanVien SET TrangThai = 'Đang làm việc' WHERE MaNhanVien = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maNhanVien);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
