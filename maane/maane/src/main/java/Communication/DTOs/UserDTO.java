package Communication.DTOs;

import Domain.UsersManagment.UserStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
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


}
