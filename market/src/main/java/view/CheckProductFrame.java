    package view;

    import controller.ProductController;
    import java.awt.*;
    import java.io.BufferedReader;
    import java.io.FileReader;
    import java.io.IOException;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
    import model.ProductGraph;
    public class CheckProductFrame extends JFrame {
        private ProductController productController;
        private JTable productTable;
        private ProductGraph productGraph;
        private DefaultTableModel tableModel;

        public CheckProductFrame() {
            productController = new ProductController();
            productGraph = new ProductGraph();
            initComponents();
        }

        private void initComponents() {
            setTitle("Cek Produk");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel();
            JTextField inputField = new JTextField(20);  // Tempat untuk input nama produk atau kategori
            JButton searchButton = new JButton("Cari Produk Terkait");

            inputPanel.add(new JLabel("Masukkan Nama Produk/Kategori:"));
            inputPanel.add(inputField);
            inputPanel.add(searchButton);

            JPanel recommendationPanel = new JPanel();
            recommendationPanel.setLayout(new BoxLayout(recommendationPanel, BoxLayout.Y_AXIS));
            recommendationPanel.setBorder(BorderFactory.createTitledBorder("Produk Terkait"));

            add(inputPanel, BorderLayout.NORTH);
            add(recommendationPanel, BorderLayout.EAST);

            searchButton.addActionListener(e -> {
                String input = inputField.getText();  // Mengambil input dari field
                if (!input.isEmpty()) {
                    showRelatedProductsByInput(input, recommendationPanel);  // Menampilkan produk terkait
                } else {
                    JOptionPane.showMessageDialog(this, "Masukkan nama atau kategori produk.");
                }
            });
        
            // Panel Sorting
            JPanel sortPanel = new JPanel();
            String[] sortOptions = {"Harga (Termahal)", "Harga (Termurah)", 
                                    "Kualitas (Terbaik)", "Kualitas (Terjelek)", 
                                    "Kategori", "Stok (Paling Sedikit)"};
            JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);
            JButton applySort = new JButton("Terapkan Sorting");
        
            sortPanel.add(new JLabel("Urutkan Berdasarkan:"));
            sortPanel.add(sortComboBox);
            sortPanel.add(applySort);
        
            add(sortPanel, BorderLayout.NORTH);
        
            // Tabel Produk
            String[] columnNames = {"ID", "Nama", "Kategori", "Harga", "Stok", "Kualitas"};
            Object[][] data = productController.getAllProductData();
            tableModel = new DefaultTableModel(data, columnNames);
            productTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(productTable);
            add(scrollPane, BorderLayout.CENTER);
        
            // Panel Rekomendasi Produk
        
            // Panel Tambah Stok
            JPanel actionPanel = new JPanel(new GridLayout(2, 1));
        
            // Baris pertama: Hapus Produk
            JPanel removePanel = new JPanel();
            JTextField productIdRemove = new JTextField(10);
            JButton removeProductButton = new JButton("Hapus Produk");
        
            removePanel.add(new JLabel("ID Produk:"));
            removePanel.add(productIdRemove);
            removePanel.add(removeProductButton);
        
            // Baris kedua: Tambah Stok
            JPanel stockPanel = new JPanel();
            JTextField productIdField = new JTextField(10);
            JTextField stockField = new JTextField(5);
            JButton updateStockButton = new JButton("Tambah Stok");
        
            stockPanel.add(new JLabel("ID Produk:"));
            stockPanel.add(productIdField);
            stockPanel.add(new JLabel("Jumlah Stok:"));
            stockPanel.add(stockField);
            stockPanel.add(updateStockButton);
        
            // Tambahkan panel ke dalam actionPanel
            actionPanel.add(removePanel);
            actionPanel.add(stockPanel);
        
            // Tambahkan actionPanel ke dalam frame
            add(actionPanel, BorderLayout.SOUTH);
        
            // Button Kembali
            JButton backButton = new JButton("Kembali ke Dashboard");
            backButton.addActionListener(e -> {
                new CashierDashboardFrame().setVisible(true);
                dispose();
            });
            add(backButton, BorderLayout.WEST);
        
            // Action Listeners
            applySort.addActionListener(e -> {
                String selectedSort = (String) sortComboBox.getSelectedItem();
                sortProducts(selectedSort);
            });
        
            removeProductButton.addActionListener(e -> {
                String productId = productIdRemove.getText();
                if (productId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "ID Produk tidak boleh kosong!");
                } else {
                    productController.removeProduct(productId);
                    JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!");
                    refreshTable();  // Refresh tabel setelah produk dihapus
                }
            });
        
            updateStockButton.addActionListener(e -> {
                String productId = productIdField.getText();
                String stockStr = stockField.getText();
        
                if (productId.isEmpty() || stockStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "ID Produk dan Jumlah Stok tidak boleh kosong!");
                } else {
                    try {
                        // Konversi input jumlah stok ke integer
                        int stock = Integer.parseInt(stockStr);
        
                        // Panggil metode untuk memperbarui stok produk
                        productController.updateProductStock(productId, stock);
                        JOptionPane.showMessageDialog(this, "Stok berhasil diperbarui!");
                        refreshTable();  // Refresh tabel untuk menampilkan data terbaru
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Masukkan jumlah stok yang valid!");
                    }
                }
            });
        
        }
        private void loadProducts(String filename, ProductGraph productGraph) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[1];
                String category = data[2];
                productGraph.addProduct(name, category);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
    private void showRelatedProductsByInput(String input, JPanel recommendationPanel) {
        Set<String> relatedProducts = productGraph.getRelatedProductsByCategory(input);
        recommendationPanel.removeAll();  // Hapus komponen sebelumnya

        // Menampilkan produk terkait
        if (!relatedProducts.isEmpty()) {
            for (String relatedProduct : relatedProducts) {
                JLabel productLabel = new JLabel("Produk Terkait: " + relatedProduct);
                recommendationPanel.add(productLabel);
            }
        } else {
            JLabel noRelatedLabel = new JLabel("Tidak ada produk terkait untuk: " + input);
            recommendationPanel.add(noRelatedLabel);
        }

        recommendationPanel.revalidate();  // Memperbarui UI
        recommendationPanel.repaint();
    }

    
    
    
        

        private void sortProducts(String sortOption) {
            Object[][] allProducts = productController.getAllProductData();
            
            switch(sortOption) {
                case "Harga (Termurah)":
                    java.util.Arrays.sort(allProducts, (a, b) -> 
                        Double.compare(Double.parseDouble(a[3].toString()), 
                                    Double.parseDouble(b[3].toString())));
                    break;
                case "Harga (Termahal)":
                    java.util.Arrays.sort(allProducts, (a, b) -> 
                        Double.compare(Double.parseDouble(b[3].toString()), 
                                    Double.parseDouble(a[3].toString())));
                    break;
                case "Kualitas (Terbaik)":
                    java.util.Arrays.sort(allProducts, (a, b) -> 
                        Double.compare(Double.parseDouble(b[5].toString()), 
                                    Double.parseDouble(a[5].toString())));
                    break;
                case "Kualitas (Terjelek)":
                    java.util.Arrays.sort(allProducts, (a, b) -> 
                        Double.compare(Double.parseDouble(a[5].toString()), 
                                    Double.parseDouble(b[5].toString())));
                    break;
                case "Kategori":
                    java.util.Arrays.sort(allProducts, (a, b) -> 
                        a[2].toString().compareTo(b[2].toString()));
                    break;

                    case "Stok (Paling Sedikit)": // Added sorting by stock
                    java.util.Arrays.sort(allProducts, (a, b) -> 
                        Integer.compare(Integer.parseInt(a[4].toString()), 
                                        Integer.parseInt(b[4].toString())));
                    break;
            }
            
            tableModel.setRowCount(0);
            for (Object[] product : allProducts) {
                tableModel.addRow(product);
            }
        }

        private void refreshTable() {
            Object[][] data = productController.getAllProductData();
            tableModel.setRowCount(0);  // Menghapus semua baris yang ada
            if (data != null && data.length > 0) {
                // Jika ada data produk
                for (Object[] product : data) {
                    tableModel.addRow(product);
                }
            } else {
                // Jika tidak ada produk
                JOptionPane.showMessageDialog(this, "Tidak ada produk yang tersedia.");
            }
        }
        
    }
