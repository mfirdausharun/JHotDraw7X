/* @(#)AbstractHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.handle;

import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.event.Listener;

/**
 * AbstractHandle.
 * @author Werner Randelshofer
 * @version $Id$
 * @param <F> the supported owner type
 */
public abstract class AbstractHandle implements Handle {
    // ---
    // Fields
    // ---
    protected final Figure owner;

    // ---
    // Constructors
    // ---
    public AbstractHandle(Figure owner) {
        this.owner = owner;
    }

    // ---
    // Behavior
    // ---
    @Override
    public final void dispose() {
    }

    @Override
    public Figure getOwner() {
       return owner;
    }
}
