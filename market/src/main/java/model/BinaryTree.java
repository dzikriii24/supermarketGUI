package model;

public class BinaryTree {
    private TreeNode root;

    // Konstruktor
    public BinaryTree() {
        root = null;
    }

    // Menambahkan produk ke dalam Binary Tree
    public void insert(Product product) {
        root = insertRec(root, product);
    }

    // Rekursif untuk menambahkan produk ke pohon
    private TreeNode insertRec(TreeNode root, Product product) {
        if (root == null) {
            root = new TreeNode(product);
            return root;
        }

        // Membandingkan ID produk untuk menentukan posisi di pohon
        if (product.getId().compareTo(root.product.getId()) < 0) {
            root.left = insertRec(root.left, product);
        } else if (product.getId().compareTo(root.product.getId()) > 0) {
            root.right = insertRec(root.right, product);
        }

        return root;
    }

    // Mencari produk berdasarkan ID
    public Product searchById(String id) {
        return searchRec(root, id);
    }

    // Rekursif untuk mencari produk berdasarkan ID
    private Product searchRec(TreeNode root, String id) {
        if (root == null || root.product.getId().equals(id)) {
            return root != null ? root.product : null;
        }

        if (id.compareTo(root.product.getId()) < 0) {
            return searchRec(root.left, id);
        }

        return searchRec(root.right, id);
    }

    // Mengambil semua produk dalam bentuk array 2D (untuk ditampilkan di tabel)
    public Object[][] getAllProducts() {
        return collectProducts(root, new java.util.ArrayList<>()).toArray(new Object[0][]);
    }

    // Mengumpulkan produk dalam urutan in-order (terurut berdasarkan ID)
    private java.util.List<Object[]> collectProducts(TreeNode root, java.util.List<Object[]> productList) {
        if (root != null) {
            collectProducts(root.left, productList);
            productList.add(new Object[]{
                root.product.getId(),
                root.product.getName(),
                root.product.getCategory(),
                root.product.getPrice(),
                root.product.getStock(),
                root.product.getQualityRating()
            });
            collectProducts(root.right, productList);
        }
        return productList;
    }

    // Node untuk BinaryTree
    private class TreeNode {
        Product product;
        TreeNode left, right;

        public TreeNode(Product product) {
            this.product = product;
            left = right = null;
        }
    }
}
