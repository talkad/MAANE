package UnitTesting.WorkPlan.UserManagement;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Communication.Initializer.ServerContextInitializer;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.GoalsManagement;
import Persistence.Connect;
import Persistence.UserQueries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.*;

import java.util.List;
import java.util.Vector;


public class UserControllerTest {

    private PasswordEncoder passwordEncoder;
    private UserController userController;
    private UserQueries userQueries;
    //private static boolean initialized = false;

    
    @Before
    public void setup(){
        this.passwordEncoder = new BCryptPasswordEncoder();
        ServerContextInitializer.getInstance().setMockMode();
/*        if(!UserControllerTest.initialized){
            //setInitialization();
        }*/
        userController = UserController.getInstance();
        userController.clearUsers();
        GoalsManagement.getInstance().clearGoals();//todo maybe call this from clearUsers
        userQueries = UserQueries.getInstance();
    }

    @Test
    public void loginAsSystemManagerSuccess(){
        String adminName = userController.login("admin").getResult();
        Assert.assertTrue(userController.getConnectedUsers().containsKey(adminName));
        Assert.assertTrue(userQueries.userExists(adminName));
    }

    @Test
    public void assigningSupervisorSuccess(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        Response<String> supervisorName = userController.login("sup1");
        Assert.assertTrue(userController.getConnectedUsers().containsKey("sup1"));
        Assert.assertTrue(userQueries.userExists("sup1"));
    }

    @Test
    public void assigningInstructorBySupervisorSuccess(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Assert.assertTrue(userQueries.userExists("ins1"));
        Assert.assertTrue(userQueries.getFullUser("ins1").getResult().getWorkField().equals("tech"));
    }

    @Test
    public void emailTest(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.sendCoordinatorEmails("sup1", "https://www.cs.bgu.ac.il/~comp211/Main");//todo assign coordinators
    }

    @Test
    public void assigningInstructorByAdminSuccess(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.login("sup1");
        userController.registerUserBySystemManager(adminName, "ins1", "ins1", UserStateEnum.INSTRUCTOR, "sup1", "", "", "", "", "", "");
        Assert.assertTrue(userQueries.userExists("ins1"));
        Assert.assertTrue(userQueries.getFullUser("ins1").getResult().getWorkField().equals("tech"));
        Assert.assertTrue(userQueries.getFullUser("sup1").getResult().getAppointments().contains("ins1"));
    }

    @Test
    public void assigningInstructorByAdminToAlreadyAppointedUserFail(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Response<String> res = userController.registerUserBySystemManager(adminName, "ins1", "ins1", UserStateEnum.INSTRUCTOR, "sup1", "", "", "", "", "", "");
        Assert.assertTrue(res.isFailure());
    }

    @Test
    public void getAppointedUsers(){
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
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        List<String> schools = new Vector<>();
        schools.add("1");
        schools.add("2");
        Response<Boolean> res =  userController.assignSchoolsToUser("sup1", "ins1", schools);
        Assert.assertTrue(userQueries.getFullUser("ins1").getResult().getSchools().size() == 2);
        Assert.assertTrue(userQueries.getFullUser("ins1").getResult().getSchools().contains("1"));
        Assert.assertTrue(userQueries.getFullUser("ins1").getResult().getSchools().contains("2"));
    }

    @Test
    public void removingSchoolsFromInstructorSuccess(){
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
        Assert.assertTrue(userQueries.getFullUser("ins1").getResult().getSchools().size() == 1);
        Assert.assertFalse(userQueries.getFullUser("ins1").getResult().getSchools().contains("1"));
        Assert.assertTrue(userQueries.getFullUser("ins1").getResult().getSchools().contains("2"));
    }

    @Test
    public void removeAssignedInstructorSuccess(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        List<String> schools = new Vector<>();
        schools.add("1");
        schools.add("2");
        userController.assignSchoolsToUser("sup1", "ins1", schools);
        userQueries.getFullUser("sup1");
        userController.removeUser("sup1", "ins1");
        Assert.assertFalse(userQueries.userExists("ins1"));
        Assert.assertFalse(userQueries.getFullUser("sup1").getResult().getAppointments().contains("ins1"));
    }

    @Test
    public void changePasswordBySupervisorFail(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.changePasswordToUser(adminName, "sup1", "sup111", "sup11");
        Assert.assertFalse(passwordEncoder.matches("sup111", userQueries.getFullUser("sup1").getResult().getPassword()));
    }

    @Test
    public void changePasswordBySupervisorSuccess(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.changePasswordToUser(adminName, "sup1", "sup111", "sup111");
        Assert.assertTrue(passwordEncoder.matches("sup111", userQueries.getFullUser("sup1").getResult().getPassword()));//userController.getConnectedUsers().containsKey("ins1"));
    }

    @Test
    public void changePasswordSuccess(){//todo check the password itself not that the user is connected
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.login("sup1");
        userController.changePassword("sup1","sup1", "1234", "1234");
        Assert.assertTrue(passwordEncoder.matches("1234", userQueries.getFullUser("sup1").getResult().getPassword()));//userController.getConnectedUsers().containsKey("ins1"));
    }

    @Test
    public void updateInfoSuccess(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        Response<String> supervisorName = userController.login("sup1");
        userController.updateInfo("sup1", "1", "", "", "", "");
        Assert.assertTrue(userQueries.getFullUser("sup1").getResult().getFirstName().equals("1"));
    }

    @Test
    public void changePasswordToInstructor() {
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.changePasswordToUser("sup1", "ins1", "ins111", "ins111");
        Assert.assertTrue(passwordEncoder.matches("ins111", userQueries.getFullUser("ins1").getResult().getPassword()));//userController.getConnectedUsers().containsKey("ins1"));
    }

    @Test
    public void assigningYeadimSuccess(){
        String year = "תשפ\"ג";
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        String supervisorName = userController.login("sup1").getResult();

        userController.addGoal(supervisorName, new GoalDTO(1, "goal1", "goal1", 1, 1), year);
        userController.addGoal(supervisorName, new GoalDTO(2, "goal2", "goal2", 1, 1), year);
        userController.addGoal(supervisorName, new GoalDTO(3, "goal3", "goal3", 1, 1), year);

        Assert.assertTrue(userController.getGoals(supervisorName, year).getResult().size() == 3);
    }

    @Test
    public void removingGoalSuccess(){
        String year = "תשפ\"ג";
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
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.logout("sup1");
        adminName = userController.login("admin").getResult();
        List<UserDTO> allUsers = userController.getAllUsers(adminName).getResult();
        Assert.assertTrue(allUsers.size() == userQueries.getUsers().size());
    }

    @Test
    public void assigningTwoSupervisorsToTheSameWorkFieldFail(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        Response<String> res = userController.registerUserBySystemManager(adminName, "sup2", "sup2", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        Assert.assertTrue(res.isFailure());
        Assert.assertTrue(userQueries.userExists("sup1"));
        Assert.assertFalse(userQueries.userExists("sup2"));
    }

    @Test
    public void RemovingInstructorAssignedBySupervisorBySystemManagerSuccess(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.logout("sup1");
        userController.login("admin");
        Response<Boolean> res = userController.removeUser(adminName, "ins1");
        Assert.assertFalse(res.isFailure());
        Assert.assertFalse(userQueries.userExists("ins1"));
        Assert.assertFalse(userQueries.getFullUser("sup1").getResult().getAppointments().contains("ins1"));
    }

    @Test
    public void transferSupervisionSuccess(){
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.logout("sup1");
        userController.login(adminName);
        Response<Boolean> res = userController.transferSupervision(adminName, "sup1", "new_sup", "new_sup", "", "", "", "", "");
        Assert.assertFalse(res.isFailure());
        Assert.assertTrue(userQueries.userExists("new_sup"));
        Assert.assertFalse(userQueries.userExists("sup1"));
        Assert.assertTrue(userQueries.getFullUser("new_sup").getResult().getAppointments().contains("ins1"));
    }

    @Test
    public void transferSupervisionFail(){
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
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.logout("sup1");
        userController.login(adminName);
        Response<Boolean> res = userController.transferSupervisionToExistingUser(adminName, "sup1", "ins1");
        Assert.assertFalse(res.isFailure());
        Assert.assertTrue(userQueries.userExists("ins1"));
        Assert.assertFalse(userQueries.userExists("sup1"));
        Assert.assertTrue(userQueries.getFullUser("ins1").getResult().getAppointments().isEmpty());
        userController.logout(adminName);
        userController.login("ins1");
        userController.registerUser("ins1", "ins2", "ins2", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Assert.assertTrue(userQueries.userExists("ins2"));
        Assert.assertTrue(userQueries.getFullUser("ins1").getResult().getAppointments().contains("ins2"));
    }
}
