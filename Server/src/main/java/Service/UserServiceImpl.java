package Service;

import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.AnnualScheduleGenerator;
import Domain.WorkPlan.Goal;
import Service.Interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private static class CreateSafeThreadSingleton {
        private static final UserServiceImpl INSTANCE = new UserServiceImpl();
    }

    public static UserServiceImpl getInstance() {
        return UserServiceImpl.CreateSafeThreadSingleton.INSTANCE;
    }

    @Override
    public Response<String> addGuest() {
        Response<String> res = UserController.getInstance().addGuest();

        if(res.isFailure())
            log.error("failed to add new guest");
        else
            log.info("created new guest");

        return res;
    }

    @Override
    public Response<String> removeGuest(String name) {
        Response<String> res = UserController.getInstance().removeGuest(name);

        if(res.isFailure())
            log.error("failed to remove guest {}", name);
        else
            log.info("removing guest with name: {}", name);

        return res;
    }

    @Override
    public Response<Pair<String, UserStateEnum>> login(String currUser, String userToLogin, String password) {
        Response<Pair<String, UserStateEnum>> res = UserController.getInstance().login(currUser, userToLogin, password);

        if(res.isFailure())
            log.error("failed to login with user {}", userToLogin);
        else
            log.info("login successful for {}", userToLogin);

        return res;
    }

    @Override
    public Response<String> logout(String name) {
        Response<String> res = UserController.getInstance().logout(name);

        if(res.isFailure())
            log.error("failed to logout user {}", name);
        else
            log.info("logged out for user {}", name);

        return res;
    }

    @Override
    public Response<User> registerUser(UserDTO user) {
        Response<User> res = UserController.getInstance().registerUser(user.getCurrUser(), user.getUserToRegister(), user.getPassword(),
                user.getUserStateEnum(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getCity());

        if(res.isFailure())
            log.error("failed to register user {}", user.getUserToRegister());
        else
            log.info("user {} registered successfully", user.getUserToRegister());

        return res;
    }

    @Override
    public Response<User> registerUserBySystemManager(UserDTO user, String optionalSupervisor) {
        Response<User> res = UserController.getInstance().registerUserBySystemManager(user.getCurrUser(), user.getUserToRegister(), user.getPassword(), user.getUserStateEnum(), optionalSupervisor, user.getWorkField(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getCity());

        if(res.isFailure())
            log.error("failed to register user {}", user.getUserToRegister());
        else
            log.info("user {} registered successfully", user.getUserToRegister());

        return res;
    }

    @Override
    public Response<Boolean> removeUser(String currUser, String userToRemove) {
        Response<Boolean> res = UserController.getInstance().removeUser(currUser, userToRemove);

        if(res.isFailure())
            log.error("failed to remove user {}", userToRemove);
        else
            log.info("removed user {}", userToRemove);

        return res;
    }

    @Override
    public Response<WorkPlanDTO> viewWorkPlan(String currUser) {
        Response<WorkPlanDTO> res = UserController.getInstance().viewWorkPlan(currUser);

        if(res.isFailure())
            log.error("user {} cannot view plan", currUser);
        else
            log.info("user {} viewed plan", currUser);

        return res;
    }

    @Override
    public Response<List<UserDTO>> getAppointedUsers(String currUser) {//todo implement on client side
        Response<List<UserDTO>> res = UserController.getInstance().getAppointedUsers(currUser);

        if(res.isFailure())
            log.error("failed to get Appointed users for the user {}", currUser);
        else
            log.info("user {} successfully received appointed users", currUser);
        return res;
    }

    //for testing purposes only
    @Override
    public Response<User> getUserRes(String username) {
        return UserController.getInstance().getUserRes(username);
    }

    @Override
    public Response<Boolean> assignSchoolsToUser(String currUser, String userToAssignName, List<String> schools) {
        Response<Boolean> res = UserController.getInstance().assignSchoolsToUser(currUser, userToAssignName, schools);

        if(res.isFailure())
            log.error("failed to assign schools for the user {}", userToAssignName);
        else
            log.info("user {} was successfully assigned to the schools", userToAssignName);
        return res;    }

    public Response<Boolean> addGoals(String currUser, List<Goal> goalList){
        Response<Boolean> res = UserController.getInstance().addGoals(currUser, goalList);

        if(res.isFailure())
            log.error("failed to add goals by {}", currUser);
        else
            log.info("user {} successfully added goals", currUser);
        return res;
    }

}
