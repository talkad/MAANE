package AcceptanceTesting.Tests;

import Communication.DTOs.UserDTO;
import Communication.Initializer.ServerContextInitializer;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserStateEnum;
import Persistence.DbDtos.SchoolDBDTO;
import Persistence.DbDtos.UserDBDTO;
import Persistence.UserQueries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;

public class UserManagementTests extends AcceptanceTests {

    private final String adminName = "admin";
    private final String supervisorName = "supervisor";
    private final String instructorName = "instructor";
    private final String schoolSymbol = "1";
    private PasswordEncoder passwordEncoder;



    @Before
    public void setUp(){
        ServerContextInitializer.getInstance().setMockMode();
        ServerContextInitializer.getInstance().setTestMode();
        super.setUp(true);
        userBridge.resetDB();
        this.passwordEncoder = new BCryptPasswordEncoder();

    }

    /**
     * admin create a supervisor in 'tech' workField
     * the supervisor assign instructor and coordinator to the same workField
     * the instructor update its info
     * the admin transfers supervision to the instructor assigned by the supervisor and the supervisor is removed
     */
    @Test
    public void legalSystemUseTest(){
        dataBridge.insertSchool(new SchoolDBDTO("1", "testing school", "beer sheva", "", "", "", "", "", "", "", "", 1000000, "", "", "", "", 30));
        userBridge.login(adminName);
        userBridge.registerUserBySystemManager(adminName, new UserDTO(adminName, "tech", supervisorName, "1234abcd", UserStateEnum.SUPERVISOR,
                "sup", "visor", "a@gmail.com", "055-555-5555", "Beer Sheva", null), "irrelevant");
        userBridge.login(supervisorName);
        userBridge.registerUser(supervisorName, new UserDTO(supervisorName, "irrelevant", instructorName, "1234abcd", UserStateEnum.INSTRUCTOR,
                "ins", "tructor", "a@gmail.com", "055-555-5555", "Beer Sheva", null));
        Assert.assertTrue(UserQueries.getInstance().userExists(supervisorName));
        userBridge.assignSchoolToUser(supervisorName, instructorName, schoolSymbol);
        Assert.assertEquals("ins", userBridge.getAppointedUsers(supervisorName).getResult().get(0).getFirstName());

        dataBridge.assignCoordinator(supervisorName, "irrelevant", "coord", "inator", "a@gmail.com", "0555555555", schoolSymbol);
        userBridge.login(instructorName);
        userBridge.updateInfo(instructorName, "new_first_name", "new_last_name", "new@gmail.com", "0555555555", "Tel Aviv");
        Assert.assertEquals("new_first_name", userBridge.getUserInfo(instructorName).getResult().getFirstName());

        userBridge.transferSupervisionToExistingUser(adminName, supervisorName, instructorName);
        Assert.assertFalse(UserQueries.getInstance().userExists(supervisorName));
        Assert.assertTrue(UserQueries.getInstance().userExists(instructorName));
        Response<UserDBDTO> insUser = UserQueries.getInstance().getFullUser(instructorName);
        Assert.assertEquals(UserStateEnum.SUPERVISOR, insUser.getResult().getStateEnum());
        Assert.assertEquals("coord", userBridge.getCoordinator(instructorName, "irrelevant", schoolSymbol).getResult().getFirstName());
    }


    /**
     * admin create a supervisor in 'tech' workfield
     * create new goals and survey answers
     * generate a new workplan accordingly
     */
    @Test
    public void workPlanGeneratingTests(){
        // ...
    }

    /**
     * admin create a supervisor in 'tech' workField
     * the supervisor registers an instructor
     * the instructor updates its information, working hours, password and his assigned school information
     */
    @Test
    public void fullUserAssignmentAndDetailEditingTest(){
        dataBridge.insertSchool(new SchoolDBDTO("1", "testing school", "beer sheva", "", "", "", "", "", "", "", "", 1000000, "", "", "", "", 30));
        userBridge.login(adminName);
        userBridge.registerUserBySystemManager(adminName, new UserDTO(adminName, "tech", supervisorName, "1234abcd", UserStateEnum.SUPERVISOR,
                "sup", "visor", "a@gmail.com", "055-555-5555", "Beer Sheva", null), "irrelevant");
        userBridge.login(supervisorName);
        userBridge.registerUser(supervisorName, new UserDTO(supervisorName, "irrelevant", instructorName, "1234abcd", UserStateEnum.INSTRUCTOR,
                "ins", "tructor", "a@gmail.com", "055-555-5555", "Beer Sheva", null));
        Assert.assertTrue(UserQueries.getInstance().userExists(supervisorName));
        userBridge.assignSchoolToUser(supervisorName, instructorName, schoolSymbol);
        userBridge.login(instructorName);
        userBridge.changePassword(instructorName, "1234abcd", "abcd1234", "abcd1234");
        userBridge.updateInfo(instructorName, "new name", "new last name", "newEmail@gmail.com", "0555555555", "Tel Aviv");
        userBridge.setWorkingTime(instructorName, 4, LocalTime.of(11, 0).toString(), LocalTime.of(13, 0).toString(), LocalTime.of(14, 0).toString(), LocalTime.of(16, 0).toString());
        dataBridge.updateSchool("1", new SchoolDBDTO("1", "testing school", "beer sheva", "", "", "", "new principal", "new manager", "", "", "", 1000000, "", "", "", "", 30));

        Response<UserDTO> userInfo = userBridge.getUserInfo(instructorName);
        Assert.assertEquals("new name", userInfo.getResult().getFirstName());

        Response<UserDBDTO> userWorkHours = userBridge.getWorkHours(instructorName);
        Assert.assertEquals(4, userWorkHours.getResult().getWorkDay());
        Assert.assertEquals(userWorkHours.getResult().getAct1Start(), LocalTime.of(11, 0));

        Assert.assertTrue(passwordEncoder.matches("abcd1234", UserQueries.getInstance().getFullUser(instructorName).getResult().getPassword()));

        Response<SchoolDBDTO> usersSchool = dataBridge.getSchool(instructorName, "1");
        Assert.assertEquals("new principal", usersSchool.getResult().getPrincipal());
        Assert.assertEquals("new manager", usersSchool.getResult().getManager());
    }

}
