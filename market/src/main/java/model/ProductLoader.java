package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProductLoader {
    public static void loadProducts(String filename, ProductGraph productGraph) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[1];
                String category = data[2];
                // Tambahkan produk ke dalam graph berdasarkan kategori
                productGraph.addProduct(name, category);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
