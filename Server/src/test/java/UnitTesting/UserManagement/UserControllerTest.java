package UnitTesting.UserManagement;

import Domain.CommonClasses.Response;
import Domain.UsersManagment.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Vector;

public class UserControllerTest { //todo reset tests


    @Test
    public void loginAsSystemManagerSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        Assert.assertTrue(userController.getConnectedUsers().containsKey(guestName));
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        Assert.assertFalse(userController.getConnectedUsers().containsKey(guestName));
        Assert.assertTrue(userController.getConnectedUsers().containsKey(adminName));
        Assert.assertTrue(userController.getRegisteredUsers().containsKey(adminName));

    }

    @Test
    public void assigningSupervisorSuccess(){//todo gets fixed by resetting the database
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        Response<User> res = userController.registerSupervisor(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR,"tech","", "", "", "", "");
        guestName = userController.logout(adminName).getResult();
        Response<String> supervisorName = userController.login(guestName, "sup1", "sup1");
        Assert.assertTrue(userController.getConnectedUsers().containsKey("sup1"));
    }

    @Test
    public void assigningInstructorBySupervisorSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        userController.registerSupervisor(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "tech", "", "", "", "", "");
        guestName = userController.logout(adminName).getResult();
        userController.login(guestName, "sup1", "sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Assert.assertTrue(userController.getRegisteredUsers().containsKey("ins1"));
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getWorkField().equals("tech"));
    }

    @Test
    public void assigningSchoolsToInstructorSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        userController.registerSupervisor(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "tech", "", "", "", "", "");
        guestName = userController.logout(adminName).getResult();
        userController.login(guestName, "sup1", "sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        List<Integer> schools = new Vector<>();
        schools.add(1);
        schools.add(2);
        Response<Boolean> res =  userController.assignSchoolsToUser("sup1", "ins1", schools);
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().size() == 2);
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().contains(1));
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().contains(2));
    }

    @Test
    public void removingSchoolsFromInstructorSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        userController.registerSupervisor(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "tech", "", "", "", "", "");
        guestName = userController.logout(adminName).getResult();
        userController.login(guestName, "sup1", "sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        List<Integer> schools = new Vector<>();
        schools.add(1);
        schools.add(2);
        userController.assignSchoolsToUser("sup1", "ins1", schools);
        List<Integer> schoolsToRemoveList = new Vector<>();
        schoolsToRemoveList.add(1);
        userController.removeSchoolsFromUser("sup1", "ins1", schoolsToRemoveList);
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().size() == 1);
        Assert.assertFalse(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().contains(1));
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().contains(2));
    }

    @Test
    public void removeAssignedInstructorSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        userController.registerSupervisor(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "tech", "","", "", "", "");
        guestName = userController.logout(adminName).getResult();
        userController.login(guestName, "sup1", "sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        List<Integer> schools = new Vector<>();
        schools.add(1);
        schools.add(2);
        userController.assignSchoolsToUser("sup1", "ins1", schools);
        userController.removeUser("sup1", "ins1");
        Assert.assertFalse(userController.getRegisteredUsers().containsKey("ins1"));
        Assert.assertFalse(userController.getRegisteredUsers().get("sup1").getFirst().getAppointments().contains("ins1"));
    }

    @Test
    public void changePasswordBySupervisorFail(){//todo fix setup so it resets
        Security security = Security.getInstance();
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        userController.registerSupervisor(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "tech", "","", "", "", "");
        userController.changePasswordToUser(adminName, "sup1", "sup111", "sup11");
        guestName = userController.logout(adminName).getResult();
        Response<String> res = userController.login(guestName, "sup1", "sup111");
        Assert.assertTrue(res.isFailure());
        Assert.assertFalse(userController.getRegisteredUsers().get("sup1").getSecond().equals(security.sha256("sup111")));
    }

    @Test
    public void changePasswordBySupervisorSuccess(){
        Security security = Security.getInstance();
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        userController.registerSupervisor(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "tech", "","", "", "", "");
        userController.changePasswordToUser(adminName, "sup1", "sup111", "sup111");
        guestName = userController.logout(adminName).getResult();
        Response<String> res = userController.login(guestName, "sup1", "sup111");
        Assert.assertFalse(res.isFailure());
        Assert.assertTrue(userController.getRegisteredUsers().get("sup1").getSecond().equals(security.sha256("sup111")));
    }

    @Test
    public void changePasswordSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        Response<User> res = userController.registerSupervisor(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR,"tech","", "", "", "", "");
        guestName = userController.logout(adminName).getResult();
        Response<String> supervisorName = userController.login(guestName, "sup1", "sup1");
        userController.changePassword("sup1", "1234", "1234");
        guestName = userController.logout("sup1").getResult();
        Assert.assertFalse(userController.getConnectedUsers().containsKey("sup1"));
        userController.login(guestName, "sup1", "1234");
        Assert.assertTrue(userController.getConnectedUsers().containsKey("sup1"));
    }

    @Test
    public void updateInfoSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        Response<User> res = userController.registerSupervisor(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR,"tech","", "", "", "", "");
        System.out.println(res.isFailure());
        guestName = userController.logout(adminName).getResult();
        Response<String> supervisorName = userController.login(guestName, "sup1", "sup1");
        userController.updateInfo("sup1", "1", "", "", "", "");
        Assert.assertTrue(userController.getRegisteredUsers().get("sup1").getFirst().getFirstName().equals("1"));
    }

    @Test
    public void changePasswordToInstructor() {
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        userController.registerSupervisor(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "tech", "", "", "", "", "");
        guestName = userController.logout(adminName).getResult();
        userController.login(guestName, "sup1", "sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.changePasswordToUser("sup1", "ins1", "ins111", "ins111");
        guestName = userController.logout("sup1").getResult();
        userController.login(guestName, "ins1", "ins111");
        Assert.assertTrue(userController.getConnectedUsers().containsKey("ins1"));
    }

    @Test
    public void assigningYeadimSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "shaked", "cohen").getResult();
        Response<User> res = userController.registerSupervisor(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR,"tech","", "", "", "", "");
        guestName = userController.logout(adminName).getResult();
        String supervisorName = userController.login(guestName, "sup1", "sup1").getResult();
        List<Goal> goalList = new Vector<>();
        goalList.add(new Goal(1, "goal1", "goal1"));
        goalList.add(new Goal(2, "goal2", "goal2"));
        goalList.add(new Goal(3, "goal3", "goal3"));
        userController.addGoals(supervisorName, goalList);
//        for (Goal goal: userController.getGoals(supervisorName).getResult()) {
//            System.out.println(goal.toString());
//        }
        Assert.assertTrue(userController.getGoals(supervisorName).getResult().size() == 3);
    }
}
