package Persistence;

import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserStateEnum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class UserQueries {
    private UserQueries() {

    }

    private static class CreateSafeThreadSingleton {
        private static final UserQueries INSTANCE = new UserQueries();
    }

    public static UserQueries getInstance() {
        return CreateSafeThreadSingleton.INSTANCE;
    }

    public List<String> getUsers() {
        Connect.createConnection();
        String sql = "SELECT username FROM \"Users\"";
        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(sql);

            ResultSet result = statement.executeQuery();
            List<String> usernames = new Vector<>();
            while (result.next()){
                usernames.add(result.getString(1));
            }
            Connect.closeConnection();
            return usernames;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public Response<UserDBDTO> getFullUser(String username) {
        Connect.createConnection();
        String userSql = "SELECT * FROM \"Users\" WHERE username = ?";
        String userSchoolsSql = "SELECT school FROM \"UsersSchools\" WHERE username = ?";
        String userAppointmentsSql = "SELECT appointee FROM \"Appointments\" WHERE appointor = ?";

        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(userSql);

            statement.setString(1, username);
            ResultSet result = statement.executeQuery();//todo test it
            if(result.next()) {
                UserDBDTO userDBDTO = new UserDBDTO();
                userDBDTO.setUsername(result.getString("username"));
                userDBDTO.setStateEnum(UserStateEnum.valueOf(result.getString("userstateenum")));
                userDBDTO.setWorkField(result.getString("workField"));
                userDBDTO.setFirstName(result.getString("firstName"));
                userDBDTO.setLastName(result.getString("lastName"));
                userDBDTO.setEmail(result.getString("email"));
                userDBDTO.setPhoneNumber(result.getString("phoneNumber"));
                userDBDTO.setCity(result.getString("city"));
                userDBDTO.setPassword(result.getString("password"));


                statement = Connect.conn.prepareStatement(userSchoolsSql);
                statement.setString(1, username);
                result = statement.executeQuery();
                List<String> schools = new Vector<>();
                while (result.next()){
                    schools.add(result.getString(1));
                }
                userDBDTO.setSchools(schools);

                statement = Connect.conn.prepareStatement(userAppointmentsSql);
                statement.setString(1, username);
                result = statement.executeQuery();
                List<String> appointments = new Vector<>();
                while (result.next()){
                    appointments.add(result.getString(1));
                }
                userDBDTO.setAppointments(appointments);

                Connect.closeConnection();
                return new Response<>(userDBDTO, false, "successfully acquired user");
            }
            Connect.closeConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Response<>(null, true, "failed to get user");
    }

    public Response<UserDBDTO> getUser(String username) {
        Connect.createConnection();
        String sql = "SELECT * FROM \"Users\" WHERE username = ?";
        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(sql);

        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        if(result.next()) {
            UserDBDTO userDBDTO = new UserDBDTO();
            userDBDTO.setUsername(result.getString("username"));
            userDBDTO.setStateEnum(UserStateEnum.valueOf(result.getString("userstateenum")));
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Response<>(null, true, "failed to get user");
    }

    public Response<Boolean> insertUser(UserDBDTO userDBDTO){
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"Users\"(username, userstateenum, workfield , firstname , lastname , email,  phonenumber, city, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            preparedStatement.setString(9, userDBDTO.getPassword());
            rows = preparedStatement.executeUpdate();
            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public Response<Boolean> assignSchoolsToUser(String username, List<String> schools){
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"UsersSchools\"(username, school) VALUES (?, ?)";
        PreparedStatement preparedStatement;
        try {//todo check user actually exists
            preparedStatement = Connect.conn.prepareStatement(sql);
            for (String school: schools) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, school);
                rows = preparedStatement.executeUpdate();
            }

            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public Response<Boolean> removeSchoolsFromUser(String username, List<String> schools){
        Connect.createConnection();
        int rows = 0;
        String sql = "DELETE FROM \"UsersSchools\" WHERE username = ? AND school = ?";
        PreparedStatement preparedStatement;
        try {//todo check user actually exists
            preparedStatement = Connect.conn.prepareStatement(sql);
            for (String school: schools) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, school);
                rows = preparedStatement.executeUpdate();
            }

            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public Response<Boolean> addAppointment(String username, String appointee){
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"Appointments\"(appointor, appointee) VALUES (?, ?)";
        PreparedStatement preparedStatement;
        try {//todo check user actually exists
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, appointee);
            rows = preparedStatement.executeUpdate();

            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public Response<Boolean> removeAppointment(String username, String appointee){
        Connect.createConnection();
        int rows = 0;
        String sql = "DELETE FROM \"Appointments\" WHERE appointor = ? AND appointee = ?";
        PreparedStatement preparedStatement;
        try {//todo check user actually exists
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, appointee);
            rows = preparedStatement.executeUpdate();

            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public Response<Boolean> updateUserInfo(UserDBDTO userDBDTO){//todo test it
        Connect.createConnection();
        int rows = 0;
        String sql = "UPDATE \"Users\" SET firstName = ?, lastName = ?, email = ?, phoneNumber = ?, city = ? WHERE username = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);

            preparedStatement.setString(1, userDBDTO.getFirstName());
            preparedStatement.setString(2, userDBDTO.getLastName());
            preparedStatement.setString(3, userDBDTO.getEmail());
            preparedStatement.setString(4, userDBDTO.getPhoneNumber());
            preparedStatement.setString(5, userDBDTO.getCity());
            preparedStatement.setString(6, userDBDTO.getUsername());

            rows = preparedStatement.executeUpdate();
            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public Response<Boolean> updateUserPassword(String username, String password){//todo test it
        Connect.createConnection();
        int rows = 0;
        String sql = "UPDATE \"Users\" SET password = ? WHERE username = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);

            preparedStatement.setString(1, password);
            preparedStatement.setString(2, username);

            rows = preparedStatement.executeUpdate();
            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public Response<Boolean> removeUser(String username){
        Connect.createConnection();
        int rows = 0;
        String sql = "DELETE FROM \"Users\" WHERE username = ?";//todo remove everything related to the user - schools appointees and etc
        String sqlDeleteSchools = "DELETE FROM \"UsersSchools\" WHERE username = ?";//todo remove everything related to the user - schools appointees and etc
        String sqlDeleteAppointments = "DELETE FROM \"Appointments\" WHERE appointor = ?";//todo remove everything related to the user - schools appointees and etc

        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            rows = preparedStatement.executeUpdate();

            preparedStatement = Connect.conn.prepareStatement(sqlDeleteSchools);
            preparedStatement.setString(1, username);
            rows = preparedStatement.executeUpdate();

            preparedStatement = Connect.conn.prepareStatement(sqlDeleteAppointments);
            preparedStatement.setString(1, username);
            rows = preparedStatement.executeUpdate();

            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public Response<String> getPassword(String username) {
        Connect.createConnection();
        String sql = "SELECT * FROM \"Users\" WHERE username = ?";
        PreparedStatement statement = null;
        try {
            statement = Connect.conn.prepareStatement(sql);

        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        if(result.next()) {
            String password = result.getString("password");
            Connect.closeConnection();
            return new Response<>(password, false, "successfully acquired password");//todo
        }
        Connect.closeConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Response<>(null, true, "failed to acquire password");//todo
    }

    public Boolean userExists(String username) {//todo test it
        Connect.createConnection();
        String sql = "SELECT exists (SELECT 1 FROM \"Users\" WHERE username = ? LIMIT 1)";
        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(sql);

            statement.setString(1, username);
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


    public void deleteUsers(){
        Connect.createConnection();
        String sql = "TRUNCATE \"Users\", \"Appointments\", \"UsersSchools\"";

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

    public void resetSchools(String username) {
        Connect.createConnection();
        int rows = 0;
        String sql = "DELETE * FROM \"Users\" WHERE username = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);

            preparedStatement.setString(1, username);

            rows = preparedStatement.executeUpdate();
            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }//todo
/*        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");*/
    }

    public void updateUserState(String username, String stateEnum) {
        Connect.createConnection();
        int rows = 0;
        String sql = "UPDATE \"Users\" SET userstateenum = ? WHERE username = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);

            preparedStatement.setString(1, stateEnum);
            preparedStatement.setString(2, username);

            rows = preparedStatement.executeUpdate();
            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
/*        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");*/
    }
}
