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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static Domain.DataManagement.AnswerState.AnswerType.MULTIPLE_CHOICE;
import static Domain.DataManagement.AnswerState.AnswerType.NUMERIC_ANSWER;

public class SurveyIntegrationTests {

    private SurveyDTO surveyDTO;
    private SurveyAnswersDTO answersDTO;
    private final SurveyController surveyController = SurveyController.getInstance();

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

        answersDTO = new SurveyAnswersDTO();

        List<String> answers2 = Arrays.asList("30", "1", "2");
        List<AnswerType> types2 = Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE);

        answersDTO.setSymbol("A123");
        answersDTO.setAnswers(answers2);
        answersDTO.setTypes(types2);

    }

    @Test
    public void surveyCreationSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserBySystemManager(adminName, "Miri", "Band", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");

        Assert.assertFalse(surveyController.createSurvey("Dvorit", surveyDTO).isFailure());
    }

    @Test
    public void surveyCreationNoPermissionFailure(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserBySystemManager(adminName, "Shlomit", "Malka", UserStateEnum.INSTRUCTOR, "", "tech", "", "", "", "", "");
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Shlomit", "Malka");

        Assert.assertTrue(surveyController.createSurvey("Shlomit", surveyDTO).isFailure());
    }

    @Test
    public void faultDetectionSuccess(){
        Response<List<List<String>>> faults;
        String year = "תשפ\"ג";

        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");

        surveyController.createSurvey("Dvorit", surveyDTO);
        surveyController.addAnswers(answersDTO);

        faults = surveyController.detectFault("Dvorit", 0, year);

        Assert.assertFalse(faults.isFailure());
    }

    @Test
    public void faultDetectionWrongSupervisorFailure(){
        Response<List<List<String>>> faults;
        String year = "תשפ\"ג";

        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.registerUserBySystemManager(adminName, "Shosh", "Bar", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");

        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");
        userController.login(newGuestName, "Shosh", "Bar");

        surveyController.createSurvey("Dvorit", surveyDTO);
        surveyController.addAnswers(answersDTO);

        faults = surveyController.detectFault("Shosh", 0, year);

        Assert.assertTrue(faults.isFailure());
    }

    @Test
    public void addRuleSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");

        surveyController.createSurvey("Dvorit", surveyDTO);
        Response<Boolean> res = surveyController.addRule("Dvorit", 0, new NumericBaseRule(0, Comparison.GREATER_THEN, 28), 0);

        Assert.assertFalse(res.isFailure());
    }

    @Test
    public void addRuleNoPermissionFailure(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.registerUserBySystemManager(adminName, "Levana", "Zoharim", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");

        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");
        userController.login(newGuestName, "Levana", "Zoharim");

        surveyController.createSurvey("Dvorit", surveyDTO);
        Response<Boolean> res = surveyController.addRule("Levana", 0, new NumericBaseRule(0, Comparison.GREATER_THEN, 28), 0);

        Assert.assertTrue(res.isFailure());
    }

    @Test
    public void removeRuleSuccess(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");

        surveyController.createSurvey("Dvorit", surveyDTO);
        surveyController.addRule("Dvorit", 0, new NumericBaseRule(0, Comparison.GREATER_THEN, 28), 0);
        Response<Boolean> res = surveyController.removeRule("Dvorit", 0, 0);

        Assert.assertFalse(res.isFailure());
    }

    @Test
    public void removeRuleNotExistsFailure(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");

        surveyController.createSurvey("Dvorit", surveyDTO);
        Response<Boolean> res = surveyController.removeRule("Dvorit", 0, 0);

        Assert.assertTrue(res.isFailure());
    }

    @Test
    public void removeRuleNoPermissionFailure(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();
        String adminName = userController.login(guestName, "admin", "admin").getResult().getFirst();
        userController.registerUserBySystemManager(adminName, "Dvorit", "Dvorit", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");
        userController.registerUserBySystemManager(adminName, "Levana", "Zoharim", UserStateEnum.SUPERVISOR, "", "tech", "", "", "", "", "");

        String newGuestName = userController.logout(adminName).getResult();
        userController.login(newGuestName, "Dvorit", "Dvorit");
        userController.login(newGuestName, "Levana", "Zoharim");

        surveyController.createSurvey("Dvorit", surveyDTO);
        surveyController.addRule("Levana", 0, new NumericBaseRule(0, Comparison.GREATER_THEN, 28), 0);
        Response<Boolean> res = surveyController.removeRule("Levana", 0, 0);

        Assert.assertTrue(res.isFailure());
    }
}
