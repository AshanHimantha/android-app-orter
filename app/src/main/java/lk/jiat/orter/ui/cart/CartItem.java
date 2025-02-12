package lk.jiat.orter.ui.cart;

public class CartItem {
    private int id;
    private String size;
    private int quantity;
    private Product product;

    public CartItem(int id, String size, int quantity, Product product) {
        this.id = id;
        this.size = size;
        this.quantity = quantity;
        this.product = product;
    }

    public int getId() {
        return id;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public static class Product {
        private String name;
        private int price;
        private String mainImage;
        private String categoryName;

        public Product(String name, int price, String mainImage, String categoryName) {
            this.name = name;
            this.price = price;
            this.mainImage = mainImage;
            this.categoryName = categoryName;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public String getMainImage() {
            return mainImage;
        }

        public String getCategoryName() {
            return categoryName;
        }
    }
}