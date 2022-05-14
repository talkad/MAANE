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

    public Response<Boolean> insertHolidaysDates(ArrayList<String[]> info, int year){
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"Holidays\" (title, date, year) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            for (String[] entry : info) {
                preparedStatement.setString(1, entry[0]);
                preparedStatement.setString(2, entry[1]);
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

            if (result.next()) {
                String title = result.getString("title");
                String date = result.getString("date");
                String [] arr = {title, date};
                output.add(arr);
                Connect.closeConnection();
            } else {
                Connect.closeConnection();
            }
        } catch (SQLException e) {e.printStackTrace();}
        return output;
    }

}
