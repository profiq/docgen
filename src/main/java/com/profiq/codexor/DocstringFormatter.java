package com.profiq.codexor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes in a response from OpenAI API and formats it into a proper Python docstring.
 */
public class DocstringFormatter {

    private final String openaiResponse;
    private String[] docstringLines = null;

    public DocstringFormatter(String openaiResponse) {
        this.openaiResponse = openaiResponse;
    }

    public String docstringWithIndentation(String indentation) {
        String[] docstringLines = getDocstringLines();

        StringBuilder docstring = new StringBuilder();
        for (String line : docstringLines) {
            docstring.append(indentation).append(line).append("\n");
        }

        return docstring.toString();
    }

    private String[] getDocstringLines() {
        if (docstringLines == null) {
            Pattern docstringRegex = Pattern.compile("(([ \t]*)\"\"\".*\"\"\")", Pattern.DOTALL);
            Matcher docstringMatcher = docstringRegex.matcher(openaiResponse);

            if (!docstringMatcher.find()) {
                throw new IllegalArgumentException("No docstring found in the OpenAI response.");
            }

            String docstring = docstringMatcher.group(1);
            String indentation = docstringMatcher.group(2);
            String[] docstringLinesRaw = docstring.split("\n");

            docstringLines = new String[docstringLinesRaw.length];
            int substringIdx = 0;

            for (int i = 0; i < docstringLinesRaw.length; i++) {
                substringIdx = Math.min(docstringLinesRaw[i].length(), indentation.length());
                docstringLines[i] = docstringLinesRaw[i].substring(substringIdx);
            }
        }

        return docstringLines;
    }
}
