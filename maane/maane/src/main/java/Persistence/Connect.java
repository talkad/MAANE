package Persistence;

import Communication.Initializer.ServerContextInitializer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    static Connection conn;

    public static void createConnection()  {
        ServerContextInitializer initializer = ServerContextInitializer.getInstance();

        try {
            conn = DriverManager.getConnection(initializer.getDbConnection(), initializer.getDbUsername(), initializer.getDbPassword());
//            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MaaneDb", "postgres", "12345");
            // shaked's line probably ===> conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/maaneDB", "postgres", "123456");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void closeConnection() throws SQLException{
        conn.close();
    }

}

