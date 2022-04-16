package UnitTesting.DataBase;

import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Persistence.SurveyQueries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SurveyDbTests {
    SurveyQueries surveyQueries;
    SurveyDTO surveyDTO;

    @Before
    public void setUp(){
        surveyQueries = SurveyQueries.getInstance();

        List<String> questions = new LinkedList<>();
        questions.add("q1"); questions.add("q2");

        List <AnswerType> answerTypes = new LinkedList<>();
        answerTypes.add(AnswerType.MULTIPLE_CHOICE); answerTypes.add(AnswerType.OPEN_ANSWER);

        List <List<String>> answers = new LinkedList<>();
        List <String> answers1 = new LinkedList<>();
        answers1.add("a"); answers1.add("20"); answers1.add("30"); answers1.add("40");
        List <String> answers2 = new LinkedList<>();
        answers2.add("keep my wife's name, out your f* mouth");
        answers.add(answers1); answers.add(answers2);

        surveyDTO = new SurveyDTO("1","survey1", "some desc", questions, answers, answerTypes);
    }

    @Test
    public void insertSurvey() throws SQLException {
        Assert.assertFalse(surveyQueries.insertSurvey(surveyDTO).isFailure());
    }

    @Test
    public void getSurvey() throws SQLException {
        Response<SurveyDTO> surveyDTO = surveyQueries.getSurvey("1");
        Assert.assertEquals(4, surveyDTO.getResult().getQuestions().size() + surveyDTO.getResult().getAnswers().size());
    }

}

