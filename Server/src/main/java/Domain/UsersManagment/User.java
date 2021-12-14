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
//    private String workField;
    //todo add more details like phone number and such

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
            case SYSTEM_MANAGER -> new SystemManager();
            default -> new Registered(); //this is a problem
        };
    }

    public Response<Boolean> logout() {
        if(this.state.allowed(PermissionsEnum.LOGOUT, this))
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
        if(this.state.allowed(PermissionsEnum.ASSIGN_SCHOOLS_TO_USER, this)) {
            if (appointments.contains(userToAssign)) {
                for (Integer schoolId : schools) {
                    if (!UserController.getInstance().getUser(userToAssign).schools.contains(schoolId)) {
                        UserController.getInstance().getUser(userToAssign).schools.add(schoolId);
                    }
                }
                return appointments.assignSchoolsToUser(userToAssign, schools);
            } else {
                return new Response<>(false, true, " the user " + userToAssign + " was not assigned by you");
            }
        }
        else return new Response<>(false, true, "user not allowed to assign schools to users");
    }

    public Response<Boolean> removeUser(String username) { //todo can a user even be appointed twice? if so needs to be removed from all personal that he was appointed by
        if(this.state.allowed(PermissionsEnum.REMOVE_USER, this)) {
            if (appointments.contains(username)) {
                Response<Boolean> response = appointments.removeAppointment(username);
                if (!response.isFailure()) {
                    return new Response<>(true, false, "successfully removed the user " + username);
                }
                return response;
            } else {
                return new Response<>(false, true, " the user " + username + " was not assigned by you");
            }
        }
        else {
            return new Response<>(false, true, "user not allowed to remove users");
        }
    }

    public Response<User> registerUser(String username, UserStateEnum registerUserStateEnum) {
        if(this.state.allowed(PermissionsEnum.REGISTER_USER, this)) {
            if ((registerUserStateEnum == UserStateEnum.INSTRUCTOR
                    || registerUserStateEnum == UserStateEnum.GENERAL_SUPERVISOR) && !appointments.contains(username)) {
                appointments.addAppointment(username);
                return new Response<>(new User(username, registerUserStateEnum), false, "user successfully assigned");
            } else {
                return new Response<>(null, true, "failed to assign user");//todo maybe more informative error
            }
        }
        else{
            return new Response<>(null, true, "user not allowed to register users");
        }
    }

    public Response<User> registerSupervisor(String username) { //todo may combine with register user and to proper adjustments
        if(this.state.allowed(PermissionsEnum.REGISTER_SUPERVISOR, this)) {
            if (!appointments.contains(username)) {
                appointments.addAppointment(username);
                return new Response<>(new User(username, UserStateEnum.SUPERVISOR), false, "supervisor successfully assigned");
            } else {
                return new Response<>(null, true, "failed to assign user");//todo maybe more informative error and null may be bad
            }
        }
        else{
            return new Response<>(null, true, "user not allowed to register supervisors");
        }
    }

    public Response<String> fillMonthlyReport(String currUser) {
        if (this.state.allowed(PermissionsEnum.FILL_MONTHLY_REPORT, this)) {
            return null; //todo unimplemented error
        }
        return null; //todo unimplemented error
    }

    public Response<Boolean> changePassword(String userToChangePassword, String newPassword) {
        if (this.state.allowed(PermissionsEnum.CHANGE_PASSWORD, this)) {
            return new Response<>(true, false,"successfully password changed");
        }
        else{
            return new Response<>(false, true, "user not allowed to change password");
        }
    }

    public Response<String> viewInstructorsDetails() {
        if (this.state.allowed(PermissionsEnum.VIEW_INSTRUCTORS_INFO, this)) {
            Response<List<String>> instructors = appointments.getAppointees();
            StringBuilder instructorDetails = new StringBuilder(); //todo maybe make it a list??
            for (String instructor: instructors.getResult()) {
                instructorDetails.append(UserController.getInstance().getUser(instructor).getInfo());
            }
            return new Response<>(instructorDetails.toString(), false, "successfully generated instructors details");
        }
        else{
            return new Response<>("", true, "user not allowed to view instructor info");
        }
    }

    public String getInfo() {
        return this.name + " " + this.schools.toString(); //todo generate proper tostring
    }
}
