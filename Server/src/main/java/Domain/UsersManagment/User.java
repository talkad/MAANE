package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Vector;

public abstract class User {

    protected UserStateEnum state;
    protected String name;
    protected List<Integer> schools;//todo school as list/map?
    protected Appointment appointments;
//    private MonthlyReport monthlyReport; //todo monthly reports history??
//    private WorkPlan workPlan;

    public User() {
        this.state = UserStateEnum.GUEST;
        this.schools = new Vector<>();//todo maybe just represent school id's
        this.appointments = new Appointment();
    }

    public User(String username, UserStateEnum userStateEnum) {
        this.state = userStateEnum;//todo shit
        this.name = username;
        this.schools = new Vector<>();//todo maybe just represent school id's
        this.appointments = new Appointment();

    }

    public User inferUserState(String username, UserStateEnum userStateEnum) {
        if (userStateEnum == UserStateEnum.INSTRUCTOR) {
            return new Instructor(username, userStateEnum);
        }
        if (userStateEnum == UserStateEnum.SUPERVISOR) {
            return new Supervisor(username, userStateEnum);
        }
        if (userStateEnum == UserStateEnum.GENERAL_SUPERVISOR) {
            return new GeneralSupervisor(username, userStateEnum);
        }
        else return new Guest(); //todo this is error
    }

    public Response<Boolean> logout() {
        if(this.state != UserStateEnum.GUEST)
            return new Response<>(true, false, "logged out successfully");
        return new Response<>(false, true, "Cannot logout without being logged in");
    }

    public String getName() {
        return name;
    }

    public void setName(String name){ this.name = name;}

    public abstract Response<Boolean> registerUser(String username, String password, UserStateEnum registerUserStateEnum);

    public abstract Response<Boolean> removeUser(String username);

}
