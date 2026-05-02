package EntitiesDTO;

import java.math.BigDecimal;

public class LoaiPhong {
    private int maLoaiPhong;
    private String tenLoaiPhong;
    private BigDecimal donGia;
    private int soNguoiToiDa;
    private String moTa;

    public LoaiPhong() {
    }

    public LoaiPhong(int maLoaiPhong, String tenLoaiPhong, BigDecimal donGia, int soNguoiToiDa, String moTa) {
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.donGia = donGia;
        this.soNguoiToiDa = soNguoiToiDa;
        this.moTa = moTa;
    }

    public int getMaLoaiPhong() { return maLoaiPhong; }
    public void setMaLoaiPhong(int maLoaiPhong) { this.maLoaiPhong = maLoaiPhong; }

    public String getTenLoaiPhong() { return tenLoaiPhong; }
    public void setTenLoaiPhong(String tenLoaiPhong) { this.tenLoaiPhong = tenLoaiPhong; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }

    public int getSoNguoiToiDa() { return soNguoiToiDa; }
    public void setSoNguoiToiDa(int soNguoiToiDa) { this.soNguoiToiDa = soNguoiToiDa; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
