package view;

import controller.ProductController;
import java.awt.*;
import java.util.Arrays;
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
        JPanel searchSortPanel = createSearchSortPanel();
        add(searchSortPanel, BorderLayout.NORTH);
    
        // Grid panel untuk 3 bagian
        JPanel mainPanel = new JPanel(new GridLayout(1, 3)); // 1 baris, 3 kolom
        add(mainPanel, BorderLayout.CENTER);
    
        
        // Panel kiri: Produk
        JPanel productPanel = new JPanel(new BorderLayout());
        createProductTable();
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        mainPanel.add(productPanel);
    
        // Panel tengah: Rekomendasi Produk
        JPanel recommendationPanel = createRecommendationPanel();
        mainPanel.add(recommendationPanel);
    
        // Panel kanan: Keranjang
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartTableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Kategori", "Harga", "Jumlah"}, 0);
        cartTable = new JTable(cartTableModel);
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        mainPanel.add(cartPanel);
    
        // Panel Transaksi di bawah
        JPanel transactionPanel = createTransactionPanel();
        add(transactionPanel, BorderLayout.SOUTH);

    }

    private JPanel createRecommendationPanel() {
        JPanel recommendationPanel = new JPanel(new GridLayout(3, 1));
        recommendationPanel.setBorder(BorderFactory.createTitledBorder("Rekomendasi Produk"));
    
        // Input kategori dan tombol cari
        JPanel inputPanel = new JPanel();
        JTextField categoryField = new JTextField(15);
        JButton searchRecommendationButton = new JButton("Cari Rekomendasi");
    
        inputPanel.add(new JLabel("Kategori:"));
        inputPanel.add(categoryField);
        inputPanel.add(searchRecommendationButton);
    
        // Tabel rekomendasi
        String[] recommendationColumns = {"ID", "Nama", "Kategori", "Harga", "Stock"};
        DefaultTableModel recommendationTableModel = new DefaultTableModel(recommendationColumns, 0);
        JTable recommendationTable = new JTable(recommendationTableModel);
        JScrollPane recommendationScrollPane = new JScrollPane(recommendationTable);
    
        recommendationPanel.add(inputPanel);  // Menambahkan inputPanel ke grid pertama
        recommendationPanel.add(recommendationScrollPane);  // Menambahkan tabel ke grid kedua
    
        // Action tombol cari rekomendasi
    searchRecommendationButton.addActionListener(e -> {
    String kategori = categoryField.getText().trim();
    if (kategori.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Masukkan kategori terlebih dahulu!");
    } else {
        recommendationTableModel.setRowCount(0); // Menghapus data tabel sebelumnya
        Object[][] produkRekomendasi = productController.getTopRatedProductsByCategory(kategori, 5);
        
        if (produkRekomendasi.length > 0) {
            // Mengurutkan produk berdasarkan kualitas (terbaik di atas)
            Arrays.sort(produkRekomendasi, (a, b) -> 
                Double.compare(Double.parseDouble(b[5].toString()), Double.parseDouble(a[5].toString()))
            );
            // Menambahkan produk yang telah disortir ke dalam tabel
            for (Object[] produk : produkRekomendasi) {
                recommendationTableModel.addRow(produk);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada produk yang cocok dengan kategori " + kategori);
        }
    }
});


        return recommendationPanel;
    }

    private JPanel createTransactionPanel() {
        JPanel transactionPanel = new JPanel(new GridLayout(2, 1));

        // Panel Tambah Pesanan
        JPanel addOrderPanel = new JPanel();
        JTextField productIdOrders = new JTextField(10);
        JTextField quantityOrders = new JTextField(5);
        JButton addOrderButton = new JButton("Tambah ke Keranjang");

        addOrderPanel.add(new JLabel("ID Produk:"));
        addOrderPanel.add(productIdOrders);
        addOrderPanel.add(new JLabel("Jumlah:"));
        addOrderPanel.add(quantityOrders);
        addOrderPanel.add(addOrderButton);

        // Panel Pembayaran
        JPanel paymentPanel = new JPanel();
        JLabel totalLabel = new JLabel("Total Harga: 0");
        JTextField paymentField = new JTextField(10);
        JButton payButton = new JButton("Bayar");

        paymentPanel.add(totalLabel);
        paymentPanel.add(new JLabel("Bayar:"));
        paymentPanel.add(paymentField);
        paymentPanel.add(payButton);

        // Tambahkan panel ke transactionPanel
        transactionPanel.add(addOrderPanel);
        transactionPanel.add(paymentPanel);

        addOrderButton.addActionListener(e -> {
            String productIdOrder = productIdOrders.getText().trim();
            String quantityOrderStr = quantityOrders.getText().trim();
            if (productIdOrder.isEmpty() || quantityOrderStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Produk dan Jumlah tidak boleh kosong!");
                return;
            }

            int quantityOrder = Integer.parseInt(quantityOrderStr);
            // Mendapatkan produk berdasarkan ID
            Object[] productOrder = productController.getProductById(productIdOrder);
            if (productOrder != null) {
                // Cek apakah produk sudah ada di keranjang
                boolean productExists = false;
                for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                    String cartProductId = cartTableModel.getValueAt(i, 0).toString();
                    if (cartProductId.equals(productOrder[0].toString())) {
                        // Jika produk sudah ada, tambahkan jumlahnya dan update harga
                        int currentQuantity = Integer.parseInt(cartTableModel.getValueAt(i, 4).toString());
                        int newQuantity = currentQuantity + quantityOrder; // Tambahkan jumlah sesuai input
                        cartTableModel.setValueAt(newQuantity, i, 4);
                        double newPrice = newQuantity * Double.parseDouble(productOrder[3].toString());
                        cartTableModel.setValueAt(newPrice, i, 3);
                        productExists = true;
                        break;
                    }
                }

                if (!productExists) {
                    // Jika produk belum ada, tambahkan sebagai entri baru
                    productOrder[4] = quantityOrder; // Set jumlah produk ke keranjang
                    double price = quantityOrder * Double.parseDouble(productOrder[3].toString());
                    cartTableModel.addRow(new Object[]{productOrder[0], productOrder[1], productOrder[2], price, quantityOrder});
                }

                updateTotalPrice(totalLabel); // Memperbarui total harga
                JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan ke keranjang!");
            } else {
                JOptionPane.showMessageDialog(this, "Produk dengan ID " + productIdOrder + " tidak ditemukan!");
            }
        });

        // Action untuk Pembayaran
// Bagian ActionListener untuk pembayaran
payButton.addActionListener(e -> {
    String paymentStr = paymentField.getText();
    if (paymentStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Masukkan jumlah pembayaran!");
        return;
    }

    try {
        double payment = Double.parseDouble(paymentStr);
        double total = calculateTotalPrice();

        if (payment < total) {
            JOptionPane.showMessageDialog(this, "Pembayaran gagal! Uang tidak cukup.");
        } else {
            // Periksa stok untuk setiap produk di keranjang
            boolean outOfStock = false;
            for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                String productId = cartTableModel.getValueAt(i, 0).toString();
                int quantity = Integer.parseInt(cartTableModel.getValueAt(i, 4).toString());

                // Cek stok produk di database
                int stock = productController.getProductStock(productId);
                if (stock < quantity) {
                    JOptionPane.showMessageDialog(this, "Stok tidak cukup untuk produk ID " + productId);
                    outOfStock = true;
                    break;
                }
            }

            if (!outOfStock) {
                // Pembayaran berhasil, update stok produk
                for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                    String productId = cartTableModel.getValueAt(i, 0).toString();
                    int quantity = Integer.parseInt(cartTableModel.getValueAt(i, 4).toString());
                    productController.updateProductStockPay(productId, quantity);
                }

                double change = payment - total;
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil! Kembalian: Rp " + change);

                // Kosongkan keranjang setelah pembayaran
                cartTableModel.setRowCount(0);
                totalLabel.setText("Total Harga: 0");
            }
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Masukkan nominal pembayaran yang valid!");
    }
});
    


        return transactionPanel;
    }
    private void createProductTable() {
        String[] productColumns = {"ID", "Nama", "Kategori", "Harga", "Stok", "Kualitas"};
        tableModel = new DefaultTableModel(productColumns, 0);
        productTable = new JTable(tableModel);

        // Isi tabel dengan data produk
        Object[][] allProducts = productController.getAllProductData();
        for (Object[] product : allProducts) {
            tableModel.addRow(product);
        }
    }

    private JPanel createSearchSortPanel() {
        JPanel panel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Cari");

        String[] sortOptions = {"Harga (Termurah)", "Harga (Termahal)", "Kualitas (Terbaik)", "Kualitas (Terjelek)"};
        JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);
        JButton applySort = new JButton("Terapkan Sorting");

        panel.add(new JLabel("Cari Produk:"));
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(new JLabel("Urutkan:"));
        panel.add(sortComboBox);
        panel.add(applySort);

        searchButton.addActionListener(e -> searchProducts(searchField.getText()));
        applySort.addActionListener(e -> sortProducts((String) sortComboBox.getSelectedItem()));

        return panel;
    }

    private void updateTotalPrice(JLabel totalLabel) {
        double total = calculateTotalPrice();
        totalLabel.setText("Total Harga: Rp " + total);
    }

    private double calculateTotalPrice() {
        double total = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            total += Double.parseDouble(cartTableModel.getValueAt(i, 3).toString());
        }
        return total;
    }


    private void searchProducts(String keyword) {
        tableModel.setRowCount(0);
        Object[][] allProducts = productController.getAllProductData();

        for (Object[] product : allProducts) {
            if (product[1].toString().toLowerCase().contains(keyword.toLowerCase())) {
                tableModel.addRow(product);
            }
        }
    }

    private void sortProducts(String sortOption) {
        Object[][] allProducts = productController.getAllProductData();

        switch (sortOption) {
            case "Harga (Termurah)":
                java.util.Arrays.sort(allProducts, (a, b) -> Double.compare(Double.parseDouble(a[3].toString()), Double.parseDouble(b[3].toString())));
                break;
            case "Harga (Termahal)":
                java.util.Arrays.sort(allProducts, (a, b) -> Double.compare(Double.parseDouble(b[3].toString()), Double.parseDouble(a[3].toString())));
                break;
            case "Kualitas (Terbaik)":
                java.util.Arrays.sort(allProducts, (a, b) -> Double.compare(Double.parseDouble(b[5].toString()), Double.parseDouble(a[5].toString())));
                break;
            case "Kualitas (Terjelek)":
                java.util.Arrays.sort(allProducts, (a, b) -> Double.compare(Double.parseDouble(a[5].toString()), Double.parseDouble(b[5].toString())));
                break;
        }

        tableModel.setRowCount(0);
        for (Object[] product : allProducts) {
            tableModel.addRow(product);
        }
    }
}
