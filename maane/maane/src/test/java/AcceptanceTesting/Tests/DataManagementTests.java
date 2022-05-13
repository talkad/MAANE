package AcceptanceTesting.Tests;

import AcceptanceTesting.Bridge.ProxyBridgeSurvey;
import Communication.DTOs.QuestionDTO;
import Communication.DTOs.RuleRequestDTO;
import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Communication.Initializer.ServerContextInitializer;
import Communication.Service.SurveyServiceImpl;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.Rules.*;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Persistence.SurveyDAO;
import Persistence.SurveyPersistence;
import Persistence.UserQueries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static Domain.DataManagement.AnswerState.AnswerType.*;

public class DataManagementTests {

    private SurveyDTO surveyDTO;
    private SurveyAnswersDTO answersDTO;


    private ProxyBridgeSurvey proxySurvey;

    @Before
    public void setUp(){
        proxySurvey = new ProxyBridgeSurvey();
        proxySurvey.setRealBridge(new SurveyServiceImpl());

        surveyDTO = new SurveyDTO();
        ServerContextInitializer.getInstance().setMockMode();
        ServerContextInitializer.getInstance().setTestMode();
        SurveyDAO.getInstance().clearCache();
        UserQueries.getInstance().clearDB();
        UserController.getInstance().clearUsers();

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
        answersDTO.setAnswers(answers2);
        answersDTO.setTypes(types2);

    }

    /**
     * Supervisor create a survey, four coordinators answer it when one of then answers before the submission
     * and another two answer illegal answers
     */
    @Test
    public void coordinatorAnswersTests(){
        Response<Boolean> answerRes;

        UserController userController = UserController.getInstance();
        String adminName = userController.login( "admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "dvorit@gmail.com", "055-555-5555", "");
        userController.login("Dvorit");

        // create legal survey
        Response<String> surveyRes = proxySurvey.createSurvey("Dvorit", surveyDTO);

        // answer the survey
        answerRes = proxySurvey.addAnswers(new SurveyAnswersDTO(surveyRes.getResult(),
                new LinkedList<>(Arrays.asList("20", "0", "0")),
                new LinkedList<>(Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE))));

        Assert.assertTrue(answerRes.isFailure());

        // submit survey
        proxySurvey.submitSurvey("Dvorit", surveyRes.getResult());

        // legal answer
        answerRes = proxySurvey.addAnswers(new SurveyAnswersDTO(surveyRes.getResult(),
                new LinkedList<>(Arrays.asList("20", "0", "0")),
                new LinkedList<>(Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE))));

        Assert.assertFalse(answerRes.isFailure());

        // illegal answer - non-numeric answer
        answerRes = proxySurvey.addAnswers(new SurveyAnswersDTO(surveyRes.getResult(),
                new LinkedList<>(Arrays.asList("hello hi", "0", "0")),
                new LinkedList<>(Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE))));

        Assert.assertTrue(answerRes.isFailure());

        // illegal answer - multiple choice index out of bounds
        answerRes = proxySurvey.addAnswers(new SurveyAnswersDTO(surveyRes.getResult(),
                new LinkedList<>(Arrays.asList("hello hi", "0", "2")),
                new LinkedList<>(Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE))));

        Assert.assertTrue(answerRes.isFailure());

        // get all accepted answers
        Assert.assertEquals(1, SurveyPersistence.getInstance().getAnswers(surveyRes.getResult()).size());
    }


    /**
     * Two supervisors try to add rules to a survey when only one of them created it
     */
    @Test
    public void rulesPermissionTests(){
        Response<Boolean> ruleRes;
        Rule rule1, rule2;

        UserController userController = UserController.getInstance();
        String adminName = userController.login( "admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "dvorit@gmail.com", "055-555-5555", "");
        userController.login("Dvorit");

        userController.registerUserBySystemManager(adminName, "Levana", "Zoharim", UserStateEnum.SUPERVISOR, "", "hebrew", "", "", "dvorit@gmail.com", "055-555-5555", "");
        userController.login("Levana");

        // create legal survey
        Response<String> surveyRes = proxySurvey.createSurvey("Dvorit", surveyDTO);

        rule1 = new AndRule(Arrays.asList(new NumericBaseRule(0, Comparison.EQUAL, 30),
                new MultipleChoiceBaseRule(1, List.of(1))));
        rule2 = new NumericBaseRule(0, Comparison.EQUAL, 10);

        // legal rule creation
        ruleRes = proxySurvey.addRule("Dvorit", surveyRes.getResult(),
                Arrays.asList(new RuleRequestDTO(1, rule1.getDTO()),
                        new RuleRequestDTO(2, rule2.getDTO())));

        Assert.assertFalse(ruleRes.isFailure());

        // illegal rule creation
        ruleRes = proxySurvey.addRule("Levana", surveyRes.getResult(),
                Arrays.asList(new RuleRequestDTO(1, rule1.getDTO()),
                        new RuleRequestDTO(2, rule2.getDTO())));

        Assert.assertTrue(ruleRes.isFailure());
    }

    /**
     * update survey before and after its submission
     */
    @Test
    public void surveyUpdateTests(){
        Response<Boolean> updateRes;

        UserController userController = UserController.getInstance();
        String adminName = userController.login( "admin").getResult();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "dvorit@gmail.com", "055-555-5555", "");
        userController.login("Dvorit");

        userController.registerUserBySystemManager(adminName, "Levana", "Zoharim", UserStateEnum.SUPERVISOR, "", "hebrew", "", "", "dvorit@gmail.com", "055-555-5555", "");
        userController.login("Levana");

        // create legal survey
        Response<String> surveyRes = proxySurvey.createSurvey("Dvorit", surveyDTO);

        // add legal question
        updateRes = proxySurvey.addQuestion("Dvorit", new QuestionDTO(surveyRes.getResult(), "que",
                new LinkedList<>(), OPEN_ANSWER));

        Assert.assertFalse(updateRes.isFailure());

        // remove illegal question
        updateRes = proxySurvey.removeQuestion("Levana", surveyRes.getResult(), 50);

        Assert.assertTrue(updateRes.isFailure());

        proxySurvey.submitSurvey("Dvorit", surveyRes.getResult());

        // add legal answer after submission
        updateRes = proxySurvey.addQuestion("Dvorit", new QuestionDTO(surveyRes.getResult(), "que",
                new LinkedList<>(), OPEN_ANSWER));

        Assert.assertTrue(updateRes.isFailure());
    }




}
