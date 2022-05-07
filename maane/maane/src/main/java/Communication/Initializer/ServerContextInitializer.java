package Communication.Initializer;

public class ServerContextInitializer {

    private boolean mockMode;
    private String dbConnection;
    private String dbUsername;
    private String dbPassword;

    private static class CreateSafeThreadSingleton {
        private static final ServerContextInitializer INSTANCE = new ServerContextInitializer();
    }

    public static ServerContextInitializer getInstance() {
        return ServerContextInitializer.CreateSafeThreadSingleton.INSTANCE;
    }

    public ServerContextInitializer() {
        this.mockMode = false;
        this.dbConnection = "jdbc:postgresql://tai.db.elephantsql.com:5432/pxbghxfm";
        this.dbUsername = "pxbghxfm";
        this.dbPassword = "ogms2UJpzqjopRw29YcJ5Wau7wHQLkcJ";
    }

    public void setMockMode() {
        this.mockMode = true;

        this.dbConnection = "jdbc:postgresql://localhost:5432/maaneDBMock";
        this.dbUsername = "postgres";
        this.dbPassword = "1234";

//        // todo - change it to the mock db params
//        this.dbConnection = "jdbc:postgresql://localhost:5432/MAANE";
//        this.dbUsername = "postgres";
//        this.dbPassword = "1234";
    }

    public boolean isMockMode(){
        return this.mockMode;
    }

    public String getDbConnection() {
        return dbConnection;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }
}