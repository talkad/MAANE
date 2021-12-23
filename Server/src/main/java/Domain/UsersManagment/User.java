package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Vector;

public class User {

    protected UserState state;
    protected String username;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNumber;
    protected String city;
    protected List<Integer> schools;
    protected Appointment appointments;
    protected List<Integer> surveys;
//    private MonthlyReport monthlyReport; //todo monthly reports history??
//    private WorkPlan workPlan;
    protected String workField;

    //todo add to schools
//    semel mossad
//    manager
//    manager phone
//    coordinator of the work field name
//    coordinator phone
//    coordinator email
//    school's city
//    all the status info needs to be inserted as well

    public User() {
        this.state = new Guest();
        this.appointments = new Appointment();
        this.schools = new Vector<>();
        this.surveys = new Vector<>();
    }

    public User(String username) {
        this.username = username;
        this.appointments = new Appointment();
        this.schools = new Vector<>();
        this.surveys = new Vector<>();
    }

    public User(String username, UserStateEnum userStateEnum) {
        this.state = inferUserType(userStateEnum);
        this.username = username;
        this.appointments = new Appointment();
        this.schools = new Vector<>();
        this.surveys = new Vector<>();
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

    public List<Integer> getSchools() {
        return schools;
    }

    public void setSchools(List<Integer> schools) {
        this.schools = schools;
    }

    public Appointment getAppointments(){
        return this.appointments;
    }

    public Response<Boolean> assignSchoolsToUser(String userToAssign, List<Integer> schools) {
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
            appointments.addAppointment(username);
            return new Response<>(new User(username, registerUserStateEnum, this.workField, firstName, lastName, email, phoneNumber, city), false, "user successfully assigned");//todo split to 2 functions cause only admin can define work field?
        }
//        else if(this.state.allowed(PermissionsEnum.REGISTER_SUPERVISOR, this) && (registerUserStateEnum == UserStateEnum.SUPERVISOR) && !appointments.contains(username)){
//            appointments.addAppointment(username);
//            return new Response<>(new User(username, UserStateEnum.SUPERVISOR, workField, firstName, lastName, email, phoneNumber, city), false, "supervisor successfully assigned");
//        }
        else{
            return new Response<>(null, true, "user not allowed to register users");
        }
    }

    public Response<User> registerSupervisor(String username, UserStateEnum registerUserStateEnum, String workField, String firstName, String lastName, String email, String phoneNumber, String city) {
        if(this.state.allowed(Permissions.REGISTER_SUPERVISOR, this) && (registerUserStateEnum == UserStateEnum.SUPERVISOR) && !appointments.contains(username)){
            appointments.addAppointment(username);
            return new Response<>(new User(username, UserStateEnum.SUPERVISOR, workField, firstName, lastName, email, phoneNumber, city), false, "supervisor successfully assigned");
        }
        else{
            return new Response<>(null, true, "user not allowed to register users");
        }
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

    public Response<List<String>> viewInstructorsDetails() {
        if (this.state.allowed(Permissions.VIEW_INSTRUCTORS_INFO, this)) {
            return appointments.getAppointees();
        }
        else{
            return new Response<>(null, true, "user not allowed to view instructor info"); //todo null may be problem and better be empty list
        }
    }

    public String getInfo() {
        return this.username + " " + this.schools.toString(); //todo generate proper tostring
    }

    public Response<Integer> createSurvey(int surveyId) {
        if(this.state.allowed(Permissions.SURVEY_MANAGEMENT, this)){
            this.surveys.add(surveyId);
            return new Response<>(surveyId, false, "user is allowed to create survey");
        }
        else {
            return new Response<>(-1, false, "user not allowed to create survey");
        }
    }

    public Response<Integer> removeSurvey(int surveyId) {
        if(this.state.allowed(Permissions.SURVEY_MANAGEMENT, this)){
            this.surveys.remove(Integer.valueOf(surveyId));
            return new Response<>(surveyId, false, "user is allowed to remove survey");
        }
        else {
            return new Response<>(-1, false, "user not allowed to remove survey");
        }
    }

    public List<Integer> getSurveys() {
        return surveys;
    }

    public String getWorkField(){
        return this.workField;
    }

    public Response<Boolean> removeSchoolsFromUser(String userToRemoveSchools, List<Integer> schools) {
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

    public void removeSchools(List<Integer> schools) {
        for (Integer schoolId: schools) {
            this.schools.remove(schoolId);
        }
    }

    public void addSchools(List<Integer> schools) {
        for (Integer i: schools) {
            if(!this.schools.contains(i)){
                this.schools.add(i);
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

    public void setWorkField(String workField) {
        this.workField = workField;
    }

}
