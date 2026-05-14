package BusinessBLL;

import DataDAL.KhachHangDAL;
import EntitiesDTO.KhachHang;

import java.util.List;

public class KhachHangBLL {
    private static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        return phone.matches("^0\\d{9}$");
    }

    private static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    public static List<KhachHang> getAllCustomers() {
        return KhachHangDAL.getAllCustomers();
    }

    public static KhachHang getCustomerById(int maKhachHang) {
        if (maKhachHang <= 0) return null;
        return KhachHangDAL.getCustomerById(maKhachHang);
    }

    public static KhachHang getCustomerByCCCD(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) return null;
        return KhachHangDAL.getCustomerByCCCD(cccd);
    }

    public static int insertCustomer(KhachHang customer) {
        if (customer == null ||
                customer.getHoTen() == null || customer.getHoTen().trim().isEmpty() ||
                customer.getCccdPassport() == null || customer.getCccdPassport().trim().isEmpty()) {
            return -1;
        }

        if (!isValidPhoneNumber(customer.getSoDienThoai())) return -1;
        if (!isValidEmail(customer.getEmail())) return -1;

        return KhachHangDAL.insertCustomer(customer);
    }

    public static boolean updateCustomer(KhachHang customer) {
        if (customer == null || customer.getMaKhachHang() <= 0 ||
                customer.getHoTen() == null || customer.getHoTen().trim().isEmpty()) {
            return false;
        }

        if (!isValidPhoneNumber(customer.getSoDienThoai())) return false;
        if (!isValidEmail(customer.getEmail())) return false;

        return KhachHangDAL.updateCustomer(customer);
    }
}