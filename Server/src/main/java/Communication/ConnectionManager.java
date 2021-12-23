package Communication;

import Domain.CommonClasses.Response;
import Domain.DataManagement.SurveyController;

import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {

    private Map<Object, String> usernameMapping;

    private ConnectionManager(){
        usernameMapping = new HashMap<>();
    }

    private static class CreateSafeThreadSingleton {
        private static final ConnectionManager INSTANCE = new ConnectionManager();
    }

    public static ConnectionManager getInstance() {
        return ConnectionManager.CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<Boolean> addNewConnection(Object identifier, String username){
        usernameMapping.put(identifier, username);

        return new Response<>(true, false, "never fails");
    }

    public Response<Boolean> setUsername(Object identifier, String newUsername){
        if(!usernameMapping.containsKey(identifier))
            return new Response<>(false, true, "the identifier " +identifier+ " doesn't exists");

        usernameMapping.put(identifier, newUsername);
        return new Response<>(true, false, "OK");
    }

    public Response<Boolean> removeConnection(Object identifier){
        usernameMapping.remove(identifier);

        return new Response<>(true, false, "never fails");
    }

}
