package BusinessBLL;

import DataDAL.DatPhongDAL;
import DataDAL.KhachHangDAL;
import EntitiesDTO.DatPhong;
import EntitiesDTO.KhachHang;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DatPhongBLL {
    public static DatPhong getBookingById(int maDatPhong) {
        if (maDatPhong <= 0) return null;
        return DatPhongDAL.getBookingById(maDatPhong);
    }

    public static int getActiveBookingIdByRoomId(int maPhong) {
        if (maPhong <= 0) return -1;
        return DatPhongDAL.getActiveBookingIdByRoomId(maPhong);
    }

    public static boolean checkDateConflict(String roomNumber, LocalDate checkInDate, LocalDate checkOutDate) {
        return DatPhongDAL.checkDateConflict(roomNumber, checkInDate, checkOutDate);
    }

    public static boolean processCheckIn(String fullName, String phoneNumber, String cccdPassport, String email, String address,
                                         List<String> roomNumbers, int employeeId,
                                         LocalDateTime checkInDate, LocalDateTime checkOutDate, double deposit) {

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
        booking.setTrangThai("Đang ở");

        return DatPhongDAL.insertCheckInTransaction(booking, roomNumbers);
    }
}