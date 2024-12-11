package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProductGraph {
    private Map<String, Set<String>> categoryGraph;

    public ProductGraph() {
        categoryGraph = new HashMap<>();
    }

    // Menambahkan produk ke dalam graph berdasarkan kategori
    public void addProduct(String name, String category) {
        if (!categoryGraph.containsKey(category)) {
            categoryGraph.put(category, new HashSet<>());
        }
        categoryGraph.get(category).add(name);  // Menambahkan nama produk ke dalam kategori
    }

    // Menambahkan hubungan antar produk berdasarkan kategori
    public void addConnection(String product1, String product2, String category) {
        Set<String> productsInCategory = categoryGraph.get(category);
        
        // Memastikan bahwa produk ada dalam kategori yang sama
        if (productsInCategory != null && productsInCategory.contains(product1) && productsInCategory.contains(product2)) {
            // Menghubungkan kedua produk dalam kategori yang sama
            productsInCategory.add(product1);
            productsInCategory.add(product2);
        }
    }

    // Mendapatkan produk terkait berdasarkan kategori yang sama
    public Set<String> getRelatedProductsByCategory(String category) {
        return categoryGraph.getOrDefault(category, new HashSet<>());
    }

    // Menampilkan graph untuk keperluan debug
    public void displayGraph() {
        for (Map.Entry<String, Set<String>> entry : categoryGraph.entrySet()) {
            System.out.println("Kategori: " + entry.getKey());
            for (String relatedProduct : entry.getValue()) {
                System.out.println(" - " + relatedProduct);
            }
        }
    }
}
