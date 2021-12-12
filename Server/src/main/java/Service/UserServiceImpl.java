package Service;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.*;
import Service.Interfaces.UserService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//TODO:-----Thats shaked code from userController---------- remove original User Controller later

public class UserServiceImpl implements UserService {
    private AtomicInteger availableId;
    private Map<String, User> connectedUsers;
    private Map<String, Pair<User, String>> registeredUsers;
    private Security security;


    private UserServiceImpl() {
        this.availableId = new AtomicInteger(1);
        this.security = Security.getInstance();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.registeredUsers = new ConcurrentHashMap<>();
        adminBoot("shaked", "cohen");
    }

    public Map<String, User> getConnectedUsers() {
        return this.connectedUsers;
    }

    public Map<String, Pair<User, String>> getRegisteredUsers() {
        return this.registeredUsers;
    }

    private static class CreateSafeThreadSingleton {
        private static final UserServiceImpl INSTANCE = new UserServiceImpl();
    }

    public static UserServiceImpl getInstance() {
        return UserServiceImpl.CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<String> removeGuest(String name) {
        connectedUsers.remove(name);
        return new Response<>(name, false, "disconnected user successfully");
    }

    public Response<String> addGuest(){
        String guestName = "Guest" + availableId.getAndIncrement();
        User user = new Guest(); //todo is this ok? originally was User()
        user.setName(guestName);
        connectedUsers.put(guestName, user);
        return new Response<>(guestName, false, "added guest");
    }

    public Response<String> login(String currUser, String userToLogin, String password){
        User user;
        if (connectedUsers.containsKey(currUser)) {
            if (currUser.startsWith("Guest")){
                if (this.isValidUser(userToLogin, security.sha256(password))) {
                    connectedUsers.remove(currUser);
                    user = registeredUsers.get(userToLogin).getFirst();
                    connectedUsers.put(userToLogin, user);
                    return new Response<>(userToLogin, false, "successfully Logged in");
                } else {
                    return new Response<>(currUser, true, "Failed to login user");
                }
            }
            else {
                return new Response<>(null, true, "error: user must disconnect before trying to login");
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public boolean isValidUser(String username, String password){
        return registeredUsers.containsKey(username) && registeredUsers.get(username).getSecond().equals(security.sha256(password));
    }

    public Response<String> logout(String name) {
        Response<String> response;
        if(connectedUsers.containsKey(name)) {
            if (!connectedUsers.get(name).logout().isFailure()) {
                connectedUsers.remove(name);
                response = addGuest();
            } else {
                response = new Response<>(name, true, "User not permitted to logout");
            }
            return response;
        }
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> registerUser(String currUser, String userToRegister, String password, UserStateEnum userStateEnum){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if (!userToRegister.startsWith("Guest")){
                Response<Boolean> result = user.registerUser(userToRegister, password, userStateEnum);
                if (!result.isFailure()) {
                    if (!registeredUsers.containsKey(userToRegister)) {
                        registeredUsers.put(userToRegister, new Pair<>(user.inferUserState(userToRegister, userStateEnum), security.sha256(password)));
                        result = new Response<>(true, false, "Registration occurred");
                    } else {
                        return new Response<>(false, true, "username already exists");
                    }
                }
                return result;
            }
            else return new Response<>(false, true, "error: cannot register user starting with the name Guest");
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> removeUser(String currUser, String userToRemove) {
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<Boolean> response = user.removeUser(userToRemove);
            if(!response.isFailure()){
                if(registeredUsers.containsKey(userToRemove)){
                    registeredUsers.remove(userToRemove);
                    return response;
                }
                else{
                    return new Response<>(null, true, "User is not in the system");
                }
            }
            return response;
        }
        else{
            return new Response<>(null, true, "User not connected");
        }
    }

    public void adminBoot(String username, String password) {
        User user = new SystemManager(username, UserStateEnum.SYSTEM_MANAGER);
        registeredUsers.put(username, new Pair<>(user, security.sha256(password)));

    }
}
