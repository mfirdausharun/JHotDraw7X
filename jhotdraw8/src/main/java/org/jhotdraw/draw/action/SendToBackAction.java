/* @(#)SendToBackAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.util.Collection;
import java.util.LinkedList;
import javafx.event.ActionEvent;
import org.jhotdraw.app.Application;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.util.Resources;

/**
 * SendToBackAction.
 *
 * @author Werner Randelshofer
 */
public class SendToBackAction extends AbstractSelectedAction {

    public static final String ID = "edit.sendToBack";

    /**
     * Creates a new instance.
     * @param app the application
     * @param editor the drawing editor
     */
    public SendToBackAction(Application app,DrawingEditor editor) {
        super(app,editor);
        Resources labels
                = Resources.getResources("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(ActionEvent e) {
        final DrawingView view = getView();
        if (view == null) {
            return;
        }
        final LinkedList<Figure> figures = new LinkedList<Figure>(view.getSelectedFigures());
        sendToBack(view, figures);

    }

    public static void sendToBack(DrawingView view, Collection<Figure> figures) {
        DrawingModel model = view.getModel();
        for (Figure child : figures) { // XXX Shouldn't the figures be sorted here back to front?
            Figure parent = child.getParent();
            if (parent != null && parent.isEditable() && parent.isDecomposable()) {
                assert parent.isAllowsChildren();
                model.insertChildAt(child, parent, 0);
            }
        }
    }
}
