package AcceptanceTesting.Tests;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.Rules.MultipleChoiceBaseRule;
import Domain.UsersManagment.UserStateEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static Domain.DataManagement.AnswerState.AnswerType.MULTIPLE_CHOICE;


public class SupervisorTests extends AcceptanceTests{

    private String supervisorName1;
    private String instructorName1;
    private String instructorName2;
    private SurveyDTO surveyDTO;
    private SurveyAnswersDTO answersDTO1, answersDTO2, answersDTO3, answersDTO4, answersDTO5, answersDTO6;

    @Before
    public void setUp() {
        boolean initialized = false;
        if(!initialized) {
            super.setUp(true);
            String adminName = "admin";
            supervisorName1 = "supervisor1";
            instructorName1 = "instructor1";
            instructorName2 = "instructor2";
            List<String> ins1Schools = new Vector<>();
            ins1Schools.add("1");
            ins1Schools.add("2");
            ins1Schools.add("3");
            List<String> ins2Schools = new Vector<>();
            ins2Schools.add("4");
            ins2Schools.add("5");
            ins2Schools.add("6");
            userBridge.login(adminName);
            userBridge.registerUserBySystemManager(adminName, new UserDTO(adminName, "science", supervisorName1, supervisorName1, UserStateEnum.SUPERVISOR,"Ronit", "Blisco", "ronit@gmail.com", "0501111111", "Tel Aviv", new Vector<>()), "");

            userBridge.registerUserBySystemManager(adminName, new UserDTO(adminName, "", instructorName1, instructorName1, UserStateEnum.INSTRUCTOR, "dan", "dani", "dan@gmail.com", "0501111111", "Tel Aviv", ins1Schools), supervisorName1);
            userBridge.registerUserBySystemManager(adminName, new UserDTO(adminName, "", instructorName2, instructorName2, UserStateEnum.INSTRUCTOR, "ben", "beni", "ben@gmail.com", "0501111111", "Tel Aviv", ins2Schools), supervisorName1);
            userBridge.login(supervisorName1);

            surveyDTO = new SurveyDTO();
            //surveyBridge.clearCache();

            List<String> questions = Arrays.asList("is there research performed in the school?", "does the school provide at least 4 private hours a week?", "is there maintenance every week?");
            List<List<String>> possibleAnswers = Arrays.asList(Arrays.asList("No", "Yes"), Arrays.asList("No", "Yes"), Arrays.asList("No", "Yes"));
            List<AnswerType> questionTypes = Arrays.asList(MULTIPLE_CHOICE, MULTIPLE_CHOICE, MULTIPLE_CHOICE);;

            surveyDTO.setId("");
            surveyDTO.setTitle("testing survey");
            surveyDTO.setDescription("testing survey");
            surveyDTO.setQuestions(questions);
            surveyDTO.setTypes(questionTypes);
            surveyDTO.setAnswers(possibleAnswers);

            answersDTO1 = new SurveyAnswersDTO();
            List<String> answers1 = Arrays.asList("0", "0", "0");
            answersDTO1.setAnswers(answers1);
            answersDTO1.setTypes(questionTypes);
            answersDTO1.setId("");


            answersDTO2 = new SurveyAnswersDTO();
            List<String> answers2 = Arrays.asList("1", "0", "1");
            answersDTO2.setAnswers(answers2);
            answersDTO2.setTypes(questionTypes);
            answersDTO2.setId("");


            answersDTO3 = new SurveyAnswersDTO();
            List<String> answers3 = Arrays.asList("1", "0", "1");
            answersDTO3.setAnswers(answers3);
            answersDTO3.setTypes(questionTypes);
            answersDTO3.setId("");


            answersDTO4 = new SurveyAnswersDTO();
            List<String> answers4 = Arrays.asList("0", "0", "1");
            answersDTO4.setAnswers(answers4);
            answersDTO4.setTypes(questionTypes);
            answersDTO4.setId("");


            answersDTO5 = new SurveyAnswersDTO();
            List<String> answers5 = Arrays.asList("1", "1", "1");
            answersDTO5.setAnswers(answers5);
            answersDTO5.setTypes(questionTypes);
            answersDTO5.setId("");


            answersDTO6 = new SurveyAnswersDTO();
            List<String> answers6 = Arrays.asList("1", "0", "1");
            answersDTO6.setAnswers(answers3);
            answersDTO6.setTypes(questionTypes);
            answersDTO6.setId("");
        }
    }

    @Test
    public void testSetUp() {
        Assert.assertFalse(userBridge.getUserRes(supervisorName1).isFailure());
        Assert.assertFalse(userBridge.getUserRes(instructorName1).isFailure());
        Assert.assertFalse(userBridge.getUserRes(instructorName2).isFailure());
        List<UserDTO> appointedUsers = userBridge.getAppointedUsers(supervisorName1).getResult();
        for (UserDTO u: appointedUsers) {
            Assert.assertTrue(u.getFirstName().equals("dan") || u.getFirstName().equals("ben"));
        }
    }

    @Test
    public void workPlanTest(){
        List<GoalDTO> goalDTOList = new Vector<>();
        String year = "תשפ\"ג";
        userBridge.addGoal(supervisorName1, new GoalDTO(0, "research", "", 1, 3), year);
        userBridge.addGoal(supervisorName1, new GoalDTO(1, "private hours", "", 1, 2), year);
        userBridge.addGoal(supervisorName1, new GoalDTO(2, "maintenance", "", 1, 4), year);

        Response<String> res = surveyBridge.createSurvey(supervisorName1, surveyDTO);
        answersDTO1.setId(res.getResult());
        answersDTO2.setId(res.getResult());
        answersDTO3.setId(res.getResult());
        answersDTO4.setId(res.getResult());
        answersDTO5.setId(res.getResult());
        answersDTO6.setId(res.getResult());

        surveyBridge.addAnswers(answersDTO1);
        surveyBridge.addAnswers(answersDTO2);
        surveyBridge.addAnswers(answersDTO3);
        surveyBridge.addAnswers(answersDTO4);
        surveyBridge.addAnswers(answersDTO5);
        surveyBridge.addAnswers(answersDTO6);

        surveyBridge.addRule(supervisorName1, res.getResult(), new MultipleChoiceBaseRule(0, 0), 0);
        surveyBridge.addRule(supervisorName1, res.getResult(), new MultipleChoiceBaseRule(1, 0), 1);
        surveyBridge.addRule(supervisorName1, res.getResult(), new MultipleChoiceBaseRule(2, 0), 2);

        userBridge.login(instructorName1);
        userBridge.login(instructorName2);
        userBridge.assignSchoolsToUser(supervisorName1, instructorName1, Arrays.asList("1", "2", "3"));
        userBridge.assignSchoolsToUser(supervisorName1, instructorName2, Arrays.asList("4", "5", "6"));

        scheduleBridge.generateSchedule(supervisorName1, res.getResult(), year);

        userBridge.getUserRes(instructorName1).getResult().getWorkPlan(year).getResult().printMe();
        userBridge.getUserRes(instructorName2).getResult().getWorkPlan(year).getResult().printMe();

    }
}
