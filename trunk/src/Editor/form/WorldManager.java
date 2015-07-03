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

import core.event.MapEvent;
import core.event.MapListener;
import core.event.WorldEvent;
import core.event.WorldListener;
import core.world.Backdrop;
import core.world.World;
import core.world.WorldCellLayer;
import core.world.WorldCell;
import core.world.WorldObject;
import io.resource.DataPackage;
import io.resource.ResourceDelegate;
import io.resource.ResourceWriter;
import io.util.FileSearch;
import io.util.FileUtils;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import Editor.renderer.WorldTreeRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class WorldManager extends javax.swing.JPanel implements MapListener, WorldListener {

    // Variable Declaration
    // Java Native Classes
    private DefaultMutableTreeNode defaultMapNode;
    private DefaultMutableTreeNode defaultRootNode;
    private DefaultTreeModel defaultTreeModel;
    // Project Clases
    private World world;
    private ResourceDelegate delegate;
    // End of Variable Declaration

    public WorldManager(ResourceDelegate delegate) {

        //
        super();

        //
        this.delegate = delegate;

        //
        initComponents();
    }

    public void remove(Object object) {

        // Find the node containing the map
        final DefaultMutableTreeNode node = findNode(object);

        if (node != null) {

            // Find its parent
            final DefaultMutableTreeNode parent = findNodeParent(object);

            // Null check
            if (parent != null) {

                // Remove it
                parent.remove(node);

                // Reload the tree model
                defaultTreeModel.reload();
            }
        }
    }

    public void update(World world) {

        // First grab the world
        this.world = world;

        // Set value
        world.addWorldListener(this);

        // Define root Node
        defaultTreeModel = (DefaultTreeModel) worldJTree.getModel();
        defaultRootNode = new DefaultMutableTreeNode(world);

        // Predefined nodes
        defaultMapNode = new DefaultMutableTreeNode("Maps");

        // Grab the fMap list
        final ArrayList<WorldCell> mapList = world.getCellList();

        // Iterate over the list of maps
        for (int i = 0; i < mapList.size(); i++) {

            // Grab the current map from the iteration
            final WorldCell map = mapList.get(i);

            // Add fMap listener
            map.addMapListener(this);

            //
            final DefaultMutableTreeNode mapNode = new DefaultMutableTreeNode(map);

            // Grab Layers
            final ArrayList<WorldCellLayer> layerList = map.getLayerList();

            // Create a section for them
            final DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode("Layers");

            for (int j = 0; j < layerList.size(); j++) {

                // Grab from iteration
                final WorldCellLayer layer = layerList.get(j);

                //
                final DefaultMutableTreeNode contentNode = new DefaultMutableTreeNode(layer);

                // Add node
                layerNode.add(contentNode);

                // Add World Objects
                update(layer, contentNode);

                // Add to the map node to finish it off
                mapNode.add(layerNode);
            }

            // Grab Backgrounds
            final ArrayList<Backdrop> backgroundList = map.getBackgroundList();

            // Create a section for them
            final DefaultMutableTreeNode backgNode = new DefaultMutableTreeNode("Backgrounds");

            for (int j = 0; j < backgroundList.size(); j++) {

                // Grab from iteration
                final Backdrop backg = backgroundList.get(j);

                // Add node
                backgNode.add(new DefaultMutableTreeNode(backg));

                // Add to the map node to finish it off
                mapNode.add(backgNode);
            }

            // Add both Background and Layer nodes
            defaultMapNode.add(mapNode);

            // Add that map to the root
            defaultRootNode.add(defaultMapNode);
        }

        // Change the root node
        defaultTreeModel.setRoot(defaultRootNode);

        // Set Custom Tree Renderer
        worldJTree.setModel(defaultTreeModel);
        worldJTree.setCellRenderer(new WorldTreeRenderer());

        // Set most of this visible down to the world
    }

    private void update(WorldCellLayer layer, DefaultMutableTreeNode layerNode) {

        // Grab its inhabitants
        final ArrayList<WorldObject> objects = layer.getInhabitants();

        for (int i = 0; i < objects.size(); i++) {

            // Grab the world object
            final WorldObject object = objects.get(i);

            // Create a new node
            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(object);

            // Add to layer node
            layerNode.add(node);
        }
    }

    private DefaultMutableTreeNode findNodeParent(Object object) {

        // Search from root node
        final Enumeration breadth = defaultRootNode.breadthFirstEnumeration();

        while (breadth.hasMoreElements()) {

            //
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) breadth.nextElement();

            //
            final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

            // Parent must exist
            if (parent != null) {

                // Check
                if (object == node.getUserObject()) {

                    // Return the parent
                    return parent;
                }
            }
        }

        // Failed to find parent
        return null;
    }

    private DefaultMutableTreeNode findNode(Object object) {

        //
        final Enumeration breadth = defaultRootNode.breadthFirstEnumeration();

        //
        while (breadth.hasMoreElements()) {

            //
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) breadth.nextElement();

            //
            if (node.getUserObject() == object) {
                return node;
            }
        }

        //
        return null;
    }

    /**
     * This method is called from within the constructor to Start the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        queueJButton = new JButton();
        worldJScrollPane = new JScrollPane();
        worldJTree = new JTree();

        queueJButton.setText("Queue Changes");
        queueJButton.setEnabled(false);
        queueJButton.setMaximumSize(new Dimension(128, 26));
        queueJButton.setMinimumSize(new Dimension(128, 26));
        queueJButton.setPreferredSize(new Dimension(128, 26));
        queueJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                queueJButtonActionPerformed(evt);
            }
        });

        setMaximumSize(new Dimension(256, 440));
        setMinimumSize(new Dimension(256, 440));
        setPreferredSize(new Dimension(256, 440));

        worldJScrollPane.setMaximumSize(new Dimension(236, 385));
        worldJScrollPane.setMinimumSize(new Dimension(236, 385));
        worldJScrollPane.setPreferredSize(new Dimension(236, 385));

        DefaultMutableTreeNode treeNode1 = new DefaultMutableTreeNode("World");
        worldJTree.setModel(new DefaultTreeModel(treeNode1));
        worldJTree.setLargeModel(true);
        worldJTree.setMaximumSize(new Dimension(32767, 32767));
        worldJTree.setPreferredSize(new Dimension(234, 383));
        worldJTree.setRowHeight(24);
        worldJTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                worldJTreeMouseClicked(evt);
            }
        });
        worldJTree.addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(TreeExpansionEvent evt) {
                worldJTreeTreeExpanded(evt);
            }
            public void treeCollapsed(TreeExpansionEvent evt) {
                worldJTreeTreeCollapsed(evt);
            }
        });
        worldJTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent evt) {
                worldJTreeValueChanged(evt);
            }
        });
        worldJScrollPane.setViewportView(worldJTree);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(worldJScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(worldJScrollPane, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void worldJTreeValueChanged(TreeSelectionEvent evt) {//GEN-FIRST:event_worldJTreeValueChanged

        // Grabbing the selected Node.
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) worldJTree.getLastSelectedPathComponent();

        final TreePath[] treePaths = evt.getPaths();

        for (int i = 0; i < treePaths.length; i++) {
            if (evt.isAddedPath(i)) {
                selectedNode = (DefaultMutableTreeNode) treePaths[i].getLastPathComponent();
            }
        }
    }//GEN-LAST:event_worldJTreeValueChanged

    private void worldJTreeMouseClicked(MouseEvent evt) {//GEN-FIRST:event_worldJTreeMouseClicked

        // Tree path at mouse position
        final TreePath chosenPath = worldJTree.getPathForLocation(evt.getX(), evt.getY());

        // Set this path as selected
        worldJTree.setSelectionPath(chosenPath);

        // Constaly called vars
        final int clickCount = evt.getClickCount();
        final int eventButton = evt.getButton();

        // Can't be null or the Root Node
        if (chosenPath != null) {

            // Object
            final DefaultMutableTreeNode userNode = (DefaultMutableTreeNode) chosenPath.getLastPathComponent();

            // Determine which kind of object it is
            final Object userObject = userNode.getUserObject();

            // Do nothing
            if (userObject instanceof WorldObject) {

                // Set selected
                final WorldObject selected = (WorldObject) userObject;

                // Detect Left mouse click
                if (eventButton == MouseEvent.BUTTON1) {

                    // Single click
                    if (clickCount == 1) {
                        // Alternate selected
                    } else if (clickCount == 2) {
                        // Open its editor
                    }
                }
            } else if (userObject instanceof World) {

                // Outline entire world canvas
                final World selected = (World) userObject;

            } else if (userObject instanceof WorldCell) {

                // Select all layers
                final WorldCell selected = (WorldCell) userObject;

            } else if (userObject instanceof WorldCellLayer) {

                // Set all selected
                final WorldCellLayer selected = (WorldCellLayer) userObject;
            }
        }
    }//GEN-LAST:event_worldJTreeMouseClicked

    private void queueJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_queueJButtonActionPerformed

        // Grab dataPackage to determine data package
        final String packageID = world.getPackageId();

        // No data package?
        if (packageID == null || packageID.equalsIgnoreCase("null") || packageID.isEmpty()) {

            //
            FileSearch search = new FileSearch(new File(delegate.getDataDirectory()), world.getReferenceName(), true);

            //
            search.perform();

            // Search for it including file extension (Will find loose files and not files inside of data packages)
            final File found = search.check(world.getSHA1CheckSum());

            if (found != null) {
                if (found.exists()) {
                    try {

                        // Delete previous
                        FileUtils.eraseFile(found);
                    } catch (IOException ioe) {
                        //
                    }
                }
            }

            // Save the world to temp
            ResourceWriter.write(delegate, world);

            // Write all the maps as well
            final ArrayList<WorldCell> mapList = world.getCellList();

            //
            for (int i = 0; i < mapList.size(); i++) {

                // Grab the map
                final WorldCell map = mapList.get(i);

                // Write the map out
                ResourceWriter.write(delegate, map);
            }
        } else {

            // Find its data package
            final DataPackage pack = delegate.getPackage(ResourceDelegate.ID_EDITOR_REFERENCE, packageID);

            if (pack != null) {

                // Write the world out again, but to a temp
                final File file = ResourceWriter.write(delegate, world);

                // Replace the entry for the world with this updated one :)
                pack.replaceEntry(world.getReferenceID(), world, file);

                // Write all the maps as well
                final ArrayList<WorldCell> mapList = world.getCellList();

                for (int i = 0; i < mapList.size(); i++) {

                    // Grab the map
                    final WorldCell map = mapList.get(i);

                    // Write the map out to disk
                    final File mapFile = ResourceWriter.write(delegate, map);

                    // Replace the entry in the data package that it is tied to with this updated one :)
                    pack.replaceEntry(map.getReferenceID(), map, mapFile);
                }
            }
        }

        // Validate and set unmodified
        queueJButton.setEnabled(false);
    }//GEN-LAST:event_queueJButtonActionPerformed

    private void worldJTreeTreeExpanded(TreeExpansionEvent evt) {//GEN-FIRST:event_worldJTreeTreeExpanded

        // TODO add your handling code here:
        final Dimension size = worldJTree.getPreferredScrollableViewportSize();
        worldJTree.setPreferredSize(size);
        worldJTree.setMaximumSize(size);
        worldJTree.setMinimumSize(size);

        //
        worldJScrollPane.revalidate();
    }//GEN-LAST:event_worldJTreeTreeExpanded

    private void worldJTreeTreeCollapsed(TreeExpansionEvent evt) {//GEN-FIRST:event_worldJTreeTreeCollapsed

        // TODO add your handling code here:
        final Dimension size = worldJTree.getPreferredScrollableViewportSize();
        worldJTree.setPreferredSize(size);
        worldJTree.setMaximumSize(size);
        worldJTree.setMinimumSize(size);

        //
        worldJScrollPane.revalidate();
    }//GEN-LAST:event_worldJTreeTreeCollapsed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton queueJButton;
    private JScrollPane worldJScrollPane;
    private JTree worldJTree;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mapModified(MapEvent event) {

        // On the event that something was added to the map (FLayer, Background, WorldObject (inherited))
        if (event.getStateChange() == MapEvent.ADDED) {

            // Grab the object
            final Object object = event.getSource();

            // Still check
            if (object instanceof WorldCellLayer) {

                // Cast to layer
                final WorldCellLayer layer = (WorldCellLayer) object;

                // Find the map to add the layer to
                final DefaultMutableTreeNode node = findNodeParent(layer.getMap());

                // Create new node
                final DefaultMutableTreeNode contentNode = new DefaultMutableTreeNode(layer);

                // Add to the parent
                node.add(contentNode);

                //
                defaultTreeModel.reload();

                // Add its contents
                update(layer, contentNode);

                // Scroll to visible
                worldJTree.scrollPathToVisible(new TreePath(contentNode.getPath()));
            } else if (WorldObject.class.isAssignableFrom(object.getClass())) {

                // Cast to world object
                final WorldObject worldObject = (WorldObject) object;

                // Find the layer to add the world object too
                final DefaultMutableTreeNode contentNode = findNode(worldObject.getLayer());

                // Create new node
                final DefaultMutableTreeNode objectNode = new DefaultMutableTreeNode(worldObject);

                // Add to parent
                contentNode.add(objectNode);

                //
                defaultTreeModel.reload();

                // Scroll to visible path
                worldJTree.scrollPathToVisible(new TreePath(objectNode.getPath()));
            } else if (object instanceof Backdrop) {

                // Cast to layer
                final Backdrop background = (Backdrop) object;

                // Find the map to add the layer to
                final DefaultMutableTreeNode node = findNodeParent(background.getMap());

                // Create new node
                final DefaultMutableTreeNode backgroundNode = new DefaultMutableTreeNode(background);

                // Add to the parent
                node.add(backgroundNode);

                //
                defaultTreeModel.reload();

                // Scroll to visible
                worldJTree.scrollPathToVisible(new TreePath(backgroundNode.getPath()));
            }

            // We can now save changes because something has changed
            queueJButton.setEnabled(true);
        } else if (event.getStateChange() == MapEvent.REMOVED) {

            // Soon scroll to parent if exists.
            final DefaultMutableTreeNode parentNode = findNodeParent(event.getSource());

            // Simple remove method
            remove(event.getSource());

            // Scroll to visible
            worldJTree.scrollPathToVisible(new TreePath(parentNode.getPath()));
            worldJTree.expandPath(new TreePath(parentNode.getPath()));

            // We can now save changes
            queueJButton.setEnabled(true);
        } else if (event.getStateChange() == MapEvent.MODIFIED) {

            // Just enable
            queueJButton.setEnabled(true);
        }
    }

    @Override
    public void worldModified(WorldEvent event) {

        // On the event that something was added (FMap, FTransition)
        if (event.getStateChange() == WorldEvent.ADDED) {

            // Grab the object that changed
            final Object object = event.getSource();

            // Still check
            if (object instanceof WorldCell) {

                // Update the JTree to this world
                update(world);

                // We can now save changes
                queueJButton.setEnabled(true);
            }
        } else if (event.getStateChange() == WorldEvent.REMOVED) {

            // Grab the object that changed
            final Object object = event.getSource();

            // Still check
            if (object instanceof WorldCell) {

                // Remove it from its parent
                remove((WorldCell) object);

                // We can now save changes
                queueJButton.setEnabled(true);
            }
        }
    }
}
