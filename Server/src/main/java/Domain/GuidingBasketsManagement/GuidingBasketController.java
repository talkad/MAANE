package Domain.GuidingBasketsManagement;

import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserController;

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

    public Response<Boolean> addBasket(String username, GuidingBasketDTO dto){
        String basketID = instance.generateID();
        Response<String> response = UserController.getInstance().createBasket(username, basketID);

        if(response.isFailure())
            return new Response<>(false, true, response.getErrMsg());

        return SearchEngine.getInstance().addBasket(dto, basketID);
    }

    public Response<Boolean> removeBasket(String username, GuidingBasketDTO dto){
        Response<String> response = UserController.getInstance().removeBasket(username, dto.getBasketID());

        if(response.isFailure())
            return  new Response<>(false, true, response.getErrMsg());

        return SearchEngine.getInstance().removeBasket(dto);
    }

    public Response<Boolean> setBasketTitle(String username, GuidingBasketDTO dto, String newTitle){
        Response<Boolean> response = UserController.getInstance().hasCreatedBasket(username, dto.getBasketID());

        if(response.isFailure())
            return  response;

        return SearchEngine.getInstance().setBasketTitle(dto, newTitle);
    }

    public Response<Boolean> setBasketDescription(String username, GuidingBasketDTO dto, String newDescription){
        Response<Boolean> response = UserController.getInstance().hasCreatedBasket(username, dto.getBasketID());

        if(response.isFailure())
            return  response;

        return SearchEngine.getInstance().setBasketDescription(dto, newDescription);
    }

    public Response<Boolean> addBasketLabel(String username, GuidingBasketDTO dto, String labelToAdd){
        Response<Boolean> response = UserController.getInstance().hasCreatedBasket(username, dto.getBasketID());

        if(response.isFailure())
            return  response;

        return SearchEngine.getInstance().addRemoveLabel(dto, labelToAdd, 0);
    }

    public Response<Boolean> removeBasketLabel(String username, GuidingBasketDTO dto, String labelToRemove){
        Response<Boolean> response = UserController.getInstance().hasCreatedBasket(username, dto.getBasketID());

        if(response.isFailure())
            return  response;

        return SearchEngine.getInstance().addRemoveLabel(dto, labelToRemove, 1);
    }

    //TODO: change / remove / add document
}
