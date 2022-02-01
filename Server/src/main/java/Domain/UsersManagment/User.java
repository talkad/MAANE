package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Vector;

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
    protected Appointment appointments;
    protected List<Integer> surveys;
    protected List<String> baskets;
//    private MonthlyReport monthlyReport; //todo monthly reports history??
    protected WorkPlan workPlan;


    public User() {
        this.state = new Guest();
        this.appointments = new Appointment();
        this.schools = new Vector<>();
        this.surveys = new Vector<>();
        this.baskets = new Vector<>();
    }

    public User(String username) {
        this.username = username;
        this.appointments = new Appointment();
        this.schools = new Vector<>();
        this.surveys = new Vector<>();
        this.baskets = new Vector<>();
    }

    public User(String username, UserStateEnum userStateEnum) {
        this.state = inferUserType(userStateEnum);
        this.username = username;
        this.appointments = new Appointment();
        this.schools = new Vector<>();
        this.surveys = new Vector<>();
        this.baskets = new Vector<>();
    }

    public User(String username, UserStateEnum userStateEnum, String workField,String firstName, String lastName, String email, String phoneNumber, String city) {
        this.state = inferUserType(userStateEnum);
        this.username = username;
        this.workField = workField;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.appointments = new Appointment();
        this.schools = new Vector<>();
        this.surveys = new Vector<>();
        this.baskets = new Vector<>();
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

    public Appointment getAppointments(){
        return this.appointments;
    }

    public Response<Boolean> assignSchoolsToUser(String userToAssign, List<String> schools) {
        if(this.state.allowed(Permissions.ASSIGN_SCHOOLS_TO_USER, this)) {
            if (appointments.contains(userToAssign)) {
                return appointments.assignSchoolsToUser(userToAssign, schools);
            }
            else {
                return new Response<>(false, true, " the user " + userToAssign + " was not assigned by you");
            }
        }
        else return new Response<>(false, true, "user not allowed to remove schools from users");
    }

    public Response<Boolean> removeUser(String username) { //todo can a user even be appointed twice? if so needs to be removed from all personal that he was appointed by
        if(this.state.allowed(Permissions.REMOVE_USER, this)) {
            if (appointments.contains(username)) {
                Response<Boolean> response = appointments.removeAppointment(username);
                if (!response.isFailure()) {
                    return new Response<>(true, false, "successfully removed the user " + username);
                }
                return response;
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
            Response<Boolean> res = appointments.addAppointment(username);
            if(res.getResult()){
                return new Response<>(new User(username, registerUserStateEnum, this.workField, firstName, lastName, email, phoneNumber, city), false, "user successfully assigned");
            }
            else{
                return new Response<>(null, true, res.getErrMsg());
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
        if(this.state.allowed(Permissions.REGISTER_SUPERVISOR, this) && (registerUserStateEnum == UserStateEnum.SUPERVISOR) && !appointments.contains(username)){
            appointments.addAppointment(username);
            return new Response<>(new User(username, registerUserStateEnum, workField, firstName, lastName, email, phoneNumber, city), false, "supervisor successfully assigned");
        }
        else{
            return new Response<>(null, true, "user not allowed to register users");
        }
    }

    public Response<Boolean> addAppointment(String appointee){
        return appointments.addAppointment(appointee);
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

            return new Response<>(false, false, "user not allowed to change password to this user");        }
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

    public Response<Integer> createSurvey(int surveyId) {
        if(this.state.allowed(Permissions.SURVEY_MANAGEMENT, this)){
            this.surveys.add(surveyId);
            return new Response<>(surveyId, false, "user is allowed to create survey");
        }
        else {
            return new Response<>(-1, true, "user not allowed to create survey");
        }
    }

    public Response<String> createBasket(String basketId) {
        this.baskets.add(basketId);
        return new Response<>(basketId, false, "user is allowed to create basket");
    }

    public Response<Integer> removeSurvey(int surveyId) {
        if(this.state.allowed(Permissions.SURVEY_MANAGEMENT, this)){
            this.surveys.remove(Integer.valueOf(surveyId));
            return new Response<>(surveyId, false, "user is allowed to remove survey");
        }
        else {
            return new Response<>(-1, true, "user not allowed to remove survey");
        }
    }

    public Response<String> removeBasket(String basketId) {
        if(!hasCreatedBasket(basketId).isFailure()){
            this.baskets.remove(basketId);
            return new Response<>(basketId, false, "user is allowed to remove survey");
        }
        else {
            return new Response<>("", true, "user not allowed to remove basket");
        }
    }

    public Response<List<Integer>> getSurveys() {
        if(this.state.allowed(Permissions.SURVEY_MANAGEMENT, this)){
            return new Response<>(surveys, false, "");
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
                return appointments.removeSchoolsFromUser(userToRemoveSchools, schools);
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
            return new Response<>(this.workField, false, "successfully acquired work field");
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

    public Response<Boolean> hasCreatedSurvey(int surveyId) {
        if(this.state.getStateEnum() == UserStateEnum.SUPERVISOR)
        {
            return new Response<>(this.surveys.contains(surveyId), false, "user is supervisor");
        }
        else {
            return new Response<>(false, true, "user is not a supervisor");
        }
    }

    public Response<Boolean> hasCreatedBasket(String basketId) {
        if(this.state.getStateEnum() == UserStateEnum.SUPERVISOR)
        {
            if(this.baskets.contains(basketId))
                return new Response<>(true, false, "user created this basket");

            return new Response<>(false, true, "user not created this basket");
        }
        else {
            return new Response<>(false, true, "user is not a supervisor");
        }
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

    public void setState(UserState state) {
        this.state = state;
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

    public void setAppointments(Appointment appointments) {
        this.appointments = appointments;
    }

    public void setSurveys(List<Integer> surveys) {
        this.surveys = surveys;
    }

    public void setBaskets(List<String> baskets) {
        this.baskets = baskets;
    }

    public void setWorkField(String workField) {
        this.workField = workField;
    }

    public Response<List<String>> getAppointees() {//todo maybe make response and verify
        if (this.state.allowed(Permissions.VIEW_USERS_INFO, this)) {
            return this.appointments.getAppointees();
        }
        else {
            return new Response<>(null, true, "user not allowed to view appointed users");
        }
    }

    public boolean isInstructor() {
        return this.getState().getStateEnum() == UserStateEnum.INSTRUCTOR;
    }

    public void assignWorkPlan(WorkPlan workPlan) {
        this.workPlan = workPlan;//todo prevent errors
    }

    public Response<WorkPlan> getWorkPlan() {
        if (this.state.allowed(Permissions.VIEW_WORK_PLAN, this)) {
            return new Response<>(this.workPlan, false, "");
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


}
