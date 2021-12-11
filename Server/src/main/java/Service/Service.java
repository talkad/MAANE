package Service;

import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Domain.DataManagement.Survey;
import Domain.DataManagement.SurveyController;

import java.util.List;

public class Service implements IService{

    private static class CreateSafeThreadSingleton {
        private static final Service INSTANCE = new Service();
    }

    public static Service getInstance() {
        return Service.CreateSafeThreadSingleton.INSTANCE;
    }

    @Override
    public Response<Integer> createSurvey(String username, String title) {
        return SurveyController.getInstance().createSurvey(username, title);
    }

    @Override
    public Response<Boolean> removeSurvey(String username, int id) {
        return SurveyController.getInstance().removeSurvey(username, id);
    }

    @Override
    public Response<Survey> getSurvey(int id) {
        return SurveyController.getInstance().getSurvey(id);
    }

    @Override
    public Response<Survey> publishSurvey(String username) {
        return SurveyController.getInstance().publishSurvey(username);
    }

    @Override
    public Response<Integer> addQuestion(int id, String questionText) {
        return SurveyController.getInstance().addQuestion(id, questionText);
    }

    @Override
    public Response<Boolean> removeQuestion(int id, int questionID) {
        return SurveyController.getInstance().removeQuestion(id, questionID);
    }

    @Override
    public Response<Integer> addAnswer(int id, int questionID, String answer) {
        return SurveyController.getInstance().addAnswer(id, questionID, answer);
    }

    @Override
    public Response<Boolean> removeAnswer(int id, int questionID, int answerID) {
        return SurveyController.getInstance().removeAnswer(id, questionID, answerID);
    }

    @Override
    public Response<Boolean> addRule(String username, int id, Rule rule, String description) {
        return SurveyController.getInstance().addRule(username, id, rule, description);
    }

    @Override
    public Response<Boolean> removeRule(String username, int id, int index) {
        return SurveyController.getInstance().removeRule(username, id, index);
    }

    @Override
    public Response<List<String>> detectFault(String username, int id) {
        return SurveyController.getInstance().detectFault(username, id);
    }
}
