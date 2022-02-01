package AcceptanceTesting.Bridge;

import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Domain.UsersManagment.UserStateEnum;
import Service.Interfaces.UserService;
import Service.UserServiceImpl;

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
    public Response<Boolean> removeUser(String currUser, String userToRemove) {
        if (real != null){
            return real.removeUser(currUser, userToRemove);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> generateSchedule(String supervisor, int surveyId) {
        if (real != null){
            return real.generateSchedule(supervisor, surveyId);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<WorkPlanDTO> viewWorkPlan(String currUser) {
        if (real != null){
            return real.viewWorkPlan(currUser);
        }

        return new Response<>(null, true, "not implemented");
    }
}
