package lk.jiat.orter;

class Product {
    String id;
    String product_name;
    String main_image;
    int price;
    String collection_name;
    int total_quantity; // if needed

    public Product(String product_name, String main_image, int price, String collection_name) {
        this.product_name = product_name;
        this.main_image = main_image;
        this.price = price;
        this.collection_name = collection_name;
    }
}
