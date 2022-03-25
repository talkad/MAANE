package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Appointment {//todo is appointment relevant?

    private Map<String, List<String>> userAppointments;

    public Appointment() {
        this.userAppointments = new ConcurrentHashMap<>();
    }

    public void addAppointment(String name, String schoolId){
        if(this.userAppointments.containsKey(name))
            this.userAppointments.get(name).add(schoolId);
        else{
            List<String> appsList = new Vector<>();
            appsList.add(schoolId);
            this.userAppointments.put(name, appsList);
        }
    }

    public Response<Boolean> addAppointment(String name){
        if(!this.userAppointments.containsKey(name)) {
            this.userAppointments.put(name, new Vector<>());
            return new Response<>(true, false, "added appointment");
        }
        return new Response<>(false, false, "appointment already exists");
    }

    public Response<String> removeSchoolAppointment(String name, String schoolId){
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
            response = new Response<>(true, false, "successfully removed appointment");
            this.userAppointments.remove(name);
        }
        else{
            response = new Response<>(null, true, "Tried removing a nonexistent appointment");
        }
        return response;
    }

    public Response<List<String>> getAppointees(){
        return new Response<>(new Vector<>(this.userAppointments.keySet()), false, "successfully generated instructors details");
    }

    public boolean contains(String appointee, String schoolId){
        if(this.userAppointments.containsKey(appointee)){
            return this.userAppointments.get(appointee).contains(schoolId);
        }
        return false;
    }

    public Response<Boolean> assignSchoolsToUser(String userToAssign, List<String> schools){
        if(this.userAppointments.containsKey(userToAssign)){
            for (String schoolId: schools) {
                if(!this.userAppointments.get(userToAssign).contains(schoolId)){
                    this.userAppointments.get(userToAssign).add(schoolId);
                }
            }
            return new Response<>(true, false, "successfully assigned the schools to the user " + userToAssign);
        }
        return new Response<>(true, false, "user was not appointed by you");
    }

    public boolean contains(String appointee){
        return this.userAppointments.containsKey(appointee);
    }

    public List<String> getSchools(String appointee){
        if(contains(appointee)){
            return userAppointments.get(appointee);
        }
        return new Vector<>();//todo error
    }

    public Map<String, List<String>> getUserAppointments(){
        return this.userAppointments;
    }

    public Response<Boolean> removeSchoolsFromUser(String userToRemoveSchools, List<String> schools) {
        if(this.userAppointments.containsKey(userToRemoveSchools)){
            for (String schoolId: schools) {
                this.userAppointments.get(userToRemoveSchools).remove(schoolId);
            }
            return new Response<>(true, false, "successfully removed the schools from the user " + userToRemoveSchools);
        }
        return new Response<>(true, false, "user was not appointed by you");
    }
}
