package AcceptanceTesting.Tests;

import AcceptanceTesting.Bridge.Driver;
import Service.Interfaces.SurveyService;
import Service.Interfaces.UserService;

public abstract class AcceptanceTest {

    protected static UserService userBridge;
    protected static SurveyService surveyBridge;

    public void setUp(boolean toInit) {
        if (toInit) {
            userBridge = Driver.getUserBridge();
            surveyBridge = Driver.getSurveyBridge();
        }
    }
}
