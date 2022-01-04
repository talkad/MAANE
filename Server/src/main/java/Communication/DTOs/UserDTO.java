package Communication.DTOs;

import Domain.UsersManagment.UserStateEnum;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String currUser;
    private String workField;
    private String userToRegister;
    private String password;
    private UserStateEnum userStateEnum;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String city;
    private List<String> schools;


    public String getCurrUser() {
        return currUser;
    }

    public String getUserToRegister() {
        return userToRegister;
    }

    public String getPassword() {
        return password;
    }

    public UserStateEnum getUserStateEnum() {
        return userStateEnum;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setCurrUser(String currUser) {
        this.currUser = currUser;
    }

    public String getWorkField() {
        return workField;
    }

    public void setWorkField(String workField) {
        this.workField = workField;
    }

    public void setUserToRegister(String userToRegister) {
        this.userToRegister = userToRegister;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserStateEnum(UserStateEnum userStateEnum) {
        this.userStateEnum = userStateEnum;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCity() {
        return city;
    }


}
