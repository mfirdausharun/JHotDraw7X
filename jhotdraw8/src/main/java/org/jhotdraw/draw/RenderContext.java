/* @(#)RenderContext.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.draw.figure.Figure;
import javafx.scene.Node;
import org.jhotdraw.beans.PropertyBean;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.SimpleKey;

/**
 * RenderContext.
 *
 * @design.pattern RenderContext Builder, Client.
 * The builder pattern is used for the creation of a JavaFX scene graph from
 * a Figure. The creation of the scene graph is delegated to the methods 
 * Figure.createNode and Figure.updateNode. Typically each concrete Figure
 * class will generate a different scene graph. The same Figure object may also 
 * create different scene graphs depending on property values of the RenderContext.
 * For example a PageFigure will render the current page number of the
 * PrintRenderContext.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface RenderContext extends PropertyBean {
    // ---
    // keys
    // ---
    Key<RenderingIntent> RENDERING_INTENT = new SimpleKey<>("renderingIntent",RenderingIntent.class,RenderingIntent.EDITOR);
    // ---
    // behavior
    // ---
    /**
     * Gets the JavaFX node which is used to render the specified figure by this
     * {@code RenderContext}.
     *
     * @param f The figure
     * @return The JavaFX node associated to the figure
     */
    Node getNode(Figure f);

}
