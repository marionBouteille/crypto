import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FilesTest {

    public static boolean testInputFile(String chemin){

        if(Files.exists(Paths.get(chemin))){
            return true;
        }
        return false;
    }

    public static boolean testOutputFile(String chemin){
        if(Files.notExists(Paths.get(chemin))){
            return true;
        }
        else {
            //on écrase le fichier
            try{
                Files.copy((Paths.get(chemin)), (Paths.get(chemin)), StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
               e.printStackTrace();
            }
        }
        return false;
    }
}
