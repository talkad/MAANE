package Persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    static Connection conn;
    private static String url = "jdbc:postgresql://localhost:5432/maaneDB";


    public static void createConnection()  {
        try {
            //conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAANE", "postgres", "1234");
//            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MaaneDb", "postgres", "12345");
            /* shaked's line probably ===> */conn = DriverManager.getConnection(url, "postgres", "123456");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void setRealUrl(){
        url = "jdbc:postgresql://localhost:5432/maaneDB";
    }

    public static void setMockUrl(){
        url = "jdbc:postgresql://localhost:5432/maaneDBMock";
    }

    public static void closeConnection() throws SQLException{
        conn.close();
    }

}

