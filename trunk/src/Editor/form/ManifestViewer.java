/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.form;

import io.resource.ResourceDelegate;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Robert A. Cherry
 */
public class ManifestViewer extends javax.swing.JDialog {

    // Variable Declaration
    // Java native classes
    private Document manifest;
    // Project Classes
    private ResourceDelegate delegate;
    // End of Variable Declaration

    public ManifestViewer(Window window, ResourceDelegate delegate, Document manifest, boolean modal) {
        super(window);
        setModal(modal);
        initComponents();

        //
        this.manifest = manifest;
        this.delegate = delegate;

        //
        init();
    }

    private void init() {

        // Create the table model
        final DefaultTableModel tableModel = new DefaultTableModel();

        // Create our column identifiers (the labels)
        final String[] columnIdentifiers = new String[]{"Display Name", "Editor Name", "Editor Id"};

        // Manifest must exist
        if (manifest != null) {

            // Grab the manifest
            final Node rootNode = manifest.getDocumentElement();

            // The children
            final NodeList children = rootNode.getChildNodes();

            // Out index
            int dataIndex = 0;

            // Out data vector
            final String[][] dataVector = new String[children.getLength() / 2][];

            // Iterate over the node list
            for (int i = 0; i < children.getLength(); i++) {

                // Grab current entry
                final Node node = children.item(i);

                // Do not take text nodes
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // Grab the referenceName from the attributes of this node
                    final String displayName = node.getAttributes().getNamedItem("displayName").getNodeValue();
                    final String referenceName = node.getAttributes().getNamedItem("referenceName").getNodeValue();
                    final String referenceID = node.getAttributes().getNamedItem("referenceID").getNodeValue();

                    // Set the data vector
                    dataVector[dataIndex] = new String[]{displayName, referenceName, referenceID};
                    dataIndex++;
                }
            }

            //
            manifestJLabel.setText("Manifest Entries: " + dataIndex);

            // Our calm colors
            final Color calm = new Color(29, 164, 8);
            final Color conflicted = new Color(243, 5, 5);

            //
            conflictedJLabel.setForeground(conflicted);
            conflictedJLabel.setFont(conflictedJLabel.getFont().deriveFont(Font.BOLD, conflictedJLabel.getFont().getSize()));
            passedJLabel.setForeground(calm);
            passedJLabel.setFont(passedJLabel.getFont().deriveFont(Font.BOLD, passedJLabel.getFont().getSize()));

            // Set the data vector and column indentifiers
            tableModel.setDataVector(dataVector, columnIdentifiers);
        }

        // Apply the table model and the data vector
        manifestJTable.setModel(tableModel);
    }

    private void manifestJTableCustomRenderer(Component component, TableCellRenderer renderer, int row, int col) {

        // Out calm colors
        final Color calm = new Color(29, 164, 8);
        final Color conflicted = new Color(243, 5, 5);

        // The information
        final String displayName = String.valueOf(manifestJTable.getValueAt(row, 0));
        final String referenceName = String.valueOf(manifestJTable.getValueAt(row, 1));
        final String referenceID = String.valueOf(manifestJTable.getValueAt(row, 2));

        // A resource is conflicted if the occurence of any of these three above forms of reference appear more than once or not at all
        final int count1 = delegate.isConflicted(ResourceDelegate.ID_EDITOR_DISPLAY, displayName) ? 1 : 0;
        final int count2 = delegate.isConflicted(ResourceDelegate.ID_EDITOR_REFERENCE, referenceID) ? 1 : 0;
        final int count3 = delegate.isConflicted(ResourceDelegate.ID_EDITOR_NAME, referenceName) ? 1 : 0;
        boolean conflict = ((count1 + count2 + count3) > 0) ? true : false;
                
                // Delegate updated to detect conflicts; so some degree. (I may make it more full proof in the future)
        component.setForeground(conflict ? conflicted : calm);
        component.setFont(conflict ? component.getFont().deriveFont(Font.BOLD, component.getFont().getSize()) : component.getFont());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonJPanel = new javax.swing.JPanel();
        conflictedJLabel = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        passedJLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        closeJButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        manifestJTable = new javax.swing.JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

                final Component component = super.prepareRenderer(renderer, row, col);

                // Do outside of this
                manifestJTableCustomRenderer(component, renderer, row, col);

                // Return the component
                return component;
            }
        };
        jPanel1 = new javax.swing.JPanel();
        manifestJLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Viewing Archive Manifest");
        setMinimumSize(new java.awt.Dimension(417, 330));
        setResizable(false);

        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));

        conflictedJLabel.setText("Conflicted");
        buttonJPanel.add(conflictedJLabel);
        buttonJPanel.add(filler2);

        passedJLabel.setText(" Passed");
        buttonJPanel.add(passedJLabel);
        buttonJPanel.add(filler1);

        closeJButton.setText("Close");
        closeJButton.setMaximumSize(new java.awt.Dimension(88, 23));
        closeJButton.setMinimumSize(new java.awt.Dimension(88, 23));
        closeJButton.setPreferredSize(new java.awt.Dimension(88, 23));
        closeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(closeJButton);

        manifestJTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(manifestJTable);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        manifestJLabel.setText("Manifest Entries: ");
        manifestJLabel.setMaximumSize(new java.awt.Dimension(132, 20));
        manifestJLabel.setMinimumSize(new java.awt.Dimension(132, 20));
        manifestJLabel.setPreferredSize(new java.awt.Dimension(132, 20));
        jPanel1.add(manifestJLabel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeJButtonActionPerformed

        // Close the Manifest Viewer
        setVisible(false);
    }//GEN-LAST:event_closeJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton closeJButton;
    private javax.swing.JLabel conflictedJLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel manifestJLabel;
    private javax.swing.JTable manifestJTable;
    private javax.swing.JLabel passedJLabel;
    // End of variables declaration//GEN-END:variables
}
