/* @(#)PrefixMatchSelector.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css.ast;

import org.jhotdraw.css.SelectorModel;

/**
 * A "prefix match selector" {@code ^=} matches an element if the element has an
 * attribute with the specified name and its value starts with the specified 
 * substring.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PrefixMatchSelector extends AbstractAttributeSelector {

    private final String attributeName;
    private final String substring;

    public PrefixMatchSelector(String attributeName, String substring) {
        this.attributeName = attributeName;
        this.substring = substring;
    }

    @Override
    protected <T> T match(SelectorModel<T> model, T element) {
        return (model.attributeValueStartsWith(element, attributeName, substring))//
                        ? element : null;
    }

}
