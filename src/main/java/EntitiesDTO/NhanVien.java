package EntitiesDTO;

public class NhanVien {
    public enum TrangThaiNhanVien {
        DANG_LAM_VIEC("Đang làm việc"),
        DA_NGHI_VIEC("Đã nghỉ việc");

        private final String text;
        TrangThaiNhanVien(String text) { this.text = text; }
        public String getText() { return text; }

        public static TrangThaiNhanVien fromString(String text) {
            for (TrangThaiNhanVien status : TrangThaiNhanVien.values()) {
                if (status.text.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            return DANG_LAM_VIEC;
        }
    }

    private int maNhanVien;
    private String hoTen;
    private String chucVu;
    private String tenDangNhap;
    private String matKhau;
    private String soDienThoai;
    private TrangThaiNhanVien trangThai;

    public NhanVien() {
        this.trangThai = TrangThaiNhanVien.DANG_LAM_VIEC;
    }

    public NhanVien(int maNhanVien, String hoTen, String chucVu, String tenDangNhap, String matKhau, String soDienThoai, TrangThaiNhanVien trangThai) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.chucVu = chucVu;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.soDienThoai = soDienThoai;
        this.trangThai = trangThai;
    }

    // --- GETTERS & SETTERS ---
    public int getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public TrangThaiNhanVien getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiNhanVien trangThai) { this.trangThai = trangThai; }
}