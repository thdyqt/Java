package BusinessBLL;

import DataDAL.DatPhongDAL;
import DataDAL.KhachHangDAL;
import EntitiesDTO.DatPhong;
import EntitiesDTO.KhachHang;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DatPhongBLL {
    public static boolean checkDateConflict(String roomNumber, LocalDate checkInDate, LocalDate checkOutDate) {
        return DatPhongDAL.checkDateConflict(roomNumber, checkInDate, checkOutDate);
    }

    public static boolean processCheckIn(String fullName, String phoneNumber, String cccdPassport, String email, String address,
                                         List<String> roomNumbers, int employeeId,
                                         LocalDateTime checkInDate, LocalDateTime checkOutDate, double deposit) {

        int customerId = KhachHangDAL.findCustomerIdByCCCD(cccdPassport);

        if (customerId == -1) {
            KhachHang newCustomer = new KhachHang(0, fullName, cccdPassport, phoneNumber, email, address);
            customerId = KhachHangDAL.insertCustomer(newCustomer);

            if (customerId == -1) return false;

        } else {
            KhachHang existingCustomer = new KhachHang(customerId, fullName, cccdPassport, phoneNumber, email, address);
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