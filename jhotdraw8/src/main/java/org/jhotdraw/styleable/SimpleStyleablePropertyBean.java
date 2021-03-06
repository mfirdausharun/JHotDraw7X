/* @(#)SimpleStyleablePropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.styleable;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.MapAccessor;

/**
 * SimpleStyleablePropertyBean.
 *
 * @author Werner Randelshofer
 */
public abstract class SimpleStyleablePropertyBean implements StyleablePropertyBean {

    /**
     * Holds the properties.
     */
    // protected StyleablePropertyMap styleableProperties = new StyleablePropertyMap();
    protected final StyleableMap<Key<?>, Object> properties =//
            new StyleableMap<Key<?>, Object>() {

                @Override
                protected void callObservers(StyleOrigin origin, MapChangeListener.Change<Key<?>, Object> change) {
                    invalidated(change.getKey());
                    super.callObservers(origin, change);
                }

            };

    /**
     * Returns the user properties.
     */
    @Override
    public final ObservableMap<Key<?>, Object> getProperties() {
        return properties;
    }

    @Override
    public <T> StyleableProperty<T> getStyleableProperty(MapAccessor<T> key) {
        if (key instanceof StyleableMapAccessor) {
            StyleableMapAccessor<T> skey = (StyleableMapAccessor<T>) key;
            return new KeyMapEntryStyleableProperty<T>(this, properties, skey, skey.getCssName(), skey.getCssMetaData());
        } else {
            return null;
        }
    }

    protected StyleableMap<Key<?>, Object> getStyleableMap() {
        @SuppressWarnings("unchecked")
        StyleableMap<Key<?>, Object> map =  properties;
        return map;
    }

    /**
     * Returns the style value.
     */
    @Override
    public <T> T getStyled(MapAccessor<T> key) {
        StyleableMap<Key<?>, Object> map = getStyleableMap();
        @SuppressWarnings("unchecked")
        T ret = key.get(map.getStyledMap());// key may invoke get multiple times!
        return ret;
    }

    /**
     * Sets the style value.
     */
    @Override
    public <T> T setStyled(StyleOrigin origin, MapAccessor<T> key, T newValue) {
        StyleableMap<Key<?>, Object> map = getStyleableMap();
        @SuppressWarnings("unchecked")
        T ret = key.put(map.getMap(origin), newValue);
        return ret;
    }

    @Override
    public <T> T remove(StyleOrigin origin, MapAccessor<T> key) {
        @SuppressWarnings("unchecked")
        T ret = key.remove(getStyleableMap().getMap(origin));
        return ret;
    }

    @Override
    public void removeAll(StyleOrigin origin) {
        getStyleableMap().removeAll(origin);
    }

    /**
     * This method is invoked just before listeners are notified. This
     * implementation is empty.
     *
     * @param key the invalidated key
     */
    protected void invalidated(Key<?> key) {
    }

}
