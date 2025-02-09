package lk.jiat.orter.model;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    private final String id;
    @SerializedName("product_name")
    private String name;

    @SerializedName("main_image")
    private String imageUrl;

    @SerializedName("price")
    private double price;

    @SerializedName("collection_name")
    private String collection;

    public Product(String id, String name, String imageUrl, double price, String collection) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.collection = collection;
        this.id = id;
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

    public String getId() {
        return id;
    }


}
