CREATE DATABASE IF NOT EXISTS QuanLyKhachSan;
USE QuanLyKhachSan;

-- 1. Bảng Loại Phòng
CREATE TABLE LoaiPhong (
    MaLoaiPhong INT AUTO_INCREMENT PRIMARY KEY,
    TenLoaiPhong VARCHAR(100) NOT NULL,
    DonGia DOUBLE NOT NULL,
    SoNguoiToiDa INT DEFAULT 2,
    MoTa TEXT
);

USE quanlykhachsan;
INSERT INTO LoaiPhong (TenLoaiPhong, DonGia, SoNguoiToiDa, MoTa) VALUES
('Standard', 300000, 2, 'Phòng tiêu chuẩn 1 giường đôi, không có cửa sổ'),
('Deluxe', 500000, 2, 'Phòng cao cấp, có ban công và view thành phố'),
('Suite', 1200000, 4, 'Phòng hạng sang diện tích lớn, có phòng khách riêng');


-- 2. Bảng Phòng
CREATE TABLE Phong (
    MaPhong INT AUTO_INCREMENT PRIMARY KEY,
    SoPhong VARCHAR(10) NOT NULL UNIQUE,
    MaLoaiPhong INT,
    TrangThai ENUM('Trống', 'Đang có khách', 'Đang dọn dẹp', 'Bảo trì') DEFAULT 'Trống',
    FOREIGN KEY (MaLoaiPhong) REFERENCES LoaiPhong(MaLoaiPhong) ON DELETE SET NULL
);

USE quanlykhachsan;
INSERT INTO Phong (SoPhong, MaLoaiPhong, TrangThai) VALUES
('201', 2, 'Trống'),
('202', 2, 'Trống'),
('203', 1, 'Trống'),
('204', 1, 'Đang dọn dẹp'),
('205', 1, 'Trống'),
('301', 2, 'Trống'),
('302', 2, 'Trống'),
('303', 1, 'Trống'),
('304', 1, 'Trống'),
('305', 1, 'Trống'),
('401', 2, 'Đang dọn dẹp'),
('402', 2, 'Trống'),
('403', 1, 'Trống'),
('404', 1, 'Bảo trì'),
('405', 1, 'Bảo trì'),
('501', 3, 'Trống'),
('502', 3, 'Đang dọn dẹp'),
('503', 3, 'Bảo trì');

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

USE quanlykhachsan;
INSERT INTO NhanVien (HoTen, ChucVu, TenDangNhap, MatKhau, SoDienThoai) VALUES
('Phan Thanh Duy', 'Admin', 'thdyqt', '123456', '0905383132'),
('Đinh Huỳnh Nguyên Khang', 'Admin', 'dhnkhang', '123456', '1'),
('Nguyễn Hoàng Hiếu', 'Lễ Tân', 'nhhieu', '123456', '1');

-- 5. Bảng Đặt Phòng (Header)
CREATE TABLE DatPhong (
    MaDatPhong INT AUTO_INCREMENT PRIMARY KEY,
    MaKhachHang INT,
    MaNhanVien INT,
    NgayDat DATETIME DEFAULT CURRENT_TIMESTAMP,
    NgayCheckInDuKien DATETIME,
    NgayCheckOutDuKien DATETIME,
    TienCoc DOUBLE DEFAULT 0,
    TrangThai ENUM('Chờ nhận phòng', 'Đang ở', 'Đã trả phòng', 'Đã hủy') DEFAULT 'Chờ nhận phòng',
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

-- 6. Bảng Chi Tiết Đặt Phòng (Lưu từng phòng cụ thể)
CREATE TABLE ChiTietDatPhong (
    MaChiTiet INT AUTO_INCREMENT PRIMARY KEY,
    MaDatPhong INT,
    MaPhong INT,
    GiaThucTe DOUBLE,
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
    ThanhTien DOUBLE,
    FOREIGN KEY (MaDatPhong) REFERENCES DatPhong(MaDatPhong),
    FOREIGN KEY (MaDichVu) REFERENCES DichVu(MaDichVu)
);

-- 9. Bảng Hóa Đơn
CREATE TABLE HoaDon (
    MaHoaDon INT AUTO_INCREMENT PRIMARY KEY,
    MaDatPhong INT UNIQUE,
    MaNhanVien INT,
    NgayThanhToan DATETIME DEFAULT CURRENT_TIMESTAMP,
    TongTienPhong DOUBLE,
    TongTienDichVu DOUBLE,
    PhuThu DOUBLE DEFAULT 0,
    GiamGia DOUBLE DEFAULT 0,
    TongThanhToan DOUBLE,
    PhuongThucThanhToan VARCHAR(50),
    FOREIGN KEY (MaDatPhong) REFERENCES DatPhong(MaDatPhong),
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);