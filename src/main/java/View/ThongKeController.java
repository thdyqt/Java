package View;

import BusinessBLL.ThongKeBLL;
import Utilities.Others;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Map;

public class ThongKeController {
    @FXML private VBox mainPane;
    @FXML private DatePicker dpTuNgay, dpDenNgay;
    @FXML private Button btnLoc;

    @FXML private Label lblTongDoanhThu, lblDoanhThuPhong, lblDoanhThuDV, lblSoHoaDon;
    @FXML private LineChart<String, Number> lineChartDoanhThu;
    @FXML private PieChart pieChartCoCau;
    @FXML private BarChart<String, Number> barChartLoaiPhong, barChartDichVu;

    @FXML
    public void initialize() {
        Others.playButtonAnimation(btnLoc);

        dpDenNgay.setValue(LocalDate.now());
        dpTuNgay.setValue(LocalDate.now().minusDays(7));

        handleLocDuLieu();
    }

    @FXML
    void handleLocDuLieu() {
        LocalDate tuNgay = dpTuNgay.getValue();
        LocalDate denNgay = dpDenNgay.getValue();

        if (tuNgay == null || denNgay == null || tuNgay.isAfter(denNgay)) {
            Others.showAlert(mainPane, "Khoảng thời gian không hợp lệ!", true);
            return;
        }

        double[] tongQuan = ThongKeBLL.getTongQuan(tuNgay, denNgay);
        lblTongDoanhThu.setText(Others.formatPrice(tongQuan[0]));
        lblDoanhThuPhong.setText(Others.formatPrice(tongQuan[1]));
        lblDoanhThuDV.setText(Others.formatPrice(tongQuan[2]));
        lblSoHoaDon.setText(String.format("%.0f", tongQuan[3]));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Tiền Phòng", tongQuan[1]),
                new PieChart.Data("Tiền Dịch Vụ", tongQuan[2])
        );
        pieChartCoCau.setData(pieData);

        lineChartDoanhThu.getData().clear();
        Map<String, Double> doanhThuNgay = ThongKeBLL.getDoanhThuTheoNgay(tuNgay, denNgay);

        if (doanhThuNgay != null && !doanhThuNgay.isEmpty()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Doanh Thu Tổng");

            for (Map.Entry<String, Double> entry : doanhThuNgay.entrySet()) {
                String rawDate = entry.getKey();
                String displayDate = rawDate.substring(8, 10) + "/" + rawDate.substring(5, 7);

                series.getData().add(new XYChart.Data<>(displayDate, entry.getValue()));
            }
            lineChartDoanhThu.getData().add(series);
        }

        barChartLoaiPhong.getData().clear();
        Map<String, Integer> tkLoaiPhong = ThongKeBLL.getThongKeLoaiPhong(tuNgay, denNgay);
        if (tkLoaiPhong != null && !tkLoaiPhong.isEmpty()) {
            XYChart.Series<String, Number> seriesLP = new XYChart.Series<>();
            for (Map.Entry<String, Integer> entry : tkLoaiPhong.entrySet()) {
                seriesLP.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            barChartLoaiPhong.getData().add(seriesLP);
        }

        barChartDichVu.getData().clear();
        Map<String, Integer> tkDichVu = ThongKeBLL.getThongKeDichVu(tuNgay, denNgay);
        if (tkDichVu != null && !tkDichVu.isEmpty()) {
            XYChart.Series<String, Number> seriesDV = new XYChart.Series<>();
            for (Map.Entry<String, Integer> entry : tkDichVu.entrySet()) {
                seriesDV.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            barChartDichVu.getData().add(seriesDV);
        }
    }
}