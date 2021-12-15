package Service;

import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
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
    public Response<Integer> createSurvey(String username, SurveyDTO surveyDTO) {
        Response<Integer> res = SurveyController.getInstance().createSurvey(username, surveyDTO);

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

//
//    @Override
//    public Response<Boolean> removeSurvey(String username, int id) {
//        Response<Boolean> res = SurveyController.getInstance().removeSurvey(username, id);
//
//        if(res.isFailure())
//            log.error("{} failed to remove survey", username);
//        else
//            log.info("{} removed survey with id {}", username, id);
//
//        return res;
//    }
//
//    @Override
//    public Response<Survey> getSurvey(int id) {
//        Response<Survey> res = SurveyController.getInstance().getSurvey(id);
//
//        if(res.isFailure())
//            log.error("failed to get survey {}", id);
//        else
//            log.info("get survey {}", id);
//
//        return res;
//    }
//
//    @Override
//    public Response<Survey> publishSurvey(String username) {
//        Response<Survey> res = SurveyController.getInstance().publishSurvey(username);
//
//        if(res.isFailure())
//            log.error("publish survey failed by user {}", username);
//        else
//            log.info("{} published survey", username);
//
//        return res;
//    }
//
//    @Override
//    public Response<Integer> addQuestion(int id, String questionText) {
//        Response<Integer> res = SurveyController.getInstance().addQuestion(id, questionText);
//
//        if(res.isFailure())
//            log.error("failed to add question: {}", questionText);
//        else
//            log.info("added question: {}", questionText);
//
//        return res;
//    }
//
//    @Override
//    public Response<Boolean> removeQuestion(int id, int questionID) {
//        Response<Boolean> res = SurveyController.getInstance().removeQuestion(id, questionID);
//
//        if(res.isFailure())
//            log.error("failed to remove question: {}", questionID);
//        else
//            log.info("removed question: {}", questionID);
//
//        return res;
//    }
//
//    @Override
//    public Response<Integer> addAnswer(int id, int questionID, String answer) {
//        Response<Integer> res = SurveyController.getInstance().addAnswer(id, questionID, answer);
//
//        if(res.isFailure())
//            log.error("failed to add answer: {}", answer);
//        else
//            log.info("added answer: {}", answer);
//
//        return res;
//    }
//
//    @Override
//    public Response<Boolean> removeAnswer(int id, int questionID, int answerID) {
//        Response<Boolean> res = SurveyController.getInstance().removeAnswer(id, questionID, answerID);
//
//        if(res.isFailure())
//            log.error("failed to remove answer: {}", answerID);
//        else
//            log.info("removed answer: {}", answerID);
//
//        return res;
//    }
//
//    @Override
//    public Response<Boolean> addRule(String username, int id, Rule rule, String description) {
//        Response<Boolean> res = SurveyController.getInstance().addRule(username, id, rule, description);
//
//        if(res.isFailure())
//            log.error("failed to add rule: {}", description);
//        else
//            log.info("added rule: {}", description);
//
//        return res;
//    }
//
//    @Override
//    public Response<Boolean> removeRule(String username, int id, int index) {
//        Response<Boolean> res = SurveyController.getInstance().removeRule(username, id, index);
//
//        if(res.isFailure())
//            log.error("failed to remove rule: {}", index);
//        else
//            log.info("removed rule: {}", index);
//
//        return res;
//    }

    @Override
    public Response<List<List<String>>> detectFault(String username, int id) {
        Response<List<List<String>>> res = SurveyController.getInstance().detectFault(username, id);

        if(res.isFailure())
            log.error("failed to detect faults in survey {}", id);
        else
            log.info("detected faults in survey {}", id);

        return res;
    }
}
