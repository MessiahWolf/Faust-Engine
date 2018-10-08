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
import core.world.LightSource;
import core.world.Animation;
import core.world.Backdrop;
import core.world.Tileset;
import core.world.Room;
import core.world.RoomLayer;
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
            mapJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-room-add24.png"));
            actorJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-actor-add24.png"));
            tilesetJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-tileset-add24.png"));
            animationJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-animation-add24.png"));
            backdropJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-background-add24.png"));
            lightJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-light24.png"));
            itemJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-item-add24.png"));
            zoneJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-zone24.png"));
            layerJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-layer-add24.png"));
            packageJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-package-add24.png"));
        } catch (NullPointerException npe) {
            System.err.println(npe);
        }

        //
        final Dimension fiveComponentDimension = new Dimension(168, 32);
        final Dimension fourComponentDimension = new Dimension(136, 32);
        final Dimension threeComponentDimension = new Dimension(104, 32);
        final Dimension twoComponentDimension = new Dimension(72, 32);
        final Dimension oneComponentDimension = new Dimension(40, 32);
        final Dimension strutDimension = new Dimension(4, 32);

        //
        final JToolBar worldBar = new JToolBar();
        worldBar.setFloatable(false);
        worldBar.setOrientation(JToolBar.HORIZONTAL);
        worldBar.setPreferredSize(threeComponentDimension);
        worldBar.setMaximumSize(threeComponentDimension);
        worldBar.setMinimumSize(threeComponentDimension);
        worldBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));
        worldBar.add(mapJButton);
        worldBar.add(lightJButton);
        worldBar.add(layerJButton);
        worldBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));

        //
        final JToolBar resourceBar = new JToolBar();
        resourceBar.setFloatable(false);
        resourceBar.setOrientation(JToolBar.HORIZONTAL);
        resourceBar.setPreferredSize(twoComponentDimension);
        resourceBar.setMaximumSize(twoComponentDimension);
        resourceBar.setMinimumSize(twoComponentDimension);
        resourceBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));
        resourceBar.add(actorJButton);
        resourceBar.add(itemJButton);
        resourceBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));

        //
        final JToolBar graphicBar = new JToolBar();
        graphicBar.setFloatable(false);
        graphicBar.setOrientation(JToolBar.HORIZONTAL);
        graphicBar.setPreferredSize(threeComponentDimension);
        graphicBar.setMaximumSize(threeComponentDimension);
        graphicBar.setMinimumSize(threeComponentDimension);
        graphicBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));
        graphicBar.add(backdropJButton);
        graphicBar.add(animationJButton);
        graphicBar.add(tilesetJButton);
        graphicBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));

        //
        final JToolBar pluginBar = new JToolBar();
        pluginBar.setFloatable(false);
        pluginBar.setOrientation(JToolBar.HORIZONTAL);
        pluginBar.setPreferredSize(oneComponentDimension);
        pluginBar.setMaximumSize(oneComponentDimension);
        pluginBar.setMinimumSize(oneComponentDimension);
        pluginBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));
        pluginBar.add(packageJButton);
        pluginBar.add(new Box.Filler(strutDimension, strutDimension, strutDimension));

        // 392
        // Change the size
        setPreferredSize(new Dimension(288, 32));
        setMinimumSize(new Dimension(288, 32));
        setMaximumSize(new Dimension(288, 32));

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
        layerJButton = new JButton();
        packageJButton = new JButton();
        zoneJButton = new JButton();
        animationJButton = new JButton();
        tilesetJButton = new JButton();
        backdropJButton = new JButton();
        itemJButton = new JButton();
        actorJButton = new JButton();
        lightJButton = new JButton();

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

        lightJButton.setToolTipText("Create a Light Source");
        lightJButton.setFocusable(false);
        lightJButton.setHorizontalTextPosition(SwingConstants.CENTER);
        lightJButton.setMaximumSize(new Dimension(32, 32));
        lightJButton.setMinimumSize(new Dimension(32, 32));
        lightJButton.setPreferredSize(new Dimension(32, 32));
        lightJButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        lightJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                lightJButtonActionPerformed(evt);
            }
        });

        setMaximumSize(new Dimension(32, 32));
        setMinimumSize(new Dimension(32, 32));
        setPreferredSize(new Dimension(32, 32));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void mapJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_mapJButtonActionPerformed
        //
        final RoomEditor maker = new RoomEditor(editor, delegate, new Room(), true);
        maker.setLocationRelativeTo(editor);
        maker.setVisible(true);

        // Dispose of it
        maker.dispose();
    }//GEN-LAST:event_mapJButtonActionPerformed

    private void zoneJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_zoneJButtonActionPerformed
        //
    }//GEN-LAST:event_zoneJButtonActionPerformed

    private void layerJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_layerJButtonActionPerformed

        // Show the layer editor
        final Room map = editor.getCanvas().getMap();

        if (map != null) {

            //
            final LayerEditor maker = new LayerEditor(editor, delegate, map, new RoomLayer(map, ""), false, true);
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

        // @NOT AVAILABLE YET
    }//GEN-LAST:event_actorJButtonActionPerformed

    private void itemJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_itemJButtonActionPerformed

        // @NOT AVAILABLE YET
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

    private void lightJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_lightJButtonActionPerformed
        // Show the layer editor
        final Room map = editor.getCanvas().getMap();

        //
        if (map != null) {
            
            //
            final LightEditor maker = new LightEditor(editor, delegate, map, new LightSource(0, 0, 15, 0, 0), true);
            maker.setLocationRelativeTo(editor);
            maker.setVisible(true);

            //
            maker.dispose();
        }
    }//GEN-LAST:event_lightJButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton actorJButton;
    private JButton animationJButton;
    private JButton backdropJButton;
    private JButton itemJButton;
    private JButton layerJButton;
    private JButton lightJButton;
    private JButton mapJButton;
    private JButton packageJButton;
    private JButton tilesetJButton;
    private JButton zoneJButton;
    // End of variables declaration//GEN-END:variables
}
