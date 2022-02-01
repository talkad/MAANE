package AcceptanceTesting.Bridge;

import Service.Interfaces.SurveyService;
import Service.Interfaces.UserService;
import Service.SurveyServiceImpl;
import Service.UserServiceImpl;

public abstract class Driver {
    public static UserService getUserBridge(){
        ProxyBridgeUser bridge = new ProxyBridgeUser(); // proxy bridge

        UserServiceImpl real = UserServiceImpl.getInstance(); // real bridge
        bridge.setRealBridge(real);

        return bridge;
    }

    public static SurveyService getSurveyBridge(){
        ProxyBridgeSurvey bridge = new ProxyBridgeSurvey(); // proxy bridge

        SurveyServiceImpl real = SurveyServiceImpl.getInstance(); // real bridge
        bridge.setRealBridge(real);

        return bridge;
    }
}
