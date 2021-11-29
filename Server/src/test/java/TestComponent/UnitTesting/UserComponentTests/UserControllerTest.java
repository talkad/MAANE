package TestComponent.UnitTesting.UserComponentTests;

import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Vector;

public class UserControllerTest {
    @Test
    public void loginAsSystemManagerSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        Assert.assertTrue(userController.getConnectedUsers().containsKey(guestName));
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        Assert.assertFalse(userController.getConnectedUsers().containsKey(guestName));
        Assert.assertTrue(userController.getConnectedUsers().containsKey(adminName));
    }

    @Test
    public void assigningSupervisorSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        userController.registerUser(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR);
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "sup1", "sup1");
        Assert.assertTrue(userController.getConnectedUsers().containsKey("sup1"));
    }

    @Test
    public void assigningInstructorBySupervisorSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        userController.registerUser(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR);
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "sup1", "sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR);
        Assert.assertTrue(userController.getRegisteredUsers().containsKey("ins1"));
    }

    @Test
    public void assigningSchoolsToInstructor(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        userController.registerUser(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR);
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "sup1", "sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR);
        List<Integer> schools = new Vector<>();
        schools.add(1);
        schools.add(2);
        userController.assignSchoolsToUser("sup1", "ins1", schools);
        System.out.println(userController.getRegisteredUsers().get("ins1").getFirst().getSchools());
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().size() == 2);
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().contains(1));
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().contains(2));
    }
}
