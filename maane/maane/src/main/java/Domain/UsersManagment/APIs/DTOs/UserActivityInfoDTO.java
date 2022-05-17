package Domain.UsersManagment.APIs.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserActivityInfoDTO {

    private LocalDateTime activityStart;
    private LocalDateTime activityEnd;
    private int totalHours;
    private String schoolName;
    private String userCity;
    private String schoolCity;

}
