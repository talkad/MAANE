package AcceptanceTesting.Tests;

import AcceptanceTesting.Bridge.Driver;
import Service.Interfaces.AnnualScheduleGeneratorService;
import Service.Interfaces.SurveyService;
import Service.Interfaces.UserService;

public abstract class AcceptanceTests {

    protected static UserService userBridge;
    protected static SurveyService surveyBridge;
    protected static AnnualScheduleGeneratorService scheduleBridge;


    public void setUp(boolean toInit) {
        if (toInit) {
            userBridge = Driver.getUserBridge();
            surveyBridge = Driver.getSurveyBridge();
            scheduleBridge = Driver.getScheduleBridge();
        }
    }
}
