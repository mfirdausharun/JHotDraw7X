/* @(#)AbstractSelectionInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.util.Collections;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.model.DrawingModel;

/**
 * AbstractSelectionInspector.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractSelectionInspector implements Inspector {

    protected DrawingView drawingView;

    private final SetChangeListener<Figure> selectionListener = change -> {
        handleSelectionChanged(drawingView==null?FXCollections.emptyObservableSet():drawingView.getSelectedFigures());
    };

    @Override
    public void setDrawingView(DrawingView newValue) {
        DrawingView oldValue = drawingView;
        if (oldValue != null) {
            oldValue.selectedFiguresProperty().removeListener(selectionListener);
        }
        this.drawingView = newValue;
        if (newValue != null) {
            newValue.selectedFiguresProperty().addListener(selectionListener);
        }
        handleDrawingViewChanged(oldValue,newValue);
    }
    
    protected Set<Figure> getSelectedFigures() {
        return drawingView==null?Collections.emptySet():drawingView.getSelectedFigures();
    }
    protected DrawingModel getDrawingModel() {
        return drawingView.getModel();
    }

    /**
     * Can be implemented by subclasses.
     *
     * @param oldValue the old selection
     * @param newValue the new selection
     */
    protected void handleDrawingViewChanged(DrawingView oldValue, DrawingView newValue) {
        
    }
    /**
     * Must be implemented by subclasses.
     *
     * @param newValue the new selection
     */
    protected abstract void handleSelectionChanged(Set<Figure> newValue);
}
