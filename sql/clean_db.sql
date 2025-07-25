-- ===== CLEAN CUSTOMER REGISTRATION DATABASE =====
-- File: clean_customer_registration.sql
-- Jalankan dengan: psql -U postgres -f sql/clean_db.sql

-- Connect ke database customer_registration
\c customer_registration;

-- Tampilkan data sebelum dihapus (untuk konfirmasi)
SELECT 'Data sebelum dihapus:' as status;
SELECT COUNT(*) as total_customers FROM customers;
SELECT COUNT(*) as total_alamat FROM alamat;
SELECT COUNT(*) as total_wali FROM wali;

-- Hapus data customers terlebih dahulu (karena ada foreign key)
DELETE FROM customers;

-- Hapus data alamat
DELETE FROM alamat;

-- Hapus data wali
DELETE FROM wali;

-- Reset sequence untuk auto-increment ID
ALTER SEQUENCE customers_id_seq RESTART WITH 1;
ALTER SEQUENCE alamat_id_seq RESTART WITH 1;
ALTER SEQUENCE wali_id_seq RESTART WITH 1;

-- Verifikasi data sudah terhapus
SELECT 'Data setelah dihapus:' as status;
SELECT COUNT(*) as total_customers FROM customers;
SELECT COUNT(*) as total_alamat FROM alamat;
SELECT COUNT(*) as total_wali FROM wali;

SELECT '✅ Customer registration database berhasil dibersihkan!' as status;