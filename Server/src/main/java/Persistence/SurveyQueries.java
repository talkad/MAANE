package Persistence;

import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SurveyQueries {
    public static Response<Boolean> insertSurvey(SurveyDTO surveyDTO) throws SQLException {
        Connect.createConnection();
        String sql = "INSERT INTO \"Surveys\" (id, title, description) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        preparedStatement.setInt(1, surveyDTO.getId());
        preparedStatement.setString(2, surveyDTO.getTitle());
        preparedStatement.setString(3, surveyDTO.getDescription());
        int rows = preparedStatement.executeUpdate();
        insertQuestions(surveyDTO.getId(), surveyDTO.getQuestions());
        insertAnswers(surveyDTO.getId(), surveyDTO.getAnswers(), surveyDTO.getTypes());
        Connect.closeConnection();
        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    private static void insertQuestions (int surveyId, List<String> questions) throws SQLException {
        String sql = "INSERT INTO \"Questions\" (survey_id, question) VALUES (?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        for (String question : questions){
            preparedStatement.setInt(1, surveyId);
            preparedStatement.setString(2, question);
            preparedStatement.executeUpdate();
        }
    }

    private static void insertAnswers (int surveyId, List<List<String>> answers, List<AnswerType> answerTypes){
//        for (String answers : questions){
//            preparedStatement.setInt(1, surveyId);
//            preparedStatement.setString(2, question);
//            preparedStatement.executeUpdate();
//        }
    }
}
