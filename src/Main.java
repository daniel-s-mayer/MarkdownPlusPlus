import java.io.File;
public class Main {
    public static void main(String[] args) throws SyntaxException {
        // Future location of command line parsing.
        File sourceDirectory = new File("D:\\Personal Projects\\Markdown++\\TestDir");
        File destinationDirectory = new File("D:\\Personal Projects\\Markdown++\\TestDestDir");
        DirectoryProcessingRoutine directoryProcessingRoutine = new DirectoryProcessingRoutine(sourceDirectory, destinationDirectory);
        try {
            directoryProcessingRoutine.convertDirectory();
            System.out.printf("All conversions successfully completed! See %s for the converted files.\n", destinationDirectory.getAbsolutePath());
        } catch (SyntaxException se) {
            System.out.println("Syntax error encountered while parsing a file: " + se.getMessage());
        }
    }
}