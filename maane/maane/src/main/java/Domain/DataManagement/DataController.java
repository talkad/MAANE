package Domain.DataManagement;


import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserController;
import Persistence.DbDtos.SchoolDBDTO;
import Persistence.DbDtos.UserDBDTO;
import Persistence.SchoolQueries;
import Persistence.UserQueries;

import java.util.List;

public class DataController {

    private SchoolQueries schoolDAO;

    private DataController() {
        this.schoolDAO = SchoolQueries.getInstance();
    }


    private static class CreateSafeThreadSingleton {
        private static final DataController INSTANCE = new DataController();
    }

    public static DataController getInstance() {
        return CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<List<String>> getCoordinatorsEmails(String workField){
        //todo change to one query
        return UserQueries.getInstance().getCoordinatorEmails(workField);

/*        public Response<List<String>> getCoordinatorsEmails(String workField){
        List<String> emails = new Vector<>();
        for (String symbol: schools.keySet()) {
            if(schools.get(symbol).getCoordinators().containsKey(workField)){//todo check that there is actually an email assigned
                emails.add(schools.get(symbol).getCoordinators().get(workField).getEmail());
            }
        }*/
        //return new Response<>(emails, false, "successfully acquired emails");
    }

    public Response<Boolean> assignCoordinator(String currUser, String workField, String firstName, String lastName, String email, String phoneNumber, String school) {
        if(schoolDAO.schoolSymbolExists(school)/*this.schools.containsKey(school)*/){
            return UserController.getInstance().assignCoordinator(currUser, workField, firstName, lastName, email, phoneNumber, school);
            //Response<Boolean> coordinator = UserController.getInstance().assignCoordinator(currUser, workField, firstName, lastName, email, phoneNumber, school);
            /*if(!coordinator.isFailure() && !this.schools.get(school).getCoordinators().containsKey(coordinator.getResult().getWorkField())){
                this.schools.get(school).getCoordinators().put(coordinator.getResult().getWorkField(), coordinator.getResult());
                return new Response<>(true, coordinator.isFailure(), coordinator.getErrMsg());
            }*/
            //return new Response<>(false, true, coordinator.getErrMsg());
        }
        else{
            return new Response<>(false, true, "no such school exists");
        }
    }

    public Response<Boolean> removeCoordinator(String currUser, String workField, String school) {
        if(schoolDAO.schoolSymbolExists(school)/*schools.containsKey(school)*/){
            Response<Boolean> coordinatorWorkFieldRes = UserController.getInstance().removeCoordinator(currUser, workField, school);
            /*if(!coordinatorWorkFieldRes.isFailure() && schools.get(school).getCoordinators().containsKey(coordinatorWorkFieldRes.getResult())){
                schools.get(school).getCoordinators().remove(coordinatorWorkFieldRes.getResult());
                return new Response<>(true, false, coordinatorWorkFieldRes.getErrMsg());
            }*/
            return new Response<>(false, true, coordinatorWorkFieldRes.getErrMsg());
        }
        else {
            return new Response<>(false, true, "no such coordinator found");
        }
    }

    public Response<Boolean> insertSchool (SchoolDBDTO school){
        return schoolDAO.insertSchool(school);
    }

    public Response<Boolean> removeSchool (String symbol){
        return schoolDAO.removeSchool(symbol);
    }

    public Response<Boolean> updateSchool (String symbol, SchoolDBDTO school){
        return schoolDAO.updateSchool(symbol, school);
    }


    public SchoolDBDTO getSchool(String symbol){

        return schoolDAO.getSchool(symbol);

        //return this.schools.get(symbol);
    }

    public Response<SchoolDBDTO> getSchool(String username, String symbol){//todo test it
        Response<String> schoolsRes = UserController.getInstance().hasSchool(username, symbol);
        if(!schoolsRes.isFailure()){
            SchoolDBDTO school = schoolDAO.getSchool(symbol);
            Response<UserDBDTO> coordinator = UserController.getInstance().getCoordinator(username, schoolsRes.getResult(), symbol);//todo probably dont need to send workfield fail check it didnt
            school.setCoordinatorFirstName(coordinator.getResult().getFirstName());
            school.setCoordinatorLastName(coordinator.getResult().getLastName());
            school.setCoordinatorPhone(coordinator.getResult().getPhoneNumber());
            school.setCoordinatorEmail(coordinator.getResult().getEmail());
            return new Response<>(school, false, "successfully acquired the school");//todo add coordinator to school
        }
        return new Response<>(null, true, schoolsRes.getErrMsg());
    }

    public Response<List<Pair<String, String>>> getUserSchools(String username) {  //pair<schoolName, symbol> //todo test it
        Response<List<String>> schoolsRes = UserController.getInstance().getUserSchools(username);
        if (!schoolsRes.isFailure()) {
            return schoolDAO.getSchoolNameAndSymbol(schoolsRes.getResult());
        } else return new Response<>(null, true, schoolsRes.getErrMsg());
    }


    //for test purposes only
    public void clearSchools() {
        schoolDAO.deleteSchools();
        //this.schools = new ConcurrentHashMap<>();
    }

    //for test purposes only
    public void addOneSchool() {
        SchoolDBDTO schoolDBDTO = new SchoolDBDTO();
        schoolDBDTO.setSymbol("1");
        schoolDAO.insertSchool(schoolDBDTO);
        //schools.put("1", new School("1", "", "", ""));
    }
}
