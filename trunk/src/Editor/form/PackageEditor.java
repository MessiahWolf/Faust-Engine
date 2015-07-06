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

import io.GeneralFileFilter;
import io.resource.DataPackage;
import io.resource.ResourceDelegate;
import io.resource.ResourceReader;
import io.util.FileSearch;
import io.util.FileUtils;
import io.util.PackageUtils;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import Editor.listener.ManifestBinder;

/**
 *
 * @author Robert A. Cherry
 */
public class PackageEditor extends javax.swing.JDialog {

    // Variable Declaration
    // Swing Classes
    // Java Native Classes
    private BufferedImage image;
    private File packageCache;
    private File explorerCache;
    private ImageIcon informationDisabledIcon;
    private ImageIcon informationEnabledIcon;
    // Project Classes
    private ManifestBinder binder;
    private DelegateCheckBox box;
    private DataPackage pack;
    private ResourceDelegate delegate;
    private ResourceTree resourceTree;
    // End of Variable Declaration

    public PackageEditor(Window window, ResourceDelegate delegate, DataPackage pack, boolean modal) {

        //
        super(window);
        setModal(modal);
        initComponents();

        //
        this.delegate = delegate;
        this.pack = pack;

        //
        init();
    }

    private void init() {

        // My Wrapper class for the JTree to handle files and other resources.
        resourceTree = new ResourceTree(this);

        //
        resourceTree.getTree().addTreeSelectionListener(new TreeSelectionListener() {
            //
            @Override
            public void valueChanged(TreeSelectionEvent evt) {

                //
                final JTree tree = resourceTree.getTree();

                //
                final TreePath path = tree.getSelectionPath();

                // Path must exist.
                if (path != null) {

                    //
                    final DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();

                    //
                    if (selected != null) {

                        //
                        informationJButton.setIcon(informationEnabledIcon);
                    } else {

                        //
                        informationJButton.setIcon(informationDisabledIcon);
                    }
                }
            }
        });
        mainJScrollPane.setViewportView(resourceTree.getTree());

        // Our wrapper class for the delegate stuff.
        box = new DelegateCheckBox(delegate);
        buttonJPanel.add(box, 0);

        //
        final Class closs = getClass();

        //
        informationEnabledIcon = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-visible-on18.png");
        informationDisabledIcon = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-visible-off18.png");

        //
        informationJButton.setIcon(informationDisabledIcon);
        //informationJButton.setFocusPainted(false);
        //informationJButton.setContentAreaFilled(false);

        //
        //guideJButton.setIcon(ResourceReader.readClassPathIcon(getClass(), "/Editor/icons/icon-info16.png"));
        //guideJButton.setFocusPainted(false);
        //guideJButton.setContentAreaFilled(false);

        // This sets up the dialog to handle an existing package or a null one
        final boolean edit = setupPackage();

        // This sets up the JTextField's so they can communicate with the delegate.
        setupDialog();

        // Testing it out.
        binder = new ManifestBinder(delegate, pack);

        // Binding stuff manually.
        binder.bind(ManifestBinder.BOX_DELEGATE, box);
        binder.bind(ManifestBinder.FIELD_DISPLAY, displayJField);
        binder.bind(ManifestBinder.FIELD_REFERENCE, referenceJField);
        binder.bind(ManifestBinder.FIELD_NAME, nameJField);
        binder.bind(ManifestBinder.BUTTON_FINISH, finishJButton);
        binder.bind(ManifestBinder.BUTTON_GENERATE, generateJButton);

        // Always invoke before edit
        binder.invoke();
        binder.setEdit(edit);

        //
        if (edit) {

            //
            generateJButton.setEnabled(false);

            // Keeps JFields from being automatically resized to abstract widths and heights by layout manager. My quick fix.
            settingJPanel.setLayout(null);
        }

        // Default options for now.
        //manifestJCheckBox.setEnabled(false);
        manifestJCheckBox.setSelected(true);
        obscureJCheckBox.setSelected(true);

        //
        prefixJField.setText("__");

        //
        addWindowClosingEvents();
    }

    private void setRootFile(File file) {

        // So here we have our Root folder
        resourceTree.setRoot(new DefaultMutableTreeNode(file));

        // Adds all folders (No lie this method took me two weeks to get working. D: )
        addItem(file, true);

        // Now add all the files o-o; for now its working.
        addItem(file, false);

        // Update the tree from here
        resourceTree.updateTree();
    }

    private void addItem(File file, boolean dir) {

        // Base Case baby
        if (file == null) {
            return;
        }

        //
        if (file.isDirectory()) {

            // First list all of its files
            final File[] files = file.listFiles();

            // Solve for directories with no files
            if (files != null) {

                // Iterate over that list of files
                for (int i = 0; i < files.length; i++) {

                    // Grab current file
                    final File current = files[i];

                    // Dir means only take directories
                    if (current.isDirectory() && dir == false) {
                        continue;
                    }

                    // Add the item to the tree "our" way.
                    resourceTree.addTreeItem(current);

                    // Only add more of type.
                    addItem(current, dir);
                }
            }
        } else {

            // Just add the file then..
            resourceTree.addTreeItem(file);
        }
    }

    private void removeItem(File file) {

        // Just add the file then..
        resourceTree.removeTreeItem(file);
    }

    private void convertList(ArrayList<File> list) {

        // Grab the root from the resource tree
        final Enumeration enumeration = resourceTree.getRoot().breadthFirstEnumeration();

        // Add em all
        while (enumeration.hasMoreElements()) {

            //
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

            // Don't take root node.
            if (node == resourceTree.getRoot()) {
                continue;
            }

            // Cast to file for clarity
            final File file = (File) node.getUserObject();

            // Add to list.
            list.add(file);
        }
    }

    private void finish() {

        // If and only if
        if (box.isSelected()) {

            // Grab from Author Panel
            String referenceName = binder.getReferenceName();
            final String referenceID = binder.getReferenceID();
            final String displayName = binder.getDisplayName();

            // We're gonna need an extension at the end of the reference name as well
            referenceName = referenceName.concat(ResourceReader.MW_ARCHIVE_EXTENSION);

            // Save all changes to the dataPackage
            pack.setReferenceId(referenceID);
            pack.setReferenceName(referenceName);
            pack.setDisplayName(displayName);

            // Write the dataPackage out
            write();

            // Close this
            setVisible(false);
        }
    }

    private void write() {

        // Polymorphism.
        final ArrayList<File> list = new ArrayList<>();

        // Create a cache directory to store the contents in
        try {

            // Convert the root node to a list of files so the FileUtils can write them to acutal directories :D
            convertList(list);

            // Write the package to File System
            writePackage(list);

            //
            FileUtils.eraseContents(packageCache);
            FileUtils.eraseFile(packageCache);
        } catch (NullPointerException | IOException ioe) {
            //
            System.err.println("PackageEditor ERROR: " + ioe);
        }
    }

    private void writePackage(ArrayList<File> list) throws IOException {

        // This is an important piece.
        final File[] files = list.toArray(new File[]{});

        // This is the file in the temp folder with the weird name like "u98def1237551566aakdj"
        packageCache = FileUtils.makeDirectoryInside(new File(delegate.getCacheDirectory()), FileUtils.createUUID(pack.getReferenceName()));

        //
        final DefaultMutableTreeNode node = resourceTree.getRoot();
        final File root = (File) node.getUserObject();
        final String text = obscureJCheckBox.isSelected() ? prefixJField.getText() : "";

        // Make the root directory in the content directory
        packageCache = FileUtils.makeDirectory(new File(packageCache, root.getName()).getAbsolutePath());

        // Clip all the path names back to the name of the root directory (This method actually copies them as well)
        contractPath(root.getName().toUpperCase(), packageCache, files);

        // Prepare the package to be written (See source PackageUtils.preparePackage();)
        PackageUtils.preparePackage(files, packageCache, delegate, pack, new Object[]{manifestJCheckBox.isSelected(), text});

        // Actually write to file to the addon folder and delete content folder afterwords
        PackageUtils.writePackage(new File(delegate.getAddonDirectory()), packageCache, pack, false);

        // Now finally add to delegate here, not in PackageUtils
        delegate.addDataPackage(pack);
    }

    private void imageJButtonPaint(Graphics monet) {

        // Graphics object cast
        final Graphics2D manet = (Graphics2D) monet;

        // Draw the graphic if we have one
        if (image != null) {

            //
            final int buttonWidth = informationJButton.getPreferredSize().width;
            final int buttonHeight = informationJButton.getPreferredSize().height;

            // Scale it down
            final int imageWidth = image.getWidth();
            final int imageHeight = image.getHeight();
            //
            int width = imageWidth;
            int height = imageHeight;

            //
            if (imageWidth > buttonWidth - 2) {
                width = imageWidth - (imageWidth - (buttonWidth - 2));
            }

            // If the Image is bigger than the button our new size should be 2 pixels smaller than the button size
            if (imageHeight > buttonHeight - 2) {
                height = imageHeight - (imageHeight - (buttonHeight - 2));
            }

            //
            final int posx = (buttonWidth / 2 - width / 2);
            final int posy = (buttonHeight / 2 - height / 2);

            //
            manet.drawImage(((Image) image).getScaledInstance(width, height, Image.SCALE_SMOOTH), posx, posy, this);
        }
    }

    private void setupDialog() {

        // Adjust the name of the control
        if (pack.getDisplayName() == null) {
            setTitle("Data Importing");
        } else {
            setTitle("Edit: " + pack.getDisplayName());
        }
    }

    private boolean setupPackage() {

        // If pack is non-null find it on the harddrive and extract it using PackageUtils
        if (pack == null) {

            // New Package
            pack = new DataPackage(null, null, null, null, null, null, null);

            // Set the root node and Treemodel
            resourceTree.setRoot(new DefaultMutableTreeNode(new File("Root")));

            // Not editting an existing package :(
            return false;
        } else {

            // Since the data package exists we need to find the file on the harddrive.
            final String name = FileUtils.extend(pack.getReferenceName(), ResourceReader.MW_ARCHIVE_EXTENSION);

            // My FileSearch utility makes this step simple
            final FileSearch search = new FileSearch(new File(delegate.getAddonDirectory()), name, true);

            // Perform the search for the DataPackage's name plus our file extension appended.
            search.perform();

            // First result may be wrong :/
            final File packageFolder = new File(search.getFirstResult());

            // Extract it; delete after the add since the file on the harddrive should be an archive
            packageCache = PackageUtils.extract(delegate, packageFolder);

            // Find the root directory and set it as root node
            final File directory = getFirstDirectory(packageCache);

            // Use the content folder as the root.
            //resourceTree.setRoot(new DefaultMutableTreeNode(directory));
            setRootFile(directory);

            // Editting an existing package :D
            return true;
        }
    }

    /**
     * Takes a Directory's name and shortens all Files within the given array to
     * that directories path Ex. C:/Users/Name/Documents (directory)
     *
     * @param root The Name of the File to use as a cut-off point
     * @param directory The Directory to move the files to
     * @param files The Files to move
     * @throws IOException
     */
    public static void contractPath(String root, File directory, File[] files) throws IOException {

        // Solve for null arguments
        if (root == null || directory == null || files == null) {
            return;
        }

        // Consider each file in the array of Files.
        for (int i = 0; i < files.length; i++) {

            // Will be reassigned to do not declare final
            File file = files[i];

            // For now don't deal with hidden files.
            if (file.isFile()) {
                continue;
            }

            // Grab the path of the file
            final String path = file.getAbsolutePath();

            // Find where the paths intersect; which will be where (String) 'directory' occurs in (String) 'path'
            final int index = path.toUpperCase().indexOf(root.toUpperCase());

            // Grab substring from found 'index'
            final String substring = path.substring(index, path.length());

            // Adjust the path of the file a little
            file = new File(directory, substring);

            // Make the directory inside of its parent
            FileUtils.makeDirectoryInside(file.getParentFile(), file.getName());
        }

        // Now do the individual files for each directory
        for (int i = 0; i < files.length; i++) {

            //
            File file = files[i];

            // This time do not take directories
            if (file.isDirectory()) {
                continue;
            }

            // Path of the file
            final String path = file.getAbsolutePath();

            // Where they intersect
            final int index = path.toUpperCase().indexOf(root.toUpperCase());

            // Grab 'substring' again from found index
            final String substring = path.substring(index, path.length());

            // Our file
            file = new File(directory, substring);

            // Create a copy of the file inside the new directory
            FileUtils.makeFileInside(files[i], file.getParentFile());
        }
    }

    private File getFirstDirectory(File directory) {

        //
        if (directory == null) {
            return null;
        }

        //
        final File[] list = directory.listFiles();

        //
        if (list != null) {

            //
            for (int i = 0; i < list.length; i++) {

                //
                final File file = list[i];

                //
                if (file.isDirectory()) {
                    return file;
                }
            }
        }

        //
        return null;
    }

    private void addWindowClosingEvents() {

        //
        this.addWindowListener(new WindowAdapter() {
            //
            @Override
            public void windowClosed(WindowEvent evt) {

                //
                try {

                    // Erase them.
                    if (explorerCache != null) {

                        //
                        FileUtils.eraseContents(explorerCache);
                        FileUtils.eraseFile(explorerCache);
                    }

                    //
                    if (packageCache != null) {

                        //
                        FileUtils.eraseContents(packageCache);
                        FileUtils.eraseFile(packageCache);
                    }
                } catch (NullPointerException | IOException me) {
                    //
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainJTabbedPane = new javax.swing.JTabbedPane();
        optionJPanel = new javax.swing.JPanel();
        optionSubJPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        directoryJLabel = new javax.swing.JLabel();
        addJButton = new javax.swing.JButton();
        folderJButton = new javax.swing.JButton();
        manifestJCheckBox = new javax.swing.JCheckBox();
        singleFileJLabel = new javax.swing.JLabel();
        removeJButton = new javax.swing.JButton();
        prefixJField = new javax.swing.JTextField();
        removeItemJLabel = new javax.swing.JLabel();
        obscureJCheckBox = new javax.swing.JCheckBox();
        nullLabel1 = new javax.swing.JLabel();
        manifestJPanel = new javax.swing.JPanel();
        settingJPanel = new javax.swing.JPanel();
        locationJField = new javax.swing.JTextField();
        locationJLabel = new javax.swing.JLabel();
        pluginJLabel = new javax.swing.JLabel();
        pluginJField = new javax.swing.JTextField();
        referenceJLabel = new javax.swing.JLabel();
        nameJLabel = new javax.swing.JLabel();
        referenceJField = new javax.swing.JTextField();
        nameJField = new javax.swing.JTextField();
        displayJField = new javax.swing.JTextField();
        displayJLabel = new javax.swing.JLabel();
        nullLabel3 = new javax.swing.JLabel();
        generateJButton = new javax.swing.JButton();
        buttonJPanel = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        informationJButton = new javax.swing.JButton(){

            //
            @Override public void paintComponent(Graphics monet) {

                // Paint normal operation
                super.paintComponent(monet);

                // Paint my custom operations
                imageJButtonPaint(monet);
            }
        };
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        finishJButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        cancelJButton = new javax.swing.JButton();
        mainJScrollPane = new javax.swing.JScrollPane();
        nullLabel4 = new javax.swing.JLabel();
        exploreJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Image Importing");
        setMinimumSize(new java.awt.Dimension(554, 359));
        setResizable(false);

        mainJTabbedPane.setMaximumSize(new java.awt.Dimension(242, 281));
        mainJTabbedPane.setMinimumSize(new java.awt.Dimension(242, 281));
        mainJTabbedPane.setPreferredSize(new java.awt.Dimension(242, 281));

        optionJPanel.setMaximumSize(new java.awt.Dimension(220, 145));
        optionJPanel.setMinimumSize(new java.awt.Dimension(220, 145));
        optionJPanel.setPreferredSize(new java.awt.Dimension(260, 320));

        directoryJLabel.setText("Add Directory:");
        directoryJLabel.setMaximumSize(new java.awt.Dimension(64, 26));
        directoryJLabel.setMinimumSize(new java.awt.Dimension(88, 26));
        directoryJLabel.setPreferredSize(new java.awt.Dimension(88, 26));

        addJButton.setText("Add");
        addJButton.setMaximumSize(new java.awt.Dimension(104, 26));
        addJButton.setMinimumSize(new java.awt.Dimension(104, 26));
        addJButton.setPreferredSize(new java.awt.Dimension(104, 26));
        addJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addJButtonActionPerformed(evt);
            }
        });

        folderJButton.setText("Add");
        folderJButton.setMaximumSize(new java.awt.Dimension(104, 26));
        folderJButton.setMinimumSize(new java.awt.Dimension(104, 26));
        folderJButton.setPreferredSize(new java.awt.Dimension(104, 26));
        folderJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                folderJButtonActionPerformed(evt);
            }
        });

        manifestJCheckBox.setText("Include Manifest");
        manifestJCheckBox.setMargin(new java.awt.Insets(2, -2, 2, 2));
        manifestJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manifestJCheckBoxActionPerformed(evt);
            }
        });

        singleFileJLabel.setText("Add Single File:");
        singleFileJLabel.setMaximumSize(new java.awt.Dimension(64, 26));
        singleFileJLabel.setMinimumSize(new java.awt.Dimension(88, 26));
        singleFileJLabel.setPreferredSize(new java.awt.Dimension(88, 26));

        removeJButton.setText("Remove");
        removeJButton.setMaximumSize(new java.awt.Dimension(104, 26));
        removeJButton.setMinimumSize(new java.awt.Dimension(104, 26));
        removeJButton.setPreferredSize(new java.awt.Dimension(104, 26));
        removeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeJButtonActionPerformed(evt);
            }
        });

        prefixJField.setMaximumSize(new java.awt.Dimension(104, 22));
        prefixJField.setMinimumSize(new java.awt.Dimension(104, 22));
        prefixJField.setPreferredSize(new java.awt.Dimension(104, 22));

        removeItemJLabel.setText("Remove Item:");
        removeItemJLabel.setMaximumSize(new java.awt.Dimension(64, 26));
        removeItemJLabel.setMinimumSize(new java.awt.Dimension(88, 26));
        removeItemJLabel.setPreferredSize(new java.awt.Dimension(88, 26));

        obscureJCheckBox.setText("Prefix Files With:");
        obscureJCheckBox.setMargin(new java.awt.Insets(2, -2, 2, 2));
        obscureJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                obscureJCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(manifestJCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(singleFileJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(directoryJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(obscureJCheckBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeItemJLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(removeJButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prefixJField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(folderJButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addJButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(singleFileJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(folderJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(directoryJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeItemJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prefixJField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(obscureJCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(manifestJCheckBox))
        );

        nullLabel1.setText("Add or Remove Files from Tree View");
        nullLabel1.setEnabled(false);

        javax.swing.GroupLayout optionSubJPanelLayout = new javax.swing.GroupLayout(optionSubJPanel);
        optionSubJPanel.setLayout(optionSubJPanelLayout);
        optionSubJPanelLayout.setHorizontalGroup(
            optionSubJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(optionSubJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nullLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        optionSubJPanelLayout.setVerticalGroup(
            optionSubJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionSubJPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nullLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );

        javax.swing.GroupLayout optionJPanelLayout = new javax.swing.GroupLayout(optionJPanel);
        optionJPanel.setLayout(optionJPanelLayout);
        optionJPanelLayout.setHorizontalGroup(
            optionJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionJPanelLayout.createSequentialGroup()
                .addComponent(optionSubJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        optionJPanelLayout.setVerticalGroup(
            optionJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionJPanelLayout.createSequentialGroup()
                .addComponent(optionSubJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 44, Short.MAX_VALUE))
        );

        mainJTabbedPane.addTab("Import Settings", optionJPanel);

        settingJPanel.setMaximumSize(new java.awt.Dimension(213, 130));
        settingJPanel.setMinimumSize(new java.awt.Dimension(213, 130));
        settingJPanel.setPreferredSize(new java.awt.Dimension(213, 130));
        java.awt.GridBagLayout settingJPanelLayout1 = new java.awt.GridBagLayout();
        settingJPanelLayout1.columnWidths = new int[] {0, 5, 0};
        settingJPanelLayout1.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0};
        settingJPanel.setLayout(settingJPanelLayout1);

        locationJField.setColumns(20);
        locationJField.setEnabled(false);
        locationJField.setMaximumSize(new java.awt.Dimension(104, 22));
        locationJField.setMinimumSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(locationJField, gridBagConstraints);

        locationJLabel.setLabelFor(locationJField);
        locationJLabel.setText("File Location:");
        locationJLabel.setInheritsPopupMenu(false);
        locationJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        locationJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        locationJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(locationJLabel, gridBagConstraints);

        pluginJLabel.setLabelFor(pluginJField);
        pluginJLabel.setText("Part of Package:");
        pluginJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        pluginJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        pluginJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(pluginJLabel, gridBagConstraints);

        pluginJField.setColumns(20);
        pluginJField.setEnabled(false);
        pluginJField.setMaximumSize(new java.awt.Dimension(104, 22));
        pluginJField.setMinimumSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(pluginJField, gridBagConstraints);

        referenceJLabel.setLabelFor(referenceJField);
        referenceJLabel.setText("Reference ID:");
        referenceJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        referenceJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        referenceJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(referenceJLabel, gridBagConstraints);

        nameJLabel.setLabelFor(nameJField);
        nameJLabel.setText("Reference Name:");
        nameJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        nameJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        nameJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(nameJLabel, gridBagConstraints);

        referenceJField.setColumns(20);
        referenceJField.setToolTipText("");
        referenceJField.setMaximumSize(new java.awt.Dimension(104, 22));
        referenceJField.setMinimumSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(referenceJField, gridBagConstraints);
        referenceJField.getAccessibleContext().setAccessibleName("");

        nameJField.setColumns(20);
        nameJField.setMaximumSize(new java.awt.Dimension(104, 22));
        nameJField.setMinimumSize(new java.awt.Dimension(104, 22));
        nameJField.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                nameJFieldComponentResized(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(nameJField, gridBagConstraints);

        displayJField.setColumns(20);
        displayJField.setMaximumSize(new java.awt.Dimension(104, 22));
        displayJField.setMinimumSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(displayJField, gridBagConstraints);

        displayJLabel.setLabelFor(displayJField);
        displayJLabel.setText("Display Name:");
        displayJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        displayJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        displayJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(displayJLabel, gridBagConstraints);

        nullLabel3.setText("Edit information about this Archive");
        nullLabel3.setEnabled(false);

        generateJButton.setText("Generate ID's");
        generateJButton.setMaximumSize(new java.awt.Dimension(100, 26));
        generateJButton.setMinimumSize(new java.awt.Dimension(100, 26));
        generateJButton.setPreferredSize(new java.awt.Dimension(100, 26));
        generateJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout manifestJPanelLayout = new javax.swing.GroupLayout(manifestJPanel);
        manifestJPanel.setLayout(manifestJPanelLayout);
        manifestJPanelLayout.setHorizontalGroup(
            manifestJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manifestJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(manifestJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nullLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addGroup(manifestJPanelLayout.createSequentialGroup()
                        .addGroup(manifestJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(settingJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(generateJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        manifestJPanelLayout.setVerticalGroup(
            manifestJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, manifestJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nullLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(settingJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(generateJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mainJTabbedPane.addTab("Manifest Settings", manifestJPanel);

        buttonJPanel.setMaximumSize(new java.awt.Dimension(240, 26));
        buttonJPanel.setMinimumSize(new java.awt.Dimension(240, 26));
        buttonJPanel.setPreferredSize(new java.awt.Dimension(240, 26));
        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));
        buttonJPanel.add(filler3);

        informationJButton.setToolTipText("Shows File Information");
        informationJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        informationJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        informationJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        informationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                informationJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(informationJButton);
        buttonJPanel.add(filler2);

        finishJButton.setText("Finish");
        finishJButton.setToolTipText("All Images will be renamed");
        finishJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        finishJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        finishJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        finishJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(finishJButton);
        buttonJPanel.add(filler1);

        cancelJButton.setText("Cancel");
        cancelJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        cancelJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        cancelJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(cancelJButton);

        mainJScrollPane.setMaximumSize(new java.awt.Dimension(280, 280));
        mainJScrollPane.setMinimumSize(new java.awt.Dimension(280, 280));
        mainJScrollPane.setPreferredSize(new java.awt.Dimension(280, 280));

        nullLabel4.setText("Tree View (Double-click Tree Item to view in Internal Resource Viewer)");
        nullLabel4.setEnabled(false);

        exploreJButton.setText("Explore");
        exploreJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        exploreJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        exploreJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        exploreJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploreJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(mainJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(mainJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nullLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exploreJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(exploreJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nullLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 260, Short.MAX_VALUE)
                    .addComponent(mainJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed

        // Set Invisible
        dispose();
        setVisible(false);
    }//GEN-LAST:event_cancelJButtonActionPerformed

    private void finishJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishJButtonActionPerformed

        // Commit the changes to memory
        finish();
    }//GEN-LAST:event_finishJButtonActionPerformed

    private void informationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_informationJButtonActionPerformed

        // Grab current file from resourceTree
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) resourceTree.getTree().getLastSelectedPathComponent();

        //
        if (node != null) {

            //
            final Object object = node.getUserObject();

            //
            final ResourceViewer viewer = new ResourceViewer(this, object, true);
            viewer.setLocationRelativeTo(this);
            viewer.setVisible(true);
            viewer.dispose();
        }
    }//GEN-LAST:event_informationJButtonActionPerformed

    private void exploreJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploreJButtonActionPerformed

        // Attempt to write but dont Archive
        try {

            //
            if (packageCache.exists()) {

                // @todo later
                final Desktop desktop = Desktop.getDesktop();

                // jerst open it.
                desktop.open(packageCache);
            } else // Just make sure.
            if (explorerCache.exists()) {


                //
                final ArrayList<File> list = new ArrayList<>();

                // Conversion again.
                convertList(list);

                //
                final File[] files = list.toArray(new File[]{});

                //
                explorerCache = FileUtils.makeDirectoryInside(new File(delegate.getCacheDirectory()), delegate.createID(0, 10));

                // Actually copy the files to the folder
                FileUtils.copyTo(files, explorerCache);

                // @todo later
                final Desktop desktop = Desktop.getDesktop();

                // open it
                desktop.open(explorerCache);
            }
        } catch (IOException ioe) {
        }
    }//GEN-LAST:event_exploreJButtonActionPerformed

    private void generateJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateJButtonActionPerformed

        // Just to make sure.
        if (binder.isEditting() == false) {

            // Click this button to auto-generate all three forms of manifest-to-delegate identification
            binder.testButton();
        }
    }//GEN-LAST:event_generateJButtonActionPerformed

    private void nameJFieldComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_nameJFieldComponentResized

        //
        final Dimension dimension = new Dimension(104, 22);

        // Destroy the layout manager to cool the auto resizing "feature"
        settingJPanel.setLayout(null);

        // TODO add your handling code here:
        //nameJField.setSize(dimension);
        nameJLabel.setSize(dimension);

        //
        referenceJField.setSize(dimension);
        referenceJLabel.setSize(dimension);

        //
        displayJField.setSize(dimension);
        displayJLabel.setSize(dimension);

        //
        pluginJField.setSize(dimension);
        pluginJLabel.setSize(dimension);

        //
        locationJField.setSize(dimension);
        locationJLabel.setSize(dimension);
    }//GEN-LAST:event_nameJFieldComponentResized

    private void obscureJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_obscureJCheckBoxActionPerformed

        //
        prefixJField.setEnabled(obscureJCheckBox.isSelected());
    }//GEN-LAST:event_obscureJCheckBoxActionPerformed

    private void removeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeJButtonActionPerformed

        //
        final DefaultMutableTreeNode selected = (DefaultMutableTreeNode) resourceTree.getTree().getLastSelectedPathComponent();
        final File file = (File) selected.getUserObject();

        // TODO
        removeItem(file);

        // Repaint
        informationJButton.repaint();
    }//GEN-LAST:event_removeJButtonActionPerformed

    private void manifestJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manifestJCheckBoxActionPerformed

        //
        final boolean bool = manifestJCheckBox.isSelected();

        // Disable Manifest related buttons and Controls
        nameJField.setEnabled(bool);
        nameJLabel.setEnabled(bool);
        displayJField.setEnabled(bool);
        displayJLabel.setEnabled(bool);
        referenceJField.setEnabled(bool);
        referenceJLabel.setEnabled(bool);

        //
        if (binder.isEditting() == false) {

            //
            generateJButton.setEnabled(bool);
        }
    }//GEN-LAST:event_manifestJCheckBoxActionPerformed

    private void folderJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_folderJButtonActionPerformed

        // Open a file Menu
        final JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(delegate.getDataDirectory()));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show the dialog
        final int value = chooser.showOpenDialog(this);

        //
        if (value == JFileChooser.APPROVE_OPTION) {

            // Grab the file or directory
            final File file = chooser.getSelectedFile();

            // Ask
            if (file.isDirectory()) {

                try {

                    // Check for root
                    final DefaultMutableTreeNode root = resourceTree.getRoot();

                    //
                    final File rootFile = (File) root.getUserObject();

                    //
                    if (rootFile.getAbsolutePath().equals(new File("Root").getAbsolutePath())) {

                        // Root file can't be root because the system wont be able to find it. unless you have a drive labeled root.
                        setRootFile(file);
                    } else {

                        // Then just add the directory over then its files after.
                        addItem(file, true);
                        addItem(file, false);
                    }
                } catch (NullPointerException npe) {
                    System.err.println(npe);
                }
            }

            // Repaint
            informationJButton.repaint();
        }
    }//GEN-LAST:event_folderJButtonActionPerformed

    private void addJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addJButtonActionPerformed

        // Open a file Menu
        final JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(delegate.getDataDirectory()));
        chooser.setFileFilter(new GeneralFileFilter(new String[]{"png"}, "PNG"));
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        // Show the dialog
        final int value = chooser.showOpenDialog(this);

        //
        if (value == JFileChooser.APPROVE_OPTION) {

            // Grab the file or directory
            final File file = chooser.getSelectedFile();

            // Ask
            if (file.isFile()) {

                // Copy only the file over
                addItem(file, false);
            }

            // Repaint
            informationJButton.repaint();
        }
    }//GEN-LAST:event_addJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addJButton;
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JLabel directoryJLabel;
    private javax.swing.JTextField displayJField;
    private javax.swing.JLabel displayJLabel;
    private javax.swing.JButton exploreJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton finishJButton;
    private javax.swing.JButton folderJButton;
    private javax.swing.JButton generateJButton;
    private javax.swing.JButton informationJButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField locationJField;
    private javax.swing.JLabel locationJLabel;
    private javax.swing.JScrollPane mainJScrollPane;
    private javax.swing.JTabbedPane mainJTabbedPane;
    private javax.swing.JCheckBox manifestJCheckBox;
    private javax.swing.JPanel manifestJPanel;
    private javax.swing.JTextField nameJField;
    private javax.swing.JLabel nameJLabel;
    private javax.swing.JLabel nullLabel1;
    private javax.swing.JLabel nullLabel3;
    private javax.swing.JLabel nullLabel4;
    private javax.swing.JCheckBox obscureJCheckBox;
    private javax.swing.JPanel optionJPanel;
    private javax.swing.JPanel optionSubJPanel;
    private javax.swing.JTextField pluginJField;
    private javax.swing.JLabel pluginJLabel;
    private javax.swing.JTextField prefixJField;
    private javax.swing.JTextField referenceJField;
    private javax.swing.JLabel referenceJLabel;
    private javax.swing.JLabel removeItemJLabel;
    private javax.swing.JButton removeJButton;
    private javax.swing.JPanel settingJPanel;
    private javax.swing.JLabel singleFileJLabel;
    // End of variables declaration//GEN-END:variables
}
