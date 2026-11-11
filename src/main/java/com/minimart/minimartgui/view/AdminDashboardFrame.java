package com.minimart.minimartgui.view;

import com.minimart.minimartgui.dao.ProductDAO;
import com.minimart.minimartgui.dao.TransactionDAO;
import com.minimart.minimartgui.model.Product;
import com.minimart.minimartgui.model.Transaction;
import com.minimart.minimartgui.model.TransactionDetail;
import com.minimart.minimartgui.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Dashboard Admin untuk manajemen produk (CRUD) dan pemantauan transaksi.
 */
public class AdminDashboardFrame extends JFrame {

    private final User currentUser;
    private final ProductDAO productDAO = new ProductDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    // Komponen Form Produk
    private JTextField txtId;
    private JTextField txtName;
    private JComboBox<String> cmbCategory;
    private JTextField txtPrice;
    private JSpinner spStock;
    private JTextArea txtDescription;
    private JTextField txtImagePath;
    private JLabel lblImagePreview;

    // Tombol CRUD
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnBrowseImage;

    // Tabel Produk
    private JTable tblProducts;
    private DefaultTableModel productTableModel;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;

    // Tabel Riwayat Transaksi
    private JTable tblTransactions;
    private DefaultTableModel transactionTableModel;
    private JButton btnRefreshTrans;
    private JButton btnViewTransDetail;

    public AdminDashboardFrame(User user) {
        this.currentUser = user;
        setTitle("MiniMart - Panel Administrator [" + (user != null ? user.getFullname() : "Admin") + "]");
        setSize(1100, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        loadCategoryCombo();
        loadProductTable(productDAO.getAllProducts());
        loadTransactionTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 1. Header Bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 44, 97));
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("🛒 MiniMart - Admin Management Console");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        JLabel lblUser = new JLabel("Halo, " + (currentUser != null ? currentUser.getFullname() : "Admin") + " (Admin)");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(new Color(223, 230, 233));
        headerRight.add(lblUser);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(235, 77, 75));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> logout());
        headerRight.add(btnLogout);

        headerPanel.add(headerRight, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Tabbed Pane: Tab 1 = Manajemen Produk, Tab 2 = Laporan Transaksi
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel productPanel = createProductManagementPanel();
        JPanel transactionPanel = createTransactionReportPanel();

        tabbedPane.addTab("📦 Manajemen Data Produk (CRUD)", productPanel);
        tabbedPane.addTab("🧾 Riwayat & Laporan Transaksi", transactionPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createProductManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Kiri: Form Input Produk
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Form Input & Kelola Produk",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(44, 62, 80)
        ));
        formPanel.setPreferredSize(new Dimension(380, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ID Produk (Read Only)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("ID Produk:"), gbc);
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(236, 240, 241));
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtId, gbc);
        row++;

        // Nama Produk
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Nama Produk:*"), gbc);
        txtName = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtName, gbc);
        row++;

        // Kategori
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Kategori:*"), gbc);
        cmbCategory = new JComboBox<>(new String[]{"Makanan", "Minuman", "Sembako", "Kebutuhan Rumah", "Lainnya"});
        cmbCategory.setEditable(true);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(cmbCategory, gbc);
        row++;

        // Harga
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Harga (Rp):*"), gbc);
        txtPrice = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtPrice, gbc);
        row++;

        // Stok
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Stok (Unit):*"), gbc);
        spStock = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(spStock, gbc);
        row++;

        // Deskripsi
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Deskripsi:"), gbc);
        txtDescription = new JTextArea(3, 15);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(scrollDesc, gbc);
        row++;

        // Path Gambar + File Chooser
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Foto/Gambar:"), gbc);
        JPanel imgPathPanel = new JPanel(new BorderLayout(4, 0));
        txtImagePath = new JTextField();
        btnBrowseImage = new JButton("Pilih...");
        btnBrowseImage.addActionListener(e -> browseImage());
        imgPathPanel.add(txtImagePath, BorderLayout.CENTER);
        imgPathPanel.add(btnBrowseImage, BorderLayout.EAST);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(imgPathPanel, gbc);
        row++;

        // Preview Thumbnail
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Preview:"), gbc);
        lblImagePreview = new JLabel("Tidak Ada Foto", SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(100, 70));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(lblImagePreview, gbc);
        row++;

        // Tombol Aksi Form (CRUD)
        JPanel actionButtonsPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        btnAdd = new JButton("➕ Tambah");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.addActionListener(e -> addProduct());

        btnUpdate = new JButton("✏️ Ubah");
        btnUpdate.setBackground(new Color(52, 152, 219));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUpdate.addActionListener(e -> updateProduct());

        btnDelete = new JButton("🗑️ Hapus");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.addActionListener(e -> deleteProduct());

        btnClear = new JButton("🔄 Bersihkan");
        btnClear.setBackground(new Color(149, 165, 166));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClear.addActionListener(e -> clearForm());

        actionButtonsPanel.add(btnAdd);
        actionButtonsPanel.add(btnUpdate);
        actionButtonsPanel.add(btnDelete);
        actionButtonsPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 8, 8, 8);
        formPanel.add(actionButtonsPanel, gbc);

        panel.add(formPanel, BorderLayout.WEST);

        // Kanan: Pencarian dan Tabel Data Produk
        JPanel tableContainer = new JPanel(new BorderLayout(8, 8));
        tableContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Daftar Seluruh Produk MiniMart",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(44, 62, 80)
        ));

        // Search Bar Atas
        JPanel searchPanel = new JPanel(new BorderLayout(6, 6));
        searchPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel lblSearch = new JLabel("🔍 Cari Produk (Nama/Kategori): ");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchPanel.add(lblSearch, BorderLayout.WEST);

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.addActionListener(e -> searchProducts());
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        JPanel searchBtnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        btnSearch = new JButton("Cari");
        btnSearch.setBackground(new Color(41, 128, 185));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> searchProducts());

        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadProductTable(productDAO.getAllProducts());
        });

        searchBtnGroup.add(btnSearch);
        searchBtnGroup.add(btnRefresh);
        searchPanel.add(searchBtnGroup, BorderLayout.EAST);

        tableContainer.add(searchPanel, BorderLayout.NORTH);

        // Tabel Produk
        String[] columns = {"ID", "Nama Produk", "Kategori", "Harga", "Stok", "Deskripsi", "Path Gambar"};
        productTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel read-only, edit via form
            }
        };

        tblProducts = new JTable(productTableModel);
        tblProducts.setRowHeight(24);
        tblProducts.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblProducts.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Kolom width styling
        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblProducts.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblProducts.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblProducts.getColumnModel().getColumn(5).setPreferredWidth(150);
        tblProducts.getColumnModel().getColumn(6).setPreferredWidth(80);

        // Center align ID & Stok
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblProducts.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblProducts.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // Listener saat baris tabel diklik -> Isi Form secara otomatis
        tblProducts.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblProducts.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });

        JScrollPane tableScroll = new JScrollPane(tblProducts);
        tableContainer.add(tableScroll, BorderLayout.CENTER);

        panel.add(tableContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTransactionReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnRefreshTrans = new JButton("🔄 Muat Ulang Riwayat");
        btnRefreshTrans.addActionListener(e -> loadTransactionTable());
        topBar.add(btnRefreshTrans);

        btnViewTransDetail = new JButton("👁️ Lihat Detail Transaksi");
        btnViewTransDetail.setBackground(new Color(41, 128, 185));
        btnViewTransDetail.setForeground(Color.WHITE);
        btnViewTransDetail.addActionListener(e -> viewSelectedTransactionDetail());
        topBar.add(btnViewTransDetail);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "No. Faktur (Invoice)", "Pelanggan", "Total Belanja", "Uang Bayar", "Kembalian", "Tanggal Transaksi"};
        transactionTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblTransactions = new JTable(transactionTableModel);
        tblTransactions.setRowHeight(24);
        tblTransactions.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblTransactions.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(new JScrollPane(tblTransactions), BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // LOGIKA CRUD & EVENT HANDLERS
    // ==========================================

    private void loadCategoryCombo() {
        List<String> categories = productDAO.getAllCategories();
        if (categories.isEmpty()) {
            cmbCategory.setModel(new DefaultComboBoxModel<>(new String[]{"Makanan", "Minuman", "Sembako", "Kebutuhan Rumah", "Lainnya"}));
        } else {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (String c : categories) {
                model.addElement(c);
            }
            cmbCategory.setModel(model);
        }
    }

    private void loadProductTable(List<Product> products) {
        productTableModel.setRowCount(0);
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

        for (Product p : products) {
            productTableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    rupiah.format(p.getPrice()).replace(",00", ""),
                    p.getStock(),
                    p.getDescription(),
                    p.getImagePath()
            });
        }
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = tblProducts.getSelectedRow();
        if (selectedRow == -1) return;

        int id = Integer.parseInt(tblProducts.getValueAt(selectedRow, 0).toString());
        Product p = productDAO.getProductById(id);
        if (p != null) {
            txtId.setText(String.valueOf(p.getId()));
            txtName.setText(p.getName());
            cmbCategory.setSelectedItem(p.getCategory());
            txtPrice.setText(String.valueOf((long) p.getPrice()));
            spStock.setValue(p.getStock());
            txtDescription.setText(p.getDescription());
            txtImagePath.setText(p.getImagePath() != null ? p.getImagePath() : "");
            updateImagePreview(p.getImagePath());
        }
    }

    private void addProduct() {
        if (!validateInputs()) return;

        try {
            String name = txtName.getText().trim();
            String category = cmbCategory.getSelectedItem() != null ? cmbCategory.getSelectedItem().toString().trim() : "Umum";
            double price = Double.parseDouble(txtPrice.getText().trim());
            int stock = (int) spStock.getValue();
            String description = txtDescription.getText().trim();
            String imagePath = txtImagePath.getText().trim();

            Product p = new Product(name, category, price, stock, description, imagePath);
            if (productDAO.insertProduct(p)) {
                JOptionPane.showMessageDialog(this, "Produk '" + name + "' berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadProductTable(productDAO.getAllProducts());
                loadCategoryCombo();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan produk ke database.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format harga harus angka!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateProduct() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin diubah dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateInputs()) return;

        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String name = txtName.getText().trim();
            String category = cmbCategory.getSelectedItem() != null ? cmbCategory.getSelectedItem().toString().trim() : "Umum";
            double price = Double.parseDouble(txtPrice.getText().trim());
            int stock = (int) spStock.getValue();
            String description = txtDescription.getText().trim();
            String imagePath = txtImagePath.getText().trim();

            Product p = new Product(id, name, category, price, stock, description, imagePath);
            if (productDAO.updateProduct(p)) {
                JOptionPane.showMessageDialog(this, "Produk berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadProductTable(productDAO.getAllProducts());
                loadCategoryCombo();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data produk.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format harga harus angka!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteProduct() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin dihapus dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(txtId.getText().trim());
        String name = txtName.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin menghapus produk '" + name + "' (ID: " + id + ")?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (productDAO.deleteProduct(id)) {
                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadProductTable(productDAO.getAllProducts());
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus produk. Mungkin produk terkait transaksi.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
        txtPrice.setText("");
        spStock.setValue(0);
        txtDescription.setText("");
        txtImagePath.setText("");
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("Tidak Ada Foto");
        tblProducts.clearSelection();
    }

    private void searchProducts() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadProductTable(productDAO.getAllProducts());
        } else {
            List<Product> results = productDAO.searchProducts(keyword);
            loadProductTable(results);
        }
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Gambar Produk");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Gambar (JPG, PNG, JPEG, GIF)", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtImagePath.setText(selectedFile.getAbsolutePath());
            updateImagePreview(selectedFile.getAbsolutePath());
        }
    }

    private void updateImagePreview(String path) {
        if (path != null && !path.trim().isEmpty()) {
            File file = new File(path);
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(path);
                Image img = icon.getImage().getScaledInstance(100, 70, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(img));
                lblImagePreview.setText("");
                return;
            }
        }
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("Tidak Ada Foto");
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama produk tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus();
            return false;
        }
        if (txtPrice.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harga produk tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtPrice.requestFocus();
            return false;
        }
        try {
            double price = Double.parseDouble(txtPrice.getText().trim());
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Harga tidak boleh bernilai negatif!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka valid!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void loadTransactionTable() {
        transactionTableModel.setRowCount(0);
        List<Transaction> list = transactionDAO.getAllTransactions();
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        for (Transaction t : list) {
            transactionTableModel.addRow(new Object[]{
                    t.getId(),
                    t.getInvoiceNumber(),
                    t.getCustomerName(),
                    rupiah.format(t.getTotalAmount()).replace(",00", ""),
                    rupiah.format(t.getPaymentAmount()).replace(",00", ""),
                    rupiah.format(t.getChangeAmount()).replace(",00", ""),
                    t.getTransactionDate() != null ? dateFormat.format(t.getTransactionDate()) : "-"
            });
        }
    }

    private void viewSelectedTransactionDetail() {
        int selectedRow = tblTransactions.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu baris transaksi terlebih dahulu!", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int transId = (int) tblTransactions.getValueAt(selectedRow, 0);
        String invoice = tblTransactions.getValueAt(selectedRow, 1).toString();
        List<TransactionDetail> details = transactionDAO.getDetailsByTransactionId(transId);

        StringBuilder sb = new StringBuilder();
        sb.append("FAKTUR TRANSAKSI: ").append(invoice).append("\n");
        sb.append("====================================================\n");
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

        for (TransactionDetail d : details) {
            sb.append(String.format("- %s x%d @ %s = %s\n",
                    d.getProductName(),
                    d.getQuantity(),
                    rupiah.format(d.getPrice()).replace(",00", ""),
                    rupiah.format(d.getSubtotal()).replace(",00", "")));
        }
        sb.append("====================================================\n");

        JTextArea ta = new JTextArea(sb.toString(), 12, 35);
        ta.setEditable(false);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Detail Faktur " + invoice, JOptionPane.PLAIN_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout dari akun Admin?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }
}
