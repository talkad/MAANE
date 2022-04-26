package Persistence;

import Communication.Initializer.ServerContextInitializer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    static Connection conn;
//    private static String url = "jdbc:postgresql://tai.db.elephantsql.com:5432/pxbghxfm";
//    private static String dbUsername = "pxbghxfm";
//    private static String dbPassword = "ogms2UJpzqjopRw29YcJ5Wau7wHQLkcJ";

    private static String url = "jdbc:postgresql://localhost:5432/maaneDBMock";;
    private static String dbUsername = "postgres";
    private static String dbPassword = "12345";


    //conn = DriverManager.getConnection("jdbc:postgresql://tai.db.elephantsql.com:5432/pxbghxfm", "pxbghxfm", "ogms2UJpzqjopRw29YcJ5Wau7wHQLkcJ");

    public static void createConnection()  {
        ServerContextInitializer initializer = ServerContextInitializer.getInstance();

        try {
            conn = DriverManager.getConnection(initializer.getDbConnection(), initializer.getDbUsername(), initializer.getDbPassword());

//            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MAANE", "postgres", "1234");
//            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MaaneDb", "postgres", "12345");
            //conn = DriverManager.getConnection(url, dbUsername, dbPassword);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void setRealDB(){
        url = "jdbc:postgresql://tai.db.elephantsql.com:5432/pxbghxfm";
        dbUsername = "pxbghxfm";
        dbPassword = "ogms2UJpzqjopRw29YcJ5Wau7wHQLkcJ";
    }

    public static void setMockDB(){
        url = "jdbc:postgresql://localhost:5432/maaneDBMock";
        dbUsername = "postgres";
        dbPassword = "12345";
    }



    public static void closeConnection() throws SQLException{
        conn.close();
    }

}

