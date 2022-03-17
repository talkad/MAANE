package Persistence;

import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SurveyQueries {
    public static Response<Boolean> insertSurvey(SurveyDTO surveyDTO) throws SQLException {
        Connect.createConnection();
        String sql = "INSERT INTO \"Surveys\" (id, title, description) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        preparedStatement.setInt(1, surveyDTO.getId());
        preparedStatement.setString(2, surveyDTO.getTitle());
        preparedStatement.setString(3, surveyDTO.getDescription());
        int rows = preparedStatement.executeUpdate();
        Connect.closeConnection();
        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }
}
