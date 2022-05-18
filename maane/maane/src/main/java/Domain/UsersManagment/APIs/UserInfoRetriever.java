package Domain.UsersManagment.APIs;

import Domain.CommonClasses.Response;
import Domain.UsersManagment.APIs.DTOs.UserActivityInfoDTO;
import Domain.UsersManagment.APIs.DTOs.UserInfoDTO;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

/**
 * currently mock implementation
 * todo by shaked
 */
public class UserInfoRetriever implements UserInfoService{

    @Override
    public Response<Boolean> canGenerateReport(String username) {
        return new Response<>(true, false, "");
    }

    @Override
    public Response<UserInfoDTO> getUserInfo(String username) {
        return new Response<>(new UserInfoDTO("קדוש", "טל", "יבנה", 1),
                false, "");
    }

    @Override
    public Response<List<UserActivityInfoDTO>> getUserActivities(String username, int month) {
        List<UserActivityInfoDTO>  activities = Arrays.asList(
                new UserActivityInfoDTO(LocalDateTime.of(2022, Month.MAY, 15, 10, 0, 0), LocalDateTime.of(2022, Month.MAY, 15, 12, 30, 0), "madaim", "", "lod"),
                new UserActivityInfoDTO(LocalDateTime.of(2022, Month.MAY, 16, 10, 0, 0), LocalDateTime.of(2022, Month.MAY, 16, 14, 40, 0), "bengu", "", "beer7"));

        return new Response<>(activities, false, "");
    }
}
