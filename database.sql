-- ==========================================================
-- SKRIP DATABASE MINIMART
-- Database: minimart_db
-- Dibuat untuk: Aplikasi Desktop MiniMart GUI (Java Swing + JDBC)
-- ==========================================================

CREATE DATABASE IF NOT EXISTS `minimart_db` 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `minimart_db`;

-- 1. TABEL PENGGUNA (USERS)
-- Menyimpan kredensial dan role (admin / customer)
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `fullname` VARCHAR(100) NOT NULL,
    `role` ENUM('admin', 'customer') NOT NULL DEFAULT 'customer',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. TABEL PRODUK (PRODUCTS)
-- Menyimpan katalog barang di MiniMart
CREATE TABLE IF NOT EXISTS `products` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(150) NOT NULL,
    `category` VARCHAR(50) NOT NULL,
    `price` DOUBLE NOT NULL,
    `stock` INT NOT NULL DEFAULT 0,
    `description` TEXT,
    `image_path` VARCHAR(255) DEFAULT '',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. TABEL TRANSAKSI (TRANSACTIONS)
-- Menyimpan ringkasan order / checkout pelanggan
CREATE TABLE IF NOT EXISTS `transactions` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `invoice_number` VARCHAR(50) NOT NULL UNIQUE,
    `user_id` INT NULL,
    `customer_name` VARCHAR(100) NOT NULL,
    `total_amount` DOUBLE NOT NULL,
    `payment_amount` DOUBLE NOT NULL,
    `change_amount` DOUBLE NOT NULL,
    `transaction_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_transactions_user` FOREIGN KEY (`user_id`) 
        REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. TABEL DETAIL TRANSAKSI (TRANSACTION_DETAILS)
-- Menyimpan rincian item tiap produk dalam satu transaksi
CREATE TABLE IF NOT EXISTS `transaction_details` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `transaction_id` INT NOT NULL,
    `product_id` INT NULL,
    `product_name` VARCHAR(150) NOT NULL,
    `price` DOUBLE NOT NULL,
    `quantity` INT NOT NULL,
    `subtotal` DOUBLE NOT NULL,
    CONSTRAINT `fk_details_transaction` FOREIGN KEY (`transaction_id`) 
        REFERENCES `transactions`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_details_product` FOREIGN KEY (`product_id`) 
        REFERENCES `products`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- DATA AWAL (SEED DATA)
-- ==========================================================

-- Data Akun Pengguna:
-- Admin: username 'admin', password '123123'
-- Customer: username 'customer', password '123123'
-- Customer 2: username 'budi', password '123123'
INSERT INTO `users` (`username`, `password`, `fullname`, `role`) VALUES
('admin', '123123', 'Administrator MiniMart', 'admin'),
('customer', '123123', 'Pelanggan Setia', 'customer'),
('budi', '123123', 'Budi Santoso', 'customer')
ON DUPLICATE KEY UPDATE `fullname` = VALUES(`fullname`);

-- Data Awal Produk MiniMart
INSERT INTO `products` (`name`, `category`, `price`, `stock`, `description`, `image_path`) VALUES
('Beras Premium 5kg', 'Sembako', 68000, 50, 'Beras pulen pilihan kualitas super keluarga.', ''),
('Minyak Goreng 2L', 'Sembako', 34000, 40, 'Minyak goreng kelapa sawit higienis dan jernih.', ''),
('Gula Pasir 1kg', 'Sembako', 17500, 60, 'Gula pasir kristal murni manis alami.', ''),
('Susu UHT Cokelat 1L', 'Minuman', 19500, 35, 'Susu segar rasa cokelat kaya kalsium dan vitamin.', ''),
('Teh Botol Kotak 250ml', 'Minuman', 4500, 100, 'Minuman teh melati asli penyegar dahaga.', ''),
('Air Mineral 600ml', 'Minuman', 3500, 120, 'Air mineral pegunungan segar dan murni.', ''),
('Kopi Instan 10 Sachet', 'Minuman', 15000, 45, 'Kopi bubuk instan mantap dengan aroma wangi.', ''),
('Mie Instan Kuah Soto', 'Makanan', 3100, 150, 'Mie kuah instan rasa soto gurih dan nikmat.', ''),
('Biskuit Cokelat Sandwich', 'Makanan', 9500, 55, 'Biskuit renyah krim cokelat lezat untuk santai.', ''),
('Sabun Cuci Piring 750ml', 'Kebutuhan Rumah', 14000, 40, 'Formula efektif angkat lemak membandel seketika.', ''),
('Deterjen Bubuk 800g', 'Kebutuhan Rumah', 21000, 30, 'Deterjen pembersih pakaian dengan wangi semerbak.', ''),
('Shampoo Anti Ketombe 170ml', 'Kebutuhan Rumah', 26500, 25, 'Menjaga rambut bersih, segar, dan bebas ketombe.', '')
ON DUPLICATE KEY UPDATE `price` = VALUES(`price`);
