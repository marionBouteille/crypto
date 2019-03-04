
import org.apache.commons.cli.*;

import java.util.Arrays;

public class CLIParser {

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        //-enc|-dec
        options.addOption("enc",false,"Chiffrement");
        options.addOption("dec", false, "Déchiffrement");
        options.addOption("in", true, "Fichier d'entré");
        options.addOption("out", false, "Fichier de sorti");
        options.addOption("pass", true, "Mot de passe ");

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("CLI options : ", options);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if(cmd.hasOption("enc")){
            System.out.println(cmd);
        }
        if(cmd.hasOption("dec")){
            System.out.println(cmd);
        }
        if(cmd.hasOption("in")){
            System.out.println(cmd.getOptionValue("in"));
        }
        if(cmd.hasOption("out")){
            System.out.println(Arrays.toString(cmd.getOptionValues("out")));
        }



        //-in <input file> - out <output file>
    }

}
