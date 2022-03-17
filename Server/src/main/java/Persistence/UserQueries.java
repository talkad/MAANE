package Persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserQueries {
    public static void getUsers () throws SQLException {
        Connect.createConnection();
        String sql = "SELECT * FROM \"Users\"";
        Statement statement = Connect.conn.createStatement();
        ResultSet result = statement.executeQuery(sql);
        while (result.next()){
            String email = result.getString("email");
            String username = result.getString("username");
            System.out.println(email + " " + username);
        }
        Connect.closeConnection();
    }

    public static void main(String [] args) throws SQLException {
        getUsers();
    }
}
