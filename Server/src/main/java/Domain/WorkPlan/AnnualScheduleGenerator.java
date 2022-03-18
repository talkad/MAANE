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

    public Response<Boolean> generateSchedule(String supervisor, int surveyId, String year){
        Response<List<SurveyAnswers>> surveyRes = surveyController.getAnswersForSurvey(surveyId);
        String workField;
        if(!surveyRes.isFailure()){
            Response<String> workFieldRes = userController.generateSchedule(supervisor);
            if(!workFieldRes.isFailure()){
                workField = workFieldRes.getResult();
                Response<List<Goal>> goalsRes = goalsManagement.getGoals(workField, year);
                if(!goalsRes.isFailure()){
                    algorithm(supervisor, surveyId, workField, goalsRes.getResult(), year);//todo
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
        return new Response<>(true, false, "successfully generated work plans");
    }

    public int indexOfMaxGoal(List<Pair<String, Goal>> goalsPriorityQueue){
        Pair<String, Goal> maxGoal = goalsPriorityQueue.get(0);
        int maxGoalIndex = 0;
        for (Pair<String, Goal> schoolGoal : goalsPriorityQueue) {
            if (schoolGoal.getSecond().getWeight() > maxGoal.getSecond().getWeight()) {
                maxGoal = schoolGoal;
                maxGoalIndex = goalsPriorityQueue.indexOf(schoolGoal);
            }
        }
        return maxGoalIndex;
    }

    public void algorithm(String supervisor, int surveyId, String workField, List<Goal> goals, String year) {
        //1 - sort Goals by their weight (goal is per workfield)
        //2 - for every instructors under workField:
        //3 - workDay = the work day of the current instructor
        //4 - listOfSchools = schools of this instructor
        //5 - init listOfSchoolsWithFault
        //6 - for each school in listOfSchools:
        //7 - if (current school has fault):
        //        listOfSchoolsWithFault.add(school)
        //8 - while (listOfSchoolsWithFault is not empty): //todo fix 8
        //          schoolToSchedule = get school with highest goal weight
        //          schedule(school, instr, goal, workDay)
        //          listOfSchoolsWithFault.delete(school)

        //1
        //List<Goal> sortedGoals = new Vector<>(goals);//todo see if necessary probably not
        //sortedGoals.sort(Comparator.comparing(Goal::getWeight).reversed());//todo see if necessary probably not
        //2
        Response<List<String>> instructorsRes = userController.getAppointedInstructors(supervisor); //get the supervisors instructors
        if (!instructorsRes.isFailure()) {

            Map<String, Map<String, List<Goal>>> instructorWithProblemsForSchools = new ConcurrentHashMap<>(); //instructor -> school symbol -> list of faults as goals for that school
            Map<String, List<Goal>> schoolsAndFaults; // a map of schools and their faults as goals

            List<String> instructors = instructorsRes.getResult();

            //4 - 7
            List<String> schoolsOfInstructor;
            List<Integer> schoolFaults;
            List<Goal> schoolFaultsGoals;

            for (String instructor : instructors) { //2 - for every instructors under workField:

                schoolsOfInstructor = userController.getSchools(instructor).getResult();
                schoolsAndFaults = new ConcurrentHashMap<>();
                for (String school : schoolsOfInstructor) { //4 - schools of this instructor

                    schoolFaultsGoals = new Vector<>();//todo verify it doesnt get deleted from the map

                    Response<List<Integer>> schoolFaultsRes = surveyController.detectSchoolFault(supervisor, surveyId, school, year);

                    if(schoolFaultsRes.isFailure()) {
                        return; //todo some error
                    }
                    schoolFaults = schoolFaultsRes.getResult();
                    if(schoolFaults != null && schoolFaults.size() > 0) {
                        Response<List<Goal>> goalsRes = goalsManagement.getGoalsById(workField, schoolFaults, year);
                        if (!goalsRes.isFailure()) {
                            schoolFaultsGoals.addAll(goalsRes.getResult());
                        } else {
                            return; //todo error goal not existent
                        }
                        schoolsAndFaults.put(school, schoolFaultsGoals);
                    }
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
                        goalsPriorityQueue.add(new Pair<>(school, instructorWithProblemsForSchools.get(instructor).get(school).remove(0)));
                    }
                }
                Pair<String, Goal> maxFirst;
                Pair<String, Goal> maxSecond;

                while (goalsPriorityQueue.size() > 0) {
                    if (goalsPriorityQueue.size() >= 2) {
                        maxFirst = goalsPriorityQueue.remove(indexOfMaxGoal(goalsPriorityQueue));
                        maxSecond = goalsPriorityQueue.remove(indexOfMaxGoal(goalsPriorityQueue));

                        if (instructorWithProblemsForSchools.get(instructor).get(maxFirst.getFirst()).size() > 0) {
                            goalsPriorityQueue.add(new Pair<>(maxFirst.getFirst(), instructorWithProblemsForSchools.get(instructor).get(maxFirst.getFirst()).remove(0)));
                            //System.out.println(instructorWithProblemsForSchools.get(instructor).get(maxFirst.getFirst()).get(0));
                        }
                        if (instructorWithProblemsForSchools.get(instructor).get(maxSecond.getFirst()) != null && instructorWithProblemsForSchools.get(instructor).get(maxSecond.getFirst()).size() > 0) {
                            goalsPriorityQueue.add(new Pair<>(maxSecond.getFirst(), instructorWithProblemsForSchools.get(instructor).get(maxSecond.getFirst()).remove(0)));
                            //System.out.println(instructorWithProblemsForSchools.get(instructor).get(maxSecond.getFirst()).get(0));
                        }
                        workPlan.insertActivityToFirstAvailableDate(maxFirst, maxSecond);
                    }
                    else {
                        String lastSchool = goalsPriorityQueue.get(0).getFirst();
                        if(instructorWithProblemsForSchools.get(instructor).get(lastSchool).size() > 0) {

                            goalsPriorityQueue.add(new Pair<>(lastSchool, instructorWithProblemsForSchools.get(instructor).get(lastSchool).remove(0)));
                            workPlan.insertActivityToFirstAvailableDate(goalsPriorityQueue.get(0), goalsPriorityQueue.get(1));
                            goalsPriorityQueue.remove(0);
                            goalsPriorityQueue.remove(0);
                        }
                        while(instructorWithProblemsForSchools.get(instructor).get(lastSchool).size() >= 2) {
                            goalsPriorityQueue.add(new Pair<>(lastSchool, instructorWithProblemsForSchools.get(instructor).get(lastSchool).remove(0)));
                            goalsPriorityQueue.add(new Pair<>(lastSchool, instructorWithProblemsForSchools.get(instructor).get(lastSchool).remove(0)));
                            workPlan.insertActivityToFirstAvailableDate(goalsPriorityQueue.get(0), goalsPriorityQueue.get(1));
                            goalsPriorityQueue.remove(0);
                            goalsPriorityQueue.remove(0);
                        }
                        if (instructorWithProblemsForSchools.get(instructor).get(lastSchool).size() > 0) {
                            goalsPriorityQueue.add(new Pair<>(lastSchool, instructorWithProblemsForSchools.get(instructor).get(lastSchool).remove(0)));
                            workPlan.insertActivityToFirstAvailableDate(goalsPriorityQueue.get(0));
                            goalsPriorityQueue.remove(0);
                        }
                        if (goalsPriorityQueue.size() > 0) {
                            workPlan.insertActivityToFirstAvailableDate(goalsPriorityQueue.remove(0));
                        }
                        //todo make sure you stop when you fill WorkPlan
                    }
                }
                userController.assignWorkPlan(instructor, workPlan, year);
            }
        }
    }

    public void algorithmMock(String supervisor, List<Pair<String, List<Integer>>> schoolFaultsMock, String workField, List<Goal> goals, String year) {
        //1 - sort Goals by their weight (goal is per workfield)
        //2 - for every instructors under workField:
        //3 - workDay = the work day of the current instructor
        //4 - listOfSchools = schools of this instructor
        //5 - init listOfSchoolsWithFault
        //6 - for each school in listOfSchools:
        //7 - if (current school has fault):
        //        listOfSchoolsWithFault.add(school)
        //8 - while (listOfSchoolsWithFault is not empty): //todo fix 8
        //          schoolToSchedule = get school with highest goal weight
        //          schedule(school, instr, goal, workDay)
        //          listOfSchoolsWithFault.delete(school)

        //1
        //2
        Response<List<String>> instructorsRes = userController.getAppointedInstructors(supervisor); //get the supervisors instructors
        if (!instructorsRes.isFailure()) {
            Map<String, Map<String, List<Goal>>> instructorWithProblemsForSchools = new ConcurrentHashMap<>(); //instructor -> school symbol -> list of faults as goals for that school
            Map<String, List<Goal>> schoolsAndFaults; // a map of schools and their faults as goals
            List<String> instructors = instructorsRes.getResult();
            //4 - 7
            List<String> schoolsOfInstructor;
            List<Integer> schoolFaults;
            List<Goal> schoolFaultsGoals;

            for (String instructor : instructors) { //2 - for every instructors under workField:
                schoolsOfInstructor = userController.getSchools(instructor).getResult();
                schoolsAndFaults = new ConcurrentHashMap<>();
                for (String school : schoolsOfInstructor) { //4 - schools of this instructor
                    schoolFaultsGoals = new Vector<>();

                    Response<List<Integer>> schoolFaultsRes = surveyController.detectSchoolFaultsMock(schoolFaultsMock, school);
                    if(schoolFaultsRes.isFailure())
                        return; //todo some error
                    schoolFaults = schoolFaultsRes.getResult();
                    if(schoolFaults != null && schoolFaults.size() > 0) {
                        Response<List<Goal>> goalsRes = goalsManagement.getGoalsById(workField, schoolFaults, year);
                        if (!goalsRes.isFailure()) {
                            schoolFaultsGoals.addAll(goalsRes.getResult());
                        } else {
                            return; //todo error goal not existent
                        }
                        schoolsAndFaults.put(school, schoolFaultsGoals);
                    }
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
                Pair<String, Goal> maxSecond;
                while (goalsPriorityQueue.size() > 0) {
                    if (goalsPriorityQueue.size() >= 2) {
                        maxFirst = goalsPriorityQueue.remove(indexOfMaxGoal(goalsPriorityQueue));
                        maxSecond = goalsPriorityQueue.remove(indexOfMaxGoal(goalsPriorityQueue));

                        if (instructorWithProblemsForSchools.get(instructor).get(maxFirst.getFirst()).size() > 0) {
                            goalsPriorityQueue.add(new Pair<>(maxFirst.getFirst(), instructorWithProblemsForSchools.get(instructor).get(maxFirst.getFirst()).remove(0)));
                            //System.out.println(instructorWithProblemsForSchools.get(instructor).get(maxFirst.getFirst()).get(0));
                        }
                        if (instructorWithProblemsForSchools.get(instructor).get(maxSecond.getFirst()) != null && instructorWithProblemsForSchools.get(instructor).get(maxSecond.getFirst()).size() > 0) {
                            goalsPriorityQueue.add(new Pair<>(maxSecond.getFirst(), instructorWithProblemsForSchools.get(instructor).get(maxSecond.getFirst()).remove(0)));
                            //System.out.println(instructorWithProblemsForSchools.get(instructor).get(maxSecond.getFirst()).get(0));
                        }
                        workPlan.insertActivityToFirstAvailableDate(maxFirst, maxSecond);
                    }
                    else {
                        String lastSchool = goalsPriorityQueue.get(0).getFirst();
                        if(instructorWithProblemsForSchools.get(instructor).get(lastSchool).size() > 0) {

                            goalsPriorityQueue.add(new Pair<>(lastSchool, instructorWithProblemsForSchools.get(instructor).get(lastSchool).remove(0)));
                            workPlan.insertActivityToFirstAvailableDate(goalsPriorityQueue.get(0), goalsPriorityQueue.get(1));
                            goalsPriorityQueue.remove(0);
                            goalsPriorityQueue.remove(0);
                        }
                        while(instructorWithProblemsForSchools.get(instructor).get(lastSchool).size() >= 2) {
                            goalsPriorityQueue.add(new Pair<>(lastSchool, instructorWithProblemsForSchools.get(instructor).get(lastSchool).remove(0)));
                            goalsPriorityQueue.add(new Pair<>(lastSchool, instructorWithProblemsForSchools.get(instructor).get(lastSchool).remove(0)));
                            workPlan.insertActivityToFirstAvailableDate(goalsPriorityQueue.get(0), goalsPriorityQueue.get(1));
                            goalsPriorityQueue.remove(0);
                            goalsPriorityQueue.remove(0);
                        }
                        if (instructorWithProblemsForSchools.get(instructor).get(lastSchool).size() > 0) {
                            goalsPriorityQueue.add(new Pair<>(lastSchool, instructorWithProblemsForSchools.get(instructor).get(lastSchool).remove(0)));
                            workPlan.insertActivityToFirstAvailableDate(goalsPriorityQueue.get(0));
                            goalsPriorityQueue.remove(0);
                        }
                        if (goalsPriorityQueue.size() > 0) {
                            workPlan.insertActivityToFirstAvailableDate(goalsPriorityQueue.remove(0));
                        }
                        //todo make sure you stop when you fill WorkPlan
                    }
                }
                userController.assignWorkPlan(instructor, workPlan, year);
            }
        }
    }

}
