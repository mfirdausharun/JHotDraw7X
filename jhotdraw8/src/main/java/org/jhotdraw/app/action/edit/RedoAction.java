/*
 * @(#)RedoAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.edit;

import org.jhotdraw.util.*;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;

/**
 * Redoes the last user action on the active view.
 * <p>

 *
 * @author Werner Randelshofer
 * @version $Id: RedoAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class RedoAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.redo";
    private Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
    /*
    private PropertyChangeListener redoActionPropertyListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == AbstractAction.NAME) {
                putValue(AbstractAction.NAME, evt.getNewValue());
            } else if (name == "enabled") {
                updateEnabledState();
            }
        }
    };*/

    /** Creates a new instance. 
     * @param app the application 
     * @param view the view
    */
    public RedoAction(Application app, View view) {
        super(app, view);
        labels.configureAction(this, ID);
    }
/*
    protected void updateEnabledState() {
        boolean isEnabled = false;
        Action realRedoAction = getRealRedoAction();
        if (realRedoAction != null && realRedoAction!=this) {
            isEnabled = realRedoAction.isEnabled();
        }
        setEnabled(isEnabled);
    }

    @Override
    protected void updateView(View oldValue, View newValue) {
        super.updateView(oldValue, newValue);
        if (newValue != null && //
                newValue.getActionMap().get(ID) != null && //
                newValue.getActionMap().get(ID) != this) {
            putValue(AbstractAction.NAME, newValue.getActionMap().get(ID).
                    getValue(AbstractAction.NAME));
            updateEnabledState();
        }
    }

    /**
     * Installs listeners on the view object.
     * /
    @Override
    protected void installViewListeners(View p) {
        super.installViewListeners(p);
        Action redoActionInView = p.getActionMap().get(ID);
        if (redoActionInView != null && redoActionInView != this) {
            redoActionInView.addPropertyChangeListener(redoActionPropertyListener);
        }
    }

    /**
     * Installs listeners on the view object.
     * /
    @Override
    protected void uninstallViewListeners(View p) {
        super.uninstallViewListeners(p);
        Action redoActionInView = p.getActionMap().get(ID);
        if (redoActionInView != null && redoActionInView != this) {
            redoActionInView.removePropertyChangeListener(redoActionPropertyListener);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Action realAction = getRealRedoAction();
        if (realAction != null && realAction!=this) {
            realAction.actionPerformed(e);
        }
    }

    @Nullable
    private Action getRealRedoAction() {
        return (getActiveView() == null) ? null : getActiveView().getActionMap().get(ID);
    }

    @Override
    public void handle(javafx.event.ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
*/

    @Override
    protected void onActionPerformed(javafx.event.ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
