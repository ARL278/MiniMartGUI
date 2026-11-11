package com.minimart.minimartgui.view;

import com.minimart.minimartgui.model.Product;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Dialog modal untuk menampilkan rincian lengkap sebuah produk.
 */
public class ProductDetailDialog extends JDialog {

    public ProductDetailDialog(Frame parent, Product product) {
        super(parent, "Detail Produk - " + product.getName(), true);
        initComponents(product);
        setSize(480, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents(Product product) {
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        // Header Title Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(product.getName());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        JLabel lblCategoryBadge = new JLabel("  " + product.getCategory() + "  ");
        lblCategoryBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCategoryBadge.setForeground(new Color(41, 128, 185));
        lblCategoryBadge.setOpaque(true);
        lblCategoryBadge.setBackground(Color.WHITE);
        lblCategoryBadge.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4, true));
        headerPanel.add(lblCategoryBadge, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 15));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Image Preview Panel
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(180, 140));
        lblImage.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 2, 4, 2, true));

        boolean imageLoaded = false;
        if (product.getImagePath() != null && !product.getImagePath().trim().isEmpty()) {
            File imgFile = new File(product.getImagePath());
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(product.getImagePath());
                Image scaled = icon.getImage().getScaledInstance(180, 140, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(scaled));
                imageLoaded = true;
            }
        }
        if (!imageLoaded) {
            lblImage.setText("<html><center>🛒<br><font color='#888888'>Foto Produk Tidak Tersedia</font></center></html>");
            lblImage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        contentPanel.add(lblImage, BorderLayout.NORTH);

        // Info Details Panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        infoPanel.setBackground(Color.WHITE);

        JLabel lblPrice = new JLabel("Harga: " + product.getFormattedPrice());
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPrice.setForeground(new Color(39, 174, 96));

        JLabel lblStock = new JLabel("Ketersediaan Stok: " + product.getStock() + " unit");
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        if (product.getStock() <= 5) {
            lblStock.setForeground(new Color(231, 76, 60));
            lblStock.setText(lblStock.getText() + " (Stok Menipis!)");
        } else {
            lblStock.setForeground(new Color(52, 73, 94));
        }

        JLabel lblId = new JLabel("Kode / ID Produk: #" + product.getId());
        lblId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblId.setForeground(Color.GRAY);

        JLabel lblDescTitle = new JLabel("Deskripsi Produk:");
        lblDescTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDescTitle.setForeground(new Color(44, 62, 80));

        infoPanel.add(lblPrice);
        infoPanel.add(lblStock);
        infoPanel.add(lblId);
        infoPanel.add(lblDescTitle);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(infoPanel, BorderLayout.NORTH);

        JTextArea txtDesc = new JTextArea(product.getDescription() != null && !product.getDescription().isEmpty() 
                ? product.getDescription() : "Tidak ada deskripsi untuk produk ini.");
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setBackground(new Color(248, 249, 250));
        txtDesc.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        centerPanel.add(scrollDesc, BorderLayout.CENTER);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // Footer Button Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        footerPanel.setBackground(new Color(245, 247, 250));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 229, 233)));

        JButton btnClose = new JButton("Tutup");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(new Color(41, 128, 185));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(100, 34));
        btnClose.addActionListener(e -> dispose());

        footerPanel.add(btnClose);
        add(footerPanel, BorderLayout.SOUTH);
    }
}
