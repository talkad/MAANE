package AcceptanceTesting.Bridge;

import Communication.Service.AnnualScheduleGeneratorServiceImpl;
import Communication.Service.Interfaces.AnnualScheduleGeneratorService;
import Communication.Service.Interfaces.SurveyService;
import Communication.Service.Interfaces.UserService;
import Communication.Service.SurveyServiceImpl;
import Communication.Service.UserServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public abstract class Driver {
    public static UserService getUserBridge(){
        ProxyBridgeUser bridge = new ProxyBridgeUser(); // proxy bridge

        UserService real = new UserServiceImpl(new BCryptPasswordEncoder()); // real bridge
        bridge.setRealBridge(real);

        return bridge;
    }

    public static SurveyService getSurveyBridge(){
        ProxyBridgeSurvey bridge = new ProxyBridgeSurvey(); // proxy bridge

        SurveyServiceImpl real = SurveyServiceImpl.getInstance(); // real bridge
        bridge.setRealBridge(real);

        return bridge;
    }

    public static AnnualScheduleGeneratorService getScheduleBridge(){
        ProxyBridgeSchedule bridge = new ProxyBridgeSchedule(); // proxy bridge

        AnnualScheduleGeneratorServiceImpl real = AnnualScheduleGeneratorServiceImpl.getInstance(); // real bridge
        bridge.setRealBridge(real);

        return bridge;
    }
}
