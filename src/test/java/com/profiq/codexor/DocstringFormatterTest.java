package com.profiq.codexor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocstringFormatterTest {

    @Test
    public void testClean() {
        String response = "\"\"\"\n" +
            "This is a docstring.\n" +
            "It has multiple lines.\n" +
            "\"\"\"\n";

        DocstringFormatter formatter = new DocstringFormatter(response);
        String docstring = formatter.docstringWithIndentation("");
        assertEquals(response, docstring);
    }

    @Test
    public void testIndentationRemoval() {
        String response = "    \"\"\"\n" +
            "    This is a docstring.\n" +
            "    It has multiple lines.\n" +
            "    \"\"\"\n";

        String expected = "\"\"\"\n" +
            "This is a docstring.\n" +
            "It has multiple lines.\n" +
            "\"\"\"\n";

        DocstringFormatter formatter = new DocstringFormatter(response);
        String docstring = formatter.docstringWithIndentation("");
        assertEquals(expected, docstring);
    }

    @Test
    public void testWithSurroundingText() {
        String response = "This is some text.\n" +
            "\"\"\"\n" +
            "This is a docstring.\n" +
            "It has multiple lines.\n" +
            "\"\"\"\n" +
            "This is some more text.\n";

        String expected = "\"\"\"\n" +
            "This is a docstring.\n" +
            "It has multiple lines.\n" +
            "\"\"\"\n";

        DocstringFormatter formatter = new DocstringFormatter(response);
        String docstring = formatter.docstringWithIndentation("");
        assertEquals(expected, docstring);
    }

    @Test
    public void testWithSurroundingTextAndIndentation() {
        String response = "This is some text.\n" +
            "    \"\"\"\n" +
            "    This is a docstring.\n" +
            "    It has multiple lines.\n" +
            "    \"\"\"\n" +
            "This is some more text.\n";

        String expected = "\"\"\"\n" +
            "This is a docstring.\n" +
            "It has multiple lines.\n" +
            "\"\"\"\n";

        DocstringFormatter formatter = new DocstringFormatter(response);
        String docstring = formatter.docstringWithIndentation("");
        assertEquals(expected, docstring);
    }

    @Test
    public void testEmptyLineWithIndentaiton() {
        String response = "    \"\"\"\n" +
            "    This is a docstring.\n" +
            "\n" +
            "    It has multiple lines.\n" +
            "    \"\"\"\n";

        String expected = "\"\"\"\n" +
            "This is a docstring.\n" +
            "\n" +
            "It has multiple lines.\n" +
            "\"\"\"\n";

        DocstringFormatter formatter = new DocstringFormatter(response);
        String docstring = formatter.docstringWithIndentation("");
        assertEquals(expected, docstring);
    }

    @Test
    public void testIndentationOnOutput() {
        String response = "\"\"\"\n" +
            "This is a docstring.\n" +
            "It has multiple lines.\n" +
            "\"\"\"\n";

        String expected = "    \"\"\"\n" +
            "    This is a docstring.\n" +
            "    It has multiple lines.\n" +
            "    \"\"\"\n";

        DocstringFormatter formatter = new DocstringFormatter(response);
        String docstring = formatter.docstringWithIndentation("    ");
        assertEquals(expected, docstring);
    }

    @Test
    public void testNoDocstring() {
        String response = "This is some text.\n" +
            "This is some more text.\n";

        DocstringFormatter formatter = new DocstringFormatter(response);
        try {
            formatter.docstringWithIndentation("");
        } catch (IllegalArgumentException e) {
            assertEquals("No docstring found in the OpenAI response.", e.getMessage());
        }
    }

}
