/* @(#)StrokedShapeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import java.util.Objects;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import static org.jhotdraw.draw.figure.StrokeableFigure.STROKE_COLOR;
import org.jhotdraw.draw.key.PaintStyleableFigureKey;

/**
 * Interface figures which render a {@code javafx.scene.shape.Shape} and
 * can be filled.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FillableFigure extends Figure {

    /**
     * Defines the paint used for filling the interior of the figure. Default
     * value: {@code Color.WHITE}.
     */
    public static PaintStyleableFigureKey FILL_COLOR = new PaintStyleableFigureKey("fill", Color.WHITE);
    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyFillableFigureProperties(Shape shape) {
        Paint p = getStyled(FILL_COLOR);
        if (!Objects.equals(shape.getStroke(), p)) {
            shape.setFill(p);
        }
    }



}
