package BusinessBLL;

import DataDAL.LoaiPhongDAL;
import EntitiesDTO.LoaiPhong;

import java.util.List;

public class LoaiPhongBLL {
    public static List<LoaiPhong> getAllLoaiPhong() {
        return LoaiPhongDAL.getAllLoaiPhong();
    }

    // --- BỔ SUNG HÀM NÀY ĐỂ LẤY GIÁ TIỀN TỪ DB ---
    public static double getDonGiaByMaLoai(int maLoaiPhong) {
        List<LoaiPhong> danhSach = LoaiPhongDAL.getAllLoaiPhong();
        for (LoaiPhong lp : danhSach) {
            if (lp.getMaLoaiPhong() == maLoaiPhong) {
                return lp.getDonGia();
            }
        }
        return 0; // Trả về 0 nếu không tìm thấy (tránh lỗi crash)
    }

    public static String saveLoaiPhong(LoaiPhong lp, boolean isEdit) {
        if (lp.getTenLoaiPhong() == null || lp.getTenLoaiPhong().trim().isEmpty()) {
            return "Tên loại phòng không được để trống!";
        }
        if (lp.getDonGia() <= 0) {
            return "Đơn giá phải lớn hơn 0!";
        }
        if (lp.getSoNguoiToiDa() <= 0) {
            return "Số người tối đa phải từ 1 trở lên!";
        }

        int excludeId = isEdit ? lp.getMaLoaiPhong() : -1;
        if (LoaiPhongDAL.isTenLoaiPhongExists(lp.getTenLoaiPhong(), excludeId)) {
            return "Tên loại phòng này đã tồn tại!";
        }

        boolean success = isEdit ? LoaiPhongDAL.updateLoaiPhong(lp) : LoaiPhongDAL.insertLoaiPhong(lp);
        return success ? "SUCCESS" : "Lỗi khi lưu dữ liệu vào hệ thống!";
    }
}