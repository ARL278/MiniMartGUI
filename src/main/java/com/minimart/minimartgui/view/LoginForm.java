package com.minimart.minimartgui.view;

import com.minimart.minimartgui.connection.DatabaseConnection;
import com.minimart.minimartgui.dao.UserDAO;
import com.minimart.minimartgui.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Form Autentikasi Pengguna (Login) untuk MiniMart.
 * Mengarahkan Admin ke AdminDashboardFrame dan Pelanggan ke CustomerDashboardFrame.
 */
public class LoginForm extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;

    public LoginForm() {
        setTitle("MiniMart - Masuk ke Sistem");
        setSize(440, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents();

        // Inisialisasi DB otomatis di latar belakang jika belum ada
        new Thread(DatabaseConnection::autoInitDatabase).start();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header Banner Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 44, 97));
        headerPanel.setBorder(new EmptyBorder(22, 20, 22, 20));

        JLabel lblTitle = new JLabel("🛒 MiniMart System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Aplikasi Toko & E-Commerce Digital", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(223, 230, 233));

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Input Panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(20, 35, 10, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Label Form
        JLabel lblFormTitle = new JLabel("Silakan Masuk ke Akun Anda");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        centerPanel.add(lblFormTitle, gbc);
        row++;

        // Username
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        centerPanel.add(lblUser, gbc);
        row++;

        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(0, 32));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        centerPanel.add(txtUsername, gbc);
        row++;

        // Password
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        centerPanel.add(lblPass, gbc);
        row++;

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(0, 32));
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        centerPanel.add(txtPassword, gbc);
        row++;

        // Tombol Login
        btnLogin = new JButton("Masuk Sekarang");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(0, 36));
        btnLogin.addActionListener(e -> performLogin());
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 6, 6, 6);
        centerPanel.add(btnLogin, gbc);
        row++;

        // Tombol Register
        btnRegister = new JButton("Belum punya akun? Daftar Customer");
        btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnRegister.setForeground(new Color(41, 128, 185));
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> showRegisterDialog());
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(2, 6, 8, 6);
        centerPanel.add(btnRegister, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Demo Quick-Login Panel
        JPanel demoPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        demoPanel.setBackground(new Color(245, 247, 250));
        demoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 224, 230)),
                new EmptyBorder(8, 15, 10, 15)
        ));

        JLabel lblHint = new JLabel("💡 Akun Demo Bawaan Database:", SwingConstants.CENTER);
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(Color.GRAY);
        demoPanel.add(lblHint);

        JPanel btnQuickPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnQuickPanel.setOpaque(false);

        JButton btnQuickAdmin = new JButton("Admin (admin/123123)");
        btnQuickAdmin.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnQuickAdmin.addActionListener(e -> {
            txtUsername.setText("admin");
            txtPassword.setText("123123");
        });

        JButton btnQuickCustomer = new JButton("Customer (customer/123123)");
        btnQuickCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnQuickCustomer.addActionListener(e -> {
            txtUsername.setText("customer");
            txtPassword.setText("123123");
        });

        btnQuickPanel.add(btnQuickAdmin);
        btnQuickPanel.add(btnQuickCustomer);
        demoPanel.add(btnQuickPanel);

        add(demoPanel, BorderLayout.SOUTH);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Otentikasi ke database via DAO
        User user = userDAO.login(username, password);

        // Fallback demo lokal jika database MySQL offline saat presentasi
        if (user == null && username.equals("admin") && password.equals("123123")) {
            user = new User(1, "admin", "123123", "Administrator MiniMart", "admin");
        } else if (user == null && username.equals("customer") && password.equals("123123")) {
            user = new User(2, "customer", "123123", "Pelanggan Setia", "customer");
        }

        if (user != null) {
            JOptionPane.showMessageDialog(this, 
                    "Login Berhasil!\nSelamat datang, " + user.getFullname() + " (" + user.getRole().toUpperCase() + ")", 
                    "Login Sukses", JOptionPane.INFORMATION_MESSAGE);

            this.dispose(); // Tutup form login

            final User loggedInUser = user;
            SwingUtilities.invokeLater(() -> {
                if (loggedInUser.isAdmin()) {
                    new AdminDashboardFrame(loggedInUser).setVisible(true);
                } else {
                    new CustomerDashboardFrame(loggedInUser).setVisible(true);
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, 
                    "Username atau Password salah!\nPastikan MySQL aktif dan database sudah dibuat.", 
                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegisterDialog() {
        JTextField regUser = new JTextField();
        JPasswordField regPass = new JPasswordField();
        JTextField regName = new JTextField();

        Object[] fields = {
                "Nama Lengkap:", regName,
                "Username Baru:", regUser,
                "Password:", regPass
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Pendaftaran Akun Pelanggan (Customer)", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String u = regUser.getText().trim();
            String p = new String(regPass.getPassword()).trim();
            String n = regName.getText().trim();

            if (u.isEmpty() || p.isEmpty() || n.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua data pendaftaran wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (userDAO.isUsernameTaken(u)) {
                JOptionPane.showMessageDialog(this, "Username '" + u + "' sudah dipakai! Gunakan yang lain.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User newUser = new User(u, p, n, "customer");
            if (userDAO.register(newUser)) {
                JOptionPane.showMessageDialog(this, "Pendaftaran berhasil! Silakan login menggunakan akun baru Anda.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                txtUsername.setText(u);
                txtPassword.setText(p);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mendaftar. Pastikan koneksi MySQL aktif.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
