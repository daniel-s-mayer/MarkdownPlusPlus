import java.io.SyncFailedException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtilities {
    private final char ESCAPE = '\\';
    private final char SPECIAL_INLINE = '@';
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

            if (generalLine.charAt(i) == SPECIAL_INLINE && !previousEscape) {
                int stopIndex = getStopPointString(generalLine, i, generalLine.charAt(i));
                parsedLine = parsedLine.concat(processSpecialInline(generalLine.substring(i + 1, stopIndex)));
                i = stopIndex;
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

    // Note: Use this function when the to-be-extracted string must be present.
    public String regexExtractSingleString(String fromString, String regexToExtract) throws SyntaxException {
        Matcher regexMatch = Pattern.compile(regexToExtract).matcher(fromString);
        if (regexMatch.find()) {
            return regexMatch.group(1);
        } else {
            throw new SyntaxException("Control sequence missing required element. Element: " + fromString + " Regex: " + regexToExtract);

        }
    }

    // For now, special inlines are only links.
    // Note that this method receives the special inline stripped of the surrounding @s.
    private String processSpecialInline(String inlineString) throws SyntaxException {
        System.out.println("Processing special inline:" + inlineString);
        String commandType = new ParserUtilities().regexExtractSingleString(inlineString, "^(.*):.*\\{");
        System.out.println("Command type: " + commandType);
        if (commandType.equals("link")) {
            String name = new ParserUtilities().regexExtractSingleString(inlineString, "^.*:(.*)\\{");
            String url = new ParserUtilities().regexExtractSingleString(inlineString, "^.*:.*\\{(.*)\\}");
            return String.format("<a href=%s>%s</a>", url, name);
        }
        return "";
    }
}
