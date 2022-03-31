package Persistence;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.DataController;
import Domain.DataManagement.SurveyController;
import Domain.EmailManagement.EmailController;
import Domain.UsersManagment.Security;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.GoalsManagement;

import java.sql.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserQueries {

    private UserQueries() {

    }

    private static class CreateSafeThreadSingleton {
        private static final UserQueries INSTANCE = new UserQueries();
    }

    public static UserQueries getInstance() {
        return UserQueries.CreateSafeThreadSingleton.INSTANCE;
    }

    public void getUsers() throws SQLException {
        Connect.createConnection();
        String sql = "SELECT * FROM \"Users\"";
        Statement statement = Connect.conn.createStatement();
        ResultSet result = statement.executeQuery(sql);
        while (result.next()){
            String username = result.getString("username");
            System.out.println(username);
        }
        Connect.closeConnection();
    }

    public Response<UserDBDTO> getUser(String username) throws SQLException {
        Connect.createConnection();
        String sql = "SELECT * FROM \"Users\" WHERE username = ?";
        PreparedStatement statement = Connect.conn.prepareStatement(sql);
        statement.setString(1, username);
        ResultSet result = statement.executeQuery(sql);
        if(result.next()) {
            UserDBDTO userDBDTO = new UserDBDTO();
            userDBDTO.setUsername(result.getString("username"));
            userDBDTO.setStateEnum(UserStateEnum.valueOf(result.getString("stateEnum")));
            userDBDTO.setWorkField(result.getString("workField"));
            userDBDTO.setFirstName(result.getString("firstName"));
            userDBDTO.setLastName(result.getString("lastName"));
            userDBDTO.setEmail(result.getString("email"));
            userDBDTO.setPhoneNumber(result.getString("phoneNumber"));
            userDBDTO.setCity(result.getString("city"));
            Connect.closeConnection();
            return new Response<>(userDBDTO, false, "successfully acquired user");
        }
        Connect.closeConnection();
        return new Response<>(null, true, "failed to get user");
    }

    public Response<Boolean> insertUser(UserDBDTO userDBDTO){
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"Users\"(username, userstateenum, workfield , firstname , lastname , email,  phonenumber, city) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);

            preparedStatement.setString(1, userDBDTO.getUsername());
            preparedStatement.setString(2, userDBDTO.getStateEnum().getState());
            preparedStatement.setString(3, userDBDTO.getWorkField());
            preparedStatement.setString(4, userDBDTO.getFirstName());
            preparedStatement.setString(5, userDBDTO.getLastName());
            preparedStatement.setString(6, userDBDTO.getEmail());
            preparedStatement.setString(7, userDBDTO.getPhoneNumber());
            preparedStatement.setString(8, userDBDTO.getCity());
            rows = preparedStatement.executeUpdate();
            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public Response<Boolean> insertUserPassword(String username, String password) throws SQLException {
        Connect.createConnection();
        String sql = "INSERT INTO \"Passwords\" (username, city) VALUES (?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        int rows = preparedStatement.executeUpdate();
        Connect.closeConnection();
        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public Response<Pair<String, String>> getPassword(String username) throws SQLException {
        Connect.createConnection();
        String sql = "SELECT * FROM \"Passwords\" WHERE username = ?";
        PreparedStatement statement = Connect.conn.prepareStatement(sql);
        statement.setString(1, username);
        ResultSet result = statement.executeQuery(sql);
        if(result.next()) {
            Pair<String, String> nameAndPassword = new Pair<>(result.getString("username"), result.getString("password"));
            Connect.closeConnection();
            return new Response<>(nameAndPassword, false, "success");//todo
        }
        Connect.closeConnection();
        return new Response<>(null, true, "fail");//todo
    }

}
