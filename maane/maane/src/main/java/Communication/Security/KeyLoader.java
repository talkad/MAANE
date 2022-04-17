package Communication.Security;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;


@Slf4j
public class KeyLoader {

    private final String password;
    private final String filepath;
    private final String encryptedText;

    private static class CreateSafeThreadSingleton {
        private static final KeyLoader INSTANCE = new KeyLoader();
    }

    public KeyLoader(){
        password = "1234";
        filepath = "maane\\src\\main\\resources\\store.keystore";
        encryptedText = "354132168465432";
    }

    public static KeyLoader getInstance() {
        return KeyLoader.CreateSafeThreadSingleton.INSTANCE;
    }


    public void storeKey() {
        try {
            File file = new File(filepath);
            SecretKey secretKey = KeyLoader.getInstance().generateKey();
            KeyStore keystore = KeyStore.getInstance("JCEKS");

            if (!file.exists()) {
                keystore.load(null, null);
            }

            keystore.setKeyEntry("auth_key", secretKey, password.toCharArray(), null);
            OutputStream writeStream = new FileOutputStream(filepath);
            keystore.store(writeStream, password.toCharArray());

            log.info("key store created");

        }catch(Exception e){
            log.error("storing key failed");
            log.error(e.getMessage());
        }
    }

    private SecretKey readKey(){
        try{
            KeyStore keystore = KeyStore.getInstance("JCEKS");
            InputStream readStream = new FileInputStream(filepath);
            keystore.load(readStream, password.toCharArray());

            return (SecretKey) keystore.getKey("auth_key", password.toCharArray());

        }catch(Exception e){
            log.error("key from {} didn't load\n error: {}", filepath, e.getMessage());
            return null;
        }
    }

    private SecretKey generateKey(){
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            return generator.generateKey();

        }catch(Exception e){
            log.error("key generation failed\n error: {}", e.getMessage());
            return null;
        }

    }

    private byte[] encryptKey(String toEncrypt, SecretKey key, Cipher cipher){
        try{
            byte[] text = toEncrypt.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return cipher.doFinal(text);

        }catch(Exception  e){
            log.error("key encryption failed");
            return null;
        }

    }

    private byte[] decryptKey(byte[] enc, SecretKey key, Cipher cipher) {
        try{
            cipher.init(Cipher.DECRYPT_MODE, key);

            return cipher.doFinal(enc);

        }catch(Exception  e){
            log.error("key decryption failed");
            return null;
        }

    }

    public byte[] getEncryptionKey() {
        SecretKey key = KeyLoader.getInstance().readKey();

        try {
            Cipher cipher = Cipher.getInstance("AES");
            // byte[] dec = KeyLoader.getInstance().decryptKey(encryptedData, key, cipher);

            return KeyLoader.getInstance().encryptKey(encryptedText, key, cipher);

        }catch(Exception e){
            System.out.println("failed");
            return null;
        }

    }

}
