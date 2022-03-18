package Domain.UsersManagment;

import Communication.DTOs.*;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.SurveyController;
import Domain.WorkPlan.GoalsManagement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserController {
    private AtomicInteger availableId;
    private Map<String, User> connectedUsers;
    private Map<String, Pair<User, String>> registeredUsers;
    private Security security;
    private GoalsManagement goalsManagement;
    private SurveyController surveyController;

    private UserController() {
        this.availableId = new AtomicInteger(1);
        this.security = Security.getInstance();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.registeredUsers = new ConcurrentHashMap<>();
        this.goalsManagement = GoalsManagement.getInstance();
        this.surveyController = SurveyController.getInstance();
        adminBoot("admin", "admin");
    }

    public Map<String, User> getConnectedUsers() {
        return this.connectedUsers;
    }

    public Map<String, Pair<User, String>> getRegisteredUsers() {
        return this.registeredUsers;
    }

    public void assignWorkPlan(String instructor, WorkPlan workPlan, String year) {
        registeredUsers.get(instructor).getFirst().assignWorkPlan(workPlan, year);//todo validate and prevent errors
    }

    private static class CreateSafeThreadSingleton {
        private static final UserController INSTANCE = new UserController();
    }

    public static UserController getInstance() {
        return UserController.CreateSafeThreadSingleton.INSTANCE;
    }

    /**
     * generate schedule for all instructors under current user
     * @param currUser the supervisor wish to generate schedules
     * @return response contains the work field of current user
     */
    public Response<String> generateSchedule(String currUser) {
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.generateSchedule();
        }
        else {
            return new Response<>("", true, "user is not logged in");
        }
    }

    public Response<String> removeGuest(String name) {
        connectedUsers.remove(name);
        return new Response<>(name, false, "disconnected user successfully");
    }

    public Response<String> addGuest(){
        String guestName = "Guest" + availableId.getAndIncrement();
        User user = new User();
        user.setUsername(guestName);
        connectedUsers.put(guestName, user);
        return new Response<>(guestName, false, "added guest");
    }

    /**
     * login user into system
     * @param currUser the holder username previous login
     * @param userToLogin the original username
     * @param password the identifier of the user
     * @return pair contains the username and its role
     */
    public Response<Pair<String, UserStateEnum>> login(String currUser, String userToLogin, String password){
        User user;
        if (connectedUsers.containsKey(currUser)) {
            if (currUser.startsWith("Guest")){
                if (this.isValidUser(userToLogin, security.sha256(password))) {
                    connectedUsers.remove(currUser);
                    user = registeredUsers.get(userToLogin).getFirst();
                    connectedUsers.put(userToLogin, user);
                    return new Response<>(new Pair<>(userToLogin, user.getState().getStateEnum()), false, "successfully Logged in");
                }
                else {
                    return new Response<>(null, true, "Failed to login user");
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
        return registeredUsers.containsKey(username) && registeredUsers.get(username).getSecond().equals(password) && !connectedUsers.containsKey(username);
    }

    public Response<String> logout(String name) {
        Response<String> response;
        if(connectedUsers.containsKey(name)) {
            if (!connectedUsers.get(name).logout().isFailure()) {
                connectedUsers.remove(name);
                response = addGuest();
            }
            else {
                response = new Response<>(name, true, "User not permitted to logout");
            }
            return response;
        }
        return new Response<>(null, true, "User not connected");
    }

    /**
     * allow user to register another user (if user is supervisor or admin)
     * @param currUser the user that trying to apply the registration
     * @param userToRegister the user to be registered
     * @param password its future password
     * @param userStateEnum the role of the registered user
     * @param firstName first name
     * @param lastName last name
     * @param email email address
     * @param phoneNumber phone number
     * @param city city
     * @return User object of the new user upon success
     */
    public Response<String> registerUser(String currUser, String userToRegister, String password, UserStateEnum userStateEnum, String firstName, String lastName, String email, String phoneNumber, String city){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if (!userToRegister.startsWith("Guest") && !registeredUsers.containsKey(userToRegister)){
                Response<User> result = user.registerUser(userToRegister, userStateEnum, firstName, lastName, email, phoneNumber, city);
                if (!result.isFailure()) {
                    registeredUsers.put(userToRegister, new Pair<>(result.getResult(), security.sha256(password)));
                    return new Response<>(result.getResult().getUsername(), false, "Registration occurred");
                }
                return new Response<>(null, result.isFailure(), result.getErrMsg());
            }
            else {
                return new Response<>(null, true, "username already exists"); // null may be a problem
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<String> registerUserBySystemManager(String currUser, String userToRegister, String password, UserStateEnum userStateEnum, String optionalSupervisor, String workField, String firstName, String lastName, String email, String phoneNumber, String city){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if (!userToRegister.startsWith("Guest") && !registeredUsers.containsKey(userToRegister)){
                if(userStateEnum == UserStateEnum.SUPERVISOR){
                    Response<User> result = user.registerSupervisor(userToRegister, userStateEnum, workField, firstName, lastName, email, phoneNumber, city);
                    if (!result.isFailure()) {
                        registeredUsers.put(userToRegister, new Pair<>(result.getResult(), security.sha256(password)));
                        goalsManagement.addGoalsField(workField);
                        return new Response<>(result.getResult().getUsername(), false, "Registration occurred");
                    }
                    return new Response<>(null, result.isFailure(), result.getErrMsg());
                }
                else {
                    if(user.appointments.contains(optionalSupervisor)){
                        User supervisor = registeredUsers.get(optionalSupervisor).getFirst();
                        if(supervisor.isSupervisor().getResult())
                        {
                            Response<User> result = user.registerUserBySystemManager(userToRegister, userStateEnum, supervisor.getWorkField(), firstName, lastName, email, phoneNumber, city);
                            if (!result.isFailure()) {
                                Response<Boolean> appointmentRes = supervisor.addAppointment(userToRegister);
                                if(appointmentRes.getResult()){
                                    registeredUsers.put(userToRegister, new Pair<>(result.getResult(), security.sha256(password)));
                                    return new Response<>(result.getResult().getUsername(), false, "Registration occurred");
                                }
                                else{
                                    return new Response<>(null, true, appointmentRes.getErrMsg());
                                }
                            }
                            return new Response<>(null, result.isFailure(), result.getErrMsg());
                        }
                        else{
                            return new Response<>(null, true, "optional supervisor isn't a supervisor");
                        }
                    }
                    else{
                        return new Response<>(null, true, "optional supervisor doesn't exist");
                    }
                }
            }
            else {
                return new Response<>(null, true, "username already exists"); // null may be a problem
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<List<UserDTO>> getSupervisors(String currUser){
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if(user.isSystemManager().getResult()){
                Response<List<String>> appointeesRes = user.getAppointees();
                if (!appointeesRes.isFailure()) {
                    List<UserDTO> supervisorsDTOs = new Vector<>();
                    for (String username : appointeesRes.getResult()) {
                        UserDTO userDTO = new UserDTO();
                        userDTO.setCurrUser(registeredUsers.get(username).getFirst().getUsername());
                        userDTO.setWorkField(registeredUsers.get(username).getFirst().getWorkField());
                        userDTO.setFirstName(registeredUsers.get(username).getFirst().getFirstName());
                        userDTO.setLastName(registeredUsers.get(username).getFirst().getLastName());
                        supervisorsDTOs.add(userDTO);
                    }
                    return new Response<>(supervisorsDTOs, false, "");
                }
                else {
                    return new Response<>(null, appointeesRes.isFailure(), appointeesRes.getErrMsg());
                }
            }
            else{
                return new Response<>(null, true, "user is not allowed to view supervisors");
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> updateInfo(String currUser, String firstName, String lastName, String email, String phoneNumber, String city){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<User> response = user.updateInfo(firstName, lastName, email, phoneNumber, city);
            if(!response.isFailure()){
                //todo check if this is required when adding DAL - registeredUsers.get(currUser).setFirst(user);
                return new Response<>(true, false, response.getErrMsg());
            }
            else {
                return new Response<>(false, true, response.getErrMsg());
            }
        }
        else{
            return new Response<>(null, true, "User not connected");
        }
    }

    /**
     * remove user from the system
     * @param currUser the user that trying to remove another user
     * @param userToRemove the user to be removed
     * @return successful response upon success. failure otherwise
     */
    public Response<Boolean> removeUser(String currUser, String userToRemove) {
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<Boolean> response = user.removeUser(userToRemove);
            if(!response.isFailure()){
                if(registeredUsers.containsKey(userToRemove)){
                    registeredUsers.remove(userToRemove);
                    connectedUsers.remove(userToRemove);
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

    /**
        successful response on success. failure otherwise.
     */
    public Response<Boolean> assignSchoolsToUser(String currUser, String userToAssign, List<String> schools){
        Response<Boolean> response;
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if(registeredUsers.containsKey(userToAssign)) {
                response = user.assignSchoolsToUser(userToAssign, schools);
                if(!response.isFailure()){
                    User userToAssignSchools = registeredUsers.get(userToAssign).getFirst();
                    userToAssignSchools.addSchools(schools);
                    if(connectedUsers.containsKey(userToAssign)){
                        userToAssignSchools = connectedUsers.get(userToAssign);
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

    public Response<Boolean> removeSchoolsFromUser(String currUser, String userToRemoveSchoolsName, List<String> schools){
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
                       for (String schoolId: schools) {
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

    public Response<List<String>> getSchools(String currUser){//todo maybe add checks
        User user = registeredUsers.get(currUser).getFirst();
        return new Response<>(user.getSchools(), false, "");
    }

    public Response<List<String>> getAppointedInstructors(String currUser){
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<List<String>> appointeesRes = user.getAppointees();
            if(!appointeesRes.isFailure()){
                List<String> instructors = new Vector<>();
                for (String appointee: appointeesRes.getResult()) {
                    User u = registeredUsers.get(appointee).getFirst();
                    if(u.isInstructor()){
                        instructors.add(u.username);
                    }
                }
                return new Response<>(instructors, false, "");
            }
            else {
                return appointeesRes;
            }
        }
        else{
            return new Response<>(null, true, "User is not connected");
        }
    }

    public Response<List<UserDTO>> getAppointedUsers(String currUser){
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<List<String>> appointeesRes = user.getAppointees();
            if (!appointeesRes.isFailure()) {
                List<UserDTO> appointeesDTOs = new Vector<>();
                for (String appointee : appointeesRes.getResult()) {
                    appointeesDTOs.add(createUserDTOS(appointee));
                }
                return new Response<>(appointeesDTOs, false, "");
            }
            else {
                return new Response<>(null, appointeesRes.isFailure(), appointeesRes.getErrMsg());
            }
        }
        else{
            return new Response<>(null, true, "User is not connected");
        }
    }

    private UserDTO createUserDTOS(String username){//todo either move this to user or move the one in User here
        UserDTO userDTO = new UserDTO();
        userDTO.setCurrUser(username);
        userDTO.setWorkField(registeredUsers.get(username).getFirst().getWorkField());
        userDTO.setFirstName(registeredUsers.get(username).getFirst().getFirstName());
        userDTO.setLastName(registeredUsers.get(username).getFirst().getLastName());
        userDTO.setEmail(registeredUsers.get(username).getFirst().getEmail());
        userDTO.setUserStateEnum(registeredUsers.get(username).getFirst().getState().getStateEnum());
        userDTO.setPhoneNumber(registeredUsers.get(username).getFirst().getPhoneNumber());
        userDTO.setCity(registeredUsers.get(username).getFirst().getCity());
        userDTO.setSchools(registeredUsers.get(username).getFirst().getSchools());
        return userDTO;
    }

    public Response<List<UserDTO>> getAllUsers(String currUser){
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<Boolean> viewAllUsersRes = user.viewAllUsers();
            if (viewAllUsersRes.getResult()) {
                List<UserDTO> users = new Vector<>();
                for (String username : registeredUsers.keySet()) {
                    users.add(createUserDTOS(username));
                }
                return new Response<>(users, false, "");
            }
            else {
                return new Response<>(null, true, viewAllUsersRes.getErrMsg());//todo should be isFailure false?
            }
        }
        else{
            return new Response<>(null, true, "User is not connected");
        }
    }

    //for test purposes only
    public User getUser(String user){
        return this.registeredUsers.get(user).getFirst();
    }

    public Response<User> getUserRes(String user){
        if(this.registeredUsers.containsKey(user)){
            return new Response<>(this.registeredUsers.get(user).getFirst(), false, "user found");
        }
        return new Response<>(null, true, "user not found");
    }

    //for test purposes only
    public void clearUsers(){
        this.availableId.set(1);
        this.security = Security.getInstance();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.registeredUsers = new ConcurrentHashMap<>();
        this.goalsManagement = GoalsManagement.getInstance();
        surveyController.clearCache();
        adminBoot("admin", "admin");
    }

    public Response<String> fillMonthlyReport(String currUser){
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<String> response = user.fillMonthlyReport(currUser);
            return response;
        }
        else{
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> changePasswordToUser(String currUser, String userToChangePassword, String newPassword, String confirmPassword){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if(newPassword.equals(confirmPassword)) {
                if (registeredUsers.containsKey(userToChangePassword)) {
                    Response<Boolean> res = user.changePasswordToUser(userToChangePassword);
                    if(res.getResult()){
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

    public Response<Boolean> changePassword(String currUser, String currPassword, String newPassword, String confirmPassword){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if (security.sha256(currPassword).equals(registeredUsers.get(currUser).getSecond()))
            {
                if (newPassword.equals(confirmPassword)) {
                    Response<Boolean> res = user.changePassword();
                    if (res.getResult()) {
                        registeredUsers.get(currUser).setSecond(security.sha256(newPassword));
                    }
                    return res;
                } else {
                    return new Response<>(false, true, "new password does not match the confirmed password");
                }
            }
            else {
                return new Response<>(false, true, "current password is incorrect");
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<String> createSurvey(String currUser, String surveyId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.createSurvey(surveyId);
        }
        else {
            return new Response<>("", true, "User not connected");
        }
    }

    public Response<String> createBasket(String currUser, String basketId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.createBasket(basketId);
        }
        else {
            return new Response<>("", true, "User not connected");
        }
    }

    public Response<Boolean> hasCreatedSurvey(String currUser, String surveyId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.hasCreatedSurvey(surveyId);
        }
        else {
            return new Response<>(false, true, "User not connected");
        }
    }

    public Response<Boolean> hasCreatedBasket(String currUser, String basketId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.hasCreatedBasket(basketId);
        }
        else {
            return new Response<>(false, true, "User not connected");
        }
    }

    public Response<String> removeSurvey(String currUser, String surveyId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.removeSurvey(surveyId);
        }
        else {
            return new Response<>("", true, "User not connected");
        }
    }

    public Response<String> removeBasket(String currUser, String basketId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.removeBasket(basketId);
        }
        else {
            return new Response<>("", true, "User not connected");
        }
    }

    public Response<Boolean> publishSurvey(String currUser){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<String> res = user.publishSurvey();
            if(!res.isFailure()){
                //todo publisher.notify(res.getResult())
                return new Response<>(true, false, res.getErrMsg());
            }
            else{
                return new Response<>(false, false, res.getErrMsg());
            }
        }
        else {
            return new Response<>(false, true, "User not connected");
        }
    }

    public Response<List<GoalDTO>> getGoals(String currUser, String year){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<String> res = user.getGoals();
            if(!res.isFailure()){
                return goalsManagement.getGoalsDTO(res.getResult(), year);
            }
            else{
                return new Response<>(null, true, res.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> addGoal(String currUser, GoalDTO goalDTO, String year){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<String> res = user.addGoals();
            if(!res.isFailure()){
                return goalsManagement.addGoalToField(res.getResult(), goalDTO, year);
            }
            else{
                return new Response<>(null, true, res.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> isSupervisor(String currUser){
         if(connectedUsers.containsKey(currUser)) {
             User user = connectedUsers.get(currUser);
             return user.isSupervisor();
         }
         else {
             return new Response<>(null, true, "User not connected");
         }
     }

    public Response<Boolean> verifyUser(String currUser, String password){
         if(connectedUsers.containsKey(currUser)) {
             return new Response<>(security.sha256(password).equals(registeredUsers.get(currUser).getSecond()), false, "");
         }
         else {
             return new Response<>(null, true, "User not connected");
         }
     }

    public void adminBoot(String username, String password) {
        User user = new User(username, UserStateEnum.SYSTEM_MANAGER);
        registeredUsers.put(username, new Pair<>(user, security.sha256(password)));
        //todo temp static data
        String guest_name_temp = addGuest().getResult();
        login(guest_name_temp,"admin", "admin");
        registerUserBySystemManager("admin", "ronit", "ronit", UserStateEnum.SUPERVISOR, "", "science", "ronit", "ronit", "ronit@gmail.com", "", "");
        registerUserBySystemManager("admin", "shoshi", "shoshi", UserStateEnum.INSTRUCTOR, "ronit", "", "shoshi", "shoshi", "shoshi@gmail.com", "", "");
        logout("admin");
    }

    public void notifySurveyCreation(String username, String indexer) {
        // todo - publisher and subscribers
    }


    public Response<Boolean> removeGoal(String currUser, String year, int goalId) {
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<String> res = user.removeGoal();
            if (!res.isFailure()) {
                return goalsManagement.removeGoal(user.workField, year, goalId);
            } else {
                return new Response<>(null, true, res.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<List<String>> getSurveys(String currUser){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.getSurveys();
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<WorkPlanDTO> viewWorkPlan(String currUser, String year){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<WorkPlan> workPlanResponse = user.getWorkPlan(year);
            if(!workPlanResponse.isFailure()){
                WorkPlanDTO workPlanDTO = generateWpDTO(user, year);
                return new Response<>(workPlanDTO, false, "successfully acquired work plan");
            }
            else{
                return new Response<>(null, true, workPlanResponse.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    private WorkPlanDTO generateWpDTO(User user, String year) {
        WorkPlanDTO workPlanDTO = new WorkPlanDTO();
        WorkPlan workPlan = user.getWorkPlan(year).getResult();
        for (String date: workPlan.getCalendar().descendingKeySet()) {
            List<Activity> fromHere = workPlan.getCalendar().get(date);
            List<ActivityDTO> toHere = new ArrayList<>();
            for (Activity activity : fromHere){
                ActivityDTO activityDTO = new ActivityDTO();
                activityDTO.setTitle(activity.getTitle());
                activityDTO.setSchoolId(activity.getSchool());
                toHere.add(activityDTO);
            }
            workPlanDTO.getCalendar().add(new Pair<>(date, toHere));
        }
        return workPlanDTO;
    }
    
    public Response<UserDTO> getUserInfo(String currUser){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.getInfo();
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }
}
