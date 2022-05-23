package Domain.UsersManagment;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Communication.Initializer.ServerContextInitializer;
import Domain.CommonClasses.Response;
import Domain.DataManagement.DataController;
import Domain.EmailManagement.EmailController;
import Domain.UsersManagment.APIs.DTOs.UserActivityInfoDTO;
import Domain.UsersManagment.APIs.DTOs.UserInfoDTO;
import Domain.WorkPlan.GoalsManagement;
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
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserController {
    private Map<String, User> connectedUsers;
    private PasswordEncoder passwordEncoder;
    private GoalsManagement goalsManagement;
    private EmailController emailController;
    private UserQueries userDAO;
    private WorkPlanQueries workPlanDAO;
    private final SecureRandom secureRandom;
    private final Base64.Encoder base64Encoder;


    private UserController() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.goalsManagement = GoalsManagement.getInstance();
        this.emailController = EmailController.getInstance();
        this.userDAO = UserQueries.getInstance();
        this.workPlanDAO = WorkPlanQueries.getInstance();
        secureRandom = new SecureRandom();
        base64Encoder = Base64.getUrlEncoder();
        if(!userDAO.userExists("admin")){
            System.out.println("here");
            adminBoot("admin", "admin123");//todo need to hide password in db also note the if query helps the tests
        }
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

        if(!ServerContextInitializer.getInstance().isTestMode() && !isValidPassword(password))
            return new Response<>("", true, "The password isn't strong enough");

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
        Phonenumber.PhoneNumber israeliNumberProto;

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
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<Boolean> response = user.removeUser(userToRemove);//todo verify connected user is dao updated
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
                response = user.assignSchoolsToUser(userToAssign);
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
                response = user.removeSchoolsFromUser(userToRemoveSchoolsName);
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

    public Response<List<String>> getSchools(String currUser){
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

    private UserDTO createUserDTOS(String username){
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

    public Response<Boolean> changePasswordToUser(String currUser, String userToChangePassword, String newPassword, String confirmPassword){
        if(!ServerContextInitializer.getInstance().isTestMode() && !isValidPassword(newPassword))
            return new Response<>(false, true, "The password isn't strong enough");

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

    public Response<UserDTO> getUserInfo(String currUser){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.getInfo();
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
        if(connectedUsers.containsKey(currUser)) {
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
                        userDAO.removeWorkPlan(newSupervisor);
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
        if(connectedUsers.containsKey(currUser)) {
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
                    userDAO.removeWorkPlan(newSupervisor);
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

    //Basket Start
    public Response<Boolean> hasCreatedBasket(String currUser, String basketId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.hasCreatedBasket(basketId);
        }
        else {
            return new Response<>(false, true, "User not connected");
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

    public Response<String> removeBasket(String currUser, String basketId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.removeBasket(basketId);
        }
        else {
            return new Response<>("", true, "User not connected");
        }
    }
    //Basket end

    //Survey start
    public Response<Boolean> hasCreatedSurvey(String currUser, String surveyId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.hasCreatedSurvey(surveyId);
        }
        else {
            return new Response<>(false, true, "User not connected");
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

    public Response<String> removeSurvey(String currUser, String surveyId) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.removeSurvey(surveyId);
        }
        else {
            return new Response<>("", true, "User not connected");
        }
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

    public Response<List<String>> getSurveys(String currUser){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            return user.getSurveys();
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }
    //Survey end

    //Goals start
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
    //Goals end

    //Coordinator start
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
                Response<UserDBDTO> isCoordinatorAssignedRes = userDAO.getCoordinator(school, result.getResult().getWorkField());
                if(!isCoordinatorAssignedRes.isFailure() && isCoordinatorAssignedRes.getResult() == null){
                    userDAO.insertUser(new UserDBDTO(result.getResult(), null));
                    userDAO.assignSchoolsToUser(result.getResult().getUsername(), result.getResult().getSchools());
                    return new Response<>(true, false, "assigned coordinator");
                }
                else{
                    return new Response<>(false, true, "a coordinator is already assigned to the school");
                }
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
                return userDAO.removeCoordinator(response.getResult(), school);
            }
            else{
                return new Response<>(false, true, response.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> sendCoordinatorEmails(String currUser, String surveyLink) {
        if (connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            if(user.isSupervisor().getResult()){
                return emailController.sendEmail(user.getWorkField(), surveyLink);//todo verify existence of the survey link
            }
            else{
                return new Response<>(null, true, "user isn't supervisor");
            }
        }
        else {
            return new Response<>(null, true, "user is not logged in");
        }
    }
    //Coordinator end

    //WorkPlan start
    public void assignWorkPlanYear(String instructor, Integer year) {
        if(connectedUsers.containsKey(instructor)){
            User user = connectedUsers.get(instructor);
            user.assignWorkPlanYear(year);
        }
    }

    public Response<WorkPlanDTO> viewWorkPlan(String currUser, Integer year, Integer month){
        if(connectedUsers.containsKey(currUser)) {
            User user = new User(userDAO.getFullUser(currUser).getResult());
            Response<Boolean> workPlanResponse = user.getWorkPlanByYear(year);//todo causes problem
            if(!workPlanResponse.isFailure()){
                return workPlanDAO.getUserWorkPlanByYearAndMonth(currUser, year, month);
            }
            else{
                return new Response<>(null, true, workPlanResponse.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<WorkPlanDTO> viewInstructorWorkPlan(String currUser, String instructor, Integer year, Integer month) {//todo test it but should be fine
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);
            Response<Boolean> workPlanResponse = user.getInstructorWorkPlan(instructor);
            if(!workPlanResponse.isFailure()){
                return workPlanDAO.getUserWorkPlanByYearAndMonth(instructor, year, month);
            }
            else{
                return new Response<>(null, true, workPlanResponse.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> editActivity(String currUser, LocalDateTime currActStart, Integer year, LocalDateTime newActStart, LocalDateTime newActEnd){ //todo test it
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);//todo maybe verify the dao was generated
            Response<Boolean> editActivityRes = user.editActivity(year);//todo change active user info
            if(!editActivityRes.isFailure()){
                return workPlanDAO.updateActivity(currUser, currActStart, year, newActStart, newActEnd);
            }
            else{
                return new Response<>(null, true, editActivityRes.getErrMsg() + " / colliding activity hours");
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
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
    //WorkPlan end

    //Monthly Report start
    public Response<Boolean> canGenerateReport(String currUser) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);//todo maybe verify the dao was generated
            return user.canGenerateReport();
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<UserInfoDTO> getUserReportInfo(String currUser) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);//todo maybe verify the dao was generated
            Response<Boolean> canGenerateReportRes = user.canGenerateReport();
            if(!canGenerateReportRes.isFailure()){
                return userDAO.getUserReportInfo(currUser);
            }
            else{
                return new Response<>(null, true, canGenerateReportRes.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<List<UserActivityInfoDTO>> getUserActivities(String currUser, int year, int month) {
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);//todo maybe verify the dao was generated
            Response<Boolean> canGenerateReportRes = user.canGenerateReport();
            if(!canGenerateReportRes.isFailure()){
                return userDAO.getUserActivities(currUser, year, month);
            }
            else{
                return new Response<>(null, true, canGenerateReportRes.getErrMsg());
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }
    //Monthly Report end

    public Response<Boolean> setWorkingTime(String currUser, int workDay, LocalTime act1Start, LocalTime act1End, LocalTime act2Start, LocalTime act2End){
        if(connectedUsers.containsKey(currUser)) {
            User user = connectedUsers.get(currUser);//todo maybe verify the dao was generated
            Response<Boolean> changeWorkTime = user.canSetWorkingTime();
            if(workDay >= 0 && workDay <= 6 && noActivityCollision(act1Start, act1End, act2Start, act2End) && !changeWorkTime.isFailure()){
                Response<Boolean> setWorkingTimeRes = userDAO.setWorkingTime(currUser, workDay, act1Start, act1End, act2Start, act2End);
                if(!setWorkingTimeRes.isFailure()){
                    user.setWorkDay(workDay);
                    user.setAct1Start(act1Start);
                    user.setAct1End(act1End);
                    user.setAct2Start(act2Start);
                    user.setAct2End(act2End);
                }
                return setWorkingTimeRes;
            }
            else{
                return new Response<>(null, true, changeWorkTime.getErrMsg() + " / colliding activity hours");
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    private boolean noActivityCollision(LocalTime act1Start, LocalTime act1End, LocalTime act2Start, LocalTime act2End) {
        return act1Start.isBefore(act1End) &&
                (act1End.isBefore(act2Start) || act1End.equals(act2Start))
                && act2Start.isBefore(act2End);
    }

    public Response<UserDBDTO> getWorkHours(String instructor) {
        return userDAO.getWorkingTime(instructor);
    }

    /**
     * check if coordinator assigned to school {@param symbol} can answer to given survey
     * @param symbol the symbol of school
     * @param surveyID identifier of survey
     * @return positive result if the answers made by suitable coordinator
     */
    public Response<Boolean> isValidAnswer(String symbol, String surveyID) {
        Response<String> supervisorRes = userDAO.getSurveyCreator(surveyID);
        if(!supervisorRes.isFailure()){
            Response<UserDBDTO> userDBDTOResponse = userDAO.getFullUser(supervisorRes.getResult());//todo probably shouldnt pull full user, just work field
            if (!userDBDTOResponse.isFailure()) {
                Response<UserDBDTO> coordinatorRes = userDAO.getCoordinator(symbol, userDBDTOResponse.getResult().getWorkField());
                if(!coordinatorRes.isFailure() && !(coordinatorRes.getResult() == null)){
                    return new Response<>(true, false, coordinatorRes.getErrMsg());
                }
                else{
                    return new Response<>(false, true, coordinatorRes.getErrMsg());
                }
            }
        }
        return new Response<>(false, true,"not allowed to answer");
    }


    //For test purposes only start

    public User getUser(String user){
        return new User(userDAO.getFullUser(user).getResult());
    }

    public Response<User> getUserRes(String user){
        if(userDAO.userExists(user)){
            return new Response<>(new User(userDAO.getFullUser(user).getResult()), false, "user found");
        }
        return new Response<>(null, true, "user not found");
    }

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

    public Response<Boolean> removeCoordinatorTester(String currUser, String workField, String school){
        User user = new User(userDAO.getFullUser(currUser).getResult());
        Response<String> response = user.removeCoordinator(school, workField);
        if(!response.isFailure()){
            return userDAO.removeCoordinator(response.getResult(), school);//todo check not failed
        }
        else{
            return new Response<>(false, true, response.getErrMsg());
        }
    }

    public Response<Boolean> assignCoordinatorTester(String currUser, String workField, String firstName, String lastName, String email, String phoneNumber, String school){
        User user = new User(userDAO.getFullUser(currUser).getResult());
        Response<User> result = user.assignCoordinator(createToken(), workField, school, firstName, lastName, email, phoneNumber);
        if (!result.isFailure()) {
            Response<UserDBDTO> isCoordinatorAssignedRes = userDAO.getCoordinator(school, result.getResult().getWorkField());
            if(!isCoordinatorAssignedRes.isFailure() && isCoordinatorAssignedRes.getResult() == null){
                userDAO.insertUser(new UserDBDTO(result.getResult(), null));
                userDAO.assignSchoolsToUser(result.getResult().getUsername(), result.getResult().getSchools());
                return new Response<>(true, false, "assigned coordinator");
            }
            else{
                return new Response<>(false, true, "a coordinator is already assigned to the school");
            }
        }
        else{
            return new Response<>(null, result.isFailure(), result.getErrMsg());
        }
    }

    public Response<Boolean> changePasswordTester(String currUser, String newPassword){
        if(!ServerContextInitializer.getInstance().isTestMode() && !isValidPassword(newPassword)){
            return new Response<>(false, true, "The password isn't strong enough");
        }
        User user = new User(userDAO.getFullUser(currUser).getResult());
        Response<Boolean> res = user.changePassword();
        if (res.getResult()) {
            userDAO.updateUserPassword(currUser, passwordEncoder.encode(newPassword));
        }
        return res;
    }

    public Response<Boolean> removeUserTester(String currUser, String userToRemove) {
        User user = new User(userDAO.getFullUser(currUser).getResult());
        Response<Boolean> response = user.removeUser(userToRemove);//todo verify connected user is dao updated
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
    //For test purposes only end
}