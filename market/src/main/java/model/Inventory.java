package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Inventory {
    private List<Product> products;

    public Inventory() {
        products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(String productId) {
        products.removeIf(p -> p.getId().equals(productId));
    }

    public Product getProductById(String productId) {
        return products.stream()
            .filter(p -> p.getId().equals(productId))
            .findFirst()
            .orElse(null);
    }

    public List<Product> getProductsByCategory(String category) {
        return products.stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    public List<Product> getLowStockProducts(int threshold) {
        return products.stream()
            .filter(p -> p.getStock() <= threshold)
            .collect(Collectors.toList());
    }

    public void updateProductStock(String productId, int quantity) {
        Product product = getProductById(productId);
        if (product != null) {
            product.setStock(product.getStock() + quantity);
        }
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public int getTotalProductCount() {
        return products.size();
    }

    public double getTotalInventoryValue() {
        return products.stream()
            .mapToDouble(p -> p.getPrice() * p.getStock())
            .sum();
    }
}