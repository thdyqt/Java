package EntitiesDTO;

import java.time.LocalDateTime;

public class SuDungDichVu {
    private int maSuDung;
    private int maDatPhong;
    private int maDichVu;
    private int soLuong;
    private LocalDateTime thoiGianSuDung;
    private double thanhTien;

    private String tenDichVu;

    public SuDungDichVu() {}

    public SuDungDichVu(int maSuDung, int maDatPhong, int maDichVu, int soLuong, LocalDateTime thoiGianSuDung, double thanhTien) {
        this.maSuDung = maSuDung;
        this.maDatPhong = maDatPhong;
        this.maDichVu = maDichVu;
        this.soLuong = soLuong;
        this.thoiGianSuDung = thoiGianSuDung;
        this.thanhTien = thanhTien;
    }

    public int getMaSuDung() {
        return maSuDung;
    }

    public void setMaSuDung(int maSuDung) {
        this.maSuDung = maSuDung;
    }

    public int getMaDatPhong() {
        return maDatPhong;
    }

    public void setMaDatPhong(int maDatPhong) {
        this.maDatPhong = maDatPhong;
    }

    public int getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(int maDichVu) {
        this.maDichVu = maDichVu;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public LocalDateTime getThoiGianSuDung() {
        return thoiGianSuDung;
    }

    public void setThoiGianSuDung(LocalDateTime thoiGianSuDung) {
        this.thoiGianSuDung = thoiGianSuDung;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }
}