package test;

import main.ParserUtilities;
import main.SyntaxException;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test the general parser utilities, including inline style and link processing.
 */
public class ParserUtilitiesTest {
    private ParserUtilities testUtilities;

    @Before
    public void setUpParserUtilitiesTests() {
        testUtilities = new ParserUtilities();
    }

    /**
     * Test that a general line containing code, bold, and italic components is correctly converted to HTML.
     */
    @Test
    public void testParseGeneralLineCodeBoldItalic() {
        String result = "";
        try {
            result = testUtilities.parseGeneralLine("test `code` *bold* _italic_", true);
            assertTrue(result.equals("<p>test <code>code</code> <b>bold</b> <i>italic</i></p>"));
        } catch (SyntaxException se) {
            fail(); // This request has valid syntax.
        }
    }
    /**
     * Test that a general line containing bold italic text is parsed correctly.
     */
    @Test
    public void testParseGeneralLineBoldItalic() {
        String result = "";
        try {
            result = testUtilities.parseGeneralLine("test *_bold italic_*", true);
            assertTrue(result.equals("<p>test <b><i>bold italic</i></b></p>"));
        } catch (SyntaxException se) {
            fail(); // The syntax is correct, so an exception constitutes failure.
        }
    }
    /**
     * Test that a header line results in the correct heading level for the output.
     */
    @Test
    public void testParseHeaderLine() {
        String result = "";
        try {
            result = testUtilities.parseHeaderLine("## Test Heading");
            assertTrue(result.equals("<h2> Test Heading</h2>\n"));
        } catch (SyntaxException se) {
            fail(); // The syntax is correct, so an exception constitutes failure.
        }
    }

    /**
     * Test that getStopPoint() reports the correct stop point for a section.
     */
    @Test
    public void testGetStopPoint() {
        String[] section = {"+start-section:mySection{}{}", "content", "content", ">end-section:mySection{}{}"};
        try {
            assertTrue(testUtilities.getStopPoint(section, 0, 4, '+', '>') == 3);
        } catch (SyntaxException se) {
            fail(); // There should not be a syntax error here.
        }
    }

    /**
     * Test that getStopPointString() correctly returns the stop point for a tagged bold item.
     */
    @Test
    public void testGetStopPointString() {
        try {
            assertTrue(testUtilities.getStopPointString("`mystring`", 1, '`') == 9);
        } catch (SyntaxException se) {
            fail(); // There should not be a syntax exception.
        }
    }
    /**
     * Test that regexExtractSingleString() extracts the correct string (specified by the regex) from the overall provided string.
     */
    @Test
    public void testRegexExtractSingleStringSuccess() {
        try {
            assertTrue(testUtilities.regexExtractSingleString("test1:test2:test3", ".*:(.*):.*").equals("test2"));
        } catch (SyntaxException se) {
            fail(); // A syntax exception here would indicate invalid regex processing.
        }
    }
    /**
     * Test that regexExtractSingleString() fails when the requested string does not have a RegEx match.
     */
    @Test
    public void testRegexExtractSingleStringFailure() {
        try {
           testUtilities.regexExtractSingleString("test1test2:test3", ".*:(.*):.*").equals("test2");
            fail(); // If no exception was thrown, the test failed.
        } catch (SyntaxException se) {
            // Don't do anything -- this is a success.
        }
    }

    /**
     * Test that processing a link yields the correct HTML for the inline link.
     */
    @Test
    public void testProcessingLink() {
        try {
            assertTrue(testUtilities.processSpecialInline("link:Test{https://example.com}{class}").equals("<a href=\"https://example.com\" class=\"class\">Test</a>"));
        } catch (SyntaxException se) {
            fail(); // A syntax exception here would indicate invalid regex processing.
        }
    }
    /**
     * Test that generating a list of options pairs from a well-formed option string works correctly.
     */
    @Test
    public void testGenerateOptionsPairsSuccess() {
        String optionString = "test:test{color:black;size:10}";
        try {
            Map<String, String> generatedOptions = testUtilities.generateOptions(optionString);
            assertTrue(generatedOptions.entrySet().size() == 2);
            assertTrue(generatedOptions.containsKey("color"));
            assertTrue(generatedOptions.containsKey("size"));
            assertTrue(generatedOptions.get("color").equals("black"));
        } catch (SyntaxException se) {
            fail(); // The well-formed option string will not cause failure in a correct implementation.
        }
    }
}
