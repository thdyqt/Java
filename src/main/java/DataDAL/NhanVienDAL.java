package DataDAL;

import EntitiesDTO.NhanVien;
import Utilities.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NhanVienDAL {
    public static NhanVien findStaffByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * from nhanvien WHERE TenDangNhap = ? AND MatKhau = ?";
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
                            rs.getString("SoDienThoai")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
