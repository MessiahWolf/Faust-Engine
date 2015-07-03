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
import core.world.Animation;
import core.world.Backdrop;
import core.world.Tileset;
import core.world.WorldCell;
import core.world.World;
import core.world.item.Weapon;
import io.resource.ResourceDelegate;
import io.resource.ResourceReader;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 *
 * @author Robert A. Cherry (Messiah Wolf)
 */
public class UtilityToolbar extends javax.swing.JPanel {

    // Variable Declaration
    // Project Classes
    private ChecksumViewer viewer;
    private ResourceDelegate delegate;
    private FaustEditor editor;
    // End of Variable Declaration

    public UtilityToolbar(FaustEditor editor, ResourceDelegate delegate) {

        // Initialize Components
        initComponents();

        // Set values equal
        this.editor = editor;
        this.delegate = delegate;

        // Initialize
        init();
    }

    private void init() {

        //
        try {

            // Grab source folder
            final Class closs = getClass();

            // Grab some icons from cache
            mapJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-map24.png"));
            actorJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-actor24.png"));
            tilesetJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-tileset24.png"));
            animationJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-animation24.png"));
            backdropJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-background24.png"));
            itemJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-item24.png"));
            zoneJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-zone24.png"));
            layerJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-layer24.png"));
            packageJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-package24.png"));
            worldJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-world24.png"));
            labJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-lab24.png"));
            scriptJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-script24.png"));
            pictureJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-camera24.png"));
            sumJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-checksum24.png"));
        } catch (NullPointerException npe) {
            System.err.println(npe);
        }

        //
        final Dimension fiveComponentDimension = new Dimension(168, 32);
        final Dimension fourComponentDimension = new Dimension(136, 32);
        final Dimension tripleComponentDimension = new Dimension(104, 32);
        final Dimension doubleComponentDimension = new Dimension(72, 32);
        final Dimension singleComponentDimension = new Dimension(40, 32);
        final Dimension strutDimension = new Dimension(4, 32);

        //
        final JToolBar worldBar = new JToolBar();
        worldBar.setFloatable(false);
        worldBar.setOrientation(JToolBar.HORIZONTAL);
        worldBar.setPreferredSize(tripleComponentDimension);
        worldBar.setMaximumSize(tripleComponentDimension);
        worldBar.setMinimumSize(tripleComponentDimension);
        worldBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));
        worldBar.add(worldJButton);
        worldBar.add(mapJButton);
        worldBar.add(layerJButton);
        worldBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));

        //
        final JToolBar resourceBar = new JToolBar();
        resourceBar.setFloatable(false);
        resourceBar.setOrientation(JToolBar.HORIZONTAL);
        resourceBar.setPreferredSize(doubleComponentDimension);
        resourceBar.setMaximumSize(doubleComponentDimension);
        resourceBar.setMinimumSize(doubleComponentDimension);
        resourceBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));
        resourceBar.add(actorJButton);
        resourceBar.add(itemJButton);
        resourceBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));

        //
        final JToolBar graphicBar = new JToolBar();
        graphicBar.setFloatable(false);
        graphicBar.setOrientation(JToolBar.HORIZONTAL);
        graphicBar.setPreferredSize(tripleComponentDimension);
        graphicBar.setMaximumSize(tripleComponentDimension);
        graphicBar.setMinimumSize(tripleComponentDimension);
        graphicBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));
        graphicBar.add(backdropJButton);
        graphicBar.add(animationJButton);
        graphicBar.add(tilesetJButton);
        graphicBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));

        //
        final JToolBar pluginBar = new JToolBar();
        pluginBar.setFloatable(false);
        pluginBar.setOrientation(JToolBar.HORIZONTAL);
        pluginBar.setPreferredSize(fiveComponentDimension);
        pluginBar.setMaximumSize(fiveComponentDimension);
        pluginBar.setMinimumSize(fiveComponentDimension);
        pluginBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));
        pluginBar.add(packageJButton);
        pluginBar.add(labJButton);
        pluginBar.add(scriptJButton);
        //pluginBar.add(pictureJButton);
        pluginBar.add(sumJButton);
        pluginBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));

        // 392
        // Change the size
        setPreferredSize(new Dimension(456, 32));
        setMinimumSize(new Dimension(456, 32));
        setMaximumSize(new Dimension(456, 32));

        //
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        add(worldBar);
        add(new JSeparator(JSeparator.VERTICAL));
        add(resourceBar);
        add(new JSeparator(JSeparator.VERTICAL));
        add(graphicBar);
        add(new JSeparator(JSeparator.VERTICAL));
        add(pluginBar);
    }

    /**
     * This method is called from within the constructor to Start the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mapJButton = new JButton();
        worldJButton = new JButton();
        layerJButton = new JButton();
        packageJButton = new JButton();
        zoneJButton = new JButton();
        animationJButton = new JButton();
        tilesetJButton = new JButton();
        backdropJButton = new JButton();
        itemJButton = new JButton();
        actorJButton = new JButton();
        labJButton = new JButton();
        scriptJButton = new JButton();
        pictureJButton = new JButton();
        sumJButton = new JButton();

        mapJButton.setToolTipText("Create Cell");
        mapJButton.setFocusable(false);
        mapJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        mapJButton.setMaximumSize(new Dimension(32, 32));
        mapJButton.setMinimumSize(new Dimension(32, 32));
        mapJButton.setPreferredSize(new Dimension(32, 32));
        mapJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        mapJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mapJButtonActionPerformed(evt);
            }
        });
        mapJButton.getAccessibleContext().setAccessibleDescription("");

        worldJButton.setToolTipText("Create World");
        worldJButton.setFocusable(false);
        worldJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        worldJButton.setMaximumSize(new Dimension(32, 32));
        worldJButton.setMinimumSize(new Dimension(32, 32));
        worldJButton.setPreferredSize(new Dimension(32, 32));
        worldJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        worldJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                worldJButtonActionPerformed(evt);
            }
        });

        layerJButton.setToolTipText("Create Cell Layer");
        layerJButton.setFocusable(false);
        layerJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        layerJButton.setMaximumSize(new Dimension(32, 32));
        layerJButton.setMinimumSize(new Dimension(32, 32));
        layerJButton.setPreferredSize(new Dimension(32, 32));
        layerJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        layerJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                layerJButtonActionPerformed(evt);
            }
        });

        packageJButton.setToolTipText("Create a Package");
        packageJButton.setFocusable(false);
        packageJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        packageJButton.setMaximumSize(new Dimension(32, 32));
        packageJButton.setMinimumSize(new Dimension(32, 32));
        packageJButton.setPreferredSize(new Dimension(32, 32));
        packageJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        packageJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                packageJButtonActionPerformed(evt);
            }
        });

        zoneJButton.setToolTipText("Create a Zone");
        zoneJButton.setFocusable(false);
        zoneJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        zoneJButton.setMaximumSize(new Dimension(32, 32));
        zoneJButton.setMinimumSize(new Dimension(32, 32));
        zoneJButton.setPreferredSize(new Dimension(32, 32));
        zoneJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        zoneJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                zoneJButtonActionPerformed(evt);
            }
        });

        animationJButton.setToolTipText("Create an Animation");
        animationJButton.setFocusable(false);
        animationJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        animationJButton.setMaximumSize(new Dimension(32, 32));
        animationJButton.setMinimumSize(new Dimension(32, 32));
        animationJButton.setPreferredSize(new Dimension(32, 32));
        animationJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        animationJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                animationJButtonActionPerformed(evt);
            }
        });

        tilesetJButton.setToolTipText("Create a Tileset");
        tilesetJButton.setFocusable(false);
        tilesetJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        tilesetJButton.setMaximumSize(new Dimension(32, 32));
        tilesetJButton.setMinimumSize(new Dimension(32, 32));
        tilesetJButton.setPreferredSize(new Dimension(32, 32));
        tilesetJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        tilesetJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                tilesetJButtonActionPerformed(evt);
            }
        });

        backdropJButton.setToolTipText("Create a Background");
        backdropJButton.setFocusable(false);
        backdropJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        backdropJButton.setMaximumSize(new Dimension(32, 32));
        backdropJButton.setMinimumSize(new Dimension(32, 32));
        backdropJButton.setPreferredSize(new Dimension(32, 32));
        backdropJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        backdropJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                backdropJButtonActionPerformed(evt);
            }
        });

        itemJButton.setToolTipText("Create an Item");
        itemJButton.setFocusable(false);
        itemJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        itemJButton.setMaximumSize(new Dimension(32, 32));
        itemJButton.setMinimumSize(new Dimension(32, 32));
        itemJButton.setPreferredSize(new Dimension(32, 32));
        itemJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        itemJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                itemJButtonActionPerformed(evt);
            }
        });

        actorJButton.setToolTipText("Create an Actor");
        actorJButton.setFocusable(false);
        actorJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        actorJButton.setMaximumSize(new Dimension(32, 32));
        actorJButton.setMinimumSize(new Dimension(32, 32));
        actorJButton.setPreferredSize(new Dimension(32, 32));
        actorJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        actorJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actorJButtonActionPerformed(evt);
            }
        });

        labJButton.setToolTipText("Open the Laboratory");
        labJButton.setFocusable(false);
        labJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        labJButton.setMaximumSize(new Dimension(32, 32));
        labJButton.setMinimumSize(new Dimension(32, 32));
        labJButton.setPreferredSize(new Dimension(32, 32));
        labJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        labJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                labJButtonActionPerformed(evt);
            }
        });

        scriptJButton.setToolTipText("Import a Script");
        scriptJButton.setFocusable(false);
        scriptJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        scriptJButton.setMaximumSize(new Dimension(32, 32));
        scriptJButton.setMinimumSize(new Dimension(32, 32));
        scriptJButton.setPreferredSize(new Dimension(32, 32));
        scriptJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        scriptJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                scriptJButtonActionPerformed(evt);
            }
        });

        pictureJButton.setToolTipText("Import Images");
        pictureJButton.setFocusable(false);
        pictureJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        pictureJButton.setMaximumSize(new Dimension(32, 32));
        pictureJButton.setMinimumSize(new Dimension(32, 32));
        pictureJButton.setPreferredSize(new Dimension(32, 32));
        pictureJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        pictureJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pictureJButtonActionPerformed(evt);
            }
        });

        sumJButton.setToolTipText("View File Checksums");
        sumJButton.setFocusable(false);
        sumJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        sumJButton.setMaximumSize(new Dimension(32, 32));
        sumJButton.setMinimumSize(new Dimension(32, 32));
        sumJButton.setPreferredSize(new Dimension(32, 32));
        sumJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        sumJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sumJButtonActionPerformed(evt);
            }
        });

        setMaximumSize(new Dimension(32, 32));
        setMinimumSize(new Dimension(32, 32));
        setPreferredSize(new Dimension(32, 32));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void mapJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_mapJButtonActionPerformed

        // Show the map editor
        final World world = editor.getCanvas().getWorld();

        if (world != null) {

            //
            final WorldCellEditor maker = new WorldCellEditor(editor, delegate, new WorldCell(world), true);
            maker.setLocationRelativeTo(editor);
            maker.setVisible(true);

            // Dispose of it
            maker.dispose();
        } else {

            //
            final String msg = "Cannot complete action.\nThere is not an active World on the canvas.";

            // Show failure Dialog
            JOptionPane.showMessageDialog(editor, msg);
        }
    }//GEN-LAST:event_mapJButtonActionPerformed

    private void zoneJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_zoneJButtonActionPerformed
        //
    }//GEN-LAST:event_zoneJButtonActionPerformed

    private void layerJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_layerJButtonActionPerformed

        // Show the layer editor
        final World world = editor.getCanvas().getWorld();
        final WorldCell map = editor.getCanvas().getMap();

        if (world != null) {
            if (map != null) {

                //
                final WorldCellLayerEditor maker = new WorldCellLayerEditor(editor, delegate, map, null, false, true);
                maker.setLocationRelativeTo(editor);
                maker.setVisible(true);

                // Dispose of it
                maker.dispose();
            } else {

                // Show error message
                final String msg = "Cannot complete action.\nThere is not an active map on the canvas.";

                // Failure to apply change to canvas
                JOptionPane.showMessageDialog(editor, msg);
            }
        } else {

            // Show error message
            final String msg = "Cannot complete action.\nThere is not an active world on the canvas.";

            // Failure to apply change to canvas
            JOptionPane.showMessageDialog(editor, msg);
        }
    }//GEN-LAST:event_layerJButtonActionPerformed

    private void tilesetJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_tilesetJButtonActionPerformed

        // Show the graphicset editor
        final IllustrationEditor maker = new IllustrationEditor(editor, delegate, new Tileset(), true);
        maker.setLocationRelativeTo(editor);
        maker.setVisible(true);

        // Dispose of it
        maker.dispose();
    }//GEN-LAST:event_tilesetJButtonActionPerformed

    private void actorJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_actorJButtonActionPerformed

        // Show the actor editor
        final ActorEditor maker = new ActorEditor(editor, delegate, null, true);
        maker.setLocationRelativeTo(editor);
        maker.setVisible(true);

        // Dispose of it
        maker.dispose();
    }//GEN-LAST:event_actorJButtonActionPerformed

    private void itemJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_itemJButtonActionPerformed

        // Show the item editor
        final ItemEditor maker = new ItemEditor(editor, delegate, new Weapon(), true);
        maker.setLocationRelativeTo(editor);
        maker.setVisible(true);

        // Dispose of it
        maker.dispose();
    }//GEN-LAST:event_itemJButtonActionPerformed

    private void backdropJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_backdropJButtonActionPerformed

        // Show the background editor
        final IllustrationEditor maker = new IllustrationEditor(editor, delegate, new Backdrop(), true);
        maker.setLocationRelativeTo(editor);
        maker.setVisible(true);

        // Dispose of it
        maker.dispose();
    }//GEN-LAST:event_backdropJButtonActionPerformed

    private void animationJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_animationJButtonActionPerformed

        // Show the animation editor
        final IllustrationEditor maker = new IllustrationEditor(editor, delegate, new Animation(), true);
        maker.setLocationRelativeTo(editor);
        maker.setVisible(true);

        // Dispose of it
        maker.dispose();
    }//GEN-LAST:event_animationJButtonActionPerformed

    private void packageJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_packageJButtonActionPerformed

        // Show the dataPackage editor
        final PackageEditor maker = new PackageEditor(editor, delegate, null, true);
        maker.setLocationRelativeTo(editor);
        maker.setVisible(true);

        // Dispose of it
        maker.dispose();
    }//GEN-LAST:event_packageJButtonActionPerformed

    private void worldJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_worldJButtonActionPerformed

        // Show the world editor
        final WorldEditor maker = new WorldEditor(editor, delegate, new World(), true);
        maker.setLocationRelativeTo(editor);
        maker.setVisible(true);

        // Dispose of it
        maker.dispose();
    }//GEN-LAST:event_worldJButtonActionPerformed

    private void labJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_labJButtonActionPerformed

        // Show the Template Selector; basically it sets up the lab with the chosen world and chosen map to mimic in the actual test lab.
        // Ill try to include a map that is basically empty in a default world in the base archives. So you dont actually have to make a map to goto the test lab
        final TemplateSelector selector = new TemplateSelector(editor, delegate, true);
        selector.setLocationRelativeTo(editor);
        selector.setVisible(true);

        // Dialog will take its course with user input
        final World world = selector.getWorld();
        final WorldCell map = selector.getMap();

        //
        selector.dispose();

        // They must both exist
        if (world != null && map != null) {

            // Show the actual Test Lab
            final Laboratory laboratory = new Laboratory(editor, delegate, world, map, true);
            laboratory.setLocationRelativeTo(editor);
            laboratory.setVisible(true);

            // Dispose of the Laboratory after the user hits the Done button
            laboratory.dispose();
        }
    }//GEN-LAST:event_labJButtonActionPerformed

    private void scriptJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_scriptJButtonActionPerformed

        // Show the script editor
        final ScriptEditor maker = new ScriptEditor(editor, delegate, null, true);
        maker.setLocationRelativeTo(editor);
        maker.setVisible(true);

        // Dialog will take its course with user input
        maker.dispose();
    }//GEN-LAST:event_scriptJButtonActionPerformed

    private void pictureJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_pictureJButtonActionPerformed

        // TODO add your handling code here:
        final PackageEditor importer = new PackageEditor(editor, delegate, null, true);
        importer.setLocationRelativeTo(editor);
        importer.setVisible(true);

        //
        importer.dispose();
    }//GEN-LAST:event_pictureJButtonActionPerformed

    private void sumJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_sumJButtonActionPerformed

        // Dispose of old one
        if (viewer != null) {
            viewer.dispose();
        }

        // TODO add your handling code here:
        viewer = new ChecksumViewer(editor, delegate, false);
        viewer.setLocationRelativeTo(editor);
        viewer.setVisible(true);
    }//GEN-LAST:event_sumJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton actorJButton;
    private JButton animationJButton;
    private JButton backdropJButton;
    private JButton itemJButton;
    private JButton labJButton;
    private JButton layerJButton;
    private JButton mapJButton;
    private JButton packageJButton;
    private JButton pictureJButton;
    private JButton scriptJButton;
    private JButton sumJButton;
    private JButton tilesetJButton;
    private JButton worldJButton;
    private JButton zoneJButton;
    // End of variables declaration//GEN-END:variables
}
