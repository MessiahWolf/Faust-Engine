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
import core.event.MapEvent;
import core.event.MapListener;
import core.world.WorldCell;
import core.world.World;
import io.resource.ResourceDelegate;
import io.resource.ResourceProducer;
import io.resource.ResourceReader;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
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
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import Editor.transfer.TransferableNode;

/**
 *
 * @author robert
 */
public class WorldCellSelector implements ActionListener, DropTargetListener {

    // Variable Declaration
    // Java Native Classes
    private ImageIcon iconMap;
    private JScrollPane scrollPane;
    // Project Classes
    private FaustEditor editor;
    private World world;
    private ResourceDelegate delegate;
    private WorldCanvas worldCanvas;
    // End of Variable Declaration

    public WorldCellSelector(FaustEditor editor, ResourceDelegate delegate) {

        // Set values
        this.editor = editor;
        this.delegate = delegate;

        //
        init();
    }

    private void init() {

        //
        final Class closs = getClass();

        //
        iconMap = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-map24.png");

        // Instantiate the scroll pane
        scrollPane = new JScrollPane();
    }

    private void validateResource(Object object, Point point) {

        // This component only accepts FMaps as valid input
        if (object instanceof WorldCell) {

            // Make sure you dropped it on a JButton and not a filler or the ButtonJPanel
            final Component component = getComponentAtPosition(point);

            if (component instanceof JButton) {

                // Get the button at position
                final JButton button = (JButton) component;

                //
                int index;

                // Grab the index from the button
                try {
                    index = Integer.parseInt(button.getName());
                } catch (NumberFormatException nfe) {
                    // Do nothing
                    index = 0;
                }

                // Grab the image from the map
                final WorldCell map = (WorldCell) object;

                // Make sure we do not add duplicates
                if (world.containsMap((WorldCell) object)) {

                    // Message to show
                    final String msg = "This world already contains this map";

                    //
                    JOptionPane.showMessageDialog(editor, msg);

                    //
                    return;
                }

                // Change the text of the button
                //button.setText(map.getDisplayName());
                button.setToolTipText(map.getDisplayName());

                // Add the map to the world
                world.addMapAtIndex(map, index);
            }
        }
    }

    private Component getComponentAtPosition(Point point) {

        // Grabs the Room button you clicked
        final Component component = scrollPane.getViewport().getView();

        // Return it
        return component.getComponentAt(point);
    }

    public void update(WorldCanvas worldCanvas, World world) {

        // Set values
        this.worldCanvas = worldCanvas;
        this.world = world;
    }

    public JScrollPane getComponent() {

        // Create the buttonJPanel which will hold all the room buttons.
        final JPanel buttonJPanel = new JPanel();

        final Dimension fillerDimension = new Dimension(8, 32);

        // Add a small separator
        buttonJPanel.add(new Filler(fillerDimension, fillerDimension, fillerDimension));

        // Make the buttonJPanel accept dropped FMaps
        final DropTarget target = new DropTarget(buttonJPanel, DnDConstants.ACTION_MOVE, this, true);
        buttonJPanel.setLayout(new BoxLayout(buttonJPanel, BoxLayout.LINE_AXIS));

        // Adjust the JScrollPane
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        //  Simple value holders
        int width = 0;

        // Static size of the buttons; cannot change
        final Dimension buttonDimension = new Dimension(88, 32);

        // Grab the mapList from the world
        final ArrayList<WorldCell> mapList = world.getCellList();

        // We need an existing world
        if (world != null) {

            if (mapList.size() > 0) {

                // Iterate
                for (int i = 0; i < mapList.size(); i++) {

                    // Grab the current map
                    final WorldCell map = mapList.get(i);

                    // Grab image
                    //Image image = null;

                    // Create the JButtons
                    final MapJButton button = new MapJButton(map, buttonDimension);
                    button.setName(String.valueOf(i));

                    // Setting Tooltip Text
                    if (map != null) {
                        button.setToolTipText(map.getDisplayName());
                    }

                    // Add to the width
                    width += buttonDimension.width + fillerDimension.width;

                    // Set the graphic as the image of the tile, not just a default image
                    button.addActionListener(this);

                    // Add to this layout
                    buttonJPanel.add(button);

                    // Add a small separator
                    buttonJPanel.add(new Filler(fillerDimension, fillerDimension, fillerDimension));
                }
            }
        }

        // Dimension for JScrollPane
        final Dimension paneDimension = new Dimension(width == 0 ? worldCanvas.getWidth() : width, 50);
        final Dimension panelDimension = new Dimension(width, 42);

        // Adjust the size of the button panel to fit contents
        buttonJPanel.setPreferredSize(panelDimension);
        buttonJPanel.setMinimumSize(panelDimension);
        buttonJPanel.setSize(panelDimension);
        buttonJPanel.setMaximumSize(panelDimension);
        buttonJPanel.revalidate();

        // Adjust scroll pane to match world canvas size and internally scroll to button dimensions
        scrollPane.setPreferredSize(paneDimension);
        scrollPane.setMaximumSize(paneDimension);
        scrollPane.setMinimumSize(paneDimension);
        scrollPane.setSize(paneDimension);
        scrollPane.revalidate();

        // Give the scrollpane the buttonJPanel as its view
        scrollPane.setViewportView(buttonJPanel);

        // Return it
        return scrollPane;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {

        // Grab the object
        final Object object = evt.getSource();

        // Soon -- communicate with world canvas
        if (worldCanvas != null) {

            // Need an existing world
            if (world != null) {

                // Change the active map this way
                if (object instanceof MapJButton) {

                    // Cast to JButton
                    final MapJButton sourceJButton = (MapJButton) object;

                    // Is that map already loaded?
                    final WorldCell map = sourceJButton.getMap();

                    if (worldCanvas.getMap() == map) {

                        // Message
                        final String msg = "That map is already loaded.\nReload map?";

                        // Ask for user input
                        final int answer = JOptionPane.showConfirmDialog(editor, msg, "Warning", JOptionPane.YES_NO_OPTION);

                        // Accepted
                        if (answer == JOptionPane.YES_OPTION) {

                            // Reload the map freshly
                            worldCanvas.update(world, map);
                        }
                    } else {

                        // Load the map fresh
                        worldCanvas.update(world, map);
                    }
                }
            }
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

        // Try--
        try {

            // Grab the transfered object
            final Transferable trans = dtde.getTransferable();

            if (dtde.isDataFlavorSupported(TransferableNode.nodeFlavor)) {

                // Cast to node
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) trans.getTransferData(TransferableNode.nodeFlavor);

                // Grab its user object
                final Object object = node.getUserObject();

                // Only accept FMaps
                if (object instanceof WorldCell) {
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
        //
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
    public void drop(DropTargetDropEvent event) {

        // Try
        try {

            // Collect data about the object dropped onto the panel
            final Transferable transferable = event.getTransferable();

            // Cast a fMap object out of the transferable object
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) transferable.getTransferData(TransferableNode.nodeFlavor);

            // Check if it is an accepted type of dropped object
            if (event.isDataFlavorSupported(TransferableNode.nodeFlavor)) {

                // Accept this flavor of dropped object
                event.acceptDrop(DnDConstants.ACTION_COPY);

                //
                final Object object = node.getUserObject();

                // Attempt to place the value on the button
                validateResource(object, event.getLocation());

                // Complete the dropping operation
                event.dropComplete(true);
                return;
            }

            // Otherwise reject the dropped object
            event.rejectDrop();
        } catch (UnsupportedFlavorException | IOException ex) {
            System.err.println(ex);
        }
    }

    private class MapJButton extends JButton implements MapListener {

        // Sub-Class Variable Declaration
        // Java Native Classes
        private ImageIcon iconNull;
        // Project Classes
        private WorldCell map;
        // End of Sub-Class Variable Declaration

        public MapJButton(WorldCell map, Dimension dimension) {

            // Call to super
            super();

            // Set value
            this.map = map;

            // Initialize
            init(dimension);
        }

        private void init(Dimension dimension) {

            // Apply dimension changes
            setPreferredSize(dimension);
            setMaximumSize(dimension);
            setSize(dimension);
            setMinimumSize(dimension);

            // Add a listener
            map.addMapListener(this);

            //
            iconNull = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Editor/icons/icon-world-null24.png")));

            // Initial update
            update();
        }

        private void update() {

            // Image or text
            if (map != null) {

                //
                Image image = map.paint(this);

                // Grab its image again and set my icon to it
                if (image != null) {

                    //
                    image = map.paint(this).getScaledInstance(getWidth() - 8, getHeight() - 8, Image.SCALE_SMOOTH);

                    //
                    if (ResourceProducer.challengeImage(image, this) == true) {
                        image = iconNull.getImage();
                    }

                    // Set the graphic as the image of the tile, not just a default image
                    setIcon(new ImageIcon(image));
                } else {
                    setIcon(iconNull);
                }
            } else {
                setIcon(iconNull);
                setText(map.getDisplayName());
            }

            // Repaint
            repaint();
        }

        @Override
        public void mapModified(MapEvent evt) {

            // update
            update();
        }

        public WorldCell getMap() {
            return map;
        }
    }
}
