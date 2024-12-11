package view;

import controller.ProductController;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.Product;

public class CashierDashboardFrame extends JFrame {
    private ProductController productController;
    private JTable productTable;
    private JTextField searchField;

    public CashierDashboardFrame() {
        productController = new ProductController("products.txt");
        initComponents();
        setTitle("Dashboard Kasir");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel Pencarian
        JPanel searchPanel = new JPanel();
    searchField = new JTextField(20);
    JButton searchButton = new JButton("Cari Nama");
    JButton searchByIdButton = new JButton("Cari ID");
    JButton addProductButton = new JButton("Tambah Produk");
    JButton checkProductButton = new JButton("Cek Produk");

    searchPanel.add(searchField);
    searchPanel.add(searchButton);
    searchPanel.add(searchByIdButton);
    searchPanel.add(addProductButton);
    searchPanel.add(checkProductButton);

    add(searchPanel, BorderLayout.NORTH);

        // Tabel Produk
        productTable = new JTable();
        refreshProductTable();
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        // Tombol Kembali
        JButton backButton = new JButton("Kembali ke Login");
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        add(backButton, BorderLayout.SOUTH);

        // Action Listeners
        searchButton.addActionListener(e -> searchProducts());
        searchByIdButton.addActionListener(e -> searchProductsById());
        addProductButton.addActionListener(e -> showAddProductDialog());
        checkProductButton.addActionListener(e -> {
            CheckProductFrame checkFrame = new CheckProductFrame();
            checkFrame.setVisible(true);
            dispose();
        });
    }

    private void refreshProductTable() {
        String[] columnNames = {"ID", "Nama", "Kategori", "Harga", "Stok", "Kualitas"};
        Object[][] data = productController.getAllProductData();
        productTable.setModel(new DefaultTableModel(data, columnNames));
    }

    private void searchProducts() {
        String keyword = searchField.getText().toLowerCase();
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(productTable.getModel());
        productTable.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
    }
    private void searchProductsById() {
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            try {
                // Mengonversi keyword ke ID produk (integer)
                int id = Integer.parseInt(keyword);
    
                // Mencari produk berdasarkan ID
                Product product = productController.searchProductById(String.valueOf(id));
                if (product != null) {
                    // Tampilkan produk yang ditemukan di tabel
                    String[] columnNames = {"ID", "Nama", "Kategori", "Harga", "Stok", "Kualitas"};
                    Object[][] data = {
                        {
                            product.getId(),
                            product.getName(),
                            product.getCategory(),
                            product.getPrice(),
                            product.getStock(),
                            product.getQualityRating()
                        }
                    };
                    productTable.setModel(new DefaultTableModel(data, columnNames));
                } else {
                    JOptionPane.showMessageDialog(this, "Produk dengan ID " + id + " tidak ditemukan.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID produk harus berupa angka!");
            }
        }
    }
    
    
    

    private void showAddProductDialog() {
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();
        JTextField qualityField = new JTextField();
        JTextField manufacturerField = new JTextField();

        Object[] message = {
            "Nama Produk:", nameField,
            "Kategori:", categoryField,
            "Harga:", priceField,
            "Stok:", stockField,
            "Kualitas:", qualityField,
            "Pabrikan:", manufacturerField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Tambah Produk", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String category = categoryField.getText();
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());
                double qualityRating = Double.parseDouble(qualityField.getText());
                String manufacturer = manufacturerField.getText();

                if (name.isEmpty() || category.isEmpty() || manufacturer.isEmpty()) {
                    throw new IllegalArgumentException("Semua field harus diisi!");
                }

                Product newProduct = new Product(name, category, price, stock, qualityRating, manufacturer);
                productController.addProduct(newProduct);

                JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!");
                refreshProductTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga, Stok, dan Kualitas harus berupa angka!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }
}
