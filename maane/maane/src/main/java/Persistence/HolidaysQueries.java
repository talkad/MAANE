package Persistence;
import Domain.CommonClasses.Response;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class HolidaysQueries {

    private static class CreateSafeThreadSingleton {
        private static final HolidaysQueries INSTANCE = new HolidaysQueries();
    }

    public static HolidaysQueries getInstance() {
        return HolidaysQueries.CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<Boolean> insertHolidaysDates(ArrayList<String[]> info){
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"Holidays\" (title, date, year) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            for (String[] entry : info) {
                preparedStatement.setString(1, entry[0]);
                preparedStatement.setString(2, entry[1]);
                int year = Integer.parseInt(entry[1].substring(0,4));
                preparedStatement.setInt(3, year);
                rows += preparedStatement.executeUpdate();
            }

            Connect.closeConnection();
        } catch (SQLException e) {e.printStackTrace();}

        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");

    }

    public ArrayList<String[]> getHolidaysDates (int year) {
        Connect.createConnection();
        String sql = "SELECT * FROM \"Holidays\" WHERE year = ?";
        PreparedStatement statement;
        ArrayList<String[]> output = new ArrayList<>();
        try {
            statement = Connect.conn.prepareStatement(sql);
            statement.setInt(1, year);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String title = result.getString("title");
                String date = result.getString("date");
                String [] arr = {title, date, year+""};
                output.add(arr);
            }

            Connect.closeConnection();

        } catch (SQLException e) {e.printStackTrace();}
        return output;
    }

    public boolean holidaysForYearExists (int year){
        Connect.createConnection();
        String sql = "SELECT exists (SELECT 1 FROM \"Holidays\" WHERE year = ? LIMIT 1)";
        PreparedStatement statement;
        try {
            statement = Connect.conn.prepareStatement(sql);
            statement.setInt(1, year);
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

    public void clearHolidays(){
        Connect.createConnection();
        String sql = "TRUNCATE \"Holidays\"";

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
