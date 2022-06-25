package IntegrationTesting;

import Communication.DTOs.ActivityDTO;
import Communication.DTOs.GoalDTO;
import Communication.DTOs.SurveyDTO;
import Communication.DTOs.WorkPlanDTO;
import Communication.Initializer.ServerContextInitializer;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.SurveyController;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.AnnualScheduleGenerator;
import Domain.WorkPlan.GoalsManagement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import static Domain.DataManagement.AnswerState.AnswerType.*;

public class UserWorkPlanIntegrationTests {
    private UserController userController;
    private SurveyController surveyController;
    private String supervisorName = "sup1";

    @Before
    public void setup(){
        ServerContextInitializer.getInstance().setMockMode();
        //ServerContextInitializer.getInstance().setTestMode();

        userController = UserController.getInstance();
        userController.resetDB();
        surveyController = SurveyController.getInstance();

        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, supervisorName, "1234abcd", UserStateEnum.SUPERVISOR, "", "tech", "", "", "a@a.com", "0555555555", "");
        userController.logout(adminName);
    }

    @Test
    public void generateWorkPlanSuccess(){
        Integer year = 2022;
        userController.login(supervisorName);

        userController.addGoal(supervisorName, new GoalDTO(1,"1", "desc", 1,5), year);
        userController.addGoal(supervisorName, new GoalDTO(2, "3", "desc", 1, 3), year);
        userController.addGoal(supervisorName, new GoalDTO(3, "2", "desc", 1,4), year);
        userController.addGoal(supervisorName, new GoalDTO(4, "4","desc", 1, 1), year);

        String instructorName = userController.registerUser(supervisorName, "ins1", "1234abcd", UserStateEnum.INSTRUCTOR, "", "", "a@a.com", "0555555555", "").getResult();

        userController.assignSchoolToUser(supervisorName, instructorName, "1");
        userController.assignSchoolToUser(supervisorName, instructorName, "2");

        List<Pair<String, List<Integer>>> schoolsAndFaults = new Vector<>();
        List<Integer> school1Faults = new Vector<>();
        school1Faults.add(1);
        school1Faults.add(3);
        school1Faults.add(4);
        school1Faults.add(2);

        List<Integer> school2Faults = new Vector<>();
        school2Faults.add(4);
        school2Faults.add(2);

        schoolsAndFaults.add(new Pair<>("1", school1Faults));
        schoolsAndFaults.add(new Pair<>("2", school2Faults));
        String workField = userController.getUser(supervisorName).getWorkField();
        AnnualScheduleGenerator.getInstance().algorithmMock(supervisorName, schoolsAndFaults, GoalsManagement.getInstance().getGoals(workField, year).getResult(), year);
        userController.login("ins1");
        Response<WorkPlanDTO> insWorkPlanSeptemberRes = userController.viewWorkPlan(instructorName, year, 9);

        List<Pair<LocalDateTime, ActivityDTO>> insCalendar = insWorkPlanSeptemberRes.getResult().getCalendar();

        Assert.assertEquals(insCalendar.get(0).getFirst(), LocalDateTime.of(2022, 9, 4, 8, 0));
        Assert.assertEquals(1, (int) insCalendar.get(0).getSecond().getGoalId());

        Assert.assertEquals(insCalendar.get(1).getFirst(), LocalDateTime.of(2022, 9, 4, 10, 0));
        Assert.assertEquals(2, (int) insCalendar.get(1).getSecond().getGoalId());

        Assert.assertEquals(insCalendar.get(2).getFirst(), LocalDateTime.of(2022, 9, 11, 8, 0));
        Assert.assertEquals(3, (int) insCalendar.get(2).getSecond().getGoalId());

        Assert.assertEquals(insCalendar.get(3).getFirst(), LocalDateTime.of(2022, 9, 11, 10, 0));
        Assert.assertEquals(4, (int) insCalendar.get(3).getSecond().getGoalId());

        Assert.assertEquals(insCalendar.get(4).getFirst(), LocalDateTime.of(2022, 9, 18, 8, 0));
        Assert.assertEquals(2, (int) insCalendar.get(4).getSecond().getGoalId());

        Assert.assertEquals(insCalendar.get(5).getFirst(), LocalDateTime.of(2022, 9, 18, 10, 0));
        Assert.assertEquals(4, (int) insCalendar.get(5).getSecond().getGoalId());
    }

    @Test
    public void generateWorkPlanFailureNoSurvey(){
        Integer year = 2022;
        userController.login(supervisorName);

        userController.addGoal(supervisorName, new GoalDTO(1,"1", "desc", 1,5), year);
        userController.addGoal(supervisorName, new GoalDTO(2, "3", "desc", 1, 3), year);
        userController.addGoal(supervisorName, new GoalDTO(3, "2", "desc", 1,4), year);
        userController.addGoal(supervisorName, new GoalDTO(4, "4","desc", 1, 1), year);

        String instructorName = userController.registerUser(supervisorName, "ins1", "1234abcd", UserStateEnum.INSTRUCTOR, "", "", "a@a.com", "0555555555", "").getResult();

        userController.assignSchoolToUser(supervisorName, instructorName, "1");
        userController.assignSchoolToUser(supervisorName, instructorName, "2");

        Response<Boolean> scheduleRes = AnnualScheduleGenerator.getInstance().generateSchedule(supervisorName, "1");
        Assert.assertTrue(scheduleRes.isFailure());
    }


    @Test
    public void generateWorkPlanFailureNoSubmission(){
        Integer year = 2022;
        userController.login(supervisorName);
        SurveyDTO surveyDTO = new SurveyDTO();
        surveyDTO.setId("");
        surveyDTO.setTitle("testing survey");
        surveyDTO.setDescription("testing survey");

        List<String> questions = Arrays.asList("symbol" ,"is there research performed in the school?", "does the school provide at least 4 private hours a week?", "is there maintenance every week?", "number of students in class");
        List<List<String>> possibleAnswers = Arrays.asList(new LinkedList<>(), Arrays.asList("No", "Yes"), Arrays.asList("No", "Yes"), Arrays.asList("No", "Yes"), new LinkedList<>());
        List<AnswerType> questionTypes = Arrays.asList(OPEN_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE, MULTIPLE_CHOICE, NUMERIC_ANSWER);
        surveyDTO.setQuestions(questions);
        surveyDTO.setTypes(questionTypes);
        surveyDTO.setAnswers(possibleAnswers);

        Response<String> surveyIdRes = surveyController.createSurvey(supervisorName, surveyDTO);
        String surveyId = surveyIdRes.getResult();

        userController.addGoal(supervisorName, new GoalDTO(1,"1", "desc", 1,5), year);
        userController.addGoal(supervisorName, new GoalDTO(2, "3", "desc", 1, 3), year);
        userController.addGoal(supervisorName, new GoalDTO(3, "2", "desc", 1,4), year);
        userController.addGoal(supervisorName, new GoalDTO(4, "4","desc", 1, 1), year);

        String instructorName = userController.registerUser(supervisorName, "ins1", "1234abcd", UserStateEnum.INSTRUCTOR, "", "", "a@a.com", "0555555555", "").getResult();

        userController.assignSchoolToUser(supervisorName, instructorName, "1");
        userController.assignSchoolToUser(supervisorName, instructorName, "2");

        Response<Boolean> scheduleRes = AnnualScheduleGenerator.getInstance().generateSchedule(supervisorName, surveyId);
        Assert.assertTrue(scheduleRes.isFailure());
    }
}
