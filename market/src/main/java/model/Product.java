package model;

import java.util.Random;

public class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private int stock;
    private double qualityRating;
    private String manufacturer;

    // Constructor untuk membuat produk baru dengan ID otomatis
    public Product(String name, String category, double price, int stock, double qualityRating, String manufacturer) {
        this.id = generateRandomId();
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.qualityRating = qualityRating;
        this.manufacturer = manufacturer;
    }

    // Constructor untuk membaca produk dengan ID yang sudah ada
    public Product(String id, String name, String category, double price, int stock, double qualityRating, String manufacturer) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.qualityRating = qualityRating;
        this.manufacturer = manufacturer;
    }

    // Generate ID unik dengan format 4 angka
    private String generateRandomId() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    // Format data produk untuk disimpan dalam file
    public String toFileFormat() {
        return String.join(",",
            id, name, category, String.valueOf(price),
            String.valueOf(stock), String.valueOf(qualityRating), manufacturer
        );
    }

    // Parse data produk dari format file
    public static Product fromFileFormat(String fileLine) {
        String[] parts = fileLine.split(",");
        if (parts.length == 7) {
            try {
                return new Product(
                    parts[0], // id
                    parts[1], // name
                    parts[2], // category
                    Double.parseDouble(parts[3]), // price
                    Integer.parseInt(parts[4]), // stock
                    Double.parseDouble(parts[5]), // qualityRating
                    parts[6] // manufacturer
                );
            } catch (NumberFormatException e) {
                System.err.println("Error parsing data produk: " + e.getMessage());
            }
        }
        return null;
    }

    // Getters dan Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getQualityRating() { return qualityRating; }
    public void setQualityRating(double qualityRating) { this.qualityRating = qualityRating; }
    public String getManufacturer() { return manufacturer; }
    public void setId(String id) {
        this.id = id;
    }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
}
