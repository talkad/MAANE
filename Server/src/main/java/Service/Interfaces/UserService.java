package Service.Interfaces;

import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserStateEnum;

public interface UserService {
    Response<String> addGuest();
    Response<String> removeGuest(String name);

    Response<String> login(String currUser, String userToLogin, String password);
    Response<String> logout(String name);

    Response<Boolean> registerUser(String currUser, String userToRegister, String password, UserStateEnum userStateEnum);
    Response<Boolean> removeUser(String currUser, String userToRemove);
}
