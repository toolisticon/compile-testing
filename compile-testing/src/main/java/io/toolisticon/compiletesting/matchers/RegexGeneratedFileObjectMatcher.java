package io.toolisticon.compiletesting.matchers;

import io.toolisticon.compiletesting.FailingAssertionException;
import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;

import javax.tools.FileObject;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Validates a generated file by using a regular expression.
 */
class RegexGeneratedFileObjectMatcher implements GeneratedFileObjectMatcher<FileObject> {

    private final String patternString;
    private final Pattern pattern;

    /**
     * Hidden constructor.
     *
     * @param patternString a valid pattern string
     * @throws PatternSyntaxException If the expression's syntax is invalid
     */
    RegexGeneratedFileObjectMatcher(String patternString) {

        this.patternString = patternString;
        this.pattern = Pattern.compile(patternString, Pattern.MULTILINE | Pattern.UNIX_LINES | Pattern.DOTALL);
    }

    @Override
    public boolean check(FileObject fileObject) throws IOException {

        String content = fileObject.getCharContent(true).toString();

        if (!pattern.matcher(content).matches()) {
            throw new FailingAssertionException(String.format("File '%s' doesn't match the following pattern: '%s'!", content /* fileObject.getName().toString()*/, patternString));
        }

        return true;
    }


}
