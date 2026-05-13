package EntitiesDTO;

public class DichVu {
    public enum TrangThaiDichVu {
        DANG_BAN("Đang bán"),
        NGUNG_KINH_DOANH("Ngừng kinh doanh");

        private final String text;
        TrangThaiDichVu(String text) { this.text = text; }
        public String getText() { return text; }

        public static TrangThaiDichVu fromString(String text) {
            for (TrangThaiDichVu status : TrangThaiDichVu.values()) {
                if (status.text.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            return DANG_BAN;
        }
    }

    private int maDichVu;
    private String tenDichVu;
    private double donGia;
    private TrangThaiDichVu trangThai;

    public DichVu() {
        this.trangThai = TrangThaiDichVu.DANG_BAN;
    }

    public DichVu(int maDichVu, String tenDichVu, double donGia, TrangThaiDichVu trangThai) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.donGia = donGia;
        this.trangThai = trangThai;
    }

    public int getMaDichVu() { return maDichVu; }
    public void setMaDichVu(int maDichVu) { this.maDichVu = maDichVu; }

    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public TrangThaiDichVu getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDichVu trangThai) { this.trangThai = trangThai; }
}