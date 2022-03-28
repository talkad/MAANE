package Domain.UsersManagment;

import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class User {

    protected UserState state;
    protected String username;
    protected String workField;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNumber;
    protected String city;
    protected List<String> schools;
    protected List<String> appointments;
    protected List<String> surveys;
    protected List<String> baskets;
//    private MonthlyReport monthlyReport; //todo monthly reports history??
    protected Map<String, WorkPlan> workPlan;


    public User() {
        this.state = new Guest();
        this.appointments = new Vector<>();
        this.schools = new Vector<>();
        this.surveys = new Vector<>();
        this.baskets = new Vector<>();
    }

    public User(String username, UserStateEnum userStateEnum) {
        this.state = inferUserType(userStateEnum);
        this.username = username;
        this.appointments = new Vector<>();
        this.schools = new Vector<>();
        this.surveys = new Vector<>();
        this.baskets = new Vector<>();
    }

    public User(String username, UserStateEnum userStateEnum, String workField, String firstName, String lastName, String email, String phoneNumber, String city) {
        this.state = inferUserType(userStateEnum);
        this.username = username;
        this.workField = workField;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.appointments = new Vector<>();
        this.schools = new Vector<>();
        this.surveys = new Vector<>();
        this.baskets = new Vector<>();
        if(this.state.getStateEnum() == UserStateEnum.INSTRUCTOR){
            this.workPlan = new ConcurrentHashMap<>();
        }
    }

    private UserDTO getUserDTO(){
        UserDTO userDTO = new UserDTO();
        userDTO.setCurrUser(this.username);
        userDTO.setWorkField(this.workField);
        userDTO.setFirstName(this.firstName);
        userDTO.setLastName(this.lastName);
        userDTO.setEmail(this.email);
        userDTO.setUserStateEnum(this.state.getStateEnum());
        userDTO.setPhoneNumber(this.phoneNumber);
        userDTO.setCity(this.city);
        userDTO.setSchools(this.schools);
        return userDTO;
    }


    private UserDTO getCoordinatorDTO(String firstName, String lastName, String email, String phoneNumber, String school, String otherWorkField){
        UserDTO userDTO = new UserDTO();
        userDTO.setWorkField(otherWorkField);
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setEmail(email);
        userDTO.setUserStateEnum(UserStateEnum.COORDINATOR);
        userDTO.setPhoneNumber(phoneNumber);
        List<String> coordinatorSchool = new Vector<>();
        coordinatorSchool.add(school);
        userDTO.setSchools(coordinatorSchool);
        return userDTO;
    }

    private UserState inferUserType(UserStateEnum userStateEnum) {
        UserState state;

        switch (userStateEnum) {
            case INSTRUCTOR:
                state = new Instructor();
                break;
            case SUPERVISOR:
                state = new Supervisor();
                break;
            case GENERAL_SUPERVISOR:
                state = new GeneralSupervisor();
                break;
            case SYSTEM_MANAGER:
                state = new SystemManager();
                break;
            default:
                state = new Registered(); //this is a problem
        }

        return state;
    }

    public Response<Boolean> logout() {
        if(this.state.allowed(Permissions.LOGOUT, this))
            return new Response<>(true, false, "logged out successfully");
        return new Response<>(false, true, "Cannot logout without being logged in");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){ this.username = username;}

    public List<String> getSchools() {
        return schools;
    }

    public void setSchools(List<String> schools) {
        this.schools = schools;
    }

    public List<String> getAppointments(){
        return this.appointments;
    }

    public Response<Boolean> assignSchoolsToUser(String userToAssign, List<String> schools) {
        if(this.state.allowed(Permissions.ASSIGN_SCHOOLS_TO_USER, this)) {
            if (appointments.contains(userToAssign)) {
                return new Response<>(true, false, "successfully assigned the schools to the user " + userToAssign);
            }
            else {
                return new Response<>(false, true, " the user " + userToAssign + " was not assigned by you");
            }
        }
        else return new Response<>(false, true, "user not allowed to assign schools to users");
    }

    public Response<Boolean> removeUser(String username) { //todo can a user even be appointed twice? if so needs to be removed from all personal that he was appointed by
        if(this.state.getStateEnum() == UserStateEnum.SYSTEM_MANAGER){
            if (!appointments.contains(username)) {
                return new Response<>(true, false, "removing a user by system manager");
            }
            else {
                return new Response<>(false, true, "cannot delete supervisor, try transferring supervisor");
            }
        }
        else if(this.state.allowed(Permissions.REMOVE_USER, this)) {
            if (appointments.contains(username)) {
                boolean res = appointments.remove(username);
                if (res) {
                    return new Response<>(true, false, "successfully removed the user " + username);
                }
                return new Response<>(false, true, "Tried removing a nonexistent appointment");
            } else {
                return new Response<>(false, true, " the user " + username + " was not assigned by you");
            }
        }
        else {
            return new Response<>(false, true, "user not allowed to remove users");
        }
    }

    public Response<User> registerUser(String username, UserStateEnum registerUserStateEnum, String firstName, String lastName, String email, String phoneNumber, String city) {
        if(this.state.allowed(Permissions.REGISTER_USER, this) && (registerUserStateEnum == UserStateEnum.INSTRUCTOR
            || registerUserStateEnum == UserStateEnum.GENERAL_SUPERVISOR) && !appointments.contains(username)) {
            if(!this.appointments.contains(username)) {
                this.appointments.add(username);
                return new Response<>(new User(username, registerUserStateEnum, this.workField, firstName, lastName, email, phoneNumber, city), false, "user successfully assigned");
            }
            else{
                return new Response<>(null, true, "appointment already exists");
            }
        }
        else{
            return new Response<>(null, true, "user not allowed to register users");
        }
    }

    public Response<User> registerUserBySystemManager(String username, UserStateEnum registerUserStateEnum, String workField, String firstName, String lastName, String email, String phoneNumber, String city) {
        if(this.state.allowed(Permissions.REGISTER_BY_ADMIN, this) && (registerUserStateEnum == UserStateEnum.INSTRUCTOR
                || registerUserStateEnum == UserStateEnum.GENERAL_SUPERVISOR)) {
            return new Response<>(new User(username, registerUserStateEnum, workField, firstName, lastName, email, phoneNumber, city), false, "user successfully assigned");//todo split to 2 functions cause only admin can define work field?
        }
        else{
            return new Response<>(null, true, "user not allowed to register users");
        }
    }

    public Response<User> registerSupervisor(String username, UserStateEnum registerUserStateEnum, String workField, String firstName, String lastName, String email, String phoneNumber, String city) {
        if(this.state.allowed(Permissions.REGISTER_SUPERVISOR, this) && !appointments.contains(username)){
            appointments.add(username);

            return new Response<>(new User(username, registerUserStateEnum, workField, firstName, lastName, email, phoneNumber, city), false, "supervisor successfully assigned");
        }
        else{
            return new Response<>(null, true, "user not allowed to register users");
        }
    }

    public Response<Boolean> addAppointment(String appointee){
        return new Response<>(appointments.add(appointee), false, "successfully added appointment");
    }

    public Response<String> fillMonthlyReport(String currUser) {
        if (this.state.allowed(Permissions.FILL_MONTHLY_REPORT, this)) {
            return null; //todo unimplemented error
        }
        return null; //todo unimplemented error
    }

    public Response<Boolean> changePasswordToUser(String userToChangePassword) {
        if (this.state.allowed(Permissions.CHANGE_PASSWORD_TO_USER, this)) {
            if(this.state.getStateEnum() == UserStateEnum.SUPERVISOR && this.appointments.contains(userToChangePassword)){
                return new Response<>(true, false,"successfully password changed");
            }
            else if(this.state.getStateEnum() == UserStateEnum.SYSTEM_MANAGER){
                return new Response<>(true, false,"successfully password changed");
            }
            return new Response<>(false, false, "user not allowed to change password to this user");
        }
        else {
            return new Response<>(false, true, "user not allowed to change password");
        }
    }

    public Response<Boolean> changePassword() {
        if (this.state.allowed(Permissions.CHANGE_PASSWORD, this)) {
            return new Response<>(true, false, "successfully password changed");
        }
        else {
            return new Response<>(false, true, "user not allowed to change password");
        }
    }

    public Response<String> createSurvey(String surveyId) {
        if(this.state.allowed(Permissions.SURVEY_MANAGEMENT, this)){
            this.surveys.add(surveyId);
            return new Response<>(surveyId, false, "user is allowed to create survey");
        }
        else {
            return new Response<>("", true, "user not allowed to create survey");
        }
    }

    public Response<String> createBasket(String basketId) {
        if(this.state.allowed(Permissions.ADD_BASKET, this)){
            this.baskets.add(basketId);
            return new Response<>(basketId, false, "user successfully added basket");
        }
        else {
            return new Response<>(null, true, "user not allowed to add baskets");
        }
    }

    public Response<String> removeSurvey(String surveyId) {
        if(this.state.allowed(Permissions.SURVEY_MANAGEMENT, this)){
            this.surveys.remove(surveyId);
            return new Response<>(surveyId, false, "user is allowed to remove survey");
        }
        else {
            return new Response<>("", true, "user not allowed to remove survey");
        }
    }

    public Response<String> removeBasket(String basketId) {
        if(this.state.allowed(Permissions.REMOVE_BASKET, this)) {
            if (!hasCreatedBasket(basketId).isFailure()) {
                this.baskets.remove(basketId);
                return new Response<>(basketId, false, "successfully removed basket");
            }
            else {
                return new Response<>("", true, "user isn't allowed to remove a basket he didn't create");
            }
        }
        else {
            return new Response<>(null, true, "user not allowed to remove basket");
        }
    }

    public Response<List<String>> getSurveys() {
        if(this.state.allowed(Permissions.SURVEY_MANAGEMENT, this)){
            return new Response<>(this.surveys, false, "");
        }
        else {
            return new Response<>(null, true, "user not allowed to view surveys");
        }
    }

    public String getWorkField(){
        return this.workField;
    }

    public Response<Boolean> removeSchoolsFromUser(String userToRemoveSchools, List<String> schools) {
        if(this.state.allowed(Permissions.REMOVE_SCHOOLS_FROM_USER, this)) {
            if (appointments.contains(userToRemoveSchools)) {
                return new Response<>(true, false, "successfully removed school assignment");
            }
            else {
                return new Response<>(false, true, " the user " + userToRemoveSchools + " was not assigned by you");
            }
        }
        else return new Response<>(false, true, "user not allowed to remove schools from users");
    }

    public void removeSchools(List<String> schools) {
        for (String schoolId: schools) {
            this.schools.remove(schoolId);
        }
    }

    public void addSchools(List<String> schools) {
        for (String school: schools) {
            if(!this.schools.contains(school)){
                this.schools.add(school);
            }
        }
    }

    public Response<String> getGoals() {//todo this and add goals are pretty much the same maybe call this function structure goal_management
        if(this.state.allowed(Permissions.GET_GOALS, this)){
            return new Response<>(this.workField, false, "user allowed to view goals");
        }
        else {
            return new Response<>("", true, "user not allowed to get goals");
        }
    }

    public Response<String> addGoals() {
        if(this.state.allowed(Permissions.ADD_GOALS, this)){
            return new Response<>(this.workField, false, "user allowed to add goals");
        }
        else {
            return new Response<>("", true, "user not allowed to add goals");
        }
    }

    public Response<Boolean> isSupervisor() {
        if(this.state.getStateEnum() == UserStateEnum.SUPERVISOR)
        {
            return new Response<>(true, false, "user is supervisor");
        }
        else {
            return new Response<>(false, false, "user is not a supervisor");
        }
    }

    public Response<Boolean> hasCreatedSurvey(String surveyId) {
        if(this.state.getStateEnum() == UserStateEnum.SUPERVISOR)
        {
            return new Response<>(this.surveys.contains(surveyId), false, "user is supervisor");
        }
        else {
            return new Response<>(false, true, "user is not a supervisor");
        }
    }

    public Response<Boolean> hasCreatedBasket(String basketId) {
        if(this.baskets.contains(basketId)) {
            return new Response<>(true, false, "user created this basket");
        }

        return new Response<>(false, true, "user didn't create this basket");
    }

    public Response<User> updateInfo(String firstName, String lastName, String email, String phoneNumber, String city) {
        if(this.state.allowed(Permissions.UPDATE_INFO, this)){
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.city = city;
            return new Response<>(this, false, "successfully updated information");
        }
        else {
            return new Response<>(null, true, "user not allowed to update information");
        }
    }

    public Response<String> publishSurvey() {
        if(this.state.allowed(Permissions.SURVEY_MANAGEMENT, this)){
            return new Response<>(this.workField, false, "successfully published survey");
        }
        else {
            return new Response<>(null, true, "user not allowed to publish survey");
        }
    }

    public Response<String> generateSchedule() {
        if (this.state.allowed(Permissions.GENERATE_WORK_PLAN, this)) {
            return new Response<>(this.workField, false, "");
        }
        else {
            return new Response<>("", true, "user not allowed to generate work plan");
        }
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserStateEnum state) {
        this.state = inferUserType(state);
        //this.state = state;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAppointments(List<String> appointments) {
        this.appointments = appointments;
    }

    public void setSurveys(List<String> surveys) {
        this.surveys = surveys;
    }

    public void setBaskets(List<String> baskets) {
        this.baskets = baskets;
    }

    public void setWorkField(String workField) {
        this.workField = workField;
    }

    public Response<List<String>> getAppointees() {
        if (this.state.allowed(Permissions.VIEW_USERS_INFO, this)) {
            return new Response<>(this.appointments, false, "successfully acquired appointments");
        }
        else {
            return new Response<>(null, true, "user not allowed to view appointed users");
        }
    }

    public boolean isInstructor() {
        return this.getState().getStateEnum() == UserStateEnum.INSTRUCTOR;
    }

    public void assignWorkPlan(WorkPlan workPlan, String year) {
        this.workPlan.put(year, workPlan);//todo prevent errors
    }

    public Response<WorkPlan> getWorkPlan(String year) {
        if (this.state.allowed(Permissions.VIEW_WORK_PLAN, this)) {
            return new Response<>(this.workPlan.get(year), false, "");
        }
        else {
            return new Response<>(null, true, "user not allowed to view work plan");
        }
    }

    public Response<Boolean> viewAllUsers() {
        if (this.state.allowed(Permissions.VIEW_ALL_USERS_INFO, this)) {
            return new Response<>(true, false, "");
        }
        else {
            return new Response<>(false, false, "user not allowed to view all users");
        }
    }

    public Response<Boolean> isSystemManager() {
        if(this.state.getStateEnum() == UserStateEnum.SYSTEM_MANAGER)
        {
            return new Response<>(true, false, "user is system manager");
        }
        else {
            return new Response<>(false, false, "user is not the system manager");
        }
    }

    public Response<String> removeGoal() {
        if(this.state.getStateEnum() == UserStateEnum.SUPERVISOR)
        {
            return new Response<>(this.workField, false, "acquired work field");
        }
        else {
            return new Response<>(null, true, "not allowed to acquire work field");
        }
    }

    public Response<UserDTO> getInfo() {
        if (this.state.allowed(Permissions.VIEW_INFO, this))
        {
            return new Response<>(getUserDTO(), false, "user dto successfully acquired");
        }
        else {
            return new Response<>(null, true, "user not allowed to view info");
        }
    }

    public Response<UserDTO> assignCoordinator(String workField, String school, String firstName, String lastName, String email, String phoneNumber) {
        if (this.state.allowed(Permissions.REGISTER_COORDINATOR, this))
        {
            if(this.state.getStateEnum() == UserStateEnum.SUPERVISOR) {
                return new Response<>(getCoordinatorDTO(firstName, lastName, email, phoneNumber, school, this.workField), false, "successfully assigned coordinator");
            }
            else if(this.state.getStateEnum() == UserStateEnum.INSTRUCTOR) {
                if(this.schools.contains(school)){
                    return new Response<>(getCoordinatorDTO(firstName, lastName, email, phoneNumber, school, this.workField), false, "successfully assigned coordinator");
                }
                else{
                    return new Response<>(null, true, "user not allowed to assign coordinator to the given school");
                }
            }
            else if(this.state.getStateEnum() == UserStateEnum.SYSTEM_MANAGER){
                return new Response<>(getCoordinatorDTO(firstName, lastName, email, phoneNumber, school, workField), false, "successfully assigned coordinator");
            }
        }
        return new Response<>(null, true, "user not allowed to assign coordinator");
    }

    public Response<String> removeCoordinator(String school, String workField) {
        if (this.state.allowed(Permissions.REMOVE_COORDINATOR, this))
        {
            if(this.state.getStateEnum() == UserStateEnum.SUPERVISOR) {//todo tell aviad to remove the ability to assign workfield if u r not system manager
                return new Response<>(this.workField, false, "successfully removed coordinator");
            }
            else if(this.state.getStateEnum() == UserStateEnum.INSTRUCTOR) {
                if(this.schools.contains(school)){
                    return new Response<>(this.workField, false, "successfully removed coordinator");
                }
                else{
                    return new Response<>(null, true, "user not allowed to remove coordinator from the given school");
                }
            }
            else if(this.state.getStateEnum() == UserStateEnum.SYSTEM_MANAGER){
                return new Response<>(workField, false, "successfully removed coordinator");
            }
        }
        return new Response<>(null, true, "user not allowed to remove coordinator");
    }

    public void removeAppointment(String userToRemove) {
        this.appointments.remove(userToRemove);
    }

    public Response<Boolean> transferSupervision(String currSupervisor, String newSupervisor) {
        if (this.state.allowed(Permissions.TRANSFER_SUPERVISION, this) && this.appointments.contains(currSupervisor) && !this.appointments.contains(newSupervisor))
        {
            return new Response<>(true, false, "user allowed to transfer supervision");
        }
        else {
            return new Response<>(false, true, "user not allowed to transfer supervision");
        }
    }
}
