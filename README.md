# 🛒 MiniMart GUI - Java Desktop Application

Aplikasi e-commerce dan toko online desktop berbasis **Java Swing**, **Hibernate 6 ORM (JPA)**, dan **MySQL** yang dirancang secara profesional dengan arsitektur **OOP (MVC / DAO pattern)** untuk dioperasikan pada **Apache NetBeans**.

---

## 📋 Fitur Utama Sistem

1. **Sistem Autentikasi & Multi-Role:**
   - **Admin:** Memiliki hak akses penuh untuk mengelola katalog produk dan memantau riwayat transaksi toko.
   - **Customer:** Memiliki hak akses untuk menjelajahi katalog, mencari produk, melihat detail produk, mengelola keranjang belanja, dan melakukan checkout.

2. **Manajemen Produk (CRUD oleh Admin):**
   - Tambah produk baru (Nama, Kategori, Harga, Stok, Deskripsi, Foto).
   - Ubah/Edit produk yang dipilih dari tabel.
   - Hapus produk dengan dialog konfirmasi.
   - Unggah & preview foto produk lokal.

3. **Pencarian Produk Dinamis (HQL `LIKE`):**
   - Cari produk secara fleksibel berdasarkan nama, kategori, atau deskripsi menggunakan Hibernate Query Language (HQL).
   - Filter dropdown kategori (Makanan, Minuman, Sembako, Kebutuhan Rumah, dll).

4. **Katalog & Detail Lengkap Produk:**
   - Tampilan katalog terformat rapi dalam mata uang Rupiah.
   - Dialog modal informasi detail produk beserta foto dan status ketersediaan stok.

5. **Keranjang Belanja (Cart) & Simulasi Checkout:**
   - Penambahan produk ke keranjang dengan validasi batas stok.
   - Kontrol kuantitas langsung (+, -, hapus item, kosongkan keranjang).
   - Perhitungan total belanja otomatis.
   - Input pembayaran dengan kalkulasi kembalian otomatis.
   - **Hibernate Transaction (ACID):** Memproses checkout secara aman dan otomatis memotong stok barang di MySQL.
   - Pencetakan Struk / Faktur digital selesai checkout.

---

## 🗄️ Konfigurasi Hibernate ORM (`hibernate.cfg.xml`)

File konfigurasi Hibernate terletak di: `src/main/resources/hibernate.cfg.xml`
- Driver: `com.mysql.cj.jdbc.Driver`
- Dialect: `org.hibernate.dialect.MySQLDialect`
- Auto Schema Update: `hibernate.hbm2ddl.auto = update` (Hibernate otomatis membuat atau memperbarui tabel database sesuai anotasi `@Entity`).

---

## 📁 Struktur Paket (Architecture)

```
com.minimart.minimartgui
├── connection/
│   ├── DatabaseConnection.java     # JDBC Singleton & Inisialisasi Database
│   └── HibernateUtil.java          # Hibernate SessionFactory Singleton
├── model/
│   ├── User.java                   # Entity Pengguna (@Entity, @Table, @Id)
│   ├── Product.java                # Entity Produk (@Entity, @Table, @Id)
│   ├── CartItem.java               # Model Item Keranjang Belanja
│   ├── Transaction.java            # Entity Transaksi (@Entity, @Table, @Id)
│   └── TransactionDetail.java      # Entity Rincian Item Transaksi (@Entity, @Table, @Id)
├── dao/
│   ├── UserDAO.java                # Autentikasi Login & Registrasi via HQL
│   ├── ProductDAO.java             # CRUD & Pencarian HQL via Hibernate Session
│   └── TransactionDAO.java         # Hibernate Transaction Checkout & Pengurangan Stok
├── view/
│   ├── LoginForm.java              # Tampilan Form Login & Register
│   ├── AdminDashboardFrame.java    # Panel Manajemen Admin (CRUD & Riwayat)
│   ├── CustomerDashboardFrame.java # Katalog Belanja, Keranjang & Checkout
│   └── ProductDetailDialog.java    # Modal Dialog Rincian Produk
└── MiniMartGUI.java                # Entry point utama aplikasi
```

---

## 🚀 Panduan Menjalankan di Apache NetBeans

1. **Nyalakan Database MySQL:**
   - Buka **XAMPP Control Panel**, lalu klik **Start** pada modul **MySQL**.

2. **Jalankan Aplikasi:**
   - Di NetBeans, tekan tombol **F6** atau klik kanan `MiniMartGUI.java` > **Run File**.
   - Saat muncul dialog *Select Main Class*, pilih:
     `com.minimart.minimartgui.MiniMartGUI`
   - Jendela Login akan muncul dengan tombol demo Admin dan Customer.
