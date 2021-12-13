package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {

    protected UserState state;
    protected String name;
    protected List<Integer> schools;
    protected Appointment appointments;
//    private MonthlyReport monthlyReport; //todo monthly reports history??
//    private WorkPlan workPlan;
    //private String workField;

    public User() {
        this.state = new Guest();
        this.appointments = new Appointment();
        this.schools = new Vector<>();

    }

    public User(String username) {
        this.name = username;
        this.appointments = new Appointment();
        this.schools = new Vector<>();
    }

    public User(String username, UserStateEnum userStateEnum) {
        this.state = inferUserType(userStateEnum);
        this.name = username;
        this.appointments = new Appointment();
        this.schools = new Vector<>();
    }

    private UserState inferUserType(UserStateEnum userStateEnum) {
        return switch (userStateEnum) {
            case INSTRUCTOR -> new Instructor();
            case SUPERVISOR -> new Supervisor();
            case GENERAL_SUPERVISOR -> new GeneralSupervisor();
            default -> new Registered(); //this is a problem
        };
    }

    public Response<Boolean> logout() {
        if(this.state.allowed(PermissionsEnum.LOGOUT, this) )
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
        return this.appointments; //todo maybe different for users without appointments
    }

    public Response<Boolean> assignSchoolsToUser(String userToAssign, List<Integer> schools) {
        if(appointments.contains(userToAssign)){
            for (Integer schoolId: schools) {
                if(!UserController.getInstance().getUser(userToAssign).schools.contains(schoolId)){
                    UserController.getInstance().getUser(userToAssign).schools.add(schoolId);
                }
            }
            return appointments.assignSchoolsToUser(userToAssign, schools);
        }
        else{
            return new Response<>(false, true, " the user " + userToAssign + " was not assigned by you");
        }
    }

    public Response<Boolean> removeUser(String username) {
        if(appointments.contains(username)) {
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

    public Response<Boolean> registerUser(String username, UserStateEnum registerUserStateEnum) {
        if((registerUserStateEnum == UserStateEnum.INSTRUCTOR
                || registerUserStateEnum == UserStateEnum.GENERAL_SUPERVISOR) && !appointments.contains(username)){
            appointments.addAppointment(username);
            return new Response<>(true, false, "user successfully assigned");
        }
        else{
            return new Response<>(false, true, "failed to assign user");//todo maybe more informative error
        }
    }

    public Response<Boolean> registerSupervisor(String username) { //todo may combine with register user and to proper adjustments
        if(!appointments.contains(username)){
            appointments.addAppointment(username);
            return new Response<>(true, false, "supervisor successfully assigned");
        }
        else{
            return new Response<>(false, true, "failed to assign user");//todo maybe more informative error
        }
    }

}
