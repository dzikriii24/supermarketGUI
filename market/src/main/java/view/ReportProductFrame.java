package view;

import controller.ProductController;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class ReportProductFrame extends JFrame {
    private ProductController productController;
    private JTable productTable;
    private DefaultTableModel tableModel;

    public ReportProductFrame() {
        productController = new ProductController();
        initComponents();
        if (productController.getAllProducts() == null || productController.getAllProducts().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data produk kosong!");
        }
    }

    private void initComponents() {
        setTitle("Laporan Produk");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Kolom untuk tabel
        String[] columnNames = {"ID", "Nama", "Kategori", "Harga", "Stok", "Kualitas", "Aksi"};
        Object[][] productData = getProductData();
        
        tableModel = new DefaultTableModel(productData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hanya kolom aksi yang bisa diedit
                return column == 6;
            }
        };

        productTable = new JTable(tableModel);
        
        // Tambahkan tombol hapus di kolom aksi
        productTable.getColumn("Aksi").setCellRenderer(new ButtonRenderer());
        productTable.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel Ringkasan
        JPanel summaryPanel = new JPanel();
        int totalProducts = productController.getAllProducts().size();
        double totalInventoryValue = calculateTotalInventoryValue();
        
        JLabel totalProductsLabel = new JLabel("Total Produk: " + totalProducts);
        JLabel totalValueLabel = new JLabel("Total Nilai Inventori: Rp " + String.format("%.2f", totalInventoryValue));
        
        summaryPanel.add(totalProductsLabel);
        summaryPanel.add(totalValueLabel);
        
        add(summaryPanel, BorderLayout.NORTH);

        // Tombol Kembali
        JButton backButton = new JButton("Kembali ke Dashboard");
        backButton.addActionListener(e -> {
            new CashierDashboardFrame().setVisible(true);
            dispose();
        });
        add(backButton, BorderLayout.SOUTH);
    }

    private Object[][] getProductData() {
        Object[][] data = productController.getAllProductData();
        Object[][] extendedData = new Object[data.length][7];
        
        for (int i = 0; i < data.length; i++) {
            System.arraycopy(data[i], 0, extendedData[i], 0, 6);
            extendedData[i][6] = "Hapus";
        }
        
        return extendedData;
    }

    private double calculateTotalInventoryValue() {
        return productController.getAllProducts().stream()
            .mapToDouble(p -> p.getPrice() * p.getStock())
            .sum();
    }

    // Inner class untuk render tombol di tabel
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Inner class untuk edit tombol di tabel
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // Hapus produk
                String productId = (String) tableModel.getValueAt(productTable.getSelectedRow(), 0);
                productController.removeProduct(productId);
                
                // Refresh tabel
                tableModel.removeRow(productTable.getSelectedRow());
                
                JOptionPane.showMessageDialog(null, "Produk berhasil dihapus!");
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
