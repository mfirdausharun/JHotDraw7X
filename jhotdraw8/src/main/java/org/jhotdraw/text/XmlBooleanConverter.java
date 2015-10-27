/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javafx.scene.shape.SVGPath;
import org.jhotdraw.draw.io.IdFactory;

/**
 * Converts a {@code Boolean} into the XML String representation.
 * <p>
 * Reference:
 * <a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#double">W3C: XML
 * Schema Part 2: Datatypes Second Edition: 3.2.5 double</a>
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlBooleanConverter implements Converter<Boolean> {

    private static final long serialVersionUID = 1L;

    private final String trueString = "true";
    private final String falseString = "false";

    /**
     * Creates a new instance.
     */
    public XmlBooleanConverter() {
    }

    @Override
    public void toString(Appendable buf, IdFactory idFactory, Boolean value) throws IOException {
        buf.append(value ? trueString : falseString);
    }

    @Override
    public Boolean fromString(CharBuffer in, IdFactory idFactory) throws ParseException {
        int pos = in.position();
        StringBuilder out = new StringBuilder();
        while (in.remaining() > 0 && !Character.isWhitespace(in.charAt(0))) {
            out.append(in.get());
        }
        String str = out.toString();
        if (str.equals(trueString)) {
            return true;
        } else if (str.equals(falseString)) {
            return false;
        } else {
            in.position(pos);
            throw new ParseException("\"" + trueString + "\" or \"" + falseString + "\" expected instead of \"" + str + "\".", pos);
        }
    }
}
