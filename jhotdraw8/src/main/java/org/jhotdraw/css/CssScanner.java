/* @(#)CssScanner.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css;

import java.io.IOException;
import java.io.Reader;
import java.util.Deque;
import java.util.LinkedList;

/**
 * The {@code CssScanner} preprocesses an input stream of UTF-16 code points for
 * the {@code CssTokenizer}.
 * <p>
 * The scanner also keeps track of the current position and line number and
 * supports lookahead of characters.
 * <p>
 * The scanner implements the following ISO 14977 EBNF productions:
 * <pre>
 * char          = inline | newline;
 * newline       = ( '\r' , ['\n']
 *                 | '\n' | '\t' | '\f
 *                 );
 * inline        = legalInline | illegalInline ;
 * legalInline   = ? all UTF-16 code points except '\r', '\n', '\t', '\f', '\0' ?
 * illegaInline  = '\0' ;
 * </pre>
 * <p>Any {@code illegalInline} production is replaced with U+FFFD
 * REPLACEMENT CHARACTER. Any {@code newline} production is replaced with U+000A
 * LINE FEED CHARACTER.
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://www.w3.org/TR/2014/CR-css-syntax-3-20140220/">CSS Syntax
 * Module Level 3, Chapter 3.3. Preprocessing the input stream</a></li>
 * </ul>
 *
 *
 * @author Werner Randelshofer
 */
public class CssScanner {

    /**
     * The underlying reader.
     */
    private Reader in;

    /**
     * The current position in the input stream.
     */
    private long position;
    /**
     * The current line number in the input stream.
     */
    private long lineNumber;

    /**
     * The current character.
     */
    private int currentChar;

    /**
     * Whether we need to skip a linefeed on the next read.
     */
    private boolean skipLF;

    /**
     * Stack of pushed back characters.
     */
    private final Deque<Integer> pushedChars = new LinkedList<>();

    public CssScanner(Reader reader) {
        this.in = reader;
    }

    /**
     * Phase 2: Processes unicode escape sequences first, and then processes
     * newlines.
     *
     * @return the next character. Returns -1 if EOF.
     * @throws IOException from the underlying input stream
     */
    public int nextChar() throws IOException {
        if (!pushedChars.isEmpty()) {
            currentChar = pushedChars.pop();
            return currentChar;
        }

        currentChar = in.read();
        if (skipLF && currentChar == '\n') {
            skipLF = false;
            position++;
            currentChar = in.read();
        }

        switch (currentChar) {
            case -1: // EOF
                break;
            case '\r': // translate "\r", "\r\n" into "\n"
                skipLF = true;
                currentChar = '\n';
                lineNumber++;
                position++;
                break;
            case '\f': // translate "\f" into "\n"
                currentChar = '\n';
                lineNumber++;
                position++;
                break;
            case '\n':
                lineNumber++;
                position++;
                break;
            case '\000':
                currentChar = '\ufffd';
                break;
            default:
                position++;
                break;
        }

        return currentChar;
    }

    /**
     * Returns the current character.
     *
     * @return the current character
     */
    public int currentChar() {
        return currentChar;
    }

    /**
     * Pushes the specified character back into the scanner.
     * @param ch The character to be pushed back
     */
    public void pushBack(int ch) {
        pushedChars.push(ch);
    }

    /**
     * Returns the position in the input stream.
     * @return the position
     */
    public long getPosition() {
        return position;
    }

    /**
     * Returns the line number in the input stream.
     * @return the line number
     */
    public long getLineNumber() {
        return lineNumber;
    }

}
