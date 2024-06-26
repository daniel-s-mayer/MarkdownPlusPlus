package main;
import java.io.File;
import java.io.SyncFailedException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryProcessingRoutine {
    private File fromDirectory;
    private File toDirectory;
    public DirectoryProcessingRoutine(File fromDirectory, File toDirectory) {
        this.fromDirectory = fromDirectory;
        this.toDirectory = toDirectory;
    }
    public void convertDirectory() throws SyntaxException {
        List<FileToConvert> filesToConvert = recursiveTreeBuilder(fromDirectory, toDirectory.getAbsolutePath() + "/");
        List<Thread> fileThreads = new ArrayList<>();
        // Send for conversion.
        for (FileToConvert ftc : filesToConvert) {
            FileProcessingRoutine fileProcessingRoutine = new FileProcessingRoutine(ftc);
            Thread fileThread = new Thread(fileProcessingRoutine);
            fileThread.start();
            fileThreads.add(fileThread);
        }
        // Join to wait for success before notifying the user.
        for (Thread ft : fileThreads) {
            try {
                ft.join();
            } catch (InterruptedException ie) {
                ;
            }
        }
        System.out.println("All conversions complete!");
    }
    public List<FileToConvert> recursiveTreeBuilder(File currentBase, String relativePath) throws SyntaxException {
        List<FileToConvert> filesToConvert = new ArrayList<>();
        File[] baseList = currentBase.listFiles();
        if (currentBase == null || baseList == null) {
            throw new SyntaxException("Invalid file path.");
        }
        for (File file : baseList) {
            if (file.isDirectory()) {
                filesToConvert.addAll(recursiveTreeBuilder(file, relativePath + file.getName() + "/"));
                continue;
            }
            if (file.getName().endsWith(".mdpp")) {
                filesToConvert.add(new FileToConvert(file, relativePath + file.getName().replace(".mdpp", ".html")));
            }
        }
        return filesToConvert;
    }
}

