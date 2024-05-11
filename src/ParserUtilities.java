import java.util.HashMap;
import java.util.Map;

public class ParserUtilities {
    private final char ESCAPE = '\\';
    private Map<Character, String> controlSequenceMap;

    public ParserUtilities() {
        this.controlSequenceMap = new HashMap<>();
        controlSequenceMap.put('`', "<code>%s</code>");
        controlSequenceMap.put('*', "<b>%s</b>");
        controlSequenceMap.put('_', "<i>%s</i>");
    }
    public String parseGeneralLine(String generalLine, boolean isBase) throws SyntaxException {
        System.out.println("Got general line: " + generalLine);
        String parsedLine = "";
        boolean previousEscape = false;
        int numChars = generalLine.length();
        for (int i = 0; i < numChars; i++) {
            if (generalLine.charAt(i) == ESCAPE && !previousEscape) {
                previousEscape = true;
                continue;
            } else if (generalLine.charAt(i) == ESCAPE && previousEscape) {
                // Double escapes are just a \.
                previousEscape = false;
                parsedLine = parsedLine.concat(String.valueOf(ESCAPE));
                continue;
            }
            if (controlSequenceMap.containsKey(generalLine.charAt(i)) && !previousEscape) {
                int stopIndex = getStopPointString(generalLine, i, generalLine.charAt(i));
                parsedLine = parsedLine.concat(String.format(controlSequenceMap.get(generalLine.charAt(i)), parseGeneralLine(generalLine.substring(i + 1, stopIndex), false)));
                i = stopIndex;
                continue;
            } else {
                System.out.println("Concatenating character :" + generalLine.charAt(i));
                parsedLine = parsedLine.concat(String.valueOf(generalLine.charAt(i)));
            }
            if (generalLine.charAt(i) != ESCAPE) {
                previousEscape = false;
            }

        }
        // Wrap the formatted line in "<p>"s for return.
        System.out.println("Produced line: " + parsedLine);
        if (isBase) {
            return String.format("<p>%s</p>", parsedLine);
        }
        return parsedLine;
    }

    public String parseHeaderLine(String headerLine) throws SyntaxException {
        // Count the number of #s before the first other character.
        int currentIndex = 0;
        int numPounds = 0;
        while (headerLine.charAt(currentIndex) == '#') {
            currentIndex++;
            numPounds++;
        }
        return String.format("<h%d>%s</h%d>\n", numPounds, parseGeneralLine(headerLine.substring(numPounds), false), numPounds);
    }

    public int getStopPoint(String[] lines, int i, int numLines, char starter, char terminator) throws SyntaxException {
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

    public int getStopPointString(String string, int i, char delimiter) throws SyntaxException {
        int stopPoint = -1;
        boolean lastCharacterEscape = false;
        for (int j = i + 1; j < string.length(); j++) {
            if (string.charAt(j) == ESCAPE) {
                lastCharacterEscape = true;
                continue;
            }
            if (string.charAt(j) == delimiter && !lastCharacterEscape) {
                stopPoint = j;
                break;
            }
            if (string.charAt(j) != ESCAPE) {
                lastCharacterEscape = false;
            }
        }
        if (stopPoint == -1) {
            throw new SyntaxException("Matching close not found for section open.");
        }
        return stopPoint;
    }
}
