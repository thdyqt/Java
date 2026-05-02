package EntitiesDTO;

public class KhachHang {
    private int maKhachHang;
    private String hoTen;
    private String cccdPassport;
    private String soDienThoai;
    private String email;
    private String diaChi;

    public KhachHang() {}

    public KhachHang(int maKhachHang, String hoTen, String cccdPassport, String soDienThoai, String email, String diaChi) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.cccdPassport = cccdPassport;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
    }

    public int getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(int maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getCccdPassport() {
        return cccdPassport;
    }

    public void setCccdPassport(String cccdPassport) {
        this.cccdPassport = cccdPassport;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
}