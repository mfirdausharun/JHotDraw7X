/* @(#)LayersInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.ReversedList;
import org.jhotdraw.collection.SimpleKey;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.gui.ClipboardIO;
import org.jhotdraw.gui.ListViewUtil;
import org.jhotdraw.util.Resources;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class LayersInspector extends AbstractDrawingInspector {

    @FXML
    private ListView<Figure> listView;
    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    private ReversedList<Figure> layers;

    private Supplier<Layer> layerFactory;

    /**
     * This key is used to store the selection count in the layers.
     */
    final static Key<Integer> SELECTION_COUNT = new SimpleKey<Integer>("selectionCount", Integer.class, 0);

    private ChangeListener<Layer> selectedLayerHandler = new ChangeListener<Layer>() {
        @Override
        public void changed(ObservableValue<? extends Layer> observable, Layer oldValue, Layer newValue) {
            if (newValue != null) {
                listView.getSelectionModel().select(newValue);
            }
        }
    };
    private InvalidationListener listInvalidationListener = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            // FIXME must perform change via the model so that undo/redo will work
            if (drawingView != null) {
                drawingView.getModel().fireNodeInvalidated(drawingView.getDrawing());
            }
        }
    };
    private InvalidationListener selectionInvalidationListener = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            onSelectionChanged();
        }
    };

    public LayersInspector() {
        this(LayersInspector.class.getResource("LayersInspector.fxml"));
    }

    public LayersInspector(URL fxmlUrl) {
        this(fxmlUrl, SimpleLayer::new);
    }

    public LayersInspector(URL fxmlUrl, Supplier<Layer> layerFactory) {
        this.layerFactory = layerFactory;
        init(fxmlUrl);
    }

    public LayersInspector(Supplier<Layer> layerFactory) {
        this(LayersInspector.class.getResource("LayersInspector.fxml"), layerFactory);
    }

    private void onSelectionChanged() {
        Drawing d = drawingView.getDrawing();
        Set<Figure> selection = drawingView.getSelectedFigures();
        HashMap<Figure, Integer> layerToIndex = new HashMap<>();
        List<Figure> children = d.getChildren();
        int[] count = new int[children.size()];
        for (int i = 0, n = children.size(); i < n; i++) {
            layerToIndex.put(children.get(i), i);
        }
        for (Figure f : selection) {
            Layer l = f.getLayer();
            Integer index = layerToIndex.get(l);
            if (index != null) {
                count[index]++;
            }
        }
        for (int i = 0, n = children.size(); i < n; i++) {
            children.get(i).set(SELECTION_COUNT, count[i]);
        }
        layers.fireUpdated(0, layers.size());
    }

    private void init(URL fxmlUrl) {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        loader.setResources(Resources.getBundle("org.jhotdraw.draw.gui.Labels"));

        try (InputStream in = fxmlUrl.openStream()) {
            setCenter(loader.load(in));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        addButton.addEventHandler(ActionEvent.ACTION, o -> {
            Layer layer = layerFactory.get();
            int index = listView.getSelectionModel().getSelectedIndex();
            if (index < 0) {
                index = 0;
            }
            Drawing drawing = drawingView.getDrawing();
            DrawingModel model = drawingView.getModel();
            int size = drawing.getChildren().size();
            model.insertChildAt(layer, drawing, size - index);
        });
        removeButton.addEventHandler(ActionEvent.ACTION, o -> {
            ArrayList<Integer> indices = new ArrayList<>(listView.getSelectionModel().getSelectedIndices());
            Drawing drawing = drawingView.getDrawing();
            DrawingModel model = drawingView.getModel();
            for (int i = indices.size() - 1; i >= 0; i--) {
                model.removeFromParent(layers.get(indices.get(i)));
            }
        });
        removeButton.disableProperty().bind(Bindings.equal(listView.getSelectionModel().selectedIndexProperty(), -1));

        listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Figure>) c -> {
            Layer selected = (Layer) listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                drawingView.setActiveLayer(selected);
            }

        });

        ClipboardIO<Figure> io = new ClipboardIO<Figure>() {

            @Override
            public void write(Clipboard clipboard, List<Figure> items) {
                if (items.size() != 1) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
                ClipboardContent content = new ClipboardContent();
                Figure f = items.get(0);
                String id = f.get(Figure.STYLE_ID);
                content.putString(id == null ? "" : id);
                clipboard.setContent(content);
            }

            @Override
            public List<Figure> read(Clipboard clipboard) {
                List<Figure> list;
                if (clipboard.hasString()) {
                    list = new ArrayList<>();
                    Layer layer = layerFactory.get();
                    layer.set(Figure.STYLE_ID, clipboard.getString());
                    list.add(layer);
                } else {
                    list = null;
                }
                return list;
            }

            @Override
            public boolean canRead(Clipboard clipboard) {
                return clipboard.hasString();
            }
        };

        listView.setFixedCellSize(24.0);
        listView.setCellFactory(addSelectionLabelDndSupport(listView, this::createCell, io));
        ListViewUtil.addReorderingSupport(listView, io);
    }

    public LayerCell createCell(ListView<Figure> listView) {
        StringConverter<Figure> converter = new StringConverter<Figure>() {

            @Override
            public String toString(Figure object) {
                return object.get(Figure.STYLE_ID);
            }

            @Override
            public Figure fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        return new LayerCell(drawingView);
        // return new TextFieldListCell<>(converter);
    }

    @Override
    protected void onDrawingViewChanged(DrawingView oldValue, DrawingView newValue) {
        if (oldValue != null) {
            oldValue.activeLayerProperty().removeListener(selectedLayerHandler);
            oldValue.selectedFiguresProperty().removeListener(selectionInvalidationListener);
        }
        if (newValue != null) {
            newValue.activeLayerProperty().addListener(selectedLayerHandler);
            newValue.selectedFiguresProperty().addListener(selectionInvalidationListener);
        }

    }

    @Override
    protected void onDrawingChanged(Drawing oldValue, Drawing newValue) {
        if (oldValue != null) {
            oldValue.getChildren().removeListener(listInvalidationListener);
        }
        if (newValue != null) {
            layers = new ReversedList<>(newValue.getChildren());
            listView.setItems(layers);
            newValue.getChildren().addListener(listInvalidationListener);
        }
    }
    private Callback<ListView<Figure>, ListCell<Figure>> addSelectionLabelDndSupport(ListView<Figure> listView, Callback<ListView<Figure>, LayerCell> cellFactory, ClipboardIO<Figure> clipboardIO
    ) {
        SelectionLabelDnDSupport dndSupport = new SelectionLabelDnDSupport(listView, clipboardIO);
        Callback<ListView<Figure>, ListCell<Figure>> dndCellFactory = lv -> {
            try {
                LayerCell cell = cellFactory.call(lv);
                cell.getSelectionLabel().addEventHandler(MouseEvent.DRAG_DETECTED, dndSupport.cellMouseHandler);
                return cell;
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        };
        listView.addEventHandler(DragEvent.ANY, dndSupport.listDragHandler);
        return dndCellFactory;
    }

    /**
     * Implements DnD support for the selectionLabel. Dragging the
     * selectionLabel to a layer will move the selected items to another layer.
     */
    private class SelectionLabelDnDSupport {

        private final ListView<Figure> listView;
        private int draggedCellIndex;
        private final ClipboardIO<Figure> io;

        public SelectionLabelDnDSupport(ListView<Figure> listView, ClipboardIO<Figure> io) {
            this.listView = listView;
            this.io = io;
        }
        

        private EventHandler<? super MouseEvent> cellMouseHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.isConsumed()) return;
                if (event.getEventType() == MouseEvent.DRAG_DETECTED) {

                    draggedCellIndex = (int) Math.floor(listView.screenToLocal(0, event.getScreenY()).getY() / listView.getFixedCellSize());
                    if (0 <= draggedCellIndex && draggedCellIndex < listView.getItems().size()) {
                        Label draggedLabel = (Label) event.getSource();
                        Dragboard dragboard = draggedLabel.startDragAndDrop(new TransferMode[]{TransferMode.MOVE});
                        ArrayList<Figure> items = new ArrayList<>();
                        items.add(listView.getItems().get(draggedCellIndex));
                        io.write(dragboard, items);
                        dragboard.setDragView(draggedLabel.snapshot(new SnapshotParameters(), null));

                        // consume the event, so that it won't interfere with dnd of the underlying listview.
                        event.consume();
                    }
                } else {
                    draggedCellIndex = -1;
                }
            }

        };

        EventHandler<? super DragEvent> listDragHandler = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.isConsumed()) return;
                EventType<DragEvent> t = event.getEventType();
                if (t == DragEvent.DRAG_DROPPED) {
                    onDragDropped(event);
                } else if (t == DragEvent.DRAG_OVER) {
                    onDragOver(event);
                }
            }

            private void onDragDropped(DragEvent event) {
                if (isAcceptable(event)) {
                    event.acceptTransferModes(TransferMode.MOVE);

                    // XXX foolishly assumes fixed cell height
                    double cellHeight = listView.getFixedCellSize();
                    List<Figure> items=listView.getItems();
                    int index = Math.max(0, Math.min((int) (event.getY() / cellHeight),items.size()));

                    Figure from = items.get(draggedCellIndex);
                    moveSelectedFiguresFromToLayer((Layer)from,(Layer)items.get(index));
                    event.setDropCompleted(true);
                    event.consume();
                }
            }

            private boolean isAcceptable(DragEvent event) {
                boolean isAcceptable = (event.getGestureSource() instanceof Label)
                        && (((Label) event.getGestureSource()).getParent().getParent() instanceof LayerCell)
                        && ((LayerCell) ((Label) event.getGestureSource()).getParent().getParent()).getListView() == listView;
                return isAcceptable;
            }

            private void onDragOver(DragEvent event) {
                if (isAcceptable(event)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            }
            
            private void moveSelectedFiguresFromToLayer(Layer from, Layer to) {
                DrawingModel model=drawingView.getModel();
                LinkedHashSet<Figure> selection=new LinkedHashSet<>(drawingView.getSelectedFigures());
                for (Figure f:selection) {
                    if (f.getLayer()==from) {
                        // add child moves a figure, so we do not need to
                        // remove it explicitly
                        model.addChildTo(f,to);
                    }
                }
                
                // Update the selection. The selection still contains the
                // same figures but they have now a different ancestor.
                drawingView.getSelectedFigures().clear();
                drawingView.getSelectedFigures().addAll(selection);
            }


        };
    }

}
