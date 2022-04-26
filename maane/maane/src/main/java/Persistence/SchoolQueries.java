package Persistence;

import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Persistence.DbDtos.SchoolDBDTO;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class SchoolQueries {

    private static class CreateSafeThreadSingleton {
        private static final SchoolQueries INSTANCE = new SchoolQueries();
    }

    public static SchoolQueries getInstance() {
        return SchoolQueries.CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<Boolean> insertSchool(SchoolDBDTO school){
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"Schools\" (symbol, name, city, city_mail, address, school_address, principal," +
                " manager, supervisor, phone, mail, zipcode, education_stage, education_type, supervisor_type, spector," +
                " num_of_students ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.getInteger(school.getSymbol()));
            preparedStatement.setString(2, school.getName());
            preparedStatement.setString(3, school.getCity());
            preparedStatement.setString(4, school.getCity_mail());
            preparedStatement.setString(5, school.getAddress());
            preparedStatement.setString(6, school.getSchool_address());
            preparedStatement.setString(7, school.getPrincipal());
            preparedStatement.setString(8, school.getManager());
            preparedStatement.setString(9, school.getSupervisor());
            preparedStatement.setString(10, school.getPhone());
            preparedStatement.setString(11, school.getMail());
            preparedStatement.setInt(12, school.getZipcode());
            preparedStatement.setString(13, school.getEducation_stage());
            preparedStatement.setString(14, school.getEducation_type());
            preparedStatement.setString(15, school.getSupervisor_type());
            preparedStatement.setString(16, school.getSpector());
            preparedStatement.setInt(17, school.getNum_of_students());

            rows = preparedStatement.executeUpdate();
            Connect.closeConnection();
        } catch (SQLException e) {e.printStackTrace();}

        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");

    }

    public Response<Boolean> removeSchool(String symbol){
        Connect.createConnection();
        String sql = "DELETE FROM \"Schools\" WHERE symbol = ?";
        try (PreparedStatement pstmt = Connect.conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.getInteger(symbol));
            pstmt.executeUpdate();
            Connect.closeConnection();
            return new Response<>(true, false, "removed school successfully");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

            return new Response<>(false, true, "school removal failed");
        }
    }

    public Response<Boolean> updateSchool (String symbol, SchoolDBDTO school){
        Connect.createConnection();
        String sql = "UPDATE \"Schools\" SET symbol = ?, name = ?, city = ?, city_mail = ?," +
                "address = ?, school_address = ?, principal = ?, manager = ?, supervisor = ?, " +
                "phone = ?, mail = ?, zipcode = ?, education_stage = ?, education_type = ?, " +
                "supervisor_type = ?, spector = ?, num_of_students WHERE symbol = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.getInteger(school.getSymbol()));
            preparedStatement.setString(2, school.getName());
            preparedStatement.setString(3, school.getCity());
            preparedStatement.setString(4, school.getCity_mail());
            preparedStatement.setString(5, school.getAddress());
            preparedStatement.setString(6, school.getSchool_address());
            preparedStatement.setString(7, school.getPrincipal());
            preparedStatement.setString(8, school.getManager());
            preparedStatement.setString(9, school.getSupervisor());
            preparedStatement.setString(10, school.getPhone());
            preparedStatement.setString(11, school.getMail());
            preparedStatement.setInt(12, school.getZipcode());
            preparedStatement.setString(13, school.getEducation_stage());
            preparedStatement.setString(14, school.getEducation_type());
            preparedStatement.setString(15, school.getSupervisor_type());
            preparedStatement.setString(16, school.getSpector());
            preparedStatement.setInt(17, school.getNum_of_students());
            preparedStatement.setInt(18, Integer.getInteger(symbol));

            preparedStatement.executeUpdate();
            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
            return new Response<>(false, true, "update school failed");
        }

        return new Response<>(true, false, "updated school successfully");
    }

    public boolean schoolSymbolExists (String symbol){
        Connect.createConnection();
        String sql = "SELECT exists (SELECT 1 FROM \"Schools\" WHERE symbol = ? LIMIT 1)";
        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(sql);

            statement.setInt(1, Integer.getInteger(symbol));
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                boolean found = result.getBoolean(1);
                Connect.closeConnection();
                return found;
            }
            Connect.closeConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean schoolNameExists (String name){
        Connect.createConnection();
        String sql = "SELECT exists (SELECT 1 FROM \"Schools\" WHERE name = ? LIMIT 1)";
        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(sql);

            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                boolean found = result.getBoolean(1);
                Connect.closeConnection();
                return found;
            }
            Connect.closeConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    //for test purposes only
    public void deleteSchools(){
        Connect.createConnection();
        String sql = "TRUNCATE \"Schools\"";

        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.executeUpdate();

            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
