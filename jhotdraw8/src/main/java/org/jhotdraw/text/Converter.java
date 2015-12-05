/* @(#)Converter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw.draw.io.IdFactory;

/**
 * Converts a data value of type {@code T} from or to a String representation.
 * <p>
 * This interface is designed so that it can be adapted to the various String
 * conversion APIs in the JDK.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <T> The type of the data value
 */
public interface Converter<T> {

    /** Converts a value to a string and appends it to the provided
     * {@code Appendable}.
     * <p>
     * This method does not change the state of the converter. 
     *
     * @param out The appendable
     * @param idFactory The factory for creating object ids. Nullable for
     * some converters.
     * @param value The value. Nullable.
     * @throws java.io.IOException thrown by Appendable
     */
    void toString(Appendable out, IdFactory idFactory, T value) throws IOException;

    /**
     * Constructs a value from a string.
     * <p>
     * The converter should try to create the value greedily, by consuming
     * as many characters as possible for the value.
     * <p>
     * This method does not change the state of the converter. 
     *
     * @param in A char buffer which holds the string. The char buffer must
     * be treated as read only! The position of the char buffer denotes
     * the beginning of the string when this method is invoked. After 
     * completion of this method, the position is set after the last consumed
     * character.
     * @param idFactory The factory for looking up object ids. Nullable for
     * some converters.
     * @return The value. Nullable. 
     *
     * @throws ParseException if conversion failed. The error offset field is
     * set to the position where parsing failed. The position of the buffer
     * is undefined.
     * @throws java.io.IOException Thrown by the CharBuffer.
     */
    T fromString(CharBuffer in, IdFactory idFactory) throws ParseException, IOException;
    
    // ----
    // convenience methods
    // ----
    /** Converts a value to a string and appends it to the provided
     * {@code Appendable}.
     * <p>
     * This method does not change the state of the converter. 
     *
     * @param out The appendable
     * @param value The value. Nullable.
     * @throws java.io.IOException thrown by Appendable
     */
    default void toString(Appendable out, T value) throws IOException {
        toString(out, null, value);
    }

    /**
     * Constructs a value from a string.
     * <p>
     * The converter should try to create the value greedily, by consuming
     * as many characters as possible for the value.
     * <p>
     * This method does not change the state of the converter. 
     *
     * @param in A char buffer which holds the string. The char buffer must
     * be treated as read only! The position of the char buffer denotes
     * the beginning of the string when this method is invoked. After 
     * completion of this method, the position is set after the last consumed
     * character.
     * @return The value. Nullable. 
     *
     * @throws ParseException if conversion failed. The error offset field is
     * set to the position where parsing failed. The position of the buffer
     * is undefined.
     * @throws java.io.IOException Thrown by the CharBuffer.
     */
    default T fromString(CharBuffer in) throws ParseException, IOException {
        return fromString(in, null);
    }
    
    
    /**
     * Converts a value to a String.
     * <p>
     * This method does not change the state of the converter. 
     * <p>
     * Note: this is a convenience method. Implementing classes rarely need
     * to overwrite this method.
     *
     * @param value The value. Nullable.
     * @return The String.
     */
    default String toString(T value) {
        StringBuilder out = new StringBuilder();
        try {
            toString(out,value);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
        return out.toString();
    }

    /**
     * Constructs a value from a String.
     * <p>
     * The conversion only succeeds if the entire string is consumed.
     * <p>
     * This method does not change the state of the converter. 
     * <p>
     * Note: this is a convenience method. Implementing classes rarely need
     * to overwrite this method.
     *
     * @param in The String.
     * @return The value. Nullable.
     *
     * @throws ParseException on conversion failure
     * @throws IOException on IO failure
     */
    default T fromString(CharSequence in) throws ParseException, IOException {
        CharBuffer buf = CharBuffer.wrap(in);
        T value = fromString(buf);
        if (buf.remaining()!=0) {
            throw new ParseException(buf.remaining()+" remaining character(s) not consumed."+" remaining:"+buf.toString(),buf.position());
        }
        return value;
    }
    
    /** Provides a default value for APIs which always require a value even
     * if conversion from String failed.
     * 
     * @return The default value to use when conversion from String failed.
     */
    T getDefaultValue();
}
