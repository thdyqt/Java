package View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SoDoPhongController implements Initializable {

    @FXML
    private ComboBox<String> cbFilterFloor;

    @FXML
    private FlowPane roomGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Nạp dữ liệu giả cho bộ lọc tầng
        cbFilterFloor.getItems().addAll("Tất cả", "Tầng 1", "Tầng 2", "Tầng 3", "Tầng 4", "Tầng 5");

        // TODO: Sau này bạn sẽ gọi Database để lấy List<Phong>
        // và dùng vòng lặp for để tạo các VBox room-card bằng code Java thay vì viết cứng trong FXML.
    }

    @FXML
    void handleRoomClick(MouseEvent event) {
        VBox clickedRoom = (VBox) event.getSource();

        if (clickedRoom.getStyleClass().contains("status-trong")) {
            System.out.println("Mở Form Check-in cho khách mới!");
            // Mở popup nhập tên khách, chọn dịch vụ...
        }
        else if (clickedRoom.getStyleClass().contains("status-cokhach")) {
            System.out.println("Mở Form Xem chi tiết/Thêm dịch vụ/Check-out!");
        }
        else if (clickedRoom.getStyleClass().contains("status-dondep")) {
            System.out.println("Đổi trạng thái phòng thành 'Trống'");
        }
    }
}