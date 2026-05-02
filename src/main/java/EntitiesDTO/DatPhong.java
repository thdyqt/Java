package EntitiesDTO;

import java.time.LocalDateTime;

public class DatPhong {
    private int maDatPhong;
    private int maKhachHang;
    private int maNhanVien;
    private LocalDateTime ngayDat;
    private LocalDateTime ngayCheckInDuKien;
    private LocalDateTime ngayCheckOutDuKien;
    private double tienCoc;
    private String trangThai;

    public DatPhong() {}

    public DatPhong(int maDatPhong, int maKhachHang, int maNhanVien, LocalDateTime ngayDat, LocalDateTime ngayCheckInDuKien, LocalDateTime ngayCheckOutDuKien, double tienCoc, String trangThai) {
        this.maDatPhong = maDatPhong;
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.ngayDat = ngayDat;
        this.ngayCheckInDuKien = ngayCheckInDuKien;
        this.ngayCheckOutDuKien = ngayCheckOutDuKien;
        this.tienCoc = tienCoc;
        this.trangThai = trangThai;
    }

    public int getMaDatPhong() {
        return maDatPhong;
    }

    public void setMaDatPhong(int maDatPhong) {
        this.maDatPhong = maDatPhong;
    }

    public int getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(int maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public LocalDateTime getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDateTime ngayDat) {
        this.ngayDat = ngayDat;
    }

    public LocalDateTime getNgayCheckInDuKien() {
        return ngayCheckInDuKien;
    }

    public void setNgayCheckInDuKien(LocalDateTime ngayCheckInDuKien) {
        this.ngayCheckInDuKien = ngayCheckInDuKien;
    }

    public LocalDateTime getNgayCheckOutDuKien() {
        return ngayCheckOutDuKien;
    }

    public void setNgayCheckOutDuKien(LocalDateTime ngayCheckOutDuKien) {
        this.ngayCheckOutDuKien = ngayCheckOutDuKien;
    }

    public double getTienCoc() {
        return tienCoc;
    }

    public void setTienCoc(double tienCoc) {
        this.tienCoc = tienCoc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}