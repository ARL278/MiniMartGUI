package com.minimart.minimartgui.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton / Utility class untuk mengelola koneksi JDBC ke MySQL.
 */
public class DatabaseConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "minimart_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Default XAMPP kosong

    // URL ke server MySQL (untuk create database jika belum ada)
    private static final String SERVER_URL = "jdbc:mysql://" + HOST + ":" + PORT 
            + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";

    // URL langsung ke database minimart_db
    private static final String DB_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";

    private static Connection connection = null;

    // Load JDBC Driver saat class dimuat
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] MySQL JDBC Driver tidak ditemukan: " + e.getMessage());
        }
    }

    private DatabaseConnection() {
        // Private constructor untuk menerapkan Singleton
    }

    /**
     * Mendapatkan objek koneksi ke database minimart_db.
     * Menggunakan pola Singleton dengan auto-reconnect jika koneksi tertutup.
     *
     * @return Connection JDBC aktif
     * @throws SQLException jika gagal terhubung
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            } catch (SQLException e) {
                // Jika database belum ada, coba auto-create database & skema awal
                if (e.getErrorCode() == 1049) { // 1049 = Unknown database
                    System.out.println("[DatabaseConnection] Database " + DB_NAME + " belum ada. Mencoba membuat otomatis...");
                    autoInitDatabase();
                    connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                } else {
                    throw e;
                }
            }
        }
        return connection;
    }

    /**
     * Menguji apakah koneksi ke database berhasil.
     */
    public static boolean testConnection() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Gagal terhubung ke MySQL: " + e.getMessage());
            return false;
        }
    }

    /**
     * Menginisialisasi database dan tabel secara otomatis jika belum dibuat.
     */
    public static void autoInitDatabase() {
        try (Connection serverConn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement stmt = serverConn.createStatement()) {

            // Buat Database jika belum ada
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("USE `" + DB_NAME + "`");

            // Tabel Users
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS `users` (
                    `id` INT AUTO_INCREMENT PRIMARY KEY,
                    `username` VARCHAR(50) NOT NULL UNIQUE,
                    `password` VARCHAR(255) NOT NULL,
                    `fullname` VARCHAR(100) NOT NULL,
                    `role` ENUM('admin', 'customer') NOT NULL DEFAULT 'customer',
                    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Tabel Products
            stmt.executeUpdate("""
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
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Tabel Transactions
            stmt.executeUpdate("""
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
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Tabel Transaction Details
            stmt.executeUpdate("""
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
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Insert data default users jika kosong
            stmt.executeUpdate("""
                INSERT IGNORE INTO `users` (`username`, `password`, `fullname`, `role`) VALUES
                ('admin', '123123', 'Administrator MiniMart', 'admin'),
                ('customer', '123123', 'Pelanggan Setia', 'customer'),
                ('budi', '123123', 'Budi Santoso', 'customer')
            """);

            // Insert data default produk jika kosong
            stmt.executeUpdate("""
                INSERT IGNORE INTO `products` (`id`, `name`, `category`, `price`, `stock`, `description`, `image_path`) VALUES
                (1, 'Beras Premium 5kg', 'Sembako', 68000, 50, 'Beras pulen pilihan kualitas super keluarga.', ''),
                (2, 'Minyak Goreng 2L', 'Sembako', 34000, 40, 'Minyak goreng kelapa sawit higienis dan jernih.', ''),
                (3, 'Gula Pasir 1kg', 'Sembako', 17500, 60, 'Gula pasir kristal murni manis alami.', ''),
                (4, 'Susu UHT Cokelat 1L', 'Minuman', 19500, 35, 'Susu segar rasa cokelat kaya kalsium dan vitamin.', ''),
                (5, 'Teh Botol Kotak 250ml', 'Minuman', 4500, 100, 'Minuman teh melati asli penyegar dahaga.', ''),
                (6, 'Air Mineral 600ml', 'Minuman', 3500, 120, 'Air mineral pegunungan segar dan murni.', ''),
                (7, 'Kopi Instan 10 Sachet', 'Minuman', 15000, 45, 'Kopi bubuk instan mantap dengan aroma wangi.', ''),
                (8, 'Mie Instan Kuah Soto', 'Makanan', 3100, 150, 'Mie kuah instan rasa soto gurih dan nikmat.', ''),
                (9, 'Biskuit Cokelat Sandwich', 'Makanan', 9500, 55, 'Biskuit renyah krim cokelat lezat untuk santai.', ''),
                (10, 'Sabun Cuci Piring 750ml', 'Kebutuhan Rumah', 14000, 40, 'Formula efektif angkat lemak membandel seketika.', ''),
                (11, 'Deterjen Bubuk 800g', 'Kebutuhan Rumah', 21000, 30, 'Deterjen pembersih pakaian dengan wangi semerbak.', ''),
                (12, 'Shampoo Anti Ketombe 170ml', 'Kebutuhan Rumah', 26500, 25, 'Menjaga rambut bersih, segar, dan bebas ketombe.', '')
            """);

            System.out.println("[DatabaseConnection] Inisialisasi skema database minimart_db berhasil.");
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Gagal melakukan auto-init database: " + e.getMessage());
        }
    }
}
