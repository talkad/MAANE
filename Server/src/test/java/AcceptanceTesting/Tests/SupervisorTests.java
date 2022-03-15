package AcceptanceTesting.Tests;

import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Communication.DTOs.UserDTO;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.Rules.Comparison;
import Domain.DataManagement.FaultDetector.Rules.MultipleChoiceBaseRule;
import Domain.DataManagement.FaultDetector.Rules.NumericBaseRule;
import Domain.DataManagement.SurveyController;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.Goal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import static Domain.DataManagement.AnswerState.AnswerType.MULTIPLE_CHOICE;
import static Domain.DataManagement.AnswerState.AnswerType.NUMERIC_ANSWER;


public class SupervisorTests extends AcceptanceTests{

    private static boolean initialized = false;
    private String adminName;
    private String supervisorName1;
    private String instructorName1;
    private String instructorName2;
    private SurveyDTO surveyDTO;
    private SurveyAnswersDTO answersDTO1, answersDTO2, answersDTO3, answersDTO4, answersDTO5, answersDTO6;

    @Before
    public void setUp() /*throws InterruptedException*/ {
        if(!initialized) {
            super.setUp(true);
            String guestName = userBridge.addGuest().getResult();
            adminName = "admin";
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
            userBridge.login(guestName, adminName, adminName);
            userBridge.registerUserBySystemManager(new UserDTO(adminName, "science", supervisorName1, supervisorName1, UserStateEnum.SUPERVISOR,"Ronit", "Blisco", "ronit@gmail.com", "0501111111", "Tel Aviv", new Vector<>()), "");

            userBridge.registerUserBySystemManager(new UserDTO(adminName, "", instructorName1, instructorName1, UserStateEnum.INSTRUCTOR, "dan", "dani", "dan@gmail.com", "0501111111", "Tel Aviv", ins1Schools), supervisorName1);
            userBridge.registerUserBySystemManager(new UserDTO(adminName, "", instructorName2, instructorName2, UserStateEnum.INSTRUCTOR, "ben", "beni", "ben@gmail.com", "0501111111", "Tel Aviv", ins2Schools), supervisorName1);
            guestName = userBridge.addGuest().getResult();
            userBridge.login(guestName, supervisorName1, supervisorName1);

            surveyDTO = new SurveyDTO();
            //surveyBridge.clearCache();

            List<String> questions = Arrays.asList("is there research performed in the school?", "does the school provide at least 4 private hours a week?", "is there maintenance every week?");
            List<List<String>> possibleAnswers = Arrays.asList(Arrays.asList("No", "Yes"), Arrays.asList("No", "Yes"), Arrays.asList("No", "Yes"));
            List<AnswerType> questionTypes = Arrays.asList(MULTIPLE_CHOICE, MULTIPLE_CHOICE, MULTIPLE_CHOICE);;

            surveyDTO.setId(-1);
            surveyDTO.setTitle("testing survey");
            surveyDTO.setDescription("testing survey");
            surveyDTO.setQuestions(questions);
            surveyDTO.setTypes(questionTypes);
            surveyDTO.setAnswers(possibleAnswers);

            answersDTO1 = new SurveyAnswersDTO();
            List<String> answers1 = Arrays.asList("0", "0", "0");
            answersDTO1.setAnswers(answers1);
            answersDTO1.setTypes(questionTypes);
            answersDTO1.setSymbol("1");
            answersDTO1.setId(0);


            answersDTO2 = new SurveyAnswersDTO();
            List<String> answers2 = Arrays.asList("1", "0", "1");
            answersDTO2.setAnswers(answers2);
            answersDTO2.setTypes(questionTypes);
            answersDTO2.setSymbol("2");
            answersDTO2.setId(0);


            answersDTO3 = new SurveyAnswersDTO();
            List<String> answers3 = Arrays.asList("1", "0", "1");
            answersDTO3.setAnswers(answers3);
            answersDTO3.setTypes(questionTypes);
            answersDTO3.setSymbol("3");
            answersDTO3.setId(0);


            answersDTO4 = new SurveyAnswersDTO();
            List<String> answers4 = Arrays.asList("0", "0", "1");
            answersDTO4.setAnswers(answers4);
            answersDTO4.setTypes(questionTypes);
            answersDTO4.setSymbol("4");
            answersDTO4.setId(0);


            answersDTO5 = new SurveyAnswersDTO();
            List<String> answers5 = Arrays.asList("1", "1", "1");
            answersDTO5.setAnswers(answers5);
            answersDTO5.setTypes(questionTypes);
            answersDTO5.setSymbol("5");
            answersDTO5.setId(0);


            answersDTO6 = new SurveyAnswersDTO();
            List<String> answers6 = Arrays.asList("1", "0", "1");
            answersDTO6.setAnswers(answers3);
            answersDTO6.setTypes(questionTypes);
            answersDTO6.setSymbol("6");
            answersDTO6.setId(0);
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
        List<Goal> goalList = new Vector<>();
        String year = "תשפ\"ג";
        goalList.add(new Goal(0, "research", "", 1, 3));
        goalList.add(new Goal(1, "private hours", "", 1, 2));
        goalList.add(new Goal(2, "maintenance", "", 1, 4));
        userBridge.addGoals(supervisorName1, goalList, year);

        surveyBridge.createSurvey(supervisorName1, surveyDTO);
        surveyBridge.addAnswers(answersDTO1);
        surveyBridge.addAnswers(answersDTO2);
        surveyBridge.addAnswers(answersDTO3);
        surveyBridge.addAnswers(answersDTO4);
        surveyBridge.addAnswers(answersDTO5);
        surveyBridge.addAnswers(answersDTO6);

        surveyBridge.addRule(supervisorName1, 0, new MultipleChoiceBaseRule(0, 0), 0);
        surveyBridge.addRule(supervisorName1, 0, new MultipleChoiceBaseRule(1, 0), 1);
        surveyBridge.addRule(supervisorName1, 0, new MultipleChoiceBaseRule(2, 0), 2);


        String guestName = userBridge.addGuest().getResult();
        userBridge.login(guestName, instructorName1, instructorName1);

        guestName = userBridge.addGuest().getResult();
        userBridge.login(guestName, instructorName2, instructorName2);
        userBridge.assignSchoolsToUser(supervisorName1, instructorName1, Arrays.asList("1", "2", "3"));
        userBridge.assignSchoolsToUser(supervisorName1, instructorName2, Arrays.asList("4", "5", "6"));

        scheduleBridge.generateSchedule(supervisorName1, 0, year);

        userBridge.getUserRes(instructorName1).getResult().getWorkPlan().getResult().printMe();
        userBridge.getUserRes(instructorName2).getResult().getWorkPlan().getResult().printMe();

    }
}
