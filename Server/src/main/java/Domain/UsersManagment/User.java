package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Vector;

public abstract class User {

    protected UserStateEnum state;
    protected String name;
    protected List<Integer> schools;
    protected Appointment appointments;
//    private MonthlyReport monthlyReport; //todo monthly reports history??
//    private WorkPlan workPlan;
    //private String workField;

    public User() {
        this.state = UserStateEnum.GUEST;
        this.schools = new Vector<>();
        this.appointments = new Appointment();
    }

    public User(String username, UserStateEnum userStateEnum) {
        this.state = userStateEnum;
        this.name = username;
        this.schools = new Vector<>();
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

    public List<Integer> getSchools() {
        return schools;
    }

    public void setSchools(List<Integer> schools) {
        this.schools = schools;
    }

    public Appointment getAppointments(){
        return this.appointments;//todo maybe different for users without appointments
    }

    public abstract Response<Boolean> registerUser(String username, String password, UserStateEnum registerUserStateEnum);

    public abstract Response<Boolean> removeUser(String username);

    public abstract Response<Boolean> assignSchoolsToUser(String userToAssign, List<Integer> schools);
}
