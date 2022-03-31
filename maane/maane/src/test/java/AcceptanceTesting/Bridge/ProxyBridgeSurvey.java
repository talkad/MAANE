package AcceptanceTesting.Bridge;

import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Communication.Service.Interfaces.SurveyService;
import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.Rule;

import java.util.List;

public class ProxyBridgeSurvey implements SurveyService {

    private SurveyService real;

    public ProxyBridgeSurvey(){
        real = null;
    }

    public void setRealBridge(SurveyService implementation) {
        if(real == null){
            real = implementation;
        }
    }

    @Override
    public Response<String> createSurvey(String username, SurveyDTO surveyDTO) {
        if (real != null){
            return real.createSurvey(username, surveyDTO);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> addAnswers(SurveyAnswersDTO answersDTO) {
        if (real != null){
            return real.addAnswers(answersDTO);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<SurveyDTO> getSurvey(String surveyID) {
        if (real != null){
            return real.getSurvey(surveyID);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> addRule(String username, String surveyID, Rule rule, int goalID) {
        if (real != null){
            return real.addRule(username, surveyID, rule, goalID);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> removeRule(String username, String surveyID, int ruleID) {
        if (real != null){
            return real.removeRule(username, surveyID, ruleID);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<List<String>> getSurveys(String username) {
        if (real != null){
            return real.getSurveys(username);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<List<Rule>> getRules(String surveyID) {
        if (real != null){
            return real.getRules(surveyID);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<List<List<String>>> detectFault(String username, String surveyID, String year) {
        if (real != null){
            return real.detectFault(username, surveyID, year);
        }

        return new Response<>(null, true, "not implemented");
    }
}