package UnitTesting.DataBase;

import Communication.DTOs.SurveyDTO;
import Domain.DataManagement.Survey;
import Persistence.SurveyQueries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;


public class QueriesTests {
    SurveyDTO surveyDTO;

    @Before
    public void setUp(){
        surveyDTO = new SurveyDTO(2,"survey2", "some desc", null, null, null);
    }

    @Test
    public void insertSurvey() throws SQLException {
        Assert.assertFalse(SurveyQueries.insertSurvey(surveyDTO).isFailure());
    }

}

