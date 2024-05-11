import java.util.Arrays;

public class SectionParser implements Parser {
    @Override
    public String parseChunk(ParsedMDPP parsedMDPP, String[] lines) throws SyntaxException {
        String chunkHTML = "";
        chunkHTML = chunkHTML.concat(parseSectionStart(lines[0]));
        int numLines = lines.length;
        for (int i = 1; i < numLines - 1; i++) {
            // Process a section within the MD++.
            if (lines[i].charAt(0) == '+') {
                int stopPoint = getStopPoint(lines, i, numLines, '+', '>');
                chunkHTML = chunkHTML.concat(new SectionParser().parseChunk(parsedMDPP, Arrays.copyOfRange(lines, i, stopPoint + 1)));
                i = stopPoint;
            } else if (lines[i].charAt(0) == '&') {
                new BaseControlSequenceHandler().parseLine(lines[i], parsedMDPP);
                continue;
            } else if (lines[i].charAt(0) == '=') {
                int stopPoint = getStopPoint(lines, i, numLines, '=', '?');
                chunkHTML = chunkHTML.concat(new ListParser().parseChunk(parsedMDPP, Arrays.copyOfRange(lines, i, stopPoint + 1)));
                i = stopPoint;
            } else {
                chunkHTML = chunkHTML.concat(parseGeneralLine(lines[i]));
            }

        }
        chunkHTML = chunkHTML.concat(sectionEnd());
        return chunkHTML;
    }

    private void printChunk(String[] lines) {
        System.out.println("Starting chunk printout");
        for (String line : lines ) {
            System.out.println(line);
        }
        System.out.println("Ending chunk printout");
    }

    private String parseSectionStart(String startLine) {
        // TEMPORARY -- NEED PARSING.
        return "<div>\n<hr>\n";
    }

    private String sectionEnd() {
        return "</div>\n";
    }

    private String parseGeneralLine(String generalLine) {
        // TEMPORARY: Just return the line in <p>s.
        return String.format("<p>%s</p>\n", generalLine);
    }

    private int getStopPoint(String[] lines, int i, int numLines, char starter, char terminator) throws SyntaxException {
        int stopPoint = -1;
        int openCloseDeficit = 0;
        for (int j = i; j < numLines; j++) {
            if (lines[j].charAt(0) == starter) {
                openCloseDeficit++;
                System.out.println("Deficit++ for " + lines[j]);
            }
            else if (lines[j].charAt(0) == terminator) {
                System.out.println("Deficit-- for " + lines[j]);
                openCloseDeficit--;
            }
            if (lines[j].charAt(0) == terminator && openCloseDeficit == 0) {
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
