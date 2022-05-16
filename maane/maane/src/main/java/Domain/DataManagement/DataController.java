package Domain.DataManagement;


import Communication.DTOs.GoalDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.Rules.*;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Persistence.DbDtos.SchoolDBDTO;
import Persistence.DbDtos.UserDBDTO;
import Persistence.GoalsQueries;
import Persistence.SchoolQueries;
import Persistence.SurveyDAO;
import Persistence.UserQueries;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
            return UserController.getInstance().removeCoordinator(currUser, workField, school);
            /*if(!coordinatorWorkFieldRes.isFailure() && schools.get(school).getCoordinators().containsKey(coordinatorWorkFieldRes.getResult())){
                schools.get(school).getCoordinators().remove(coordinatorWorkFieldRes.getResult());
                return new Response<>(true, false, coordinatorWorkFieldRes.getErrMsg());
            }*/
            //return new Response<>(false, true, coordinatorWorkFieldRes.getErrMsg());
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
            if(!schoolsRes.getResult().equals("")) { //admin case - no workField
                Response<UserDBDTO> coordinator = UserController.getInstance().getCoordinator(username, schoolsRes.getResult(), symbol);//todo probably dont need to send workfield fail check it didnt
                if (!coordinator.isFailure() && coordinator.getResult() != null) {
                    school.setCoordinatorFirstName(coordinator.getResult().getFirstName());
                    school.setCoordinatorLastName(coordinator.getResult().getLastName());
                    school.setCoordinatorPhone(coordinator.getResult().getPhoneNumber());
                    school.setCoordinatorEmail(coordinator.getResult().getEmail());
                }
            }
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

    public Response<Boolean> resetDB(){
        UserController userController = UserController.getInstance();
        userController.resetDB();
        userController.login("admin");

        userController.registerUserBySystemManager("admin","ronit", "1234abcd", UserStateEnum.SUPERVISOR, "", "tech",
            "ronit", "newe", "ronit@gmail.com", "055-555-5555",  "");

        userController.registerUserBySystemManager("admin","tal", "1234abcd", UserStateEnum.INSTRUCTOR, "ronit", "tech",
                    "tal", "kad", "tal@gmail.com", "055-555-5555",  "");

        userController.registerUserBySystemManager("admin","shaked", "1234abcd", UserStateEnum.INSTRUCTOR, "ronit", "tech",
                    "shaked", "ch", "shaked@gmail.com", "055-555-5555",  "");

        insertSchool(new SchoolDBDTO("1111111", "testing school", "beer sheva", "", "", "", "", "", "", "", "", 1000000, "", "", "", "", 30));

        insertSchool(new SchoolDBDTO("2222222", "testing school2", "beer sheva", "", "", "", "", "", "", "", "", 1000000, "", "", "", "", 31));

        assignCoordinator("admin", "tech", "aviad", "shal", "aviad@gmail.com", "0555555555", "1111111");

        userController.logout("admin");
        userController.login("ronit");

        List<String> school1 = new Vector<>();
        List<String> school2 = new Vector<>();
        List<String> school3 = new Vector<>();
        school1.add("1111111");
        school2.add("2222222");
        school3.add("33333333");

        userController.assignSchoolsToUser("ronit", "tal", school1);
        userController.assignSchoolsToUser("ronit", "shaked", school2);
        userController.assignSchoolsToUser("ronit", "shaked", school3);

        // create survey
        SurveyDTO surveyDTO = new SurveyDTO(true, "1111", "title", "description",
        Arrays.asList("symbol", "open?", "numeric?", "multiple choice?"),
        Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), Arrays.asList("correct", "wrong")),
        Arrays.asList(AnswerType.NUMERIC_ANSWER, AnswerType.OPEN_ANSWER, AnswerType.NUMERIC_ANSWER, AnswerType.MULTIPLE_CHOICE), 2022);

        SurveyController.getInstance().createSurvey("ronit", surveyDTO);

        // create goals
        GoalDTO goalDTO1 = new GoalDTO(555, "yahad1", "", 1,
                5, "tech", 2022);
        GoalDTO goalDTO2 = new GoalDTO(666, "yahad2", "", 2,
                10, "tech",2022);

        GoalsQueries.getInstance().insertGoalMock(goalDTO1);
        GoalsQueries.getInstance().insertGoalMock(goalDTO2);

        // create rules
        Rule rule1 = new AndRule(Arrays.asList(new NumericBaseRule(2, Comparison.EQUAL, 30),
                new MultipleChoiceBaseRule(3, List.of(1))));
        Rule rule2 = new NumericBaseRule(2, Comparison.EQUAL, 30);

        SurveyDAO.getInstance().insertRule("1111", 555, rule1.getDTO());
        SurveyDAO.getInstance().insertRule("1111", 666, rule2.getDTO());

        // submit survey
        SurveyController.getInstance().submitSurvey("tal", "1111");

        // add answers
        SurveyDAO.getInstance().insertCoordinatorAnswers("1111", "01234",
                new LinkedList<>(Arrays.asList("open ans", "20", "0")),
                new LinkedList<>(Arrays.asList(AnswerType.OPEN_ANSWER, AnswerType.NUMERIC_ANSWER, AnswerType.MULTIPLE_CHOICE)));

        SurveyDAO.getInstance().insertCoordinatorAnswers("1111", "56789",
                new LinkedList<>(Arrays.asList("open ans", "40", "1")),
                new LinkedList<>(Arrays.asList(AnswerType.OPEN_ANSWER, AnswerType.NUMERIC_ANSWER, AnswerType.MULTIPLE_CHOICE)));
        // create another survey
        surveyDTO = new SurveyDTO(true, "2222", "title", "description",
                Arrays.asList("symbol", "open?", "numeric?", "multiple choice?"),
                Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), Arrays.asList("correct", "wrong")),
                Arrays.asList(AnswerType.NUMERIC_ANSWER, AnswerType.OPEN_ANSWER, AnswerType.NUMERIC_ANSWER, AnswerType.MULTIPLE_CHOICE), 2022);

        SurveyDAO.getInstance().insertSurvey(surveyDTO);
        userController.logout("ronit");

        return new Response<>(true, false, "reset db");
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
