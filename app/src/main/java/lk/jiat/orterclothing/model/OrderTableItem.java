package lk.jiat.orterclothing.model;

public class OrderTableItem {
    private String productName;
    private String size;
    private int quantity;
    private double price;

    private String imageUrl;

    public OrderTableItem(String productName, String size, int quantity, double price , String imageUrl) {
        this.productName = productName;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return quantity * price;
    }
}