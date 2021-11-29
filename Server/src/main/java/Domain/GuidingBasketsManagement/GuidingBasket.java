package Domain.GuidingBasketsManagement;

import Domain.CommonClasses.Response;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GuidingBasket {

    String basketID;
    String title;
    String description;
    List<String> labels; // TODO: Save baskets in lowercase
    // TODO: how would we save the file?

    public GuidingBasket(String basketID ,String title, String description, String[] labels) { //TODO: should we pass labels as an array or List?
        this.basketID = basketID;
        this.title = title;
        this.description = description;

        this.labels = new LinkedList<>();
        this.labels.addAll(Arrays.asList(labels));
    }

    public GuidingBasket(GuidingBasketDTO dto){
        this.basketID = dto.getBasketID();
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.labels = dto.getLabels();
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

    public Response<Boolean> addLabel(String label) {
        if (label != null && !labels.contains(label)){
            labels.add(label);
            return new Response<>(true, false, null);
        }

        return new Response<>(false, true, "The label already exists in this guiding basket");
    }

    public Response<Boolean> removeLabel(String label) {
        if (label != null && labels.contains(label)){
            labels.remove(label);
            return new Response<>(true, false, null);
        }

        return new Response<>(false, true, "The label doesn't exist in this guiding basket");
    }
}
