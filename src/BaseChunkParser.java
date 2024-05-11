import java.util.Arrays;

// This is the parser for the entire file; it merely dispatches requests to other relevant components.
public class BaseChunkParser implements Parser {

    @Override
    public String parseChunk(ParsedMDPP parsedMDPP, String[] lines) throws SyntaxException {
        String chunkHTML = "";
        int numLines = lines.length;
        for (int i = 0; i < numLines; i++) {
            // Process a section within the MD++.
            if (lines[i].charAt(0) == '+') {
                int stopPoint = getStopPoint(lines, i, numLines);
                chunkHTML = chunkHTML.concat(new SectionParser().parseChunk(parsedMDPP, Arrays.copyOfRange(lines, i, stopPoint + 1)));
                i = stopPoint;
            } else if (lines[i].charAt(0) == '&') {
                new BaseControlSequenceHandler().parseLine(lines[i], parsedMDPP);
                continue;
            } else {
                throw new SyntaxException("Invalid base-level control sequence.");
            }
        }
        return chunkHTML;
    }

    private int getStopPoint(String[] lines, int i, int numLines) throws SyntaxException {
        int stopPoint = -1;
        int openCloseDeficit = 0;
        for (int j = i; j < numLines; j++) {
            if (lines[i].length() < 1) {
                throw new SyntaxException("Blank lines not permitted outside of a section.");
            }
            if (lines[j].charAt(0) == '+') {
                openCloseDeficit++;
                System.out.println("Deficit++ for " + lines[j]);
            }
            else if (lines[j].charAt(0) == '>') {
                System.out.println("Deficit-- for " + lines[j]);
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
    

