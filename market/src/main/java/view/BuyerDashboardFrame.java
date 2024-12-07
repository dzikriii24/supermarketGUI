package view;

import controller.ProductController;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BuyerDashboardFrame extends JFrame {
    private ProductController productController;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;

    public BuyerDashboardFrame() {
        productController = new ProductController();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Halaman Pembeli");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    
        // Panel pencarian dan sorting
        JPanel searchSortPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Cari");
        

        String[] sortOptions = {"Harga (Termurah)", "Harga (Termahal)", "Kualitas (Terbaik)", "Kualitas (Terjelek)"};
        JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);
        JButton applySort = new JButton("Terapkan Sorting");

        searchSortPanel.add(new JLabel("Cari Produk:"));
        searchSortPanel.add(searchField);
        searchSortPanel.add(searchButton);
        searchSortPanel.add(new JLabel("Urutkan:"));
        searchSortPanel.add(sortComboBox);
        searchSortPanel.add(applySort);
        add(searchSortPanel, BorderLayout.NORTH);
    
        String[] productColumns = {"ID", "Nama", "Kategori", "Harga", "Stok", "Kualitas", "Pabrikan"};
        tableModel = new DefaultTableModel(productColumns, 0);
        productTable = new JTable(tableModel);
        JScrollPane productScrollPane = new JScrollPane(productTable);

        cartTable = new JTable(cartTableModel);
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setLeftComponent(productScrollPane);
    splitPane.setRightComponent(cartScrollPane);
    splitPane.setDividerLocation(0.6);
    add(splitPane, BorderLayout.CENTER);
        
    JPanel recommendationPanel = new JPanel();
    JButton showRecommendationsButton = new JButton("Lihat Rekomendasi Produk");
    recommendationPanel.add(showRecommendationsButton);
    add(recommendationPanel, BorderLayout.SOUTH);

        // Button Kembali
        JButton backButton = new JButton("Kembali ke Login");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        recommendationPanel.add(backButton);

        // Action Listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText();
                searchProducts(keyword);
            }    
        });

        applySort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSort = (String) sortComboBox.getSelectedItem();
                sortProducts(selectedSort);
            }

        });

        JButton addToCartButton = new JButton("Tambah ke Keranjang");
JButton removeFromCartButton = new JButton("Hapus dari Keranjang");
JButton checkoutButton = new JButton("Checkout");

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSelectedProductToCart();
            }
        });
        
        removeFromCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedProductFromCart();
            }
        });
        
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCheckout();
            }
        });
        

        showRecommendationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRecommendedProducts();
            }
        });
    }

    private void searchProducts(String keyword) {
        // Implementasi pencarian produk
        // Filter tabel produk berdasarkan keyword
        tableModel.setRowCount(0);
        Object[][] allProducts = productController.getAllProductData();
        
        for (Object[] product : allProducts) {
            if (product[1].toString().toLowerCase().contains(keyword.toLowerCase())) {
                tableModel.addRow(product);
            }
        }
    }

    private void sortProducts(String sortOption) {
        // Implementasi sorting produk
        Object[][] allProducts = productController.getAllProductData();
        
        switch(sortOption) {
            case "Harga (Termurah)":
                // Sorting berdasarkan harga terendah
                java.util.Arrays.sort(allProducts, (a, b) -> 
                    Double.compare(Double.parseDouble(a[3].toString()), 
                                   Double.parseDouble(b[3].toString())));
                break;
            case "Harga (Termahal)":
                // Sorting berdasarkan harga tertinggi
                java.util.Arrays.sort(allProducts, (a, b) -> 
                    Double.compare(Double.parseDouble(b[3].toString()), 
                                   Double.parseDouble(a[3].toString())));
                break;
            case "Kualitas (Terbaik)":
                // Sorting berdasarkan kualitas tertinggi
                java.util.Arrays.sort(allProducts, (a, b) -> 
                    Double.compare(Double.parseDouble(b[5].toString()), 
                                   Double.parseDouble(a[5].toString())));
                break;
            case "Kualitas (Terjelek)":
                // Sorting berdasarkan kualitas terendah
                java.util.Arrays.sort(allProducts, (a, b) -> 
                    Double.compare(Double.parseDouble(a[5].toString()), 
                                   Double.parseDouble(b[5].toString())));
                break;
        }
        
        // Perbarui tabel dengan data yang sudah diurutkan
        tableModel.setRowCount(0);
        for (Object[] product : allProducts) {
            tableModel.addRow(product);
        }
    }

    private void addSelectedProductToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            // Ambil data produk yang dipilih
            Object[] productData = new Object[4];
            productData[0] = tableModel.getValueAt(selectedRow, 0); // ID
            productData[1] = tableModel.getValueAt(selectedRow, 1); // Nama
            productData[2] = tableModel.getValueAt(selectedRow, 3); // Harga
            
            // Minta jumlah produk
            String quantityStr = JOptionPane.showInputDialog(this, "Masukkan jumlah produk:");
            try {
                int quantity = Integer.parseInt(quantityStr);
                productData[3] = quantity;
                
                // Cek stok
                int currentStock = Integer.parseInt(tableModel.getValueAt(selectedRow, 4).toString());
                if (quantity <= currentStock) {
                    cartTableModel.addRow(productData);
                    
                    // Kurangi stok
                    tableModel.setValueAt(currentStock - quantity, selectedRow, 4);
                } else {
                    JOptionPane.showMessageDialog(this, "Stok tidak mencukupi!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Masukkan jumlah yang valid!");
            }
        }
    }

    private void removeSelectedProductFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow != -1) {
            cartTableModel.removeRow(selectedRow);
        }
    }

    private void performCheckout() {
        double totalBelanja = 0;
        
        // Hitung total belanja
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            double harga = Double.parseDouble(cartTableModel.getValueAt(i, 2).toString());
            int jumlah = Integer.parseInt(cartTableModel.getValueAt(i, 3).toString());
            totalBelanja += harga * jumlah;
        }
        
        // Minta pembayaran
        String paymentStr = JOptionPane.showInputDialog(this, 
            "Total Belanja: Rp " + String.format("%.2f", totalBelanja) + "\nMasukkan Nominal Pembayaran:");
        
        try {
            double payment = Double.parseDouble(paymentStr);
            
            if (payment >= totalBelanja) {
                double kembalian = payment - totalBelanja;
                JOptionPane.showMessageDialog(this, 
                    "Pembayaran Berhasil!\nTotal: Rp " + String.format("%.2f", totalBelanja) + 
                    "\nPembayaran: Rp " + String.format("%.2f", payment) + 
                    "\nKembalian: Rp " + String.format("%.2f", kembalian));
                
                // Kosongkan keranjang
                cartTableModel.setRowCount(0);
            } else {
                JOptionPane.showMessageDialog(this, "Pembayaran Kurang!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan nominal pembayaran yang valid!");
        }
    }

    private void showRecommendedProducts() {
        // Implementasi rekomendasi produk
        // Misalnya, tampilkan produk dengan kualitas tertinggi
        Object[][] allProducts = productController.getAllProductData();
        
        // Urutkan berdasarkan kualitas
        java.util.Arrays.sort(allProducts, (a, b) -> 
            Double.compare(Double.parseDouble(b[5].toString()), 
                           Double.parseDouble(a[5].toString())));
        
        // Ambil 5 produk teratas
        StringBuilder recommendations = new StringBuilder("Rekomendasi Produk:\n");
        for (int i = 0; i < Math.min(5, allProducts.length); i++) {
            recommendations.append(String.format("%d. %s (Kualitas: %.1f)\n", 
                i+1, allProducts[i][1], Double.parseDouble(allProducts[i][5].toString())));
        }
        
        JOptionPane.showMessageDialog(this, recommendations.toString());
    }
}