package UnitTesting.WorkPlan;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.AnnualScheduleGenerator;
import Domain.WorkPlan.Goal;
import Domain.WorkPlan.GoalsManagement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Vector;

public class AnnualScheduleGeneratorTest {

    private UserController userController;
    private String guestName;

    @Before
    public void setup(){
        GoalsManagement.getInstance().clearGoals();
        UserController.getInstance().clearUsers();
        userController = UserController.getInstance();
        guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserByAdmin(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        guestName = userController.logout(adminName).getResult();
        //Response<String> supervisorName = userController.login(guestName, "sup1", "sup1");
    }

    @Test
    public void basicAlgorithmFunctionalitySuccess(){
        String supervisorName = userController.login(guestName, "sup1", "sup1").getResult().getFirst();
        List<Goal> goalList = new Vector<>();
        goalList.add(new Goal("1", 5));
        goalList.add(new Goal("3", 3));
        goalList.add(new Goal("2", 4));
        goalList.add(new Goal("4", 1));
        userController.addGoals(supervisorName, goalList);
        String instructorName = userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "").getResult().getUsername();
        List<String> schools = new Vector<>();
        schools.add("1");
        schools.add("2");
        userController.assignSchoolsToUser(supervisorName, instructorName, schools);
        List<Pair<String, List<String>>> schoolsAndFaults = new Vector<>();
        List<String> school1Faults = new Vector<>();
        school1Faults.add("1");
        school1Faults.add("3");
        school1Faults.add("4");
        school1Faults.add("2");
        List<String> school2Faults = new Vector<>();
        school2Faults.add("4");
        school2Faults.add("2");
        //school2Faults.add("3");

        schoolsAndFaults.add(new Pair<>("1", school1Faults));
        schoolsAndFaults.add(new Pair<>("2", school2Faults));
        String workField = userController.getUser(supervisorName).getWorkField();
        AnnualScheduleGenerator.getInstance().algorithmMock(supervisorName, schoolsAndFaults, workField, GoalsManagement.getInstance().getGoals(workField).getResult());
        userController.getUser(instructorName).getWorkPlan().printMe();
    }
}
