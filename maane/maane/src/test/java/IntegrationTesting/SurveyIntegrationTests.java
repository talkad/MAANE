package IntegrationTesting;

import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.Rules.Comparison;
import Domain.DataManagement.FaultDetector.Rules.NumericBaseRule;
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static Domain.DataManagement.AnswerState.AnswerType.MULTIPLE_CHOICE;
import static Domain.DataManagement.AnswerState.AnswerType.NUMERIC_ANSWER;
import static org.mockito.Mockito.when;

public class SurveyIntegrationTests {

    private SurveyDTO surveyDTO;
    private SurveyAnswersDTO answersDTO;

    @InjectMocks
    private SurveyController surveyController;

    @Mock
    private SurveyQueries surveyDAO;

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);

        surveyDTO = new SurveyDTO();
        surveyController.clearCache();

        List<String> questions1 = Arrays.asList("que1", "que2", "que3");
        List<List<String>> answers1 = Arrays.asList(new LinkedList<>(), Arrays.asList("1", "2"), Arrays.asList("1", "2"));
        List<AnswerType> types1 = Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE);

        surveyDTO.setId("");
        surveyDTO.setTitle("title");
        surveyDTO.setDescription("description");
        surveyDTO.setQuestions(questions1);
        surveyDTO.setTypes(types1);
        surveyDTO.setAnswers(answers1);

        answersDTO = new SurveyAnswersDTO();

        List<String> answers2 = Arrays.asList("30", "1", "2");
        List<AnswerType> types2 = Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE);

        answersDTO.setId("");
        answersDTO.setSymbol("A123");
        answersDTO.setAnswers(answers2);
        answersDTO.setTypes(types2);

    }

    @Test
    public void surveyCreationSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login( "admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.login("Dvorit");

        Assert.assertFalse(surveyController.createSurvey("Dvorit", surveyDTO).isFailure());
    }

    @Test
    public void surveyCreationPermissionFailure(){
        UserController userController = UserController.getInstance();
        userController.login("admin");

        userController.registerUserBySystemManager("admin", "Andrey", "Tenenbaum", UserStateEnum.SUPERVISOR, "sup1", "cs", "", "", "", "", "");
        userController.login("Andrey");

        Assert.assertFalse(surveyController.createSurvey("Andrey", surveyDTO).isFailure());
    }

    @Test
    public void faultDetectionSuccess(){
        Response<List<List<String>>> faults;
        String year = "תשפ\"ג";

        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.login("Dvorit");

        Response<String> res = surveyController.createSurvey("Dvorit", surveyDTO);
        answersDTO.setId(res.getResult());
        surveyController.addAnswers(answersDTO);

        faults = surveyController.detectFault("Dvorit", res.getResult(), year);

        Assert.assertFalse(faults.isFailure());
    }

    @Test
    public void faultDetectionWrongSupervisorFailure(){
        Response<List<List<String>>> faults;
        String year = "תשפ\"ג";

        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.registerUserBySystemManager(adminName, "Shosh", "Bar", UserStateEnum.COORDINATOR, "", "hebrew", "", "", "", "", "");

        userController.login("Dvorit");

        Response<String> res = surveyController.createSurvey("Dvorit", surveyDTO);
        answersDTO.setId(res.getResult());
        surveyController.addAnswers(answersDTO);

        faults = surveyController.detectFault("Shosh", res.getResult(), year);

        Assert.assertTrue(faults.isFailure());
    }

    @Test
    public void addRuleSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.login("Dvorit");

        Response<String> res2 = surveyController.createSurvey("Dvorit", surveyDTO);
        Response<Boolean> res = surveyController.addRule("Dvorit", res2.getResult(), new NumericBaseRule(0, Comparison.GREATER_THEN, 28), 0);

        Assert.assertFalse(res.isFailure());
    }

    @Test
    public void addRuleNoPermissionFailure(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.registerUserBySystemManager(adminName, "Levana", "Zoharim", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");

        userController.login("Dvorit");

        Response<String> res2 = surveyController.createSurvey("Dvorit", surveyDTO);
        Response<Boolean> res = surveyController.addRule("Levana", res2.getResult(), new NumericBaseRule(0, Comparison.GREATER_THEN, 28), 0);

        Assert.assertTrue(res.isFailure());
    }

    @Test
    public void removeRuleSuccess(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.login("Dvorit");

        Response<String> res2 = surveyController.createSurvey("Dvorit", surveyDTO);
        surveyController.addRule("Dvorit", res2.getResult(), new NumericBaseRule(0, Comparison.GREATER_THEN, 28), 0);

        when(surveyDAO.removeRule(0)).thenReturn(new Response<>(true, false, "OK"));

        Response<Boolean> res = surveyController.removeRule("Dvorit", res2.getResult(), 0);

        Assert.assertFalse(res.isFailure());
    }

    @Test
    public void removeRuleNotExistsFailure(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.login("Dvorit");

        Response<String> res2 = surveyController.createSurvey("Dvorit", surveyDTO);
        when(surveyDAO.removeRule(0)).thenReturn(new Response<>(false, true, "rule not in db"));

        Response<Boolean> res = surveyController.removeRule("Dvorit", res2.getResult(), 0);

        Assert.assertTrue(res.isFailure());
    }

    @Test
    public void removeRuleNoPermissionFailure(){
        UserController userController = UserController.getInstance();
        String adminName = userController.login("admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");

        userController.login("Dvorit");

        Response<String> res2 = surveyController.createSurvey("Dvorit", surveyDTO);
        surveyController.addRule("Dvorit", res2.getResult(), new NumericBaseRule(0, Comparison.GREATER_THEN, 28), 0);

        when(surveyDAO.removeRule(0)).thenReturn(new Response<>(false, true, "fail"));

        Response<Boolean> res = surveyController.removeRule("Dvorit", res2.getResult(), 0);

        Assert.assertTrue(res.isFailure());
    }

}
