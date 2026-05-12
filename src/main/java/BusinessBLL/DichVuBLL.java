package BusinessBLL;

import DataDAL.DichVuDAL;
import EntitiesDTO.DichVu;

import java.util.List;

public class DichVuBLL {
    public static List<DichVu> getAllDichVu() {
        return DichVuDAL.getAllDichVu();
    }
}
