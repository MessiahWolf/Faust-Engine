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
import core.world.RoomLayer;
import core.world.Room;
import io.resource.ResourceDelegate;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Robert A. Cherry
 */
public class LayerEditor extends javax.swing.JDialog {

    // Variable Declaration
    // Project Classes
    private FaustEditor editor;
    private RoomLayer layer;
    private Room worldCell;
    private ResourceDelegate delegate;
    // Data Types
    private boolean edit;
    private int layerWidth;
    private int layerHeight;
    // End of Variable Declaration

    public LayerEditor(FaustEditor editor, ResourceDelegate delegate, Room worldCell, RoomLayer layer, boolean edit, boolean modal) {

        // Call to super
        super(editor, modal);
        initComponents();

        // Set values
        this.editor = editor;
        this.delegate = delegate;
        this.worldCell = worldCell;
        this.layer = layer;

        // -^
        this.edit = edit;

        // Initialize my commands
        init();
    }

    private void init() {

        // Default values
        if (worldCell == null) {
            
            // Default values
            layerWidth = worldCell.getWidth();
            layerHeight = worldCell.getHeight();
        } else {
            
            // Otherwise grab directly from layer
            layerWidth = layer.getWidth();
            layerHeight = layer.getHeight();
        }

        // Create new box model
        final DefaultComboBoxModel boxModel = new DefaultComboBoxModel();

        // Grab all the worlds
        final Object[] worldCells = delegate.getType(Room.class);

        // Iterate
        for (int i = 0; i < worldCells.length; i++) {

            // Cast to an FWorld
            final Room current = (Room) worldCells[i];

            // Add Element
            boxModel.addElement(current.getDisplayName());
        }

        // Set the box model
        cellJComboBox.setModel(boxModel);

        // Setup the controls
        setup();
    }

    private void finish() {

        // validJCheckBox is always update to date
        if (validJCheckBox.isSelected()) {

            // Apply these changes to the fMap
            layer.setReferenceID(idJField.getText());
            layer.setReferenceName(nameJField.getText());
            layer.setDisplayName(displayJField.getText());

            // Ensure attributes map is up to date
            layer.updateAttributes();

            // Map must exist
            if (worldCell != null) {
                worldCell.addLayer(layer);
            }

            // We are done.
            setVisible(false);
        }
    }

    private void setup() {

        //
        widthJField.setValue(layerWidth);
        widthJField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("value")) {
                    String value = String.valueOf(evt.getNewValue());

                    try {

                        //
                        int newValue = Integer.parseInt(value);

                        // Apply change
                        layerWidth = newValue;
                    } catch (NumberFormatException nfe) {
                        layerWidth = 640;
                    }
                }
            }
        });
        heightJField.setValue(layerHeight);
        heightJField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("value")) {
                    String value = String.valueOf(evt.getNewValue());

                    try {

                        //
                        int newValue = Integer.parseInt(value);

                        // Apply change
                        layerHeight = newValue;
                    } catch (NumberFormatException nfe) {
                        layerHeight = 480;
                    }
                }
            }
        });

        // Copy from here
        // Copy from here
        final Color validColor = new Color(46, 164, 4);
        final Color errorColor = new Color(255, 32, 32);
        final Color conflictColor = new Color(255, 180, 0);

        displayJField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLabel(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLabel(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLabel(e);
            }

            private void updateLabel(DocumentEvent e) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        // Check the text at each button press
                        final String text = displayJField.getText();

                        // If and only if editing
                        // Check if this is a valid resource name
                        if (worldCell.isValidLayerName(text)) {

                            // When valid change the foreground to Green
                            displayJField.setForeground(validColor);

                            //
                            validJCheckBox.setSelected(true);
                        } else {

                            // Otherwise this object is not valid overall
                            validJCheckBox.setSelected(false);

                            // Also change the fields foreground to RED
                            displayJField.setForeground(errorColor);
                        }
                    }
                });
            }
        });

        // Disable if editting the layer instead of creating a fresh one
        if (edit == true) {

            // Set Auto Enabled if this is an existing fMap -- Do not just write, overwrite (meaning destroy the previous copy)
            validJCheckBox.setSelected(true);
        }

        // Adjust editor JField
        idJField.setText(worldCell.getReferenceID());
        displayJField.setText(layer.getDisplayName());
        nameJField.setText(worldCell.getReferenceName());
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
        roomJPanel = new javax.swing.JPanel();
        heightJLabel = new javax.swing.JLabel();
        widthJLabel = new javax.swing.JLabel();
        cellJComboBox = new javax.swing.JComboBox();
        roomJLabel = new javax.swing.JLabel();
        heightJField = new javax.swing.JFormattedTextField();
        widthJField = new javax.swing.JFormattedTextField();
        basicJPanel = new javax.swing.JPanel();
        nameJField = new javax.swing.JTextField();
        nameJLabel = new javax.swing.JLabel();
        editorJLabel = new javax.swing.JLabel();
        idJField = new javax.swing.JTextField();
        referenceJLabel = new javax.swing.JLabel();
        displayJField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cell Layer Editing");
        setMinimumSize(new java.awt.Dimension(278, 205));

        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));

        validJCheckBox.setForeground(new java.awt.Color(60, 60, 60));
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
        jPanel1Layout.rowHeights = new int[] {0, 4, 0, 4, 0};
        roomJPanel.setLayout(jPanel1Layout);

        heightJLabel.setText("Layer Height:");
        heightJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        heightJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        heightJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        roomJPanel.add(heightJLabel, gridBagConstraints);

        widthJLabel.setText("Layer Width:");
        widthJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        widthJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        widthJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        roomJPanel.add(widthJLabel, gridBagConstraints);

        cellJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cellJComboBox.setEnabled(false);
        cellJComboBox.setMaximumSize(new java.awt.Dimension(136, 22));
        cellJComboBox.setMinimumSize(new java.awt.Dimension(136, 22));
        cellJComboBox.setPreferredSize(new java.awt.Dimension(136, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        roomJPanel.add(cellJComboBox, gridBagConstraints);

        roomJLabel.setText("Part of Cell:");
        roomJLabel.setEnabled(false);
        roomJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        roomJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        roomJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        roomJPanel.add(roomJLabel, gridBagConstraints);

        heightJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        heightJField.setMaximumSize(new java.awt.Dimension(136, 22));
        heightJField.setMinimumSize(new java.awt.Dimension(136, 22));
        heightJField.setPreferredSize(new java.awt.Dimension(136, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        roomJPanel.add(heightJField, gridBagConstraints);

        widthJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        widthJField.setMaximumSize(new java.awt.Dimension(136, 22));
        widthJField.setMinimumSize(new java.awt.Dimension(136, 22));
        widthJField.setPreferredSize(new java.awt.Dimension(136, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        roomJPanel.add(widthJField, gridBagConstraints);

        mainJTabbedPane.addTab("Layer Settings", roomJPanel);

        basicJPanel.setMaximumSize(new java.awt.Dimension(224, 66));
        java.awt.GridBagLayout basicJPanelLayout = new java.awt.GridBagLayout();
        basicJPanelLayout.columnWidths = new int[] {0, 4, 0};
        basicJPanelLayout.rowHeights = new int[] {0, 4, 0, 4, 0};
        basicJPanel.setLayout(basicJPanelLayout);

        nameJField.setEditable(false);
        nameJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        nameJField.setToolTipText("Layers are not savable and so the editor name cannot be changed");
        nameJField.setEnabled(false);
        nameJField.setMaximumSize(new java.awt.Dimension(136, 22));
        nameJField.setMinimumSize(new java.awt.Dimension(136, 22));
        nameJField.setPreferredSize(new java.awt.Dimension(136, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        basicJPanel.add(nameJField, gridBagConstraints);

        nameJLabel.setText("Editor Name:");
        nameJLabel.setEnabled(false);
        nameJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        nameJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        nameJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        basicJPanel.add(nameJLabel, gridBagConstraints);

        editorJLabel.setText("Editor Id Tag:");
        editorJLabel.setEnabled(false);
        editorJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        editorJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        editorJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        basicJPanel.add(editorJLabel, gridBagConstraints);

        idJField.setEditable(false);
        idJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        idJField.setToolTipText("Layers are not savable and so the id cannot be changed");
        idJField.setEnabled(false);
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
        
        // Apply changes to this Layer
        finish();
    }//GEN-LAST:event_finishJButtonActionPerformed

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        
        // Cancel changes
        setVisible(false);
    }//GEN-LAST:event_cancelJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel basicJPanel;
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JComboBox cellJComboBox;
    private javax.swing.JTextField displayJField;
    private javax.swing.JLabel editorJLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton finishJButton;
    private javax.swing.JFormattedTextField heightJField;
    private javax.swing.JLabel heightJLabel;
    private javax.swing.JTextField idJField;
    private javax.swing.JTabbedPane mainJTabbedPane;
    private javax.swing.JTextField nameJField;
    private javax.swing.JLabel nameJLabel;
    private javax.swing.JLabel referenceJLabel;
    private javax.swing.JLabel roomJLabel;
    private javax.swing.JPanel roomJPanel;
    private javax.swing.JCheckBox validJCheckBox;
    private javax.swing.JFormattedTextField widthJField;
    private javax.swing.JLabel widthJLabel;
    // End of variables declaration//GEN-END:variables
}
