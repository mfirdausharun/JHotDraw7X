/* @(#)Application.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app;

import java.net.URI;
import java.util.function.Consumer;
import org.jhotdraw.collection.HierarchicalMap;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.beans.PropertyBean;

/**
 * Application.
 * @author Werner Randelshofer
 */
public interface Application extends Disableable, PropertyBean {



    /** The set of views contains all visible views.
     * @return the views  */
    public SetProperty<View> viewsProperty();

    // Convenience method
    default public ObservableSet<View> views() {
        return viewsProperty().get();
    }

    /** Adds the view to the set of views and shows it.
     * @param v the view */
    default public void add(View v) {
        viewsProperty().add(v);
    }

    /** Removes the view from the set of views and hides it.
     * @param v the view */
    default public void remove(View v) {
        viewsProperty().remove(v);
    }

    /** Provides the currently active view. This is the last view which was
     * focus owner. Returns null, if the application has no views.
     * @return The active view.
     */
    public ReadOnlyObjectProperty<View> activeViewProperty();

    // Convenience method
    default public View getActiveView() {
        return activeViewProperty().get();
    }

    /** Returns the action map of the application.
     * @return the action map */
    public HierarchicalMap<String, Action> getActionMap();
    
    /** Executes a worker on the thread pool of the application.
     * @param r the runnable */
    public void execute(Runnable r);
    
    /** Returns the application model.
     * @return the model */
    public ApplicationModel getModel();
    /** Sets the application model.
     * @param newValue the model */
    public void setModel(ApplicationModel newValue);

    /** Exits the application. */
    public void exit();
    
    /** Returns the application node.
     * @return the node */
    default public Node getNode() {return null;}
    
    /** Adds an URI to the list of recent URIs.
     * @param uri the uri*/
    public void addRecentURI(URI uri);
    
    /** Creates a new view, initializes it, then invokes the callback.
     * @param callback A callback. Can be null.
     */
    public void createView(Consumer<View> callback);
}
