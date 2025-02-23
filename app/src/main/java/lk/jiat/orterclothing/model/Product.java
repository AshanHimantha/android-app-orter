package lk.jiat.orterclothing.model;

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

    private String category;


    public Product(String id, String name, String imageUrl, double price, String collection , String category) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.collection = collection;
        this.id = id;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
