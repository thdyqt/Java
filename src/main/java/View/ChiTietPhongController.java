package View;

import BusinessBLL.PhongBLL;
import EntitiesDTO.Phong;
import Utilities.Others;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChiTietPhongController {

    @FXML
    private VBox mainPane;

    @FXML
    private Label lblSoPhong;

    private Phong phongHienTai;

    // Hàm này đón dữ liệu từ SoDoPhongController truyền sang
    public void setPhongData(Phong p) {
        this.phongHienTai = p;
        lblSoPhong.setText("Phòng " + p.getSoPhong());

        // TODO: Sau này dùng p.getSoPhong() gọi Database lấy tên khách hàng và hiện lên giao diện
    }

    @FXML
    void handleThemDichVu(ActionEvent event) {
        Others.showAlert(mainPane, "Tính năng Thêm dịch vụ đang được phát triển!", false);
    }

    @FXML
    void handleTraPhong(ActionEvent event) {
        // Hỏi xác nhận xem khách có chắc chắn muốn trả phòng không
        if (Others.showCustomConfirm("Thanh toán", "Khách hàng muốn thanh toán và trả phòng " + phongHienTai.getSoPhong() + "?", "Đồng ý", "Hủy")) {

            // TODO: Sau này sẽ có code tính toán tiền bạc, sinh hóa đơn ở đây

            // Chuyển trạng thái phòng thành "Đang dọn dẹp"
            boolean success = PhongBLL.updateRoomStatus(phongHienTai.getSoPhong(), "Đang dọn dẹp");

            if (success) {
                // Đóng cửa sổ Popup hiện tại lại
                Stage stage = (Stage) lblSoPhong.getScene().getWindow();
                stage.close();
            } else {
                Others.showAlert(mainPane, "Lỗi khi cập nhật trạng thái phòng!", true);
            }
        }
    }
}