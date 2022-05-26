package Communication.Service.Interfaces;

import Communication.DTOs.ActivityDTO;
import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Persistence.DbDtos.UserDBDTO;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface UserService {

    Response<String> login(String username);
    Response<String> logout(String name);

    Response<String> registerUser(String currUser, UserDTO user);
    Response<String> registerUserBySystemManager(String currUser, UserDTO user, String optionalSupervisor);

    Response<Boolean> removeUser(String currUser, String userToRemove);

    Response<WorkPlanDTO> viewWorkPlan(String currUser, Integer year, Integer month);

    Response<WorkPlanDTO> viewInstructorWorkPlan(String currUser, String instructor, Integer year, Integer month);

    Response<List<UserDTO>> getAppointedUsers(String currUser);

    Response<Boolean> addGoal(String currUser, GoalDTO goalDTO, Integer year);

    Response<Boolean> removeGoal(String currUser, Integer year, int goalId);

    Response<List<GoalDTO>> getGoals(String currUser, Integer year);

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

    Response<UserDBDTO> getCoordinator(String currUser, String workField, String symbol);

    Response<List<String>> allWorkFields(String currUser);

    Response<Boolean> setWorkingTime(String currUser, int workDay, LocalTime act1Start, LocalTime act1End, LocalTime act2Start, LocalTime act2End);

    Response<UserDBDTO> getWorkHours(String instructor);

    Response<Boolean> editActivity(String currUser, LocalDateTime currActStart, LocalDateTime newActStart, LocalDateTime newActEnd);

    Response<Boolean> addActivity(String currUser, LocalDateTime startAct, ActivityDTO activity);

    Response<Boolean> removeActivity(String currUser, LocalDateTime startAct);

    Response<Boolean> changePasswordTester(String currUser, String newPassword);

    void resetDB();

    Response<Boolean> removeUserTester(String currUser, String userToRemove);
}