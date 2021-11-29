package UsersManagment;

import CommonClasses.Response;

import java.util.Vector;

public class SystemManager extends User{
    public SystemManager(String username, UserStateEnum userStateEnum) {
        this.state = userStateEnum;
        this.name = username;
        this.schools = new Vector<>();//todo unlikely to stick around
        this.appointments = new Appointment();//todo unlikely to stick around
    }

    @Override
    public Response<Boolean> registerUser(String username, String password, UserStateEnum registerUserStateEnum) {
        return new Response<>(true, false, "successfully registered a new user");
    }

    public Response<Boolean> removeUser(String username){
        return new Response<>(true, false, "successfully removed the user");//todo remove all that users responsibility?
    }
}
