package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.Vector;

public class Supervisor extends Instructor{

    public Supervisor(String username, UserStateEnum userStateEnum) {
        super();
        this.state = userStateEnum;//todo shit
        this.name = username;
        this.schools = new Vector<>();//todo maybe just represent school id's
        this.appointments = new Appointment();
    }

    @Override
    public Response<Boolean> registerUser(String username, String password, UserStateEnum registerUserStateEnum) {
        if((registerUserStateEnum == UserStateEnum.INSTRUCTOR
                || registerUserStateEnum == UserStateEnum.GENERAL_SUPERVISOR) && !appointments.contains(username)){
            appointments.addAppointment(username);
            return new Response<>(true, false, "user successfully assigned");
        }
        else{
            return new Response<>(false, true, "failed to assign user");//todo maybe more informative error
        }
    }

    @Override
    public Response<Boolean> removeUser(String username) {
        if (appointments.contains(username)) {
            Response<Boolean> response = appointments.removeAppointment(username);
            if (!response.isFailure()) {
                return new Response<>(true, false, "successfully removed the user " + username);
            }
            return response;
        }
        else {
            return new Response<>(false, true, " the user " + username + " was not assigned by you");
        }
    }
}
