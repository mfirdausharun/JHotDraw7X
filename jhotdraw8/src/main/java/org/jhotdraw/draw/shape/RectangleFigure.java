/* @(#)RectangleFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import static java.lang.Math.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.DirtyBits;
import org.jhotdraw.draw.DirtyMask;
import org.jhotdraw.draw.DrawingRenderer;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.FigureKey;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;

/**
 * Renders a {@code javafx.scene.shape.Rectangle}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RectangleFigure extends AbstractConnectableShapeFigure {

    public final static FigureKey<Rectangle2D> RECTANGLE = new FigureKey<>("rectangle", Rectangle2D.class, DirtyMask.of(DirtyBits.NODE,DirtyBits.GEOMETRY,DirtyBits.LAYOUT_BOUNDS,DirtyBits.VISUAL_BOUNDS),new Rectangle2D(0, 0, 1, 1));
    public final static FigureKey<Double> ARC_HEIGHT = new FigureKey<>("arcHeight",Double.class, DirtyMask.of(DirtyBits.NODE,DirtyBits.GEOMETRY), 0.0);
    public final static FigureKey<Double> ARC_WIDTH = new FigureKey<>("arcWidth", Double.class, DirtyMask.of(DirtyBits.NODE,DirtyBits.GEOMETRY), 0.0);

    public RectangleFigure() {
        this(0, 0, 1, 1);
    }

    public RectangleFigure(double x, double y, double width, double height) {
        set(RECTANGLE, new Rectangle2D(x, y, width, height));
    }

    public RectangleFigure(Rectangle2D rect) {
        set(RECTANGLE, rect);
    }

    @Override
    public Bounds getBoundsInLocal() {
        Rectangle2D r= get(RECTANGLE);
        return new BoundingBox(r.getMinX(),r.getMinY(),r.getWidth(),r.getHeight());
    }

    @Override
    public void reshape(Transform transform) {
        Rectangle2D r = get(RECTANGLE);
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        reshape(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        set(RECTANGLE, new Rectangle2D(x+min(width,0),y+min(height,0),abs(width),abs(height)));
    }

    @Override
    public Node createNode(DrawingRenderer drawingView) {
        return new Rectangle();
    }

    @Override
    public void updateNode(DrawingRenderer drawingView, Node node) {
        Rectangle rectangleNode = (Rectangle) node;
        applyFigureProperties(rectangleNode);
        updateShapeProperties(rectangleNode);
        Rectangle2D r = get(RECTANGLE);
        rectangleNode.setX(r.getMinX());
        rectangleNode.setY(r.getMinY());
        rectangleNode.setWidth(r.getWidth());
        rectangleNode.setHeight(r.getHeight());
        rectangleNode.setArcWidth(get(ARC_WIDTH));
        rectangleNode.setArcHeight(get(ARC_HEIGHT));
    }
    @Override
    public Connector findConnector(Point2D p, ConnectionFigure prototype) {
        return new ChopRectangleConnector();
    }
}
