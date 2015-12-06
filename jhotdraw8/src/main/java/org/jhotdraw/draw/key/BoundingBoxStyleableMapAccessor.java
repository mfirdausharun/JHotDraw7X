/* @(#)BoundingBoxStyleableMapAccessor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.Map;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.BoundingBox;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.styleable.AbstractStyleableFigureMapAccessor;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssBoundingBoxConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;

/**
 * BoundingBoxStyleableMapAccessor.
 *
 * @author werni
 */
public class BoundingBoxStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<BoundingBox> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, BoundingBox> cssMetaData;
    private final MapAccessor<Double> xKey;
    private final MapAccessor<Double> yKey;
    private final MapAccessor<Double> widthKey;
    private final MapAccessor<Double> heightKey;

    /**
     * Creates a new instance with the specified name.
     * 
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the rectangle
     * @param yKey the key for the y coordinate of the rectangle
     * @param widthKey the key for the width of the rectangle
     * @param heightKey the key for the height of the rectangle
     */
    public BoundingBoxStyleableMapAccessor(String name, MapAccessor<Double> xKey, MapAccessor<Double> yKey, MapAccessor<Double> widthKey, MapAccessor<Double> heightKey) {
        super(name, BoundingBox.class, new MapAccessor<?>[]{xKey, yKey, widthKey, heightKey}, new BoundingBox(xKey.getDefaultValue(), yKey.getDefaultValue(), widthKey.getDefaultValue(), heightKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<BoundingBox>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, BoundingBox> cnvrtr
                = new StyleConverterConverterWrapper<>(getConverter());
        CssMetaData<Styleable, BoundingBox> md
                = new SimpleCssMetaData<>(property, function,
                        cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
        this.widthKey = widthKey;
        this.heightKey = heightKey;
    }

    @Override
    public CssMetaData<?, BoundingBox> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<BoundingBox> converter;

    @Override
    public Converter<BoundingBox> getConverter() {
        if (converter == null) {
            converter = new CssBoundingBoxConverter();
        }
        return converter;
    }

    @Override
    public BoundingBox get(Map<? super Key<?>, Object> a) {
        return new BoundingBox(xKey.get(a), yKey.get(a), widthKey.get(a), heightKey.get(a));
    }

    @Override
    public BoundingBox put(Map<? super Key<?>, Object> a, BoundingBox value) {
        BoundingBox oldValue = get(a);
        xKey.put(a, value.getMinX());
        yKey.put(a, value.getMinY());
        widthKey.put(a, value.getWidth());
        heightKey.put(a, value.getHeight());
        return oldValue;
    }

    @Override
    public BoundingBox remove(Map<? super Key<?>, Object> a) {
        BoundingBox oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        widthKey.remove(a);
        heightKey.remove(a);
        return oldValue;
    }
}
