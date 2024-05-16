package main;
public interface Parser {
    /**
     * parseChunk parses a series of lines of the concrete type of this interface's implementation, generating a single HTML string to represent that chunk of lines.
     * The HTML string is then returned to the caller for integration in place of the chunk of lines.
     *
     * @param parsedMDPP The ParsedMDPP object to insert file-level information (e.g. the title) into, as these pieces of data may be encountered at any time.
     * @param lines The lines of the chunk being analyzed.
     * @return The String of HTML that will "replace" this chunk in the final file.
     */
    public String parseChunk(ParsedMDPP parsedMDPP, String[] lines) throws SyntaxException;
}
