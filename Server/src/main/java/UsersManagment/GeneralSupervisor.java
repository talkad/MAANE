package UsersManagment;

import CommonClasses.Response;

import java.util.Vector;

public class GeneralSupervisor extends User{
    public GeneralSupervisor(String username, UserStateEnum userStateEnum) {
        this.state = userStateEnum;//todo shit
        this.name = username;
        this.schools = new Vector<>();//todo maybe just represent school id's
        this.appointments = new Appointment();    }

    @Override
    public Response<Boolean> registerUser(String username, String password, UserStateEnum registerUserStateEnum) {
        return new Response<>(false, true, "user is not allowed to register users");
    }

    @Override
    public Response<Boolean> removeUser(String username) {
        return new Response<>(false, true, "user is not allowed to remove users");
    }
}
