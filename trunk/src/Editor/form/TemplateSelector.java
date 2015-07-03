/**
    Copyright (c) 2013, Robert Cherry    
    
    All rights reserved.
  
    This file is part of the Faust Editor.

    The Faust Editor is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Faust Editor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with The Faust Editor.  If not, see <http://www.gnu.org/licenses/>.
*/
package Editor.form;

import Editor.FaustEditor;
import core.world.WorldCell;
import core.world.World;
import io.resource.DataPackage;
import core.world.WorldTemplate;
import io.resource.ResourceDelegate;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.plaf.basic.ComboPopup;
import Editor.renderer.DataBoxRenderer;
import Editor.renderer.TemplateBoxRenderer;
import Editor.renderer.WorldBoxRenderer;
import Editor.renderer.WorldListRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class TemplateSelector extends javax.swing.JDialog {

    // Variable Declaration
    // Project Classes
    private FaustEditor editor;
    private WorldCell map;
    private World world;
    private ResourceDelegate delegate;
    // End of Variable Declaration

    public TemplateSelector(FaustEditor editor, ResourceDelegate delegate, boolean modal) {
        super(editor, modal);
        initComponents();

        // Set values
        this.editor = editor;
        this.delegate = delegate;

        // Initialize
        init();
    }

    private void init() {

        // Find all data packages
        final DataPackage[] packages = delegate.getDataPackages();

        // Define our combobox model and populate it
        final DefaultComboBoxModel packageModel = new DefaultComboBoxModel();
        packageModel.addElement(ResourceDelegate.UNPACKAGED_STATEMENT);

        // Iterate over packages
        for (int i = 0; i < packages.length; i++) {

            // Current data package
            final DataPackage element = packages[i];

            // Add as element
            packageModel.addElement(element);
        }

        // Define out template model
        final DefaultComboBoxModel templateModel = new DefaultComboBoxModel();

        // Check
        if (packageModel.getSize() > 0) {

            // Data Package check
            if (packageJComboBox.getSelectedItem() instanceof DataPackage) {

                // Attempt grab
                final DataPackage selected = (DataPackage) packageJComboBox.getSelectedItem();

                // Null check
                if (selected != null) {

                    // Find all templates
                    final Object[] templates = selected.getType(WorldTemplate.class);

                    // Iterate over templates
                    for (int i = 0; i < templates.length; i++) {

                        // Current template
                        final WorldTemplate template = (WorldTemplate) templates[i];

                        // Add as element
                        templateModel.addElement(template);
                    }
                }
            }
        }

        // Give to combobox
        packageJComboBox.setModel(packageModel);
        packageJComboBox.setRenderer(new DataBoxRenderer());

        //
        final Object popup = packageJComboBox.getUI().getAccessibleChild(packageJComboBox, 0);

        //
        if (popup instanceof ComboPopup) {
            final JList jList = ((ComboPopup) popup).getList();
            jList.setFixedCellHeight(48);
            jList.setVisibleRowCount(8);
        }

        //
        templateJComboBox.setModel(templateModel);
        templateJComboBox.setRenderer(new TemplateBoxRenderer());

        // ComboBox set disabled by default
        worldJComboBox.setRenderer(new WorldBoxRenderer());
        worldJComboBox.setEnabled(false);

        // List disabled by default
        mapJList.setEnabled(false);
        mapJList.setCellRenderer(new WorldListRenderer());

        // Disabled by default
        finishJButton.setEnabled(false);
    }

    public World getWorld() {
        return world;
    }

    public WorldCell getMap() {
        return map;
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

        jPanel1 = new javax.swing.JPanel();
        packageJLabel = new javax.swing.JLabel();
        packageJComboBox = new javax.swing.JComboBox();
        templateJLabel = new javax.swing.JLabel();
        templateJComboBox = new javax.swing.JComboBox();
        worldJComboBox = new javax.swing.JComboBox();
        worldJLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        finishJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        cancelJButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        mapJList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Selecting Template");
        setMinimumSize(new java.awt.Dimension(237, 300));
        setResizable(false);

        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWidths = new int[] {0, 5, 0};
        jPanel1Layout.rowHeights = new int[] {0, 5, 0, 5, 0};
        jPanel1.setLayout(jPanel1Layout);

        packageJLabel.setText("Data Package:");
        packageJLabel.setMaximumSize(new java.awt.Dimension(84, 24));
        packageJLabel.setMinimumSize(new java.awt.Dimension(84, 24));
        packageJLabel.setPreferredSize(new java.awt.Dimension(84, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(packageJLabel, gridBagConstraints);

        packageJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        packageJComboBox.setMaximumSize(new java.awt.Dimension(128, 24));
        packageJComboBox.setMinimumSize(new java.awt.Dimension(128, 24));
        packageJComboBox.setPreferredSize(new java.awt.Dimension(128, 24));
        packageJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packageJComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(packageJComboBox, gridBagConstraints);

        templateJLabel.setText("Lab Template:");
        templateJLabel.setMaximumSize(new java.awt.Dimension(84, 24));
        templateJLabel.setMinimumSize(new java.awt.Dimension(84, 24));
        templateJLabel.setPreferredSize(new java.awt.Dimension(84, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(templateJLabel, gridBagConstraints);

        templateJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        templateJComboBox.setMaximumSize(new java.awt.Dimension(128, 24));
        templateJComboBox.setMinimumSize(new java.awt.Dimension(128, 24));
        templateJComboBox.setPreferredSize(new java.awt.Dimension(128, 24));
        templateJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                templateJComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(templateJComboBox, gridBagConstraints);

        worldJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        worldJComboBox.setMaximumSize(new java.awt.Dimension(128, 24));
        worldJComboBox.setMinimumSize(new java.awt.Dimension(128, 24));
        worldJComboBox.setPreferredSize(new java.awt.Dimension(128, 24));
        worldJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                worldJComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        jPanel1.add(worldJComboBox, gridBagConstraints);

        worldJLabel.setText("Chosen World:");
        worldJLabel.setMaximumSize(new java.awt.Dimension(84, 24));
        worldJLabel.setMinimumSize(new java.awt.Dimension(84, 24));
        worldJLabel.setPreferredSize(new java.awt.Dimension(84, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        jPanel1.add(worldJLabel, gridBagConstraints);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        jPanel2.add(filler1);

        finishJButton.setText("Start Lab");
        finishJButton.setToolTipText("Teh Lab will be started with a reproduction of the original map and world.");
        finishJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        finishJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        finishJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        finishJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishJButtonActionPerformed(evt);
            }
        });
        jPanel2.add(finishJButton);
        jPanel2.add(filler2);

        cancelJButton.setText("Cancel");
        cancelJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        cancelJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        cancelJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });
        jPanel2.add(cancelJButton);

        mapJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        mapJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        mapJList.setFixedCellHeight(24);
        mapJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                mapJListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(mapJList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed

        // Close this dialog, and set values to null
        world = null;
        map = null;

        //
        setVisible(false);
    }//GEN-LAST:event_cancelJButtonActionPerformed

    private void mapJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_mapJListValueChanged

        // TODO add your handling code here:
        map = (WorldCell) mapJList.getSelectedValue();

        // We can only finish when map and world exist
        finishJButton.setEnabled(map != null && world != null);
    }//GEN-LAST:event_mapJListValueChanged

    private void packageJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packageJComboBoxActionPerformed

        // TODO add your handling code here:
        final DefaultComboBoxModel worldModel = new DefaultComboBoxModel();

        // Data package option
        if (packageJComboBox.getSelectedItem() instanceof DataPackage) {

            // Grab the data package
            final DataPackage dataPackage = (DataPackage) packageJComboBox.getSelectedItem();

            // Populate the World Combobox
            final Object[] worlds = dataPackage.getType(World.class);

            // Iterate over the collection of worlds
            for (int i = 0; i < worlds.length; i++) {

                // Current world
                final World iworld = (World) worlds[i];

                // Add as element
                worldModel.addElement(iworld);
            }

            // Define out template model
            final DefaultComboBoxModel templateModel = new DefaultComboBoxModel();

            // Attempt grab
            final DataPackage selected = (DataPackage) packageJComboBox.getSelectedItem();

            // Null check
            if (selected != null) {

                // Find all templates
                final Object[] templates = selected.getType(WorldTemplate.class);

                // Iterate over templates
                for (int i = 0; i < templates.length; i++) {

                    // Current template
                    final WorldTemplate template = (WorldTemplate) templates[i];

                    // Add as element
                    templateModel.addElement(template);
                }
            }

            // Give to world ComboBox
            worldJComboBox.setModel(worldModel);

            // Enable map
            worldJComboBox.setEnabled(worldModel.getSize() > 0);
        } else if (packageJComboBox.getSelectedItem() instanceof String) {

            // The string should be 'No Association'
            // Populate the World Combobox from loose files
            final Object[] worlds = delegate.getLooseType(World.class);

            // Iterate over the collection of worlds
            for (int i = 0; i < worlds.length; i++) {

                // Current world
                final World iworld = (World) worlds[i];

                // Add as element
                worldModel.addElement(iworld);
            }

            // Give to world ComboBox
            worldJComboBox.setModel(worldModel);

            // Enable map
            worldJComboBox.setEnabled(worldModel.getSize() > 0);
        }
    }//GEN-LAST:event_packageJComboBoxActionPerformed

    private void worldJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_worldJComboBoxActionPerformed

        // List model
        final DefaultListModel listModel = new DefaultListModel();

        // Grab all the maps
        world = (World) worldJComboBox.getSelectedItem();

        //
        if (world != null) {

            // Grab the collection of maps
            final WorldCell[] maps = world.getCellList().toArray(new WorldCell[]{});

            // Iterate over the collection of maps
            for (int i = 0; i < maps.length; i++) {

                //
                final WorldCell imap = maps[i];

                // Add to map list
                listModel.addElement(imap);
            }

            // Give to mapJList
            mapJList.setModel(listModel);

            //
            mapJList.setEnabled(listModel.capacity() > 0);
        }
    }//GEN-LAST:event_worldJComboBoxActionPerformed

    private void templateJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_templateJComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_templateJComboBoxActionPerformed

    private void finishJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishJButtonActionPerformed

        // A Simple set invisible
        setVisible(false);
    }//GEN-LAST:event_finishJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton finishJButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList mapJList;
    private javax.swing.JComboBox packageJComboBox;
    private javax.swing.JLabel packageJLabel;
    private javax.swing.JComboBox templateJComboBox;
    private javax.swing.JLabel templateJLabel;
    private javax.swing.JComboBox worldJComboBox;
    private javax.swing.JLabel worldJLabel;
    // End of variables declaration//GEN-END:variables
}