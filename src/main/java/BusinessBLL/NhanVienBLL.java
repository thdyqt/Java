package BusinessBLL;

import DataDAL.NhanVienDAL;
import EntitiesDTO.NhanVien;
import Utilities.UserSession;

public class NhanVienBLL {
    public static NhanVien checkLogin(String username, String password) {
        NhanVien nv = NhanVienDAL.findStaffByUsernameAndPassword(username, password);
        if (nv != null) {
            UserSession.getInstance().setNhanVien(nv);
            return nv;
        }
        return null;
    }

    public static void logout() {
        UserSession.getInstance().clearSession();
    }
}
