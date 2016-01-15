/* @(#)HandleTracker.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.util.Collection;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.handle.Handle;

/**
 * A <em>handle tracker</em> provides the behavior for manipulating a
 * {@link Handle} of a figure to the {@link SelectionTool}.
 *
 * @design.pattern SelectionTool Strategy, Strategy.
 * 
 * @design.pattern HandleTracker Chain of Responsibility, Handler.
 * Mouse and keyboard events occur on a {@link org.jhotdraw.draw.DrawingView}, 
 * and are preprocessed by {@link SelectionTool}, and then by 
 * {@link HandleTracker}. {@code HandleTracker} invokes corresponding methods
 * on a {@link Handle} which in turn changes an aspect of a figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface HandleTracker extends Tracker {

    public void setHandles(Handle handle, Collection<Figure> compatibleFigures);

}
