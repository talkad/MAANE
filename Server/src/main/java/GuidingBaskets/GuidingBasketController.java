package GuidingBaskets;

import java.util.LinkedList;
import java.util.List;

public class GuidingBasketController {

    private static GuidingBasketController instance;
    private List<GuidingBasket> baskets;

    private GuidingBasketController(){
        baskets = new LinkedList<>();
    }

    public static GuidingBasketController getInstance(){
        if(instance == null){
            instance = new GuidingBasketController();
        }

        return instance;
    }
}
