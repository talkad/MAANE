package UnitTesting.DataBase;

import Communication.DTOs.SurveyDTO;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.Survey;
import Persistence.SurveyQueries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


public class QueriesTests {
    SurveyDTO surveyDTO;

    @Before
    public void setUp(){
        List <String> questions = new LinkedList<>();
        questions.add("q1"); questions.add("q2");

        List <AnswerType> answerTypes = new LinkedList<>();
        answerTypes.add(AnswerType.NUMERIC_ANSWER); answerTypes.add(AnswerType.OPEN_ANSWER);

        List <List<String>> answers = new LinkedList<>();
        List <String> answers1 = new LinkedList<>();
        answers1.add("10"); answers1.add("20"); answers1.add("30"); answers1.add("40");
        List <String> answers2 = new LinkedList<>();
        answers2.add("aa"); answers2.add("bb"); answers2.add("cc"); answers2.add("dd");
        answers.add(answers1); answers.add(answers2);

        surveyDTO = new SurveyDTO("1","survey2", "some desc", questions, answers, answerTypes);
    }

    @Test
    public void insertSurvey() throws SQLException {
        Assert.assertFalse(SurveyQueries.insertSurvey(surveyDTO).isFailure());
    }

}

