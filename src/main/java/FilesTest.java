import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public static void zip(String zipName, List<String> fileList){

        byte[] buffer = new byte[1024];
        try {
            //flux de sortie où écrire les données
            FileOutputStream fos = new FileOutputStream(zipName);
            //flux de sortie pour écrire un fichier au format ZIP
            ZipOutputStream zipOutputStream = new ZipOutputStream(fos);
            //Méthode de compression pour les entrées non compréssées
            zipOutputStream.setMethod(ZipOutputStream.STORED);

            for (String file : fileList){
                System.out.println("Le fichier "+file+"a été ajouté ! ");
                //Création d'un nouvau fichier zip
                ZipEntry zipEntry = new ZipEntry(file);
                zipOutputStream.putNextEntry(zipEntry);

                FileInputStream inputStream = new FileInputStream(file);

                int taille;

                while((taille = inputStream.read(buffer)) > 0){
                    zipOutputStream.write(buffer,0,taille);
                }

                inputStream.close();

            }

            zipOutputStream.closeEntry();
            zipOutputStream.close();

            System.out.println("Fichier compréssé réussi !");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
