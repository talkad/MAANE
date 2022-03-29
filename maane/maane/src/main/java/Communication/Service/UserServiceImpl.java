package Communication.Service;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Communication.Service.Interfaces.UserService;
import DataManagement.DataController;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Response<User> userRes = UserController.getInstance().getUserRes(username);

        if(userRes.isFailure()){
            log.error("user not exist");
            throw new UsernameNotFoundException("user not found");
        }
        else{
            log.info("user {} found", username);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRes.getResult().getState().getStateEnum().getState()));

        return new org.springframework.security.core.userdetails.User(username, DataController.getInstance().loadPassword(username), authorities); //todo - implement the function that loads a password by username
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Response<User> userRes = UserController.getInstance().getUserRes(username);
//        Response<String> pwdRes = UserController.getInstance().getPassword(username);
//        User user = userRes.getResult();
//        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaa");
//        if(userRes.isFailure() || pwdRes.isFailure()){
//            log.error("user not found");
//            throw new UsernameNotFoundException("user not found");
//        }
//        else {
//            log.info("user {} found", username);
//        }
//
//        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(user.getState().getStateEnum().getState()));
//
//        return new org.springframework.security.core.userdetails.User(user.getUsername(), pwdRes.getResult(), authorities);
//    }

    @Override
    public Response<String> addGuest() {
        Response<String> res = UserController.getInstance().addGuest();

        if (res.isFailure())
            log.error("failed to add new guest");
        else
            log.info("created new guest");

        return res;
    }

    @Override
    public Response<String> removeGuest(String name) {
        Response<String> res = UserController.getInstance().removeGuest(name);

        if (res.isFailure())
            log.error("failed to remove guest {}", name);
        else
            log.info("removing guest with name: {}", name);

        return res;
    }

    @Override
    public Response<Pair<String, UserStateEnum>> login(String currUser, String userToLogin, String password) {
        Response<Pair<String, UserStateEnum>> res = UserController.getInstance().login(currUser, userToLogin, password);

        if (res.isFailure())
            log.error("failed to login with user {}", userToLogin);
        else
            log.info("login successful for {}", userToLogin);

        return res;
    }

    @Override
    public Response<String> logout(String name) {
        Response<String> res = UserController.getInstance().logout(name);

        if (res.isFailure())
            log.error("failed to logout user {}", name);
        else
            log.info("logged out for user {}", name);

        return res;
    }

    @Override
    public Response<String> registerUser(String currUser, UserDTO user) {
        Response<String> res = UserController.getInstance().registerUser(currUser,
                user.getUserToRegister(), passwordEncoder.encode(user.getPassword()), user.getUserStateEnum(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getCity());

        if (res.isFailure())
            log.error("failed to register user {}", user.getUserToRegister());
        else
            log.info("user {} registered successfully", user.getUserToRegister());

        return res;
    }

    @Override
    public Response<String> registerUserBySystemManager(String currUser, UserDTO user, String optionalSupervisor) {
        Response<String> res = UserController.getInstance().registerUserBySystemManager(currUser, user.getUserToRegister(), user.getPassword(), user.getUserStateEnum(), optionalSupervisor, user.getWorkField(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getCity());

        if (res.isFailure())
            log.error("failed to register user {}", user.getUserToRegister());
        else
            log.info("user {} registered successfully", user.getUserToRegister());

        return res;
    }

    @Override
    public Response<Boolean> removeUser(String currUser, String userToRemove) {
        Response<Boolean> res = UserController.getInstance().removeUser(currUser, userToRemove);

        if (res.isFailure())
            log.error("failed to remove user {}", userToRemove);
        else
            log.info("removed user {}", userToRemove);

        return res;
    }

    @Override
    public Response<WorkPlanDTO> viewWorkPlan(String currUser, String year) {
        Response<WorkPlanDTO> res = UserController.getInstance().viewWorkPlan(currUser, year);

        if (res.isFailure())
            log.error("user {} cannot view plan", currUser);
        else
            log.info("user {} viewed plan", currUser);

        return res;
    }

    @Override
    public Response<List<UserDTO>> getAppointedUsers(String currUser) {//todo implement on client side
        Response<List<UserDTO>> res = UserController.getInstance().getAppointedUsers(currUser);

        if (res.isFailure())
            log.error("failed to get Appointed users for the user {}", currUser);
        else
            log.info("user {} successfully received appointed users", currUser);
        return res;
    }

    //for testing purposes only
    @Override
    public Response<User> getUserRes(String username) {
        return UserController.getInstance().getUserRes(username);
    }

    @Override
    public Response<Boolean> assignSchoolsToUser(String currUser, String userToAssignName, List<String> schools) {
        Response<Boolean> res = UserController.getInstance().assignSchoolsToUser(currUser, userToAssignName, schools);

        if (res.isFailure())
            log.error("failed to assign schools for the user {} by the user {}", userToAssignName, currUser);
        else
            log.info("user {} was successfully assigned to the schools by {}", userToAssignName, currUser);
        return res;
    }

    @Override
    public Response<Boolean> verifyUser(String currUser, String password) {
        Response<Boolean> res = UserController.getInstance().verifyUser(currUser, password);

        if (res.isFailure())
            log.error("failed to verify the user {}", currUser);
        else
            log.info("successfully verified the user {}", currUser);

        return res;
    }

    public Response<Boolean> addGoal(String currUser, GoalDTO goalDTO, String year) {
        Response<Boolean> res = UserController.getInstance().addGoal(currUser, goalDTO, year);

        if (res.isFailure())
            log.error("failed to add goals by {}", currUser);
        else
            log.info("user {} successfully added goals", currUser);
        return res;
    }

    public Response<Boolean> removeGoal(String currUser, String year, int goalId) {
        Response<Boolean> res = UserController.getInstance().removeGoal(currUser, year, goalId);

        if (res.isFailure())
            log.error("failed to remove goal by {}", currUser);
        else
            log.info("user {} successfully removed goal", currUser);
        return res;
    }

    public Response<List<GoalDTO>> getGoals(String currUser, String year) {
        Response<List<GoalDTO>> res = UserController.getInstance().getGoals(currUser, year);

        if (res.isFailure())
            log.error("failed to view goal by {}", currUser);
        else
            log.info("the user {} successfully accessed goals", currUser);
        return res;
    }

    public Response<Boolean> updateInfo(String currUser, String firstName, String lastName, String email, String phoneNumber, String city) {
        Response<Boolean> res = UserController.getInstance().updateInfo(currUser, firstName, lastName, email, phoneNumber, city);
        if (res.isFailure())
            log.error("failed to update {}'s info", currUser);
        else
            log.info("successfully updated {}'s info", currUser);
        return res;
    }

    public Response<UserDTO> getUserInfo(String currUser) {
        Response<UserDTO> res = UserController.getInstance().getUserInfo(currUser);
        if (res.isFailure())
            log.error("failed to get {}'s info", currUser);
        else
            log.info("successfully acquired {}'s info", currUser);
        return res;
    }

        public Response<Boolean> changePasswordToUser(String currUser, String userToChangePassword, String newPassword, String confirmPassword) {
        Response<Boolean> res = UserController.getInstance().changePasswordToUser(currUser, userToChangePassword, newPassword, confirmPassword);
        if (res.isFailure())
            log.error("failed to update {}'s password", userToChangePassword);
        else
            log.info("successfully updated {}'s password", userToChangePassword);
        return res;
    }

    public Response<Boolean> changePassword(String currUser, String currPassword, String newPassword, String confirmPassword) {
        Response<Boolean> res = UserController.getInstance().changePassword(currUser, currPassword, newPassword, confirmPassword);
        if (res.isFailure())
            log.error("failed to update {}'s password", currUser);
        else
            log.info("successfully updated {}'s password", currUser);
        return res;
    }

    public Response<List<UserDTO>> getAllUsers(String currUser) {
        Response<List<UserDTO>> res = UserController.getInstance().getAllUsers(currUser);
        if (res.isFailure())
            log.error("failed to acquire all users by {}", currUser);
        else
            log.info("successfully to acquired all users by {}", currUser);
        return res;
    }

    public Response<Boolean> removeSchoolsFromUser(String currUser, String userToRemoveSchoolsName, List<String> schools) {
        Response<Boolean> res = UserController.getInstance().removeSchoolsFromUser(currUser, userToRemoveSchoolsName, schools);
        if (res.isFailure())
            log.error("failed to remove school from user by {}", currUser);
        else
            log.info("successfully removed schools from the user {} by {}", userToRemoveSchoolsName, currUser);
        return res;
    }



}
