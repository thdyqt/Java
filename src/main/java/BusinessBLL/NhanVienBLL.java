package BusinessBLL;

import DataDAL.NhanVienDAL;
import EntitiesDTO.NhanVien;
import Utilities.UserSession;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class NhanVienBLL {
    private static boolean isValidPassword(String password) {
        if (password == null) return false;
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        return password.matches(regex);
    }

    private static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        return phone.matches("^0\\d{9}$"); // Bắt đầu bằng 0, theo sau là 9 chữ số
    }

    public static NhanVien checkLogin(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }

        NhanVien nv = NhanVienDAL.findActiveStaffByUsername(username);

        if (nv != null && BCrypt.checkpw(password, nv.getMatKhau())) {
            UserSession.getInstance().setNhanVien(nv);
            return nv;
        }
        return null;
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

        if (nv.getSoDienThoai() == null || nv.getSoDienThoai().trim().isEmpty()) {
            return "Số điện thoại là bắt buộc!";
        }
        if (!isValidPhoneNumber(nv.getSoDienThoai())) {
            return "Số điện thoại không hợp lệ! (Phải đủ 10 số và bắt đầu bằng số 0)";
        }

        if (nv.getMatKhau() != null && !nv.getMatKhau().equals("123456") && !isValidPassword(nv.getMatKhau())) {
            return "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!";
        }

        if (NhanVienDAL.checkUsernameExists(nv.getTenDangNhap(), -1)) {
            return "Tên đăng nhập này đã tồn tại trong hệ thống!";
        }

        if (NhanVienDAL.checkPhoneExists(nv.getSoDienThoai(), -1)) {
            return "Số điện thoại này đã được đăng ký cho nhân viên khác!";
        }

        String hashedPassword = BCrypt.hashpw(nv.getMatKhau(), BCrypt.gensalt(12));
        nv.setMatKhau(hashedPassword);

        boolean success = NhanVienDAL.insertNhanVien(nv);
        return success ? "SUCCESS" : "Lỗi hệ thống khi thêm nhân viên!";
    }

    public static String updateNhanVien(NhanVien nv) {
        if (nv.getHoTen().isEmpty() || nv.getTenDangNhap().isEmpty()) {
            return "Họ tên và Tên đăng nhập không được để trống!";
        }

        if (nv.getSoDienThoai() == null || nv.getSoDienThoai().trim().isEmpty()) {
            return "Số điện thoại là bắt buộc!";
        }
        if (!isValidPhoneNumber(nv.getSoDienThoai())) {
            return "Số điện thoại không hợp lệ! (Phải đủ 10 số và bắt đầu bằng số 0)";
        }

        if (NhanVienDAL.checkUsernameExists(nv.getTenDangNhap(), nv.getMaNhanVien())) {
            return "Tên đăng nhập này đã bị nhân viên khác sử dụng!";
        }

        if (NhanVienDAL.checkPhoneExists(nv.getSoDienThoai(), nv.getMaNhanVien())) {
            return "Số điện thoại này đã được đăng ký cho nhân viên khác!";
        }

        boolean success = NhanVienDAL.updateNhanVien(nv);
        return success ? "SUCCESS" : "Lỗi hệ thống khi cập nhật thông tin!";
    }

    public static boolean changePassword(int maNV, String newPass) {
        if (!isValidPassword(newPass)) {
            return false;
        }

        String hashedPassword = BCrypt.hashpw(newPass, BCrypt.gensalt(12));
        return NhanVienDAL.updatePassword(maNV, hashedPassword);
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

    public static boolean resetPasswordDefault(int maNV) {
        if (maNV <= 0) return false;

        String defaultPass = "123456";

        String hashedDefaultPass = BCrypt.hashpw(defaultPass, BCrypt.gensalt(12));

        return NhanVienDAL.updatePassword(maNV, hashedDefaultPass);
    }
}