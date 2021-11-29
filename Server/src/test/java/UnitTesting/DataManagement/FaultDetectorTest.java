package UnitTesting.DataManagement;

import Domain.DataManagement.FaultDetector.FaultDetector;
import Domain.DataManagement.FaultDetector.Rules.*;
import Domain.DataManagement.Survey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class FaultDetectorTest {

    private FaultDetector detector;
    private Survey survey;

    @Before
    public void setUp(){
        detector = new FaultDetector();
        survey = new Survey();

        /*
        definition for a survey containing to questions with to answers both.
        for the first question the first answer is marked
        and for the second question the second answer is marked
         */
        survey.addQuestion("que1",null);
        survey.getQuestion(0).addAnswer("ans1");
        survey.getQuestion(0).addAnswer("ans2");
        survey.getQuestion(0).markAnswer(0);

        survey.addQuestion("que2",null);
        survey.getQuestion(1).addAnswer("ans1");
        survey.getQuestion(1).addAnswer("ans2");
        survey.getQuestion(1).markAnswer(1);
    }

    @Test
    public void legalSurveyNoRules(){
        List<String> faults = detector.detectFault(survey);

        Assert.assertEquals(0, faults.size());
    }

    @Test
    public void legalSurveyWithRules(){
        List<Rule> baseRules = new LinkedList<>();
        baseRules.add(new BaseRule(0, 0));
        baseRules.add(new BaseRule(1, 0));

        detector.addRule(new AndRule(baseRules), "description1");

        List<String> faults = detector.detectFault(survey);

        Assert.assertEquals(0, faults.size());
    }

    @Test
    public void illegalSurveyWithRulesAnd(){
        List<Rule> baseRules = new LinkedList<>();
        baseRules.add(new BaseRule(0, 0));
        baseRules.add(new BaseRule(1, 1));

        detector.addRule(new AndRule(baseRules), "description1");

        List<String> faults = detector.detectFault(survey);

        Assert.assertEquals(1, faults.size());
    }

    @Test
    public void illegalSurveyWithRulesOr(){
        List<Rule> baseRules = new LinkedList<>();
        baseRules.add(new BaseRule(0, 0));
        baseRules.add(new BaseRule(1, 0));

        detector.addRule(new OrRule(baseRules), "description1");

        List<String> faults = detector.detectFault(survey);

        Assert.assertEquals(1, faults.size());
    }

    @Test
    public void illegalSurveyWithRulesImply(){
        detector.addRule(new ImplyRule(new BaseRule(0, 0), new BaseRule(1,1)), "description1");
        detector.addRule(new ImplyRule(new BaseRule(0, 1), new BaseRule(1,0)), "description2");

        List<String> faults = detector.detectFault(survey);

        Assert.assertEquals(2, faults.size());
    }

    @Test
    public void illegalSurveyWithRulesIff(){
        detector.addRule(new IffRule(new BaseRule(0, 0), new BaseRule(1,1)), "description1");
        detector.addRule(new ImplyRule(new BaseRule(0, 0), new BaseRule(1,0)), "description2");

        List<String> faults = detector.detectFault(survey);

        Assert.assertEquals(1, faults.size());
    }

    @Test
    public void illegalSurveyWithRulesComplex(){
        List<Rule> baseRules = new LinkedList<>();
        baseRules.add(new BaseRule(0, 0));
        baseRules.add(new BaseRule(1, 0));

        List<Rule> rules = new LinkedList<>();
        rules.add(new AndRule(baseRules));
        rules.add(new BaseRule(1, 1));

        detector.addRule(new OrRule(rules), "description1");

        List<String> faults = detector.detectFault(survey);

        Assert.assertEquals(1, faults.size());
    }


}
