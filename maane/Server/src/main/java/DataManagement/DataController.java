package DataManagement;


public class DataController {

    private static class CreateSafeThreadSingleton {
        private static final DataController INSTANCE = new DataController();
    }

    public static DataController getInstance() {
        return DataController.CreateSafeThreadSingleton.INSTANCE;
    }

    public String loadPassword(String username){
        //todo - implement this function
        return "1234";
    }

}
