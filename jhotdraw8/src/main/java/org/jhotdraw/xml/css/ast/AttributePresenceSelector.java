/*
 * @(#)AttributeSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.xml.css.ast;

import org.jhotdraw.xml.css.SelectorModel;

/**
 * An "attribute presence selector" matches an element if the element has
 * an attribute with the specified name.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class AttributePresenceSelector extends AttributeSelector {
private final String attributeName;

    public AttributePresenceSelector(String attributeName) {
        this.attributeName=attributeName;
    }

    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        return model.hasAttribute(element, attributeName) ? element : null;
    }

}