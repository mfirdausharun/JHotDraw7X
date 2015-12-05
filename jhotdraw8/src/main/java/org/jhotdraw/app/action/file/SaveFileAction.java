/*
 * @(#)SaveFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.net.URI;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.SimpleKey;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.Resources;

/**
 * Saves the changes in the active view. If the active view has not an URI,
 * an {@code URIChooser} is presented.
 * <p>
 *
 * @author  Werner Randelshofer
 * @version $Id: SaveFileAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class SaveFileAction extends AbstractViewAction {

    private static final long serialVersionUID = 1L;
    public final static Key<URIChooser> SAVE_CHOOSER_KEY = new SimpleKey<>("saveChooser", URIChooser.class);

    public static final String ID = "file.save";
    private boolean saveAs;
    private Node oldFocusOwner;

    /** Creates a new instance.
     * @param app the application
     */
    public SaveFileAction(Application app) {
        this(app, null, false);
    }

    /** Creates a new instance.
     * @param app the application
     * @param view the view */
    public SaveFileAction(Application app, View view) {
        this(app, view, false);
    }

    /** Creates a new instance. 
     * @param app the application 
     * @param view the view
     * @param saveAs whether to force a file dialog */
    public SaveFileAction(Application app, View view, boolean saveAs) {
        super(app, view);
        this.saveAs = saveAs;
        Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    protected URIChooser getChooser(View view) {
        URIChooser c = app.get(SAVE_CHOOSER_KEY);
        if (c == null) {
            c = getApplication().getModel().createSaveChooser();
            app.set(SAVE_CHOOSER_KEY, c);
        }
        return c;
    }

    @Override
    protected void onActionPerformed(ActionEvent evt) {
        if (isDisabled()) {
            return;
        }
        final View v = getActiveView();
        if (v==null || v.isDisabled()) {
            return;
        }
        oldFocusOwner = v.getNode().getScene().getFocusOwner();
        v.addDisabler(this);
        saveView(v);
    }

    protected void saveView(final View v) {
        if (v.getURI() == null || saveAs) {
            URIChooser chooser = getChooser(v);
            //int option = fileChooser.showSaveDialog(this);

            URI uri = null;
            Outer:
            while (true) {
                uri = chooser.showDialog(v.getNode());

                // Prevent save to URI that is open in another view!
                // unless  multipe views to same URI are supported
                if (uri!=null && !app.getModel().isAllowMultipleViewsPerURI()) {
                    for (View vi : app.views()) {
                        if (vi != v && uri.equals(v.getURI())) {
                            // FIXME Localize message
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You can not save to a file which is already open.");
                            alert.showAndWait();
                            continue Outer;
                        }
                    }
                }
                break;
            }
            if (uri!=null) {
                saveViewToURI(v, uri, chooser);
            }
            v.removeDisabler(this);
            if (oldFocusOwner != null) {
                oldFocusOwner.requestFocus();
            }
        } else {
            saveViewToURI(v, v.getURI(), null);
        }
    }

    protected void saveViewToURI(final View v, final URI uri, final URIChooser chooser) {
        v.write(uri, event -> {
            switch (event.getState()) {
                case CANCELLED:
                    v.removeDisabler(this);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
                    break;
                case FAILED:
                    Throwable value = event.getException();
                    String message = (value != null && value.getMessage() != null) ? value.getMessage() : value.toString();
                    Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            ((message == null) ? "" : message));
                    alert.setHeaderText(labels.getFormatted("file.save.couldntSave.message", URIUtil.getName(uri)));
                    alert.showAndWait();
                    v.removeDisabler(this);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
                    break;
                case SUCCEEDED:
                    v.setURI(uri);
                    v.clearModified();
                    v.setTitle(URIUtil.getName(uri));
                    app.addRecentURI(uri);
                    v.removeDisabler(this);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
                    break;
                default:
                    break;
            }
        });
    }
}
