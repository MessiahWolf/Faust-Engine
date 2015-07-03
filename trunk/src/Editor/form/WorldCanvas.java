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
import core.event.WorldEvent;
import core.event.WorldListener;
import core.world.Actor;
import core.world.Animation;
import core.world.Backdrop;
import core.world.World;
import core.world.WorldCellLayer;
import core.world.WorldCell;
import core.world.WorldObject;
import core.world.Tileset;
import core.world.WorldItem;
import io.resource.ResourceDelegate;
import java.awt.BasicStroke;
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
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import Editor.tools.Grid;
import Editor.tools.WorldCanvasTool;
import Editor.transfer.TransferableNode;

/**
 *
 * @author Robert A. Cherry
 */
public class WorldCanvas extends javax.swing.JPanel implements DropTargetListener, WorldListener {

    // Variable Declaration
    // Project Classes
    private FaustEditor editor;
    private FileToolbar fileSelector;
    private Grid grid;
    private WorldCanvasTool mapTool;
    private ResourceDelegate delegate;
    private WorldCell worldCell;
    private WorldCellSelector cellSelector;
    private World world;
    // Data Types
    private boolean gridEditing;
    // End of Variable Declaration

    public WorldCanvas(FaustEditor editor, ResourceDelegate delegate, WorldCellSelector cellSelector, World world) {

        // Call to super
        super();
        initComponents();

        // Set values
        this.editor = editor;
        this.delegate = delegate;
        this.cellSelector = cellSelector;
        this.world = world;

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
        final DropTarget dropTarget = new DropTarget(this, DnDConstants.ACTION_MOVE, this, true);
    }

    public void update(WorldCell map) {

        // Attempt to grab first fMap from FWorld
        this.worldCell = map;

        // Map must exist
        if (map != null) {

            // Grab its world
            final World mapWorld = map.getWorld();

            // World integrity check
            if (mapWorld != null && world != mapWorld) {
                world = mapWorld;
            }

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
                if ((grid.getCellWidth() + 8) < worldCell.getWidth()) {

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

        // Adjust the location
        object.setX(point.x);
        object.setY(point.y);

        // Grab the selected layer
        final WorldCellLayer selectedLayer = worldCell.getSelectedLayer();

        // Add to the specified layer
        worldCell.add(object, selectedLayer);

        // repaint
        repaint();
    }

    private void validateResource(Object object, Point point) {

        // Little things last
        if (world != null && worldCell != null) {

            // Determine what kind of resource it is --
            if (object instanceof Actor) {

                // Cast to an actor
                Actor actor = (Actor) object;

                //
                actor = actor.reproduce();

                // Add the worldObject to the fMap
                worldCell.addAt(actor, worldCell.getSelectedLayer(), point);

                //
                return;
            } else if (WorldItem.class.isAssignableFrom(object.getClass())) {

                // Cast to an Item
                WorldItem item = (WorldItem) object;

                //
                item = (WorldItem) item.reproduce();

                //
                worldCell.addAt(item, worldCell.getSelectedLayer(), point);

                //
                return;
            } else if (object instanceof Backdrop) {

                // Cast to a background
                final Backdrop background = (Backdrop) object;

                // Support for multiple backgrounds now...
                worldCell.addBackground(background);

                System.err.println("Added it :D.");

                //
                return;
            }
        }

        // Map with a different world
        if (object instanceof WorldCell) {

            // Cast to a Cell
            final WorldCell cell = (WorldCell) object;

            //  Grab its world
            final World cellParent = cell.getWorld();

            // Do not accept loose maps; they need physics details behind them to be rendered.
            if (cellParent != null) {

                // Make sure its not the exact same world; we do not need to reload it then
                if (cellParent != world) {

                    // Change the world
                    update(cellParent, cell);

                    // Kick out
                    return;
                }
            } else {

                //
                if (world != null) {
                    world.addCell(cell);
                    update(cell.getWorld(), cell);
                    return;
                }
            }
        }

        // Base
        if (object instanceof World) {

            // Has previous world
            if (world != null) {
                world.removeWorldListener(this);
            }

            // Cast to an FWorld
            world = (World) object;
            world.addWorldListener(this);

            //
            update(world, world.getFirstMap());
        }
    }

    private void addTile() {

        // Check if the fMap tool is painting tiles
        if (mapTool.getTilePaint() != null) {

            // Grab selected layer
            final WorldCellLayer selectedLayer = worldCell.getSelectedLayer();

            // Check for null layer
            if (selectedLayer != null) {

                // Paint the tile upon single click with tile selected
                mapTool.paintTile(selectedLayer);
            }
        }
    }

    private void removeTile(Point point) {

        // Grab the selected layer
        final WorldCellLayer selectedLayer = worldCell.getSelectedLayer();

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
    public void paintComponent(Graphics monet) {

        //
        super.paintComponent(monet);

        // Graphics Object
        final Graphics2D manet = (Graphics2D) monet;

        // Apply the rendering hint
        manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // World must exist
        if (world != null) {

            // Map must exist
            if (worldCell != null) {

                // Paint the Map
                manet.drawImage(worldCell.paint(this), 0, 0, this);

                // Draw the grid
                if (grid != null) {
                    grid.paint(manet, this, .45f);
                }

                // Draw the map tool
                if (mapTool != null) {
                    mapTool.paint(manet, this);
                }
            }
        } else {

            // Underbrush
            manet.setStroke(new BasicStroke());
            manet.setColor(new Color(222, 222, 222));
            manet.fillRect(0, 0, getWidth(), getHeight());

            // Overlay
            manet.setColor(new Color(160, 160, 160));

            //
            final String str = "Drag world from Resource Palette to create a map";

            // Draw Text
            final int stringWidth = (int) manet.getFontMetrics().getStringBounds(str, manet).getWidth();
            final int stringHeight = (int) manet.getFontMetrics().getStringBounds(str, manet).getHeight();

            // Draw the string
            manet.drawString(str, getWidth() / 2 - stringWidth / 2, getHeight() / 2 + stringHeight / 2);
        }

        // Dispose the graphics object
        manet.dispose();
    }

    public Grid getGrid() {
        return grid;
    }

    public World getWorld() {
        return world;
    }

    public WorldCell getMap() {
        return worldCell;
    }

    public WorldCanvasTool getTool() {
        return mapTool;
    }

    public void setWorld(World world) {

        //
        this.world = world;

        // World must exist
        if (world != null) {
            world.addWorldListener(this);
        }
    }

    public void update(World world, WorldCell map) {

        //
        this.world = world;

        // World must exist
        if (world != null) {

            //
            world.addWorldListener(this);

            // Update the map selector to show all the rooms of this world.
            cellSelector.update(this, world);

            // Add the world to the editor to validate it across all nesscary components
            editor.update(world);

            // If and only if this world has cells
            if (world.getCellList().isEmpty() == false) {

                //
                //System.out.println("Djaldkjsalkdjaslkjdklaj.");

                // Grab the split pane
                final JSplitPane worldSplitPane = editor.getWorldSplitPane();

                // Do not adjust this setting
                worldSplitPane.setBottomComponent(cellSelector.getComponent());
                worldSplitPane.setDividerLocation(.90f);
            }// else

            //
            update(map);
        } else {
            System.out.println("World is null");
        }
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

        // Grab the location
        final Point point = evt.getPoint();

        // Move the map tool around
        if (mapTool != null) {
            mapTool.move(point);
        }

        // Grab the world and it must exist
        if (worldCell != null) {

            // Grab its active layer
            final WorldCellLayer layer = worldCell.getSelectedLayer();

            // Must exist
            if (layer != null) {

                // Grab the list of objects from the current selected layer; we wouldn't want to do them all needlessly
                final ArrayList<WorldObject> objects = layer.getInhabitants();

                // Iterate over the list of objects
                for (int i = 0; i < objects.size(); i++) {

                    // Grab the current tile in the iteration
                    final WorldObject object = objects.get(i);

                    if (object.getBounds() != null) {

                        // Mouse must be inside the objects boundry
                        if (object.getBounds().contains(point)) {

                            // Draw the border so that the user if assured of which object it has entered
                            object.setDrawBorder(true);
                        } else {

                            // No longer draw the border
                            object.setDrawBorder(false);
                        }
                    }
                }
            }

            // Repaint after all of those shinnaniganns
            repaint();
        }
    }//GEN-LAST:event_formMouseMoved

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked

        // Grab the point
        final Point point = evt.getPoint();

        if (worldCell != null) {

            // Layer Check
            final WorldCellLayer layer = worldCell.getSelectedLayer();

            // Layer must exist
            if (layer == null) {
                return;
            }

            // Now we have clearence to paint onto the canvas.
            if (mapTool != null) {

                //
                if (evt.getButton() == MouseEvent.BUTTON1) {

                    // Check fMap Tool Mode
                    if (mapTool.getMode() == WorldCanvasTool.FLAG_ERASER) {

                        // Remove the Tile
                        removeTile(point);

                        // Repaint Canvas
                        repaint();
                    } else if (mapTool.getMode() == WorldCanvasTool.FLAG_MARKER) {

                        // Warn if there is no world or map on the canvas
                        // Paint the Tile
                        addTile();

                        // Repaint Canvas
                        repaint();
                    }
                } else if (evt.getButton() == MouseEvent.BUTTON3) {

                    //
                    if (mapTool.getMode() == WorldCanvasTool.FLAG_MARKER) {

                        // Change to Destroy Mode on Right Click
                        mapTool.setMode(WorldCanvasTool.FLAG_ERASER);
                        mapTool.clearTilePaint();
                    } else {

                        // Clear tile Paint if on other mode than CREATE
                        mapTool.clearTilePaint();
                    }

                    // Repaint the canvas
                    repaint();
                }
            }
        }
    }//GEN-LAST:event_formMouseClicked

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered

        // Request focus
        requestFocus();

        //
        if (world != null && worldCell != null) {

            //
            final BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

            //
            final Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "Blank");

            //
            editor.setCursor(blank);
        }
    }//GEN-LAST:event_formMouseEntered

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved

        // TODO add your handling code here:
        if (gridEditing) {

            //
            if (evt.getWheelRotation() < 0) {

                // Moving up
                if ((grid.getCellWidth() + 8) < worldCell.getWidth()) {

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
            } else {

                // Moving down
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
            }
        } else {

            if (mapTool != null) {

                // Ask if mapTool has an active tile
                if (mapTool.getTilePaint() != null) {

                    // Dealing with rotation of tile
                    if (evt.getWheelRotation() > 0) {
                        mapTool.rotateTileCounterClockwise(45.0f);
                    } else {
                        mapTool.rotateTileClockwise(45.0f);
                    }

                    // Repaint the canvas afterwards.
                    repaint();
                }
            }
        }
    }//GEN-LAST:event_formMouseWheelMoved

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
    }//GEN-LAST:event_formMouseDragged

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited

        // TODO add your handling code here:
        editor.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_formMouseExited

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
                //System.out.println("Supported Flavor");

                // Accept this flavor of dropped object
                event.acceptDrop(DnDConstants.ACTION_COPY);

                //
                final Object userObject = newNode.getUserObject();

                if (mapTool != null) {

                    // Attempt
                    validateResource(userObject, mapTool.getLocation());
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

        requestFocus();

        // Try--
        try {

            // Grab the transfered object
            final Transferable trans = dtde.getTransferable();

            if (dtde.isDataFlavorSupported(TransferableNode.nodeFlavor)) {

                //
                //System.out.println("Supported Flavor entered.");

                // Cast to node
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) trans.getTransferData(TransferableNode.nodeFlavor);

                // Grab its user object
                final Object userObject = node.getUserObject();

                // Must be an active world to drag and drop backgrounds and actors
                if (worldCell != null) {

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
                    }
                }

                //
                if (userObject instanceof World || userObject instanceof WorldCell) {
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
    public void worldModified(WorldEvent event) {

        // On the event that something was added (FMap, FTransition)
        if (event.getStateChange() == WorldEvent.ADDED) {

            // Grab the object that changed
            final Object object = event.getSource();

            // Still check
            if (object instanceof WorldCell) {

                // Allow changes to be saved
                fileSelector.setInvalidated();
            }
        } else if (event.getStateChange() == WorldEvent.REMOVED) {

            // Grab the object that changed
            final Object object = event.getSource();

            // Still check
            if (object instanceof WorldCell) {

                // Allow changes to be saved
                fileSelector.setInvalidated();
            }
        } else if (event.getStateChange() == WorldEvent.CHANGED) {

            // Allow changes to be saved
            fileSelector.setInvalidated();
        }
    }
}
