package com.minimart.minimartgui;

import com.minimart.minimartgui.connection.DatabaseConnection;
import com.minimart.minimartgui.connection.HibernateUtil;
import com.minimart.minimartgui.view.LoginForm;

import javax.swing.*;

/**
 * Main Application Entry Point untuk MiniMart Desktop Application.
 * Menginisialisasi Look and Feel, menguji koneksi database MySQL & Hibernate ORM,
 * dan membuka layar Login pertama kali.
 */
public class MiniMartGUI {

    public static void main(String[] args) {
        // Konfigurasi antialiasing font dan tampilan modern Nimbus / System
        try {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Jalankan background check MySQL & inisialisasi Hibernate SessionFactory
        new Thread(() -> {
            boolean connected = DatabaseConnection.testConnection();
            if (connected) {
                System.out.println("[MiniMartGUI] Berhasil terhubung ke database MySQL.");
                // Inisialisasi Hibernate SessionFactory
                HibernateUtil.getSessionFactory();
            } else {
                System.err.println("[MiniMartGUI] Peringatan: Tidak dapat terhubung ke MySQL di localhost:3306.");
                System.err.println("[MiniMartGUI] Pastikan modul MySQL di XAMPP / MariaDB sudah dinyalakan (Start).");
            }
        }).start();

        // Shutdown hook untuk menutup SessionFactory saat aplikasi ditutup
        Runtime.getRuntime().addShutdownHook(new Thread(HibernateUtil::shutdown));

        // Tampilkan Form Login pada Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
