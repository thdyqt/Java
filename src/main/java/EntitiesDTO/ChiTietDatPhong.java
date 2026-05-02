package EntitiesDTO;

public class ChiTietDatPhong {
    private int maChiTiet;
    private int maDatPhong;
    private int maPhong;
    private double giaThucTe;

    public ChiTietDatPhong() {}

    public ChiTietDatPhong(int maChiTiet, int maDatPhong, int maPhong, double giaThucTe) {
        this.maChiTiet = maChiTiet;
        this.maDatPhong = maDatPhong;
        this.maPhong = maPhong;
        this.giaThucTe = giaThucTe;
    }

    public int getMaChiTiet() {
        return maChiTiet;
    }

    public void setMaChiTiet(int maChiTiet) {
        this.maChiTiet = maChiTiet;
    }

    public int getMaDatPhong() {
        return maDatPhong;
    }

    public void setMaDatPhong(int maDatPhong) {
        this.maDatPhong = maDatPhong;
    }

    public int getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(int maPhong) {
        this.maPhong = maPhong;
    }

    public double getGiaThucTe() {
        return giaThucTe;
    }

    public void setGiaThucTe(double giaThucTe) {
        this.giaThucTe = giaThucTe;
    }
}