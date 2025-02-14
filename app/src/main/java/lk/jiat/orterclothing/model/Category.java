package lk.jiat.orterclothing.model;

public class Category {
    private int iconResourceId; // Or a URL if you load from the internet
    private String name;

    private String description;

    private String id;


    public Category(int iconResourceId, String name, String description, String id) {
        this.iconResourceId = iconResourceId;
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }


    public int getIconResourceId() {
        return iconResourceId;
    }

    public String getName() {
        return name;
    }
}
