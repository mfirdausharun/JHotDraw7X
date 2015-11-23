/* @(#)CssPoint2DConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.Insets;
import org.jhotdraw.draw.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.Insets} into a {@code String} and vice
 * versa.
 * <p>
 * List of four sizes in the sequence top, right, bottom, left. If left is
 * omitted, it is the same as right. If bottom is omitted, it is the same as
 * top. If right is omitted it is the same as top.
 * <pre>
 * insets       = top , right , bottom, left ;
 * </pre> *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssInsetsConverter implements Converter<Insets> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,size}|[ ]+}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, Insets value) throws IOException {
        if (value.getRight() == value.getLeft()) {
            if (value.getTop() == value.getBottom()) {
                if (value.getTop() == value.getLeft()) {
                    formatter.toStr(out, idFactory, 1, value.getTop());
                } else {
                    formatter.toStr(out, idFactory, 2, value.getTop(), value.getRight());
                }
            } else {
                formatter.toStr(out, idFactory, 3, value.getTop(), value.getRight(), value.getBottom());
            }
        } else {
            formatter.toStr(out, idFactory, 4, value.getTop(), value.getRight(), value.getBottom(), value.getLeft());
        }
    }

    @Override
    public Insets fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        switch ((int) v[0]) {
            case 1:
                return new Insets((double) v[1], (double) v[1], (double) v[1], (double) v[1]);
            case 2:
                return new Insets((double) v[1], (double) v[2], (double) v[1], (double) v[2]);
            case 3:
                return new Insets((double) v[1], (double) v[2], (double) v[3], (double) v[2]);
            case 4:
                return new Insets((double) v[1], (double) v[2], (double) v[3], (double) v[4]);
            default:
                throw new ParseException("Insets with 1 to 4 dimension values expected.", buf.position());
        }
    }

    @Override
    public Insets getDefaultValue() {
        return new Insets(0, 0, 0, 0);
    }
}
