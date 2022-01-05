package Service.Interfaces;

import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Domain.UsersManagment.UserStateEnum;

public interface UserService {
    Response<String> addGuest();
    Response<String> removeGuest(String name);

    Response<Pair<String, UserStateEnum>> login(String currUser, String userToLogin, String password);
    Response<String> logout(String name);

    Response<User> registerUser(UserDTO user);
    Response<Boolean> removeUser(String currUser, String userToRemove);

    Response<Boolean> generateSchedule(String supervisor, int surveyId);
    Response<WorkPlanDTO> viewWorkPlan(String currUser);
}
