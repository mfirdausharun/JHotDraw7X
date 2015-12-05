/* @(#)CssStringConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw.css.CssTokenizer;
import org.jhotdraw.draw.io.IdFactory;
import org.jhotdraw.io.CharBufferReader;

/**
 * Converts an {@code String} to a quoted CSS {@code String}.
 * <pre>
 * unicode       = '\' , ( 6 * hexd
 *                       | hexd , 5 * [hexd] , w
 *                       );
 * escape        = ( unicode
 *                 | '\' , -( newline | hexd)
 *                 ) ;
 * string        = string1 | string2 ;
 * string1       = '"' , { -( '"' ) | '\\' , newline |  escape } , '"' ;
 * string2       = "'" , { -( "'" ) | '\\' , newline |  escape } , "'" ;
 * </pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssStringOrIdentConverter implements Converter<String> {

    @Override
    public String fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(buf));
        if (tt.nextToken() != CssTokenizer.TT_STRING && tt.currentToken()!= CssTokenizer.TT_IDENT) {
            throw new ParseException("Css String or Ident expected. " + tt.currentToken(), buf.position());
        }
        return tt.currentStringValue();
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, String value) throws IOException {
        StringBuffer buf = new StringBuffer();
        boolean isIdent = true;
        buf.append('"');
        for (char ch : value.toCharArray()) {
            switch (ch) {
                case '"':
                    buf.append('\\');
                    buf.append('"');
                    isIdent = false;
                    break;
                case ' ':
                    buf.append(ch);
                    isIdent = false;
                    break;
                case '\n':
                    buf.append('\\');
                    buf.append('\n');
                    isIdent = false;
                    break;
                default:
                    if (Character.isISOControl(ch) || Character.isWhitespace(ch)) {
                        buf.append('\\');
                        String hex = Integer.toHexString(ch);
                        for (int i = 0, n = 6 - hex.length(); i < n; i++) {
                            buf.append('0');
                        }
                        buf.append(hex);
                    } else {
                        buf.append(ch);
                    }
                    break;
            }
        }
        buf.append('"');
        if (isIdent) {
            out.append(value);
        } else {
            out.append(buf.toString());
        }
    }

    @Override
    public String getDefaultValue() {
        return "";
    }
}
