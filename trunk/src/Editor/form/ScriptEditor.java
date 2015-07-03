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
import io.GeneralFileFilter;
import io.resource.DataPackage;
import io.resource.ResourceDelegate;
import io.script.ScriptReader;
import core.world.WorldScript;
import io.util.FileSearch;
import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import Editor.renderer.DataBoxRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class ScriptEditor extends javax.swing.JDialog {

    // Variable Declaration
    // Project Classes
    private DataPackage dataPackage;
    private FaustEditor editor;
    private ResourceDelegate delegate;
    private WorldScript resource;
    // Data Types
    private boolean edit;
    // End of Variable Declaration

    public ScriptEditor(FaustEditor editor, ResourceDelegate delegate, WorldScript resource, boolean modal) {

        // Call to super
        super(editor, modal);
        initComponents();

        // Set values equal
        this.editor = editor;
        this.delegate = delegate;
        this.resource = resource;

        // Initialize my commands
        init();
    }

    private void init() {

        // Box Model setup
        final DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
        comboModel.addElement(ResourceDelegate.UNPACKAGED_STATEMENT);

        // Apply the model and other options to the JComboBox
        pluginJComboBox.setRenderer(new DataBoxRenderer());
        pluginJComboBox.setModel(comboModel);

        // Check to see if the world even exists
        if (resource == null) {

            // Creating a default map
            resource = new WorldScript(null, "", "", "", "", "");

            // Change the title
            setTitle("Script Editor: New Script");
        } else {
            setTitle("Script Editor: " + resource.getDisplayName());

            // Label as 'loose' by default
            pluginJComboBox.setSelectedItem(ResourceDelegate.UNPACKAGED_STATEMENT);
        }

        // Do the dataPackage check
        doPluginCheck(comboModel);

        // Setup controls
        setup();
    }

    private void finish() {

        //
        if (validJCheckBox.isSelected()) {

            // Apply these changes to the fMap
            resource.setReferenceID(idJField.getText());
            resource.setReferenceName(nameJField.getText());
            resource.setDisplayName(displayJField.getText());

            // Ensure attributes map is up to date
            resource.updateAttributes();
            
            resource.validate();
            
            //
            delegate.addResource(resource);

            //
            setVisible(false);
        }
    }

    /*
     *  Quick method to copy across similar classes to ensure all the dataPackage related information is up to date.
     */
    private void doPluginCheck(DefaultComboBoxModel model) {

        // Plugin check
        final String packageID = resource.getPackageId();

        // Grab all the plugins
        final DataPackage[] plugins = delegate.getDataPackages();

        // Iterate over the list of ResourcePlugins
        for (int i = 0; i < plugins.length; i++) {

            // Grab the current ResourcePlugin
            final DataPackage iPackage = plugins[i];

            // Add to the model
            model.addElement(iPackage);

            // If and only if dataPackage exists
            if (packageID != null) {

                // Quick check
                if (iPackage.getReferenceId().equalsIgnoreCase(packageID)) {

                    // Assign
                    dataPackage = iPackage;
                }
            }
        }

        // Quick check to see if the Resource belongs to a specific dataPackage, if not label as 'loose'
        if (dataPackage != null) {
            pluginJComboBox.setSelectedItem(dataPackage);

            // Disable the ComboBox if this animation existed before the dialog was opened
            pluginJComboBox.setEditable(false);
            pluginJComboBox.setEnabled(false);
            edit = true;
        } else {

            // No package does not nessecarily mean no edit
            pluginJComboBox.setSelectedItem(ResourceDelegate.UNPACKAGED_STATEMENT);

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
        dummyJPanel1 = new javax.swing.JPanel();
        scriptJPanel = new javax.swing.JPanel();
        locationJLabel = new javax.swing.JLabel();
        locationJField = new javax.swing.JTextField();
        scriptJButton = new javax.swing.JButton();
        sourceJLabel = new javax.swing.JLabel();
        dummyJPanel2 = new javax.swing.JPanel();
        basicJPanel = new javax.swing.JPanel();
        nameJField = new javax.swing.JTextField();
        nameJLabel = new javax.swing.JLabel();
        editorJLabel = new javax.swing.JLabel();
        idJField = new javax.swing.JTextField();
        referenceJLabel = new javax.swing.JLabel();
        displayJField = new javax.swing.JTextField();
        pluginJComboBox = new javax.swing.JComboBox();
        pluginJLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("JavaScript Importing");
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
        jPanel1Layout.rowHeights = new int[] {0, 4, 0};
        scriptJPanel.setLayout(jPanel1Layout);

        locationJLabel.setText("Script Location:");
        locationJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        locationJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        locationJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        locationJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        scriptJPanel.add(locationJLabel, gridBagConstraints);

        locationJField.setEnabled(false);
        locationJField.setMaximumSize(new java.awt.Dimension(136, 22));
        locationJField.setMinimumSize(new java.awt.Dimension(136, 22));
        locationJField.setPreferredSize(new java.awt.Dimension(136, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        scriptJPanel.add(locationJField, gridBagConstraints);

        scriptJButton.setText("Source File");
        scriptJButton.setMaximumSize(new java.awt.Dimension(136, 22));
        scriptJButton.setMinimumSize(new java.awt.Dimension(136, 22));
        scriptJButton.setPreferredSize(new java.awt.Dimension(136, 22));
        scriptJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scriptJButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        scriptJPanel.add(scriptJButton, gridBagConstraints);

        sourceJLabel.setText("Change Script:");
        sourceJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sourceJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        sourceJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        sourceJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        scriptJPanel.add(sourceJLabel, gridBagConstraints);

        javax.swing.GroupLayout dummyJPanel1Layout = new javax.swing.GroupLayout(dummyJPanel1);
        dummyJPanel1.setLayout(dummyJPanel1Layout);
        dummyJPanel1Layout.setHorizontalGroup(
            dummyJPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 249, Short.MAX_VALUE)
            .addGroup(dummyJPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dummyJPanel1Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scriptJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(11, Short.MAX_VALUE)))
        );
        dummyJPanel1Layout.setVerticalGroup(
            dummyJPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 131, Short.MAX_VALUE)
            .addGroup(dummyJPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(dummyJPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scriptJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(72, Short.MAX_VALUE)))
        );

        mainJTabbedPane.addTab("Script Settings", dummyJPanel1);

        basicJPanel.setMaximumSize(new java.awt.Dimension(224, 66));
        java.awt.GridBagLayout basicJPanelLayout = new java.awt.GridBagLayout();
        basicJPanelLayout.columnWidths = new int[] {0, 4, 0};
        basicJPanelLayout.rowHeights = new int[] {0, 4, 0, 4, 0, 4, 0};
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

        pluginJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        pluginJComboBox.setMaximumSize(new java.awt.Dimension(136, 22));
        pluginJComboBox.setMinimumSize(new java.awt.Dimension(136, 22));
        pluginJComboBox.setPreferredSize(new java.awt.Dimension(136, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        basicJPanel.add(pluginJComboBox, gridBagConstraints);

        pluginJLabel.setText("Part of Package:");
        pluginJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        pluginJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        pluginJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        basicJPanel.add(pluginJLabel, gridBagConstraints);

        javax.swing.GroupLayout dummyJPanel2Layout = new javax.swing.GroupLayout(dummyJPanel2);
        dummyJPanel2.setLayout(dummyJPanel2Layout);
        dummyJPanel2Layout.setHorizontalGroup(
            dummyJPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dummyJPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(basicJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );
        dummyJPanel2Layout.setVerticalGroup(
            dummyJPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dummyJPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(basicJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        mainJTabbedPane.addTab("Editor Settings", dummyJPanel2);

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
                .addComponent(mainJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void scriptJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scriptJButtonActionPerformed

        // Open the script selector// TODO add your handling code here:// Search for file:
        final GeneralFileFilter filter = new GeneralFileFilter(new String[]{"js", "JS"}, "Javascript");

        // Create a File Chooser
        final JFileChooser chooser = new JFileChooser();

        // Will be changed later
        chooser.setCurrentDirectory(new File(delegate.getDataDirectory()));
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // If you selected a File and didnt click cancel
        final int value = chooser.showDialog(this, "Load Script");

        if (value == JFileChooser.APPROVE_OPTION) {

            // Selected Javascript File
            final File file = chooser.getSelectedFile();

            if (filter.accept(file)) {

                // Read the script in from the script reader
                resource.setInterface(ScriptReader.read(file.getAbsolutePath(), null));
                resource.setFile(file);

                //
                locationJField.setText(file.getAbsolutePath());
            }
        }
    }//GEN-LAST:event_scriptJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel basicJPanel;
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JTextField displayJField;
    private javax.swing.JPanel dummyJPanel1;
    private javax.swing.JPanel dummyJPanel2;
    private javax.swing.JLabel editorJLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton finishJButton;
    private javax.swing.JTextField idJField;
    private javax.swing.JTextField locationJField;
    private javax.swing.JLabel locationJLabel;
    private javax.swing.JTabbedPane mainJTabbedPane;
    private javax.swing.JTextField nameJField;
    private javax.swing.JLabel nameJLabel;
    private javax.swing.JComboBox pluginJComboBox;
    private javax.swing.JLabel pluginJLabel;
    private javax.swing.JLabel referenceJLabel;
    private javax.swing.JButton scriptJButton;
    private javax.swing.JPanel scriptJPanel;
    private javax.swing.JLabel sourceJLabel;
    private javax.swing.JCheckBox validJCheckBox;
    // End of variables declaration//GEN-END:variables
}
