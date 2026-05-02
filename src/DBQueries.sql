CREATE DATABASE IF NOT EXISTS QuanLyKhachSan;
USE QuanLyKhachSan;

-- 1. Bảng Loại Phòng
CREATE TABLE LoaiPhong (
    MaLoaiPhong INT AUTO_INCREMENT PRIMARY KEY,
    TenLoaiPhong VARCHAR(100) NOT NULL,
    DonGia DECIMAL(15, 2) NOT NULL,
    SoNguoiToiDa INT DEFAULT 2,
    MoTa TEXT
);

-- 2. Bảng Phòng
CREATE TABLE Phong (
    MaPhong INT AUTO_INCREMENT PRIMARY KEY,
    SoPhong VARCHAR(10) NOT NULL UNIQUE,
    MaLoaiPhong INT,
    TrangThai ENUM('Trống', 'Đang có khách', 'Đang dọn dẹp', 'Bảo trì') DEFAULT 'Trống',
    FOREIGN KEY (MaLoaiPhong) REFERENCES LoaiPhong(MaLoaiPhong) ON DELETE SET NULL
);

-- 3. Bảng Khách Hàng
CREATE TABLE KhachHang (
    MaKhachHang INT AUTO_INCREMENT PRIMARY KEY,
    HoTen VARCHAR(100) NOT NULL,
    CCCD_Passport VARCHAR(20) NOT NULL UNIQUE,
    SoDienThoai VARCHAR(15),
    Email VARCHAR(100),
    DiaChi TEXT
);

-- 4. Bảng Nhân Viên
CREATE TABLE NhanVien (
    MaNhanVien INT AUTO_INCREMENT PRIMARY KEY,
    HoTen VARCHAR(100) NOT NULL,
    ChucVu VARCHAR(50),
    TenDangNhap VARCHAR(50) UNIQUE,
    MatKhau VARCHAR(255),
    SoDienThoai VARCHAR(15)
);

-- 5. Bảng Đặt Phòng (Header)
CREATE TABLE DatPhong (
    MaDatPhong INT AUTO_INCREMENT PRIMARY KEY,
    MaKhachHang INT,
    MaNhanVien INT,
    NgayDat DATETIME DEFAULT CURRENT_TIMESTAMP,
    NgayCheckInDuKien DATETIME,
    NgayCheckOutDuKien DATETIME,
    TienCoc DECIMAL(15, 2) DEFAULT 0,
    TrangThai ENUM('Chờ xác nhận', 'Đã xác nhận', 'Đang ở', 'Đã trả phòng', 'Đã hủy') DEFAULT 'Chờ xác nhận',
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

-- 6. Bảng Chi Tiết Đặt Phòng (Lưu từng phòng cụ thể)
CREATE TABLE ChiTietDatPhong (
    MaChiTiet INT AUTO_INCREMENT PRIMARY KEY,
    MaDatPhong INT,
    MaPhong INT,
    GiaThucTe DECIMAL(15, 2),
    FOREIGN KEY (MaDatPhong) REFERENCES DatPhong(MaDatPhong) ON DELETE CASCADE,
    FOREIGN KEY (MaPhong) REFERENCES Phong(MaPhong)
);

-- 7. Bảng Dịch Vụ
CREATE TABLE DichVu (
    MaDichVu INT AUTO_INCREMENT PRIMARY KEY,
    TenDichVu VARCHAR(100) NOT NULL,
    DonGia DECIMAL(15, 2) NOT NULL
);

-- 8. Bảng Sử Dụng Dịch Vụ
CREATE TABLE SuDungDichVu (
    MaSuDung INT AUTO_INCREMENT PRIMARY KEY,
    MaDatPhong INT,
    MaDichVu INT,
    SoLuong INT DEFAULT 1,
    ThoiGianSuDung DATETIME DEFAULT CURRENT_TIMESTAMP,
    ThanhTien DECIMAL(15, 2),
    FOREIGN KEY (MaDatPhong) REFERENCES DatPhong(MaDatPhong),
    FOREIGN KEY (MaDichVu) REFERENCES DichVu(MaDichVu)
);

-- 9. Bảng Hóa Đơn
CREATE TABLE HoaDon (
    MaHoaDon INT AUTO_INCREMENT PRIMARY KEY,
    MaDatPhong INT UNIQUE,
    MaNhanVien INT,
    NgayThanhToan DATETIME DEFAULT CURRENT_TIMESTAMP,
    TongTienPhong DECIMAL(15, 2),
    TongTienDichVu DECIMAL(15, 2),
    PhuThu DECIMAL(15, 2) DEFAULT 0,
    GiamGia DECIMAL(15, 2) DEFAULT 0,
    TongThanhToan DECIMAL(15, 2),
    PhuongThucThanhToan VARCHAR(50),
    FOREIGN KEY (MaDatPhong) REFERENCES DatPhong(MaDatPhong),
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);