/* @(#)Actions.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app.action;

import javafx.beans.binding.Binding;
import javafx.beans.property.Property;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.KeyCombination;

/**
 * Actions.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Actions {

    /** Prevent instance creation. */
    private Actions() {

    }

    /**
     * Binds a button to an action
     * @param control The menu control
     * @param action The action
     */
    public static void bindButton(Button control, Action action) {
        // create a strong reference to name binding:
        Binding<String> nameBinding = Action.LABEL.valueAt(action.propertiesProperty());
        control.getProperties().put("ActionsNameBinding", nameBinding);
        control.textProperty().bind(Action.LABEL.valueAt(action.propertiesProperty()));

        control.setOnAction(action);
        control.disableProperty().bind(action.disabledProperty());
    }

    /**
     * Binds a menu control to an action
     * @param control The menu control
     * @param action The action
     */
    public static void bindMenuItem(MenuItem control, Action action) {
        // create a strong reference to name binding:
        Binding<String> nameBinding = Action.LABEL.valueAt(action.propertiesProperty());
        control.getProperties().put("ActionsNameBinding", nameBinding);
        control.textProperty().bind(Action.LABEL.valueAt(action.propertiesProperty()));

        if (control instanceof CheckMenuItem) {
            Property<Boolean> selectedBinding = Action.SELECTED_KEY.propertyAt(action.propertiesProperty());
            // create a strong reference to name binding:
            control.getProperties().put("ActionsSelectedBinding", selectedBinding);
            // this only creates a weak reference to the name binding:
            ((CheckMenuItem) control).selectedProperty().bindBidirectional(selectedBinding);
        } else if (control instanceof RadioMenuItem) {
            Property<Boolean> selectedBinding = Action.SELECTED_KEY.propertyAt(action.propertiesProperty());
            // create a strong reference to name binding:
            control.getProperties().put("ActionsSelectedBinding", selectedBinding);
            // this only creates a weak reference to the name binding:
            ((RadioMenuItem) control).selectedProperty().bindBidirectional(selectedBinding);
        }
        control.setOnAction(action);
        control.disableProperty().bind(action.disabledProperty());
        
        Binding<KeyCombination> acceleratorBinding = Action.ACCELERATOR_KEY.valueAt(action.propertiesProperty());
        // create a strong reference to name binding:
        control.getProperties().put("ActionsAcceleratorBinding", acceleratorBinding);
        // this only creates a weak reference to the name binding:
        control.acceleratorProperty().bind(acceleratorBinding);
    }
}
