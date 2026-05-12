package DataDAL;

import EntitiesDTO.KhachHang;
import Utilities.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class KhachHangDAL {
    public static KhachHang getCustomerById(int maKhachHang) {
        String sql = "SELECT * FROM KhachHang WHERE MaKhachHang = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maKhachHang);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKhachHang(rs.getInt("MaKhachHang"));
                kh.setHoTen(rs.getString("HoTen"));
                kh.setCccdPassport(rs.getString("CCCD_Passport"));
                kh.setSoDienThoai(rs.getString("SoDienThoai"));
                kh.setEmail(rs.getString("Email"));
                kh.setDiaChi(rs.getString("DiaChi"));

                return kh;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static KhachHang getCustomerByCCCD(String cccd) {
        String sql = "SELECT * FROM KhachHang WHERE CCCD_Passport = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cccd);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKhachHang(rs.getInt("MaKhachHang"));
                kh.setHoTen(rs.getString("HoTen"));
                kh.setCccdPassport(rs.getString("CCCD_Passport"));
                kh.setSoDienThoai(rs.getString("SoDienThoai"));
                kh.setEmail(rs.getString("Email"));
                kh.setDiaChi(rs.getString("DiaChi"));
                return kh;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public static boolean updateCustomer(KhachHang customer) {
        String sql = "UPDATE KhachHang SET HoTen = ?, SoDienThoai = ?, Email = ?, DiaChi = ? WHERE MaKhachHang = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getHoTen());
            stmt.setString(2, customer.getSoDienThoai());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getDiaChi());
            stmt.setInt(5, customer.getMaKhachHang());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}