/*
 * @(#)AdjacentSiblingCombinator.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.xml.css.ast;

import org.jhotdraw.xml.css.SelectorModel;

/**
 * An "adjacent sibling combinator" matches an element if its first selector
 * matches on the adjacent sibling of the element and if its second selector
 * matches the element.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class AdjacentSiblingCombinator extends Combinator {

    public AdjacentSiblingCombinator(SimpleSelector firstSelector, Selector secondSelector) {
        super(firstSelector, secondSelector);
    }

    @Override
    public String toString() {
        return firstSelector + " + " + secondSelector;
    }

    @Override
    public <T> T match(SelectorModel<T> model, T element) {
        T matchingElement = secondSelector.match(model, element);
        if (matchingElement != null) {
            return firstSelector.match(model, model.getPreviousSibling(matchingElement));
        }
        return null;
    }
}
