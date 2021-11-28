package UsersManagment;

import CommonClasses.Pair;
import CommonClasses.Response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserController {
    private AtomicInteger availableId;
    private Map<String, User> connectedUsers;
    private Map<String, Pair<User, String>> registeredUsers;
    private Security security;


    private UserController() {
        this.availableId = new AtomicInteger(1);
        this.security = Security.getInstance();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.registeredUsers = new ConcurrentHashMap<>();
    }

    private static class CreateSafeThreadSingleton {
        private static final UserController INSTANCE = new UserController();
    }

    public static UserController getInstance() {
        return UserController.CreateSafeThreadSingleton.INSTANCE;
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
}
