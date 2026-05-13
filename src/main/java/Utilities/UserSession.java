package Utilities;

import EntitiesDTO.NhanVien;

public class UserSession {
    private static UserSession instance;
    private int maNhanVien;
    private String hoTen;
    private String chucVu;
    private String tenDangNhap;
    private String matKhau;
    private String soDienThoai;
    private NhanVien.TrangThaiNhanVien trangThai;

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public NhanVien getNhanVien() {
        return new NhanVien(maNhanVien, hoTen, chucVu, tenDangNhap, matKhau, soDienThoai, trangThai);
    }

    public void setNhanVien(NhanVien nv) {
        this.maNhanVien = nv.getMaNhanVien();
        this.hoTen = nv.getHoTen();
        this.chucVu = nv.getChucVu();
        this.tenDangNhap = nv.getTenDangNhap();
        this.matKhau = nv.getMatKhau();
        this.soDienThoai = nv.getSoDienThoai();
        this.trangThai = nv.getTrangThai();
    }

    public void clearSession() {
        this.maNhanVien = -1;
        this.hoTen = null;
        this.chucVu = null;
        this.tenDangNhap = null;
        this.matKhau = null;
        this.soDienThoai = null;
        this.trangThai = null;
    }

    public int getMaNhanVien() { return maNhanVien; }
    public String getHoTen() { return hoTen; }
    public String getChucVu() { return chucVu; }
    public String getTenDangNhap() { return tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public String getSoDienThoai() { return soDienThoai; }
    public NhanVien.TrangThaiNhanVien getTrangThai() { return trangThai; }

    public static void setInstance(UserSession instance) {
        UserSession.instance = instance;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public void setTrangThai(NhanVien.TrangThaiNhanVien trangThai) {
        this.trangThai = trangThai;
    }
}