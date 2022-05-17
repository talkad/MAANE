package Domain.UsersManagment.APIs.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoDTO {

    private String lastName;
    private String firstName;
    private int numInstructionDays;

    // index of the working days, when sunday-0, monday-1 ...
    private List<Integer> workingDays;

}
