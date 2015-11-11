/* @(#)StyleablePropertyMap.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import java.util.HashMap;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import org.jhotdraw.collection.Key;

/**
 * {@code StyleablePropertyMap} provides an acceleration structure for
 * properties which can by styled from CSS.
 * <p>
 * {@code StyleablePropertyMap} consists internally of four input maps and one
 * output map. 
 * <p>
 * Performance of this class is not very good, because of the output map
 * that needs to be updated.
 * <ul>
 * <li>An input map is provided for each {@link StyleOrigin}.</li>
 * <li>The output map contains the styled value. The style origins have the
 * precedence as defined in {@link StyleableProperty} which is
 * {@code INLINE, AUTHOR, USER, USER_AGENT}.</li>
 * </ul>
 *
 * @author Werner Randelshofer
 */
public class StyleablePropertyMap1 {

    // ---
    // constant declarations
    // ---
    /**
     * The name of the "user" property.
     */
    public final String USER_PROPERTY = "user";
    /**
     * The name of the "user agent" property.
     */
    public final String USER_AGENT_PROPERTY = "userAgent";
    /**
     * The name of the "inline" property.
     */
    public final String INLINE_PROPERTY = "inline";
    /**
     * The name of the "author" property.
     */
    public final String AUTHOR_PROPERTY = "author";
    /**
     * The name of the "author" property.
     */
    public final String OUTPUT_PROPERTY = "output";
    // ---
    // field declarations
    // ---
    /**
     * Holds the user getProperties.
     */
    protected final ReadOnlyMapProperty<Key<?>, Object> user = new ReadOnlyMapWrapper<Key<?>, Object>(this, USER_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();
    /**
     * Holds the inline getProperties.
     */
    protected ReadOnlyMapProperty<Key<?>, Object> inline;// = new ReadOnlyMapWrapper<Key<?>, Object>(this, INLINE_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();
    /**
     * Holds the author getProperties.
     */
    protected ReadOnlyMapProperty<Key<?>, Object> author;// = new ReadOnlyMapWrapper<Key<?>, Object>(this, AUTHOR_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();
    /**
     * Holds the user agent getProperties.
     */
    protected ReadOnlyMapProperty<Key<?>, Object> userAgent;// 
    /**
     * Holds the outputReadonly getProperties.
     */
    protected final ObservableMap<Key<?>, Object> output = FXCollections.observableHashMap();
    /**
     * Read-only wrapper for the outputReadonly getProperties.
     */
    protected final ReadOnlyMapProperty<Key<?>, Object> outputReadonly = new ReadOnlyMapWrapper<Key<?>, Object>(this, OUTPUT_PROPERTY, FXCollections.unmodifiableObservableMap(output)).getReadOnlyProperty();

    /**
     * Holds the styleable getProperties.
     */
    protected final HashMap<Key<?>, StyleableProperty<?>> styleableProperties = new HashMap<>();

    private MapChangeListener<Key<?>, Object> inputHandler = new MapChangeListener<Key<?>, Object>() {

        @Override
        public void onChanged(MapChangeListener.Change<? extends Key<?>, ? extends Object> change) {
            updateOutput(change.getKey());
        }
    };
    private final Object bean;

    // ---
    // constructors
    // ---

    public StyleablePropertyMap1() {
        this(null);
    }

    public StyleablePropertyMap1(Object bean) {
        user.addListener(inputHandler);
        this.bean = bean;
    }

    // ---
    // property methods
    // ---
    /**
     * Returns an observable map of property keys and their values.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> userProperties() {
        return user;
    }

    /**
     * Returns an observable map of property keys and their values.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> userAgentProperties() {
        if (userAgent == null) {
            userAgent = new ReadOnlyMapWrapper<Key<?>, Object>(this, USER_AGENT_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();
            userAgent.addListener(inputHandler);
        }
        return userAgent;
    }

    /**
     * Returns an observable map of property keys and their values.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> authorProperties() {
        if (author == null) {
            author = new ReadOnlyMapWrapper<Key<?>, Object>(this, AUTHOR_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();
            author.addListener(inputHandler);
        }
        return author;
    }

    /**
     * Returns an observable map of property keys and their values.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> inlineProperties() {
        if (inline == null) {
            inline = new ReadOnlyMapWrapper<Key<?>, Object>(this, INLINE_PROPERTY, FXCollections.observableHashMap()).getReadOnlyProperty();
            inline.addListener(inputHandler);
        }
        return inline;
    }

    /**
     * Returns an observable map of property keys and their values.
     * <p>
     * The map is unmodifiable.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> outputProperties() {
        return outputReadonly;
    }

    // ---
    // behavior
    // ---
    /**
     * Clears all getProperties except the user getProperties.
     */
    public void clearNonUserProperties() {
        if (userAgent != null) {
            userAgent.clear();
        }
        if (inline != null) {
            inline.clear();
        }
        if (author != null) {
            author.clear();
        }
    }

    private void updateOutput(Key<?> key) {
        StyleOrigin origin = getStyleOrigin(key);
        if (origin == null) {
            output.remove(key);
        } else {
            switch (origin) {
                case INLINE:
                    output.put(key, inlineProperties().get(key));
                    break;
                case AUTHOR:
                    output.put(key, authorProperties().get(key));
                    break;
                case USER:
                    output.put(key, userProperties().get(key));
                    break;
                case USER_AGENT:
                    output.put(key, userAgentProperties().get(key));
                    break;
                default:
                    throw new InternalError("unknown enum value " + origin);
            }
        }
    }

    /**
     * Returns the style origin for the specified value.
     *
     * @param key The key identifying the value.
     * @return The style origin or null if the key is not contained in the map.
     */
    public StyleOrigin getStyleOrigin(Key<?> key) {
        if (inline != null && inline.containsKey(key)) {
            return StyleOrigin.INLINE;
        } else if (author != null && author.containsKey(key)) {
            return StyleOrigin.AUTHOR;
        } else if (user != null && user.containsKey(key)) {
            return StyleOrigin.USER;
        } else if (userAgent != null && userAgent.containsKey(key)) {
            return StyleOrigin.USER_AGENT;
        } else {
            return null;
        }
    }

    public Object getBean() {
        return bean;
    }

    public <T> StyleableProperty<T> getStyleableProperty(Key<T> key) {
        @SuppressWarnings("unchecked")
        StyleableProperty<T> sp = (StyleableProperty<T>) styleableProperties.get(key);
        if (sp == null) {
            if (key instanceof StyleableKey) {
                @SuppressWarnings("unchecked")
                StyleableProperty<T> temp = new MapStyleableProperty<>(key, ((StyleableKey) key).getCssMetaData());
                sp = temp;
            } else {
                @SuppressWarnings("unchecked")
                StyleableProperty<T> temp = new MapStyleableProperty<>(key, null);
                sp = temp;
            }
            styleableProperties.put(key, sp);
        }
        return sp;
    }

    public <T> T remove(StyleOrigin origin, Key<T> key) {
        T value = null;
        switch (origin) {
            case INLINE:
                if (inline != null) {
                    @SuppressWarnings("unchecked")
                    T temp = (T) inline.remove(key);
                    value = temp;
                }
                break;
            case AUTHOR:
                if (author != null) {
                    @SuppressWarnings("unchecked")
                    T temp = (T) author.remove(key);
                    value = temp;
                }
                break;
            case USER:
                if (user != null) {
                    @SuppressWarnings("unchecked")
                    T temp = (T) user.remove(key);
                    value = temp;
                }
                break;
            case USER_AGENT:
                if (userAgent != null) {
                    @SuppressWarnings("unchecked")
                    T temp = (T) userAgent.remove(key);
                    value = temp;
                }
                break;
            default:
                throw new InternalError("unknown enum value " + origin);
        }
        return value;
    }

    public void removeAll(StyleOrigin origin) {
        switch (origin) {
            case INLINE:
                if (inline != null) {
                    inline.clear();
                }
                break;
            case AUTHOR:
                if (author != null) {
                    author.clear();
                }
                break;
            case USER:
                if (user != null) {
                    user.clear();
                }
                break;
            case USER_AGENT:
                if (userAgent != null) {
                    userAgent.clear();
                }
                break;
            default:
                throw new InternalError("unknown enum value " + origin);
        }
    }

    // ---
    // static inner classes
    // ---
    public class MapStyleableProperty<T> extends ObjectPropertyBase<T> implements StyleableProperty<T> {

        private final Key<T> key;
        private final CssMetaData<?, T> metaData;

        public MapStyleableProperty(Key<T> key, CssMetaData<?, T> metaData) {
            this.key = key;
            this.metaData = metaData;

            bindBidirectional(new Key.PropertyAt<>(user, key));
        }

        @Override
        public Object getBean() {
            return StyleablePropertyMap1.this.getBean();
        }

        @Override
        public String getName() {
            return key.getName();
        }

        @Override
        public CssMetaData<?, T> getCssMetaData() {
            return metaData;
        }

        /**
         *
         * @param origin the style origin
         * @param value the value null removes the key from the style origin
         */
        @Override
        public void applyStyle(StyleOrigin origin, T value) {
            if (!key.isAssignable(value)) {
                throw new ClassCastException("value is not assignable. key:" + key + " value:" + value);
            }
            if (origin == null) {
                throw new IllegalArgumentException("origin must not be null");
            } else {
                switch (origin) {
                    case INLINE:
                        if (value == null) {
                            inlineProperties().remove(key);
                        } else {
                            inlineProperties().put(key, value);
                        }
                        break;
                    case AUTHOR:
                        if (value == null) {
                            authorProperties().remove(key);
                        } else {
                            authorProperties().put(key, value);
                        }
                        break;
                    case USER:
                        if (value == null) {
                            userProperties().remove(key);
                        } else {
                            userProperties().put(key, value);
                        }
                        break;
                    case USER_AGENT:
                        if (value == null) {
                            userAgentProperties().remove(key);
                        } else {
                            userAgentProperties().put(key, value);
                        }
                        break;
                    default:
                        throw new InternalError("unknown enum value " + origin);
                }
            }
        }

        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleablePropertyMap1.this.getStyleOrigin(key);
        }

    }
    
}
