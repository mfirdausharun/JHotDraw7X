/* @(#)ClipboardIO.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.gui;

import java.util.List;
import javafx.scene.input.Clipboard;

/**
 * ClipboardIO.
 * @author Werner Randelshofer
 */
public interface ClipboardIO<T> {
    /** Writes items to the clipboard
     * 
     * @param clipboard The clipboard
     * @param items the items
     */
    void write(Clipboard clipboard, List<T> items);
    /** Returns null if read failed.
     * 
     * @param clipboard The clipboard
     * @return izrmd the items
     */
    List<T> read(Clipboard clipboard);
    /**
     * Returns true if data from the clibpoard can be imported
     * @param clipboard The clipboard
     * @return true if import is possible
     */
    boolean canRead(Clipboard clipboard);
}
