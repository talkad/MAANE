package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Appointment {//todo go over entire class and fit it to what we need

    private Map<String, List<Integer>> userAppointments;

    public Appointment() {
        this.userAppointments = new ConcurrentHashMap<>();
    }

    public void addAppointment(String name, int schoolId){
        if(this.userAppointments.containsKey(name))
            this.userAppointments.get(name).add(schoolId);
        else{
            List<Integer> appsList = new Vector<>();
            appsList.add(schoolId);
            this.userAppointments.put(name, appsList);
        }
    }

    public Response<Boolean> addAppointment(String name){
        if(!this.userAppointments.containsKey(name)) {
            this.userAppointments.put(name, new Vector<>());
            return new Response<>(true, false, "added appointment");
        }
        return new Response<>(false, true, "appointment already exists");
    }

    public Response<String> removeSchoolAppointment(String name, int schoolId){
        Response<String> response;
        if(this.userAppointments.containsKey(name) && this.userAppointments.get(name).contains(schoolId)) {
            response = new Response<>(name, false, "successfully removed school assignment");
            this.userAppointments.get(name).remove(schoolId);
        }
        else{
            response = new Response<>(null, true, "Tried removing school assignment for nonexistent user");
        }
        return response;
    }

    public Response<Boolean> removeAppointment(String name){
        Response<Boolean> response;
        if(this.userAppointments.containsKey(name)) {
            response = new Response<>(true, false, "successfully removed school assignment");
            this.userAppointments.remove(name);//todo maybe look at again
        }
        else{
            response = new Response<>(null, true, "Tried removing school assignment for nonexistent user");//todo bad errmsg
        }
        return response;
    }

    public Response<List<String>> getAppointees(String name){//todo should return all assigned users by this user
        return new Response<>(new Vector<>(this.userAppointments.keySet()), false, "");
//        Response<List<Integer>> response;
//        if(this.userAppointments.containsKey(name)){
//            response = new Response<>(this.userAppointments.get(name), false, "");
//        }
//        else response = new Response<>(new Vector<>(), true, "No appointments for given store");
//        return response;
    }

    public boolean contains(String appointee, int schoolId){
        if(this.userAppointments.containsKey(appointee)){
            return this.userAppointments.get(appointee).contains(schoolId);
        }
        return false;
    }

    public Response<Boolean> assignSchoolsToUser(String userToAssign, List<Integer> schools){
        if(this.userAppointments.containsKey(userToAssign)){
            this.userAppointments.get(userToAssign).addAll(schools);//todo remove duplicated schools maybe make it a set
            return new Response<>(true, false, "successfully assigned the schools to the user " + userToAssign);
        }
        return new Response<>(true, false, "user was not appointed by you");//todo better errmsg
    }

    public boolean contains(String appointee){
        return this.userAppointments.containsKey(appointee);
    }

    public List<Integer> getSchools(String appointee){
        if(contains(appointee)){
            return userAppointments.get(appointee);//todo add schools to appointee list schools
        }
        return new Vector<>();//todo error
    }
}
