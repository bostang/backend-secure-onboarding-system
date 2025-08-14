-- create_all_tables.sql
-- Script untuk setup semua table termasuk KTP Dukcapil
-- Jalankan dengan: psql -U postgres -d customer_registration -f sql/create_all_tables.sql

-- Drop existing tables (kalau ada)
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS alamat CASCADE;
DROP TABLE IF EXISTS wali CASCADE;
DROP TABLE IF EXISTS ktp_dukcapil CASCADE;

-- Table Alamat
CREATE TABLE alamat (
    id BIGSERIAL PRIMARY KEY,
    nama_alamat VARCHAR(255) NOT NULL,
    provinsi VARCHAR(100) NOT NULL,
    kota VARCHAR(100) NOT NULL,
    kecamatan VARCHAR(100) NOT NULL,
    kelurahan VARCHAR(100) NOT NULL,
    kode_pos VARCHAR(10) NOT NULL
);

-- Table Wali
CREATE TABLE wali (
    id BIGSERIAL PRIMARY KEY,
    jenis_wali VARCHAR(50) NOT NULL,
    nama_lengkap_wali VARCHAR(255) NOT NULL,
    pekerjaan_wali VARCHAR(100) NOT NULL,
    alamat_wali VARCHAR(500) NOT NULL,
    nomor_telepon_wali VARCHAR(15) NOT NULL
);

-- Table Customers (Main table)
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    nama_lengkap VARCHAR(255) NOT NULL,
    nama_ibu_kandung VARCHAR(255) NOT NULL,
    nomor_telepon VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    tipe_akun VARCHAR(100) NOT NULL,
    tempat_lahir VARCHAR(100) NOT NULL,
    tanggal_lahir DATE NOT NULL,
    jenis_kelamin VARCHAR(20) NOT NULL,
    agama VARCHAR(50) NOT NULL,
    status_pernikahan VARCHAR(50) NOT NULL,
    pekerjaan VARCHAR(100) NOT NULL,
    sumber_penghasilan VARCHAR(100) NOT NULL,
    rentang_gaji VARCHAR(50) NOT NULL,
    tujuan_pembuatan_rekening VARCHAR(255) NOT NULL,
    kode_rekening INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email_verified BOOLEAN DEFAULT FALSE NOT NULL,
    alamat_id BIGINT,
    wali_id BIGINT,
    
    -- Foreign Keys
    CONSTRAINT fk_customer_alamat FOREIGN KEY (alamat_id) REFERENCES alamat(id),
    CONSTRAINT fk_customer_wali FOREIGN KEY (wali_id) REFERENCES wali(id),
    
    -- Check constraints
    CONSTRAINT chk_jenis_kelamin CHECK (jenis_kelamin IN ('Laki-laki', 'Perempuan'))
);

-- Table KTP Dukcapil (Single Database)
CREATE TABLE ktp_dukcapil (
    id BIGSERIAL PRIMARY KEY,
    nik VARCHAR(20) NOT NULL UNIQUE,
    nama_lengkap VARCHAR(100) NOT NULL,
    tempat_lahir VARCHAR(50) NOT NULL,
    tanggal_lahir DATE NOT NULL,
    jenis_kelamin VARCHAR(20) NOT NULL CHECK (jenis_kelamin IN ('LAKI_LAKI', 'PEREMPUAN')),
    nama_alamat TEXT NOT NULL,
    kecamatan VARCHAR(50) NOT NULL,
    kelurahan VARCHAR(50) NOT NULL,
    agama VARCHAR(20) NOT NULL CHECK (agama IN ('ISLAM', 'KRISTEN', 'BUDDHA', 'HINDU', 'KONGHUCU', 'LAINNYA')),
    status_perkawinan VARCHAR(20) NOT NULL,
    kewarganegaraan VARCHAR(20) DEFAULT 'WNI' NOT NULL,
    berlaku_hingga VARCHAR(20) DEFAULT 'SEUMUR HIDUP' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better performance
CREATE INDEX idx_customers_email ON customers(LOWER(email));
CREATE INDEX idx_customers_phone ON customers(nomor_telepon);
CREATE INDEX idx_customers_verified ON customers(email_verified);

-- Indexes untuk KTP Dukcapil
CREATE INDEX idx_ktp_nik ON ktp_dukcapil(nik);
CREATE INDEX idx_ktp_nama ON ktp_dukcapil(LOWER(nama_lengkap));
CREATE INDEX idx_ktp_nik_nama ON ktp_dukcapil(nik, LOWER(nama_lengkap));
CREATE INDEX idx_ktp_tanggal_lahir ON ktp_dukcapil(tanggal_lahir);
CREATE INDEX idx_ktp_jenis_kelamin ON ktp_dukcapil(jenis_kelamin);

-- Function for updated_at trigger
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers for updated_at
CREATE TRIGGER trigger_customers_updated_at
    BEFORE UPDATE ON customers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_ktp_updated_at
    BEFORE UPDATE ON ktp_dukcapil
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Sample data untuk alamat dan wali
INSERT INTO alamat (nama_alamat, provinsi, kota, kecamatan, kelurahan, kode_pos) 
VALUES ('Jl. Melati No. 10', 'DKI Jakarta', 'Jakarta Selatan', 'Kebayoran Baru', 'Melawai', '12160');

INSERT INTO wali (jenis_wali, nama_lengkap_wali, pekerjaan_wali, alamat_wali, nomor_telepon_wali)
VALUES ('Ayah', 'Budi Hartono', 'Pensiunan', 'Jl. Melati No. 10', '081298765432');

-- Sample data KTP Dukcapil (Data dummy realistis)
INSERT INTO ktp_dukcapil (
    nik, nama_lengkap, tempat_lahir, tanggal_lahir, jenis_kelamin,
    nama_alamat, kecamatan, kelurahan, agama, status_perkawinan
) VALUES 
-- Jakarta Area
('3175031234567890', 'John Doe', 'Jakarta', '1990-05-15', 'LAKI_LAKI',
 'Jl. Sudirman No. 123, RT 001/RW 002', 'Tanah Abang', 'Bendungan Hilir', 'ISLAM', 'BELUM KAWIN'),

('3175032345678901', 'Jane Smith', 'Jakarta', '1995-08-22', 'PEREMPUAN', 
 'Jl. Gatot Subroto No. 456, RT 003/RW 004', 'Setiabudi', 'Kuningan Timur', 'KRISTEN', 'KAWIN'),

('3175033456789012', 'Ahmad Rahman', 'Bogor', '1985-12-10', 'LAKI_LAKI',
 'Jl. Thamrin No. 789, RT 005/RW 006', 'Menteng', 'Gondangdia', 'ISLAM', 'KAWIN'),

('3175034567890123', 'Siti Nurhaliza', 'Depok', '1992-03-18', 'PEREMPUAN',
 'Jl. HR Rasuna Said No. 321, RT 007/RW 008', 'Setiabudi', 'Setiabudi', 'ISLAM', 'BELUM KAWIN'),

('3175035678901234', 'Budi Santoso', 'Jakarta', '1988-11-25', 'LAKI_LAKI',
 'Jl. Kemang Raya No. 654, RT 009/RW 010', 'Mampang Prapatan', 'Kemang', 'BUDDHA', 'KAWIN'),

-- Jakarta Barat
('3174011234567890', 'Maria Gonzales', 'Jakarta', '1993-07-14', 'PEREMPUAN',
 'Jl. Daan Mogot No. 111, RT 001/RW 001', 'Cengkareng', 'Cengkareng Barat', 'KRISTEN', 'BELUM KAWIN'),

('3174012345678901', 'Rudi Hermawan', 'Bekasi', '1987-09-30', 'LAKI_LAKI',
 'Jl. Puri Kembangan No. 222, RT 002/RW 003', 'Kebon Jeruk', 'Sukabumi Utara', 'ISLAM', 'KAWIN'),

-- Jakarta Timur  
('3175071234567890', 'Dewi Sartika', 'Jakarta', '1991-11-08', 'PEREMPUAN',
 'Jl. Raya Bekasi No. 333, RT 004/RW 005', 'Cakung', 'Cakung Barat', 'ISLAM', 'KAWIN'),

('3175072345678901', 'Andi Wijaya', 'Tangerang', '1989-04-12', 'LAKI_LAKI',
 'Jl. Kalimalang No. 444, RT 006/RW 007', 'Duren Sawit', 'Pondok Bambu', 'HINDU', 'BELUM KAWIN'),

-- Jakarta Utara
('3174051234567890', 'Linda Kusuma', 'Medan', '1994-06-20', 'PEREMPUAN',
 'Jl. Ancol Barat No. 555, RT 008/RW 009', 'Pademangan', 'Pademangan Barat', 'BUDDHA', 'BELUM KAWIN'),

-- Bandung
('3273011234567890', 'Dedi Setiawan', 'Bandung', '1986-01-25', 'LAKI_LAKI',
 'Jl. Asia Afrika No. 666, RT 010/RW 011', 'Sumur Bandung', 'Braga', 'ISLAM', 'KAWIN'),

('3273012345678901', 'Rina Melati', 'Garut', '1990-12-05', 'PEREMPUAN',
 'Jl. Dago No. 777, RT 012/RW 013', 'Coblong', 'Dago', 'KRISTEN', 'BELUM KAWIN'),

-- Surabaya
('3578011234567890', 'Agus Salim', 'Surabaya', '1988-08-15', 'LAKI_LAKI',
 'Jl. Pemuda No. 888, RT 014/RW 015', 'Gubeng', 'Gubeng', 'ISLAM', 'KAWIN'),

('3578012345678901', 'Ratna Dewi', 'Malang', '1992-10-28', 'PEREMPUAN',
 'Jl. Darmo No. 999, RT 016/RW 017', 'Wonokromo', 'Darmo', 'HINDU', 'BELUM KAWIN'),

-- Yogyakarta  
('3471011234567890', 'Bambang Nugroho', 'Yogyakarta', '1985-03-22', 'LAKI_LAKI',
 'Jl. Malioboro No. 101, RT 018/RW 019', 'Jetis', 'Cokrodiningratan', 'ISLAM', 'KAWIN'),

-- Test data untuk verifikasi specific
('1234567890123456', 'Test User One', 'Jakarta', '1995-01-01', 'LAKI_LAKI',
 'Jl. Test No. 123', 'Test Kecamatan', 'Test Kelurahan', 'ISLAM', 'BELUM KAWIN'),

('1234567890123457', 'Test User Two', 'Bandung', '1992-02-02', 'PEREMPUAN',
 'Jl. Test No. 456', 'Test Kecamatan', 'Test Kelurahan', 'KRISTEN', 'KAWIN');

-- Verify setup
SELECT 'All tables created successfully!' as status;
SELECT COUNT(*) as total_customers FROM customers;
SELECT COUNT(*) as total_ktp_records FROM ktp_dukcapil;
SELECT nik, nama_lengkap, tempat_lahir, jenis_kelamin FROM ktp_dukcapil LIMIT 5;