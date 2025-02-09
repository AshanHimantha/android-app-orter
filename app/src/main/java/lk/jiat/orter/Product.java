package lk.jiat.orter;

import android.widget.Toast;

class Product {
    String id;
    String product_name;
    String main_image;
    String price;
    String collection_name;
    int total_quantity;

    public Product(String id,String product_name, String main_image, String price, String collection_name) {
        this.product_name = product_name;
        this.main_image = main_image;
        this.price = price;
        this.collection_name = collection_name;
        this.id = id;

    }


}
