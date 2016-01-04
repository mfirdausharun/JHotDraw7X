/*
 * @(#)AbstractCompositeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.collection.IndexedSet;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * This base class can be used to implement figures which support child figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractCompositeFigure extends AbstractFigure {

    private class ChildList extends IndexedSet<Figure> {

        @Override
        public int indexOf(Object o) {
            if ((o instanceof Figure) && ((Figure) o).getParent() == AbstractCompositeFigure.this) {
                return super.indexOf(o);// linear search!
            }
            return -1;
        }

        @Override
        public boolean contains(Object o) {
            return ((o instanceof Figure) && ((Figure) o).getParent() == AbstractCompositeFigure.this);
        }

        @Override
        protected void onAdded(Figure e) {
            Figure oldParent = e.getParent();
            if (oldParent != null && oldParent != AbstractCompositeFigure.this) {
                oldParent.remove(e);
            }
            e.parentProperty().set(AbstractCompositeFigure.this);
        }

        @Override
        protected void onRemoved(Figure e) {
            e.parentProperty().set(null);
        }

        @Override
        protected boolean doAdd(int index, Figure element, boolean checkForDuplicates) {
            Figure oldParent = element.getParent();
            if (oldParent != AbstractCompositeFigure.this) {
                return super.doAdd(index, element, false);
            } else {
                return super.doAdd(index, element, true);// linear search!
            }
        }
    }
    /**
     * The name of the children property.
     */
    public final static String CHILDREN_PROPERTY = "children";

    private final ReadOnlyListProperty<Figure> children = new ReadOnlyListWrapper<>(this, CHILDREN_PROPERTY, new ChildList()).getReadOnlyProperty();

    /*
    {
        children.addListener(new ListChangeListener<Figure>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends Figure> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        final List<? extends Figure> removedList = c.getRemoved();
                        for (Figure removed : removedList) {
                            removed.parentProperty().set(null);
                        }
                    }
                    if (c.wasAdded()) {
                        final ObservableList<? extends Figure> addedList = c.getList();
                        for (int i = c.getFrom(), to = c.getTo(); i < to; i++) {
                            Figure added = addedList.get(i);
                            Figure oldParentOfAdded = added.getParent();
                            if (oldParentOfAdded != null) {
                                if (oldParentOfAdded == AbstractCompositeFigure.this) {
                                    int lastIndex = children.lastIndexOf(added);
                                    int firstIndex = children.indexOf(added);
                                    if (lastIndex != firstIndex) {
                                        children.remove(firstIndex == i ? lastIndex : firstIndex);
                                    }
                                } else {
                                    System.out.println("AbstractCompositeFigure.oldParentOfAdded " + oldParentOfAdded);
                                    oldParentOfAdded.remove(added);
                                }
                            }
                            added.parentProperty().set(AbstractCompositeFigure.this);
                        }
                    }
                }
            }
        });
    }*/
    @Override
    public ObservableList<Figure> getChildren() {
        return children.get();
    }

    @Override
    public final boolean isAllowsChildren() {
        return true;
    }

    @Override
    public Bounds getBoundsInLocal() {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Figure child : getChildren()) {
            Bounds b = child.getBoundsInParent();
            minX = min(minX, b.getMinX());
            maxX = max(maxX, b.getMaxX());
            minY = min(minY, b.getMinY());
            maxY = max(maxY, b.getMaxY());
        }

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        ObservableList<Figure> cs = getChildren();
        for (int i = cs.size() - 1; i >= 0; i--) {
            Figure c = cs.get(i);
            Connector cr = c.findConnector(p, prototype);
            if (cr != null) {
                return cr;
            }
        }
        return null;
    }

    /**
     * First updateLayout all getChildren and then updateLayout self.
     */
    @Override
    public final void updateLayout() {
        for (Figure child : getChildren()) {
            child.updateLayout();
        }
        layoutImpl();
    }

    /**
     * Layout self.
     */
    protected void layoutImpl() {

    }

}
