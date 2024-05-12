public class BaseControlSequenceHandler {
    // Parses the base control line (e.g. title).
    public void parseLine(String line, ParsedMDPP pmpp) throws SyntaxException {
        if (line.length() < 1 || line.charAt(0) != '&') {
            throw new SyntaxException("Unable to parse a base control sequence.");
        }
        // Identify the command type
        ParserUtilities parserUtilities = new ParserUtilities();
        String commandType = parserUtilities.regexExtractSingleString(line, "^&&:(.*)\\{");
        System.out.println("Command type: " + commandType);
        switch (commandType) {
            case "page-title":
                pmpp.setPageTitle(parserUtilities.regexExtractSingleString(line, "^&&:.*\\{(.*)}"));
                break;
            case "css-link":
                pmpp.addCSSURL(parserUtilities.regexExtractSingleString(line, "/^&&:.*{(.*)}/gm"));
                break;
            default:
                throw new SyntaxException("Invalid base-level command.");
        }

    }
}
