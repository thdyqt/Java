package BusinessBLL;

import DataDAL.DichVuDAL;
import DataDAL.SuDungDichVuDAL;
import EntitiesDTO.DichVu;
import EntitiesDTO.SuDungDichVu;

import java.util.List;

public class SuDungDichVuBLL {
    public static List<SuDungDichVu> getServiceByBookingId(int bookingId) {
        if (bookingId <= 0) return null;
        return SuDungDichVuDAL.getServiceByBookingId(bookingId);
    }

    public static boolean addService(int bookingId, int serviceId, int quantity, double total) {
        SuDungDichVu sd = new SuDungDichVu();
        sd.setMaDatPhong(bookingId);
        sd.setMaDichVu(serviceId);
        sd.setSoLuong(quantity);
        sd.setThanhTien(total);
        return SuDungDichVuDAL.insertService(sd);
    }

    public static boolean deleteService(int maSuDung) {
        if (maSuDung <= 0) return false;
        return SuDungDichVuDAL.deleteService(maSuDung);
    }
}
