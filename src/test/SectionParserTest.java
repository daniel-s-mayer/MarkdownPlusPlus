package test;

import main.SectionParser;
import org.junit.Before;
import org.junit.Test;

public class SectionParserTest {
    private SectionParser testSectionParser;

    @Before
    public void setUpTestSectionParser() {
        this.testSectionParser = new SectionParser();
    }

    /**
     * Test that parseSectionStart() produces the correct HTML output for a valid section start tag.
     */

    /**
     * Test that parseSectionStart() throws an exception for an invalid section start tag.
     */

    /**
     * Test that getOptionsCSS() correctly converts an options map to CSS options, both for the special case section fields and general-case CSS fields.
     */

    /**
     * Test that getImageOptionsCSS() correctly converts an options map to CSS options for an image, both for special-case image properties and general CSS classes.
     */

    /**
     * Test that processImage() returns the correct HTML for the specified image.
     */

    /**
     * Test that processImage() throws an exception for a malformed request.
     */
}
