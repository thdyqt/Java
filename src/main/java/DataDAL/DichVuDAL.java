package DataDAL;

import EntitiesDTO.DichVu;
import Utilities.DBHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DichVuDAL {
    public static List<DichVu> getAllDichVu() {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu";
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new DichVu(rs.getInt("MaDichVu"), rs.getString("TenDichVu"), rs.getDouble("DonGia")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
