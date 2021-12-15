package Service.Interfaces;

import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Domain.DataManagement.Survey;

import java.util.List;

public interface SurveyService {

    // Data Management Module
    Response<Integer> createSurvey(String username, SurveyDTO surveyDTO);

    Response<Boolean> addAnswers(SurveyAnswersDTO answersDTO);

//    Response<Boolean> removeSurvey(String username, int id);
//
//    Response<Survey> getSurvey(int id);
//
//    Response<Survey> publishSurvey(String username);
//
//    Response<Integer> addQuestion(int id, String questionText);
//
//    Response<Boolean> removeQuestion(int id, int questionID);
//
//    Response<Integer> addAnswer(int id, int questionID, String answer);
//
//    Response<Boolean> removeAnswer(int id, int questionID, int answerID);
//
//    Response<Boolean> addRule(String username, int id, Rule rule, String description);
//
//    Response<Boolean> removeRule(String username, int id, int index);

    Response<List<String>> detectFault(String username, int id);


}
