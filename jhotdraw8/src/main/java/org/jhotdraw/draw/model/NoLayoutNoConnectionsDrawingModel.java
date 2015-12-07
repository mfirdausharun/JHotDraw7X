/* @(#)NoLayoutNoConnectionsDrawingModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.model;

import javafx.scene.transform.Transform;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.key.FigureKey;

/**
 * This drawing model assumes that the drawing contains no figures which perform
 * layouts and no getConnectedFigures between figures.
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NoLayoutNoConnectionsDrawingModel extends AbstractDrawingModel {

    @Override
    public void setRoot(Drawing root) {
        this.root = root;
        fire(DrawingModelEvent.rootChanged(this, root));
    }

    @Override
    public void removeFromParent(Figure child) {
        Drawing oldDrawing = child.getDrawing();
        Figure parent = child.getParent();
        if (parent != null) {
            int index = parent.getChildren().indexOf(child);
            if (index != -1) {
                parent.getChildren().remove(index);
                fire(DrawingModelEvent.figureRemovedFromParent(this, parent, child, index));
                fire(DrawingModelEvent.nodeInvalidated(this, parent));
            }
        }
        Drawing newDrawing = child.getDrawing();
        if (oldDrawing != newDrawing) {
            if (oldDrawing != null) {
                fire(DrawingModelEvent.figureRemovedFromDrawing(this, oldDrawing, child));
            }
            if (newDrawing != null) {
                fire(DrawingModelEvent.figureAddedToDrawing(this, newDrawing, child));
            }
        }
    }

    @Override
    public void insertChildAt(Figure child, Figure parent, int index) {
        Drawing oldDrawing = child.getDrawing();
        if (child.getParent() != null) {
            child.getParent().remove(child);
        }
        parent.getChildren().add(index, child);
        fire(DrawingModelEvent.figureAddedToParent(this, parent, child, index));
        fire(DrawingModelEvent.nodeInvalidated(this, parent));
        Drawing newDrawing = child.getDrawing();
        if (oldDrawing != newDrawing) {
            if (oldDrawing != null) {
                fire(DrawingModelEvent.figureRemovedFromDrawing(this, oldDrawing, child));
            }
            if (newDrawing != null) {
                fire(DrawingModelEvent.figureAddedToDrawing(this, newDrawing, child));
            }
        }
    }

    @Override
    public <T> T set(Figure figure, MapAccessor<T> key, T newValue) {
        T oldValue = figure.set(key, newValue);
        if (oldValue != newValue) {

            final DirtyMask dm;
            if (key instanceof FigureKey) {
                FigureKey<T> fk = (FigureKey<T>) key;
                dm = fk.getDirtyMask();
            } else {
                dm = DirtyMask.EMPTY;
            }

            if (dm.containsOneOf(DirtyBits.NODE)) {
                fire(DrawingModelEvent.nodeInvalidated(this, figure));
            }
        }
        return oldValue;
    }

    @Override
    public void reshape(Figure f, Transform transform) {
        f.reshape(transform);
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, f));
    }

    @Override
    public void reshape(Figure f, double x, double y, double width, double height) {
        f.reshape(x, y, width, height);
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, f));
    }

    @Override
    public void layout(Figure f) {
        f.layout();
        // no event fired! fire(DrawingModelEvent.subtreeNodesInvalidated(this,f));
    }

    @Override
    public void disconnect(Figure figure) {
        // no event fired! 
        figure.disconnect();
    }

    @Override
    public void applyCss(Figure figure) {
        figure.applyCss();
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, figure));
    }
}
