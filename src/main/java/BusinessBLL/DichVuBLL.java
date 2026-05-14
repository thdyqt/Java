package BusinessBLL;

import DataDAL.DichVuDAL;
import EntitiesDTO.DichVu;

import java.util.List;

public class DichVuBLL {
    public static List<DichVu> getAllDichVu() {
        return DichVuDAL.getAllDichVu();
    }

    public static List<DichVu> getActiveDichVu() {
        return DichVuDAL.getActiveDichVu();
    }

    public static boolean insertDichVu(DichVu dv) {
        if (dv == null || dv.getTenDichVu() == null || dv.getTenDichVu().trim().isEmpty()) {
            return false;
        }

        if (dv.getTenDichVu().length() > 100) {
            return false;
        }

        if (dv.getDonGia() < 0 || dv.getDonGia() > 9999999999L) {
            return false;
        }

        return DichVuDAL.insertDichVu(dv);
    }

    public static boolean updateDichVu(DichVu dv) {
        if (dv == null || dv.getMaDichVu() <= 0 ||
                dv.getTenDichVu() == null || dv.getTenDichVu().trim().isEmpty()) {
            return false;
        }

        if (dv.getTenDichVu().length() > 100) {
            return false;
        }

        if (dv.getDonGia() < 0 || dv.getDonGia() > 9999999999L) {
            return false;
        }

        return DichVuDAL.updateDichVu(dv);
    }

    public static boolean stopDichVu(int maDichVu) {
        if (maDichVu <= 0) {
            return false;
        }
        return DichVuDAL.stopDichVu(maDichVu);
    }

    public static boolean restoreDichVu(int maDichVu) {
        if (maDichVu <= 0) return false;
        return DichVuDAL.restoreDichVu(maDichVu);
    }
}