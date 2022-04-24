package UnitTesting.UserManagement;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.GoalsManagement;
import Persistence.UserDBDTO;
import Persistence.UserQueries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Vector;

public class UserControllerTest {

    private PasswordEncoder passwordEncoder;
/*    @InjectMocks
    private UserController userController;

    @Mock
    private UserQueries userQueries;*/

    @Before
    public void setup(){
        //MockitoAnnotations.openMocks(this);
        this.passwordEncoder = new BCryptPasswordEncoder();

        UserController.getInstance().clearUsers();
        GoalsManagement.getInstance().clearGoals();
    }

    @Test
    public void loginAsSystemManagerSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        Assert.assertTrue(userController.getConnectedUsers().containsKey(adminName));
        Assert.assertTrue(UserQueries.getInstance().userExists(adminName));
    }

    @Test
    public void tester(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        String supervisorName = userController.login("sup1").getResult();
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        List<String> schools = new Vector<>();
        schools.add("a");
        schools.add("b");
        schools.add("c");

        userController.assignSchoolsToUser(supervisorName, "ins1", schools);
        List<String> schoolsToRemove = new Vector<>();
        schoolsToRemove.add("a");
        schoolsToRemove.add("b");
        userController.removeSchoolsFromUser(supervisorName, "ins1", schoolsToRemove);
        Response<UserDBDTO> userDBDTOResponse = UserQueries.getInstance().getFullUser("ins1");
        System.out.println(userDBDTOResponse.getResult().getUsername() + " " + userDBDTOResponse.getResult().getSchools());
        System.out.println(UserQueries.getInstance().getFullUser(supervisorName).getResult().getAppointments());
    }//todo remove later


    @Test
    public void assigningSupervisorSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        Response<String> supervisorName = userController.login("sup1");
        Assert.assertTrue(userController.getConnectedUsers().containsKey("sup1"));
        Assert.assertTrue(UserQueries.getInstance().userExists("sup1"));

    }

    @Test
    public void assigningInstructorBySupervisorSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Assert.assertTrue(UserQueries.getInstance().userExists("ins1"));
        Assert.assertTrue(UserQueries.getInstance().getFullUser("ins1").getResult().getWorkField().equals("tech"));
    }

    @Test
    public void assigningInstructorByAdminSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        System.out.println(userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "").isFailure());
        userController.login("sup1");
        System.out.println(userController.registerUserBySystemManager(adminName, "ins1", "ins1", UserStateEnum.INSTRUCTOR, "sup1", "", "", "", "", "", "").isFailure());
        Assert.assertTrue(UserQueries.getInstance().userExists("ins1"));
        Assert.assertTrue(UserQueries.getInstance().getFullUser("ins1").getResult().getWorkField().equals("tech"));
        Assert.assertTrue(UserQueries.getInstance().getFullUser("sup1").getResult().getAppointments().contains("ins1"));
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
        Assert.assertTrue(UserQueries.getInstance().getFullUser("ins1").getResult().getSchools().size() == 2);
        Assert.assertTrue(UserQueries.getInstance().getFullUser("ins1").getResult().getSchools().contains("1"));
        Assert.assertTrue(UserQueries.getInstance().getFullUser("ins1").getResult().getSchools().contains("2"));
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
        Assert.assertTrue(UserQueries.getInstance().getFullUser("ins1").getResult().getSchools().size() == 1);
        Assert.assertFalse(UserQueries.getInstance().getFullUser("ins1").getResult().getSchools().contains("1"));
        Assert.assertTrue(UserQueries.getInstance().getFullUser("ins1").getResult().getSchools().contains("2"));
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
        System.out.println(UserQueries.getInstance().getFullUser("sup1").getResult().getAppointments());
        userController.removeUser("sup1", "ins1");
        Assert.assertFalse(UserQueries.getInstance().userExists("ins1"));
        Assert.assertFalse(UserQueries.getInstance().getFullUser("sup1").getResult().getAppointments().contains("ins1"));
    }

    @Test
    public void changePasswordBySupervisorFail(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.changePasswordToUser(adminName, "sup1", "sup111", "sup11");
        Assert.assertFalse(UserQueries.getInstance().getFullUser("sup1").getResult().getPassword().equals(passwordEncoder.encode("sup111")));
    }

    @Test
    public void changePasswordBySupervisorSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.changePasswordToUser(adminName, "sup1", "sup111", "sup111");
        userController.logout(adminName);
        Response<String> res = userController.login("sup1");
        Assert.assertFalse(res.isFailure());
        Assert.assertTrue(UserQueries.getInstance().getFullUser("sup1").getResult().getPassword().equals(passwordEncoder.encode("sup111")));
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
        userController.logout(adminName);
        Response<String> supervisorName = userController.login("sup1");
        userController.updateInfo("sup1", "1", "", "", "", "");
        Assert.assertTrue(UserQueries.getInstance().getFullUser("sup1").getResult().getFirstName().equals("1"));
    }

    @Test
    public void changePasswordToInstructor() {
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.changePasswordToUser("sup1", "ins1", "ins111", "ins111");
        Assert.assertTrue(UserQueries.getInstance().getFullUser("ins1").getResult().getPassword().equals(passwordEncoder.encode("ins111")));//userController.getConnectedUsers().containsKey("ins1"));
    }

    @Test
    public void assigningYeadimSuccess(){
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
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        userController.logout("sup1");
        adminName = userController.login("admin").getResult();
        List<UserDTO> allUsers = userController.getAllUsers(adminName).getResult();
        Assert.assertTrue(allUsers.size() == UserQueries.getInstance().getUsers().size());
    }

    @Test
    public void assigningTwoSupervisorsToTheSameWorkFieldFail(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        Response<String> res = userController.registerUserBySystemManager(adminName, "sup2", "sup2", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        Assert.assertTrue(res.isFailure());
        Assert.assertTrue(UserQueries.getInstance().userExists("sup1"));
        Assert.assertFalse(UserQueries.getInstance().userExists("sup2"));
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
        Assert.assertFalse(UserQueries.getInstance().userExists("ins1"));
        Assert.assertFalse(UserQueries.getInstance().getFullUser("sup1").getResult().getAppointments().contains("ins1"));
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
        Assert.assertTrue(UserQueries.getInstance().userExists("new_sup"));
        Assert.assertFalse(UserQueries.getInstance().userExists("sup1"));
        Assert.assertTrue(UserQueries.getInstance().getFullUser("new_sup").getResult().getAppointments().contains("ins1"));
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
        Assert.assertTrue(UserQueries.getInstance().userExists("ins1"));
        Assert.assertFalse(UserQueries.getInstance().userExists("sup1"));
        Assert.assertTrue(UserQueries.getInstance().getFullUser("ins1").getResult().getAppointments().isEmpty());
        userController.logout(adminName);
        userController.login("ins1");
        userController.registerUser("ins1", "ins2", "ins2", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Assert.assertTrue(UserQueries.getInstance().userExists("ins2"));
        Assert.assertTrue(UserQueries.getInstance().getFullUser("ins1").getResult().getAppointments().contains("ins2"));
    }
}
