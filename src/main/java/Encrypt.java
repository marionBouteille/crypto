import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Encrypt {

    public static SecretKey generateKey() throws NoSuchAlgorithmException {

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize, secureRandom);

        SecretKey secretKey = keyGenerator.generateKey();

        return secretKey;
    }

    public static byte[] generateIv() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        return iv;
    }

    public static void cipherTextStealing(String message, byte[] IV){

        byte[] input = message.getBytes();

        int blocks = message.length()/16;

        byte[] init = IV;

        for(int i =0; i < blocks; i++){

            init[i] = (byte) (input[i]^init[i]);
        }

        int lastBlock = blocks - 1;

    }


}
