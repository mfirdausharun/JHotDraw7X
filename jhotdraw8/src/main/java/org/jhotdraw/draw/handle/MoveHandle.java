/* @(#)ConnectionFigureConnectionHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.util.HashSet;
import java.util.Set;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TransformableFigure;
import static org.jhotdraw.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw.draw.locator.Locator;
import org.jhotdraw.draw.model.DrawingModel;

/**
 * Handle for moving (translating) a figure.
 *
 * @author Werner Randelshofer
 */
public class MoveHandle extends LocatorHandle {
    private Point2D pickLocation;
    private Point2D oldPoint;
    private final Region node;
    private final String styleclass;
    private static final Rectangle REGION_SHAPE = new Rectangle(5, 5);
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    protected Set<Figure> groupReshapeableFigures;

    public MoveHandle(Figure figure, Locator locator) {
        this(figure, STYLECLASS_HANDLE_MOVE, locator);
    }

    public MoveHandle(Figure figure, String styleclass, Locator locator) {
        super(figure, locator);
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);

        // The node size must be odd. 
        // This size is independent of the shape that represenst the handle.
        node.resize(11, 11);

        node.getStyleClass().clear();
        node.getStyleClass().add(styleclass);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public Cursor getCursor() {
        return Cursor.OPEN_HAND;
    }

    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = view.getWorldToView().createConcatenation(f.getLocalToWorld());
        Bounds b = f.getBoundsInLocal();
        Point2D p = getLocation();
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        pickLocation = p = t.transform(p);

        // The node is centered around the location. 
        // (The value 5.5 is half of the node size, which is 11,11.
        // 0.5 is subtracted from 5.5 so that the node snaps between pixels
        // so that we get sharp lines. 
        node.relocate(p.getX() - 5, p.getY() - 5);

        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
    }

    @Override
    public void onMousePressed(MouseEvent event, DrawingView view) {
        oldPoint = view.getConstrainer().constrainPoint(getOwner(), view.viewToWorld(new Point2D(event.getX(), event.getY())));

        // determine which figures can be reshaped together as a group
        Set<Figure> selectedFigures = view.getSelectedFigures();
        groupReshapeableFigures = new HashSet<>();
        for (Figure f : view.getSelectedFigures()) {
            if (f.isGroupReshapeableWith(selectedFigures)) {
                groupReshapeableFigures.add(f);
            }
        }
        groupReshapeableFigures = view.getFiguresWithCompatibleHandle(groupReshapeableFigures, this);
    }

    @Override
    public void onMouseDragged(MouseEvent event, DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
        }

        if (event.isMetaDown()) {
            // meta snaps the location of the handle to the grid
            Point2D loc = getLocation();
            oldPoint = getOwner().localToWorld(loc);
        }

        //Transform tx = Transform.translate(newPoint.getX() - oldPoint.getX(), newPoint.getY() - oldPoint.getY());
        DrawingModel model = view.getModel();

        if (event.isShiftDown()) {
            // shift transforms all selected figures
            for (Figure f : groupReshapeableFigures) {
                Point2D npl = f.worldToParent(newPoint);
                Point2D opl = f.worldToParent(oldPoint);
            Transform tt = ((TransformableFigure)f).getInverseTransform();
            npl=tt.transform(npl);
            opl=tt.transform(opl);
                Transform tx = Transform.translate(npl.getX() - opl.getX(), npl.getY() - opl.getY());
                //tx = f.getWorldToParent().createConcatenation(tx);
                model.reshape(f, tx);
            }
        } else {
            Figure f = getOwner();
            Point2D npl = f.worldToParent(newPoint);
            Point2D opl = f.worldToParent(oldPoint);
            Transform tt = ((TransformableFigure)f).getInverseTransform();
            npl=tt.transform(npl);
            opl=tt.transform(opl);
            Transform tx = Transform.translate(npl.getX() - opl.getX(), npl.getY() - opl.getY());
            model.reshape(f, tx);
        }
        oldPoint = newPoint;
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView dv) {
        // FIXME fire undoable edit
    }

    @Override
    public boolean isSelectable() {
        return true;
    }
        @Override
    public Point2D getLocationInView() {
        return pickLocation;
    }
}
