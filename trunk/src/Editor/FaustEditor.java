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
package Editor;

import Editor.form.ApplicationSettingsEditor;
import Editor.form.DataLoader;
import Editor.form.FileToolbar;
import Editor.form.ResourceManager;
import Editor.form.StatusBar;
import Editor.form.TileSelector;
import Editor.form.TilesetManager;
import Editor.form.UtilityToolbar;
import Editor.form.WorkspaceTabComponent;
import Editor.form.WorldCanvas;
import Editor.form.WorldCellSelector;
import Editor.form.WorldManager;
import core.world.World;
import io.resource.ResourceDelegate;
import io.resource.ResourceProducer;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import Editor.tools.Grid;

/**
 *
 * @author Robert A. Cherry (MessiahWolf)
 */
public class FaustEditor extends JFrame {

    // Variable Declaration
    private boolean generateEditorIds;
    private boolean loadPackages;
    private boolean loadImages;
    private int scanMode;
    private String dataDirectory = "NULL";
    // Swing Classes
    private JTabbedPane mainTabbedPane;
    private JTabbedPane workspacePane;
    private JScrollPane renderPane;
    private JSplitPane resourceSplitPane;
    private JSplitPane worldSplitPane;
    // Project Classes
    private FileToolbar fileSelector;
    private UtilityToolbar toolSelector;
    private ResourceDelegate delegate;
    private ResourceManager resourceManager;
    private StatusBar statusBar;
    private WorldCellSelector mapSelector;
    private TileSelector tileSelector;
    private WorldCanvas worldCanvas;
    private WorldManager worldManager;
    private TilesetManager tilesetManager;
    // End of Variable Declaration

    public static void main(String[] args) {

        // Initialize the editor.
        final FaustEditor faustEditor = new FaustEditor();

        // Now set it visible
        faustEditor.setVisible(true);
    }

    public FaustEditor() {

        // JFrame Constructor for settings the title.
        super("Faust Map Editor");

        // Attempt to set the look and feel of the application
        try {

            // Set to native look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException cnfe) {
            System.err.println(cnfe);
        }

        // Load the property File
        loadPropertyFile();

        // Initialize GUI
        initComponents();

        // Custom Initialization
        initCustomComponents();
    }

    private void initCustomComponents() {

        // Set to Border Layout
        setLayout(new BorderLayout());

        // Grab resource from data folder  -- ! really important that this class must be up top
        delegate = new ResourceDelegate();

        // Preserve Inst. Order
        tileSelector = new TileSelector();
        mapSelector = new WorldCellSelector(this, delegate);

        // Preserve Inst. Order
        worldCanvas = new WorldCanvas(this, delegate, mapSelector, null);
        fileSelector = new FileToolbar(this, delegate, worldCanvas);

        //
        worldCanvas.connect(fileSelector);

        // Preserve Inst. Order
        toolSelector = new UtilityToolbar(this, delegate);

        // Managers
        resourceManager = new ResourceManager(this, delegate);
        tilesetManager = new TilesetManager(this, tileSelector, delegate);
        worldManager = new WorldManager(delegate);

        //
        statusBar = new StatusBar(this, delegate);

        // Now scan for resources after we register the listeners
        delegate.initialize(dataDirectory, scanMode, loadPackages, generateEditorIds, loadImages);

        // Create the JScrollPane
        final Dimension renderDimension = new Dimension(500, 300);
        renderPane = new JScrollPane();
        renderPane.setPreferredSize(renderDimension);
        renderPane.setMaximumSize(renderDimension);
        renderPane.setMinimumSize(renderDimension);
        mainTabbedPane = new JTabbedPane();
        workspacePane = new JTabbedPane();
        workspacePane.setPreferredSize(renderDimension);
        workspacePane.setMaximumSize(renderDimension);
        workspacePane.setMinimumSize(renderDimension);
        mainTabbedPane.setPreferredSize(new Dimension(264, 540));
        mainTabbedPane.setMaximumSize(new Dimension(264, 540));

        //
        final Dimension editorDimension = new Dimension(814, 602);
        setPreferredSize(editorDimension);
        setMaximumSize(editorDimension);
        setMinimumSize(editorDimension);
        setSize(editorDimension);

        //
        renderPane.setViewportView(worldCanvas);
        tilesetManager.setWorldCanvas(worldCanvas);

        //
        resourceSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, mainTabbedPane);
        worldSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, renderPane, null);
        worldSplitPane.setOneTouchExpandable(false);
        worldSplitPane.setEnabled(false);

        // Begin to add them to This Frame
        mainTabbedPane.addTab("Resource Palette", resourceManager);
        mainTabbedPane.addTab("Tileset Palette", tilesetManager);
        mainTabbedPane.addTab("World View", worldManager);

        //
        workspacePane.addTab("0", worldSplitPane);
        workspacePane.setTabComponentAt(0, new WorkspaceTabComponent(workspacePane, "Workspace 1"));

        //
        final JPanel buttonJPanel = new JPanel();
        buttonJPanel.setLayout(new BoxLayout(buttonJPanel, BoxLayout.LINE_AXIS));
        buttonJPanel.setPreferredSize(new Dimension(814, 32));
        buttonJPanel.setMaximumSize(new Dimension(814, 32));
        buttonJPanel.add(fileSelector);
        buttonJPanel.add(toolSelector);

        // Default Disabled
        worldManager.setEnabled(false);

        // Piecing together
        add(buttonJPanel, BorderLayout.NORTH);
        add(resourceSplitPane, BorderLayout.EAST);
        add(workspacePane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Create property file at window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                // Save the property file
                createPropertyFile();
            }
        });
        addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent event) {

                // Only if your OS supports this kind of maximization
                if (Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {

                    // Just when both directions are maximized
                    if (event.getNewState() == Frame.MAXIMIZED_BOTH) {

                        // World split pane must exist
                        if (worldSplitPane != null) {

                            // The resize weight will allow the component to maintain reasonable size after maximization.
                            worldSplitPane.setResizeWeight(1);
                            worldSplitPane.setDividerLocation(.88f);
                        }
                    }
                }
            }
        });

        // After we have added the components, pack the JFrame and reposition it
        pack();
        repositionCenter();

        // Some commands to change the JFrames image
        initFrameImage();
    }

    private void initFrameImage() {

        // Grab some ImageIcons
        final Class closs = getClass();
        final Toolkit kit = Toolkit.getDefaultToolkit();

        // Change the icon for the application
        final ArrayList<Image> list = new ArrayList<>();
        list.add(kit.getImage(closs.getResource("/stock/stock-frame16.png")));
        list.add(kit.getImage(closs.getResource("/stock/stock-frame32.png")));

        // Give it both the 16x16 and 32x32
        setIconImages(list);
    }

    public void update(World world) {

        // fMap Canvas listens to fMap: Avoid loop
        if (worldCanvas.getWorld() != world) {
            worldCanvas.setWorld(world);
        }

        // Revalidate the worldCanvas to show the new map associated
        worldCanvas.revalidate();

        // Adjusting the ScrollPane to hold the fMap
        renderPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        renderPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        renderPane.setViewportView(worldCanvas);
        renderPane.revalidate();

        // Give the graphic manager a world canvas
        tilesetManager.setWorldCanvas(worldCanvas);

        // Default Disabled
        worldManager.update(world);
        worldManager.setEnabled(true);
    }

    private void repositionCenter() {

        // Set to correct position on screen
        final Dimension screen = getToolkit().getScreenSize();

        // Set the window to the middle of the screen
        final int x = ((screen.width / 2 - getSize().width / 2));
        final int y = ((screen.height / 2 - getSize().height / 2));

        // Move the window
        setLocation(new Point(x, y));
    }

    public void open() {

        // Show the data loader
        final DataLoader loader = new DataLoader(this, delegate, true);
        loader.setLocationRelativeTo(this);
        loader.setVisible(true);

        //
        loader.dispose();
    }

    public void save() {

        // Validate all temporary files
        delegate.validateTemporary();

        // Will save all worlds
        delegate.validateWorld(worldCanvas.getWorld());

        // Save all the plugins; the plugins keep a record of all the changes
        delegate.validatePackages();
    }

    public void takeScreenshot() {

        try {

            // Create a new Robot
            final Robot robot = new Robot();

            // Capture the Image
            final BufferedImage image = robot.createScreenCapture(getBounds());

            //
            final String name = delegate.createID(0, 12);

            // Write the image out
            ResourceProducer.writeImage(new File(delegate.getDataDirectory()), name, image);

            //
            JOptionPane.showMessageDialog(this, "Screen-shot " + name + " saved.");
        } catch (AWTException awe) {
            //
        }
    }

    private void loadPropertyFile() {

        // Attmept to find the property file
        final Properties propertyFile = new Properties();

        try {

            // Load the property file
            propertyFile.load(new FileInputStream("config.properties"));

            // Attempt to grab the data property
            dataDirectory = propertyFile.getProperty("sData");

            // Grab the scan mode
            scanMode = Integer.parseInt(propertyFile.getProperty("bScanMode"));

            //
            loadPackages = Boolean.parseBoolean(propertyFile.getProperty("bLoadPackages"));
            loadImages = Boolean.parseBoolean(propertyFile.getProperty("bLoadImages"));

            //
            generateEditorIds = Boolean.parseBoolean(propertyFile.getProperty("bGenerateEditorIds"));

            //
            if (dataDirectory.equals("NULL")) {
                findDataDirectory();
            }
        } catch (IOException fnfe) {
            createPropertyFile();
        }
    }

    private void findDataDirectory() {

        final JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Locate the Faust Data Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(new File(""));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            // Try with this
            dataDirectory = chooser.getSelectedFile().getAbsolutePath();
        }
    }

    private void createPropertyFile() {

        try {

            // Property File :) example time
            final Properties propertyFile = new Properties();

            // Strings
            propertyFile.setProperty("\\Strings", "");
            propertyFile.setProperty("sData", dataDirectory);

            // Booleans
            propertyFile.setProperty("\\Booleans", "");
            propertyFile.setProperty("bScanMode", String.valueOf(delegate.getScanMode()));
            propertyFile.setProperty("bGenerateEditorIds", String.valueOf(delegate.isGeneratingEditorIds()));
            propertyFile.setProperty("bLoadPackages", String.valueOf(delegate.isLoadingPackages()));
            propertyFile.setProperty("bLoadImages", String.valueOf(delegate.isLoadingImages()));

            // Save all used plugins for load order next time -- will severely slow load time; but ask
            propertyFile.store(new FileOutputStream("config.properties"), null);
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    public boolean acceptsDataPackages() {
        return loadPackages;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public WorldCanvas getCanvas() {
        return worldCanvas;
    }

    public JSplitPane getResourceSplitPane() {
        return resourceSplitPane;
    }

    public JSplitPane getWorldSplitPane() {
        return worldSplitPane;
    }

    /**
     * This method is called from within the constructor to Start the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileMenuBar = new javax.swing.JMenuBar();
        fileJMenu = new javax.swing.JMenu();
        dataJMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        saveJMenuItem = new javax.swing.JMenuItem();
        saveWorldJMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        exitJMenuItem = new javax.swing.JMenuItem();
        editJMenu = new javax.swing.JMenu();
        optionJMenuItem = new javax.swing.JMenuItem();
        viewJMenu = new javax.swing.JMenu();
        visibleActorJCheckBox = new javax.swing.JCheckBoxMenuItem();
        visibleTileJCheckBox = new javax.swing.JCheckBoxMenuItem();
        visibleEffectJCheckBox = new javax.swing.JCheckBoxMenuItem();
        visibleBackgroundJCheckBox = new javax.swing.JCheckBoxMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        gridJCheckBox = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Faust Map Editor");
        setMinimumSize(new java.awt.Dimension(360, 188));
        setName("mainFrame"); // NOI18N

        fileMenuBar.setMaximumSize(new java.awt.Dimension(425, 32));
        fileMenuBar.setMinimumSize(new java.awt.Dimension(425, 32));

        fileJMenu.setText("File");

        dataJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        dataJMenuItem.setText("Data");
        dataJMenuItem.setToolTipText("Create a new plugin");
        dataJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(dataJMenuItem);
        fileJMenu.add(jSeparator2);

        saveJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveJMenuItem.setText("Save");
        saveJMenuItem.setToolTipText("Save changes made to all plugins");
        saveJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(saveJMenuItem);

        saveWorldJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveWorldJMenuItem.setText("Save All Worlds");
        saveWorldJMenuItem.setToolTipText("Save changes made to all plugins");
        saveWorldJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveWorldJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(saveWorldJMenuItem);
        fileJMenu.add(jSeparator3);

        exitJMenuItem.setText("Exit");
        exitJMenuItem.setToolTipText("Exit the Faust editor");
        exitJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(exitJMenuItem);

        fileMenuBar.add(fileJMenu);

        editJMenu.setText("Edit");

        optionJMenuItem.setText("Options");
        optionJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionJMenuItemActionPerformed(evt);
            }
        });
        editJMenu.add(optionJMenuItem);

        fileMenuBar.add(editJMenu);

        viewJMenu.setText("View");

        visibleActorJCheckBox.setSelected(true);
        visibleActorJCheckBox.setText("Show Actors");
        visibleActorJCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                visibleActorJCheckBoxItemStateChanged(evt);
            }
        });
        viewJMenu.add(visibleActorJCheckBox);

        visibleTileJCheckBox.setSelected(true);
        visibleTileJCheckBox.setText("Show Tiles");
        visibleTileJCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                visibleTileJCheckBoxItemStateChanged(evt);
            }
        });
        viewJMenu.add(visibleTileJCheckBox);

        visibleEffectJCheckBox.setSelected(true);
        visibleEffectJCheckBox.setText("Show Effects");
        visibleEffectJCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                visibleEffectJCheckBoxItemStateChanged(evt);
            }
        });
        viewJMenu.add(visibleEffectJCheckBox);

        visibleBackgroundJCheckBox.setSelected(true);
        visibleBackgroundJCheckBox.setText("Show Backgrounds");
        visibleBackgroundJCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                visibleBackgroundJCheckBoxItemStateChanged(evt);
            }
        });
        viewJMenu.add(visibleBackgroundJCheckBox);
        viewJMenu.add(jSeparator5);

        gridJCheckBox.setSelected(true);
        gridJCheckBox.setText("Show Grid");
        gridJCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                gridJCheckBoxItemStateChanged(evt);
            }
        });
        viewJMenu.add(gridJCheckBox);

        fileMenuBar.add(viewJMenu);

        setJMenuBar(fileMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 360, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 191, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveJMenuItemActionPerformed

        // Save all changes made to all active plugins, now
        save();
    }//GEN-LAST:event_saveJMenuItemActionPerformed

    private void dataJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataJMenuItemActionPerformed

        // Depends
        if (delegate.getScanMode() == ResourceDelegate.SCAN_BIASED) {

            // Show the dataPackage editor
            final DataLoader maker = new DataLoader(this, delegate, true);
            maker.setLocationRelativeTo(this);
            maker.setVisible(true);

            //
            maker.dispose();
        } else if (delegate.getScanMode() == ResourceDelegate.SCAN_UNBIASED) {

            // Add all files unbiased
            delegate.performScan(dataDirectory, true, loadPackages);
            delegate.validate();
        }
    }//GEN-LAST:event_dataJMenuItemActionPerformed

    private void exitJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitJMenuItemActionPerformed

        // Dispose of this JFrame
        dispose();

        // System exit
        System.exit(0);
    }//GEN-LAST:event_exitJMenuItemActionPerformed

    private void visibleActorJCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_visibleActorJCheckBoxItemStateChanged
    }//GEN-LAST:event_visibleActorJCheckBoxItemStateChanged

    private void visibleTileJCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_visibleTileJCheckBoxItemStateChanged
        // Base Cas
    }//GEN-LAST:event_visibleTileJCheckBoxItemStateChanged

    private void visibleEffectJCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_visibleEffectJCheckBoxItemStateChanged
    }//GEN-LAST:event_visibleEffectJCheckBoxItemStateChanged

    private void visibleBackgroundJCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_visibleBackgroundJCheckBoxItemStateChanged
    }//GEN-LAST:event_visibleBackgroundJCheckBoxItemStateChanged

    private void gridJCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_gridJCheckBoxItemStateChanged

        // World Canvas must exist
        if (worldCanvas != null) {

            // Quickly grab the grid as reference
            final Grid grid = worldCanvas.getGrid();

            // World Canvas's Grid must exist
            if (grid != null) {

                // Enable of Disable the grid
                boolean state = grid.isVisible();

                // Change the state of the grid's visibility
                grid.setVisible(!state);

                // Repaint
                worldCanvas.repaint();
            }
        }
    }//GEN-LAST:event_gridJCheckBoxItemStateChanged

    private void optionJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionJMenuItemActionPerformed

        // Show the application settings editor
        final ApplicationSettingsEditor maker = new ApplicationSettingsEditor(this, delegate, true);
        maker.setLocationRelativeTo(this);
        maker.setVisible(true);

        //
        ///maker.dispose();
    }//GEN-LAST:event_optionJMenuItemActionPerformed

    private void saveWorldJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveWorldJMenuItemActionPerformed

        // Saves every world it can find; this also resaves all the world cells associated with the world instance.
        delegate.validateWorlds();
    }//GEN-LAST:event_saveWorldJMenuItemActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem dataJMenuItem;
    private javax.swing.JMenu editJMenu;
    private javax.swing.JMenuItem exitJMenuItem;
    private javax.swing.JMenu fileJMenu;
    private javax.swing.JMenuBar fileMenuBar;
    private javax.swing.JCheckBoxMenuItem gridJCheckBox;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuItem optionJMenuItem;
    private javax.swing.JMenuItem saveJMenuItem;
    private javax.swing.JMenuItem saveWorldJMenuItem;
    private javax.swing.JMenu viewJMenu;
    private javax.swing.JCheckBoxMenuItem visibleActorJCheckBox;
    private javax.swing.JCheckBoxMenuItem visibleBackgroundJCheckBox;
    private javax.swing.JCheckBoxMenuItem visibleEffectJCheckBox;
    private javax.swing.JCheckBoxMenuItem visibleTileJCheckBox;
    // End of variables declaration//GEN-END:variables
}
