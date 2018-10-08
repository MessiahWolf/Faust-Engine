/**
 * Copyright (c) 2013, Robert Cherry * All rights reserved.
 *
 * This file is part of the Faust Editor.
 *
 * The Faust Editor is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * The Faust Editor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * The Faust Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package Editor.form;

import Editor.FaustEditor;
import core.event.RoomEvent;
import core.event.RoomListener;
import core.world.Actor;
import core.world.Animation;
import core.world.Backdrop;
import core.world.RoomLayer;
import core.world.Room;
import core.world.WorldObject;
import core.world.Tileset;
import core.world.WorldItem;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import Editor.tools.Grid;
import Editor.tools.WorldCanvasTool;
import Editor.transfer.TransferableNode;
import core.world.LightSource;

/**
 *
 * @author Robert A. Cherry
 */
public class RoomCanvas extends javax.swing.JPanel implements DropTargetListener, RoomListener {

    // Variable Declaration
    // Project Classes
    private final FaustEditor editor;
    private FileToolbar fileSelector;
    private Grid grid;
    private WorldCanvasTool mapTool;
    private Room room;
    // Java Native Classes
    private Point mousePoint = new Point();
    // Data Types
    private boolean gridEditing;
    // End of Variable Declaration

    public RoomCanvas(FaustEditor editor) {

        // Call to super
        super();
        initComponents();

        // Set values
        this.editor = editor;

        // Initialize
        init();
    }

    public void connect(FileToolbar fileSelector) {
        this.fileSelector = fileSelector;
    }

    private void init() {

        // Setup Key Strokes
        addKeyStrokes();

        // Create a new Drop Target
        DropTarget dropTarget = new DropTarget(this, DnDConstants.ACTION_MOVE, this, true);
    }

    public void update(Room map) {

        // Attempt to grab first fMap from FWorld
        this.room = map;

        // Map must exist
        if (map != null) {

            // Could be final if not for adjustment below +=
            int numRows = map.getWidth() / 24 + 1;
            int numCols = map.getHeight() / 24 + 1;

            // Delete if you dont want to adjust for Fullscreen
            numRows += 20;
            numCols += 4;

            //
            if (map.getWidth() == 0 || map.getHeight() == 0) {

                numRows = (getWidth() / 24) + 1;
                numCols = (getHeight() / 24) + 1;
            }

            // Match the fMap
            final Dimension mapDimension = new Dimension(map.getWidth(), map.getHeight());
            setSize(mapDimension);
            setPreferredSize(mapDimension);
            setMaximumSize(mapDimension);
            setMinimumSize(mapDimension);

            // Create a default grid
            grid = new Grid(numRows, numCols, 24, 24, Color.GRAY);

            //
            mapTool = new WorldCanvasTool(this);
        }
    }

    private void addKeyStrokes() {

        // Add Keys to JPanel
        final KeyStroke buttonW = KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonW, "p W");
        getActionMap().put("p W", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Map Tool must exist
                if (mapTool != null) {

                    // Move the Map tool
                    mapTool.move(new Point(mapTool.getLocation().x, mapTool.getLocation().y - grid.getCellHeight()));

                    // Repaint the Canvas
                    repaint();
                }
            }
        });
        final KeyStroke buttonS = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonS, "p S");
        getActionMap().put("p S", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Map Tool must exist
                if (mapTool != null) {

                    // Move the Map tool
                    mapTool.move(new Point(mapTool.getLocation().x, mapTool.getLocation().y + grid.getCellHeight()));

                    // Repaint the Canvas
                    repaint();
                }
            }
        });
        final KeyStroke buttonD = KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonD, "p D");
        getActionMap().put("p D", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Map Tool must exist
                if (mapTool != null) {

                    // Move the Map tool
                    mapTool.move(new Point(mapTool.getLocation().x + grid.getCellWidth(), mapTool.getLocation().y));

                    // Repaint the canvas
                    repaint();
                }
            }
        });
        final KeyStroke buttonA = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonA, "p A");
        getActionMap().put("p A", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Map Tool must exist
                if (mapTool != null) {

                    // Move the Map tool
                    mapTool.move(new Point(mapTool.getLocation().x - grid.getCellWidth(), mapTool.getLocation().y));

                    // Repaint the canvas
                    repaint();
                }
            }
        });
        final KeyStroke buttonJ = KeyStroke.getKeyStroke(KeyEvent.VK_J, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonJ, "p J");
        getActionMap().put("p J", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Map Tool must exist
                if (mapTool != null) {

                    // Add Tile
                    addTile();

                    // Repaint the canvas
                    repaint();
                }
            }
        });
        final KeyStroke buttonK = KeyStroke.getKeyStroke(KeyEvent.VK_K, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonK, "p K");
        getActionMap().put("p K", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Map Tool must exist
                if (mapTool != null) {

                    // Remove Tile
                    removeTile(mapTool.getLocation());

                    // Repaint the canvas
                    repaint();
                }
            }
        });
        final KeyStroke buttonE = KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonE, "p E");
        getActionMap().put("p E", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Map Tool must exist
                if (mapTool != null) {

                    // Rotate Tile
                    mapTool.rotateTileClockwise(45.0f);

                    // Repaint the canvas
                    repaint();
                }
            }
        });
        final KeyStroke buttonR = KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonR, "p R");
        getActionMap().put("p R", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Map Tool must exist
                if (mapTool != null) {

                    // Rotate Tile
                    mapTool.rotateTileCounterClockwise(45.0f);

                    // Repaint the canvas
                    repaint();
                }
            }
        });
        final KeyStroke buttonRightBrace = KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonRightBrace, "]");
        getActionMap().put("]", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Moving up
                if ((grid.getCellWidth() + 8) < room.getWidth()) {

                    // Adjust the cell width and height
                    grid.setCellWidth(grid.getCellWidth() + 8);
                    grid.setCellHeight(grid.getCellHeight() + 8);

                    if (mapTool != null) {

                        // Adjust the Map Tool to the grid
                        mapTool.revalidate();
                    }

                    // Repaint and validate the grid
                    grid.validate();

                    // Repaint the canvas
                    repaint();
                }
            }
        });
        final KeyStroke buttonLeftBrace = KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonLeftBrace, "[");
        getActionMap().put("[", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Moving down
                if ((grid.getCellWidth() - 8) > 16) {

                    // Adjust the cell width and height
                    grid.setCellWidth(grid.getCellWidth() - 8);
                    grid.setCellHeight(grid.getCellHeight() - 8);

                    if (mapTool != null) {

                        // Adjust the Map Tool to the grid
                        mapTool.revalidate();
                    }

                    // Repaint and validate the grid
                    grid.validate();

                    // Repaint the canvas
                    repaint();
                }
            }
        });
        final KeyStroke buttonC = KeyStroke.getKeyStroke(KeyEvent.VK_C, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonC, "p C");
        getActionMap().put("p C", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Toggle
                mapTool.toggleCentered();

                // Repaint the canvas
                repaint();
            }
        });
        final KeyStroke buttonO = KeyStroke.getKeyStroke(KeyEvent.VK_O, 0, true);
        getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonO, "O");
        getActionMap().put("O", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Not forever
                editor.takeScreenshot();
            }
        });
    }

    public void addAt(WorldObject object, Point point) {

        // Grab the selected layer
        final RoomLayer selectedLayer = room.getSelectedLayer();

        // Layer must exist
        if (selectedLayer != null) {

            // Add to the specified layer
            selectedLayer.addObject(object, point);
        }

        // repaint
        repaint();
    }

    private void validateResource(Object object, Point point) {

        // Little things last
        if (room != null) {

            // Determine what kind of resource it is --
            if (object instanceof Actor) {

                // Cast to an actor
                Actor actor = (Actor) object;

                //
                actor = actor.reproduce();

                // Add the worldObject to the fMap
                room.getSelectedLayer().addObject(actor, point);

                //
                return;
            } else if (WorldItem.class.isAssignableFrom(object.getClass())) {

                // Cast to an Item
                WorldItem item = (WorldItem) object;

                //
                item = (WorldItem) item.reproduce();

                //
                room.getSelectedLayer().addObject(item, point);

                //
                return;
            } else if (object instanceof Backdrop) {

                // Cast to a background
                final Backdrop background = (Backdrop) object;

                // Support for multiple backgrounds now...
                room.addBackground(background);

                //
                return;
            } else if (object instanceof LightSource) {

                //
                final LightSource source = (LightSource) object;

                //
                System.out.println("Mouse Point: " + mousePoint);
                System.out.println("Given Point: " + point);
                System.out.println("Room Dimensions: " + room.getWidth() + "x" + room.getHeight());

                //  Add light source to the selected layer.
                room.getSelectedLayer().addLightSource(source, point);
            }
        }

        // Map with a different world
        if (object instanceof Room) {

            //
            // System.out.println("Cell Dropped.");
            // Cast to a Cell
            final Room cell = (Room) object;

            // Make sure we have a default selected layer
            cell.setDefaultLayer();

            // Adding the ones who listen to maps which are the room canvas and manager
            cell.removeRoomListener(this);
            cell.removeRoomListener(editor.getManager());
            cell.addMapListener(this);
            cell.addMapListener(editor.getManager());
            // Change the world
            update(cell);
            editor.getManager().update(cell);
        }
    }

    private void addTile() {

        // Check if the fMap tool is painting tiles
        if (mapTool.getTilePaint() != null) {

            // Grab selected layer
            final RoomLayer selectedLayer = room.getSelectedLayer();

            // Check for null layer
            if (selectedLayer != null) {

                // Paint the tile upon single click with tile selected
                mapTool.paintTile(selectedLayer);
            }
        }
    }

    private void removeTile(Point point) {

        // Grab the selected layer
        final RoomLayer selectedLayer = room.getSelectedLayer();

        // Check for null layer
        if (selectedLayer != null) {

            //Grab instance at position
            final WorldObject object = selectedLayer.getInstanceAtPosition(point);

            // Check for null object
            if (object != null) {

                // Remove the object from the layer
                selectedLayer.remove(object);
            }
        }
    }

    @Override
    public void paint(Graphics monet) {

        //
        super.paint(monet);

        // Graphics Object
        final Graphics2D manet = (Graphics2D) monet;

        // Apply the rendering hint
        manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Map must exist
        if (room != null) {

            // On Cell trigger
            manet.setColor(new Color(0, 24, 66));
            manet.fillRect(0, 0, getWidth(), getHeight());

            // Draw lines to indicate the boundaries
            manet.setColor(Color.RED);
            manet.drawRect(0, 0, room.getWidth(), room.getHeight());

            // Paint the Map
            manet.drawImage(room.paint(this, mousePoint), 0, 0, this);

            // Draw the grid
            if (grid != null) {
                grid.paint(manet, this, .45f);
            }

            // Draw the map tool
            if (mapTool != null) {
                mapTool.paint(manet, this);
                manet.drawImage(mapTool.getToolImage(), mousePoint.x, mousePoint.y, this);
            }
        }

        // Dispose the graphics object
        manet.dispose();
    }

    public Grid getGrid() {
        return grid;
    }

    public Room getMap() {
        return room;
    }

    public WorldCanvasTool getTool() {
        return mapTool;
    }

    public void setTile(Tileset tileset, int index) {

        // Must be an active world
        if (mapTool != null) {

            // Adjust the fMap Tool
            mapTool.setMode(WorldCanvasTool.FLAG_MARKER);
            mapTool.setTilePaint(tileset, index);
        }

        // Repaint
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMaximumSize(new java.awt.Dimension(400, 300));
        setMinimumSize(new java.awt.Dimension(400, 300));
        setPreferredSize(new java.awt.Dimension(400, 300));
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved

        //
        if (room != null) {

            if (!editor.getCursor().getName().equalsIgnoreCase("Blank")) {
                //
                final BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

                //
                final Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "Blank");

                //
                editor.setCursor(blank);
            }
        }

        // Grab the location
        mousePoint = evt.getPoint();

        // Move the map tool around
        if (mapTool != null) {
            mapTool.move(mousePoint);
        }

        // Grab the world and it must exist
        if (room != null) {

            // Grab its active layer
            final RoomLayer layer = room.getSelectedLayer();

            // Must exist
            if (layer != null) {

                // Grab the list of objects from the current selected layer; we wouldn't want to do them all needlessly
                final ArrayList<WorldObject> objects = layer.getInhabitants();

                // Iterate over the list of objects
                for (int i = 0; i < objects.size(); i++) {

                    // Grab the current tile in the iteration
                    final WorldObject object = objects.get(i);

                    if (object.getPreciseBounds() != null) {

                        // Mouse must be inside the objects boundry
                        if (object.getPreciseBounds().contains(mousePoint)) {

                            // Draw the border so that the user if assured of which object it has entered
                            object.setDrawBorder(true);
                        } else {

                            // No longer draw the border
                            object.setDrawBorder(false);
                        }
                    }
                }
            }
        }

        // Repaint after all of those shinnaniganns
        repaint();
    }//GEN-LAST:event_formMouseMoved

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked

        // Grab the point
        mousePoint = evt.getPoint();

        if (room != null) {

            // Layer Check
            final RoomLayer layer = room.getSelectedLayer();

            // Layer must exist
            if (layer == null) {
                return;
            }

            // Now we have clearence to paint onto the canvas.
            if (mapTool != null) {

                //
                if (evt.getButton() == MouseEvent.BUTTON1) {

                    // Check fMap Tool Mode
                    switch (mapTool.getMode()) {
                        case WorldCanvasTool.FLAG_ERASER:
                            // Remove the Tile
                            removeTile(mousePoint);
                            break;
                        case WorldCanvasTool.FLAG_MARKER:
                            // Warn if there is no world or map on the canvas
                            // Paint the Tile
                            addTile();
                            break;
                        case WorldCanvasTool.FLAG_SELECT:

                            //
                            final LightSource lightSource = layer.getLightAtPosition(mousePoint);

                            //
                            if (evt.getClickCount() == 1) {

                                //
                                if (lightSource != null && lightSource.getBounds().contains(mousePoint)) {
                                    lightSource.reprojectLightSource();
                                }
                            } else if (evt.getClickCount() == 2) {
                                
                                //
                                if (lightSource != null && lightSource.getBounds().contains(mousePoint)) {
                                    
                                    //
                                    LightEditor maker = new LightEditor(editor, editor.getDelegate(), room, lightSource, true);
                                    maker.setVisible(true);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                } else if (evt.getButton() == MouseEvent.BUTTON3) {

                    //
                    switch (mapTool.getMode()) {
                        case WorldCanvasTool.FLAG_MARKER:
                            // Change to Destroy Mode on Right Click
                            mapTool.setMode(WorldCanvasTool.FLAG_ERASER);
                            mapTool.clearTilePaint();
                            break;
                        case WorldCanvasTool.FLAG_ERASER:
                            // Change to Destroy Mode on Right Click
                            mapTool.setMode(WorldCanvasTool.FLAG_SELECT);
                            mapTool.clearTilePaint();
                            break;
                        case WorldCanvasTool.FLAG_SELECT:

                            //
                            final LightSource lightSource = layer.getLightAtPosition(mousePoint);

                            // Light Source specific
                            if (lightSource != null && lightSource.getBounds().contains(mousePoint)) {
                                lightSource.setFilled(!lightSource.isFilled());
                            } else {
                                mapTool.setMode(WorldCanvasTool.FLAG_MARKER);
                                mapTool.clearTilePaint();
                            }
                        default:
                            // Clear tile Paint if on other mode than CREATE
                            mapTool.clearTilePaint();
                            break;
                    }
                }
            }
        }

        // Repaint the canvas
        repaint();
    }//GEN-LAST:event_formMouseClicked

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered

        // Request focus @TODO re-enable this when we figure out how to 
        // determine if other windows are open.
        mousePoint = evt.getPoint();

        //
        if (mapTool != null) {
            //
            mapTool.setVisible(true);
        }

        // requestFocus();
        //
        if (room != null) {

            //
            final BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

            //
            final Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "Blank");

            //
            editor.setCursor(blank);
        }

        //
        repaint();
    }//GEN-LAST:event_formMouseEntered

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved

        //
        final int rotation = evt.getWheelRotation();

        // Move wheel went up and we're editing the grid
        if (gridEditing) {

            if (rotation > 0) {
                //
                if ((grid.getCellWidth() - 8) > 16) {

                    // Adjust the cell width and height
                    grid.setCellWidth(grid.getCellWidth() - 8);
                    grid.setCellHeight(grid.getCellHeight() - 8);

                    // Adjust the fMap tool to the grid
                    mapTool.revalidate();

                    // Repaint and validate the grid
                    grid.validate();

                    //
                    repaint();
                }
            } else if ((grid.getCellWidth() + 8) < room.getWidth()) {

                // Adjust the cell width and height
                grid.setCellWidth(grid.getCellWidth() + 8);
                grid.setCellHeight(grid.getCellHeight() + 8);

                // Adjust the fMap tool to the grid
                mapTool.revalidate();

                // Repaint and validate the grid
                grid.validate();

                //
                repaint();
            }
        }

        // Map tool options.
        if (mapTool != null) {

            // Ask if mapTool has an active tile
            if (mapTool.getTilePaint() != null) {

                // Dealing with rotation of tile
                if (rotation > 0) {
                    mapTool.rotateTileCounterClockwise(45.0f);
                } else {
                    mapTool.rotateTileClockwise(45.0f);
                }

                // Repaint the canvas afterwards.
                repaint();
            }
        }

        //
        final LightSource lightSource = room.getSelectedLayer().getLightAtPosition(mousePoint);

//        // Dealing with the LightSource
        if (lightSource != null) {

            //
            if (lightSource.getBounds().contains(evt.getPoint())) {

                //
                if (rotation > 0) {
                    lightSource.increaseAngle(-1);
                } else {
                    lightSource.increaseAngle(1);
                }

                //
                repaint();
            }
        }
    }//GEN-LAST:event_formMouseWheelMoved

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged

        // Update the mouse position
        mousePoint = evt.getPoint();

        //
        if (mapTool == null) {
            return;
        }

        // If and only if we're in select mode.
        if (mapTool.getMode() == WorldCanvasTool.FLAG_SELECT) {

            // Light Source must exist
            final LightSource lightSource = room.getSelectedLayer().getLightAtPosition(mousePoint);

            //
            if (lightSource != null) {

                // If we're inside the bounds of the lightsource box.
                if (lightSource.getBounds().contains(mousePoint)) {

                    // Move it.
                    lightSource.setX(mousePoint.x);
                    lightSource.setY(mousePoint.y);
                }
            }
        }

        // Always repaint
        repaint();
    }//GEN-LAST:event_formMouseDragged

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited

        // TODO addObject your handling code here:
        editor.setCursor(Cursor.getDefaultCursor());

        //
        if (mapTool != null) {
            //
            mapTool.setVisible(false);
        }
        repaint();
    }//GEN-LAST:event_formMouseExited

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased

        //
        mousePoint = evt.getPoint();

        // Quick check to make sure this content pane has loaded fully.
        if (mapTool == null) {
            return;
        }

        //
        if (evt.getButton() == MouseEvent.BUTTON1) {

            //
            if (mapTool.getMode() == WorldCanvasTool.FLAG_SELECT) {

                //
                final LightSource lightSource = room.getSelectedLayer().getLightAtPosition(mousePoint);

                // Light Source specific
                if (lightSource != null) {
                    lightSource.reprojectLightSource();
                    repaint();
                }
            }
        }
    }//GEN-LAST:event_formMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void drop(DropTargetDropEvent event) {

        // Try--
        try {

            // Collect data about the object dropped onto the panel
            final Transferable transferable = event.getTransferable();

            // Cast a fMap object out of the transferable object
            final DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) transferable.getTransferData(TransferableNode.nodeFlavor);

            // Check if it is an accepted type of dropped object
            if (event.isDataFlavorSupported(TransferableNode.nodeFlavor)) {

                //
                //// System.out.println("Supported Flavor");
                // Accept this flavor of dropped object
                event.acceptDrop(DnDConstants.ACTION_COPY);

                //
                final Object userObject = newNode.getUserObject();

                //
                if (mapTool != null) {

                    // Attempt
                    validateResource(userObject, event.getLocation());
                } else {

                    //
                    validateResource(userObject, event.getLocation());
                }

                // Complete the dropping operation
                event.dropComplete(true);

                if (mapTool != null) {

                    // Clear mapTool animation shadow
                    mapTool.clearShadow();
                }

                repaint();
                return;
            }

            // Otherwise reject the dropped object
            event.rejectDrop();
        } catch (UnsupportedFlavorException | IOException ex) {
            System.err.println("Drop Rejected.");
            //
            event.rejectDrop();
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

        //
        requestFocus();

        // Try--
        try {

            // Grab the transfered object
            final Transferable trans = dtde.getTransferable();

            if (dtde.isDataFlavorSupported(TransferableNode.nodeFlavor)) {

                //
                //// System.out.println("Supported Flavor entered.");
                // Cast to node
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) trans.getTransferData(TransferableNode.nodeFlavor);

                // Grab its user object
                final Object userObject = node.getUserObject();

                // Must be an active world to drag and drop backgrounds and actors
                if (room != null) {

                    // Background solving
                    if (userObject instanceof Backdrop) {

                        // Accept backgrounds
                        dtde.acceptDrag(DnDConstants.ACTION_COPY);

                        // Kick out
                        return;
                    } else if (userObject instanceof Actor) {

                        // Grab hover image
                        mapTool.shadow((Actor) userObject);
                        dtde.acceptDrag(DnDConstants.ACTION_COPY);

                        // Kick out
                        return;
                    } else if (userObject instanceof WorldItem) {

                        //
                        mapTool.shadow((WorldItem) userObject);
                        dtde.acceptDrag(DnDConstants.ACTION_COPY);

                        //
                        return;
                    } else if (userObject instanceof LightSource) {

                        //
                        dtde.acceptDrag(DnDConstants.ACTION_COPY);
                        return;
                    }
                }

                //
                if (userObject instanceof Room) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                } else {
                    dtde.rejectDrag();
                }
            }
        } catch (UnsupportedFlavorException | IOException ioe) {
            System.err.println("Unsupported Flavor " + ioe);
        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

        if (mapTool != null) {

            //
            final Animation animation = mapTool.getShadow();

            //
            if (animation != null) {

                //
                final BufferedImage image = animation.getCurrentImage();

                // Reposition about the mouse
                final Point pointFocal = new Point(dtde.getLocation().x - image.getWidth(this) / 2, dtde.getLocation().y - image.getHeight(this) / 2);

                //
                mapTool.move(pointFocal);
            }
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        //
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        //
    }

    @Override
    public void mapModified(RoomEvent event) {

        // On the event that something was added (FMap, FTransition)
        switch (event.getStateChange()) {
            case RoomEvent.ADDED: {
                // Grab the object that changed
                final Object object = event.getSource();
                // Still check
                if (object instanceof Room) {

                    // Allow changes to be saved
                    fileSelector.setInvalidated();
                }
            }
            case RoomEvent.REMOVED: {
                // Grab the object that changed
                final Object object = event.getSource();
                // Still check
                if (object instanceof Room) {

                    // Allow changes to be saved
                    fileSelector.setInvalidated();
                }
            }
            case RoomEvent.MODIFIED:
                // Allow changes to be saved
                fileSelector.setInvalidated();
            default:
                break;
        }

        editor.update();
    }
}
