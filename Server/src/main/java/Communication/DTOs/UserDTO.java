package Communication.DTOs;

import Domain.UsersManagment.UserStateEnum;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String currUser;
    private String userToRegister;
    private String password;
    private UserStateEnum userStateEnum;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String city;

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
