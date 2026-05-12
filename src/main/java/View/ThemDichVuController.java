package View;

import BusinessBLL.DichVuBLL;
import BusinessBLL.SuDungDichVuBLL;
import EntitiesDTO.DichVu;
import Utilities.Others;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.util.List;

public class ThemDichVuController {
    @FXML private ComboBox<DichVu> cbDichVu;
    @FXML private Spinner<Integer> spSoLuong;
    @FXML private Label lblThanhTien;

    private int bookingId;
    private boolean isSuccess = false;

    @FXML
    public void initialize() {
        List<DichVu> menu = DichVuBLL.getAllDichVu();
        ObservableList<DichVu> items = FXCollections.observableArrayList(menu);
        FilteredList<DichVu> filteredItems = new FilteredList<>(items, p -> true);
        cbDichVu.setItems(filteredItems);

        cbDichVu.setConverter(new StringConverter<DichVu>() {
            @Override
            public String toString(DichVu object) {
                if (object == null) return "";
                return object.getTenDichVu() + " (" + Others.formatPrice(object.getDonGia()) + ")";
            }
            @Override
            public DichVu fromString(String string) {
                return items.stream()
                        .filter(item -> toString(item).equals(string))
                        .findFirst().orElse(null);
            }
        });

        cbDichVu.setEditable(true);
        cbDichVu.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final DichVu selected = cbDichVu.getSelectionModel().getSelectedItem();

            if (selected != null && cbDichVu.getConverter().toString(selected).equals(newValue)) {
                calculateTotal();
                return;
            }

            filteredItems.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return item.getTenDichVu().toLowerCase().contains(newValue.toLowerCase());
            });

            if (filteredItems.size() > 0) cbDichVu.show();
            else cbDichVu.hide();
        });

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spSoLuong.setValueFactory(valueFactory);
        spSoLuong.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
    }

    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public boolean isSuccess() { return isSuccess; }

    private void calculateTotal() {
        DichVu selected = cbDichVu.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int sl = spSoLuong.getValue();
            double total = selected.getDonGia() * sl;
            lblThanhTien.setText(Others.formatPrice(total));
        } else {
            lblThanhTien.setText("0 đ");
        }
    }

    @FXML
    void handleConfirm() {
        String currentText = cbDichVu.getEditor().getText();
        DichVu selected = cbDichVu.getConverter().fromString(currentText);

        if (selected == null) {
            Others.showAlert(lblThanhTien, "Vui lòng chọn một dịch vụ hợp lệ từ danh sách!", true);
            return;
        }

        int sl = spSoLuong.getValue();
        double total = selected.getDonGia() * sl;

        boolean result = SuDungDichVuBLL.addService(bookingId, selected.getMaDichVu(), sl, total);

        if (result) {
            this.isSuccess = true;
            Stage stage = (Stage) lblThanhTien.getScene().getWindow();
            stage.close();
        } else {
            Others.showAlert(lblThanhTien, "Lỗi khi lưu vào cơ sở dữ liệu!", true);
        }
    }

    @FXML
    void handleCancel() {
        Stage stage = (Stage) lblThanhTien.getScene().getWindow();
        stage.close();
    }
}