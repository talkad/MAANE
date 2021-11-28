package GuidingBaskets;

import CommonClasses.Response;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class SearchEngine {

    private static SearchEngine instance;
    private List<GuidingBasket> baskets;

    private SearchEngine(){
        baskets = new LinkedList<>();
    }

    public static SearchEngine getInstance(){
        if(instance == null){
            instance = new SearchEngine();
        }

        return instance;
    }

    public Response<List<GuidingBasketDTO>> search(String query, String[] labels){
        return null;
    }

    public Response<Boolean> addBasket(GuidingBasketDTO dto, String newID){
        if(dto != null){
            dto.setBasketID(newID);
            instance.baskets.add(new GuidingBasket(dto));
            return new Response<>(true, false, null);
        }

        return new Response<>(false, true, "Couldn't add the basket");
    }

    public Response<Boolean> removeBasket(GuidingBasketDTO dto){
        if (dto != null && dto.getBasketID() != null){
            if(instance.baskets.removeIf(b -> b.getBasketID().equals(dto.getBasketID()))){
                return new Response<>(true, false, null);
            }
        }

        return new Response<>(false, true, "Couldn't find basket to remove");
    }

    private GuidingBasket getBasket(GuidingBasketDTO dto){
        try{
           return baskets.stream().filter(b -> b.getBasketID().equals(dto.getBasketID()))
                    .findFirst().get();
        }
        catch (NoSuchElementException e){
            return null;
        }
    }

    public Response<Boolean> setBasketTitle(GuidingBasketDTO dto, String newTitle){

        if(newTitle != null){
            GuidingBasket selected = getBasket(dto);
            if(selected != null){
                selected.setTitle(newTitle);
                return new Response<>(true, false, null);
            }
            return new Response<>(false, true, "The selected basket does not exist");
        }

        return new Response<>(false, true, "A new title was not provided");
    }

    public Response<Boolean> setBasketDescription(GuidingBasketDTO dto, String newDescription){
        if(newDescription != null){
            GuidingBasket selected = getBasket(dto);
            if(selected != null){
                selected.setDescription(newDescription);
                return new Response<>(true, false, null);
            }
            return new Response<>(false, true, "The selected basket does not exist");
        }

        return new Response<>(false, true, "A new description was not provided");
    }

    // action = 0 - add
    // action = 1 - remove
    public Response<Boolean> addRemoveLabel(GuidingBasketDTO dto, String label, int action){
        if(label != null){
            GuidingBasket selected = getBasket(dto);
            if(selected != null){
                if(action == 0){
                    return selected.addLabel(label);
                }
                else{
                    return selected.removeLabel(label);
                }
            }
            return new Response<>(false, true, "The selected basket does not exist");
        }

        return new Response<>(false, true, "The label was not provided");
    }
}
