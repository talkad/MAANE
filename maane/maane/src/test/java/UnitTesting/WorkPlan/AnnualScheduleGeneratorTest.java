package UnitTesting.WorkPlan;

import Communication.DTOs.GoalDTO;
import Communication.Initializer.ServerContextInitializer;
import Domain.CommonClasses.Pair;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.AnnualScheduleGenerator;
import Domain.WorkPlan.GoalsManagement;
import Persistence.UserQueries;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Vector;

public class AnnualScheduleGeneratorTest {

    private UserController userController;

    @Before
    public void setup(){
        ServerContextInitializer.getInstance().setMockMode();
        ServerContextInitializer.getInstance().setTestMode();

        //this.passwordEncoder = new BCryptPasswordEncoder();
        //ServerContextInitializer.getInstance().setMockMode();
/*        if(!UserControllerTest.initialized){
            //setInitialization();
        }*/
        userController = UserController.getInstance();
        userController.clearUsers();
        GoalsManagement.getInstance().clearGoals();//todo maybe call this from clearUsers
        //userQueries = UserQueries.getInstance();

/*        UserController.getInstance().clearUsers();
        GoalsManagement.getInstance().clearGoals();
        userController = UserController.getInstance();*/
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "a@a.com", "0555555555", "");
        userController.logout(adminName);
        //Response<String> supervisorName = userController.login(guestName, "sup1", "sup1");
    }

    @Test
    public void basicAlgorithmFunctionalitySuccess(){
        Integer year = 2022;// "תשפ\"ג";
        String supervisorName = userController.login("sup1").getResult();

        userController.addGoal(supervisorName, new GoalDTO(0,"1", "desc", 1,5), year);
        userController.addGoal(supervisorName, new GoalDTO(1, "3", "desc", 1, 3), year);
        userController.addGoal(supervisorName, new GoalDTO(2, "2", "desc", 1,4), year);
        userController.addGoal(supervisorName, new GoalDTO(3, "4","desc", 1, 1), year);

        String instructorName = userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "a@a.com", "0555555555", "").getResult();
        List<String> schools = new Vector<>();
        schools.add("1");
        schools.add("2");
        userController.assignSchoolsToUser(supervisorName, instructorName, schools);
        List<Pair<String, List<Integer>>> schoolsAndFaults = new Vector<>();
        List<Integer> school1Faults = new Vector<>();
        school1Faults.add(0);
        school1Faults.add(2);
        school1Faults.add(3);
        school1Faults.add(1);
        List<Integer> school2Faults = new Vector<>();
        school2Faults.add(3);
        school2Faults.add(1);

        schoolsAndFaults.add(new Pair<>("1", school1Faults));
        schoolsAndFaults.add(new Pair<>("2", school2Faults));
        String workField = userController.getUser(supervisorName).getWorkField();
        AnnualScheduleGenerator.getInstance().algorithmMock(supervisorName, schoolsAndFaults, workField, GoalsManagement.getInstance().getGoals(workField, year).getResult(), year);
        userController.login("ins1");
        userController.viewWorkPlan(instructorName, year).getResult().printMe();
        //userController.getUser(instructorName).getWorkPlanByYear(year).getResult().printMe();
    }

}
