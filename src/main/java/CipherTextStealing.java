import javax.crypto.*;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class CipherTextStealing {

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {

        String mes = "Voici le texte que je tente de chiffrer et de déchiffrer avec un mode CTS a la mano et c'est tres relou a faire !";
        byte[] IV = generateIv();
        Key key = generateKey();
        encrypt(mes, IV, key);

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

    private static void encrypt(String message, byte[] IV, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException {

        System.out.println("Input : " + message);

        byte[] input = message.getBytes();
        byte[] resu = new byte[input.length];
        byte[] cipherBlock = new byte[16];
        byte[] xorBlock;
        byte[] firstBlock = new byte[16];
        byte[] nextBlock = new byte[16];
        System.arraycopy(input , 0,firstBlock,0 ,  16);


        int blocks = input.length/16;
        int rest = input.length%16;
        byte[] lastBlock = new byte[rest];
        System.out.println("blocks : " + blocks);
        System.out.println("modulo : " + rest);

        //XOR avec le vecteur d'initialisation
        xorBlock = xor(firstBlock, IV);
        //Chiffre le premier bloc avec aesEncrypt, et insère dans le tableau resu
        System.arraycopy(aesEncrypt(xorBlock, key), 0, resu,0 ,  16);
        //On recupère le bloc chiffrer
        System.arraycopy(aesEncrypt(xorBlock, key) , 0, cipherBlock,0, 16);
        //on récupère le bloc de texte claire suivant
        System.arraycopy(input ,16 , nextBlock , 0, 16);

        for (int i = 1; i<blocks-1; i++){
            //on récupère le bloc de texte claire suivant
            System.arraycopy(input ,16*i , nextBlock , 0, 16);
            //on xor le chiffrer précédent avec le claire suivant
            xorBlock = xor(cipherBlock, nextBlock);
            //On recupère le chiffer
            System.arraycopy(aesEncrypt(xorBlock, key),0 ,cipherBlock , 0, 16);
            //on insère le chiffrer dans notre tableau de resu
            System.arraycopy(cipherBlock,0 ,resu , 16*i, 16);

        }

        //On recupère le chiffer
        System.arraycopy(aesEncrypt(xorBlock, key), 0 ,cipherBlock, 0, 16);
        //On garde que la dernière partie dans lastblock
        System.arraycopy(cipherBlock, 0,lastBlock,0 , rest);
        //on récupère le bloc de texte claire suivant
        lastClearBlock(input, nextBlock);
        //System.out.println("last clear block : " + Arrays.toString(nextBlock));
        //xor du dernier bloc avec le chiffrer de l'avant dernier bloc
        xorBlock = xor(cipherBlock, nextBlock);
        //System.out.println("cipher block: " + Arrays.toString(cipherBlock));
        //System.out.println("xor last and cipher: " + Arrays.toString(xorBlock));
        //On chiffre notre dernier bloc
        System.arraycopy(aesEncrypt(xorBlock, key), 0, cipherBlock,0,16);
        //on insère le chiffrer dans notre tableau de resu
        System.arraycopy(cipherBlock , 0, resu,(16*(input.length/16-1)), 16);
        System.out.println("insert in resu : " + Arrays.toString(cipherBlock) + Arrays.toString(xorBlock));
        //On insère notre dernier bloc à notre resu
        System.arraycopy(lastBlock , 0, resu,(16*(input.length/16)), rest);

        String str = new String(resu, "UTF-8");
        System.out.println("resu : " + str);

        decrypt(resu, IV, key);
    }

    private static void decrypt(byte[] input, byte[] IV, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException {

        System.out.println("clear input : " + Arrays.toString(input));
        int blocks = input.length/16;
        int rest = input.length%16;

        byte[] resu = new byte[input.length];
        byte[] cipherBlock = new byte[16];
        byte[] xorBlock;
        byte[] nextBlock = new byte[16];
        byte[] lastCBCBlock = new byte[16];
        byte[] lastBlock = new byte[rest];
        byte[] lastUncipher = new byte[16];

        //On recupère le bloc chiffrer
        System.arraycopy(input , 0, cipherBlock,0, 16);
        //XOR du clob déchiffrer avec le vecteur d'initialisation
        xorBlock = xor(aesDecrypt(cipherBlock, key), IV);
        //Copie du resultat du xor dans resu
        System.arraycopy(xorBlock, 0, resu,0 ,  16);

        for (int i = 1; i<blocks-1; i++){

            //On récupère le chiffrer precedent
            System.arraycopy(input,16*i-16 ,cipherBlock , 0, 16);
            //On récupère le chiffrer en cours
            System.arraycopy(input ,16*i, nextBlock , 0, 16);
            //On déchiffre et on xor
            xorBlock = xor(aesDecrypt(nextBlock, key), cipherBlock);
            //On recopie le déchiffrer dans resu
            System.arraycopy(xorBlock,0 ,resu , 16*i, 16);
        }

        //On sauvegarde l'avant avant dernier chiffrer
        System.arraycopy(cipherBlock ,0 , lastCBCBlock , 0, 16);
        //System.out.println("Avant avant dernier bloc chiffer : " + Arrays.toString(lastCBCBlock));
        //On sauvegarde l'avant dernier chiffrer
        System.arraycopy(input , blocks*16-16, cipherBlock , 0, 16);
        //System.out.println("avant dernier bloc chiffer : " + Arrays.toString(cipherBlock));

        //On récupère l'avant dernier bloc et on le déchiffre
        System.arraycopy(aesDecrypt(cipherBlock, key) ,0 , lastUncipher , 0, 16);

        //on récupère le dernier bloc
        System.arraycopy(input ,(16*blocks) , lastBlock , 0, rest);
        //System.out.println("Dernier bloc : " + Arrays.toString(lastBlock));
        //System.out.println("avant dernier dechiffrer  bloc : " + Arrays.toString(lastUncipher));
        //On met la fin du block à la fin du dernier block
        System.arraycopy(lastBlock ,0 , nextBlock , 0, rest);
        System.arraycopy(lastUncipher ,rest , nextBlock , rest, 16-rest);
        //System.out.println("Dernier bloc après fusion : " + Arrays.toString(nextBlock));

        //On xor last block et next block
        xorBlock = xor(lastCBCBlock, aesDecrypt(nextBlock, key));
        //On ajoute ce dernier au resu
        System.arraycopy(xorBlock,0 ,resu , (16*(input.length/16-1)), 16);
        //On xor avec le dernier chiffré et ses 0
        xorBlock = xor(lastUncipher, nextBlock);
        //On ajoute au resu
        System.arraycopy(xorBlock,0 ,resu , (16*(input.length/16)), rest);

        String str = new String(resu, "UTF-8"); // for UTF-8 encoding

        System.out.println("resu decrypt : " + str);

    }

    private static byte[] xor(byte[] input1, byte[] input2){
        byte[] resu = new byte[input1.length];
        if(input1.length == input2.length){
            for (int i = 0; i<input1.length; i++){
                resu[i] = (byte) (input1[i] ^ input2[i]);
            }
        }
        return resu;
    }

    private static byte[] aesEncrypt(byte[] input, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher m1 = Cipher.getInstance("AES/ECB/NoPadding");
        m1.init(Cipher.ENCRYPT_MODE, key);
        return m1.doFinal(input);
    }

    private static byte[] aesDecrypt(byte[] input, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher m1 = Cipher.getInstance("AES/ECB/NoPadding");
        m1.init(Cipher.DECRYPT_MODE, key);
        return m1.doFinal(input);
    }

    private static void lastClearBlock(byte[] input, byte[] resu){
        int debLast = 16 * (input.length/16);
        int sizeZero = 16 - input.length%16;
        int sizeMessage = input.length%16;

        System.arraycopy(input , debLast, resu,0 ,  sizeMessage);

        byte[] zero = new byte[sizeZero];
        for (int i = 0; i<sizeZero; i++){
            zero[i] = 0x0;
        }

        System.arraycopy(zero, 0, resu  , sizeMessage, sizeZero);
    }
}
