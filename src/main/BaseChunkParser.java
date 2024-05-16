package main;
import java.util.Arrays;

// This is the parser for the entire file; it merely dispatches requests to other relevant components.
public class BaseChunkParser implements Parser {

    @Override
    public String parseChunk(ParsedMDPP parsedMDPP, String[] lines) throws SyntaxException {
        String chunkHTML = "";
        int numLines = lines.length;
        for (int i = 0; i < numLines; i++) {
            if (lines[i].length() < 1) {
                continue;
            }
            if (lines[i].charAt(0) == '%') {
                continue;
            }
            if (lines[i].charAt(0) == ':') {
                String width = new ParserUtilities().regexExtractSingleString(lines[i], "^:\\{(.*)}").replace("\"", "");
                chunkHTML = chunkHTML.concat(String.format("<hr width=\"%s%%\">\n", width));
                continue;
            }
            if (lines[i].charAt(0) == '+') {
                int stopPoint = getStopPoint(lines, i, numLines);
                chunkHTML = chunkHTML.concat(new SectionParser().parseChunk(parsedMDPP, Arrays.copyOfRange(lines, i, stopPoint + 1)));
                i = stopPoint;
            } else if (lines[i].charAt(0) == '&') {
                new BaseControlSequenceHandler().parseLine(lines[i], parsedMDPP);
                continue;
            } else {
                System.out.println("Error line in MD++ source: " + lines[i]);
                throw new SyntaxException("Invalid base-level control sequence.");
            }
        }
        return chunkHTML;
    }

    public int getStopPoint(String[] lines, int i, int numLines) throws SyntaxException {
        int stopPoint = -1;
        int openCloseDeficit = 0;
        for (int j = i; j < numLines; j++) {
            if (lines[j].length() < 1) {continue;}
            else if (lines[j].charAt(0) == '%') {
                continue; // It's a comment.
            }

            if (lines[j].charAt(0) == '+') {
                openCloseDeficit++;
            }
            else if (lines[j].charAt(0) == '>') {
                openCloseDeficit--;
            }

            if (lines[j].charAt(0) == '>' && openCloseDeficit == 0) {
                stopPoint = j;
                break;
            }
        }
        if (stopPoint == -1) {
            throw new SyntaxException("Matching close not found for section open.");
        }
        return stopPoint;
    }
}
    

