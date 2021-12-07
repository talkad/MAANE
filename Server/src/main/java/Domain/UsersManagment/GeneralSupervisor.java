package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Vector;

public class GeneralSupervisor extends User{
    public GeneralSupervisor(String username, UserStateEnum userStateEnum) {
        this.state = userStateEnum;
        this.name = username;
        this.schools = new Vector<>();
        this.appointments = new Appointment();    }

    @Override
    public Response<Boolean> registerUser(String username, String password, UserStateEnum registerUserStateEnum) {
        return new Response<>(false, true, "user is not allowed to register users");
    }

    @Override
    public Response<Boolean> removeUser(String username) {
        return new Response<>(false, true, "user is not allowed to remove users");
    }

    @Override
    public Response<Boolean> assignSchoolsToUser(String userToAssign, List<Integer> schools) {
        return new Response<>(false, true, "user is not allowed to assign schools to users");
    }
}
