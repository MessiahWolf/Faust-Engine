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

import core.world.World;
import core.world.WorldCell;
import core.world.WorldTemplate;
import io.resource.ResourceDelegate;
import io.util.FileSearch;
import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import Editor.renderer.WorldBoxRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class TemplateEditor extends javax.swing.JDialog {

    // Variable Declaration
    // Project Classes
    private Laboratory lab;
    private WorldCell map;
    private WorldTemplate resource;
    private World world;
    private ResourceDelegate delegate;
    // Data Types
    // Integer Decl.
    private boolean edit;
    // End of Variable Declaration

    public TemplateEditor(Laboratory lab, ResourceDelegate delegate, WorldTemplate resource, boolean modal) {

        // Call to super
        super(lab, modal);
        initComponents();

        // Set values equal
        this.lab = lab;
        this.delegate = delegate;
        this.resource = resource;

        // Initialize my commands
        init();
    }

    private void init() {

        // Model Decl.
        final DefaultComboBoxModel model = new DefaultComboBoxModel();

        // Grab all the worlds
        final Object[] worlds = delegate.getType(World.class);

        // Iterate over the list of worlds
        for (int i = 0; i < worlds.length; i++) {

            // Cast to an FWorld
            final World fworld = (World) worlds[i];

            // Add by display name
            model.addElement(fworld);
        }

        // Setup the combo box model
        worldJComboBox.setModel(model);
        worldJComboBox.setRenderer(new WorldBoxRenderer());

        //
        mapJComboBox.setRenderer(new WorldBoxRenderer());
        mapJComboBox.setEditable(false);
        mapJComboBox.setEnabled(false);

        // Check to see if the world even exists
        if (resource == null) {

            // Creating a default map
            resource = new WorldTemplate(null, null, null, null, null);

            // Change the title
            setTitle("Template Editor: New Template");
        } else {
            setTitle("Template Editor: " + resource.getDisplayName());

            // Grab world from template
            world = resource.getWorld();

            // Make sure world exists
            if (world != null) {
                worldJComboBox.setSelectedItem(world.getDisplayName());
            }

            // Grab from template
            map = resource.getMap();

            // Map must exist
            if (map != null) {
                mapJComboBox.setSelectedItem(map.getDisplayName());
            }
        }

        // Editor Name includes extension
        if (resource.getReferenceName() != null) {

            //
            FileSearch search = new FileSearch(new File(delegate.getDataDirectory()), resource.getReferenceName(), true);

            //
            search.perform();

            // Search for it including file extension (Will find loose files and not files inside of data packages)
            final File file = search.check(resource.getSHA1CheckSum());

            // If we found the file set edit to true
            if (file != null) {
                edit = true;
            }
        } else {
            edit = false;
        }

        // Setup controls
        setup();
    }

    private void finish() {

        // Validity check.
        if (validJCheckBox.isSelected()) {

            // Map and world must exist
            if (world != null && map != null) {

                // Apply these changes to the map
                resource.setReferenceID(idJField.getText());
                resource.setReferenceName(nameJField.getText());
                resource.setDisplayName(displayJField.getText());

                // Ensure attributes map is up to date
                resource.updateAttributes();
                
                //
                resource.validate();
                
                //
                delegate.addResource(resource);

                //
                setVisible(false);
            } else {

                // The error to display
                final String msg = "The world and map must both exist to continue.";

                // Show the message
                JOptionPane.showMessageDialog(this, msg);
            }
        }
    }

    private void setup() {

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

        buttonJPanel = new javax.swing.JPanel();
        validJCheckBox = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        finishJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        cancelJButton = new javax.swing.JButton();
        mainJTabbedPane = new javax.swing.JTabbedPane();
        templateJPanel = new javax.swing.JPanel();
        worldJComboBox = new javax.swing.JComboBox();
        worldJLabel = new javax.swing.JLabel();
        mapJLabel = new javax.swing.JLabel();
        mapJComboBox = new javax.swing.JComboBox();
        basicJPanel = new javax.swing.JPanel();
        nameJField = new javax.swing.JTextField();
        nameJLabel = new javax.swing.JLabel();
        editorJLabel = new javax.swing.JLabel();
        idJField = new javax.swing.JTextField();
        referenceJLabel = new javax.swing.JLabel();
        displayJField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Template Editing");
        setMinimumSize(new java.awt.Dimension(274, 196));
        setResizable(false);

        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));

        validJCheckBox.setText("Valid");
        validJCheckBox.setEnabled(false);
        validJCheckBox.setMaximumSize(new java.awt.Dimension(54, 26));
        validJCheckBox.setMinimumSize(new java.awt.Dimension(54, 26));
        validJCheckBox.setPreferredSize(new java.awt.Dimension(54, 26));
        buttonJPanel.add(validJCheckBox);
        buttonJPanel.add(filler1);

        finishJButton.setText("Finish");
        finishJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        finishJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        finishJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        finishJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(finishJButton);
        buttonJPanel.add(filler2);

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

        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWidths = new int[] {0, 4, 0};
        jPanel1Layout.rowHeights = new int[] {0, 4, 0, 4, 0, 4, 0};
        templateJPanel.setLayout(jPanel1Layout);

        worldJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        worldJComboBox.setMaximumSize(new java.awt.Dimension(136, 22));
        worldJComboBox.setMinimumSize(new java.awt.Dimension(136, 22));
        worldJComboBox.setPreferredSize(new java.awt.Dimension(136, 22));
        worldJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                worldJComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        templateJPanel.add(worldJComboBox, gridBagConstraints);

        worldJLabel.setText("Chosen World:");
        worldJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        worldJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        worldJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        templateJPanel.add(worldJLabel, gridBagConstraints);

        mapJLabel.setText("Chosen Map:");
        mapJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        mapJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        mapJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        templateJPanel.add(mapJLabel, gridBagConstraints);

        mapJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        mapJComboBox.setMaximumSize(new java.awt.Dimension(136, 22));
        mapJComboBox.setMinimumSize(new java.awt.Dimension(136, 22));
        mapJComboBox.setPreferredSize(new java.awt.Dimension(136, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        templateJPanel.add(mapJComboBox, gridBagConstraints);

        mainJTabbedPane.addTab("Template Settings", templateJPanel);

        basicJPanel.setMaximumSize(new java.awt.Dimension(224, 66));
        java.awt.GridBagLayout basicJPanelLayout = new java.awt.GridBagLayout();
        basicJPanelLayout.columnWidths = new int[] {0, 4, 0};
        basicJPanelLayout.rowHeights = new int[] {0, 4, 0, 4, 0};
        basicJPanel.setLayout(basicJPanelLayout);

        nameJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        nameJField.setMaximumSize(new java.awt.Dimension(136, 22));
        nameJField.setMinimumSize(new java.awt.Dimension(136, 22));
        nameJField.setPreferredSize(new java.awt.Dimension(136, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        basicJPanel.add(nameJField, gridBagConstraints);

        nameJLabel.setText("Editor Name:");
        nameJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        nameJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        nameJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        basicJPanel.add(nameJLabel, gridBagConstraints);

        editorJLabel.setText("Editor Id Tag:");
        editorJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        editorJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        editorJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        basicJPanel.add(editorJLabel, gridBagConstraints);

        idJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        idJField.setMaximumSize(new java.awt.Dimension(136, 22));
        idJField.setMinimumSize(new java.awt.Dimension(136, 22));
        idJField.setPreferredSize(new java.awt.Dimension(136, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        basicJPanel.add(idJField, gridBagConstraints);

        referenceJLabel.setText("Display Name:");
        referenceJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        referenceJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        referenceJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        basicJPanel.add(referenceJLabel, gridBagConstraints);

        displayJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        displayJField.setMaximumSize(new java.awt.Dimension(136, 22));
        displayJField.setMinimumSize(new java.awt.Dimension(136, 22));
        displayJField.setPreferredSize(new java.awt.Dimension(136, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        basicJPanel.add(displayJField, gridBagConstraints);

        mainJTabbedPane.addTab("Editor Settings", basicJPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void finishJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishJButtonActionPerformed

        // Apply changes to the map
        finish();
    }//GEN-LAST:event_finishJButtonActionPerformed

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed

        // Cancel changes
        setVisible(false);
    }//GEN-LAST:event_cancelJButtonActionPerformed

    private void worldJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_worldJComboBoxActionPerformed

        // Grab selected item
        world = (World) worldJComboBox.getSelectedItem();

        // World must exist
        if (world != null) {

            // Our box model
            final DefaultComboBoxModel mapModel = new DefaultComboBoxModel();

            // Grab collection of maps from world
            final WorldCell[] maps = world.getCellList().toArray(new WorldCell[]{});

            // Maps must exist
            if (maps != null) {

                // Iterate over map collection
                for (int i = 0; i < maps.length; i++) {

                    // Grab current map
                    final WorldCell imap = maps[i];

                    // Add as element
                    mapModel.addElement(imap);
                }
            }

            // Apply the box model
            mapJComboBox.setEnabled(true);
            mapJComboBox.setModel(mapModel);
        }
    }//GEN-LAST:event_worldJComboBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel basicJPanel;
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JTextField displayJField;
    private javax.swing.JLabel editorJLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton finishJButton;
    private javax.swing.JTextField idJField;
    private javax.swing.JTabbedPane mainJTabbedPane;
    private javax.swing.JComboBox mapJComboBox;
    private javax.swing.JLabel mapJLabel;
    private javax.swing.JTextField nameJField;
    private javax.swing.JLabel nameJLabel;
    private javax.swing.JLabel referenceJLabel;
    private javax.swing.JPanel templateJPanel;
    private javax.swing.JCheckBox validJCheckBox;
    private javax.swing.JComboBox worldJComboBox;
    private javax.swing.JLabel worldJLabel;
    // End of variables declaration//GEN-END:variables
}
