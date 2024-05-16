package main;
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
            if (lines[i].charAt(0) == '%') {
                continue; // Comment
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
            // Process a blockquote within the current section.
            else if (lines[i].charAt(0) == '<') {
                String blockQuoteChunk = "<blockquote>";
               int stopPoint = i;
               for (int j = i; j < numLines; j++) {

                   if (lines[j].length() < 1 || lines[j].charAt(0) != '<') {
                       stopPoint = j;
                   } else {
                       blockQuoteChunk = blockQuoteChunk.concat(lines[j].substring(1));
                   }
               }
               blockQuoteChunk = blockQuoteChunk.concat("</blockquote>\n");
               chunkHTML = chunkHTML.concat(blockQuoteChunk);
               i = stopPoint;
            }
            // Process an image within the current section.
            else if (lines[i].charAt(0) == ';') {
                chunkHTML = chunkHTML.concat(processImage(lines[i]));
            }
            else if (lines[i].charAt(0) == ':') {
                String width = parserUtilities.regexExtractSingleString(lines[i], "^:\\{(.*)}").replace("\"", "");
                chunkHTML = chunkHTML.concat(String.format("<hr width=\"%s%%\">\n", width));
            }
            else if (lines[i].charAt(0) == '~') {
                String htmlChunk = "";
                int stopPoint = i;
                for (int j = i + 1; j < numLines; j++) {
                    if (lines[j].length() < 1) {
                        continue;
                    } else if (lines[j].charAt(0) == '~') {

                        stopPoint = j;
                        break;
                    } else {
                        htmlChunk = htmlChunk.concat(lines[j].substring(0));
                    }
                }
                chunkHTML = chunkHTML.concat(htmlChunk);
                i = stopPoint;
            }
            else {
                chunkHTML = chunkHTML.concat(parserUtilities.parseGeneralLine(lines[i], true));
            }

        }
        chunkHTML = chunkHTML.concat(sectionEnd());
        return chunkHTML;
    }


    public String parseSectionStart(String startLine) throws SyntaxException {
        ParserUtilities parserUtilities = new ParserUtilities();
        String sectionName = parserUtilities.regexExtractSingleString(startLine, "^\\+start-section:(.*?)\\{").replaceAll("\"", "");
        String classFields = parserUtilities.regexExtractSingleString(startLine, "^.*?:.*?\\{.*?}\\{(.*)}");
        Map<String, String> sectionOptions = parserUtilities.generateOptions(startLine);
        String optionsCSS = getSectionOptionsCSS(sectionOptions);
        return String.format("<div id=\"%s\" name=\"%s\" style=\"%s\" class=\"%s\">\n", sectionName, sectionName, optionsCSS, classFields);
    }

    public String getSectionOptionsCSS(Map<String, String> sectionOptions) {
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

    public String getImageOptionsCSS(Map<String, String> imageOptions) {
        String optionsCSS = "";
        for (Map.Entry<String, String> optionsEntry : imageOptions.entrySet()) {
            optionsCSS = switch (optionsEntry.getKey()) {
                case "float" -> optionsCSS.concat(String.format("float: %s;", optionsEntry.getValue()));
                case "css-class" -> optionsCSS.concat(String.format("class: %s;", optionsEntry.getValue()));
                default -> optionsCSS.concat(String.format("%s: %s;", optionsEntry.getKey(), optionsEntry.getValue()));
            };
        }
        return optionsCSS;
    }

    private String sectionEnd() {
        return "</div>\n";
    }



    public String processImage(String line) throws SyntaxException {
        ParserUtilities parserUtilities = new ParserUtilities();
        String altText = parserUtilities.regexExtractSingleString(line, "^;\\[(.*?)\\]").replace("\"", "");
        String htmlID = parserUtilities.regexExtractSingleString(line, "^;\\[.*]\\[(.*?)\\]").replace("\"", "");
        String url = parserUtilities.regexExtractSingleString(line, "^;\\[.*]\\[.*]\\[(.*?)\\]").replace("\"", "");
        Map<String, String> imageOptions = parserUtilities.generateOptions(line);
        String imageCSS = getImageOptionsCSS(imageOptions);
        return String.format("<img src=\"%s\" alt=\"%s\" style=\"%s\" id=\"%s\">\n", url, altText, imageCSS, htmlID);
    }


}
