package DataDAL;

import EntitiesDTO.ChiTietDatPhong;
import Utilities.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChiTietDatPhongDAL {
    public static List<ChiTietDatPhong> getChiTietPhongTheoDoan(int maDatPhong) {
        List<ChiTietDatPhong> list = new ArrayList<>();

        String sql = "SELECT ct.MaChiTiet, ct.MaDatPhong, ct.MaPhong, ct.GiaThucTe, " +
                "p.SoPhong, lp.TenLoaiPhong " +
                "FROM ChiTietDatPhong ct " +
                "JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                "JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " +
                "WHERE ct.MaDatPhong = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maDatPhong);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ChiTietDatPhong ct = new ChiTietDatPhong();

                ct.setMaChiTiet(rs.getInt("MaChiTiet"));
                ct.setMaDatPhong(rs.getInt("MaDatPhong"));
                ct.setMaPhong(rs.getInt("MaPhong"));
                ct.setGiaThucTe(rs.getDouble("GiaThucTe"));
                ct.setSoPhong(rs.getString("SoPhong"));
                ct.setLoaiPhong(rs.getString("TenLoaiPhong"));

                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
