package Persistence;

import Communication.DTOs.RuleDTO;
import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class SurveyQueries {

    private static class CreateSafeThreadSingleton {
        private static final SurveyQueries INSTANCE = new SurveyQueries();
    }

    public static SurveyQueries getInstance() {
        return SurveyQueries.CreateSafeThreadSingleton.INSTANCE;
    }

    public String check(){
        return "hello";
    }

    public Response<Boolean> insertSurvey(SurveyDTO surveyDTO) throws SQLException {
        Connect.createConnection();
        String sql = "INSERT INTO \"Surveys\" (id, title, description) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        preparedStatement.setString(1, surveyDTO.getId());
        preparedStatement.setString(2, surveyDTO.getTitle());
        preparedStatement.setString(3, surveyDTO.getDescription());
        int rows = preparedStatement.executeUpdate();
        insertQuestions(surveyDTO.getId(), surveyDTO.getQuestions());
        insertAnswers(surveyDTO.getId(), ListToString(surveyDTO.getAnswers()), surveyDTO.getTypes());
        Connect.closeConnection();
        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    private List<String> ListToString(List<List<String>> l){
        List<String> result = new LinkedList<>();

        for(List<String> list: l){
            result.add(list.toString());
        }
        return result;
    }

    public void insertQuestions (String surveyId, List<String> questions) throws SQLException {
        String sql = "INSERT INTO \"Questions\" (survey_id, index, question) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        for (String question : questions){
            preparedStatement.setString(1, surveyId);
            preparedStatement.setInt(2, questions.indexOf(question));
            preparedStatement.setString(3, question);
            preparedStatement.executeUpdate();
        }
    }

//    public static void insertAnswers (String surveyId, List<List<String>> answers, List<AnswerType> answerTypes) throws SQLException {
//        String sql = "INSERT INTO \"Answers\" (survey_id, question_index, answer_index, answer_type, answer) VALUES (?, ?, ?, ?, ?)";
//        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
//        for (List<String> answersList : answers){
//            for (String answer: answersList) {
//                preparedStatement.setString(1, surveyId);
//                preparedStatement.setInt(2, answers.indexOf(answersList));
//                preparedStatement.setInt(3, answersList.indexOf(answer));
//                preparedStatement.setString(4, answerTypes.get(answers.indexOf(answersList)).toString());
//                preparedStatement.setString(5, answer);
//                preparedStatement.executeUpdate();
//            }
//        }
//    }

    public void insertAnswers (String surveyId, List<String> answers, List<AnswerType> answerTypes) throws SQLException {
        String sql = "INSERT INTO \"Answers\" (survey_id, question_index, answer_type, answer) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        for (String answer: answers) {
            preparedStatement.setString(1, surveyId);
            preparedStatement.setInt(2, answers.indexOf(answer));
            preparedStatement.setString(3, answerTypes.get(answers.indexOf(answer)).toString());
            preparedStatement.setString(4, answer);
            preparedStatement.executeUpdate();
        }
    }

    public Response<SurveyDTO> getSurvey(String index) throws SQLException {
        Connect.createConnection();
        String sqlSurvey = "SELECT * FROM \"Surveys\" WHERE survey_id = ?";
        PreparedStatement statement = Connect.conn.prepareStatement(sqlSurvey);
        statement.setString(1, index);
        ResultSet resultSurvey = statement.executeQuery(sqlSurvey);
        SurveyDTO surveyDTO = new SurveyDTO();

        if(resultSurvey.next()) {
            surveyDTO.setId(resultSurvey.getString("survey_id"));
            surveyDTO.setTitle(resultSurvey.getString("title"));
            surveyDTO.setDescription(resultSurvey.getString("description"));

            Connect.closeConnection();
        }
        else {
            Connect.closeConnection();
            return new Response<>(null, true, "failed to get survey");
        }

        String sqlQue = "SELECT * FROM \"Questions\" WHERE survey_id = ?";
        // todo almog - load all the rest
        return new Response<SurveyDTO>(surveyDTO, false, "survey loaded successfully");
    }

    public void insertRule(String survey_id, int goalID, RuleDTO dto) {
        //todo - almog
    }

    public Response<Boolean> removeRule(int ruleID) {
        //todo - almog
        return null;
    }

    public List<Pair<RuleDTO, Integer>> getRules(String surveyIID) {
        //todo - almog
        return null;
    }

    public List<SurveyAnswersDTO> getAnswers(String surveyID) {
        //todo - almog
        return null;
    }

    public Map<String, List<SurveyAnswersDTO>> getAllAnswers() {
        //todo - almog
        return null;
    }

    public List<SurveyAnswersDTO> getAnswerForSurvey(String surveyId) {
        //todo - almog
        return null;
    }

    public List<String> getSurveyTitles(List<String> result) {
        //todo - almog
        return null;
    }
}