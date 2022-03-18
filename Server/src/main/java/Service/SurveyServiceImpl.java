package Service;

import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Domain.DataManagement.SurveyController;
import Service.Interfaces.SurveyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class SurveyServiceImpl implements SurveyService {


    private static class CreateSafeThreadSingleton {
        private static final SurveyServiceImpl INSTANCE = new SurveyServiceImpl();
    }

    public static SurveyServiceImpl getInstance() {
        return SurveyServiceImpl.CreateSafeThreadSingleton.INSTANCE;
    }

    @Override
    public Response<String> createSurvey(String username, SurveyDTO surveyDTO) {
        Response<String> res = SurveyController.getInstance().createSurvey(username, surveyDTO);

        if(res.isFailure())
            log.error("{} failed to create new survey", username);
        else
            log.info("{} created new survey with title: {}", username, surveyDTO.getTitle());

        return res;
    }

    @Override
    public Response<Boolean> addAnswers(SurveyAnswersDTO answersDTO) {
        Response<Boolean> res = SurveyController.getInstance().addAnswers(answersDTO);

        if(res.isFailure())
            log.error("failed to add answers");
        else
            log.info("added answers successfully");

        return res;
    }

    @Override
    public Response<SurveyDTO> getSurvey(String surveyID) {
        Response<SurveyDTO> res = SurveyController.getInstance().getSurvey(surveyID);

        if(res.isFailure())
            log.error(res.getErrMsg());
        else
            log.info("survey {} found", surveyID);

        return res;
    }

    @Override
    public Response<Boolean> addRule(String username, String surveyID, Rule rule, int goalID) {
        Response<Boolean> res = SurveyController.getInstance().addRule(username, surveyID, rule, goalID);

        if(res.isFailure())
            log.error(res.getErrMsg());
        else
            log.info("new rule added successfully");

        return res;
    }

    @Override
    public Response<Boolean> removeRule(String username, String surveyID, int ruleID) {
        Response<Boolean> res = SurveyController.getInstance().removeRule(username, surveyID, ruleID);

        if(res.isFailure())
            log.error(res.getErrMsg());
        else
            log.info("new rule added successfully");

        return res;
    }

    @Override
    public Response<List<List<String>>> detectFault(String username, String surveyID, String year) {
        Response<List<List<String>>> res = SurveyController.getInstance().detectFault(username, surveyID, year);

        if(res.isFailure())
            log.error("failed to detect faults in survey {}", surveyID);
        else
            log.info("detected faults in survey {}", surveyID);

        return res;
    }

    public Response<List<String>> getSurveys(String username) {
        Response<List<String>> res = SurveyController.getInstance().getSurveys(username);

        if(res.isFailure())
            log.error(res.getErrMsg());
        else
            log.info("get surveys");

        return res;
    }

    @Override
    public Response<List<Rule>> getRules(String surveyID) {
        Response<List<Rule>> res = SurveyController.getInstance().getRules(surveyID);

        if(res.isFailure())
            log.error(res.getErrMsg());
        else
            log.info("get rules");

        return res;
    }


}
