package GuidingBasketsManagement;

import CommonClasses.Response;

import java.util.List;

public class GuidingBasketController {

    private static GuidingBasketController instance;
    private static long id = 0;

    public static GuidingBasketController getInstance(){
        if(instance == null){
            instance = new GuidingBasketController();
        }

        return instance;
    }

    private String generateID(){
        return String.valueOf(id++);
    }

    public Response<List<GuidingBasketDTO>> search(String query, String[] labels){
        return SearchEngine.getInstance().search(query, labels);
    }

    public Response<Boolean> addBasket(GuidingBasketDTO dto){
        return SearchEngine.getInstance().addBasket(dto, instance.generateID());
    }

    public Response<Boolean> removeBasket(GuidingBasketDTO dto){
        return SearchEngine.getInstance().removeBasket(dto);
    }

    public Response<Boolean> setBasketTitle(GuidingBasketDTO dto, String newTitle){
        return SearchEngine.getInstance().setBasketTitle(dto, newTitle);
    }

    public Response<Boolean> setBasketDescription(GuidingBasketDTO dto, String newDescription){
        return SearchEngine.getInstance().setBasketDescription(dto, newDescription);
    }

    public Response<Boolean> addBasketLabel(GuidingBasketDTO dto, String labelToAdd){
        return SearchEngine.getInstance().addRemoveLabel(dto, labelToAdd, 0);
    }

    public Response<Boolean> removeBasketLabel(GuidingBasketDTO dto, String labelToRemove){
        return SearchEngine.getInstance().addRemoveLabel(dto, labelToRemove, 1);
    }

    //TODO: change / remove / add document
}
