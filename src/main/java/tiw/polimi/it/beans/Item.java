package tiw.polimi.it.beans;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class Item {
    private final int id_item;
    private final String name;
    private final String description;
    private final String category;
    private final String image;



    public Item(int id_item, String name, String description, String category, String image){
        this.id_item = id_item;
        this.name = name;
        this.description = description;
        this.category = category;
        this.image = "images/"+id_item+".jpg";
/*
        try {
            byte[] fileContent = FileUtils.readFileToByteArray(new File("C:\\Users\\Momo\\IdeaProjects\\TIW-RIA\\src\\main\\java\\tiw\\polimi\\it\\images\\1.jpg"));
            this.image = Base64.getEncoder().encodeToString(fileContent);
        }catch (IOException e){throw new IllegalArgumentException(e);}
*/
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
