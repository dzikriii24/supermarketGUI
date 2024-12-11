package controller;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.BinaryTree;
import model.Product;


public class ProductController {
    private BinaryTree productTree; // Menggunakan pohon biner
    private List<Product> products;
    private Map<String, Product> productMap;
    private String filePath;

    public ProductController() {
        this("products.txt"); // Default file path
    }

    public ProductController(String fileName) {
        productTree = new BinaryTree(); // Inisialisasi pohon biner
        productMap = new HashMap<>();
        this.filePath = fileName; // Menyimpan path file
        this.products = new ArrayList<>();
        loadProductsFromFile(); // Memuat produk dari file
    }

    public List<Product> getAllProducts() {
        return products;
        
    }

    // Tambahkan produk baru ke daftar dan ke BinaryTree serta simpan ke file
    public void addProduct(Product product) {
        products.add(product); // Menambahkan ke list produk
        productTree.insert(product); // Menambahkan ke pohon biner
        saveProductsToFile(); // Menyimpan produk ke file
    }

    public Object[][] getAllProductDataBin() {
        return productTree.getAllProducts(); // Mengambil produk dari BinaryTree dalam format array 2D
    }

    // Mencari produk berdasarkan ID menggunakan BinaryTree
    public Product searchProductById(String id) {
        return productTree.searchById(id);
    }

    // Mengambil semua produk dalam format array 2D untuk tabel
    public Object[][] getAllProductData() {
        if (products.isEmpty()) {
            return new Object[0][6]; // Mengembalikan array kosong jika tidak ada produk
        }
        
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
    private void saveProductsToFileHash() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Product product : productMap.values()) {
                writer.write(product.toFileFormat()); // Menyimpan produk ke dalam file
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error menyimpan data ke file: " + e.getMessage());
        }
    }
    private void loadProductsFromFileHash() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = Product.fromFileFormat(line);
                if (product != null) {
                    productMap.put(product.getId(), product); // Menambahkan produk ke HashMap
                }
            }
        } catch (IOException e) {
            System.err.println("Error membaca file: " + e.getMessage());
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
                    productMap.put(product.getId(), product); // Memasukkan produk ke productMap juga
                    productTree.insert(product); // Menambahkan ke pohon biner
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan, membuat file baru...");
            saveProductsToFile();
        } catch (IOException e) {
            System.err.println("Error membaca data produk: " + e.getMessage());
        }
    }
    
    
    

    public Object[] getProductById(String productId) {
        for (Object[] product : getAllProductData()) {
            if (product[0] instanceof String && product[0].toString().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    public Object[][] getTopRatedProductsByCategory(String category, int limit) {
        Object[][] allProducts = getAllProductData();
        return java.util.Arrays.stream(allProducts)
            .filter(product -> product[2].toString().equalsIgnoreCase(category))
            .sorted((a, b) -> Double.compare(Double.parseDouble(b[5].toString()), Double.parseDouble(a[5].toString())))
            .limit(limit)
            .toArray(Object[][]::new);
    }

    // Update stok produk berdasarkan ID (pembayaran)
    public void updateProductStockPay(String productId, int quantity) {
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                int currentStock = product.getStock();
                if (quantity > currentStock) {
                    System.out.println("Stok tidak cukup untuk produk " + productId + ". Stok tersedia: " + currentStock);
                    return;
                } else {
                    int newStock = currentStock - quantity;
                    product.setStock(newStock);
                    saveProductsToFile();
                    System.out.println("Transaksi berhasil. Stok baru untuk produk " + productId + ": " + newStock);
                    return;
                }
            }
        }
        System.err.println("Produk dengan ID " + productId + " tidak ditemukan.");
    }

    // Update stok produk berdasarkan ID
    public void updateProductStock(String productId, int additionalStock) {
        Product product = productMap.get(productId);
        if (product != null) {
            int currentStock = product.getStock();  // Mendapatkan stok saat ini
            int newStock = currentStock + additionalStock;  // Menambahkan stok yang baru
            product.setStock(newStock);  // Memperbarui stok produk
            saveProductsToFile();  // Menyimpan perubahan ke file
            System.out.println("Stok produk " + productId + " berhasil diperbarui menjadi " + newStock);
        } else {
            System.err.println("Produk dengan ID " + productId + " tidak ditemukan di productMap.");
        }
    }
    
    

    public int getProductStock(String productId) {
        int stock = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath)); // Ganti PRODUCT_FILE dengan filePath
            String line;
            while ((line = reader.readLine()) != null) {
                String[] productDetails = line.split(",");
                if (productDetails[0].equals(productId)) {
                    stock = Integer.parseInt(productDetails[4]);  // Mengasumsikan stok ada di kolom ke-5
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stock;
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
