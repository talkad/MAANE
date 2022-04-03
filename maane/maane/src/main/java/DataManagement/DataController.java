package DataManagement;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DataController {

    private static class CreateSafeThreadSingleton {
        private static final DataController INSTANCE = new DataController();
    }

    public static DataController getInstance() {
        return DataController.CreateSafeThreadSingleton.INSTANCE;
    }

    public String loadPassword(String username){
        //todo - implement this function
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // todo tal
        return passwordEncoder.encode("1234");
    }

}