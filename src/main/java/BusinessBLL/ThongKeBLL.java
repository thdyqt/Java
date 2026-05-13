package BusinessBLL;

import DataDAL.ThongKeDAL;
import java.time.LocalDate;
import java.util.Map;

public class ThongKeBLL {
    public static double[] getTongQuan(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null || tuNgay.isAfter(denNgay)) return new double[4];
        return ThongKeDAL.getTongQuan(tuNgay, denNgay);
    }

    public static Map<String, Double> getDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null || tuNgay.isAfter(denNgay)) return null;
        return ThongKeDAL.getDoanhThuTheoNgay(tuNgay, denNgay);
    }
}