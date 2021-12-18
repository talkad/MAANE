package Domain.UsersManagment;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserController {
    private AtomicInteger availableId;
    private Map<String, User> connectedUsers;
    private Map<String, Pair<User, String>> registeredUsers;
    private Security security;
    private GoalsManagement goalsManagement;


    private UserController() {
        this.availableId = new AtomicInteger(1);
        this.security = Security.getInstance();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.registeredUsers = new ConcurrentHashMap<>();
        this.goalsManagement = GoalsManagement.getInstance();
        adminBoot("shaked", "cohen");
    }

    public Map<String, User> getConnectedUsers() {
        return this.connectedUsers;
    }

    public Map<String, Pair<User, String>> getRegisteredUsers() {
        return this.registeredUsers;
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
        User user = new User();
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
        return registeredUsers.containsKey(username) && registeredUsers.get(username).getSecond().equals(password);
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

    public Response<User> registerUser(String currUser, String userToRegister, String password, UserStateEnum userStateEnum, String firstName, String lastName, String email, String phoneNumber, String city){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if (!userToRegister.startsWith("Guest") && !registeredUsers.containsKey(userToRegister)){
                Response<User> result = user.registerUser(userToRegister, userStateEnum, firstName, lastName, email, phoneNumber, city);
                if (!result.isFailure()) {
                    registeredUsers.put(userToRegister, new Pair<>(result.getResult(), security.sha256(password)));
                    result = new Response<>(result.getResult(), false, "Registration occurred");
                }
                return result;
            }
            else {
                return new Response<>(null, true, "username already exists"); // null may be a problem
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<User> registerSupervisor(String currUser, String userToRegister, String password, UserStateEnum userStateEnum, String workField, String firstName, String lastName, String email, String phoneNumber, String city){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if (!userToRegister.startsWith("Guest") && !registeredUsers.containsKey(userToRegister)){
                Response<User> result = user.registerSupervisor(userToRegister, userStateEnum, workField, firstName, lastName, email, phoneNumber, city);
                if (!result.isFailure()) {
                    registeredUsers.put(userToRegister, new Pair<>(result.getResult(), security.sha256(password)));
                    result = new Response<>(result.getResult(), false, "Registration occurred");
                    goalsManagement.addGoalsField(workField);
                }
                return result;
            }
            else {
                return new Response<>(null, true, "username already exists"); // null may be a problem
            }
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
                    connectedUsers.remove(userToRemove);//todo check that user was disconnected from the system
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

    public Response<Boolean> assignSchoolsToUser(String currUser, String userToAssignName, List<Integer> schools){
        Response<Boolean> response;
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if(registeredUsers.containsKey(userToAssignName)) {
                response = user.assignSchoolsToUser(userToAssignName, schools);
                if(!response.isFailure()){
                    User userToAssignSchools = registeredUsers.get(userToAssignName).getFirst();
                    userToAssignSchools.addSchools(schools);
                    if(connectedUsers.containsKey(userToAssignName)){
                        userToAssignSchools = connectedUsers.get(userToAssignName);
                        userToAssignSchools.addSchools(schools);
                    }
                }
                return response;
            }
            else{
                return new Response<>(false, true, "User is not in the system");
            }
        }
        else{
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> removeSchoolsFromUser(String currUser, String userToRemoveSchoolsName, List<Integer> schools){
        Response<Boolean> response;
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if(registeredUsers.containsKey(userToRemoveSchoolsName)) {
                response = user.removeSchoolsFromUser(userToRemoveSchoolsName, schools);
                if(!response.isFailure()){
                   User userToRemoveSchools = registeredUsers.get(userToRemoveSchoolsName).getFirst();
                   userToRemoveSchools.removeSchools(schools);
                   if(connectedUsers.containsKey(userToRemoveSchoolsName)){
                       userToRemoveSchools = connectedUsers.get(userToRemoveSchoolsName);
                       for (Integer schoolId: schools) {
                           userToRemoveSchools.schools.remove(schoolId);
                       }
                   }
                }
                return response;
            }
            else{
                return new Response<>(false, true, "User is not in the system");
            }
        }
        else{
            return new Response<>(null, true, "User not connected");
        }
    }

    public User getUser(String user){
        return this.registeredUsers.get(user).getFirst();//todo bad temp function
    }

    public Response<String> fillMonthlyReport(String currUser){
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<String> response = user.fillMonthlyReport(currUser);
            return response;
        }
        else{
            return new Response<>(null, true, "User not connected");//todo bad null res
        }
    }

    public Response<Boolean> changePassword(String currUser, String userToChangePassword,String newPassword, String confirmPassword){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if(newPassword.equals(confirmPassword)) {
                if (registeredUsers.containsKey(userToChangePassword)) {
                    Response<Boolean> res = user.changePassword();
                    if(!res.isFailure()){
                        registeredUsers.get(userToChangePassword).setSecond(security.sha256(newPassword));
                    }
                    return res;
                }
                else {
                    return new Response<>(false, true, "cannot change a password to a user not in the system");
                }
            }
            else {
                return new Response<>(false, true, "new password does not match the confirmed password");
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<String> viewInstructorsDetails(String currUser) {
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            StringBuilder instructorDetails = new StringBuilder(); //todo maybe make it a list??
            Response<List<String>> instructorListRes = user.viewInstructorsDetails();
            if(!instructorListRes.isFailure()){
                for (String instructor: instructorListRes.getResult()) {
                    User ins = registeredUsers.get(instructor).getFirst();
                    instructorDetails.append(ins.getInfo());
                }
                return new Response<>(instructorDetails.toString(), false, "successfully generated instructors details");
            }
            return new Response<>("", true, instructorListRes.getErrMsg());
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Integer> createSurvey(String currUser, int surveyId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.createSurvey(surveyId);
        }
        else {
            return new Response<>(-1, true, "User not connected"); //todo make sure -1 is not a problem
        }
    }

    public Response<Integer> removeSurvey(String currUser, int surveyId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.removeSurvey(surveyId);
        }
        else {
            return new Response<>(-1, true, "User not connected"); //todo make sure -1 is not a problem
        }
    }

    public Response<List<Goal>> getGoals(String currUser){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<String> res = user.getGoals();
            if(!res.isFailure()){
                return goalsManagement.getGoals(res.getResult());
            }
            else{
                return new Response<>(null, true, res.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> addGoals(String currUser, List<Goal> goalList){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<String> res = user.addGoals();
            if(!res.isFailure()){
                return goalsManagement.addGoalsToField(res.getResult(), goalList);
            }
            else{
                return new Response<>(null, true, res.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected"); //todo make sure null is not a problem
        }
    }

    public void adminBoot(String username, String password) {
        User user = new User(username, UserStateEnum.SYSTEM_MANAGER);
        registeredUsers.put(username, new Pair<>(user, security.sha256(password)));
    }
}
