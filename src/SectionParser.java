import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SectionParser implements Parser {
    private ParserUtilities parserUtilities;
    public SectionParser() {
        this.parserUtilities = new ParserUtilities();
    }

    @Override
    public String parseChunk(ParsedMDPP parsedMDPP, String[] lines) throws SyntaxException {
        String chunkHTML = "";
        chunkHTML = chunkHTML.concat(parseSectionStart(lines[0]));
        int numLines = lines.length;
        for (int i = 1; i < numLines - 1; i++) {
            if (lines[i].length() < 1) {
                chunkHTML = chunkHTML.concat("<br>\n");
                continue;
            }
            // Process a section within the current section.
            if (lines[i].charAt(0) == '+') {
                int stopPoint = parserUtilities.getStopPoint(lines, i, numLines, '+', '>');
                chunkHTML = chunkHTML.concat(new SectionParser().parseChunk(parsedMDPP, Arrays.copyOfRange(lines, i, stopPoint + 1)));
                i = stopPoint;
            }
            // Process a base-level control sequence within the current section.
            else if (lines[i].charAt(0) == '&') {
                new BaseControlSequenceHandler().parseLine(lines[i], parsedMDPP);
                continue;
            }
            // Process a list within the current section.
            else if (lines[i].charAt(0) == '=') {
                int stopPoint = parserUtilities.getStopPoint(lines, i, numLines, '=', '?');
                chunkHTML = chunkHTML.concat(new ListParser().parseChunk(parsedMDPP, Arrays.copyOfRange(lines, i, stopPoint + 1)));
                i = stopPoint;
            }
            // Process a header within the current section.
            else if (lines[i].charAt(0) == '#') {
                chunkHTML = chunkHTML.concat(parserUtilities.parseHeaderLine(lines[i]));
            }
            else {
                chunkHTML = chunkHTML.concat(parserUtilities.parseGeneralLine(lines[i], true));
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

    private String parseSectionStart(String startLine) throws SyntaxException {
        ParserUtilities parserUtilities = new ParserUtilities();
        String sectionName = parserUtilities.regexExtractSingleString(startLine, "^+.*:(.*)\\{").replaceAll("\"", "");
        Map<String, String> sectionOptions = generateSectionOptions(startLine);
        String optionsCSS = getOptionsCSS(sectionOptions);
        return String.format("<div id=\"%s\" name=\"%s\" style=\"%s\">\n", sectionName, sectionName, optionsCSS);
    }

    private static String getOptionsCSS(Map<String, String> sectionOptions) {
        String optionsCSS = "";
        for (Map.Entry<String, String> optionsEntry : sectionOptions.entrySet()) {
            optionsCSS = switch (optionsEntry.getKey()) {
                case "background-color" -> optionsCSS.concat(String.format("background: %s;", optionsEntry.getValue()));
                case "css-class" -> optionsCSS.concat(String.format("class: %s;", optionsEntry.getValue()));
                default -> optionsCSS.concat(String.format("%s: %s;", optionsEntry.getKey(), optionsEntry.getValue()));
            };
        }
        return optionsCSS;
    }

    private String sectionEnd() {
        return "</div>\n";
    }

    private Map<String, String> generateSectionOptions(String startLine) throws SyntaxException {
        String optionsString = new ParserUtilities().regexExtractSingleString(startLine, "^+.*:.*\\{(.*)}");
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
