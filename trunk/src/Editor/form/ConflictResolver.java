/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.form;

import io.resource.DataRef;
import io.resource.ResourceDelegate;
import io.resource.ResourceReader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import Editor.renderer.DataTableCheckBoxRenderer;
import Editor.renderer.DataTableRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class ConflictResolver extends javax.swing.JDialog {

    // Variable Declaration
    // Project Classes
    private DataRef reference;
    private ResourceDelegate delegate;
    // Data Types
    private String tag;
    // End of Variable Declaration

    public ConflictResolver(Window window, ResourceDelegate delegate, DataRef reference, boolean modal) {
        super(window);
        setModal(modal);
        initComponents();

        // Set Values
        this.reference = reference;
        this.delegate = delegate;

        //
        init();
    }

    private void init() {

        //
        final Class closs = getClass();
        final Toolkit kit = Toolkit.getDefaultToolkit();

        //
        final ImageIcon icon = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-search16.png");

        //
        scanJButton.setIcon(icon);

        //
        final DefaultComboBoxModel boxModel = new DefaultComboBoxModel();

        // Fill the box model with these three things
        boxModel.addElement("Display Name");
        boxModel.addElement("Editor Id Tag");
        boxModel.addElement("Editor Name");

        // Set the box model
        tagJComboBox.setModel(boxModel);
        tagJComboBox.setSelectedIndex(0);

        //
        final DefaultTableModel model = new DefaultTableModel();

        // Apply the table model
        referenceJTable.setModel(model);
        referenceJTable.getTableHeader().setReorderingAllowed(false);

        // Add document listener
        replaceJField.getDocument().addDocumentListener(new DocumentListener() {
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
                        final String text = replaceJField.getText();

                        // Check for empty text
                        if (text.isEmpty()) {
                            performJButton.setEnabled(false);
                            replaceJField.setForeground(Color.RED);
                        }

                        if (tag.equalsIgnoreCase("Display Name")) {

                            // Check if this is a valid display name
                            if (1 == 1) {

                                // Enable the ability to perform the change
                                performJButton.setEnabled(true);

                                // Change the foreground
                                replaceJField.setForeground(Color.BLACK);
                            } else {

                                //
                                performJButton.setEnabled(false);

                                // Also change the fields foreground to RED
                                replaceJField.setForeground(Color.RED);
                            }
                        } else if (tag.equalsIgnoreCase("Editor Id Tag")) {

                            // Check if this is a valid editor id
                            if (1 == 1) {

                                // Enable the ability to perform the change
                                performJButton.setEnabled(true);

                                // Change the foreground
                                replaceJField.setForeground(Color.BLACK);
                            } else {

                                //
                                performJButton.setEnabled(false);

                                // Also change the fields foreground to RED
                                replaceJField.setForeground(Color.RED);
                            }
                        } else if (tag.equalsIgnoreCase("Editor Name")) {

                            //Check if this is a valid editor name
                            if (1 == 1) {

                                // Enable the ability to perform the change
                                performJButton.setEnabled(true);

                                // Change the foreground
                                replaceJField.setForeground(Color.BLACK);
                            } else {

                                //
                                performJButton.setEnabled(false);

                                // Also change the fields foreground to RED
                                replaceJField.setForeground(Color.RED);
                            }
                        }
                    }
                });
            }
        });

        if (reference != null) {

            // Change title
            setTitle("Resolving conflicts for: " + reference.getDisplayName() + "@" + reference.getEditorId());

            //
            String string = null;
            switch (tag) {
                case "Display Name":
                    string = reference.getDisplayName();
                    break;
                case "Editor Id Tag":
                    string = reference.getEditorId();
                    break;
                case "Editor Name":
                    string = reference.getEditorName();
                    break;
            }

            //
            replaceJField.requestFocus();
            replaceJField.setText(string);
            replaceJField.selectAll();
        }
    }

    private void referenceJTableCustomRenderer(Component component, TableCellRenderer renderer, int row, int col) {
        // Do nothing for now.
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        referenceJTable = new javax.swing.JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

                final Component component = super.prepareRenderer(renderer, row, col);

                // Do outside of this
                referenceJTableCustomRenderer(component, renderer, row, col);

                // Return the component
                return component;
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        tagJComboBox = new javax.swing.JComboBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        jLabel3 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        replaceJField = new javax.swing.JTextField();
        buttonJPanel = new javax.swing.JPanel();
        scanJButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        performJButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        closeJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Resolving Conflicts in Resources");

        referenceJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        referenceJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                referenceJTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(referenceJTable);

        jLabel1.setText("Resolve A conflict here by selecting resources and identification tags to change");
        jLabel1.setEnabled(false);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setText("Replace Instances of: ");
        jLabel2.setMaximumSize(new java.awt.Dimension(108, 22));
        jLabel2.setMinimumSize(new java.awt.Dimension(108, 22));
        jLabel2.setPreferredSize(new java.awt.Dimension(108, 22));
        jPanel1.add(jLabel2);
        jPanel1.add(filler1);

        tagJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        tagJComboBox.setMaximumSize(new java.awt.Dimension(128, 22));
        tagJComboBox.setMinimumSize(new java.awt.Dimension(128, 22));
        tagJComboBox.setPreferredSize(new java.awt.Dimension(128, 22));
        tagJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tagJComboBoxActionPerformed(evt);
            }
        });
        jPanel1.add(tagJComboBox);
        jPanel1.add(filler2);

        jLabel3.setText("  with:");
        jLabel3.setMaximumSize(new java.awt.Dimension(30, 22));
        jLabel3.setMinimumSize(new java.awt.Dimension(30, 22));
        jLabel3.setPreferredSize(new java.awt.Dimension(30, 22));
        jPanel1.add(jLabel3);
        jPanel1.add(filler3);

        replaceJField.setMaximumSize(new java.awt.Dimension(128, 22));
        replaceJField.setMinimumSize(new java.awt.Dimension(128, 22));
        replaceJField.setPreferredSize(new java.awt.Dimension(128, 22));
        jPanel1.add(replaceJField);

        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));

        scanJButton.setText("Confliction Scan");
        scanJButton.setMaximumSize(new java.awt.Dimension(140, 26));
        scanJButton.setMinimumSize(new java.awt.Dimension(140, 26));
        scanJButton.setPreferredSize(new java.awt.Dimension(140, 26));
        scanJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(scanJButton);
        buttonJPanel.add(filler5);

        performJButton.setText("Perform");
        performJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        performJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        performJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        performJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                performJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(performJButton);
        buttonJPanel.add(filler4);

        closeJButton.setText("Cancel");
        closeJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        closeJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        closeJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        closeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(closeJButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void tagJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tagJComboBoxActionPerformed

        // Change the tag
        tag = String.valueOf(tagJComboBox.getSelectedItem());

        if (reference != null) {

            //
            String string = null;
            switch (tag) {
                case "Display Name":
                    string = reference.getDisplayName();
                    break;
                case "Editor Id Tag":
                    string = reference.getEditorId();
                    break;
                case "Editor Name":
                    string = reference.getEditorName();
                    break;
            }

            //
            replaceJField.requestFocus();
            replaceJField.setText(string);
            replaceJField.selectAll();
        }
    }//GEN-LAST:event_tagJComboBoxActionPerformed

    private void scanJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanJButtonActionPerformed

        // Data Table
        final DefaultTableModel model = new DefaultTableModel();

        // Find all conflictions with this reference (You could change this to a database search) (Wait until I implement Database Control though :( )
        final DataRef[] conflicted = delegate.detectConflictionsWith(reference);

        // Our column names
        final String[] columnNames = {"Perform Change", "Conflicted Reference"};

        //
        final Object[][] dataVector = new Object[conflicted.length][2];

        // Iterate over the set of plugins
        for (int i = 0; i < dataVector.length; i++) {

            // Grab the matching resource dataPackage
            final DataRef conflict = conflicted[i];

            //
            dataVector[i][0] = false;
            dataVector[i][1] = conflict;
        }

        // Apply the data vector
        model.setDataVector(dataVector, columnNames);

        // Fixed column width for checkbox column
        final int checkWidth = 128;
        final int referenceWidth = 200;

        //
        referenceJTable.setModel(model);

        // Change the renderer for the first column to solve for JCheckBox
        final TableColumn checkColumn = referenceJTable.getColumnModel().getColumn(0);
        checkColumn.setCellRenderer(new DataTableCheckBoxRenderer());
        checkColumn.setMaxWidth(checkWidth);
        checkColumn.setPreferredWidth(checkWidth);
        checkColumn.setMinWidth(checkWidth);

        //
        final TableColumn referenceColumn = referenceJTable.getColumnModel().getColumn(1);
        referenceColumn.setCellRenderer(new DataTableRenderer());
        referenceColumn.setMaxWidth(referenceWidth);
        referenceColumn.setPreferredWidth(referenceWidth);
        referenceColumn.setMinWidth(referenceWidth);
    }//GEN-LAST:event_scanJButtonActionPerformed

    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeJButtonActionPerformed

        // Close this Dialog
        setVisible(false);
    }//GEN-LAST:event_closeJButtonActionPerformed

    private void referenceJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_referenceJTableMouseClicked

        // Grab the position
        final Point position = evt.getPoint();

        // Grab the row and column
        final int row = referenceJTable.rowAtPoint(position);
        final int col = referenceJTable.columnAtPoint(position);

        // Row must exist
        if (row > -1 && col > -1) {

            // Only the first column
            if (col == 0) {

                try {

                    // Get the row and column
                    final Boolean bool = Boolean.parseBoolean(String.valueOf(referenceJTable.getValueAt(row, col)));

                    // Change to opposite
                    referenceJTable.setValueAt(!bool, row, col);
                } catch (NumberFormatException nfe) {
                    //
                }
            }
        }
    }//GEN-LAST:event_referenceJTableMouseClicked

    private void performJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_performJButtonActionPerformed
        // Search through all affected files and replace the tag -- soon
    }//GEN-LAST:event_performJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton closeJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton performJButton;
    private javax.swing.JTable referenceJTable;
    private javax.swing.JTextField replaceJField;
    private javax.swing.JButton scanJButton;
    private javax.swing.JComboBox tagJComboBox;
    // End of variables declaration//GEN-END:variables
}
