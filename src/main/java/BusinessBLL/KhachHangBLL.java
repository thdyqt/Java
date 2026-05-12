package BusinessBLL;

import DataDAL.KhachHangDAL;
import EntitiesDTO.KhachHang;

public class KhachHangBLL {
    public static KhachHang getCustomerById(int maKhachHang) {
        if (maKhachHang <= 0) return null;
        return KhachHangDAL.getCustomerById(maKhachHang);
    }
}
