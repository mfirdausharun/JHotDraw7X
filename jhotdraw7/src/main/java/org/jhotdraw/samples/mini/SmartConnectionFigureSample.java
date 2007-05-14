/*
 * @(#)SmartConnectionFigureSample.java   1.0  March 9, 2007
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */
package org.jhotdraw.samples.mini;

import java.awt.geom.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * Example showing how to connect two text areas with an elbow connection.
 * <p>
 * The 'SmartConnectionFigure', that is used to connect the two areas draws
 * with double stroke, if the Figure at the start and at the end of the connection
 * is the same. 
 * <p>
 * In order to prevent editing of the stroke type by the user, the 
 * SmartConnectionFigure disables the stroke type attribute. Unless it needs
 * to be changed by the SmartConnectionFigure by itself.
 * 
 *
 * @author Werner Randelshofer
 * @version 1.0 March 9, 2007 Created.
 */
public class SmartConnectionFigureSample {
    private static class SmartConnectionFigure extends LineConnectionFigure {
        public SmartConnectionFigure() {
            setAttributeEnabled(STROKE_TYPE, false);
        }
        
        @Override public void handleConnect(Figure start, Figure end) {
            setAttributeEnabled(STROKE_TYPE, true);
            STROKE_TYPE.set(this,
                    (start == end) ? StrokeType.DOUBLE : StrokeType.BASIC
                    );
            setAttributeEnabled(STROKE_TYPE, false);
        }
        @Override public void handleDisconnect(Figure start, Figure end) {
            setAttributeEnabled(STROKE_TYPE, true);
            STROKE_TYPE.set(this, StrokeType.BASIC);
            setAttributeEnabled(STROKE_TYPE, false);
        }
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                // Create a simple drawing consisting of three
                // text areas and an elbow connection.
                TextAreaFigure ta = new TextAreaFigure();
                ta.setBounds(new Point2D.Double(10,30),new Point2D.Double(100,100));
                TextAreaFigure tb = new TextAreaFigure();
                tb.setBounds(new Point2D.Double(220,130),new Point2D.Double(310,210));
                TextAreaFigure tc = new TextAreaFigure();
                tc.setBounds(new Point2D.Double(220,30),new Point2D.Double(310,100));
                ConnectionFigure cf = new SmartConnectionFigure();
                cf.setLiner(new ElbowLiner());
                cf.setStartConnector(ta.findConnector(Geom.center(ta.getBounds()), cf));
                cf.setEndConnector(tb.findConnector(Geom.center(tb.getBounds()), cf));
                Drawing drawing = new DefaultDrawing();
                drawing.add(ta);
                drawing.add(tb);
                drawing.add(tc);
                drawing.add(cf);
                
                // Show the drawing
                JFrame f = new JFrame("My Drawing");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400,300);
                
                // Set up the drawing view
                DrawingView view = new DefaultDrawingView();
                view.setDrawing(drawing);
                f.getContentPane().add(view.getComponent());
                
                // Set up the drawing editor
                DrawingEditor editor = new DefaultDrawingEditor();
                editor.add(view);
                editor.setTool(new DelegationSelectionTool());
                
                f.show();
            }
        });
    }
}
