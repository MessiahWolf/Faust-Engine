/**
 * Copyright (c) 2013, Robert Cherry  *
 * All rights reserved.
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
import core.event.DelegateEvent;
import core.event.DelegateListener;
import core.world.Tileset;
import io.resource.DataRef;
import core.world.WorldResource;
import io.resource.ResourceDelegate;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import Editor.renderer.ResourceTreeRenderer;
import Editor.transfer.NodeTransferHandler;

/**
 *
 * @author Robert A. Cherry
 */
public class TilesetManager extends javax.swing.JPanel implements DelegateListener {

    // Variable Declaration
    // Swing Classes
    private FaustEditor editor;
    private ResourceDelegate delegate;
    private RoomCanvas worldCanvas;
    //
    private DefaultMutableTreeNode defaultRootNode;
    private DefaultMutableTreeNode defaultTilesetNode;
    private DefaultTreeModel defaultTreeModel;
    // End of Variable Declaration

    public TilesetManager(FaustEditor editor, ResourceDelegate delegate) {

        // Begin Generated GUI Code:
        initComponents();

        // Set Values
        this.editor = editor;
        this.delegate = delegate;

        // Then start
        init();
    }

    private void init() {

        // Please note that this class does not communicate with the
        // resource delegate; the resource delegate communicates with this class.
        delegate.addDelegateListener(this);

        // Define Root Node and tree model
        defaultTreeModel = (DefaultTreeModel) graphicJTree.getModel();
        defaultRootNode = ((DefaultMutableTreeNode) defaultTreeModel.getRoot());

        // Categories to show
        defaultTilesetNode = new DefaultMutableTreeNode("Tilesets");
        // Soon to come a fMap node -- once its all put together

        // Add categories to tree model
        defaultRootNode.add(defaultTilesetNode);

        // Set Custom Tree Renderer to allow icons to show
        graphicJTree.setCellRenderer(new ResourceTreeRenderer());
        graphicJTree.setTransferHandler(new NodeTransferHandler());

        //
        graphicJTree.expandRow(0);

        // Enable Tooltips
        ToolTipManager.sharedInstance().registerComponent(graphicJTree);
    }

    public void addResource(DataRef reference) {
        // If the model does not already contain this resource@hashtag
        if (containsChild(defaultRootNode, reference) == false) {

            // Create a node for it
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(reference.getResource());

            // Find a family for it
            DefaultMutableTreeNode familyNode = findNodeFamily(reference.getResource().getClass());

            // Insert into the tree model at that position
            defaultTreeModel.insertNodeInto(newNode, familyNode, familyNode.getChildCount());

            // Revalidate
            graphicJTree.revalidate();
        }
    }

    public void removeResource(DataRef reference) {

        // Find its family
        DefaultMutableTreeNode family = findNodeFamily(reference.getResource().getClass());

        if (family != null) {

            // Grab its enum
            Enumeration breadth = family.breadthFirstEnumeration();

            // Search for it
            while (breadth.hasMoreElements()) {

                // Grab current
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) breadth.nextElement();

                // Cast
                if (node.getUserObject() instanceof WorldResource) {

                    // Interrogatethe resource
                    WorldResource resource = (WorldResource) node.getUserObject();

                    if (resource.getReferenceID().equalsIgnoreCase(reference.getEditorId())) {

                        // Remove the node from the family
                        family.remove(node);

                        // Adjust the TreeModel
                        defaultTreeModel.reload();
                    }
                }
            }
        }
    }

    private boolean containsChild(DefaultMutableTreeNode parentNode, Object newObject) {

        // Grab all of the Nodes values
        Enumeration enumA = parentNode.children();

        // Search the enumeration for the object
        while (enumA.hasMoreElements()) {

            //
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) enumA.nextElement();

            //
            if (currentNode.getUserObject() == newObject) {
                return true;
            }
        }

        //
        return false;
    }

    private DefaultMutableTreeNode findNodeFamily(Class resourceClass) {

        //
        DefaultMutableTreeNode familyRootNode = null;

        if (resourceClass == Tileset.class) {
            familyRootNode = defaultTilesetNode;
        }

        // Return it
        return familyRootNode;
    }

    public void revalidateTree() {

        // Removes dead nodes.
        for (int i = 0; i < defaultRootNode.getChildCount(); i++) {
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) defaultRootNode.getChildAt(i);
            if (parentNode.isLeaf() == false) {
                for (int j = 0; j < parentNode.getChildCount(); j++) {
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(j);
                    if (childNode == null) {
                        parentNode.remove(childNode);
                    } else if (childNode.getUserObject() == null) {
                        parentNode.remove(childNode);
                    }
                }
            }
        }

        //
        graphicJTree.revalidate();
        graphicJTree.makeVisible(graphicJTree.getLeadSelectionPath());
    }

    @Override
    public void referenceAdded(DelegateEvent evt) {

        // Grab the resource from the event
        DataRef reference = (DataRef) evt.getSource();

        if (reference.getResource().getClass() == Tileset.class) {

            // Add the resource to the tree model
            addResource(reference);
        }
    }

    @Override
    public void referenceRemoved(DelegateEvent evt) {
        //
        DataRef reference = (DataRef) evt.getSource();

        //
        removeResource(reference);
    }

    @Override
    public void packageAdded(DelegateEvent evt) {
        // Add the plugins tileset content
    }

    @Override
    public void packageRemoved(DelegateEvent evt) {
        // Remove the plugins tileset content
    }

    public void clear() {

        // Removes all children from root Node
        for (int i = 0; i < defaultRootNode.getChildCount(); i++) {
            ((DefaultMutableTreeNode) defaultRootNode.getChildAt(i)).removeAllChildren();
        }
    }

    public JTree getTree() {
        return graphicJTree;
    }

    /**
     * This method is called from within the constructor to Start the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        roomJScrollPane = new JScrollPane();
        graphicJTree = new JTree();

        setFocusable(false);
        setMaximumSize(new Dimension(256, 440));
        setMinimumSize(new Dimension(256, 440));
        setPreferredSize(new Dimension(256, 440));

        roomJScrollPane.setMaximumSize(new Dimension(236, 385));
        roomJScrollPane.setMinimumSize(new Dimension(236, 385));
        roomJScrollPane.setPreferredSize(new Dimension(236, 385));

        DefaultMutableTreeNode treeNode1 = new DefaultMutableTreeNode("Resources");
        graphicJTree.setModel(new DefaultTreeModel(treeNode1));
        graphicJTree.setDragEnabled(true);
        graphicJTree.setMaximumSize(new Dimension(32767, 32767));
        graphicJTree.setPreferredSize(new Dimension(234, 383));
        graphicJTree.setRowHeight(24);
        graphicJTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                graphicJTreeMouseClicked(evt);
            }
        });
        graphicJTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent evt) {
                graphicJTreeValueChanged(evt);
            }
        });
        roomJScrollPane.setViewportView(graphicJTree);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roomJScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roomJScrollPane, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void graphicJTreeValueChanged(TreeSelectionEvent evt) {//GEN-FIRST:event_graphicJTreeValueChanged
        // May have a use for later
    }//GEN-LAST:event_graphicJTreeValueChanged

    private void graphicJTreeMouseClicked(MouseEvent evt) {//GEN-FIRST:event_graphicJTreeMouseClicked

        // Tree path at mouse position
        TreePath chosenPath = graphicJTree.getPathForLocation(evt.getX(), evt.getY());

        // Set this path as selected
        graphicJTree.setSelectionPath(chosenPath);

        // Constaly called vars
        final int clickCount = evt.getClickCount();
        final int eventButton = evt.getButton();

        // Can't be null or the Root Node
        if (chosenPath != null) {

            // Object
            final DefaultMutableTreeNode userNode = (DefaultMutableTreeNode) chosenPath.getLastPathComponent();
            final Object userObject = userNode.getUserObject();

            //
            if (userNode == defaultRootNode) {

                // Explore to Tileset folder
                File folder = new File(delegate.getTilesetDirectory());

                //
                try {

                    //
                    final Desktop desktop = Desktop.getDesktop();

                    //
                    desktop.open(folder);
                } catch (IOException ioe) {
                }
            }

            // Solving for...
            if (userObject instanceof Tileset) {

                // Derived a fMap Object from selection
                final Tileset tileset = (Tileset) userObject;

                if (eventButton == MouseEvent.BUTTON1) {
                    if (clickCount == 1) {
                        //
                        TileSelector tileSelector = editor.getTileSelector();

                        // First things first is to make sure the worldSplitPane has the tileSelector as its
                        // second component
                        JSplitPane pane = editor.getWorldSplitPane();
                        if (pane.getBottomComponent() == null) {
                            pane.setBottomComponent(tileSelector.getComponent());
                        }

                        // ----->
                        // Adjust the selector
                        tileSelector.setTileset(tileset);

                        //
                        editor.update();
                    } else if (clickCount == 2) {

                        // Double click to show graphicset editor
                        final IllustrationEditor maker = new IllustrationEditor(editor, delegate, tileset, true);
                        maker.setLocationRelativeTo(editor);
                        maker.setVisible(true);
                    }
                }
            }
        }
    }//GEN-LAST:event_graphicJTreeMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTree graphicJTree;
    private JScrollPane roomJScrollPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void dataLoaded(DelegateEvent evt) {
        //
    }
}
