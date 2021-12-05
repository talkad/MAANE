package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.Vector;

public class Instructor extends User{


    public Instructor(String username, UserStateEnum userStateEnum) {
        this.state = userStateEnum;
        this.name = username;
        this.schools = new Vector<>();
        this.appointments = new Appointment();    }

    public Instructor() {} //todo for supervisor super

    @Override
    public Response<Boolean> registerUser(String username, String password, UserStateEnum registerUserStateEnum) {
        return new Response<>(false, true, "user is not allowed to register users");
    }

    @Override
    public Response<Boolean> removeUser(String username) {
        return new Response<>(false, true, "user is not allowed to remove users");
    }
}