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
import core.event.DelegateEvent;
import core.event.DelegateListener;
import core.world.Actor;
import core.world.Animation;
import core.world.Backdrop;
import core.world.World;
import core.world.WorldCell;
import core.world.WorldObject;
import core.world.Tileset;
import io.resource.DataRef;
import core.world.WorldResource;
import io.resource.ResourceDelegate;
import core.world.Picture;
import core.world.WorldItem;
import io.resource.DataPackage;
import core.world.WorldScript;
import core.world.item.Weapon;
import io.resource.ResourceRequest;
import io.util.TempUtils;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import Editor.renderer.ResourceTreeRenderer;
import Editor.transfer.NodeTransferHandler;

/**
 *
 * @author Robert A. Cherry (MessiahWolf)
 */
public class ResourceManager extends javax.swing.JPanel implements DelegateListener {

    // Variable Declaration
    // Java Native Classes
    private DefaultMutableTreeNode defaultActorNode;
    private DefaultMutableTreeNode defaultWeaponNode;
    private DefaultMutableTreeNode defaultAnimationNode;
    private DefaultMutableTreeNode defaultBackgroundNode;
    private DefaultMutableTreeNode defaultPackageNode;
    private DefaultMutableTreeNode defaultWorldCellNode;
    private DefaultMutableTreeNode defaultScriptNode;
    private DefaultMutableTreeNode defaultRootNode;
    private DefaultMutableTreeNode defaultWorldNode;
    private DefaultTreeModel defaultTreeModel;
    // Project Classes
    private FaustEditor editor;
    private ResourceDelegate delegate;
    // End of Variable Declaration

    public ResourceManager(FaustEditor editor, ResourceDelegate delegate) {

        // Call to super and begin Netbeans generated code
        super();
        initComponents();

        // Set values
        this.editor = editor;
        this.delegate = delegate;

        // Then start
        init();
    }

    private void init() {

        // Please note that this class does not communicate with the ->
        // resource delegate; the resource delegate communicates with this class.
        delegate.addDelegateListener(this);

        // Define Root Node and tree model
        defaultTreeModel = (DefaultTreeModel) defaultJTree.getModel();
        defaultRootNode = ((DefaultMutableTreeNode) defaultTreeModel.getRoot());

        // Categories to show
        defaultActorNode = new DefaultMutableTreeNode("Actors");
        defaultWeaponNode = new DefaultMutableTreeNode("Items");
        // Soon will add Map Object Types
        defaultAnimationNode = new DefaultMutableTreeNode("Animated Sprites");
        defaultBackgroundNode = new DefaultMutableTreeNode("Backgrounds");
        // Static Environments
        defaultWorldCellNode = new DefaultMutableTreeNode("World Cells");
        defaultWorldNode = new DefaultMutableTreeNode("Worlds");
        // Resources
        defaultPackageNode = new DefaultMutableTreeNode("Data Packages");
        //
        defaultScriptNode = new DefaultMutableTreeNode("World Object Scripts");
        // Soon to come a fMap node -- once its all put together

        // Add categories to tree model
        defaultRootNode.add(defaultActorNode);
        defaultRootNode.add(defaultWeaponNode);
        defaultRootNode.add(defaultAnimationNode);
        defaultRootNode.add(defaultBackgroundNode);
        defaultRootNode.add(defaultWorldCellNode);
        defaultRootNode.add(defaultPackageNode);
        defaultRootNode.add(defaultWorldNode);
        defaultRootNode.add(defaultScriptNode);

        // Set Custom Tree Renderer to allow icons to show
        defaultJTree.setCellRenderer(new ResourceTreeRenderer());
        defaultJTree.setTransferHandler(new NodeTransferHandler());

        // Expand the Resource Node
        defaultJTree.expandRow(0);

        // Enable Tooltips
        ToolTipManager.sharedInstance().registerComponent(defaultJTree);
    }

    public void addResource(DataRef reference) {

        // If the model does not already contain this resource@hashtag
        if (containsChild(defaultRootNode, reference) == false) {

            // Grab resource for quick reference
            final WorldResource resource = reference.getResource();

            // !DO NOT ADD GraphicsSet's or FImage's (That is what the GraphicsetManager.class is for.)
            if ((resource instanceof Tileset) == false && (resource instanceof Picture == false)) {

                // Create a node for it
                final DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(resource);

                // Find a family for it
                final DefaultMutableTreeNode familyNode = findNodeFamily(resource.getClass());

                // Insert into the tree model at that position
                defaultTreeModel.insertNodeInto(newNode, familyNode, familyNode.getChildCount());

                // Revalidate
                defaultJTree.revalidate();
            }
        }
    }

    public void removeResource(DataRef reference) {

        // Find its family
        final DefaultMutableTreeNode family = findNodeFamily(reference.getResource().getClass());

        if (family != null) {

            // Grab its enum
            final Enumeration breadth = family.breadthFirstEnumeration();

            // Search for it
            while (breadth.hasMoreElements()) {

                // Grab current
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) breadth.nextElement();

                // Cast
                if (node.getUserObject() instanceof WorldResource) {

                    // Interrogatethe resource
                    final WorldResource resource = (WorldResource) node.getUserObject();

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

    public void addPackage(DataRef reference) {

        // No duplicate entries; assured.
        if (containsChild(defaultRootNode, reference) == false) {

            // Grab the package for quick reference
            final DataPackage dataPackage = reference.getPackage();

            // Create a node for it
            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(dataPackage);

            // Find the DataPackage's family
            final DefaultMutableTreeNode family = findNodeFamily(DataPackage.class);

            // Grab all of its citations for quick looping
            final DataRef[] citations = dataPackage.getCitations();

            // Add all its citations
            for (int i = 0; i < citations.length; i++) {

                // Add as a leaf; will auto place into correct category
                addResource(citations[i]);
            }

            // Add the Package to the Family it belongs to
            defaultTreeModel.insertNodeInto(node, family, family.getChildCount());

            // Revalite date; kind of unnesscary
            defaultJTree.revalidate();
        }
    }

    public void removePackage(DataRef reference) {

        // Find its family
        final DefaultMutableTreeNode family = findNodeFamily(reference.getPackage().getClass());

        // Family must exist
        if (family != null) {

            // Grab its enum
            final Enumeration breadth = family.breadthFirstEnumeration();

            // Search for it
            while (breadth.hasMoreElements()) {

                // Grab current
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) breadth.nextElement();

                // Cast
                if (node.getUserObject() instanceof DataPackage) {

                    // Cast to data package for quick reference
                    final DataPackage dataPackage = (DataPackage) node.getUserObject();

                    // Ask
                    if (dataPackage.getReferenceId().equalsIgnoreCase(reference.getEditorId())) {

                        // Remove the node from the family
                        family.remove(node);

                        // Adjust the TreeModel
                        defaultTreeModel.reload();
                    }
                }
            }
        }
    }

    private boolean containsChild(DefaultMutableTreeNode parent, Object object) {

        // Grab all of the Nodes values
        final Enumeration children = parent.children();

        // Search the enumeration for the object
        while (children.hasMoreElements()) {

            // Grab the current for a quick reference
            final DefaultMutableTreeNode current = (DefaultMutableTreeNode) children.nextElement();

            // Ask
            if (current.getUserObject() == object) {
                return true;
            }
        }

        // Failure :(
        return false;
    }

    private DefaultMutableTreeNode findNodeFamily(Class closs) {

        // Switch over
        if (closs == Actor.class) {
            return defaultActorNode;
        } else if (closs == Animation.class) {
            return defaultAnimationNode;
        } else if (closs == Backdrop.class) {
            return defaultBackgroundNode;
        } else if (closs == WorldCell.class) {
            return defaultWorldCellNode;
        } else if (closs == DataPackage.class) {
            return defaultPackageNode;
        } else if (closs == World.class) {
            return defaultWorldNode;
        } else if (closs == WorldScript.class) {
            return defaultScriptNode;
        } else if (closs == Weapon.class) {
            return defaultWeaponNode;
        }

        // Return nothing
        return null;
    }

    public void revalidateTree() {

        // Removes dead nodes.
        for (int i = 0; i < defaultRootNode.getChildCount(); i++) {

            // Find the root from the root
            final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) defaultRootNode.getChildAt(i);

            // Leaves dont have children
            if (parentNode.isLeaf() == false) {

                // Iterate
                for (int j = 0; j < parentNode.getChildCount(); j++) {

                    // Find that child
                    final DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(j);

                    // The child must exist-- there should be a keyword called 'exists'
                    if (childNode == null) {

                        // Remove from the parent; I dont feel like checking if the parent contains it; so deal with it
                        parentNode.remove(childNode);
                    } else if (childNode.getUserObject() == null) {

                        // Driod Sans is the best programming font ever.
                        parentNode.remove(childNode);
                    }
                }
            }
        }

        // Reload the tree model and make sure that we can see the tree contents afterwards
        defaultTreeModel.reload();
        defaultJTree.makeVisible(defaultJTree.getLeadSelectionPath());
    }

    @Override
    public void referenceAdded(DelegateEvent evt) {

        // Cast to a reference
        final DataRef reference = (DataRef) evt.getSource();

        // Add the resource to the tree model
        addResource(reference);
    }

    @Override
    public void referenceRemoved(DelegateEvent evt) {

        // Cast to a reference
        final DataRef reference = (DataRef) evt.getSource();

        // Remove the resource from the tree model
        removeResource(reference);
    }

    @Override
    public void dataLoaded(DelegateEvent evt) {
        // Do nothing
    }

    @Override
    public void packageAdded(DelegateEvent evt) {

        // Could be any kind of FResource
        final DataRef reference = (DataRef) evt.getSource();

        // Same as adding a resource
        addPackage(reference);
    }

    @Override
    public void packageRemoved(DelegateEvent evt) {

        // Should only be a data package
        final DataRef reference = (DataRef) evt.getSource();

        // Remove the package
        removePackage(reference);
    }

    /**
     * Use this method when you load a new fMap Completely clears all lists and
     * starts fresh
     */
    public void clear() {

        // Removes all children from root Node
        for (int i = 0; i < defaultRootNode.getChildCount(); i++) {
            ((DefaultMutableTreeNode) defaultRootNode.getChildAt(i)).removeAllChildren();
        }
    }

    public JTree getTree() {
        return defaultJTree;
    }

    /**
     * This method is called from within the constructor to Start the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        resourceJScrollPane = new JScrollPane();
        defaultJTree = new JTree();

        setFocusable(false);
        setMaximumSize(new Dimension(256, 440));
        setMinimumSize(new Dimension(256, 440));
        setPreferredSize(new Dimension(256, 440));

        resourceJScrollPane.setMaximumSize(new Dimension(236, 385));
        resourceJScrollPane.setMinimumSize(new Dimension(236, 385));
        resourceJScrollPane.setPreferredSize(new Dimension(236, 385));

        DefaultMutableTreeNode treeNode1 = new DefaultMutableTreeNode("Resources");
        defaultJTree.setModel(new DefaultTreeModel(treeNode1));
        defaultJTree.setDragEnabled(true);
        defaultJTree.setLargeModel(true);
        defaultJTree.setMaximumSize(new Dimension(32767, 32767));
        defaultJTree.setPreferredSize(new Dimension(234, 388));
        defaultJTree.setRowHeight(24);
        defaultJTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                defaultJTreeMouseClicked(evt);
            }
        });
        resourceJScrollPane.setViewportView(defaultJTree);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resourceJScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(resourceJScrollPane, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void defaultJTreeMouseClicked(MouseEvent evt) {//GEN-FIRST:event_defaultJTreeMouseClicked

        // Tree path at mouse position
        final TreePath chosenPath = defaultJTree.getPathForLocation(evt.getX(), evt.getY());

        // Set this path as selected
        defaultJTree.setSelectionPath(chosenPath);

        // Constaly called vars
        final int clickCount = evt.getClickCount();
        final int eventButton = evt.getButton();

        // Can't be null or the Root Node
        if (chosenPath != null) {

            // Object
            final DefaultMutableTreeNode userNode = (DefaultMutableTreeNode) chosenPath.getLastPathComponent();
            final Object object = userNode.getUserObject();

            // Solving for...
            if (object instanceof WorldObject) {

                // Derived a fMap Object from selection
                final WorldObject worldObject = (WorldObject) object;

                // Double Left mouse click
                if (eventButton == MouseEvent.BUTTON1) {
                    if (clickCount == 2) {
                        // Check for Actor
                        if (worldObject instanceof Actor) {

                            // Just show it -- No need to add
                            final ActorEditor maker = new ActorEditor(editor, delegate, (Actor) worldObject, true);
                            maker.setLocationRelativeTo(editor);
                            maker.setVisible(true);

                            // Dispose of it
                            maker.dispose();
                        } else if (worldObject instanceof WorldItem) {

                            //
                            final ItemEditor maker = new ItemEditor(editor, delegate, (Weapon) worldObject, true);
                            maker.setLocationRelativeTo(editor);
                            maker.setVisible(true);

                            // Dispose of it
                            maker.dispose();
                        }

                    }
                } else if (eventButton == MouseEvent.BUTTON3) {
                    // Possibly show a PopupMenu
                }
            } else if (object instanceof Animation) {

                // Cast
                final Animation animation = (Animation) object;

                // Double Left mouse click
                if (eventButton == MouseEvent.BUTTON1) {
                    if (clickCount == 2) {

                        // Just show it -- No need to add
                        final IllustrationEditor maker = new IllustrationEditor(editor, delegate, animation, false);
                        maker.setLocationRelativeTo(editor);
                        maker.setVisible(true);
                    }
                }
            } else if (object instanceof Backdrop) {

                // Cast
                final Backdrop background = (Backdrop) object;

                // Double Left mouse click
                if (eventButton == MouseEvent.BUTTON1) {
                    if (clickCount == 2) {

                        // Show the editor
                        final IllustrationEditor maker = new IllustrationEditor(editor, delegate, background, true);
                        maker.setLocationRelativeTo(editor);
                        maker.setVisible(true);

                        // Dispose of it
                        maker.dispose();
                    }
                }
            } else if (object instanceof DataPackage) {

                // Cast
                final DataPackage pack = (DataPackage) object;

                // Double Left mouse click
                if (eventButton == MouseEvent.BUTTON1) {

                    // Double click detection
                    if (clickCount == 2) {

                        // Show the editor
                        final PackageEditor maker = new PackageEditor(editor, delegate, pack, true);
                        maker.setLocationRelativeTo(editor);
                        maker.setVisible(true);

                        // Dispose of it
                        maker.dispose();
                    }
                }
            } else if (object instanceof World) {

                // Cast
                final World fworld = (World) object;

                // Double Left mouse click
                if (eventButton == MouseEvent.BUTTON1) {

                    //
                    if (clickCount == 2) {

                        // Show the editor
                        final WorldEditor maker = new WorldEditor(editor, delegate, fworld, true);
                        maker.setLocationRelativeTo(editor);
                        maker.setVisible(true);

                        // Dispose of it
                        maker.dispose();
                    }
                }
            } else if (object instanceof WorldCell) {

                // Cast
                final WorldCell map = (WorldCell) object;

                // Double Left mouse click
                if (eventButton == MouseEvent.BUTTON1) {
                    if (clickCount == 2) {

                        // Show the map editor
                        final WorldCellEditor maker = new WorldCellEditor(editor, delegate, map, true);
                        maker.setLocationRelativeTo(editor);
                        maker.setVisible(true);

                        // Dispose of it
                        maker.dispose();
                    }
                }
            } else if (object instanceof WorldScript) {

                // Cast
                final WorldScript script = (WorldScript) object;

                //
                if (eventButton == MouseEvent.BUTTON1) {
                    if (clickCount == 2) {

                        //
                        final ScriptEditor maker = new ScriptEditor(editor, delegate, script, true);
                        maker.setLocationRelativeTo(editor);
                        maker.setVisible(true);

                        // Dispose of it
                        maker.dispose();
                    }
                }
            }
        }
    }//GEN-LAST:event_defaultJTreeMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTree defaultJTree;
    private JScrollPane resourceJScrollPane;
    // End of variables declaration//GEN-END:variables
}
