package UnitTesting.DataManagement;

import Domain.CommonClasses.Response;
import Domain.DataManagement.DataController;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.GoalsManagement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DataControllerTests {

    @Before
    public void setup(){//todo probably set mock mode
        UserController.getInstance().clearUsers();
        GoalsManagement.getInstance().clearGoals();
        DataController.getInstance().clearSchools();
        DataController.getInstance().addOneSchool();
    }

    @Test
    public void assignCoordinatorSuccess(){
        UserController userController = UserController.getInstance();
        DataController dataController = DataController.getInstance();
        String adminName = userController.login("admin").getResult();
        String supervisorName = userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "").getResult();
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Response<Boolean> res = dataController.assignCoordinator(supervisorName, "irrelevant", "coordinator", "1", "email@gmail.com", "5555555555", "1");
        Assert.assertFalse(res.isFailure());
        //todo Assert.assertTrue(dataController.getSchool("1").getCoordinators().get("tech").getFirstName().equals("coordinator"));
    }

    @Test
    public void assignTwoCoordinatorToTheSameFieldFail(){
        UserController userController = UserController.getInstance();
        DataController dataController = DataController.getInstance();
        String adminName = userController.login("admin").getResult();
        String supervisorName = userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "").getResult();
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Response<Boolean> res1 = dataController.assignCoordinator(supervisorName, "irrelevant", "coordinator", "1", "email@gmail.com", "5555555555", "1");
        Response<Boolean> res2 = dataController.assignCoordinator(supervisorName, "irrelevant", "coordinator2", "2", "email@gmail.com", "5555555555", "1");

        Assert.assertFalse(res1.isFailure());
        Assert.assertTrue(res2.isFailure());

        //todo Assert.assertFalse(dataController.getSchool("1").getCoordinators().get("tech").getFirstName().equals("coordinator2"));

    }

    @Test
    public void removeCoordinatorSuccess(){
        UserController userController = UserController.getInstance();
        DataController dataController = DataController.getInstance();
        String adminName = userController.login("admin").getResult();
        String supervisorName = userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "").getResult();
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Response<Boolean> res = dataController.assignCoordinator(supervisorName, "irrelevant", "coordinator", "1", "email@gmail.com", "5555555555", "1");
        Assert.assertFalse(res.isFailure());
        //todo Assert.assertTrue(dataController.getSchool("1").getCoordinators().get("tech").getFirstName().equals("coordinator"));
        Response<Boolean> res2 = dataController.removeCoordinator(supervisorName, "irrelevant", "1");
        Assert.assertFalse(res2.isFailure());
        //todo Assert.assertFalse(dataController.getSchool("1").getCoordinators().containsKey("tech"));
    }

    @Test
    public void removeCoordinatorFail(){
        UserController userController = UserController.getInstance();
        DataController dataController = DataController.getInstance();
        String adminName = userController.login("admin").getResult();
        String supervisorName = userController.registerUserBySystemManager(adminName, "sup1", "sup1", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "").getResult();
        userController.logout(adminName);
        userController.login("sup1");
        userController.registerUser("sup1", "ins1", "ins1", UserStateEnum.INSTRUCTOR, "", "", "", "", "");
        Response<Boolean> res = dataController.removeCoordinator(supervisorName, "irrelevant", "1");
        Assert.assertTrue(res.isFailure());
        //todo Assert.assertTrue(dataController.getSchool("1").getCoordinators().keySet().size() == 0);
    }
}
