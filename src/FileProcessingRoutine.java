import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class FileProcessingRoutine implements Runnable {
    private FileToConvert subjectFile;
    public FileProcessingRoutine(FileToConvert subjectFile) {
        this.subjectFile = subjectFile;
    }
    @Override
    public void run() {
        String[] lines = getLines(subjectFile.getFile());
        ParsedMDPP parsedMDPP = new ParsedMDPP();
        try {
            // Obtain the parsed HTML components.
            String baseChunkHTML = new BaseChunkParser().parseChunk(parsedMDPP, lines);
            String bodyHTML = generateTitlebar(parsedMDPP);
            bodyHTML = bodyHTML.concat("<body>\n" + baseChunkHTML + "</body>\n");
            String headHTML = generateHeadHTML(parsedMDPP);
            // Write the parsed components to the relevant file.
            String fileString = "<html>\n" + headHTML + bodyHTML + "</html>\n";
            File newDestination = new File(subjectFile.getRelativePath());
            FileWriter fileWriter = new FileWriter(newDestination);
            fileWriter.write(fileString);
            fileWriter.close();
        } catch (SyntaxException se) {
            System.out.println("Error parsing file " + subjectFile.getFile().getName() + ". Error: " + se.getMessage());
            return;
        } catch (IOException ioe) {
            System.out.println("Fatal I/O exception while processing file.");
            return;
        }
    }

    public String[] getLines(File subjectFile) {
        try {
            List<String> lines = new ArrayList<>();
            FileReader conversionFileReader = new FileReader(subjectFile);
            BufferedReader conversionReader = new BufferedReader(conversionFileReader);
            String line;
            while ((line = conversionReader.readLine()) != null) {
                lines.add(line.stripLeading());
            }
            String[] linesArr = new String[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                linesArr[i] = lines.get(i);
            }
            return linesArr;
        } catch (IOException ie) {
            System.out.println("Fatal I/O error -- try again.");
            exit(1);
            return null;
        }
    }
    private String generateHeadHTML(ParsedMDPP parsedMDPP) {
        // TEMPORARY: Don't do much of anything.
        String headHTML = "";
        // Process the title.
        headHTML = headHTML.concat(String.format("<title>%s</title>\n", parsedMDPP.getPageTitle().replace("\"", "")));
        // Process the CSS URLS
        for (String cssURL : parsedMDPP.getCSSURLs()) {
            headHTML = headHTML.concat(String.format("<link rel=\"stylesheet\" href=\"%s\">\n", cssURL.replace("\"", "")));
        }
        return String.format("<head>\n%s</head>\n", headHTML);
    }

    private String generateTitlebar(ParsedMDPP parsedMDPP) {
        // TEMPORARY: Don't do much of anything.
        return "";
    }
}
