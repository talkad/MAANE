package Service.Interfaces;

import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Domain.UsersManagment.UserStateEnum;

public interface UserService {
    Response<String> addGuest();
    Response<String> removeGuest(String name);

    Response<String> login(String currUser, String userToLogin, String password);
    Response<String> logout(String name);

    Response<User> registerUser(UserDTO user);
    Response<Boolean> removeUser(String currUser, String userToRemove);
}
