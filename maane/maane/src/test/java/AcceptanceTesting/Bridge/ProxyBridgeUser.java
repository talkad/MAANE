package AcceptanceTesting.Bridge;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Communication.Service.Interfaces.UserService;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Persistence.Connect;

import java.util.List;

public class ProxyBridgeUser implements UserService {
    private UserService real;

    public ProxyBridgeUser() {
        real = null;
    }

    public void setRealBridge(UserService implementation) {
        if (real == null) {
            real = implementation;
        }
    }

    @Override
    public Response<String> login(String username) {
        if (real != null) {
            return real.login(username);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<String> logout(String name) {
        if (real != null) {
            return real.logout(name);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<String> registerUser(String currUser, UserDTO user) {
        if (real != null) {
            return real.registerUser(currUser, user);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<String> registerUserBySystemManager(String currUser, UserDTO user, String optionalSupervisor) {
        if (real != null) {
            return real.registerUserBySystemManager(currUser, user, optionalSupervisor);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> removeUser(String currUser, String userToRemove) {
        if (real != null) {
            return real.removeUser(currUser, userToRemove);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<WorkPlanDTO> viewWorkPlan(String currUser, String year) {
        if (real != null) {
            return real.viewWorkPlan(currUser, year);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<List<UserDTO>> getAppointedUsers(String currUser) {
        if (real != null) {
            return real.getAppointedUsers(currUser);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> addGoal(String currUser, GoalDTO goalDTO, String year) {
        if (real != null) {
            return real.addGoal(currUser, goalDTO, year);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> removeGoal(String currUser, String year, int goalId) {
        if (real != null) {
            return real.removeGoal(currUser, year, goalId);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<List<GoalDTO>> getGoals(String currUser, String year) {
        if (real != null) {
            return real.getGoals(currUser, year);
        }

        return new Response<>(null, true, "not implemented");
    }

    public Response<User> getUserRes(String username) {
        if (real != null) {
            return real.getUserRes(username);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> assignSchoolsToUser(String currUser, String userToAssignName, List<String> schools) {
        if (real != null) {
            return real.assignSchoolsToUser(currUser, userToAssignName, schools);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> verifyUser(String currUser, String password) {
        if (real != null) {
            return real.verifyUser(currUser, password);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> updateInfo(String currUser, String firstName, String lastName, String email, String phoneNumber, String city) {
        if (real != null) {
            return real.updateInfo(currUser, firstName, lastName, email, phoneNumber, city);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<UserDTO> getUserInfo(String currUser) {
        if (real != null) {
            return real.getUserInfo(currUser);
        }

        return new Response<>(null, true, "not implemented");
    }

    public Response<Boolean> changePasswordToUser(String currUser, String userToChangePassword, String newPassword, String confirmPassword) {
        if (real != null) {
            return real.changePasswordToUser(currUser, userToChangePassword, newPassword, confirmPassword);
        }

        return new Response<>(null, true, "not implemented");
    }

    public Response<Boolean> changePassword(String currUser, String currPassword, String newPassword, String confirmPassword) {
        if (real != null) {
            return real.changePassword(currUser, currPassword, newPassword, confirmPassword);
        }

        return new Response<>(null, true, "not implemented");
    }

    public Response<List<UserDTO>> getAllUsers(String currUser) {
        if (real != null) {
            return real.getAllUsers(currUser);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> sendCoordinatorEmails(String currUser, String surveyLink, String surveyToken) {
        if (real != null) {
            return real.sendCoordinatorEmails(currUser, surveyLink, surveyToken);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> transferSupervision(String currUser, String currSupervisor, String newSupervisor, String password, String firstName, String lastName, String email, String phoneNumber, String city) {
        if (real != null) {
            return real.transferSupervision(currUser, currSupervisor, newSupervisor, password, firstName, lastName, email, phoneNumber, city);
        }

        return new Response<>(null, true, "not implemented");
    }

    public Response<List<UserDTO>> getSupervisors(String currUser) {
        if (real != null) {
            return real.getSupervisors(currUser);

        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> transferSupervisionToExistingUser(String currUser, String currSupervisor, String newSupervisor) {
        if (real != null) {
            return real.transferSupervisionToExistingUser(currUser, currSupervisor, newSupervisor);
        }

        return new Response<>(null, true, "not implemented");
    }

    public void setMockDB() { //for tests purposes only
        if (real != null) {
            Connect.setMockDB();
        }
    }
}