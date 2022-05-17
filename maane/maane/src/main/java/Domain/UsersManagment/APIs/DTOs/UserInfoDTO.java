package Domain.UsersManagment.APIs.DTOs;

import lombok.Data;


@Data
public class UserInfoDTO {

    private String lastName;
    private String firstName;
    private String city;
    private int workingDay;

}
