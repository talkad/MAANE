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
        preparedStatement.setString(1, surveyDTO.getId());
        preparedStatement.setString(2, surveyDTO.getTitle());
        preparedStatement.setString(3, surveyDTO.getDescription());
        int rows = preparedStatement.executeUpdate();
        insertQuestions(surveyDTO.getId(), surveyDTO.getQuestions());
        insertAnswers(surveyDTO.getId(), surveyDTO.getAnswers(), surveyDTO.getTypes());
        Connect.closeConnection();
        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    private static void insertQuestions (String surveyId, List<String> questions) throws SQLException {
        String sql = "INSERT INTO \"Questions\" (survey_id, index, question) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        for (String question : questions){
            preparedStatement.setString(1, surveyId);
            preparedStatement.setInt(2, questions.indexOf(question));
            preparedStatement.setString(3, question);
            preparedStatement.executeUpdate();
        }
    }

    private static void insertAnswers (String surveyId, List<List<String>> answers, List<AnswerType> answerTypes) throws SQLException {
        String sql = "INSERT INTO \"Answers\" (survey_id, question_index, answer_index, answer_type, answer) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        for (List<String> answersList : answers){
            for (String answer: answersList) {
                preparedStatement.setString(1, surveyId);
                preparedStatement.setInt(2, answers.indexOf(answersList));
                preparedStatement.setInt(3, answersList.indexOf(answer));
                preparedStatement.setString(4, answerTypes.get(answers.indexOf(answersList)).toString());
                preparedStatement.setString(5, answer);
                preparedStatement.executeUpdate();
            }
        }
    }
}
