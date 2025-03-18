package foly.anhld.duan1.Modol;

public class CartItem {
    private String productId;
    private String size;
    private int quantity;
    private int availableQuantity;
    private String productName;
    private double productPrice;
    private String productImage;

    public CartItem() {
        // Default constructor required for Firestore
    }

    public CartItem(String productId, String size, int quantity, int availableQuantity, String productName, double productPrice, String productImage) {
        this.productId = productId;
        this.size = size;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = productImage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
    // Getters and setters
}
