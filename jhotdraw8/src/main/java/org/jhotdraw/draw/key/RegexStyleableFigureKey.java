/* @(#)RegexStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssRegexConverter;
import org.jhotdraw.styleable.StyleableMapAccessor;
import org.jhotdraw.text.Regex;
import org.jhotdraw.text.StyleConverterConverterWrapper;

/**
 * RegexStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class RegexStyleableFigureKey extends SimpleFigureKey<Regex> implements StyleableMapAccessor<Regex> {

    final static long serialVersionUID = 1L;
    private final CssRegexConverter converter ;
    private final CssMetaData<? extends Styleable, Regex> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public RegexStyleableFigureKey(String name) {
        this(name, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), new Regex());
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public RegexStyleableFigureKey(String name, Regex defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), defaultValue);
    }

    /**
     * Creates a new instance with the specified name and default value.
     * The value is nullable.
     *
     * @param name The name of the key.
     * @param mask the dirty mask
     * @param defaultValue The default value.
     */
    public RegexStyleableFigureKey(String name, DirtyMask mask, Regex defaultValue) {
        this(name, true, mask, defaultValue);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param nullable whether the value is nullable
     * @param mask the dirty mask
     * @param defaultValue The default value.
     */
    public RegexStyleableFigureKey(String name, boolean nullable, DirtyMask mask, Regex defaultValue) {
        super(name, Regex.class, nullable, mask, defaultValue);

        Function<Styleable, StyleableProperty<Regex>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Regex> cnvrtr
                = new StyleConverterConverterWrapper<Regex>(getConverter());
        CssMetaData<Styleable, Regex> md
                = new SimpleCssMetaData<Styleable, Regex>(property, function,
                        cnvrtr, defaultValue, inherits);
        cssMetaData = md;
        converter= new CssRegexConverter(isNullable());
    }

    @Override
    public CssMetaData<? extends Styleable, Regex> getCssMetaData() {
        return cssMetaData;

    }

    @Override
    public Converter<Regex> getConverter() {
        return converter;
    }
}
