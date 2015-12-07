/* @(#)GroupFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import org.jhotdraw.draw.figure.AbstractCompositeFigure;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.draw.connector.Connector;

/**
 * GroupFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GroupFigure extends AbstractCompositeFigure implements TransformableFigure, HideableFigure, StyleableFigure, LockableFigure {

    /**
     * The CSS type selector for group objects is @code("group"}.
     */
    public final static String TYPE_SELECTOR = "group";

    @Override
    public void reshape(Transform transform) {
        // FIXME needs to transform the transform into local coordinates
        for (Figure child : childrenProperty()) {
            child.reshape(transform);
        }
    }

    @Override
    public void updateNode(RenderContext v, Node n) {
        applyHideableFigureProperties(n);
        applyTransformableFigureProperties(n);
        ObservableList<Node> group = ((Group) n).getChildren();
        group.clear();
        for (Figure child : childrenProperty()) {
            group.add(v.getNode(child));
        }
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        g.setAutoSizeChildren(false);
        return g;
    }

    @Override
    public boolean isLayoutable() {
        return false;
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return null;
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
