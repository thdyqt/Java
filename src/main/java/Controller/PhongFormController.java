package Controller;

import BusinessBLL.LoaiPhongBLL;
import BusinessBLL.PhongBLL;
import EntitiesDTO.LoaiPhong;
import EntitiesDTO.Phong;
import Utilities.Others;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class PhongFormController {
    @FXML private VBox mainPane;
    @FXML private Label lblTitle;
    @FXML private TextField txtSoPhong;
    @FXML private ComboBox<LoaiPhong> cbLoaiPhong;
    @FXML private ComboBox<String> cbTrangThai;
    @FXML private Button btnSave, btnCancel;

    private Phong currentPhong;
    private boolean isEditMode = false;
    private List<LoaiPhong> listLoaiPhong;

    @FXML
    public void initialize() {
        Others.playButtonAnimation(btnSave);
        Others.playButtonAnimation(btnCancel);

        listLoaiPhong = LoaiPhongBLL.getAllLoaiPhong();
        cbLoaiPhong.setItems(FXCollections.observableArrayList(listLoaiPhong));

        cbLoaiPhong.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(LoaiPhong item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getTenLoaiPhong() + " (" + Others.formatPrice(item.getDonGia()) + ")");
            }
        });
        cbLoaiPhong.setButtonCell(cbLoaiPhong.getCellFactory().call(null));

        cbTrangThai.setItems(FXCollections.observableArrayList("Trống", "Bảo trì"));
        cbTrangThai.getSelectionModel().select(0);
    }

    public void setPhong(int maPhong) {
        this.isEditMode = true;
        lblTitle.setText("CẬP NHẬT PHÒNG");

        this.currentPhong = PhongBLL.getAllRooms().stream()
                .filter(p -> p.getMaPhong() == maPhong).findFirst().orElse(null);

        if (currentPhong != null) {
            txtSoPhong.setText(currentPhong.getSoPhong());
            cbTrangThai.getSelectionModel().select(currentPhong.getTrangThai());

            for (LoaiPhong lp : listLoaiPhong) {
                if (lp.getMaLoaiPhong() == currentPhong.getMaLoaiPhong()) {
                    cbLoaiPhong.getSelectionModel().select(lp);
                    break;
                }
            }
        }
    }

    @FXML
    void handleSave() {
        String soPhong = txtSoPhong.getText().trim();
        LoaiPhong loaiPhong = cbLoaiPhong.getValue();

        if (soPhong.isEmpty() || loaiPhong == null) {
            Others.showAlert(mainPane, "Vui lòng nhập đầy đủ thông tin!", true);
            return;
        }

        int excludeId = isEditMode ? currentPhong.getMaPhong() : -1;
        if (PhongBLL.isSoPhongExists(soPhong, excludeId)) {
            Others.showAlert(mainPane, "Số phòng này đã tồn tại!", true);
            return;
        }

        boolean success;
        if (isEditMode) {
            currentPhong.setSoPhong(soPhong);
            currentPhong.setMaLoaiPhong(loaiPhong.getMaLoaiPhong());
            currentPhong.setTrangThai(cbTrangThai.getValue());
            success = PhongBLL.updatePhong(currentPhong);
        } else {
            Phong newP = new Phong(0, soPhong, loaiPhong.getMaLoaiPhong(), cbTrangThai.getValue());
            success = PhongBLL.insertPhong(newP);
        }

        if (success) {
            Others.showAlert(mainPane, "Lưu thông tin phòng thành công!", false);
            handleCancel();
        } else {
            Others.showAlert(mainPane, "Có lỗi xảy ra khi lưu vào Database!", true);
        }
    }

    @FXML
    void handleCancel() {
        ((Stage) mainPane.getScene().getWindow()).close();
    }
}