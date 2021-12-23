package Domain.WorkPlan;

import Domain.CommonClasses.Response;
import Domain.DataManagement.SurveyAnswers;
import Domain.DataManagement.SurveyController;
import Domain.UsersManagment.UserController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnnualScheduleGenerator {
    private GoalsManagement goalsManagement;
    private UserController userController;
    private SurveyController surveyController;


    private AnnualScheduleGenerator() {
        this.goalsManagement = GoalsManagement.getInstance();
        this.userController = UserController.getInstance();
        this.surveyController = SurveyController.getInstance();
    }

    private static class CreateSafeThreadSingleton {
        private static final AnnualScheduleGenerator INSTANCE = new AnnualScheduleGenerator();
    }

    public static AnnualScheduleGenerator getInstance() {
        return AnnualScheduleGenerator.CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<Boolean> generateSchedule(String supervisor, int surveyId){
        Response<List<SurveyAnswers>> surveyRes = surveyController.getAnswersForSurvey(surveyId);
        String workField;
        if(!surveyRes.isFailure()){
            Response<String> workFieldRes = userController.generateSchedule(supervisor);
            if(!workFieldRes.isFailure()){
                List<String> schoolIds = new Vector<>();
                for (SurveyAnswers surveyAnswers: surveyRes.getResult()) {
                    schoolIds.add(surveyAnswers.getSymbol());
                }
                workField = workFieldRes.getResult();
                Response<List<Goal>> goalsRes = goalsManagement.getGoals(workField);
                if(!goalsRes.isFailure()){
                    algorithm(supervisor, surveyId, workField, goalsRes.getResult());
                }
                else{
                    return new Response<>(false, true, goalsRes.getErrMsg());
                }
            }
            else{
                return new Response<>(false, true, workFieldRes.getErrMsg());
            }
        }
        else{
            return new Response<>(false, true, surveyRes.getErrMsg());
        }
        return null; //todo
    }

    public void algorithm(String supervisor, int surveyId, String workField, List<Goal> goals){
        //1 - sort Goals by their weight (goal is per workfield)
        //2 - for every instructors under workField:
        //3 - workDay = the work day of the current instructor
        //4 - listOfSchools = schools of this instructor
        //5 - init listOfSchoolsWithFault
        //6 - for each school in listOfSchools:
        //7 - if (current school has fault):
        //        listOfSchoolsWWithFault.add(school)
        //8 - while (listOfSchoolsWithFault is not empty): //todo fix 8
        //          schoolToSchedule = get school with highest goal weight
        //          schedule(school, instr, goal, workDay)
        //          listOfSchoolsWithFault.delete(school)

        //1
        List<Goal> sortedGoals = new Vector<>(goals);//todo see if necessary
        sortedGoals.sort(Comparator.comparing(Goal::getWeight).reversed());
        //2
        Response<List<String>> instructorsRes = userController.getAppointedInstructors(supervisor);
        if(!instructorsRes.isFailure()){
            Map<String, Map<String, List<String>>> instructorWithProblemsForSchools = new ConcurrentHashMap<>(); //instructor -> school symbol -> list of faults for that school
            Map<String, List<String>> schoolsAndFaults;
            List<String> instructors = instructorsRes.getResult();
            //4 - 7
            List<String> schoolsOfInstructor;
            List<String> schoolFaults;
            for (String instructor: instructors) {
                schoolsOfInstructor = userController.getSchools(instructor).getResult();
                schoolsAndFaults = new ConcurrentHashMap<>();
                for (String school: schoolsOfInstructor) {
                    schoolFaults = surveyController.getFaults(surveyId, school).getResult();
                    schoolsAndFaults.put(school, schoolFaults);
                }
                instructorWithProblemsForSchools.put(instructor, schoolsAndFaults);
            }
            //8
            for (String instructor: instructorWithProblemsForSchools.keySet()) {
                //ye
            }
        }


    }
}
