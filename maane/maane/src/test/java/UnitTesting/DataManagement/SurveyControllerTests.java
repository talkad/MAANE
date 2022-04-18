package UnitTesting.DataManagement;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.RuleDTO;
import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.Rules.Comparison;
import Domain.DataManagement.FaultDetector.Rules.RuleType;
import Domain.DataManagement.SurveyController;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Persistence.SurveyQueries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static Domain.DataManagement.AnswerState.AnswerType.MULTIPLE_CHOICE;
import static Domain.DataManagement.AnswerState.AnswerType.NUMERIC_ANSWER;
import static org.mockito.Mockito.when;

public class SurveyControllerTests {

    private SurveyDTO surveyDTO;
    private SurveyAnswersDTO answersDTO1, answersDTO2;

    @InjectMocks
    private SurveyController surveyController;

    @Mock
    private SurveyQueries surveyDAO;

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);

        surveyDTO = new SurveyDTO();
        surveyController.clearCache();
        UserController.getInstance().clearUsers();

        List<String> questions1 = Arrays.asList("que1", "que2", "que3");
        List<List<String>> answers1 = Arrays.asList(new LinkedList<>(), Arrays.asList("1", "2"), Arrays.asList("1", "2"));
        List<AnswerType> types1 = Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE);;

        surveyDTO.setId("0");
        surveyDTO.setTitle("title");
        surveyDTO.setDescription("description");
        surveyDTO.setQuestions(questions1);
        surveyDTO.setTypes(types1);
        surveyDTO.setAnswers(answers1);

        // legal answer
        answersDTO1 = new SurveyAnswersDTO();

        List<String> answers2 = Arrays.asList("30", "1", "2");
        List<AnswerType> types2 = Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE);
        answersDTO1.setId("0");
        answersDTO1.setAnswers(answers2);
        answersDTO1.setTypes(types2);

        // illegal answer
        answersDTO2 = new SurveyAnswersDTO();
        answersDTO2.setId("0");
        List<String> answers3 = Arrays.asList("30", "1");
        List<AnswerType> types3 = Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE);

        answersDTO2.setAnswers(answers3);
        answersDTO2.setTypes(types3);
    }

    @Test
    public void testCheck(){
        UserController.getInstance().clearUsers();
        when(surveyDAO.check()).thenReturn("hey");
        System.out.println(surveyController.check());
    }

    @Test
    public void surveyCreationSuccess(){
        UserController.getInstance().clearUsers();
//
//        UserController userController = UserController.getInstance();
//
//        String adminName = userController.login("admin").getResult();
//        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
//
//        userController.logout(adminName);
//        userController.login("Dvorit");
//
//        Assert.assertFalse(surveyController.createSurvey("Dvorit", surveyDTO).isFailure());
    }

    @Test
    public void addAnswerInCacheSuccess(){
        UserController userController = UserController.getInstance();

        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");

        userController.login("Dvorit");

        Response<String> res = surveyController.createSurvey("Dvorit", surveyDTO);
        answersDTO1.setId(res.getResult());
        Assert.assertFalse(surveyController.addAnswers(answersDTO1).isFailure());
    }

    @Test
    public void addAnswerNotInCacheSuccess(){
        UserController userController = UserController.getInstance();

        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");

        userController.logout(adminName);

        userController.login("Dvorit");

        Response<String> res = surveyController.createSurvey("Dvorit", surveyDTO);
        answersDTO1.setId(res.getResult());
        surveyController.clearCache();

        try {
            when(surveyDAO.getSurvey(res.getResult())).thenReturn(new Response<>(surveyDTO, false, "OK"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Assert.assertFalse(surveyController.addAnswers(answersDTO1).isFailure());
    }

    @Test
    public void addAnswerFailure(){
        Response<String> res = surveyController.createSurvey("Dvorit", surveyDTO);

        try {
            when(surveyDAO.getSurvey(answersDTO2.getId())).thenReturn(new Response<>(surveyDTO, false, "OK"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(surveyController.addAnswers(answersDTO2).isFailure());
    }

    @Test
    public void faultDetectionSuccess(){
        String year = "תשפ\"ג";
        Response<List<List<String>>> faults;

        UserController userController = UserController.getInstance();

        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");

        userController.logout(adminName);

        userController.login("Dvorit");

        Response<String> res = surveyController.createSurvey("Dvorit", surveyDTO);
        answersDTO1.setId(res.getResult());

        List<SurveyAnswersDTO> answersDTOS = Arrays.asList(answersDTO1, answersDTO1);

        List<Pair<RuleDTO, Integer>> ruleDTOS = Arrays.asList(
            new Pair<>(new RuleDTO(null, RuleType.NUMERIC, Comparison.GREATER_THEN, 0, 28), 0),
                new Pair<>(new RuleDTO(null, RuleType.MULTIPLE_CHOICE, Comparison.GREATER_THEN, 1, 1), 1),
                new Pair<>(new RuleDTO(null, RuleType.MULTIPLE_CHOICE, Comparison.GREATER_THEN, 2, 2), 2)

        );

        when(surveyDAO.getRules(res.getResult())).thenReturn(ruleDTOS);
        when(surveyDAO.getAnswers(res.getResult())).thenReturn(answersDTOS);

        UserController.getInstance().addGoal("Dvorit", new GoalDTO(0, "goal0", "goal0", 1,1), year);
        UserController.getInstance().addGoal("Dvorit", new GoalDTO(1, "goal1", "goal1", 1, 1), year);
        UserController.getInstance().addGoal("Dvorit", new GoalDTO(2, "goal2", "goal2", 1,1), year);

        faults = surveyController.detectFault("Dvorit", res.getResult(), year);

        Assert.assertTrue(faults.getResult().size() == 2 && faults.getResult().get(0).size() == 3);

    }
}
