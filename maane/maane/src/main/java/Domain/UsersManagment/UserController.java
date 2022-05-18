package Domain.UsersManagment;

import Communication.DTOs.ActivityDTO;
import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Communication.Initializer.ServerContextInitializer;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.SurveyController;
import Domain.EmailManagement.EmailController;
import Domain.WorkPlan.GoalsManagement;
import Domain.WorkPlan.WorkPlan;
import Persistence.DbDtos.UserDBDTO;
import Persistence.SurveyDAO;
import Persistence.UserQueries;
import Persistence.WorkPlanQueries;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserController {
    private Map<String, User> connectedUsers;
    private PasswordEncoder passwordEncoder;
    private GoalsManagement goalsManagement;
    private SurveyController surveyController;
    private EmailController emailController;
    private UserQueries userDAO;
    private WorkPlanQueries workPlanDAO;
    private final SecureRandom secureRandom;
    private final Base64.Encoder base64Encoder;


    private UserController() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.goalsManagement = GoalsManagement.getInstance();
        this.surveyController = SurveyController.getInstance();
        this.emailController = EmailController.getInstance();
        this.userDAO = UserQueries.getInstance();
        this.workPlanDAO = WorkPlanQueries.getInstance();
        secureRandom = new SecureRandom();
        base64Encoder = Base64.getUrlEncoder();

        adminBoot("admin", "admin");//todo need to hide password in db
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

    public void assignWorkPlan(String instructor, WorkPlan workPlan, Integer year) {
        //todo implement properly later validate and prevent errors
        User user = new User(userDAO.getFullUser(instructor).getResult());
        //user.assignWorkPlan(workPlan, instructor);//todo assign properly
        //workPlanDAO.insertUserWorkPlan(instructor, workPlan, year);

    }

    public Response<Boolean> sendCoordinatorEmails(String currUser, String surveyLink) {
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if(user.isSupervisor().getResult()){
                return emailController.sendEmail(user.getWorkField(),  surveyLink);//todo verify existence of the survey link
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
                return passwordEncoder.matches(password, pass.getResult());
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
        if(email.length() != 0 && !isValidEmailAddress(email))
            return new Response<>("", true, "invalid email address");

        if(phoneNumber.length() != 0 && !isValidPhoneNumber(phoneNumber))
            return new Response<>("", true, "invalid phone number");

        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if (!userDAO.userExists(userToRegister)){
                Response<User> result = user.registerUser(userToRegister, userStateEnum, firstName, lastName, email, phoneNumber, city);
                if (!result.isFailure()) {
                    userDAO.insertUser(new UserDBDTO(result.getResult(), passwordEncoder.encode(password)));
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

    /**
     * validate email address at user registration
     * @param email the email address
     * @return true if the email address is valid, false otherwise.
     */
    private boolean isValidEmailAddress(String email) {
        boolean result = true;

        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }

        return result;
    }

    /**
     * validate phone number at user registration
     * @param phoneNumber the phone number
     * @return true if the phone number is valid, false otherwise.
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber israeliNumberProto = null;

        try {
             israeliNumberProto = phoneUtil.parse(phoneNumber, "IL");
        } catch (NumberParseException e) {
            return false;
        }

        if(israeliNumberProto == null)
            return false;

        return phoneUtil.isValidNumber(israeliNumberProto);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches("([A-Za-z]+[0-9]|[0-9]+[A-Za-z])[A-Za-z0-9]*");
    }

    public Response<String> registerUserBySystemManager(String currUser, String userToRegister, String password, UserStateEnum userStateEnum, String optionalSupervisor, String workField, String firstName, String lastName, String email, String phoneNumber, String city){
        if(!ServerContextInitializer.getInstance().isTestMode() && !isValidPassword(password))
            return new Response<>("", true, "The password isn't strong enough");

        if(email.length() != 0 && !isValidEmailAddress(email))
            return new Response<>("", true, "invalid email address");

        if(phoneNumber.length() != 0 && !isValidPhoneNumber(phoneNumber))
            return new Response<>("", true, "invalid phone number");

        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if (!userDAO.userExists(userToRegister)){
                if(userStateEnum == UserStateEnum.SUPERVISOR) {
                    if (onlyOneSupervisorPerWorkField(user, workField)) {
                        Response<User> result = user.registerSupervisor(userToRegister, userStateEnum, workField, firstName, lastName, email, phoneNumber, city);
                        if (!result.isFailure()) {
                            userDAO.insertUser(new UserDBDTO(result.getResult(), passwordEncoder.encode(password)));
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
                                    userDAO.insertUser(new UserDBDTO(result.getResult(), passwordEncoder.encode(password)));
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
        UserDBDTO userDB;

        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if(user.isSystemManager().getResult()){
                Response<List<String>> appointeesRes = user.getAppointees();
                if (!appointeesRes.isFailure()) {
                    List<UserDTO> supervisorsDTOs = new Vector<>();
                    for (String username : appointeesRes.getResult()) {
                        userDB = userDAO.getFullUser(username).getResult();

                        supervisorsDTOs.add(new UserDTO(userDB));
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

        if(email.length() != 0 && !isValidEmailAddress(email))
            return new Response<>(false, true, "invalid email address");

        if(phoneNumber.length() != 0 && !isValidPhoneNumber(phoneNumber))
            return new Response<>(false, true, "invalid phone number");

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

    public Response<List<String>> getUserSchools(String currUser){
        User user = new User(userDAO.getFullUser(currUser).getResult());
        return user.getUserSchools();
    }

    public Response<String> hasSchool(String username, String symbol) {
        return new User(userDAO.getFullUser(username).getResult()).hasSchool(symbol);
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
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.userDAO.deleteUsers();
        this.goalsManagement = GoalsManagement.getInstance();
        SurveyDAO.getInstance().clearCache();
        adminBoot("admin", "admin123");
    }

    public void resetDB(){
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.userDAO.clearDB();
        this.goalsManagement = GoalsManagement.getInstance();
        adminBoot("admin", "admin123");
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
                        userDAO.updateUserPassword(userToChangePassword, passwordEncoder.encode(newPassword));
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
        if(!ServerContextInitializer.getInstance().isTestMode() && !isValidPassword(newPassword))
            return new Response<>(false, true, "The password isn't strong enough");

        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if (passwordEncoder.matches(currPassword, userDAO.getPassword(currUser).getResult()))
            {
                if (newPassword.equals(confirmPassword)) {
                    Response<Boolean> res = user.changePassword();
                    if (res.getResult()) {
                        userDAO.updateUserPassword(currUser, passwordEncoder.encode(newPassword));
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
                return userDAO.addSurvey(currUser, surveyId);
            }
            else {
                return surveyCreation;
            }
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

    /*public Response<Boolean> publishSurvey(String currUser){
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
    }*/

    public Response<List<GoalDTO>> getGoals(String currUser, Integer year){
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

    public Response<Boolean> addGoal(String currUser, GoalDTO goalDTO, Integer year){
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

             boolean verify = passwordEncoder.matches(password, userDAO.getPassword(currUser).getResult());
             return new Response<>(verify, !verify, "");
         }
         else {
             return new Response<>(null, true, "User not connected");
         }
     }

    public void adminBoot(String username, String password) {
        User user = new User(username, UserStateEnum.SYSTEM_MANAGER);
        userDAO.insertUser(new UserDBDTO(user, passwordEncoder.encode(password)));
    }

    public void notifySurveyCreation(String username, String surveyToken) {
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            Response<String> response = user.publishSurvey();
            if(!response.isFailure()){
                emailController.sendEmail(response.getResult(), "http://localhot:8080/survey/getSurvey/surveyID=" + surveyToken);
            }
        }
/*        else {
            return new Response<>(null, true, "User not connected");
        }*/
        // the username is the name of the supervisor created a survey,
        // email all relevant coordinator and send them this link (for now):
        // http://localhot:8080/survey/getSurvey/surveyID={surveyToken}
    }


    public Response<Boolean> removeGoal(String currUser, Integer year, int goalId) {
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

    public Response<WorkPlanDTO> viewWorkPlan(String currUser, Integer year, Integer month){
/*        WorkPlanDTO workPlanDTO = new WorkPlanDTO();
        List<Pair<LocalDateTime, ActivityDTO>> l = new LinkedList<>();

        l.add(new Pair<>(LocalDateTime.of(2022, Month.MAY, 15, 10, 0, 0), new ActivityDTO("2222222", "בטיחות במעבדה 1")));
        l.add(new Pair<>(LocalDateTime.of(2022, Month.MAY, 15, 12, 0, 0), new ActivityDTO("2222222", "בטיחות במעבדה 1")));

        l.add(new Pair<>(LocalDateTime.of(2022, Month.MAY, 18, 10, 0, 0), new ActivityDTO("2222222", "בטיחות במעבדה 2")));

        workPlanDTO.setCalendar(l);

        return new Response<>(workPlanDTO, false, "HI THERE!");*/

        //todo: fix it
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            //return new Response<>(workPlanDAO.getUserWorkPlanByYear(currUser, year).getResult(), false, "");
            //Response<Boolean> workPlanResponse = user.getWorkPlanByYear(year);
            Response<Boolean> workPlanResponse = user.getWorkPlanByYear(year);

            if(!workPlanResponse.isFailure()){
                return workPlanDAO.getUserWorkPlanByYearAndMonth(currUser, year, month);// generateWpDTO(user, year);
                //return new Response<>(workPlanDTO, false, "successfully acquired work plan");
            }
            else{
                return new Response<>(null, true, workPlanResponse.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

/*    private WorkPlanDTO generateWpDTO(User user, String year) {
        WorkPlanDTO workPlanDTO = new WorkPlanDTO();
        WorkPlan workPlan = user.getWorkPlanByYear(year).getResult();
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
    }*/
    
    public Response<UserDTO> getUserInfo(String currUser){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.getInfo();
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    private String createToken(){
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public Response<Boolean> assignCoordinator(String currUser, String workField, String firstName, String lastName, String email, String phoneNumber, String school){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<User> result = user.assignCoordinator(createToken(), workField, school, firstName, lastName, email, phoneNumber);

            if (!result.isFailure()) {
                userDAO.insertUser(new UserDBDTO(result.getResult(), null));
                userDAO.assignSchoolsToUser(result.getResult().getUsername(), result.getResult().getSchools());
                return new Response<>(true, false, "assigned coordinator");
            }
            else{
                return new Response<>(null, result.isFailure(), result.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<UserDBDTO> getCoordinator(String currUser, String workField, String symbol){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<String> workFieldRes = user.getCoordinator();
            if (!workFieldRes.isFailure()) {
                if(workFieldRes.getResult().equals("")){
                    return userDAO.getCoordinator(symbol, workField);
                }
                else{
                    return userDAO.getCoordinator(symbol, workFieldRes.getResult());
                }
            }
            else{
                return new Response<>(null, workFieldRes.isFailure(), workFieldRes.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> removeCoordinator(String currUser, String workField, String school){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<String> response = user.removeCoordinator(school, workField);
            if(!response.isFailure()){
                return userDAO.removeCoordinator(response.getResult(), school);//todo check not failed
            }
            else{
                return new Response<>(false, true, response.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<List<String>> allWorkFields(String currUser){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<List<String>> response = user.getAllWorkFields();
            if(!response.isFailure()){
                return userDAO.getAllWorkFields(response.getResult());
            }
            else{
                return new Response<>(null, true, response.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> transferSupervision(String currUser, String currSupervisor, String newSupervisor, String password, String firstName, String lastName, String email, String phoneNumber, String city){
        if(connectedUsers.containsKey(currUser)) {//todo maybe remove all workPlans
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
                        userDAO.insertUser(new UserDBDTO(result.getResult(),passwordEncoder.encode(password)));

                        userDAO.removeUser(currSupervisor);
                        userDAO.addAppointment(currUser, newSupervisor);
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
        if(connectedUsers.containsKey(currUser)) {//todo maybe remove all workPlans
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
                    newSup.setSchools(new Vector<>());
                    userDAO.resetSchools(newSup.getUsername());
                    userDAO.updateUserState(newSup.getUsername(), newSup.getState().getStateEnum().getState());
                    userDAO.updateSurveys(newSup.getUsername(), newSup.getSurveys().getResult());
                    userDAO.removeUser(currSupervisor);
                    connectedUsers.remove(currSupervisor);
                    if(connectedUsers.containsKey(newSupervisor)){
                        newSup = connectedUsers.get(newSupervisor);
                        newSup.setState(UserStateEnum.SUPERVISOR);
                        newSup.setAppointments(currSup.getAppointments());
                        newSup.removeAppointment(newSupervisor);//remove yourself from your own appointment
                        newSup.setSurveys(currSup.getSurveys().getResult());
                        newSup.setSchools(new Vector<>());
                    }
                    userDAO.addAppointment(currUser, newSupervisor);
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
