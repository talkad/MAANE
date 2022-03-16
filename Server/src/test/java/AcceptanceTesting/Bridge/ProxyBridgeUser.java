package AcceptanceTesting.Bridge;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.Goal;
import Service.Interfaces.UserService;

import java.util.List;

public class ProxyBridgeUser implements UserService {
    private UserService real;

    public ProxyBridgeUser(){
        real = null;
    }

    public void setRealBridge(UserService implementation) {
        if(real == null){
            real = implementation;
        }
    }

    @Override
    public Response<String> addGuest() {
        if (real != null){
            return real.addGuest();
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<String> removeGuest(String name) {
        if (real != null){
            return real.removeGuest(name);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Pair<String, UserStateEnum>> login(String currUser, String userToLogin, String password) {
        if (real != null){
            return real.login(currUser, userToLogin, password);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<String> logout(String name) {
        if (real != null){
            return real.logout(name);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<User> registerUser(UserDTO user) {
        if (real != null){
            return real.registerUser(user);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<User> registerUserBySystemManager(UserDTO user, String optionalSupervisor) {
        if (real != null){
            return real.registerUserBySystemManager(user, optionalSupervisor);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> removeUser(String currUser, String userToRemove) {
        if (real != null){
            return real.removeUser(currUser, userToRemove);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<WorkPlanDTO> viewWorkPlan(String currUser, String year) {
        if (real != null){
            return real.viewWorkPlan(currUser, year);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<List<UserDTO>> getAppointedUsers(String currUser) {
        if (real != null){
            return real.getAppointedUsers(currUser);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> addGoals(String currUser, List<GoalDTO> goalDTOList, String year) {
        if (real != null){
            return real.addGoals(currUser, goalDTOList, year);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> removeGoal(String currUser, String year, int goalId) {
        if (real != null){
            return real.removeGoal(currUser, year, goalId);
        }

        return new Response<>(null, true, "not implemented");
    }

    public Response<User> getUserRes(String username) {
        if (real != null){
            return real.getUserRes(username);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> assignSchoolsToUser(String currUser, String userToAssignName, List<String> schools) {
        if (real != null){
            return real.assignSchoolsToUser(currUser, userToAssignName, schools);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> verifyUser(String currUser, String password) {
        if (real != null){
            return real.verifyUser(currUser, password);
        }

        return new Response<>(null, true, "not implemented");
    }
}
