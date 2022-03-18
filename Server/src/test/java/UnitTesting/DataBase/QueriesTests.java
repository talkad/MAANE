package UnitTesting.DataBase;

import Communication.DTOs.SurveyDTO;
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
        questions.add("q1"); questions.add("q2"); questions.add("q3");
        surveyDTO = new SurveyDTO(2,"survey2", "some desc", questions, null, null);
    }

    @Test
    public void insertSurvey() throws SQLException {
        Assert.assertFalse(SurveyQueries.insertSurvey(surveyDTO).isFailure());
    }

}

