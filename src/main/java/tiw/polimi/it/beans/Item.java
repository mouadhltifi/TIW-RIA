package tiw.polimi.it.beans;

public class Item {
    private final int id_item;
    private final String name;
    private final String description;
    private final String category;
    private final String image;



    public Item(int id_item, String name, String description, String category, String image) {
        this.id_item = id_item;
        this.name = name;
        this.description = description;
        this.category = category;
        this.image = image;
    }

    public int getId_item() {
        return id_item;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id_item=" + id_item +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
