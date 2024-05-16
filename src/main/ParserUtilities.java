package main;
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
        controlSequenceMap.put('^', "<small>%s</small>");
    }
    public String parseGeneralLine(String generalLine, boolean isBase) throws SyntaxException {
        String parsedLine = "";
        boolean previousEscape = false;
        int numChars = generalLine.length();
        for (int i = 0; i < numChars; i++) {
            if (generalLine.charAt(i) == ESCAPE && !previousEscape) {
                previousEscape = true;
                continue;
            } else if (generalLine.charAt(i) == ESCAPE && previousEscape) {
                // Double escapes are just a \.
                previousEscape = true;
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
                parsedLine = parsedLine.concat(String.valueOf(generalLine.charAt(i)));
            }
            if (generalLine.charAt(i) != ESCAPE) {
                previousEscape = false;
            }


        }
        // Wrap the formatted line in "<p>"s for return.
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
            if (lines[j].length() < 1) {
                continue;
            }
            if (lines[j].charAt(0) == starter) {
                openCloseDeficit++;
            }
            else if (lines[j].charAt(0) == terminator) {
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
                if (lastCharacterEscape) {
                    lastCharacterEscape = false;
                } else {
                    lastCharacterEscape = true;
                }
                continue;
            }
            if (string.charAt(j) == delimiter && (!lastCharacterEscape)) {
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
    public String processSpecialInline(String inlineString) throws SyntaxException {
        String commandType = new ParserUtilities().regexExtractSingleString(inlineString, "^(.*?)\\:");
        if (commandType.equals("link")) {
            String name = new ParserUtilities().regexExtractSingleString(inlineString, "^link:(.*?)\\{");
            String url = new ParserUtilities().regexExtractSingleString(inlineString, "^.*?:.*?\\{(.*?)}");
            String classText = new ParserUtilities().regexExtractSingleString(inlineString, "^.*?:.*?\\{.*?}\\{(.*)}");
            return String.format("<a href=\"%s\" class=\"%s\">%s</a>", url, classText, name);
        }
        if (commandType.equals("style")) {
            String styleOptions = new ParserUtilities().regexExtractSingleString(inlineString, "^.*:\\{(.*)}").replace("\"", "");
            String actualString = new ParserUtilities().regexExtractSingleString(inlineString, "^.*:\\{.*}\\[(.*)]").replace("\"", "");
            return String.format("<span style=\"%s\">%s</span>", styleOptions, actualString);
        }
        return "";
    }

    public Map<String, String> generateOptions(String startLine) throws SyntaxException {
        String optionsString = new ParserUtilities().regexExtractSingleString(startLine, "^.*?:.*?\\{(.*?)}");
        String[] splitOptionsStrings = optionsString.split(";");
        HashMap<String, String> optionValuePairs = new HashMap<>();
        for (String singleSplit : splitOptionsStrings) {
            if (singleSplit.contains(":")) {
                String[] singleSplitParts = singleSplit.split(":");
                optionValuePairs.put(singleSplitParts[0], singleSplitParts[1]);
            }
        }
        return optionValuePairs;
    }
}
