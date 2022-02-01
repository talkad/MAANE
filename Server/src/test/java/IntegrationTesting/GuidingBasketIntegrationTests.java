package IntegrationTesting;

import Domain.GuidingBasketsManagement.GuidingBasketController;
import Domain.GuidingBasketsManagement.GuidingBasketDTO;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class GuidingBasketIntegrationTests {

    private GuidingBasketDTO basketDTO;
    private final GuidingBasketController guidingBasketController = GuidingBasketController.getInstance();

    @Before
    public void setUp(){
        basketDTO = new GuidingBasketDTO("0", "Hello", "There", Arrays.asList("General", "Kenobi"));
    }

    @Test
    public void addBasketSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserByAdmin(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");

        Assert.assertFalse(guidingBasketController.addBasket("Dvorit", basketDTO).isFailure());
    }

    @Test
    public void addBasketNotLoggedInFailure(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserByAdmin(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);

        Assert.assertTrue(guidingBasketController.addBasket("Dvorit", basketDTO).isFailure());
    }

    @Test
    public void removeBasketSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserByAdmin(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");

        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");

        Assert.assertFalse(guidingBasketController.addBasket("Dvorit", basketDTO).isFailure());
    }

    @Test
    public void removeBasketNoPermissionsFailure(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserByAdmin(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.registerUserByAdmin(adminName, "Miri", "Band", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");

        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");

        Assert.assertTrue(guidingBasketController.removeBasket("Miri", basketDTO).isFailure());
    }
}
