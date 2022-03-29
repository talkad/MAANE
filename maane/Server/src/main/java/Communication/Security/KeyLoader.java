package Communication.Security;

import java.nio.charset.StandardCharsets;

public class KeyLoader {

    private static class CreateSafeThreadSingleton {
        private static final KeyLoader INSTANCE = new KeyLoader();
    }

    public static KeyLoader getInstance() {
        return KeyLoader.CreateSafeThreadSingleton.INSTANCE;
    }

    public byte[] readKey(String filename){
        return "secret".getBytes(StandardCharsets.UTF_8);
    }
}
