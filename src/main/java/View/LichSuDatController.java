package View;

import DataDAL.DatPhongDAL;
import javafx.beans.property.SimpleStringProperty; // Thêm import này
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class LichSuDatController {
    @FXML private Label lblTitle;
    @FXML private TableView<Map<String, Object>> tbvLichSu;
    @FXML private TableColumn<Map<String, Object>, String> colTenKhach, colNgayIn, colNgayOut, colTrangThai;

    public void loadData(String soPhong) {
        lblTitle.setText("Lịch sử & Lịch đặt phòng: " + soPhong);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        colTenKhach.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("HoTen")))
        );

        colTrangThai.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("TrangThai")))
        );

        colNgayIn.setCellValueFactory(data -> {
            LocalDateTime date = (LocalDateTime) data.getValue().get("NgayIn");
            return new SimpleStringProperty(date != null ? date.format(formatter) : "");
        });

        colNgayOut.setCellValueFactory(data -> {
            LocalDateTime date = (LocalDateTime) data.getValue().get("NgayOut");
            return new SimpleStringProperty(date != null ? date.format(formatter) : "");
        });

        tbvLichSu.setItems(FXCollections.observableArrayList(DatPhongDAL.getLichSuDatPhong(soPhong)));
    }

    @FXML
    void handleClose() {
        ((Stage) lblTitle.getScene().getWindow()).close();
    }
}