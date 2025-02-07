package lk.jiat.orter.model;

public class Product {
    private String name;
    private String imageUrl;
    private double price;

    private String collection;

    public Product(String name, String imageUrl, double price , String collection) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.collection = collection;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
