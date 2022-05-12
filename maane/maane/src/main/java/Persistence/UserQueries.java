package Persistence;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserStateEnum;
import Persistence.DbDtos.UserDBDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
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

    /*public List<String> getFullUsers() {
        Connect.createConnection();
        String userSql = "SELECT * FROM \"Users\"";
        String userSchoolsSql = "SELECT * FROM \"UsersSchools\"";
        String userAppointmentsSql = "SELECT appointee FROM \"Appointments\"";

        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(userSql);

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
    }*/

    public Response<UserDBDTO> getFullUser(String username) {
        Connect.createConnection();
        String userSql = "SELECT * FROM \"Users\" WHERE username = ?";//todo make into one query
        String userSchoolsSql = "SELECT school FROM \"UsersSchools\" WHERE username = ?";
        String userAppointmentsSql = "SELECT appointee FROM \"Appointments\" WHERE appointor = ?";
        String userSurveysSql = "SELECT surveyid FROM \"UsersSurveys\" WHERE username = ?";
        String userWorkPlans = "SELECT year FROM \"WorkPlans\" WHERE username = ? GROUP BY year";

        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(userSql);

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
                userDBDTO.setPassword(result.getString("password"));
/*                if(userDBDTO.userStateEnum.equals(UserStateEnum.SUPERVISOR)){
                    userDBDTO.setSurveys(getUserSurveys(result.getString("username")));
                }*///todo either use function or convert schools and appointments to functions as well
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

                statement = Connect.conn.prepareStatement(userSurveysSql);
                statement.setString(1, username);
                result = statement.executeQuery();
                List<String> surveys = new Vector<>();
                while (result.next()){
                    surveys.add(result.getString(1));
                }
                userDBDTO.setSurveys(surveys);

                statement = Connect.conn.prepareStatement(userWorkPlans);
                statement.setString(1, username);
                result = statement.executeQuery();
                List<Integer> workPlansYears = new Vector<>();
                while (result.next()){
                    workPlansYears.add(result.getInt(1));
                }
                userDBDTO.setWorkPlanYears(workPlansYears);

                Connect.closeConnection();
                return new Response<>(userDBDTO, false, "successfully acquired user");
            }else {
                System.out.println(username);
            }

            Connect.closeConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Response<>(null, true, "failed to get user");
    }

    private List<String> getUserSurveys (String username) {
        String query = "SELECT * FROM \"UsersSurveys\" WHERE username = ?";
        PreparedStatement statement = null;
        try {
            statement = Connect.conn.prepareStatement(query);

        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        List<String> surveys = new LinkedList<>();

        while (result.next()) {
            String survey = result.getString("surveyid");
            surveys.add(survey);
        }
        return surveys;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Vector<>();//todo maybe return null
    }

    public Response<UserDBDTO> getUser(String username) {//todo remove it later potentially
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
            if(userDBDTO.getStateEnum().equals(UserStateEnum.SUPERVISOR)){
                for(String survey : userDBDTO.getSurveys()){
                    insertUserSurveys(userDBDTO.getUsername(), survey);
                }
            }
            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    private void insertUserSurveys (String username, String surveyID){
        String sql = "INSERT INTO \"UsersSurveys\"(username, surveyid) VALUES (?, ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, surveyID);
            preparedStatement.executeUpdate();
        }
        catch (SQLException throwables) {throwables.printStackTrace();}
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

    public Response<Boolean> updateUserInfo(UserDBDTO userDBDTO){
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
                new Response<>(false, true, "failed to write to db");
    }

    public Response<Boolean> updateUserPassword(String username, String password){
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
        String sql = "DELETE FROM \"Users\" WHERE username = ?";//todo see if its possible to make it as one query
        String sqlDeleteSchools = "DELETE FROM \"UsersSchools\" WHERE username = ?";
        String sqlDeleteAppointments = "DELETE FROM \"Appointments\" WHERE appointor = ?";
        String sqlDeleteSurveys = "DELETE FROM \"UsersSurveys\" WHERE username = ?";


        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            rows = preparedStatement.executeUpdate();

            preparedStatement = Connect.conn.prepareStatement(sqlDeleteSchools);
            preparedStatement.setString(1, username);
            /*rows = */preparedStatement.executeUpdate();

            preparedStatement = Connect.conn.prepareStatement(sqlDeleteAppointments);
            preparedStatement.setString(1, username);
            /*rows = */preparedStatement.executeUpdate();//todo not sure if should update failure here for the admin user removal case

            preparedStatement = Connect.conn.prepareStatement(sqlDeleteSurveys);
            preparedStatement.setString(1, username);
            /*rows = */preparedStatement.executeUpdate();//todo not sure if should update failure here for the admin user removal case

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
        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(sql);

        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        if(result.next()) {
            String password = result.getString("password");
            Connect.closeConnection();
            return new Response<>(password, false, "successfully acquired password");
        }
        Connect.closeConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Response<>(null, true, "failed to acquire password");
    }

    public Boolean userExists(String username) {
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
        String sql = "DELETE FROM \"UsersSchools\" WHERE username = ?";
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


    // for end2end testing (mock mode)
    public void clearDB() {
        Connect.createConnection();
        String sql = "TRUNCATE \"Answers\", \"Appointments\", \"MultiChoices\"" +
                ", \"Questions\", \"Surveys\", \"Rules\", \"Users\"" +
                ", \"UsersSchools\", \"UsersSurveys\", \"Goals\"";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.executeUpdate();

            Connect.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Response<String> addSurvey(String username, String surveyId) {
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"UsersSurveys\"(username, surveyid) VALUES (?, ?)";
        PreparedStatement preparedStatement;
        try {//todo check user actually exists
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, surveyId);
            rows = preparedStatement.executeUpdate();

            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(surveyId, false, "") :
                new Response<>(null, true, "bad Db writing");
    }

    public void updateSurveys(String username, List<String> surveys) {
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"UsersSurveys\"(username, surveyid) VALUES (?, ?)";
        PreparedStatement preparedStatement;
        try {//todo check user actually exists
            preparedStatement = Connect.conn.prepareStatement(sql);
            for (String surveyid: surveys) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, surveyid);
                rows = preparedStatement.executeUpdate();
            }

            Connect.closeConnection();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
/*        return rows > 0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");*/
    }


    public Response<Boolean> removeCoordinator(String workField, String school) {
        Connect.createConnection();
        int rows = 0;//todo add cascade to all tables with foreign keys
        String sqlName = "SELECT \"Users\".username FROM (\"Users\" JOIN \"UsersSchools\" ON \"Users\".username=\"UsersSchools\".username) WHERE (workfield = ? AND (school = ? AND userstateenum = ?))";

        String sqlToDelete = "BEGIN;\n";
        sqlToDelete += "DELETE FROM \"Users\" WHERE username = ?;\n";
        sqlToDelete += "DELETE FROM \"UsersSchools\" WHERE username = ?;\n";
        sqlToDelete += "COMMIT;\n";

        String coordinatorName = "";
        //todo fix it with cascade
//todo also remove schools
        // todo : this is not working regardless of the cascading

        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(sqlName);

            statement.setString(1, workField);
            statement.setString(2, school);
            statement.setString(3, "COORDINATOR");

            ResultSet result = statement.executeQuery();
            if(result.next()) {
                coordinatorName = result.getString("username");
            }
            else{
                Connect.closeConnection();
                new Response<>(false, true, "no such coordinator");
            }

            statement = Connect.conn.prepareStatement(sqlToDelete);
            statement.setString(1, coordinatorName);
            statement.setString(2, coordinatorName);
            rows = statement.executeUpdate();
            Connect.closeConnection();
            return new Response<>(true, false, "");
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows > 0 ? new Response<>(true, false, "") ://todo fix rows
                new Response<>(false, true, "bad Db writing");
    }

    public Response<List<String>> getCoordinatorEmails(String workField) {
        Connect.createConnection();
        int rows = 0;//todo add cascade to all tables with foreign keys
        String sql = "SELECT email FROM \"Users\" WHERE workfield = ? AND userstateenum = ?";
        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(sql);

            statement.setString(1, workField);
            statement.setString(2, UserStateEnum.COORDINATOR.getState());

            ResultSet result = statement.executeQuery();
            List<String> emails = new Vector<>();
            while(result.next()) {
                emails.add(result.getString("email"));
            }
            Connect.closeConnection();
            return new Response<>(emails, false, "successfully acquired emails");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Response<>(null, true, "failed to acquire emails");
    }

    public Response<UserDBDTO> getCoordinator(String symbol, String workField) {
        Connect.createConnection();
        String sql = "SELECT \"Users\".firstname, \"Users\".lastname, \"Users\".phonenumber, \"Users\".email FROM (\"Users\" JOIN \"UsersSchools\" ON \"Users\".username=\"UsersSchools\".username) WHERE (workfield = ? AND (school = ? AND userstateenum = ?))";
        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(sql);

            statement.setString(1, workField);
            statement.setString(2, symbol);
            statement.setString(3,"COORDINATOR");

            ResultSet result = statement.executeQuery();
            if(result.next()) {
                UserDBDTO userDBDTO = new UserDBDTO();
                userDBDTO.setFirstName(result.getString("firstname"));
                userDBDTO.setLastName(result.getString("lastname"));
                userDBDTO.setPhoneNumber(result.getString("phonenumber"));
                userDBDTO.setEmail(result.getString("email"));
                Connect.closeConnection();
                return new Response<>(userDBDTO, false, "successfully acquired coordinator");
            }
            else{
                Connect.closeConnection();
                return new Response<>(null, false, "no coordinator was assigned to the school");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Response<>(null, true, "failed to acquire coordinator");
    }

    public Response<List<String>> getAllWorkFields(List<String> supervisors) {
        Connect.createConnection();
        String sql = "SELECT workfield FROM \"Users\" WHERE username = ?";
        PreparedStatement statement;
        List<String> workFields = new Vector<>();;
        try {
            statement = Connect.conn.prepareStatement(sql);
            for(String supervisor : supervisors){
                statement.setString(1, supervisor);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    workFields.add(resultSet.getString("workfield"));
                }
            }
            Connect.closeConnection();
            return new Response<>(workFields, false, "successfully acquired all workfields");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Response<>(null, true, "failed to acquire workFields");
    }
}
