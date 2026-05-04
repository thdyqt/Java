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

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public NhanVien getNhanVien() {
        return new NhanVien (maNhanVien, hoTen, chucVu, tenDangNhap, matKhau, soDienThoai);
    }

    public void setNhanVien(int id, String name, String role, String user, String pass, String phone) {
        this.maNhanVien = id;
        this.hoTen = name;
        this.chucVu = role;
        this.tenDangNhap = user;
        this.matKhau = pass;
        this.soDienThoai = phone;
    }

    public void setNhanVien(NhanVien nv) {
        this.maNhanVien = nv.getMaNhanVien();
        this.hoTen =  nv.getHoTen();
        this.chucVu = nv.getChucVu();
        this.tenDangNhap = nv.getTenDangNhap();
        this.matKhau = nv.getMatKhau();
        this.soDienThoai = nv.getSoDienThoai();
    }

    public void clearSession() {
        this.maNhanVien = -1;
        this.hoTen = null;
        this.chucVu = null;
        this.tenDangNhap = null;
        this.matKhau = null;
        this.soDienThoai = null;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}
