package DataDAL;

import EntitiesDTO.Phong;
import Utilities.DBHelper;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhongDAL {
    public static List<Phong> getAllRooms() {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT * FROM phong";

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
