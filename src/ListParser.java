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
                listHTML = listHTML.concat(String.format("<li>%s</li>\n", line.substring(1)));
            }
        }
        return listHTML;
    }

    private String processListStart(String listStartingTag) {
        // TEMPORARY: Assume an unordered list.
        return "<ul>\n";
    }

    private String processListEnd(String listEndingTag) {
        // TEMPORARY: Assume an unordered list.
        return "</ul>\n";
    }
}
