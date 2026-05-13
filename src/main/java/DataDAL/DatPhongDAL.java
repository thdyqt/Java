package DataDAL;

import EntitiesDTO.DatPhong;
import Utilities.DBHelper;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public static List<Map<String, Object>> getAllBookingsWithDetails() {
        List<Map<String, Object>> list = new ArrayList<>();

        String sql = "SELECT dp.MaDatPhong, kh.HoTen, kh.SoDienThoai, " +
                "dp.NgayCheckInDuKien, dp.NgayCheckOutDuKien, dp.TienCoc, dp.TrangThai, " +
                "GROUP_CONCAT(p.SoPhong SEPARATOR ', ') AS DanhSachPhong " +
                "FROM DatPhong dp " +
                "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                "LEFT JOIN ChiTietDatPhong ct ON dp.MaDatPhong = ct.MaDatPhong " +
                "LEFT JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                "GROUP BY dp.MaDatPhong, kh.HoTen, kh.SoDienThoai, dp.NgayCheckInDuKien, dp.NgayCheckOutDuKien, dp.TienCoc, dp.TrangThai " +
                "ORDER BY dp.MaDatPhong DESC";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("MaDatPhong", rs.getInt("MaDatPhong"));
                row.put("HoTen", rs.getString("HoTen"));
                row.put("SoDienThoai", rs.getString("SoDienThoai"));
                row.put("NgayIn", rs.getTimestamp("NgayCheckInDuKien").toLocalDateTime());
                row.put("NgayOut", rs.getTimestamp("NgayCheckOutDuKien").toLocalDateTime());
                row.put("TienCoc", rs.getDouble("TienCoc"));
                row.put("TrangThai", rs.getString("TrangThai"));
                row.put("DanhSachPhong", rs.getString("DanhSachPhong"));
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Map<String, Object> getBookingFullInfo(int maDatPhong) {
        Map<String, Object> result = new java.util.HashMap<>();
        String sql = "SELECT dp.*, kh.HoTen, kh.SoDienThoai, kh.CCCD_Passport, kh.Email, kh.DiaChi, " +
                "(SELECT GROUP_CONCAT(p.SoPhong SEPARATOR ', ') FROM ChiTietDatPhong ct JOIN Phong p ON ct.MaPhong = p.MaPhong WHERE ct.MaDatPhong = dp.MaDatPhong) AS DanhSachPhong " +
                "FROM DatPhong dp JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                "WHERE dp.MaDatPhong = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maDatPhong);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result.put("HoTen", rs.getString("HoTen"));
                result.put("SoDienThoai", rs.getString("SoDienThoai"));
                result.put("CCCD", rs.getString("CCCD_Passport")); // Vẫn giữ Key là CCCD để Controller dùng không bị lỗi
                result.put("Email", rs.getString("Email"));
                result.put("DiaChi", rs.getString("DiaChi"));
                result.put("NgayIn", rs.getTimestamp("NgayCheckInDuKien").toLocalDateTime());
                result.put("NgayOut", rs.getTimestamp("NgayCheckOutDuKien").toLocalDateTime());
                result.put("TienCoc", rs.getDouble("TienCoc"));
                result.put("DanhSachPhong", rs.getString("DanhSachPhong"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public static boolean checkDateConflict(String roomNumber, LocalDate checkInDate, LocalDate checkOutDate, int excludeMaDatPhong) {
        String sql = "SELECT COUNT(*) AS SoLuong FROM DatPhong dp " +
                "JOIN ChiTietDatPhong ct ON dp.MaDatPhong = ct.MaDatPhong " +
                "JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                "WHERE p.SoPhong = ? " +
                "AND dp.TrangThai IN ('Chờ nhận phòng', 'Đang ở') " + // Xét cả khách đang ở
                "AND dp.MaDatPhong != ? " + // BỎ QUA CHÍNH ĐƠN ĐANG SỬA
                "AND dp.NgayCheckInDuKien < ? " +
                "AND dp.NgayCheckOutDuKien > ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roomNumber);
            stmt.setInt(2, excludeMaDatPhong); 
            stmt.setDate(3, java.sql.Date.valueOf(checkOutDate));
            stmt.setDate(4, java.sql.Date.valueOf(checkInDate));

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

    public static boolean quickCheckIn(int maDatPhong) {
        String sql = "UPDATE DatPhong SET TrangThai = 'Đang ở', NgayCheckInDuKien = ? WHERE MaDatPhong = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, maDatPhong);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean changeStatus(int maDatPhong, String newStatus) {
        String sql = "UPDATE DatPhong SET TrangThai = ? WHERE MaDatPhong = ?";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, maDatPhong);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateBookingTransaction(int maDatPhong, String hoTen, String sdt, String cccd, String email, String diaChi, List<String> roomNumbers, LocalDateTime checkIn, LocalDateTime checkOut, double tienCoc) {
        java.sql.Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);

            String updateKh = "UPDATE KhachHang SET HoTen=?, SoDienThoai=?, CCCD_Passport=?, Email=?, DiaChi=? WHERE MaKhachHang = (SELECT MaKhachHang FROM DatPhong WHERE MaDatPhong = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(updateKh)) {
                stmt.setString(1, hoTen); stmt.setString(2, sdt); stmt.setString(3, cccd);
                stmt.setString(4, email); stmt.setString(5, diaChi); stmt.setInt(6, maDatPhong);
                stmt.executeUpdate();
            }

            String updateDp = "UPDATE DatPhong SET NgayCheckInDuKien=?, NgayCheckOutDuKien=?, TienCoc=? WHERE MaDatPhong=?";
            try (PreparedStatement stmt = conn.prepareStatement(updateDp)) {
                stmt.setTimestamp(1, java.sql.Timestamp.valueOf(checkIn));
                stmt.setTimestamp(2, java.sql.Timestamp.valueOf(checkOut));
                stmt.setDouble(3, tienCoc); stmt.setInt(4, maDatPhong);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM ChiTietDatPhong WHERE MaDatPhong=?")) {
                stmt.setInt(1, maDatPhong); stmt.executeUpdate();
            }
            String insCt = "INSERT INTO ChiTietDatPhong (MaDatPhong, MaPhong) VALUES (?, (SELECT MaPhong FROM Phong WHERE SoPhong = ?))";
            try (PreparedStatement stmt = conn.prepareStatement(insCt)) {
                for (String soPhong : roomNumbers) {
                    stmt.setInt(1, maDatPhong); stmt.setString(2, soPhong); stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit(); return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace(); return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception ex) {}
        }
    }
}