/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.form;

import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import Editor.renderer.DataTreeRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class ResourceTree {

    // Variable Declaration
    // Java Native Classes
    private DefaultMutableTreeNode defaultRootNode;
    private DefaultTreeModel defaultTreeModel;
    // Swing Native Classes
    private JTree defaultTree;
    private Window window;
    // End of Variable Declaration

    //
    public ResourceTree(Window window) {

        //
        this.window = window;

        //
        init();
    }

    private void init() {

        // Creating a normal swing JTree.
        defaultTree = new JTree() {
            
            // This needs to be overriden so we can find files instead of text (Kind of a workaround I came up with)
            @Override
            public TreePath getNextMatch(String string, int row, Position.Bias bias) {

                //
                super.getNextMatch(string, row, bias);

                // Then do my stuff
                return customGetNextMatch(string, row, bias);
            }
        };

        // Solve for user double clicking.
        defaultTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeMouseClicked(evt);
            }
        });

        // Default stuffs
        defaultRootNode = new DefaultMutableTreeNode(new File("Root"));
        defaultTreeModel = new DefaultTreeModel(defaultRootNode);

        // Apply model and my custom renderer.
        defaultTree.setRowHeight(24);
        defaultTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        defaultTree.setModel(defaultTreeModel);
        defaultTree.setCellRenderer(new DataTreeRenderer());
    }

    public void addTreeItem(Object current) {

        // Create a new leaf for our file; but do not add it yet. First check to see if its parent exists in tree
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(current);
        DefaultMutableTreeNode parent = null;

        // Parent will only be non-null if we are dealing with FILES.
        if (current instanceof File) {

            //
            parent = getFileParentNode((File) current);
        }

        // Attempt to find a parent node was successful :D
        if (parent != null) {

            // I will find a way to keep duplicates from being added in first place :(
            if (!containsFileNode(parent, (File) current)) {

                // Maybe ?
                parent.add(node);
            }
        } else {

            //
            if (!containsDefaultNode(defaultRootNode, current)) {

                // Just add it then.
                defaultRootNode.add(node);
            }
        }

        //
        updateTree();
    }

    public void removeTreeItem(Object current) {

        // Casting
        final File file = (File) current;

        // Information required
        final DefaultMutableTreeNode node = getFileNode(file);

        //
        if (node != null) {
            
            //
            final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            
            // Dont allow removal of root node.
            if (node == defaultRootNode) {
                return;
            }

            // Grab the index of it
            final int index = parent.getIndex(node);

            // Remove it
            parent.remove(index);

            //
            updateTree();
        }
    }

    public void updateTree() {

        // Don't remove this line please.
        defaultTreeModel.setRoot(defaultRootNode);
        defaultTree.setModel(defaultTreeModel);
    }

    private DefaultMutableTreeNode getFileNode(File file) {

        // Enumerate over the entire collection
        for (Enumeration enumeration = defaultRootNode.breadthFirstEnumeration(); enumeration.hasMoreElements();) {

            // The current node
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            final File object = (File) node.getUserObject();

            // We dont take kindly to null objects.
            if (object != null) {

                // Grab from both objects
                final String first = file.getAbsolutePath().toUpperCase();
                final String second = object.getAbsolutePath().toUpperCase();

                // Quick check
                if (first.equals(second)) {

                    //
                    return node;
                }
            }
        }

        //
        return null;
    }

    private DefaultMutableTreeNode getFileParentNode(File file) {

        // Grab its actual parent file.
        final File parent = file.getParentFile();

        // Enumerate over the entire collection
        for (Enumeration enumeration = defaultRootNode.breadthFirstEnumeration(); enumeration.hasMoreElements();) {

            // The current node
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            final File object = (File) node.getUserObject();

            // We dont take kindly to null objects.
            if (parent != null && object != null) {

                // Grab from both objects
                final String first = object.getAbsolutePath().toUpperCase();
                final String second = parent.getAbsolutePath().toUpperCase();

                // Quick check
                if (first.equals(second)) {

                    //
                    return node;
                }
            }
        }

        //
        return null;
    }

    public DefaultMutableTreeNode getRoot() {
        return defaultRootNode;
    }

    public JTree getTree() {
        return defaultTree;
    }

    public void setRoot(DefaultMutableTreeNode node) {
        defaultRootNode = node;
        defaultTreeModel.setRoot(defaultRootNode);
        defaultTree.setModel(defaultTreeModel);
    }

    private boolean containsDefaultNode(DefaultMutableTreeNode parent, Object object) {

        //
        final String first = object.toString().toUpperCase();

        //
        for (Enumeration enumeration = parent.breadthFirstEnumeration(); enumeration.hasMoreElements();) {

            //
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

            //
            final Object current = (File) node.getUserObject();

            //
            final String second = current.toString().toUpperCase();

            //
            if (first.equals(second)) {
                return true;
            }
        }

        // Tree does not contain file by path (file)
        return false;
    }

    private boolean containsFileNode(DefaultMutableTreeNode parent, File file) {

        //
        final String first = file.getAbsolutePath().toUpperCase();

        //
        for (Enumeration enumeration = parent.breadthFirstEnumeration(); enumeration.hasMoreElements();) {

            //
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

            //
            final File current = (File) node.getUserObject();

            //
            final String second = current.getAbsolutePath().toUpperCase();

            //
            if (first.equals(second)) {
                return true;
            }
        }

        // Tree does not contain file by path (file)
        return false;
    }

    public TreePath customGetNextMatch(String filePath, int row, Position.Bias bias) {

        // Copied from JTree.java
        int max = defaultTree.getRowCount();

        //
        if (filePath == null) {
            throw new IllegalArgumentException();
        }
        if (row < 0 || row >= max) {
            throw new IllegalArgumentException();
        }

        //
        filePath = filePath.toUpperCase();

        // start search from the next/previous element froom the
        // selected element
        int increment = (bias == Position.Bias.Forward) ? 1 : -1;
        int newRow = row;

        // @dowhile
        do {

            //
            final TreePath path = defaultTree.getPathForRow(newRow);

            // This text should return a file.toString()
            final String text = defaultTree.convertValueToText(path.getLastPathComponent(), defaultTree.isRowSelected(newRow), defaultTree.isExpanded(newRow), true, newRow, false);

            // Checking
            if (filePath.equalsIgnoreCase(text)) {
                return path;
            }

            // Incrementing
            newRow = (newRow + increment + max) % max;
        } while (row != newRow);

        //
        return null;
    }

    private void treeMouseClicked(MouseEvent evt) {

        // TODO add your handling code here:
        final int count = evt.getClickCount();

        // So if you double click
        if (count == 2) {

            // Selected thing
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) defaultTree.getLastSelectedPathComponent();

            if (node == null) {
                return;
            }

            //
            final File file = (File) node.getUserObject();

            //
            if (file != null) {

                // 
                final ResourceViewer viewer = new ResourceViewer(window, file, true);
                viewer.setLocationRelativeTo(window);
                viewer.setVisible(true);
                viewer.dispose();
            }
        }
    }
}
