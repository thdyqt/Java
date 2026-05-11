package DataDAL;

import EntitiesDTO.KhachHang;
import Utilities.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class KhachHangDAL {
    public static int findCustomerIdByPhone(String phoneNumber) {
        String sql = "SELECT MaKhachHang FROM KhachHang WHERE SoDienThoai = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MaKhachHang");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int insertCustomer(KhachHang customer) {
        String sql = "INSERT INTO KhachHang (HoTen, CCCD_Passport, SoDienThoai, Email, DiaChi) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getHoTen());
            stmt.setString(2, customer.getCccdPassport());
            stmt.setString(3, customer.getSoDienThoai());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getDiaChi());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}