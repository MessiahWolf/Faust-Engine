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

import Editor.renderer.WorldTreeRenderer;
import core.event.RoomEvent;
import core.event.RoomListener;
import core.world.Backdrop;
import core.world.RoomLayer;
import core.world.Room;
import core.world.WorldObject;
import io.resource.ResourceDelegate;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Robert A. Cherry
 */
public class RoomManager extends javax.swing.JPanel implements RoomListener {

    // Variable Declaration
    // Java Native Classes
    private DefaultMutableTreeNode defaultRootNode;
    private DefaultTreeModel defaultTreeModel;
    // Project Clases
    private ResourceDelegate delegate;
    // End of Variable Declaration

    public RoomManager(ResourceDelegate delegate) {

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

    public void update(Room room) {

        //
        defaultRootNode = new DefaultMutableTreeNode(room);
        defaultTreeModel = new DefaultTreeModel(defaultRootNode);

        //
        for (RoomLayer layer : room.getLayerList()) {

            //
            final DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode(layer);

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

            //
            defaultRootNode.add(layerNode);
        }
        
        //
        defaultTreeModel.setRoot(defaultRootNode);
        
        worldJTree.setModel(defaultTreeModel);
        worldJTree.setCellRenderer(new WorldTreeRenderer());
        worldJTree.updateUI();
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
            public void treeCollapsed(TreeExpansionEvent evt) {
                worldJTreeTreeCollapsed(evt);
            }
            public void treeExpanded(TreeExpansionEvent evt) {
                worldJTreeTreeExpanded(evt);
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
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(worldJScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
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
            } else if (userObject instanceof Room) {

                // Select all layers
                final Room selected = (Room) userObject;

            } else if (userObject instanceof RoomLayer) {

                // Set all selected
                final RoomLayer selected = (RoomLayer) userObject;
                selected.getMap().setSelectedLayer(selected);
            }
        }
    }//GEN-LAST:event_worldJTreeMouseClicked

    private void queueJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_queueJButtonActionPerformed
//
//        // Grab dataPackage to determine data package
//        final String packageID = world.getPackageID();
//
//        // No data package?
//        if (packageID == null || packageID.equalsIgnoreCase("null") || packageID.isEmpty()) {
//
//            //
//            FileSearch search = new FileSearch(new File(delegate.getDataDirectory()), world.getReferenceName(), true);
//
//            //
//            search.perform();
//
//            // Search for it including file extension (Will find loose files and not files inside of data packages)
//            final File found = search.check(world.getSHA1CheckSum());
//
//            if (found != null) {
//                if (found.exists()) {
//                    try {
//
//                        // Delete previous
//                        FileUtils.eraseFile(found);
//                    } catch (IOException ioe) {
//                        //
//                    }
//                }
//            }
//
//            // Save the world to temp
//            ResourceWriter.write(delegate, world);
//
//            // Write all the maps as well
//            final ArrayList<WorldCell> mapList = world.getCellList();
//
//            //
//            for (int i = 0; i < mapList.size(); i++) {
//
//                // Grab the map
//                final Room map = mapList.get(i);
//
//                // Write the map out
//                ResourceWriter.write(delegate, map);
//            }
//        } else {
//
//            // Find its data package
//            final DataPackage pack = delegate.getPackage(ResourceDelegate.ID_EDITOR_REFERENCE, packageID);
//
//            if (pack != null) {
//
//                // Write the world out again, but to a temp
//                final File file = ResourceWriter.write(delegate, world);
//
//                // Replace the entry for the world with this updated one :)
//                pack.replaceEntry(world.getReferenceID(), world, file);
//
//                // Write all the maps as well
//                final ArrayList<WorldCell> mapList = world.getCellList();
//
//                for (int i = 0; i < mapList.size(); i++) {
//
//                    // Grab the map
//                    final Room map = mapList.get(i);
//
//                    // Write the map out to disk
//                    final File mapFile = ResourceWriter.write(delegate, map);
//
//                    // Replace the entry in the data package that it is tied to with this updated one :)
//                    pack.replaceEntry(map.getReferenceID(), map, mapFile);
//                }
//            }
//        }
//
//        // Validate and set unmodified
//        queueJButton.setEnabled(false);
    }//GEN-LAST:event_queueJButtonActionPerformed

    private void worldJTreeTreeExpanded(TreeExpansionEvent evt) {//GEN-FIRST:event_worldJTreeTreeExpanded

        // TODO addObject your handling code here:
        final Dimension size = worldJTree.getPreferredScrollableViewportSize();
        worldJTree.setPreferredSize(size);
        worldJTree.setMaximumSize(size);
        worldJTree.setMinimumSize(size);

        //
        worldJScrollPane.revalidate();
    }//GEN-LAST:event_worldJTreeTreeExpanded

    private void worldJTreeTreeCollapsed(TreeExpansionEvent evt) {//GEN-FIRST:event_worldJTreeTreeCollapsed

        // TODO addObject your handling code here:
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
    public void mapModified(RoomEvent event) {
        
        final Object object = event.getSource();
        
        //
        System.out.println("Map Modified: " + object);
        
        if (object instanceof RoomLayer) {
            final RoomLayer layer = (RoomLayer) object;
            
            update(layer.getMap());
        } else if (object instanceof Backdrop) {
            final Backdrop backdrop = (Backdrop) object;
            System.err.println("This one.");
            update(backdrop.getMap());
        }
         // On the event that something was added to the map (FLayer, Background, WorldObject (inherited))
        switch (event.getStateChange()) {
            case RoomEvent.ADDED:

                //
                System.err.println("Event recieved: " + event.getSource());
                // Grab the object
                // Still check
                if (object instanceof RoomLayer) {

                    // Cast to layer
                    final RoomLayer layer = (RoomLayer) object;

                    // Find the map to addObject the layer to
                    final DefaultMutableTreeNode node = findNodeParent(layer.getMap());

                    // Create new node
                    final DefaultMutableTreeNode contentNode = new DefaultMutableTreeNode(layer);

                    // Add to the parent
                    node.add(contentNode);

                    //

                    // Add its contents
                    update(layer.getMap());
                    defaultTreeModel.reload();

                    // Scroll to visible
                    worldJTree.scrollPathToVisible(new TreePath(contentNode.getPath()));
                } else if (WorldObject.class.isAssignableFrom(object.getClass())) {

                    // Cast to world object
                    final WorldObject worldObject = (WorldObject) object;

                    // Find the layer to addObject the world object too
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

                    // Find the map to addObject the layer to
                    final DefaultMutableTreeNode node = findNodeParent(background.getMap());

                    // Create new node
                    final DefaultMutableTreeNode backgroundNode = new DefaultMutableTreeNode(background);

                    // Add to the parent
                    node.add(backgroundNode);

                    //
                    defaultTreeModel.reload();

                    // Scroll to visible
                    worldJTree.scrollPathToVisible(new TreePath(backgroundNode.getPath()));
                }   // We can now save changes because something has changed
                queueJButton.setEnabled(true);
                break;
            case RoomEvent.REMOVED:
                // Soon scroll to parent if exists.
                final DefaultMutableTreeNode parentNode = findNodeParent(event.getSource());
                // Simple remove method
                remove(event.getSource());
                // Scroll to visible
                worldJTree.scrollPathToVisible(new TreePath(parentNode.getPath()));
                worldJTree.expandPath(new TreePath(parentNode.getPath()));
                // We can now save changes
                queueJButton.setEnabled(true);
                break;
            case RoomEvent.MODIFIED:
                // Just enable
                queueJButton.setEnabled(true);
                break;
            default:
                break;
        }
    }
}
