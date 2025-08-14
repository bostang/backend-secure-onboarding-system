-- 1. CREATE DUKCAPIL DATABASE
-- Jalankan sebagai postgres user

CREATE USER ktp_reader WITH PASSWORD 'ktp_pass123';
GRANT CONNECT ON DATABASE dukcapil_db TO ktp_reader;CREATE DATABASE dukcapil_db;

-- 2. CONNECT KE DUKCAPIL DATABASE DAN BUAT TABLE
-- psql -U postgres -d dukcapil_db

-- Grant permissions
GRANT USAGE ON SCHEMA public TO ktp_reader;
GRANT CREATE ON SCHEMA public TO ktp_reader;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ktp_reader;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO ktp_reader;

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

-- Sample data KTP untuk testing
INSERT INTO ktp_data (
    nik, nama_lengkap, tempat_lahir, tanggal_lahir, jenis_kelamin,
    nama_alamat, kecamatan, kelurahan, agama, status_perkawinan
) VALUES 
('3175031234567890', 'John Doe', 'Jakarta', '1990-05-15', 'LAKI_LAKI',
 'Jl. Sudirman No. 123, RT 001/RW 002', 'Tanah Abang', 'Bendungan Hilir', 'ISLAM', 'BELUM KAWIN'),

('3175032345678901', 'Jane Smith', 'Jakarta', '1995-08-22', 'PEREMPUAN', 
 'Jl. Gatot Subroto No. 456, RT 003/RW 004', 'Setiabudi', 'Kuningan Timur', 'KRISTEN', 'KAWIN'),

('3175033456789012', 'Ahmad Rahman', 'Bogor', '1985-12-10', 'LAKI_LAKI',
 'Jl. Thamrin No. 789, RT 005/RW 006', 'Menteng', 'Gondangdia', 'ISLAM', 'KAWIN'),

('3175034567890123', 'Siti Nurhaliza', 'Depok', '1992-03-18', 'PEREMPUAN',
 'Jl. HR Rasuna Said No. 321, RT 007/RW 008', 'Setiabudi', 'Setiabudi', 'ISLAM', 'BELUM KAWIN'),

('3175035678901234', 'Budi Santoso', 'Jakarta', '1988-11-25', 'LAKI_LAKI',
 'Jl. Kemang Raya No. 654, RT 009/RW 010', 'Mampang Prapatan', 'Kemang', 'BUDDHA', 'KAWIN');

-- Grant final permissions
GRANT ALL PRIVILEGES ON ktp_data TO ktp_reader;
GRANT USAGE, SELECT ON SEQUENCE ktp_data_id_seq TO ktp_reader;

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

-- 3. VERIFY DATA
SELECT 'KTP Database setup completed!' as status;
SELECT nik, nama_lengkap, tempat_lahir FROM ktp_data LIMIT 5;