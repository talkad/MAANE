package UnitTesting.UserManagement;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.Security;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.GoalsManagement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Vector;

public class UserControllerTest {

    @Before
    public void setup(){
        UserController.getInstance().clearUsers();
        GoalsManagement.getInstance().clearGoals();
    }

    @Test
    public void loginAsSystemManagerSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        Assert.assertTrue(userController.getConnectedUsers().containsKey(guestName));
        String adminName = userController.login("admin").getResult();
        Assert.assertTrue(userController.getConnectedUsers().containsKey(adminName));
        Assert.assertTrue(userController.getRegisteredUsers().containsKey(adminName));
    }


    @Test
    public void tester(){
        UserController userController = UserController.getInstance();
    }//todo remove later

    /*@Test
    public void loginAsAlreadyLoggedInUser(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        Assert.assertTrue(userController.getConnectedUsers().containsKey(guestName));
        String adminName = userController.login("admin").getResult();
        Assert.assertFalse(userController.getConnectedUsers().containsKey(guestName));
        Assert.assertTrue(userController.getConnectedUsers().containsKey(adminName));
        Assert.assertTrue(userController.getRegisteredUsers().containsKey(adminName));
        Response<String> loginShouldFailRes = userController.login("admin");
        Assert.assertTrue(loginShouldFailRes.isFailure());
    }*/

    @Test
    public void assigningSupervisorSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        Response<String> supervisorName = userController.login("sup1");
        Assert.assertTrue(userController.getConnectedUsers().containsKey("sup1"));
    }

    @Test
    public void assigningInstructorBySupervisorSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName).getResult();
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Assert.assertTrue(userController.getRegisteredUsers().containsKey("ins1"));
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getWorkField().equals("tech"));
    }

    @Test
    public void assigningInstructorByAdminSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.login("sup1");
        userController.registerUserBySystemManager(adminName, "ins1", "ins1", UserStateEnum.INSTRUCTOR, "sup1", "", "", "", "", "", "");
        Assert.assertTrue(userController.getRegisteredUsers().containsKey("ins1"));
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getWorkField().equals("tech"));
        Assert.assertTrue(userController.getRegisteredUsers().get("sup1").getFirst().getAppointees().getResult().contains("ins1"));
    }

    @Test
    public void assigningInstructorByAdminToAlreadyAppointedUserFail(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Response<String> res = userController.registerUserBySystemManager(adminName, "ins1", "ins1", UserStateEnum.INSTRUCTOR, "sup1", "", "", "", "", "", "");
        Assert.assertTrue(res.isFailure());
    }

    @Test
    public void getAppointedUsers(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.registerUser("sup1", "gensup1", "gensup1", UserStateEnum.GENERAL_SUPERVISOR, "", "", "", "", "");
        List<UserDTO> appointees = userController.getAppointedUsers("sup1").getResult();
        Assert.assertTrue(appointees.size() == 2);
    }

    @Test
    public void assigningSchoolsToInstructorSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        List<String> schools = new Vector<>();
        schools.add("1");
        schools.add("2");
        Response<Boolean> res =  userController.assignSchoolsToUser("sup1", "ins1", schools);
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().size() == 2);
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().contains("1"));
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().contains("2"));
    }

    @Test
    public void removingSchoolsFromInstructorSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        List<String> schools = new Vector<>();
        schools.add("1");
        schools.add("2");
        userController.assignSchoolsToUser("sup1", "ins1", schools);
        List<String> schoolsToRemoveList = new Vector<>();
        schoolsToRemoveList.add("1");
        userController.removeSchoolsFromUser("sup1", "ins1", schoolsToRemoveList);
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().size() == 1);
        Assert.assertFalse(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().contains("1"));
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getSchools().contains("2"));
    }

    @Test
    public void removeAssignedInstructorSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        List<String> schools = new Vector<>();
        schools.add("1");
        schools.add("2");
        userController.assignSchoolsToUser("sup1", "ins1", schools);
        userController.removeUser("sup1", "ins1");
        Assert.assertFalse(userController.getRegisteredUsers().containsKey("ins1"));
        Assert.assertFalse(userController.getRegisteredUsers().get("sup1").getFirst().getAppointments().contains("ins1"));
    }

    @Test
    public void changePasswordBySupervisorFail(){//todo fix later
        Security security = Security.getInstance();
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.changePasswordToUser(adminName, "sup1", "sup111", "sup11");
        //Assert.assertTrue(res.isFailure());
        Assert.assertFalse(userController.getRegisteredUsers().get("sup1").getSecond().equals(security.sha256("sup111")));
    }

    @Test
    public void changePasswordBySupervisorSuccess(){//todo fix fail
        Security security = Security.getInstance();
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.changePasswordToUser(adminName, "sup1", "sup111", "sup111");
        userController.logout(adminName).getResult();
        Response<String> res = userController.login("sup1");
        Assert.assertFalse(res.isFailure());
        Assert.assertTrue(userController.getRegisteredUsers().get("sup1").getSecond().equals(security.sha256("sup111")));
    }

    @Test
    public void changePasswordSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.changePassword("sup1","sup1", "1234", "1234");
        Assert.assertFalse(userController.getConnectedUsers().containsKey("sup1"));
        userController.login("sup1");
        Assert.assertTrue(userController.getConnectedUsers().containsKey("sup1"));
    }

    @Test
    public void updateInfoSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName).getResult();
        Response<String> supervisorName = userController.login("sup1");
        userController.updateInfo("sup1", "1", "", "", "", "");
        Assert.assertTrue(userController.getRegisteredUsers().get("sup1").getFirst().getFirstName().equals("1"));
    }

    @Test
    public void changePasswordToInstructor() {//todo fix
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName).getResult();
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.changePasswordToUser("sup1", "ins1", "ins111", "ins111");
        userController.logout("sup1").getResult();
        userController.login("ins1");
        Assert.assertTrue(userController.getConnectedUsers().containsKey("ins1"));
    }

    @Test
    public void assigningYeadimSuccess(){
        String year = "תשפ\"ג";
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName).getResult();
        String supervisorName = userController.login("sup1").getResult();

        userController.addGoal(supervisorName, new GoalDTO(1, "goal1", "goal1", 1, 1), year);
        userController.addGoal(supervisorName, new GoalDTO(2, "goal2", "goal2", 1, 1), year);
        userController.addGoal(supervisorName, new GoalDTO(3, "goal3", "goal3", 1, 1), year);

        Assert.assertTrue(userController.getGoals(supervisorName, year).getResult().size() == 3);
    }

    @Test
    public void removingGoalSuccess(){
        String year = "תשפ\"ג";
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        String supervisorName = userController.login("sup1").getResult();

        userController.addGoal(supervisorName, new GoalDTO(1, "goal1", "goal1", 1, 1), year);
        userController.addGoal(supervisorName, new GoalDTO(2, "goal2", "goal2", 1, 1), year);
        userController.addGoal(supervisorName, new GoalDTO(3, "goal3", "goal3", 1, 1), year);

        Assert.assertTrue(userController.getGoals(supervisorName, year).getResult().size() == 3);
        int goalToRemoveId = userController.getGoals(supervisorName, year).getResult().get(0).getGoalId();
        userController.removeGoal(supervisorName, year, goalToRemoveId);
        Assert.assertTrue(userController.getGoals(supervisorName, year).getResult().size() == 2);
    }

    @Test
    public void viewAllUsersSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName).getResult();
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.logout("sup1").getResult();
        adminName = userController.login("admin").getResult();
        List<UserDTO> allUsers = userController.getAllUsers(adminName).getResult();
        Assert.assertTrue(allUsers.size() == userController.getRegisteredUsers().size());
    }

    @Test
    public void assigningTwoSupervisorsToTheSameWorkFieldFail(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        Response<String> res = userController.registerUserBySystemManager(adminName, "sup2", "sup2", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        Assert.assertTrue(res.isFailure());
        Assert.assertTrue(userController.getRegisteredUsers().containsKey("sup1"));
        Assert.assertFalse(userController.getRegisteredUsers().containsKey("sup2"));
    }

    @Test
    public void RemovingInstructorAssignedBySupervisorBySystemManagerSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.logout("sup1");
        userController.login("admin");
        Response<Boolean> res = userController.removeUser(adminName, "ins1");
        Assert.assertFalse(res.isFailure());
        Assert.assertFalse(userController.getRegisteredUsers().containsKey("ins1"));
        Assert.assertFalse(userController.getRegisteredUsers().get("sup1").getFirst().getAppointees().getResult().contains("ins1"));
    }

    @Test
    public void transferSupervisionSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.logout("sup1");
        userController.login(adminName);
        Response<Boolean> res = userController.transferSupervision(adminName, "sup1", "new_sup", "new_sup", "", "", "", "", "");
        Assert.assertFalse(res.isFailure());
        Assert.assertTrue(userController.getRegisteredUsers().containsKey("new_sup"));
        Assert.assertFalse(userController.getRegisteredUsers().containsKey("sup1"));
        Assert.assertTrue(userController.getRegisteredUsers().get("new_sup").getFirst().getAppointees().getResult().contains("ins1"));
    }

    @Test
    public void transferSupervisionFail(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.logout("sup1");
        userController.login(adminName);
        Response<Boolean> res = userController.transferSupervision(adminName, "sup1", "sup1", "new_sup", "", "", "", "", "");
        Assert.assertTrue(res.isFailure());
    }

    @Test
    public void transferSupervisionToExistingUserSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.logout("sup1");
        userController.login(adminName);
        Response<Boolean> res = userController.transferSupervisionToExistingUser(adminName, "sup1", "ins1");
        Assert.assertFalse(res.isFailure());
        Assert.assertTrue(userController.getRegisteredUsers().containsKey("ins1"));
        Assert.assertFalse(userController.getRegisteredUsers().containsKey("sup1"));
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getAppointees().getResult().isEmpty());
        userController.logout(adminName);
        userController.login("ins1");
        userController.registerUser("ins1", "ins2", "ins2", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Assert.assertTrue(userController.getRegisteredUsers().containsKey("ins2"));
        Assert.assertTrue(userController.getRegisteredUsers().get("ins1").getFirst().getAppointees().getResult().contains("ins2"));
    }
}
