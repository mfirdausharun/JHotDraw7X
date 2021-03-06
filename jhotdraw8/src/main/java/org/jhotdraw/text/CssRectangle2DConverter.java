/* @(#)CssRectangle2DConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.draw.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.Rectangle2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssRectangle2DConverter implements Converter<Rectangle2D> {

    private final PatternConverter formatter = new PatternConverter("{0,number} +{1,number} +{2,number} +{3,number}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, Rectangle2D value) throws IOException {
        formatter.toStr(out,idFactory, value.getMinX(), value.getMinY(), value.getWidth(), value.getHeight());
    }

    @Override
    public Rectangle2D fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new Rectangle2D((double) v[0], (double) v[1],(double) v[2], (double) v[3]);
    }
    @Override
    public Rectangle2D getDefaultValue() {
        return new Rectangle2D(0,0,1,1);
    }
}
