package Service.Interfaces;

import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.Goal;

import java.util.List;

public interface UserService {
    Response<String> addGuest();
    Response<String> removeGuest(String name);

    Response<Pair<String, UserStateEnum>> login(String currUser, String userToLogin, String password);
    Response<String> logout(String name);

    Response<User> registerUser(UserDTO user);
    Response<User> registerUserBySystemManager(UserDTO user, String optionalSupervisor);

    Response<Boolean> removeUser(String currUser, String userToRemove);

    Response<WorkPlanDTO> viewWorkPlan(String currUser);

    Response<List<UserDTO>> getAppointedUsers(String currUser);

    Response<Boolean> addGoals(String currUser, List<Goal> goalList);

    Response<User> getUserRes(String username); //for testing purposes only

    Response<Boolean> assignSchoolsToUser(String currUser, String userToAssignName, List<String> schools);

}
