import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class Encrypt {

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        String mes = "111111111111111 111111111111111 11111111111";
        byte[] IV = generateIv();
        cipherTextStealing(mes, IV);
    }

    private static SecretKey generateKey() throws NoSuchAlgorithmException {

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize, secureRandom);

        return keyGenerator.generateKey();
    }

    private static byte[] generateIv() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        return iv;
    }

    private static void cipherTextStealing(String message, byte[] IV) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {

        byte[] input = message.getBytes();
        byte[] resu = new byte[message.length()];
        byte[] cipherBlock = new byte[16];
        byte[] xorBlock;
        byte[] firstBlock = new byte[16];
        byte[] nextBlock = new byte[16];
        Key key = generateKey();
        System.arraycopy(firstBlock,0 , input , 0, 16);


        int blocks = message.length()/16;
        int rest = message.length()%16;
        byte[] lastBlock = new byte[rest];
        System.out.println("blocks : " + blocks);
        System.out.println("modulo : " + rest);

        //XOR avec le vecteur d'initialisation
        xorBlock = xor(firstBlock, IV);
        //Chiffre le premier bloc avec aes, et insère dans le tableau resu
        System.arraycopy(resu,0 , aes(xorBlock, key) , 0, 16);
        //On recupère le bloc chiffrer
        System.arraycopy(cipherBlock,0 , aes(xorBlock, key) , 0, 16);

        for (int i = 1; i<blocks-1; i++){
            //On recupère le chiffer
            System.arraycopy(cipherBlock,0 , aes(xorBlock, key), 0, 16);
            //on insère le chiffrer dans notre tableau de resu
            System.arraycopy(resu,0 , cipherBlock , 16*i, 16);

            //on récupère le bloc de texte claire suivant
            System.arraycopy(nextBlock,0 , input , 16*i, 16);
            //on xor le chiffrer précédent avec le claire suivant
            xorBlock = xor(cipherBlock, nextBlock);
        }

        //On recupère le chiffer
        System.arraycopy(cipherBlock,0 , aes(xorBlock, key), 0, 16);
        //On garde que la dernière partie dans lastblock
        System.arraycopy(lastBlock,0 , cipherBlock, 0, rest);
        //on récupère le bloc de texte claire suivant
        lastClearBlock(input, nextBlock);
        //xor du dernier bloc avec le chiffrer de l'avant dernier bloc
        xorBlock = xor(cipherBlock, nextBlock);
        //On chiffre notre dernier bloc
        System.arraycopy(cipherBlock,0 , aes(xorBlock, key), 0, 16);
        //on insère le chiffrer dans notre tableau de resu
        System.arraycopy(resu,(16*(input.length/16-1)) , cipherBlock , 0, 16);
        //On insère notre dernier bloc à notre resu
        System.arraycopy(resu,(16*(input.length/16)) , lastBlock , 0, input.length%16);

        System.out.println("resu : " + Arrays.toString(resu));

    }

    private static byte[] xor(byte[] input1, byte[] input2){
        byte[] resu = new byte[input1.length];
        if(input1.length == input2.length){
            for (int i = 1; i<input1.length; i++){
                resu[i] = (byte) (input1[i] ^ input2[i]);
            }
        }
        return resu;
    }

    private static byte[] aes(byte[] input, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher m1 = Cipher.getInstance("AES");
        m1.init(Cipher.ENCRYPT_MODE, key);
        return m1.doFinal(input);
    }

    private static void lastClearBlock(byte[] input, byte[] resu){
        int debLast = 16 * (input.length/16);
        int sizeZero = 16 - input.length%16;
        int sizeMessage = input.length%16;

        System.arraycopy(resu,0 , input , debLast, sizeMessage);

        byte[] zero = new byte[sizeZero];
        for (int i = 0; i<sizeZero; i++){
            zero[i] = 0;
        }

        System.arraycopy(resu,sizeMessage, zero , 0, sizeZero);
    }
}
