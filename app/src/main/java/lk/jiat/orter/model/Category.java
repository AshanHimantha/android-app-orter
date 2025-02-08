package lk.jiat.orter.model;

public class Category {
    private int iconResourceId; // Or a URL if you load from the internet
    private String name;

    public Category(int iconResourceId, String name) {
        this.iconResourceId = iconResourceId;
        this.name = name;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public String getName() {
        return name;
    }
}
