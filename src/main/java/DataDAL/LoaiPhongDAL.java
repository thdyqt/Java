package DataDAL;

import EntitiesDTO.LoaiPhong;
import Utilities.DBHelper;

import java.sql.Connection;
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
}
