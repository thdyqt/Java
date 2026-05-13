package View;

import BusinessBLL.DatPhongBLL;
import Utilities.Others;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class LichSuKhachHangController {
    @FXML private VBox mainPane;
    @FXML private Label lblCustomerName;
    @FXML private TableView<HistoryRow> tvLichSu;
    @FXML private TableColumn<HistoryRow, String> colMaDon, colPhong, colNgayIn, colNgayOut, colTrangThai;

    private ObservableList<HistoryRow> historyData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Others.animateTableRows(tvLichSu);
        setupColumns();
    }

    private void setupColumns() {
        tvLichSu.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colMaDon.setCellValueFactory(d -> new SimpleStringProperty("HD-" + d.getValue().getMaDon()));
        colPhong.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhong()));
        colNgayIn.setCellValueFactory(d -> new SimpleStringProperty(Others.formatDateTime(d.getValue().getNgayIn())));
        colNgayOut.setCellValueFactory(d -> new SimpleStringProperty(Others.formatDateTime(d.getValue().getNgayOut())));
        colTrangThai.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTrangThai()));
    }

    public void loadHistory(int maKH, String tenKH) {
        lblCustomerName.setText(tenKH);
        historyData.clear();

        List<Map<String, Object>> data = DatPhongBLL.getHistoryByCustomerId(maKH);
        for (Map<String, Object> row : data) {
            historyData.add(new HistoryRow(
                    (Integer) row.get("MaDatPhong"),
                    (String) row.get("DanhSachPhong"),
                    (LocalDateTime) row.get("NgayIn"),
                    (LocalDateTime) row.get("NgayOut"),
                    (String) row.get("TrangThai")
            ));
        }
        tvLichSu.setItems(historyData);
    }

    @FXML
    void handleClose() {
        ((Stage) mainPane.getScene().getWindow()).close();
    }

    public static class HistoryRow {
        private int maDon;
        private String phong, trangThai;
        private LocalDateTime ngayIn, ngayOut;

        public HistoryRow(int maDon, String phong, LocalDateTime ngayIn, LocalDateTime ngayOut, String trangThai) {
            this.maDon = maDon; this.phong = phong; this.ngayIn = ngayIn; this.ngayOut = ngayOut; this.trangThai = trangThai;
        }
        public int getMaDon() { return maDon; }
        public String getPhong() { return phong; }
        public String getTrangThai() { return trangThai; }
        public LocalDateTime getNgayIn() { return ngayIn; }
        public LocalDateTime getNgayOut() { return ngayOut; }
    }
}