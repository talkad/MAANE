package Domain.UsersManagment;

import Communication.DTOs.ActivityDTO;
import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.SurveyController;
import Domain.EmailManagement.EmailController;
import Domain.WorkPlan.GoalsManagement;
import Persistence.UserDBDTO;
import Persistence.UserQueries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class UserController {
    private Map<String, User> connectedUsers;
    private Security security;
    private GoalsManagement goalsManagement;
    private SurveyController surveyController;
    private EmailController emailController;
    private UserQueries userDAO;

    private UserController() {
        this.security = Security.getInstance();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.goalsManagement = GoalsManagement.getInstance();
        this.surveyController = SurveyController.getInstance();
        this.emailController = EmailController.getInstance();
        this.userDAO = UserQueries.getInstance();
        adminBoot("admin", "admin");
    }

    private static class CreateSafeThreadSingleton {
        private static final UserController INSTANCE = new UserController();
    }

    public static UserController getInstance() {
        return CreateSafeThreadSingleton.INSTANCE;
    }

    public Map<String, User> getConnectedUsers() {
        return this.connectedUsers;
    }

    public void assignWorkPlan(String instructor, WorkPlan workPlan, String year) {
        //todo implement properly later validate and prevent errors
        new User(userDAO.getFullUser(instructor).getResult()).assignWorkPlan(workPlan, instructor);
    }

    public Response<Boolean> sendCoordinatorEmails(String currUser, String surveyLink, String surveyToken) {
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if(user.isSupervisor().getResult()){
                return emailController.sendEmail(user.getWorkField(),  surveyLink,  surveyToken);//todo verify existence of the link and survey token
            }
            else{
                return new Response<>(null, true, "user isn't supervisor");
            }
        }
        else {
            return new Response<>(null, true, "user is not logged in");
        }
    }

    public Response<String> getPassword(String currUser) {
        return userDAO.getPassword(currUser);
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

    /**
     * login user into system
     * @param username the original username
     * @return username
     */
    public Response<String> login(String username){
        Response<UserDBDTO> userRes = userDAO.getFullUser(username);
        if(!userRes.isFailure()){
            User user = new User(userRes.getResult());
            connectedUsers.put(username, user);
            System.out.println(username + " logged in successfully");
            return new Response<>(username, false, "successfully Logged in");
        }
        else{
            System.out.println(username + " failed to login successfully");
            return new Response<>(null, true, "failed to login");
        }
    }

    public boolean isValidUser(String username, String password){
        if(userDAO.userExists(username)){
            Response<String> pass = userDAO.getPassword(username);
            if(!pass.isFailure()){
                 return pass.getResult().equals(password);
            }
        }
        return false;
    }

    public Response<String> logout(String name) {
        Response<String> response;
        if(connectedUsers.containsKey(name)) {
            if (!connectedUsers.get(name).logout().isFailure()) {
                connectedUsers.remove(name);
                response = new Response<>(name, false, "successfully  logged out");
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
            if (!userDAO.userExists(userToRegister)){
                Response<User> result = user.registerUser(userToRegister, userStateEnum, firstName, lastName, email, phoneNumber, city);
                if (!result.isFailure()) {
                    userDAO.insertUser(new UserDBDTO(result.getResult(), security.sha256(password)));
                    userDAO.addAppointment(currUser, userToRegister);
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
            if (!userDAO.userExists(userToRegister)){
                if(userStateEnum == UserStateEnum.SUPERVISOR) {
                    if (onlyOneSupervisorPerWorkField(user, workField)) {
                        Response<User> result = user.registerSupervisor(userToRegister, userStateEnum, workField, firstName, lastName, email, phoneNumber, city);
                        if (!result.isFailure()) {
                            userDAO.insertUser(new UserDBDTO(result.getResult(), security.sha256(password)));
                            userDAO.addAppointment(currUser, userToRegister);
                            //goalsManagement.addGoalsField(workField);
                            return new Response<>(result.getResult().getUsername(), false, "Registration occurred");
                        }
                        return new Response<>(null, result.isFailure(), result.getErrMsg());
                    }
                    else{
                        return new Response<>(null, true, "a supervisor was already appointed to the work field");
                    }
                }
                else {
                    if(user.appointments.contains(optionalSupervisor)) {
                        Response<UserDBDTO> supervisorRes = userDAO.getFullUser(optionalSupervisor);
                        if(!supervisorRes.isFailure()){
                            User supervisor = new User(supervisorRes.getResult());
                        if (supervisor.isSupervisor().getResult()) {
                            Response<User> result = user.registerUserBySystemManager(userToRegister, userStateEnum, supervisor.getWorkField(), firstName, lastName, email, phoneNumber, city);
                            if (!result.isFailure()) {
                                Response<Boolean> appointmentRes = supervisor.addAppointment(userToRegister);
                                if (appointmentRes.getResult()) {
                                    userDAO.insertUser(new UserDBDTO(result.getResult(), security.sha256(password)));
                                    userDAO.addAppointment(supervisor.getUsername(), userToRegister);
                                    return new Response<>(result.getResult().getUsername(), false, "Registration occurred");
                                } else {
                                    return new Response<>(null, true, appointmentRes.getErrMsg());
                                }
                            }
                            return new Response<>(null, result.isFailure(), result.getErrMsg());
                        } else {
                            return new Response<>(null, true, "optional supervisor isn't a supervisor");
                        }
                    }
                        else{
                            return new Response<>(null, true, supervisorRes.getErrMsg());
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

    private boolean onlyOneSupervisorPerWorkField(User user, String workField) {
        for (String appointee: user.getAppointments()) {
            Response<UserDBDTO> uRes = userDAO.getFullUser(appointee);
            if(!uRes.isFailure()){
                User u = new User(uRes.getResult());
                if(u.getWorkField().equals(workField)){
                    return false;
                }
            }
            else{
                //todo some error related to bad db reading
            }
        }
        return true;
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
                        userDTO.setWorkField(new User(userDAO.getFullUser(username).getResult()).getWorkField());
                        userDTO.setFirstName(new User(userDAO.getFullUser(username).getResult()).getFirstName());
                        userDTO.setLastName(new User(userDAO.getFullUser(username).getResult()).getLastName());
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
                UserDBDTO userDBDTO = new UserDBDTO();
                userDBDTO.setUsername(currUser);
                userDBDTO.setFirstName(firstName);
                userDBDTO.setLastName(lastName);
                userDBDTO.setEmail(email);
                userDBDTO.setPhoneNumber(phoneNumber);
                userDBDTO.setCity(city);

                return userDAO.updateUserInfo(userDBDTO);
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
        System.out.println(currUser + " trying to remove " + userToRemove);
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<Boolean> response = user.removeUser(userToRemove);
            if(!response.isFailure()){
                if(userDAO.userExists(userToRemove)){
                    if(response.getResult()){
                        findSupervisorAndRemoveAppointment(user, userToRemove);
                    }
                    else{
                        userDAO.removeAppointment(currUser, userToRemove);
                    }
                    response = userDAO.removeUser(userToRemove);
                    if(!response.isFailure()){
                        connectedUsers.remove(userToRemove);
                    }
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

    private void findSupervisorAndRemoveAppointment(User user, String userToRemove) {//todo maybe return bool and check in removeUser
        Response<List<String>> appointeesRes = user.getAppointees();
        String userToRemoveWorkField = userDAO.getFullUser(userToRemove).getResult().getWorkField();
        if(!appointeesRes.isFailure()){
            for (String appointee: appointeesRes.getResult()) {
                if(userDAO.userExists(appointee)){
                    User u = new User(userDAO.getFullUser(appointee).getResult());
                    if(u.getWorkField().equals(userToRemoveWorkField)){
                        u.removeAppointment(userToRemove);
                        userDAO.removeAppointment(u.getUsername(), userToRemove);
                        if(connectedUsers.containsKey(u.getUsername())){
                            connectedUsers.get(u.getUsername()).removeAppointment(userToRemove);
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
        successful response on success. failure otherwise.
     */
    public Response<Boolean> assignSchoolsToUser(String currUser, String userToAssign, List<String> schools){
        Response<Boolean> response;
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if(userDAO.userExists(userToAssign)) {
                response = user.assignSchoolsToUser(userToAssign, schools);
                if(!response.isFailure()){
                    User userToAssignSchools = new User(userDAO.getFullUser(userToAssign).getResult());
                    userToAssignSchools.addSchools(schools);
                    userDAO.assignSchoolsToUser(userToAssign, schools);
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
            if(userDAO.userExists(userToRemoveSchoolsName)) {
                response = user.removeSchoolsFromUser(userToRemoveSchoolsName, schools);
                if(!response.isFailure()){
                   User userToRemoveSchools = new User(userDAO.getFullUser(userToRemoveSchoolsName).getResult());
                   userToRemoveSchools.removeSchools(schools);
                   userDAO.removeSchoolsFromUser(userToRemoveSchoolsName, schools);
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
        User user = new User(userDAO.getFullUser(currUser).getResult());
        return new Response<>(user.getSchools(), false, "");
    }

    public Response<List<String>> getAppointedInstructors(String currUser){
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<List<String>> appointeesRes = user.getAppointees();
            if(!appointeesRes.isFailure()){
                List<String> instructors = new Vector<>();
                for (String appointee: appointeesRes.getResult()) {
                    User u = new User(userDAO.getFullUser(appointee).getResult());
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
        User user = new User(userDAO.getFullUser(username).getResult());
        userDTO.setUsername(username);
        userDTO.setWorkField(user.getWorkField());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setUserStateEnum(user.getState().getStateEnum());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setCity(user.getCity());
        userDTO.setSchools(user.getSchools());
        return userDTO;
    }

    public Response<List<UserDTO>> getAllUsers(String currUser){
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<Boolean> viewAllUsersRes = user.viewAllUsers();
            if (viewAllUsersRes.getResult()) {
                List<UserDTO> users = new Vector<>();
                for (String username : userDAO.getUsers()) {//todo check not null maybe
                    users.add(createUserDTOS(username));
                }
                return new Response<>(users, false, "");
            }
            else {
                return new Response<>(null, true, viewAllUsersRes.getErrMsg());
            }
        }
        else{
            return new Response<>(null, true, "User is not connected");
        }
    }

    //for test purposes only
    public User getUser(String user){
        return new User(userDAO.getFullUser(user).getResult());
    }

    public Response<User> getUserRes(String user){
        if(userDAO.userExists(user)){
            return new Response<>(new User(userDAO.getFullUser(user).getResult()), false, "user found");
        }
        return new Response<>(null, true, "user not found");
    }

    //for test purposes only
    public void clearUsers(){
        this.security = Security.getInstance();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.userDAO.deleteUsers();
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
                if (userDAO.userExists(userToChangePassword)) {
                    Response<Boolean> res = user.changePasswordToUser(userToChangePassword);
                    if(res.getResult()){
                        userDAO.updateUserPassword(userToChangePassword, security.sha256(newPassword));
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
            if (security.sha256(currPassword).equals(userDAO.getPassword(currUser).getResult()))
            {
                if (newPassword.equals(confirmPassword)) {
                    Response<Boolean> res = user.changePassword();
                    if (res.getResult()) {
                        userDAO.updateUserPassword(currUser, security.sha256(newPassword));
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
            Response<String> surveyCreation = user.createSurvey(surveyId);
            if(!surveyCreation.isFailure()){
                userDAO.addSurvey(currUser, surveyId);//todo need to add check it didnt fail on db
            }
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
                goalDTO.setYear(year);//todo maybe make this a little less messy
                goalDTO.setWorkField(user.getWorkField());//todo maybe make this a little less messy
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
             return new Response<>(security.sha256(password).equals(userDAO.getPassword(currUser).getResult()), false, "");
         }
         else {
             return new Response<>(null, true, "User not connected");
         }
     }

    public void adminBoot(String username, String password) { // this is not going to work because of incorrect pwd encryption
        User user = new User(username, UserStateEnum.SYSTEM_MANAGER);
        userDAO.insertUser(new UserDBDTO(user, security.sha256(password)));
        //todo temp static data
/*        login("admin");
        registerUserBySystemManager("admin", "ronit", "ronit", UserStateEnum.SUPERVISOR, "", "science", "ronit", "ronit", "ronit@gmail.com", "", "");
        registerUserBySystemManager("admin", "shoshi", "shoshi", UserStateEnum.INSTRUCTOR, "ronit", "", "shoshi", "shoshi", "shoshi@gmail.com", "", "");
        logout("admin");*/
    }

    public void notifySurveyCreation(String username, String indexer) {
        // todo - talk to tal see if we still need this
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

    public Response<UserDTO> assignCoordinator(String currUser, String workField, String firstName, String lastName, String email, String phoneNumber, String school){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<UserDTO> result = user.assignCoordinator(workField, school, firstName, lastName, email, phoneNumber);
            if (!result.isFailure()) {
                return new Response<>(result.getResult(), false, "assigned coordinator");
            }
            return new Response<>(null, result.isFailure(), result.getErrMsg());
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<String> removeCoordinator(String currUser, String workField, String school){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);//todo make sure when displaying coordinators only display the ones from the same workField
            return user.removeCoordinator(school, workField);
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> transferSupervision(String currUser, String currSupervisor, String newSupervisor, String password, String firstName, String lastName, String email, String phoneNumber, String city){
        if(connectedUsers.containsKey(currUser)) {//todo maybe remove all workPlans and surveys
            User user = connectedUsers.get(currUser);
            Response<Boolean> transferSupervisionRes = user.transferSupervision(currSupervisor, newSupervisor);
            if(!transferSupervisionRes.isFailure()){
                if(!userDAO.userExists(newSupervisor)){
                    User supervisor = new User(userDAO.getFullUser(currSupervisor).getResult());
                    Response<User> result = user.registerSupervisor(newSupervisor, UserStateEnum.SUPERVISOR, supervisor.getWorkField(), firstName, lastName, email, phoneNumber, city);
                    if(!result.isFailure()){
                        result.getResult().setAppointments(supervisor.getAppointments());
                        for (String appointee: supervisor.getAppointments()) {
                            userDAO.addAppointment(result.getResult().getUsername(), appointee);
                        }
                        result.getResult().setSurveys(supervisor.getSurveys().getResult());
                        userDAO.insertUser(new UserDBDTO(result.getResult(), security.sha256(password)));
                        userDAO.removeUser(currSupervisor);
                        connectedUsers.remove(currSupervisor);
                        return transferSupervisionRes;
                    }
                    else{
                        return new Response<>(false, true, result.getErrMsg());
                    }
                }
                else{
                    return new Response<>(false, true, "chosen supervisor doesn't exist");
                }
            }
            else{
                return transferSupervisionRes;
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> transferSupervisionToExistingUser(String currUser, String currSupervisor, String newSupervisor){
        if(connectedUsers.containsKey(currUser)) {//todo maybe remove all workPlans and surveys
            User user = connectedUsers.get(currUser);
            Response<Boolean> transferSupervisionRes = user.transferSupervision(currSupervisor, newSupervisor);
            if(!transferSupervisionRes.isFailure()){
                if(userDAO.userExists(newSupervisor) && userDAO.userExists(currSupervisor)){
                    User currSup = new User(userDAO.getFullUser(currSupervisor).getResult());
                    User newSup = new User(userDAO.getFullUser(newSupervisor).getResult());
                    newSup.setState(UserStateEnum.SUPERVISOR);
                    newSup.setAppointments(currSup.getAppointments());
                    for (String appointee: currSup.getAppointments()) {
                        userDAO.addAppointment(newSup.getUsername(), appointee);
                    }
                    newSup.removeAppointment(newSupervisor);//remove yourself from your own appointment
                    userDAO.removeAppointment(newSup.getUsername(), newSup.getUsername());
                    newSup.setSurveys(currSup.getSurveys().getResult());
                    newSup.setSchools(new Vector<>());//todo move surveys in db as well
                    userDAO.resetSchools(newSup.getUsername());
                    userDAO.updateUserState(newSup.getUsername(), newSup.getState().getStateEnum().getState());
                    userDAO.removeUser(currSupervisor);
                    connectedUsers.remove(currSupervisor);//todo maybe dont delete old sup just change his role
                    if(connectedUsers.containsKey(newSupervisor)){
                        newSup = connectedUsers.get(newSupervisor);
                        newSup.setState(UserStateEnum.SUPERVISOR);
                        newSup.setAppointments(currSup.getAppointments());
                        newSup.removeAppointment(newSupervisor);//remove yourself from your own appointment
                        newSup.setSurveys(currSup.getSurveys().getResult());
                        newSup.setSchools(new Vector<>());
                    }
                    return transferSupervisionRes;
                }
                else{
                    return new Response<>(false, true, "chosen supervisor doesn't exist");
                }
            }
            else{
                return transferSupervisionRes;
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

}
