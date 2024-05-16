package main;
public class ListParser implements Parser {

    @Override
    public String parseChunk(ParsedMDPP parsedMDPP, String[] lines) throws SyntaxException {
        // Note: Every line in a list is a list element, so it should begin with '-'.
        String listHTML = "";
        for (String line : lines) {
            if (line.length() < 1) {
                throw new SyntaxException("Empty line in list.");
            }
            if (line.charAt(0) == '=') {
                listHTML = listHTML.concat(processListStart(line));
                continue; // It's a control sequence.
            }
            if (line.charAt(0) == '?') {
                listHTML = listHTML.concat(processListEnd(line));
                continue;
            }

            if ((line.charAt(0) != '-' && line.charAt(0) != '!')) {
                throw new SyntaxException("Invalid character in a list.");
            }
            // All cases.
            if (line.charAt(0) == '-') {
                listHTML = listHTML.concat(String.format("<li>%s</li>\n", new ParserUtilities().parseGeneralLine(line.substring(1), false)));
            }
        }
        return listHTML;
    }

    public String processListStart(String listStartingTag) throws SyntaxException {
        String listType = new ParserUtilities().regexExtractSingleString(listStartingTag, "^=start-(.*)-list");
        if (listType.equals("ordered")) {
            return "<ol>\n";
        } else if (listType.equals("unordered")) {
            return "<ul>\n";
        } else {
            throw new SyntaxException("Invalid ordering type.");
        }
    }

    public String processListEnd(String listEndingTag) throws SyntaxException {
        String listType = new ParserUtilities().regexExtractSingleString(listEndingTag, "^?end-(.*)-list");
        if (listType.equals("ordered")) {
            return "</ol>\n";
        } else if (listType.equals("unordered")) {
            return "</ul>\n";
        } else {
            throw new SyntaxException("Invalid ordering type.");
        }
    }
}
