package DataDAL;

import EntitiesDTO.DatPhong;
import Utilities.DBHelper;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatPhongDAL {
    public static DatPhong getBookingById(int maDatPhong) {
        String sql = "SELECT * FROM DatPhong WHERE MaDatPhong = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maDatPhong);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                DatPhong dp = new DatPhong();
                dp.setMaDatPhong(rs.getInt("MaDatPhong"));
                dp.setMaKhachHang(rs.getInt("MaKhachHang"));
                dp.setMaNhanVien(rs.getInt("MaNhanVien"));
                if (rs.getTimestamp("NgayDat") != null) {
                    dp.setNgayDat(rs.getTimestamp("NgayDat").toLocalDateTime());
                }
                if (rs.getTimestamp("NgayCheckInDuKien") != null) {
                    dp.setNgayCheckInDuKien(rs.getTimestamp("NgayCheckInDuKien").toLocalDateTime());
                }
                if (rs.getTimestamp("NgayCheckOutDuKien") != null) {
                    dp.setNgayCheckOutDuKien(rs.getTimestamp("NgayCheckOutDuKien").toLocalDateTime());
                }

                dp.setTienCoc(rs.getDouble("TienCoc"));
                dp.setTrangThai(rs.getString("TrangThai"));

                return dp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getActiveBookingIdByRoomId(int maPhong) {
        String sql = "SELECT dp.MaDatPhong FROM DatPhong dp " +
                "JOIN ChiTietDatPhong ct ON dp.MaDatPhong = ct.MaDatPhong " +
                "WHERE ct.MaPhong = ? AND dp.TrangThai = 'Đang ở'";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maPhong);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("MaDatPhong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<Map<String, Object>> getLichSuDatPhong(String soPhong) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT kh.HoTen, dp.NgayCheckInDuKien, dp.NgayCheckOutDuKien, dp.TrangThai " +
                "FROM DatPhong dp " +
                "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                "JOIN ChiTietDatPhong ct ON dp.MaDatPhong = ct.MaDatPhong " +
                "JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                "WHERE p.SoPhong = ? " +
                "ORDER BY dp.NgayCheckInDuKien DESC";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, soPhong);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("HoTen", rs.getString("HoTen"));
                row.put("NgayIn", rs.getTimestamp("NgayCheckInDuKien").toLocalDateTime());
                row.put("NgayOut", rs.getTimestamp("NgayCheckOutDuKien").toLocalDateTime());
                row.put("TrangThai", rs.getString("TrangThai"));
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean checkDateConflict(String roomNumber, LocalDate checkInDate, LocalDate checkOutDate) {
        String sql = "SELECT COUNT(*) AS SoLuong FROM DatPhong dp " +
                "JOIN ChiTietDatPhong ct ON dp.MaDatPhong = ct.MaDatPhong " +
                "JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                "WHERE p.SoPhong = ? " +
                "AND dp.TrangThai = 'Chờ nhận phòng' " +
                "AND dp.NgayCheckInDuKien < ? " +
                "AND dp.NgayCheckOutDuKien > ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roomNumber);
            stmt.setDate(2, java.sql.Date.valueOf(checkOutDate));
            stmt.setDate(3, java.sql.Date.valueOf(checkInDate));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("SoLuong") > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean insertCheckInTransaction(DatPhong booking, List<String> roomList) {
        String sqlInsertBooking = "INSERT INTO DatPhong (MaKhachHang, MaNhanVien, NgayDat, NgayCheckInDuKien, NgayCheckOutDuKien, TienCoc, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlInsertDetail = "INSERT INTO ChiTietDatPhong (MaDatPhong, MaPhong, GiaThucTe) " +
                "SELECT ?, p.MaPhong, lp.DonGia " +
                "FROM Phong p JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " +
                "WHERE p.SoPhong = ?";

        try (Connection conn = DBHelper.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtBooking = conn.prepareStatement(sqlInsertBooking, Statement.RETURN_GENERATED_KEYS)) {

                stmtBooking.setInt(1, booking.getMaKhachHang());
                stmtBooking.setInt(2, booking.getMaNhanVien());
                stmtBooking.setTimestamp(3, Timestamp.valueOf(booking.getNgayDat() != null ? booking.getNgayDat() : java.time.LocalDateTime.now()));
                stmtBooking.setTimestamp(4, Timestamp.valueOf(booking.getNgayCheckInDuKien()));
                stmtBooking.setTimestamp(5, Timestamp.valueOf(booking.getNgayCheckOutDuKien()));
                stmtBooking.setDouble(6, booking.getTienCoc());
                stmtBooking.setString(7, booking.getTrangThai());

                stmtBooking.executeUpdate();
                ResultSet rs = stmtBooking.getGeneratedKeys();
                int newBookingId = -1;
                if (rs.next()) {
                    newBookingId = rs.getInt(1);
                }

                if (newBookingId != -1) {
                    try (PreparedStatement stmtDetail = conn.prepareStatement(sqlInsertDetail)) {
                        for (String roomNumber : roomList) {
                            stmtDetail.setInt(1, newBookingId);
                            stmtDetail.setString(2, roomNumber);
                            stmtDetail.addBatch();
                        }
                        stmtDetail.executeBatch();
                    }
                }

                conn.commit();
                return true;

            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}