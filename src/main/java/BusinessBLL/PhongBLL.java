package BusinessBLL;

import DataDAL.PhongDAL;
import EntitiesDTO.Phong;
import Utilities.UserSession;

import java.util.List;

public class PhongBLL {
    public static List<Phong> getAllRooms() {
        List<Phong> danhSachPhong = PhongDAL.getAllRooms();

        String chucVu = UserSession.getInstance().getNhanVien().getChucVu();
        if (!"Admin".equals(chucVu)) {
            danhSachPhong.removeIf(p -> "Bảo trì".equals(p.getTrangThai()));
        }

        return danhSachPhong;
    }

    public static List<PhongDAL.PhongViewModel> getDanhSachPhongFull() {
        return PhongDAL.getDanhSachPhongFull();
    }

    public static boolean isSoPhongExists(String soPhong, int excludeId) {
        if (soPhong == null || soPhong.trim().isEmpty()) return false;
        return PhongDAL.isSoPhongExists(soPhong, excludeId);
    }

    public static boolean insertPhong(Phong p) {
        if (p.getSoPhong() == null || p.getSoPhong().trim().isEmpty()) return false;
        return PhongDAL.insertPhong(p);
    }

    public static boolean updatePhong(Phong p) {
        if (p.getSoPhong() == null || p.getSoPhong().trim().isEmpty()) return false;
        return PhongDAL.updatePhong(p);
    }

    public static boolean updateRoomStatus(String roomNumber, String newStatus) {
        return PhongDAL.updateRoomStatus(roomNumber, newStatus);
    }
}