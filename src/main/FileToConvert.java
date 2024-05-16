package main;
import java.io.File;

public class FileToConvert {
    private File file;
    private String relativePath;
    public FileToConvert(File file, String relativePath) {
        this.file = file;
        this.relativePath = relativePath;
    }

    public File getFile() {
        return this.file;
    }

    public String getRelativePath() {
        return this.relativePath;
    }
}
