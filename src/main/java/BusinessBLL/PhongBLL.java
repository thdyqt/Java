package BusinessBLL;

import DataDAL.PhongDAL;
import EntitiesDTO.Phong;

import java.util.List;

public class PhongBLL {
    public static List<Phong> getAllRooms() {
        return PhongDAL.getAllRooms();
    }

    public static boolean updateRoomStatus(String roomNumber, String newStatus) {
        return PhongDAL.updateRoomStatus(roomNumber, newStatus);
    }
}
