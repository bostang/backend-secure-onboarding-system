-- create_dukcapil_tables.sql
-- Script untuk setup database dukcapil
-- Jalankan dengan: psql -U postgres -d dukcapil_db -f create_dukcapil_tables.sql

-- Create ktp_reader user
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'ktp_reader') THEN
        CREATE USER ktp_reader WITH PASSWORD 'ktp_pass123';
    END IF;
END
$$;

-- Grant basic permissions
GRANT CONNECT ON DATABASE dukcapil_db TO ktp_reader;
GRANT USAGE ON SCHEMA public TO ktp_reader;

-- Drop existing tables (kalau ada)
DROP TABLE IF EXISTS ktp_data CASCADE;

-- Table KTP_DATA untuk database dukcapil
CREATE TABLE ktp_data (
    id BIGSERIAL PRIMARY KEY,
    nik VARCHAR(20) NOT NULL UNIQUE,
    nama_lengkap VARCHAR(50) NOT NULL,
    tempat_lahir VARCHAR(20) NOT NULL,
    tanggal_lahir DATE NOT NULL,
    jenis_kelamin VARCHAR(20) NOT NULL CHECK (jenis_kelamin IN ('LAKI_LAKI', 'PEREMPUAN')),
    nama_alamat TEXT NOT NULL,
    kecamatan VARCHAR(20) NOT NULL,
    kelurahan VARCHAR(20) NOT NULL,
    agama VARCHAR(20) NOT NULL CHECK (agama IN ('ISLAM', 'KRISTEN', 'BUDDHA', 'HINDU', 'KONGHUCU', 'LAINNYA')),
    status_perkawinan VARCHAR(20) NOT NULL,
    kewarganegaraan VARCHAR(20) DEFAULT 'WNI' NOT NULL,
    berlaku_hingga VARCHAR(20) DEFAULT 'SEUMUR HIDUP' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes untuk performance
CREATE INDEX idx_ktp_nik ON ktp_data(nik);
CREATE INDEX idx_ktp_nama ON ktp_data(LOWER(nama_lengkap));
CREATE INDEX idx_ktp_nik_nama ON ktp_data(nik, LOWER(nama_lengkap));
CREATE INDEX idx_ktp_tanggal_lahir ON ktp_data(tanggal_lahir);
CREATE INDEX idx_ktp_jenis_kelamin ON ktp_data(jenis_kelamin);

-- Sample data KTP untuk testing (Data dummy realistis)
INSERT INTO ktp_data (
    nik, nama_lengkap, tempat_lahir, tanggal_lahir, jenis_kelamin,
    nama_alamat, kecamatan, kelurahan, agama, status_perkawinan
) VALUES 
-- Jakarta Selatan
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
 'Jl. Malioboro No. 101, RT 018/RW 019', 'Jetis', 'Cokrodiningratan', 'ISLAM', 'KAWIN');

-- Function untuk update timestamp
CREATE OR REPLACE FUNCTION update_ktp_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger untuk auto update timestamp
CREATE TRIGGER trigger_ktp_updated_at
    BEFORE UPDATE ON ktp_data
    FOR EACH ROW
    EXECUTE FUNCTION update_ktp_updated_at();

-- Grant permissions ke ktp_reader
GRANT SELECT, INSERT, UPDATE ON ktp_data TO ktp_reader;
GRANT USAGE, SELECT ON SEQUENCE ktp_data_id_seq TO ktp_reader;

-- Grant permissions untuk future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE ON TABLES TO ktp_reader;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO ktp_reader;

-- Verify setup
SELECT 'Dukcapil KTP Database setup completed!' as status;
SELECT COUNT(*) as total_ktp_records FROM ktp_data;
SELECT nik, nama_lengkap, tempat_lahir, jenis_kelamin FROM ktp_data LIMIT 5;