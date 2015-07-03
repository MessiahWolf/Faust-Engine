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
import core.event.WorldEvent;
import core.event.WorldListener;
import core.world.WorldController;
import core.world.WorldCell;
import core.world.World;
import core.world.WorldObject;
import core.world.Zone;
import io.resource.ResourceDelegate;
import io.resource.ResourceReader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import Editor.tools.Grid;

/**
 *
 * @author Robert A. Cherry
 */
public class Laboratory extends javax.swing.JDialog implements MapListener, WorldListener {

    // Variable Declaration
    // Swing Native classes
    private JPanel renderJPanel;
    // Java Native Classes
    private ArrayList<Class> classList;
    // Project Classes
    private WorldController controller;
    private FaustEditor editor;
    private World world;
    private WorldCell map;
    private Grid grid;
    private ResourceDelegate delegate;
    private WorldObject selected;
    // End of Variable Declaraton

    /*
     * As this is just started it does not support the Box2D worlds just yet; give me a couple of days to get a readymade renderer down and l'll just
     * add it in here with a simple decl.
     */
    public Laboratory(FaustEditor editor, ResourceDelegate delegate, World world, WorldCell map, boolean modal) {
        super(editor, modal);
        initComponents();

        // Set values
        this.editor = editor;
        this.delegate = delegate;
        this.world = world;
        this.map = map;

        // Initialize
        init();
    }

    private void init() {

        // Initialize the collection
        classList = new ArrayList<>();

        // Now for the renderer
        renderJPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics monet) {

                // Clear previous paint operation
                super.paintComponent(monet);

                // Custom paint operations defined outside of class; for ease of changing
                renderJPanelPaint(monet);

                // Dispose of this graphics object
                monet.dispose();
            }
        };
        renderJPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {

                //
                if (map != null) {

                    //
                    final Zone[] zones = map.getZoneList().toArray(new Zone[]{});

                    for (int i = 0; i < zones.length; i++) {

                        //
                        final Zone zone = zones[i];

                        //
                        if (zone != null) {
                            zone.setEntered(zone.getBounds().contains(event.getPoint()));
                        }
                    }

                    //
                    renderJPanel.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
            }

            @Override
            public void mouseDragged(MouseEvent event) {

                // Map must exist
                if (map != null) {

                    // Grab the zone at position
                    final Zone zone = map.getZoneAtPosition(event.getPoint());

                    // Zone must exist
                    if (zone != null) {

                        // Center it about the mouse point
                        int x = event.getPoint().x - zone.getBounds().width / 2;
                        int y = event.getPoint().y - zone.getBounds().height / 2;

                        // Move to that position
                        zone.move(new Point(x, y));

                        // repaint
                        renderJPanel.repaint();
                    }
                }
            }
        });
        renderJPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {

                //
                if (map != null) {

                    //
                    if (selected != null) {
                        selected.setDrawBorder(false);
                    }
                    selected = null;
                }
            }
        });
        renderJPanel.revalidate();
        renderJPanel.repaint();
        final KeyStroke buttonRightBrace = KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0, true);
        renderJPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonRightBrace, "]");
        renderJPanel.getActionMap().put("]", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Moving up
                if ((grid.getCellWidth() + 8) < map.getWidth()) {

                    // Adjust the cell width and height
                    grid.setCellWidth(grid.getCellWidth() + 8);
                    grid.setCellHeight(grid.getCellHeight() + 8);

                    // Repaint and validate the grid
                    grid.validate();

                    // Repaint the canvas
                    repaint();
                }
            }
        });
        final KeyStroke buttonLeftBrace = KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0, true);
        renderJPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(buttonLeftBrace, "[");
        renderJPanel.getActionMap().put("[", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Moving down
                if ((grid.getCellWidth() - 8) > 16) {

                    // Adjust the cell width and height
                    grid.setCellWidth(grid.getCellWidth() - 8);
                    grid.setCellHeight(grid.getCellHeight() - 8);

                    // Repaint and validate the grid
                    grid.validate();

                    // Repaint the canvas
                    repaint();
                }
            }
        });

        // Give to scroll pane
        renderJPanel.setPreferredSize(new Dimension(map.getWidth(), map.getHeight()));
        renderJPanel.setMinimumSize(new Dimension(map.getWidth(), map.getHeight()));
        renderJPanel.setMaximumSize(new Dimension(map.getWidth(), map.getHeight()));
        renderJPanel.setSize(map.getWidth(), map.getHeight());
        worldJScrollPane.revalidate();

        if (map != null) {
            //
            final int numRows = map.getWidth() / 24 + 1;
            final int numCols = map.getHeight() / 24 + 1;

            // Create a default grid
            grid = new Grid(numRows, numCols, 24, 24, Color.GRAY);
        }

        // We want copies; not the actual thing
        world = world.reproduce();
        map = map.reproduce();

        // Initialize the controller
        controller = new WorldController(60);
        controller.connect(world);

        // Listen to world and map
        world.addWorldListener(this);
        map.addMapListener(this);

        // Close thread when closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                // Shutdown the controller
                controller.stop();
                controller = null;
            }
        });

        //
        setupIcons();
    }

    private void setupIcons() {

        // Grab class and toolkit for quick references.
        final Class closs = getClass();

        // Define Icons
        final ImageIcon iconMarker = ResourceReader.readClassPathIcon(closs,"/icons/icon-zone16.png");
        final ImageIcon iconRestart = ResourceReader.readClassPathIcon(closs,"/icons/icon-restart16.png");
        final ImageIcon iconStart = ResourceReader.readClassPathIcon(closs,"/icons/icon-thread-start16.png");
        final ImageIcon iconStop = ResourceReader.readClassPathIcon(closs,"/icons/icon-thread-stop16.png");
        final ImageIcon iconMapPrevious = ResourceReader.readClassPathIcon(closs,"/icons/icon-previous16.png");
        final ImageIcon iconMapNext = ResourceReader.readClassPathIcon(closs,"/icons/icon-next16.png");
        final ImageIcon iconGrid = ResourceReader.readClassPathIcon(closs,"/icons/icon-grid16.png");
        final ImageIcon iconGridColor = ResourceReader.readClassPathIcon(closs,"/icons/icon-grid-color16.png");
        final ImageIcon iconTemplate = ResourceReader.readClassPathIcon(closs,"/icons/icon-template16.png");

        // Apply to Buttons
        markerJButton.setIcon(iconMarker);
        restartJButton.setIcon(iconRestart);
        resumeJButton.setIcon(iconStart);
        stopJButton.setIcon(iconStop);
        previousJButton.setIcon(iconMapPrevious);
        nextJButton.setIcon(iconMapNext);
        gridJButton.setIcon(iconGrid);
        gridColorJButton.setIcon(iconGridColor);
        templateJButton.setIcon(iconTemplate);
    }

    private void renderJPanelPaint(Graphics monet) {

        // Make sure they all exist
        if (world != null) {

            // Make the map exists
            if (map != null) {

                // Ask the map to paint all of its contents
                final BufferedImage image = map.paint(renderJPanel);

                // Render the map
                monet.drawImage(image, 0, 0, renderJPanel);

                // Draw the grid
                grid.paint(monet, this, 1.0f);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        worldJScrollPane = new javax.swing.JScrollPane();
        buttonJPanel = new javax.swing.JPanel();
        frameCountJLabel = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        frameRateJLabel = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        closeJButton = new javax.swing.JButton();
        toolJPanel = new javax.swing.JPanel();
        markerJButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        restartJButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        resumeJButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        stopJButton = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        previousJButton = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        nextJButton = new javax.swing.JButton();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        templateJButton = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        gridJButton = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        gridColorJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Research Laboratory");
        setMinimumSize(new java.awt.Dimension(464, 452));
        setResizable(false);

        worldJScrollPane.setMaximumSize(new java.awt.Dimension(404, 401));
        worldJScrollPane.setMinimumSize(new java.awt.Dimension(404, 401));
        worldJScrollPane.setPreferredSize(new java.awt.Dimension(404, 401));

        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));

        frameCountJLabel.setText("Frame Count: ");
        buttonJPanel.add(frameCountJLabel);
        buttonJPanel.add(filler2);

        frameRateJLabel.setText("Frame Rate: ");
        buttonJPanel.add(frameRateJLabel);
        buttonJPanel.add(filler3);

        closeJButton.setText("Done Testing");
        closeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(closeJButton);

        toolJPanel.setLayout(new javax.swing.BoxLayout(toolJPanel, javax.swing.BoxLayout.LINE_AXIS));

        markerJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        markerJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        markerJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        markerJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                markerJButtonActionPerformed(evt);
            }
        });
        toolJPanel.add(markerJButton);
        toolJPanel.add(filler4);

        restartJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        restartJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        restartJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        restartJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restartJButtonActionPerformed(evt);
            }
        });
        toolJPanel.add(restartJButton);
        toolJPanel.add(filler1);

        resumeJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        resumeJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        resumeJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        resumeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resumeJButtonActionPerformed(evt);
            }
        });
        toolJPanel.add(resumeJButton);
        toolJPanel.add(filler5);

        stopJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        stopJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        stopJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        stopJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopJButtonActionPerformed(evt);
            }
        });
        toolJPanel.add(stopJButton);
        toolJPanel.add(filler7);

        previousJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        previousJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        previousJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        previousJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousJButtonActionPerformed(evt);
            }
        });
        toolJPanel.add(previousJButton);
        toolJPanel.add(filler6);

        nextJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        nextJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        nextJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        nextJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextJButtonActionPerformed(evt);
            }
        });
        toolJPanel.add(nextJButton);
        toolJPanel.add(filler10);

        templateJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        templateJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        templateJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        templateJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                templateJButtonActionPerformed(evt);
            }
        });
        toolJPanel.add(templateJButton);
        toolJPanel.add(filler8);

        gridJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        gridJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        gridJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        gridJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gridJButtonActionPerformed(evt);
            }
        });
        toolJPanel.add(gridJButton);
        toolJPanel.add(filler9);

        gridColorJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        gridColorJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        gridColorJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        gridColorJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gridColorJButtonActionPerformed(evt);
            }
        });
        toolJPanel.add(gridColorJButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(worldJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                        .addComponent(toolJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(toolJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(worldJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeJButtonActionPerformed

        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_closeJButtonActionPerformed

    private void markerJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markerJButtonActionPerformed

        // Allows you to choose a type of marker
        // For now just create a new Zone
        final Zone zone = new Zone(new Point(0, 0));

        //
        if (map != null) {

            // Add the zone to the map
            map.addZone(zone);
        }
    }//GEN-LAST:event_markerJButtonActionPerformed

    private void restartJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restartJButtonActionPerformed

        // Restart the controller
        controller.restart();
    }//GEN-LAST:event_restartJButtonActionPerformed

    private void resumeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resumeJButtonActionPerformed

        // Start the thread
        controller.resume();
    }//GEN-LAST:event_resumeJButtonActionPerformed

    private void stopJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopJButtonActionPerformed

        // Stop the thread
        controller.stop();
    }//GEN-LAST:event_stopJButtonActionPerformed

    private void nextJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextJButtonActionPerformed

        // Ensure world exists
        if (world != null) {

            // Ensure map exists
            if (map != null) {

                // First find the current map
                final int index = world.getIndexOfMap(map);

                // Grab the next map
                final WorldCell next = world.getMapAtIndex(index + 1);

                // Next must exist
                if (next != null) {

                    // Set map as
                    map = next;

                    // Repaint
                    renderJPanel.repaint();
                }
            }
        }
    }//GEN-LAST:event_nextJButtonActionPerformed

    private void previousJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousJButtonActionPerformed

        // Ensure world exists
        if (world != null) {

            // Ensure map exists
            if (map != null) {

                // First find the current map
                final int index = world.getIndexOfMap(map);

                // Grab the next map
                final WorldCell previous = world.getMapAtIndex(index - 1);

                // Next must exist
                if (previous != null) {

                    // Set map as
                    map = previous;

                    // Repaint
                    renderJPanel.repaint();
                }
            }
        }
    }//GEN-LAST:event_previousJButtonActionPerformed

    private void gridJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gridJButtonActionPerformed

        // Toggle grid visiblity
        if (grid != null) {

            // Ensure exists and swap
            grid.setVisible(!grid.isVisible());

            // Repaint 
            renderJPanel.repaint();
        }
    }//GEN-LAST:event_gridJButtonActionPerformed

    private void gridColorJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gridColorJButtonActionPerformed

        // Ensure grid existince
        if (grid != null) {

            // Show color chooser
            final Color color = JColorChooser.showDialog(this, "Choose new grid color", grid.getColor());

            // Ensure exists
            if (color != null) {

                // Set value
                grid.setColor(color);

                // Repaint
                repaint();
            }
        }
    }//GEN-LAST:event_gridColorJButtonActionPerformed

    private void templateJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_templateJButtonActionPerformed

        // Creates a template from the current world and map
        final TemplateEditor maker = new TemplateEditor(this, delegate, null, true);
        maker.setLocationRelativeTo(this);
        maker.setVisible(true);

        //
        maker.dispose();
    }//GEN-LAST:event_templateJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton closeJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel frameCountJLabel;
    private javax.swing.JLabel frameRateJLabel;
    private javax.swing.JButton gridColorJButton;
    private javax.swing.JButton gridJButton;
    private javax.swing.JButton markerJButton;
    private javax.swing.JButton nextJButton;
    private javax.swing.JButton previousJButton;
    private javax.swing.JButton restartJButton;
    private javax.swing.JButton resumeJButton;
    private javax.swing.JButton stopJButton;
    private javax.swing.JButton templateJButton;
    private javax.swing.JPanel toolJPanel;
    private javax.swing.JScrollPane worldJScrollPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mapModified(MapEvent event) {

        // Force a repaint
        renderJPanel.repaint();
    }

    @Override
    public void worldModified(WorldEvent event) {

        // Adjust the frame count and frame rate labels
        frameCountJLabel.setText("Frame Count: " + controller.getFrameCount());
        frameRateJLabel.setText("Frame Rate: " + controller.getFrameRate());

        // Force a repaint
        renderJPanel.repaint();
    }
}
