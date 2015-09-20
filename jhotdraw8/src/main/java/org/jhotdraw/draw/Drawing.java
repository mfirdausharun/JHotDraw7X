/* @(#)Drawing.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.net.URI;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw.collection.Key;

/**
 * A <em>drawing</em> is an image composed of graphical (figurative) elements.
 * <p>
 * The graphical elements are represented by {@link Figure} objects.
 * The figure objects are organized in a tree structure of which the drawing
 * object is the root.
 * <p>
 * {@code Drawing} extends {@code Figure}. This allows to compose a
 * drawing from other drawings.
 * <p>
 * A drawing has the following features:
 * <ul>
 * <li>A drawing has a style sheet which defines the visual representation of
 * JavaFX {@code Node}s generated by its figures.</li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Drawing extends Figure {
    public final static Key<URI> STYLESHEET = new Key<>("stylesheet", URI.class, null);
    public final static Key<Rectangle2D> BOUNDS = new Key<>("bounds", Rectangle2D.class, new Rectangle2D(0, 0, 640, 480));
    public final static Key<Paint> BACKGROUND_PAINT = new Key<>("background", Paint.class, Color.WHITE);

}
