package Persistence;

import java.sql.*;

public class Connect {
    static Connection conn;

    public static void createConnection() throws SQLException {
        conn = DriverManager.getConnection("jdbc:postgresql://localhost/MaaneDb", "postgres", "12345");
    }

    public static void closeConnection() throws SQLException{
        conn.close();
    }

}

