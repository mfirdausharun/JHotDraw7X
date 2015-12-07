/* @(#)DoubleStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssSizeConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;
import org.jhotdraw.styleable.StyleableMapAccessor;

/**
 * DoubleStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class DoubleStyleableFigureKey extends SimpleFigureKey<Double> implements StyleableMapAccessor<Double> {

    final static long serialVersionUID = 1L;
    private final CssMetaData<? extends Styleable, Double> cssMetaData;

    private final CssSizeConverter converter=new CssSizeConverter();

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public DoubleStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public DoubleStyleableFigureKey(String name, Double defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public DoubleStyleableFigureKey(String name, DirtyMask mask, Double defaultValue) {
        super(name, Double.class, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createSizeCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getCssName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });*/

        Function<Styleable, StyleableProperty<Double>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Double> converter
                = new StyleConverterConverterWrapper<Double>(new CssSizeConverter());
        CssMetaData<Styleable, Double> md
                = new SimpleCssMetaData<Styleable, Double>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<? extends Styleable, Double> getCssMetaData() {
        return cssMetaData;

    }

    @Override
    public Converter<Double> getConverter() {
        return converter;
    }
}
