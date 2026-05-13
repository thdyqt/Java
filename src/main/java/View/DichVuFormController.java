package View;

import BusinessBLL.DichVuBLL;
import EntitiesDTO.DichVu;
import Utilities.Others;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DichVuFormController {
    @FXML private VBox mainPane;
    @FXML private Label lblTitle, lblStatus;
    @FXML private TextField txtTenDV, txtDonGia;
    @FXML private ComboBox<DichVu.TrangThaiDichVu> cbTrangThai;
    @FXML private RowConstraints rowStatus;
    @FXML private Button btnSave, btnCancel;

    private int editingMaDV = -1;

    @FXML
    public void initialize() {
        setupConstraints();
        setupComboBox();

        rowStatus.setPrefHeight(0);
        rowStatus.setMaxHeight(0);
        lblStatus.setVisible(false);
        cbTrangThai.setVisible(false);

        Others.playButtonAnimation(btnSave);
        Others.playButtonAnimation(btnCancel);
    }

    private void setupConstraints() {
        Others.setMaxLength(txtTenDV, 100);
        Others.setNumericOnly(txtDonGia);
    }

    private void setupComboBox() {
        cbTrangThai.getItems().setAll(DichVu.TrangThaiDichVu.values());
        cbTrangThai.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(DichVu.TrangThaiDichVu object) { return object == null ? "" : object.getText(); }
            @Override public DichVu.TrangThaiDichVu fromString(String string) { return null; }
        });
    }

    public void setDichVuData(DichVu dv) {
        if (dv == null) return;

        this.editingMaDV = dv.getMaDichVu();
        lblTitle.setText("CẬP NHẬT DỊCH VỤ");

        rowStatus.setPrefHeight(45);
        rowStatus.setMaxHeight(45);
        lblStatus.setVisible(true);
        cbTrangThai.setVisible(true);

        txtTenDV.setText(dv.getTenDichVu());
        txtDonGia.setText(String.valueOf((long)dv.getDonGia()));
        cbTrangThai.setValue(dv.getTrangThai());
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (txtTenDV.getText().trim().isEmpty() || txtDonGia.getText().trim().isEmpty()) {
            Others.showAlert(mainPane, "Vui lòng nhập đầy đủ Tên và Đơn giá!", true);
            return;
        }

        DichVu dv = new DichVu();
        dv.setTenDichVu(txtTenDV.getText().trim());
        dv.setDonGia(Double.parseDouble(txtDonGia.getText().trim()));

        boolean isSuccess;
        if (editingMaDV == -1) {
            dv.setTrangThai(DichVu.TrangThaiDichVu.DANG_BAN);
            isSuccess = DichVuBLL.insertDichVu(dv);
        } else {
            dv.setMaDichVu(editingMaDV);
            dv.setTrangThai(cbTrangThai.getValue());
            isSuccess = DichVuBLL.updateDichVu(dv);
        }

        if (isSuccess) {
            Others.showAlert(mainPane, "Lưu thông tin thành công!", false);
            ((Stage) mainPane.getScene().getWindow()).close();
        } else {
            Others.showAlert(mainPane, "Có lỗi xảy ra khi lưu vào Database!", true);
        }
    }

    @FXML void handleCancel(ActionEvent event) {
        ((Stage) mainPane.getScene().getWindow()).close();
    }
}