package Persistence;

import Domain.UsersManagment.UserState;
import Domain.UsersManagment.UserStateEnum;
import Domain.UsersManagment.WorkPlan;

import java.util.List;
import java.util.Map;

public class UserDBDTO {
    protected String username;
    protected UserStateEnum stateEnum;
    protected String workField;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNumber;
    protected String city;
    protected String password;
    protected List<String> schools;
    protected List<String> appointments;
    protected List<String> surveys;
    protected List<String> baskets;
    //    private MonthlyReport monthlyReport; //todo monthly reports history??
    protected Map<String, WorkPlan> workPlan;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStateEnum getStateEnum() {
        return stateEnum;
    }

    public void setStateEnum(UserStateEnum stateEnum) {
        this.stateEnum = stateEnum;
    }

    public String getWorkField() {
        return workField;
    }

    public void setWorkField(String workField) {
        this.workField = workField;
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

    public List<String> getSchools() {
        return schools;
    }

    public void setSchools(List<String> schools) {
        this.schools = schools;
    }

    public List<String> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<String> appointments) {
        this.appointments = appointments;
    }

    public List<String> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<String> surveys) {
        this.surveys = surveys;
    }

    public List<String> getBaskets() {
        return baskets;
    }

    public void setBaskets(List<String> baskets) {
        this.baskets = baskets;
    }

    public Map<String, WorkPlan> getWorkPlan() {
        return workPlan;
    }

    public void setWorkPlan(Map<String, WorkPlan> workPlan) {
        this.workPlan = workPlan;
    }
}
