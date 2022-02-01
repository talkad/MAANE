package UnitTesting.DataManagement;

import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.Rules.Comparison;
import Domain.DataManagement.FaultDetector.Rules.MultipleChoiceBaseRule;
import Domain.DataManagement.FaultDetector.Rules.NumericBaseRule;
import Domain.DataManagement.SurveyController;
import Domain.WorkPlan.Goal;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static Domain.DataManagement.AnswerState.AnswerType.*;

public class SurveyControllerTests {

    private SurveyDTO surveyDTO;
    private SurveyAnswersDTO answersDTO1, answersDTO2;
    private SurveyController surveyController = SurveyController.getInstance();

    @Before
    public void setUp(){
        surveyDTO = new SurveyDTO();
        surveyController.clearCache();

        List<String> questions1 = Arrays.asList("que1", "que2", "que3");
        List<List<String>> answers1 = Arrays.asList(new LinkedList<>(), Arrays.asList("1", "2"), Arrays.asList("1", "2"));
        List<AnswerType> types1 = Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE);;

        surveyDTO.setId(-1);
        surveyDTO.setTitle("title");
        surveyDTO.setDescription("description");
        surveyDTO.setQuestions(questions1);
        surveyDTO.setTypes(types1);
        surveyDTO.setAnswers(answers1);

        // legal answer
        answersDTO1 = new SurveyAnswersDTO();

        List<String> answers2 = Arrays.asList("30", "1", "2");
        List<AnswerType> types2 = Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE);

        answersDTO1.setSymbol("A123");
        answersDTO1.setAnswers(answers2);
        answersDTO1.setTypes(types2);

        // illegal answer
        answersDTO2 = new SurveyAnswersDTO();

        List<String> answers3 = Arrays.asList("30", "1");
        List<AnswerType> types3 = Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE);

        answersDTO1.setSymbol("B456");
        answersDTO2.setAnswers(answers3);
        answersDTO2.setTypes(types3);
    }

    @Test
    public void surveyCreationSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserByAdmin(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");

        Assert.assertFalse(surveyController.createSurvey("Dvorit", surveyDTO).isFailure());
    }

    @Test
    public void addAnswerSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserByAdmin(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");

        surveyController.createSurvey("Dvorit", surveyDTO);
        Assert.assertFalse(surveyController.addAnswers(answersDTO1).isFailure());
    }

    @Test
    public void addAnswerFailure(){
        surveyController.createSurvey("Dvorit", surveyDTO);
        Assert.assertTrue(surveyController.addAnswers(answersDTO2).isFailure());
    }

    @Test
    public void faultDetectionSuccess(){
        Response<List<List<String>>> faults;

        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserByAdmin(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");

        surveyController.createSurvey("Dvorit", surveyDTO);
        surveyController.addAnswers(answersDTO1);
        surveyController.addAnswers(answersDTO1);

        surveyController.addRule("Dvorit", 0, new NumericBaseRule(0, Comparison.GREATER_THEN, 28), 0);
        surveyController.addRule("Dvorit", 0, new MultipleChoiceBaseRule(1, 1), 1);
        surveyController.addRule("Dvorit", 0, new MultipleChoiceBaseRule(2, 2), 2);

        UserController.getInstance().addGoals("Dvorit", Arrays.asList(new Goal(0, "goal0", "goal0", 1,1),
                                                                                new Goal(1, "goal1", "goal1", 1, 1),
                                                                                new Goal(2, "goal2", "goal2", 1,1)));
        faults = surveyController.detectFault("Dvorit", 0);

        Assert.assertTrue(faults.getResult().size() == 2 && faults.getResult().get(0).size() == 3);

    }
}
