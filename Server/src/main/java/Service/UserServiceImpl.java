package Service;

import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Service.Interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Response<String> login(String currUser, String userToLogin, String password) {
        Response<String> res = null;//todo bring back for merge - UserController.getInstance().login(currUser, userToLogin, password);

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
    public Response<Boolean> removeUser(String currUser, String userToRemove) {
        Response<Boolean> res = UserController.getInstance().removeUser(currUser, userToRemove);

        if(res.isFailure())
            log.error("failed to remove user {}", userToRemove);
        else
            log.info("removed user {}", userToRemove);

        return res;
    }

}
