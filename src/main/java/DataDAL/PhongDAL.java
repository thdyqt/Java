package DataDAL;

import EntitiesDTO.Phong;
import Utilities.DBHelper;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhongDAL {
    public static class PhongViewModel {
        public int maPhong;
        public String soPhong;
        public String tenLoaiPhong;
        public double donGia;
        public int soNguoiToiDa;
        public String trangThai;

        public PhongViewModel(int maPhong, String soPhong, String tenLoaiPhong, double donGia, int soNguoiToiDa, String trangThai) {
            this.maPhong = maPhong; this.soPhong = soPhong; this.tenLoaiPhong = tenLoaiPhong;
            this.donGia = donGia; this.soNguoiToiDa = soNguoiToiDa; this.trangThai = trangThai;
        }
    }

    public static List<PhongViewModel> getDanhSachPhongFull() {
        List<PhongViewModel> list = new ArrayList<>();
        String sql = "SELECT p.MaPhong, p.SoPhong, lp.TenLoaiPhong, lp.DonGia, lp.SoNguoiToiDa, p.TrangThai " +
                "FROM Phong p " +
                "LEFT JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " + // CHUYỂN THÀNH LEFT JOIN
                "ORDER BY p.SoPhong ASC";

        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new PhongViewModel(
                        rs.getInt("MaPhong"),
                        rs.getString("SoPhong"),
                        rs.getString("TenLoaiPhong"),
                        rs.getDouble("DonGia"),
                        rs.getInt("SoNguoiToiDa"),
                        rs.getString("TrangThai")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static List<Phong> getAllRooms() {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT * FROM Phong";

        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(sql)){
                while (rs.next()) {
                    list.add(new Phong(
                      rs.getInt("MaPhong"),
                      rs.getString("SoPhong"),
                      rs.getInt("MaLoaiPhong"),
                      rs.getString("TrangThai")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static boolean isSoPhongExists(String soPhong, int excludeId) {
        String sql = "SELECT COUNT(*) FROM Phong WHERE SoPhong = ? AND MaPhong != ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, soPhong);
            stmt.setInt(2, excludeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean insertPhong(Phong p) {
        String sql = "INSERT INTO Phong (SoPhong, MaLoaiPhong, TrangThai) VALUES (?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getSoPhong());
            stmt.setInt(2, p.getMaLoaiPhong());
            stmt.setString(3, p.getTrangThai());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean updatePhong(Phong p) {
        String sql = "UPDATE Phong SET SoPhong = ?, MaLoaiPhong = ?, TrangThai = ? WHERE MaPhong = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getSoPhong());
            stmt.setInt(2, p.getMaLoaiPhong());
            stmt.setString(3, p.getTrangThai());
            stmt.setInt(4, p.getMaPhong());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean updateRoomStatus(String roomNumber, String newStatus) {
        String sql = "UPDATE Phong SET TrangThai = ? WHERE SoPhong = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setString(2, roomNumber);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
