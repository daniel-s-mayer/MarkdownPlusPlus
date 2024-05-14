import java.io.File;
public class Main {

    public static void main(String[] args) throws SyntaxException {
        for (String arg : args) {
            System.out.println("Argument: "  + arg);
        }
        // Parse the command-line arguments.
        if (args.length < 1) {
            displayError("Not enough arguments. Run with -h for help.");
            return;
        }
        if (args.length < 2 && !args[0].equals("-h")) {
            displayError("Not enough arguments. Run with -h for help.");
            return;
        }
        if (args[0].equals("-h")) {
            System.out.println("Help: mdpp [from directory] [to directory]");
            System.out.println("    Where 'from directory' is the directory containing files to convert MDPP->HTML and 'to directory' is the directory in which the produced HTML files should be placed.");
            return;
        }
        // Future location of command line parsing.
        File sourceDirectory = new File(args[0]);
        File destinationDirectory = new File(args[1]);
        DirectoryProcessingRoutine directoryProcessingRoutine = new DirectoryProcessingRoutine(sourceDirectory, destinationDirectory);
        try {
            directoryProcessingRoutine.convertDirectory();
            System.out.printf("All conversions successfully completed! See %s for the converted files.\n", destinationDirectory.getAbsolutePath());
        } catch (SyntaxException se) {
            System.out.println("Error encountered while processing a file: " + se.getMessage());
        }
    }
    public static void displayError(String error) {
        System.out.println(error);
    }
}