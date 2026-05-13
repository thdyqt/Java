package BusinessBLL;

import DataDAL.LoaiPhongDAL;
import EntitiesDTO.LoaiPhong;

import java.util.List;

public class LoaiPhongBLL {
    public static List<LoaiPhong> getAllLoaiPhong() {
        return LoaiPhongDAL.getAllLoaiPhong();
    }
}
