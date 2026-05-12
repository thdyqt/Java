package BusinessBLL;

import DataDAL.ChiTietDatPhongDAL;
import EntitiesDTO.ChiTietDatPhong;

import java.util.List;

public class ChiTietDatPhongBLL {
    public static List<ChiTietDatPhong> getChiTietPhongTheoDoan(int maDatPhong) {
        return ChiTietDatPhongDAL.getChiTietPhongTheoDoan(maDatPhong);
    }
}
