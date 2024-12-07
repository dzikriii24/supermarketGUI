package controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Product;

public class ProductController {
    private List<Product> products;
    private String filePath;
    
    public ProductController() {
        this("products.txt"); // Default file path
    }
    
    public ProductController(String fileName) {
        this.filePath = fileName; // Store file path
        this.products = new ArrayList<>();
        loadProductsFromFile(); // Load existing products from the file
    }
    
    public List<Product> getAllProducts() {
        return products;
    }
    
    
    

    // Tambahkan produk baru ke daftar dan simpan ke file
    public void addProduct(Product product) {
        products.add(product);
        saveProductsToFile();
    }

    // Mendapatkan semua produk dalam format array 2D untuk tabel
    public Object[][] getAllProductData() {
        Object[][] data = new Object[products.size()][6];
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            data[i] = new Object[]{
                p.getId(),
                p.getName(),
                p.getCategory(),
                p.getPrice(),
                p.getStock(),
                p.getQualityRating()
            };
        }
        return data;
    }

    // Simpan produk ke file
    private void saveProductsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Product product : products) {
                writer.write(product.toFileFormat());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error menyimpan data produk: " + e.getMessage());
        }
    }

    // Load produk dari file
    private void loadProductsFromFile() {
        products.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = Product.fromFileFormat(line);
                if (product != null) {
                    products.add(product);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan, membuat file baru...");
            saveProductsToFile();
        } catch (IOException e) {
            System.err.println("Error membaca data produk: " + e.getMessage());
        }
    }

    // Update stok produk berdasarkan ID
    public void updateProductStock(String productId, int quantity) {
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                product.setStock(product.getStock() + quantity);
                saveProductsToFile();
                return;
            }
        }
        System.err.println("Produk dengan ID " + productId + " tidak ditemukan.");
    }

    // Hapus produk berdasarkan ID
    public void removeProduct(String productId) {
        boolean removed = products.removeIf(product -> product.getId().equals(productId));
        if (removed) {
            saveProductsToFile();
        } else {
            System.err.println("Produk dengan ID " + productId + " tidak ditemukan.");
        }
    }
}