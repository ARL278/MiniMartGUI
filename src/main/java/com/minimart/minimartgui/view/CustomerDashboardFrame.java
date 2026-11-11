package com.minimart.minimartgui.view;

import com.minimart.minimartgui.dao.ProductDAO;
import com.minimart.minimartgui.dao.TransactionDAO;
import com.minimart.minimartgui.model.CartItem;
import com.minimart.minimartgui.model.Product;
import com.minimart.minimartgui.model.Transaction;
import com.minimart.minimartgui.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Dashboard & Antarmuka Katalog untuk Pelanggan (Customer).
 * Fitur: Pencarian Produk, Filter Kategori, Detail Produk, Keranjang Belanja, dan Simulasi Checkout.
 */
public class CustomerDashboardFrame extends JFrame {

    private final User currentUser;
    private final ProductDAO productDAO = new ProductDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    // Data Keranjang Belanja
    private final List<CartItem> cartList = new ArrayList<>();

    // Komponen Katalog
    private JTextField txtSearch;
    private JComboBox<String> cmbCategoryFilter;
    private JTable tblCatalog;
    private DefaultTableModel catalogTableModel;
    private JSpinner spQuantity;
    private JButton btnAddToCart;
    private JButton btnViewDetail;

    // Komponen Keranjang Belanja & Checkout
    private JTable tblCart;
    private DefaultTableModel cartTableModel;
    private JButton btnIncreaseQty;
    private JButton btnDecreaseQty;
    private JButton btnRemoveCartItem;
    private JButton btnClearCart;

    private JLabel lblTotalAmount;
    private JTextField txtPayment;
    private JLabel lblChangeAmount;
    private JButton btnCheckout;

    public CustomerDashboardFrame(User user) {
        this.currentUser = user;
        setTitle("MiniMart - Belanja Online Hemat & Mudah [" + (user != null ? user.getFullname() : "Pelanggan") + "]");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        loadCategories();
        loadCatalogTable(productDAO.getAllProducts());
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 1. Header Bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 130, 76)); // Hijau retail segar
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("🛒 MiniMart - Toko Belanja Digital");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        headerRight.setOpaque(false);

        JLabel lblUser = new JLabel("Selamat datang, " + (currentUser != null ? currentUser.getFullname() : "Pelanggan"));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(new Color(236, 240, 241));
        headerRight.add(lblUser);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> logout());
        headerRight.add(btnLogout);

        headerPanel.add(headerRight, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Main Content: Split Pane (Kiri = Katalog Produk, Kanan = Keranjang & Checkout)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        mainPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        mainPanel.add(createCatalogPanel());
        mainPanel.add(createCartPanel());

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Katalog Produk MiniMart",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(44, 62, 80)
        ));

        // Filter & Search Bar
        JPanel searchFilterPanel = new JPanel(new GridBagLayout());
        searchFilterPanel.setBorder(new EmptyBorder(4, 4, 8, 4));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 4, 3, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Search by Keyword (SQL LIKE)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        searchFilterPanel.add(new JLabel("Cari Produk:"), gbc);

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.addActionListener(e -> performSearch());
        gbc.gridx = 1; gbc.weightx = 0.6;
        searchFilterPanel.add(txtSearch, gbc);

        JButton btnSearch = new JButton("Cari");
        btnSearch.setBackground(new Color(41, 128, 185));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> performSearch());
        gbc.gridx = 2; gbc.weightx = 0.2;
        searchFilterPanel.add(btnSearch, gbc);

        // Row 2: Category Filter Dropdown
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        searchFilterPanel.add(new JLabel("Kategori:"), gbc);

        cmbCategoryFilter = new JComboBox<>(new String[]{"Semua Kategori"});
        cmbCategoryFilter.addActionListener(e -> performCategoryFilter());
        gbc.gridx = 1; gbc.weightx = 0.6;
        searchFilterPanel.add(cmbCategoryFilter, gbc);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbCategoryFilter.setSelectedIndex(0);
            loadCatalogTable(productDAO.getAllProducts());
        });
        gbc.gridx = 2; gbc.weightx = 0.2;
        searchFilterPanel.add(btnReset, gbc);

        panel.add(searchFilterPanel, BorderLayout.NORTH);

        // Katalog Table
        String[] columns = {"ID", "Nama Produk", "Kategori", "Harga", "Stok"};
        catalogTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblCatalog = new JTable(catalogTableModel);
        tblCatalog.setRowHeight(26);
        tblCatalog.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCatalog.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblCatalog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblCatalog.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblCatalog.getColumnModel().getColumn(1).setPreferredWidth(190);
        tblCatalog.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblCatalog.getColumnModel().getColumn(3).setPreferredWidth(95);
        tblCatalog.getColumnModel().getColumn(4).setPreferredWidth(55);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tblCatalog.getColumnModel().getColumn(0).setCellRenderer(center);
        tblCatalog.getColumnModel().getColumn(4).setCellRenderer(center);

        panel.add(new JScrollPane(tblCatalog), BorderLayout.CENTER);

        // Action Panel Bawah Katalog (Lihat Detail & Tambah ke Keranjang)
        JPanel bottomAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));

        btnViewDetail = new JButton("🔍 Lihat Detail");
        btnViewDetail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnViewDetail.addActionListener(e -> viewProductDetail());
        bottomAction.add(btnViewDetail);

        bottomAction.add(new JLabel("Qty:"));
        spQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spQuantity.setPreferredSize(new Dimension(55, 28));
        bottomAction.add(spQuantity);

        btnAddToCart = new JButton("🛒 Tambah ke Keranjang");
        btnAddToCart.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddToCart.setBackground(new Color(39, 174, 96));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFocusPainted(false);
        btnAddToCart.addActionListener(e -> addToCart());
        bottomAction.add(btnAddToCart);

        panel.add(bottomAction, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Keranjang Belanja & Checkout",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(44, 62, 80)
        ));

        // Tabel Keranjang
        String[] cols = {"Nama Produk", "Harga Satuan", "Qty", "Subtotal"};
        cartTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblCart = new JTable(cartTableModel);
        tblCart.setRowHeight(24);
        tblCart.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCart.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        tblCart.getColumnModel().getColumn(0).setPreferredWidth(170);
        tblCart.getColumnModel().getColumn(1).setPreferredWidth(85);
        tblCart.getColumnModel().getColumn(2).setPreferredWidth(45);
        tblCart.getColumnModel().getColumn(3).setPreferredWidth(90);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tblCart.getColumnModel().getColumn(2).setCellRenderer(center);

        panel.add(new JScrollPane(tblCart), BorderLayout.CENTER);

        // Panel Kontrol Item Keranjang (+, -, Hapus, Kosongkan)
        JPanel cartControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));

        btnIncreaseQty = new JButton("➕");
        btnIncreaseQty.setToolTipText("Tambah jumlah");
        btnIncreaseQty.addActionListener(e -> changeCartItemQty(1));

        btnDecreaseQty = new JButton("➖");
        btnDecreaseQty.setToolTipText("Kurangi jumlah");
        btnDecreaseQty.addActionListener(e -> changeCartItemQty(-1));

        btnRemoveCartItem = new JButton("❌ Hapus Item");
        btnRemoveCartItem.addActionListener(e -> removeCartItem());

        btnClearCart = new JButton("🗑️ Kosongkan");
        btnClearCart.addActionListener(e -> clearCart());

        cartControlPanel.add(btnIncreaseQty);
        cartControlPanel.add(btnDecreaseQty);
        cartControlPanel.add(btnRemoveCartItem);
        cartControlPanel.add(btnClearCart);

        // Bagian Bawah: Ringkasan Total & Pembayaran Checkout
        JPanel checkoutContainer = new JPanel(new BorderLayout());
        checkoutContainer.add(cartControlPanel, BorderLayout.NORTH);

        JPanel checkoutBox = new JPanel(new GridBagLayout());
        checkoutBox.setBackground(new Color(245, 247, 250));
        checkoutBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                new EmptyBorder(10, 15, 10, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Total Belanja
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        JLabel lblTotalTitle = new JLabel("Total Belanja:");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        checkoutBox.add(lblTotalTitle, gbc);

        lblTotalAmount = new JLabel("Rp 0");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalAmount.setForeground(new Color(192, 57, 43));
        gbc.gridx = 1; gbc.weightx = 0.6;
        checkoutBox.add(lblTotalAmount, gbc);

        // Uang Bayar
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPayTitle = new JLabel("Uang Pembayaran (Rp):");
        lblPayTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        checkoutBox.add(lblPayTitle, gbc);

        txtPayment = new JTextField();
        txtPayment.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtPayment.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateChange();
            }
        });
        gbc.gridx = 1;
        checkoutBox.add(txtPayment, gbc);

        // Kembalian
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblChangeTitle = new JLabel("Uang Kembalian:");
        lblChangeTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        checkoutBox.add(lblChangeTitle, gbc);

        lblChangeAmount = new JLabel("Rp 0");
        lblChangeAmount.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblChangeAmount.setForeground(new Color(39, 174, 96));
        gbc.gridx = 1;
        checkoutBox.add(lblChangeAmount, gbc);

        // Tombol Checkout
        btnCheckout = new JButton("💳 Checkout & Bayar Sekarang");
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckout.setBackground(new Color(41, 128, 185));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setPreferredSize(new Dimension(0, 38));
        btnCheckout.addActionListener(e -> processCheckout());

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 6, 4, 6);
        checkoutBox.add(btnCheckout, gbc);

        checkoutContainer.add(checkoutBox, BorderLayout.CENTER);
        panel.add(checkoutContainer, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    // LOGIKA KATALOG & KERANJANG
    // ==========================================

    private void loadCategories() {
        List<String> list = productDAO.getAllCategories();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Semua Kategori");
        for (String c : list) {
            model.addElement(c);
        }
        cmbCategoryFilter.setModel(model);
    }

    private void loadCatalogTable(List<Product> products) {
        catalogTableModel.setRowCount(0);
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

        for (Product p : products) {
            catalogTableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    rupiah.format(p.getPrice()).replace(",00", ""),
                    p.getStock()
            });
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadCatalogTable(productDAO.getAllProducts());
        } else {
            List<Product> results = productDAO.searchProducts(keyword);
            loadCatalogTable(results);
        }
    }

    private void performCategoryFilter() {
        String selected = (String) cmbCategoryFilter.getSelectedItem();
        if (selected == null || selected.equals("Semua Kategori")) {
            loadCatalogTable(productDAO.getAllProducts());
        } else {
            loadCatalogTable(productDAO.getProductsByCategory(selected));
        }
    }

    private void viewProductDetail() {
        int row = tblCatalog.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu produk dari katalog terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tblCatalog.getValueAt(row, 0);
        Product product = productDAO.getProductById(productId);
        if (product != null) {
            ProductDetailDialog dialog = new ProductDetailDialog(this, product);
            dialog.setVisible(true);
        }
    }

    private void addToCart() {
        int row = tblCatalog.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin dibeli!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tblCatalog.getValueAt(row, 0);
        Product product = productDAO.getProductById(productId);
        if (product == null) return;

        int requestedQty = (int) spQuantity.getValue();

        if (product.getStock() <= 0) {
            JOptionPane.showMessageDialog(this, "Maaf, stok produk '" + product.getName() + "' sedang habis!", "Stok Kosong", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Cek apakah produk sudah ada di keranjang
        CartItem existingItem = null;
        for (CartItem item : cartList) {
            if (item.getProduct().getId() == product.getId()) {
                existingItem = item;
                break;
            }
        }

        int currentInCart = (existingItem != null) ? existingItem.getQuantity() : 0;
        int totalWillBe = currentInCart + requestedQty;

        if (totalWillBe > product.getStock()) {
            JOptionPane.showMessageDialog(this, 
                    "Jumlah melebihi stok tersedia! Stok saat ini: " + product.getStock() 
                    + " (Sudah ada " + currentInCart + " di keranjang)",
                    "Stok Terbatas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (existingItem != null) {
            existingItem.setQuantity(totalWillBe);
        } else {
            cartList.add(new CartItem(product, requestedQty));
        }

        spQuantity.setValue(1); // Reset spinner
        refreshCartTable();
    }

    private void changeCartItemQty(int delta) {
        int row = tblCart.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item di keranjang yang ingin diubah!", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        CartItem item = cartList.get(row);
        int newQty = item.getQuantity() + delta;

        if (newQty <= 0) {
            cartList.remove(row);
        } else {
            // Cek stok terbaru di database
            Product latest = productDAO.getProductById(item.getProduct().getId());
            if (latest != null && newQty > latest.getStock()) {
                JOptionPane.showMessageDialog(this, "Jumlah melebihi stok yang tersedia (" + latest.getStock() + ")!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            item.setQuantity(newQty);
        }

        refreshCartTable();
    }

    private void removeCartItem() {
        int row = tblCart.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang ingin dihapus dari keranjang!", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        cartList.remove(row);
        refreshCartTable();
    }

    private void clearCart() {
        if (cartList.isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Kosongkan seluruh keranjang belanja?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cartList.clear();
            refreshCartTable();
        }
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        double total = 0;

        for (CartItem item : cartList) {
            cartTableModel.addRow(new Object[]{
                    item.getProduct().getName(),
                    rupiah.format(item.getProduct().getPrice()).replace(",00", ""),
                    item.getQuantity(),
                    rupiah.format(item.getSubtotal()).replace(",00", "")
            });
            total += item.getSubtotal();
        }

        lblTotalAmount.setText(rupiah.format(total).replace(",00", ""));
        calculateChange();
    }

    private double calculateTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void calculateChange() {
        double total = calculateTotal();
        String payText = txtPayment.getText().trim();
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

        if (payText.isEmpty()) {
            lblChangeAmount.setText("Rp 0");
            lblChangeAmount.setForeground(Color.GRAY);
            return;
        }

        try {
            double payment = Double.parseDouble(payText);
            double change = payment - total;
            if (change >= 0) {
                lblChangeAmount.setText(rupiah.format(change).replace(",00", ""));
                lblChangeAmount.setForeground(new Color(39, 174, 96));
            } else {
                lblChangeAmount.setText("Kurang: " + rupiah.format(Math.abs(change)).replace(",00", ""));
                lblChangeAmount.setForeground(new Color(231, 76, 60));
            }
        } catch (NumberFormatException e) {
            lblChangeAmount.setText("Input tidak valid");
            lblChangeAmount.setForeground(Color.RED);
        }
    }

    private void processCheckout() {
        if (cartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja Anda masih kosong! Silakan pilih produk terlebih dahulu.", "Keranjang Kosong", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = calculateTotal();
        String payText = txtPayment.getText().trim();
        if (payText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah uang pembayaran pelanggan!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtPayment.requestFocus();
            return;
        }

        double payment;
        try {
            payment = Double.parseDouble(payText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nominal pembayaran harus berupa angka valid!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtPayment.requestFocus();
            return;
        }

        if (payment < total) {
            JOptionPane.showMessageDialog(this, "Nominal pembayaran kurang dari total belanja!", "Pembayaran Kurang", JOptionPane.WARNING_MESSAGE);
            txtPayment.requestFocus();
            return;
        }

        double change = payment - total;

        // Buat Nomor Invoice Unik
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String invoiceNo = "INV-" + sdf.format(new Date());

        int userId = (currentUser != null) ? currentUser.getId() : 0;
        String custName = (currentUser != null) ? currentUser.getFullname() : "Pelanggan";

        Transaction transaction = new Transaction(invoiceNo, userId, custName, total, payment, change);

        // Eksekusi transaksi database (ACID)
        try {
            boolean success = transactionDAO.processCheckout(transaction, cartList);
            if (success) {
                // Tampilkan Struk Pembelian
                showReceiptDialog(transaction, new ArrayList<>(cartList));

                // Bersihkan keranjang
                cartList.clear();
                txtPayment.setText("");
                refreshCartTable();

                // Muat ulang katalog produk (stok database otomatis berkurang!)
                loadCatalogTable(productDAO.getAllProducts());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memproses transaksi:\n" + ex.getMessage(), "Error Checkout", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReceiptDialog(Transaction trans, List<CartItem> items) {
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("                MINIMART INDONESIA               \n");
        sb.append("           Struk Pembelian & Bukti Bayar          \n");
        sb.append("==================================================\n");
        sb.append(String.format("No. Faktur : %s\n", trans.getInvoiceNumber()));
        sb.append(String.format("Tanggal    : %s\n", df.format(new Date())));
        sb.append(String.format("Pelanggan  : %s\n", trans.getCustomerName()));
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-22s %5s %10s %10s\n", "Item", "Qty", "Harga", "Subtotal"));
        sb.append("--------------------------------------------------\n");

        for (CartItem item : items) {
            String name = item.getProduct().getName();
            if (name.length() > 20) name = name.substring(0, 18) + "..";
            sb.append(String.format("%-22s %5d %10s %10s\n",
                    name,
                    item.getQuantity(),
                    rupiah.format(item.getProduct().getPrice()).replace(",00", ""),
                    rupiah.format(item.getSubtotal()).replace(",00", "")));
        }

        sb.append("--------------------------------------------------\n");
        sb.append(String.format("TOTAL BELANJA : %32s\n", rupiah.format(trans.getTotalAmount()).replace(",00", "")));
        sb.append(String.format("UANG BAYAR    : %32s\n", rupiah.format(trans.getPaymentAmount()).replace(",00", "")));
        sb.append(String.format("KEMBALIAN     : %32s\n", rupiah.format(trans.getChangeAmount()).replace(",00", "")));
        sb.append("==================================================\n");
        sb.append("    Terima kasih telah berbelanja di MiniMart!    \n");
        sb.append("       Stok barang di database telah terpotong.    \n");
        sb.append("==================================================\n");

        JTextArea ta = new JTextArea(sb.toString(), 20, 42);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setBackground(new Color(254, 254, 250));

        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Struk Transaksi Selesai", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda ingin keluar dari akun?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }
}
