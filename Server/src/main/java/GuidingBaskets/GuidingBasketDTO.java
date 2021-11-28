package GuidingBaskets;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GuidingBasketDTO {
    String basketID;
    String title;
    String description;
    List<String> labels;
    // TODO: how would we save the file?

    public GuidingBasketDTO(String basketID ,String title, String description, List<String> labels) { //TODO: should we pass labels as an array or List?
        this.basketID = basketID;
        this.title = title;
        this.description = description;
        this.labels = labels;
    }

    public String getBasketID() {
        return basketID;
    }

    public void setBasketID(String basketID) {
        this.basketID = basketID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
