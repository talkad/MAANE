package Communication.Service.Interfaces;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;

import java.util.List;

public interface UserService {

    Response<String> login(String username);
    Response<String> logout(String name);

    Response<String> registerUser(String currUser, UserDTO user);
    Response<String> registerUserBySystemManager(String currUser, UserDTO user, String optionalSupervisor);

    Response<Boolean> removeUser(String currUser, String userToRemove);

    Response<WorkPlanDTO> viewWorkPlan(String currUser, String year);

    Response<List<UserDTO>> getAppointedUsers(String currUser);

    Response<Boolean> addGoal(String currUser, GoalDTO goalDTO, String year);

    Response<Boolean> removeGoal(String currUser, String year, int goalId);

    Response<List<GoalDTO>> getGoals(String currUser, String year);

    Response<User> getUserRes(String username); //for testing purposes only

    Response<Boolean> assignSchoolsToUser(String currUser, String userToAssignName, List<String> schools);

    Response<Boolean> verifyUser(String currUser, String password);

    Response<UserDTO> getUserInfo(String currUser);

    Response<Boolean> updateInfo(String currUser, String firstName, String lastName, String email, String phoneNumber, String city);

    Response<Boolean> changePasswordToUser(String currUser, String userToChangePassword, String newPassword, String confirmPassword);

    Response<Boolean> changePassword(String currUser, String currPassword, String newPassword, String confirmPassword);

    Response<List<UserDTO>> getAllUsers(String currUser);

    Response<Boolean> sendCoordinatorEmails(String currUser, String surveyLink, String surveyToken);

    Response<Boolean> transferSupervision(String currUser, String currSupervisor, String newSupervisor, String password, String firstName, String lastName, String email, String phoneNumber, String city);

    Response<List<UserDTO>> getSupervisors(String currUser);

    Response<Boolean> transferSupervisionToExistingUser(String currUser, String currSupervisor, String newSupervisor);

}
