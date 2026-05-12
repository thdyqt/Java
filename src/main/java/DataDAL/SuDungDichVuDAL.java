package DataDAL;

import EntitiesDTO.SuDungDichVu;
import Utilities.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SuDungDichVuDAL {
    public static List<SuDungDichVu> getServiceByBookingId(int bookingId) {
        List<SuDungDichVu> list = new ArrayList<>();

        String sql = "SELECT sd.MaSuDung, sd.MaDatPhong, sd.MaDichVu, sd.SoLuong, sd.ThoiGianSuDung, sd.ThanhTien, dv.TenDichVu " +
                "FROM SuDungDichVu sd " +
                "JOIN DichVu dv ON sd.MaDichVu = dv.MaDichVu " +
                "WHERE sd.MaDatPhong = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                SuDungDichVu dv = new SuDungDichVu();

                dv.setMaSuDung(rs.getInt("MaSuDung"));
                dv.setMaDatPhong(rs.getInt("MaDatPhong"));
                dv.setMaDichVu(rs.getInt("MaDichVu"));
                dv.setSoLuong(rs.getInt("SoLuong"));
                if (rs.getTimestamp("ThoiGianSuDung") != null) {
                    dv.setThoiGianSuDung(rs.getTimestamp("ThoiGianSuDung").toLocalDateTime());
                }

                dv.setThanhTien(rs.getDouble("ThanhTien"));
                dv.setTenDichVu(rs.getString("TenDichVu"));

                list.add(dv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean insertService(SuDungDichVu sd) {
        String sql = "INSERT INTO SuDungDichVu (MaDatPhong, MaDichVu, SoLuong, ThanhTien) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sd.getMaDatPhong());
            stmt.setInt(2, sd.getMaDichVu());
            stmt.setInt(3, sd.getSoLuong());
            stmt.setDouble(4, sd.getThanhTien());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteService(int maSuDung) {
        String sql = "DELETE FROM SuDungDichVu WHERE MaSuDung = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maSuDung);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}