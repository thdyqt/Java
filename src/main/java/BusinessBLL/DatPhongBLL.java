package BusinessBLL;

import DataDAL.DatPhongDAL;
import DataDAL.KhachHangDAL;
import EntitiesDTO.DatPhong;
import EntitiesDTO.KhachHang;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DatPhongBLL {
    public static DatPhong getBookingById(int maDatPhong) {
        if (maDatPhong <= 0) return null;
        return DatPhongDAL.getBookingById(maDatPhong);
    }

    public static int getActiveBookingIdByRoomId(int maPhong) {
        if (maPhong <= 0) return -1;
        return DatPhongDAL.getActiveBookingIdByRoomId(maPhong);
    }

    public static List<Map<String, Object>> getLichSuDatPhong(String soPhong) {
        return DatPhongDAL.getLichSuDatPhong(soPhong);
    }

    public static List<Map<String, Object>> getAllBookingsWithDetails() {
        return DatPhongDAL.getAllBookingsWithDetails();
    }

    public static Map<String, Object> getBookingFullInfo(int maDatPhong) {
        return DatPhongDAL.getBookingFullInfo(maDatPhong);
    }

    public static List<Map<String, Object>> getHistoryByCustomerId(int maKH) {
        return DatPhongDAL.getHistoryByCustomerId(maKH);
    }

    public static boolean checkDateConflict(String roomNumber, LocalDate checkInDate, LocalDate checkOutDate, int excludeMaDatPhong) {
        return DatPhongDAL.checkDateConflict(roomNumber, checkInDate, checkOutDate, excludeMaDatPhong);
    }

    public static boolean processCheckIn(String fullName, String phoneNumber, String cccdPassport, String email, String address,
                                         List<String> roomNumbers, int employeeId,
                                         LocalDateTime checkInDate, LocalDateTime checkOutDate, double deposit, String trangThai) {

        KhachHang existingCustomer = KhachHangDAL.getCustomerByCCCD(cccdPassport);
        int customerId = -1;

        if (existingCustomer == null) {
            KhachHang newCustomer = new KhachHang(0, fullName, cccdPassport, phoneNumber, email, address);
            customerId = KhachHangDAL.insertCustomer(newCustomer);

            if (customerId == -1) {
                return false;
            }
        } else {
            customerId = existingCustomer.getMaKhachHang();

            existingCustomer.setHoTen(fullName);
            existingCustomer.setSoDienThoai(phoneNumber);
            existingCustomer.setEmail(email);
            existingCustomer.setDiaChi(address);

            KhachHangDAL.updateCustomer(existingCustomer);
        }

        DatPhong booking = new DatPhong();
        booking.setMaKhachHang(customerId);
        booking.setMaNhanVien(employeeId);
        booking.setNgayDat(LocalDateTime.now());
        booking.setNgayCheckInDuKien(checkInDate);
        booking.setNgayCheckOutDuKien(checkOutDate);
        booking.setTienCoc(deposit);
        booking.setTrangThai(trangThai);

        return DatPhongDAL.insertCheckInTransaction(booking, roomNumbers);
    }

    public static boolean quickCheckIn(int maDatPhong) {
        return DatPhongDAL.quickCheckIn(maDatPhong);
    }

    public static boolean changeStatus(int maDatPhong, String newStatus) {
        if (maDatPhong <= 0 || newStatus == null || newStatus.trim().isEmpty()) {
            return false;
        }
        return DatPhongDAL.changeStatus(maDatPhong, newStatus);
    }

    public static boolean updateBooking(int maDatPhong, String hoTen, String sdt, String cccd, String email, String diaChi, List<String> roomNumbers, LocalDateTime checkIn, LocalDateTime checkOut, double tienCoc) {
        return DatPhongDAL.updateBookingTransaction(maDatPhong, hoTen, sdt, cccd, email, diaChi, roomNumbers, checkIn, checkOut, tienCoc);
    }
}