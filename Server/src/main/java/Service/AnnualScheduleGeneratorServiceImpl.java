package Service;

import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.SurveyController;
import Domain.WorkPlan.AnnualScheduleGenerator;
import Domain.WorkPlan.Goal;
import Service.Interfaces.AnnualScheduleGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class AnnualScheduleGeneratorServiceImpl implements AnnualScheduleGeneratorService {

    private static class CreateSafeThreadSingleton {
        private static final AnnualScheduleGeneratorServiceImpl INSTANCE = new AnnualScheduleGeneratorServiceImpl();
    }

    public static AnnualScheduleGeneratorServiceImpl getInstance() {
        return AnnualScheduleGeneratorServiceImpl.CreateSafeThreadSingleton.INSTANCE;
    }

    @Override
    public Response<Boolean> generateSchedule(String supervisor, String surveyId){
        Response<Boolean> res = AnnualScheduleGenerator.getInstance().generateSchedule(supervisor, surveyId);

        if(res.isFailure())
            log.error(res.getErrMsg());
        else
            log.info("annual work plan successfully generated");

        return res;
    }
}
