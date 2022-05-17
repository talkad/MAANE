package Domain.UsersManagment.APIs.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserActivityInfoDTO {

    private LocalDateTime date;
    private int totalHours;
    private String schoolName;
}
