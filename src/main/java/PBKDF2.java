import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

public class PBKDF2 {

    private static final Random RANDOM = new SecureRandom();

    public static void main(String[] args) throws Exception {
        byte[] hashBytes = hashPassword("ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789");

        System.out.println(Arrays.toString(hashBytes));
    }
    public static byte[] getNextSalt() {
        byte[] salt = new byte[64];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public static boolean testCharacters(String password){
        char currentCharacter;
        boolean numberPresent = false;
        boolean upperCasePresent = false;
        boolean lowerCasePresent = false;

        for(int i = 0; i < password.length(); i++){
            currentCharacter = password.charAt(i);

            if(Character.isDigit(currentCharacter)){
                numberPresent = true;
            } else if(Character.isUpperCase(currentCharacter)){
                upperCasePresent = true;
            } else if (Character.isLowerCase(currentCharacter)){
                lowerCasePresent = true;
            }
        }


        return numberPresent && upperCasePresent && lowerCasePresent;
    }

    public static byte[] hashPassword(String password) throws Exception {

        if(password.length() < 22){
            System.out.println("Le mot de passe n'est pas assez long");
            return null;
        } else {

            if(testCharacters(password)) {

                char[] chars = password.toCharArray();
                byte[] salt = getNextSalt();
                int iterations = 10000;

                PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 256 * 8);
                SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                byte[] key = skf.generateSecret(spec).getEncoded();

                return key;
            }
            else {
                System.out.println("Le mot de passe doit contenir au moins un chiffre, une majuscule et une minuscule ");

            }
        }

        return null;
    }


}
