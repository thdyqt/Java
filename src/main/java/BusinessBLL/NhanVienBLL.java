package BusinessBLL;

import DataDAL.NhanVienDAL;
import EntitiesDTO.NhanVien;
import Utilities.UserSession;

import java.util.List;

public class NhanVienBLL {
    public static NhanVien checkLogin(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }

        NhanVien nv = NhanVienDAL.findActiveStaffByUsernameAndPassword(username, password);

        if (nv != null) {
            UserSession.getInstance().setNhanVien(nv);
        }
        return nv;
    }

    public static void logout() {
        UserSession.getInstance().clearSession();
    }

    public static List<NhanVien> getAllNhanVien() {
        return NhanVienDAL.getAllNhanVien();
    }

    public static String insertNhanVien(NhanVien nv) {
        if (nv.getHoTen().isEmpty() || nv.getTenDangNhap().isEmpty() || nv.getMatKhau().isEmpty()) {
            return "Vui lòng điền đầy đủ Họ tên, Tên đăng nhập và Mật khẩu!";
        }

        if (nv.getMatKhau().length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự!";
        }

        if (NhanVienDAL.checkUsernameExists(nv.getTenDangNhap(), -1)) {
            return "Tên đăng nhập này đã tồn tại trong hệ thống!";
        }

        if (NhanVienDAL.checkPhoneExists(nv.getSoDienThoai(), -1)) {
            return "Số điện thoại này đã được đăng ký cho nhân viên khác!";
        }

        boolean success = NhanVienDAL.insertNhanVien(nv);
        return success ? "SUCCESS" : "Lỗi hệ thống khi thêm nhân viên!";
    }

    public static String updateNhanVien(NhanVien nv) {
        if (nv.getHoTen().isEmpty() || nv.getTenDangNhap().isEmpty()) {
            return "Họ tên và Tên đăng nhập không được để trống!";
        }

        if (NhanVienDAL.checkUsernameExists(nv.getTenDangNhap(), nv.getMaNhanVien())) {
            return "Tên đăng nhập này đã bị nhân viên khác sử dụng!";
        }

        if (NhanVienDAL.checkPhoneExists(nv.getSoDienThoai(), -1)) {
            return "Số điện thoại này đã được đăng ký cho nhân viên khác!";
        }

        boolean success = NhanVienDAL.updateNhanVien(nv);
        return success ? "SUCCESS" : "Lỗi hệ thống khi cập nhật thông tin!";
    }

    public static boolean changePassword(int maNV, String newPass) {
        if (newPass == null || newPass.length() < 6) return false;
        return NhanVienDAL.updatePassword(maNV, newPass);
    }

    public static boolean stopNhanVien(int maNV) {
        if (maNV <= 0) return false;

        NhanVien current = UserSession.getInstance().getNhanVien();
        if (current != null && current.getMaNhanVien() == maNV) {
            return false;
        }

        return NhanVienDAL.stopNhanVien(maNV);
    }

    public static boolean restoreNhanVien(int maNV) {
        if (maNV <= 0) return false;
        return NhanVienDAL.restoreNhanVien(maNV);
    }
}
