package EntitiesDTO;

import java.time.LocalDateTime;

public class HoaDon {
    private int maHoaDon;
    private int maDatPhong;
    private int maNhanVien;
    private LocalDateTime ngayThanhToan;
    private double tongTienPhong;
    private double tongTienDichVu;
    private double phuThu;
    private double giamGia;
    private double tongThanhToan;
    private String phuongThucThanhToan;

    public HoaDon() {}

    public HoaDon(int maHoaDon, int maDatPhong, int maNhanVien, LocalDateTime ngayThanhToan, double tongTienPhong, double tongTienDichVu, double phuThu, double giamGia, double tongThanhToan, String phuongThucThanhToan) {
        this.maHoaDon = maHoaDon;
        this.maDatPhong = maDatPhong;
        this.maNhanVien = maNhanVien;
        this.ngayThanhToan = ngayThanhToan;
        this.tongTienPhong = tongTienPhong;
        this.tongTienDichVu = tongTienDichVu;
        this.phuThu = phuThu;
        this.giamGia = giamGia;
        this.tongThanhToan = tongThanhToan;
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public int getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(int maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public int getMaDatPhong() {
        return maDatPhong;
    }

    public void setMaDatPhong(int maDatPhong) {
        this.maDatPhong = maDatPhong;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public double getTongTienPhong() {
        return tongTienPhong;
    }

    public void setTongTienPhong(double tongTienPhong) {
        this.tongTienPhong = tongTienPhong;
    }

    public double getTongTienDichVu() {
        return tongTienDichVu;
    }

    public void setTongTienDichVu(double tongTienDichVu) {
        this.tongTienDichVu = tongTienDichVu;
    }

    public double getPhuThu() {
        return phuThu;
    }

    public void setPhuThu(double phuThu) {
        this.phuThu = phuThu;
    }

    public double getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(double giamGia) {
        this.giamGia = giamGia;
    }

    public double getTongThanhToan() {
        return tongThanhToan;
    }

    public void setTongThanhToan(double tongThanhToan) {
        this.tongThanhToan = tongThanhToan;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }
}