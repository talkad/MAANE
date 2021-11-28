package UnitTesting.DataManagement;

import DataManagement.FaultDetector.FaultDetector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FaultDetectorTest {

    private FaultDetector detector;

    @Before
    public void setUp(){
        detector = new FaultDetector();
    }

    @Test
    public void legalSurveyNoRules(){

    }

    @Test
    public void legalSurveyWithRules(){

    }

    @Test
    public void illegalSurveyWithRules(){

    }


}
