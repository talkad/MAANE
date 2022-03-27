package Persistence;

import java.sql.*;

public class Connect {
    static Connection conn;

    public static void createConnection()  {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/MaaneDb", "postgres", "12345");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void closeConnection() throws SQLException{
        conn.close();
    }

}

