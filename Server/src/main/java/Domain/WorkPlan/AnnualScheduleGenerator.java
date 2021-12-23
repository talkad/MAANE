package Domain.WorkPlan;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.SurveyAnswers;
import Domain.DataManagement.SurveyController;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.WorkPlan;

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
                List<String> schoolIds = new Vector<>();//todo missing something?
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

    public void algorithm(String supervisor, int surveyId, String workField, List<Goal> goals) {
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
        sortedGoals.sort(Comparator.comparing(Goal::getWeight).reversed());//todo see if necessary
        //2
        Response<List<String>> instructorsRes = userController.getAppointedInstructors(supervisor); //get the supervisors instructors
        if (!instructorsRes.isFailure()) {
            Map<String, Map<String, List<Goal>>> instructorWithProblemsForSchools = new ConcurrentHashMap<>(); //instructor -> school symbol -> list of faults as goals for that school
            Map<String, List<Goal>> schoolsAndFaults; // a map of schools and their faults as goals
            List<String> instructors = instructorsRes.getResult();
            //4 - 7
            List<String> schoolsOfInstructor;
            List<String> schoolFaults;
            List<Goal> schoolFaultsGoals = new Vector<>();

            for (String instructor : instructors) { //2 - for every instructors under workField:
                schoolsOfInstructor = userController.getSchools(instructor).getResult();
                schoolsAndFaults = new ConcurrentHashMap<>();
                for (String school : schoolsOfInstructor) { //4 - schools of this instructor
                    schoolFaults = surveyController.detectSchoolFault(supervisor, surveyId, school).getResult();
                    for (String goalFault : schoolFaults) { // faults of the school
                        Response<Goal> goalRes = goalsManagement.getGoalByTitle(workField, goalFault);
                        if (!goalRes.isFailure()) {
                            schoolFaultsGoals.add(goalRes.getResult());
                        } else {
                            return; //todo error goal not existent
                        }
                    }
                    schoolsAndFaults.put(school, schoolFaultsGoals);
                }
                instructorWithProblemsForSchools.put(instructor, schoolsAndFaults);
            }
            //8

            for (String instructor : instructorWithProblemsForSchools.keySet()) {
                for (String school : instructorWithProblemsForSchools.get(instructor).keySet()) {
                    instructorWithProblemsForSchools.get(instructor).get(school).sort(Comparator.comparing(Goal::getWeight).reversed());
                }
            }
            for (String instructor : instructorWithProblemsForSchools.keySet()) {
                WorkPlan workPlan = new WorkPlan(2022);
                List<Pair<String, Goal>> goalsPriorityQueue = new Vector<>();
                for (String school : instructorWithProblemsForSchools.get(instructor).keySet()) {

                    if (instructorWithProblemsForSchools.get(instructor).get(school) != null && instructorWithProblemsForSchools.get(instructor).get(school).size() > 0) {
                        goalsPriorityQueue.add(new Pair<>(school, instructorWithProblemsForSchools.get(instructor).get(school).get(0)));
                        instructorWithProblemsForSchools.get(instructor).get(school).remove(0);
                    }

                }
                Pair<String, Goal> maxFirst;
                int maxFirstIndex = 0;
                Pair<String, Goal> maxSecond;
                int maxSecondIndex = 0;

                while (goalsPriorityQueue.size() > 0) {
                    if (goalsPriorityQueue.size() > 2) {
                        maxFirst = goalsPriorityQueue.get(0);
                        for (Pair<String, Goal> schoolGoal : goalsPriorityQueue) {
                            if (schoolGoal.getSecond().getWeight() > maxFirst.getSecond().getWeight()) {
                                maxFirst = schoolGoal;
                                maxFirstIndex = goalsPriorityQueue.indexOf(schoolGoal);
                            }
                        }
                        goalsPriorityQueue.remove(maxFirstIndex);

                        maxSecond = goalsPriorityQueue.get(0);
                        for (Pair<String, Goal> schoolGoal : goalsPriorityQueue) {
                            if (schoolGoal.getSecond().getWeight() > maxSecond.getSecond().getWeight()) {
                                maxSecond = schoolGoal;
                                maxSecondIndex = goalsPriorityQueue.indexOf(schoolGoal);
                            }
                        }
                        goalsPriorityQueue.remove(maxSecondIndex);
                        if (instructorWithProblemsForSchools.get(instructor).get(maxFirst.getFirst()) != null && instructorWithProblemsForSchools.get(instructor).get(maxFirst.getFirst()).size() > 0) {
                            goalsPriorityQueue.add(new Pair<>(maxFirst.getFirst(), instructorWithProblemsForSchools.get(instructor).get(maxFirst.getFirst()).get(0)));
                        }
                        if (instructorWithProblemsForSchools.get(instructor).get(maxSecond.getFirst()) != null && instructorWithProblemsForSchools.get(instructor).get(maxSecond.getFirst()).size() > 0) {
                            goalsPriorityQueue.add(new Pair<>(maxSecond.getFirst(), instructorWithProblemsForSchools.get(instructor).get(maxSecond.getFirst()).get(0)));
                        }
                        workPlan.insertActivityToFirstAvailableDate(maxFirst, maxSecond);
                    }
                    else if (goalsPriorityQueue.size() == 1) {
                        workPlan.insertActivityToFirstAvailableDate(goalsPriorityQueue.get(0));
                        goalsPriorityQueue.remove(0);
                        //todo make sure you stop when you fill WorkPlan
                        //todo when finishing work plan assign it to the instructor
                    }
                }
                userController.assignWorkPlan(instructor, workPlan);
            }
        }
    }

}