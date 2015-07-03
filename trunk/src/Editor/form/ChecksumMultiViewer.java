/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.form;

import io.resource.ResourceReader;
import io.util.FileUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class ChecksumMultiViewer extends javax.swing.JDialog {

    // Variable Declaration
    // Java native classes
    private File folder;
    // Project Classes
    // Data Types
    private String flavor;
    private int[] sect = {0, 0};
    // End of Variable Declaration

    public ChecksumMultiViewer(Window window, File folder, String flavor, boolean modal) {
        super(window);
        setModal(modal);
        initComponents();

        //
        this.folder = folder;
        this.flavor = flavor;

        //
        init();
    }

    private void init() {

        // Create the table model
        final DefaultTableModel tableModel = new DefaultTableModel();

        // Create our column identifiers (the labels)
        final String[] columnIdentifiers = new String[]{"File Name", "Checksum", "Absolute Path"};

        // Manifest must exist
        if (folder != null) {

            // If the directory contains alot of memory it will take a while.
            final File[] fileArray = FileUtils.getDirectoryContents(folder);

            //
            final int dataMaxIndex = fileArray.length;

            // Out data vector
            final String[][] dataVector = new String[dataMaxIndex][];

            //
            setup(fileArray, dataVector);

            //
            manifestJLabel.setText("Folder Contents: " + folder.getName());

            // Set the data vector and column indentifiers
            tableModel.setDataVector(dataVector, columnIdentifiers);
        }

        // Apply the table model and the data vector
        checksumJTable.setModel(tableModel);

        try {

            // Grab source folder
            final Class closs = getClass();

            //
            copyJButton.setIcon(ResourceReader.readClassPathIcon(closs,"/icons/icon-clipboard24.png"));
        } catch (NullPointerException ioe) {
            //
        }
    }

    private void setup(File[] files, String[][] dataVector) {

        //
        for (int i = 0; i < files.length; i++) {

            //
            final File file = files[i];

            //
            final String fileName = file.getName();
            final String filePath = file.getAbsolutePath();
            String fileSum;

            if (file.isFile()) {

                //
                fileSum = generate(file, flavor);
            } else {

                //
                fileSum = "*Folder";
            }

            //
            dataVector[i] = new String[]{fileName, fileSum, filePath};
        }
    }

    private void checksumJTableCustomRenderer(Component component, TableCellRenderer renderer, int row, int col) {

        // Out calm colors
        final Color calm = new Color(29, 164, 8);

        // The information
        final String checksum = String.valueOf(checksumJTable.getValueAt(row, 1));

        // A resource is conflicted if the occurence of any of these three above forms of reference appear more than once or not at all
        final boolean isFolder = checksum.equals("*Folder") ? true : false;

        // Folders are bolded and blue
        component.setForeground(isFolder ? Color.GRAY : calm);
        //component.setFont(isFolder ? component.getFont().deriveFont(Font.BOLD, component.getFont().getSize()) : component.getFont());
    }

    private String generate(File file, String flavor) {

        //
        if (file != null) {
            try {
                return FileUtils.generateChecksum(file.getAbsolutePath(), flavor);
            } catch (Exception ex) {
                Logger.getLogger(ChecksumViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //
        return null;
    }

    private void copy(String string) {

        // No checksum no copies
        if (string == null || string.equals("")) {

            //
            JOptionPane.showMessageDialog(this, "There is nothing to copy.");
            return;
        }

        //
        if (string.length() > 0) {

            // Copy to clipboard and show dialog box
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(string), null);

            // Show a quick message
            JOptionPane.showMessageDialog(this, "Value successfully copied to clipboard.");
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

        buttonJPanel = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        copyJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        closeJButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        checksumJTable = new javax.swing.JTable() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

                final Component component = super.prepareRenderer(renderer, row, col);

                // Do outside of this
                checksumJTableCustomRenderer(component, renderer, row, col);

                // Return the component
                return component;
            }
        };
        jPanel1 = new javax.swing.JPanel();
        manifestJLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Viewing Multiple Checksums");
        setMinimumSize(new java.awt.Dimension(417, 330));

        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));
        buttonJPanel.add(filler1);

        copyJButton.setToolTipText("Copy to Clipboard");
        copyJButton.setContentAreaFilled(false);
        copyJButton.setFocusPainted(false);
        copyJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        copyJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        copyJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        copyJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(copyJButton);
        buttonJPanel.add(filler2);

        closeJButton.setText("Close");
        closeJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        closeJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        closeJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        closeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(closeJButton);

        checksumJTable.setModel(new javax.swing.table.DefaultTableModel(
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
        checksumJTable.setToolTipText("Double-Click a Value to Copy to Clipboard");
        checksumJTable.setFillsViewportHeight(true);
        checksumJTable.setRowSelectionAllowed(false);
        checksumJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checksumJTableMouseClicked(evt);
            }
        });
        checksumJTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                checksumJTableMouseMoved(evt);
            }
        });
        jScrollPane1.setViewportView(checksumJTable);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        manifestJLabel.setText("Folder Contents: ");
        manifestJLabel.setToolTipText("Contents of Folder");
        manifestJLabel.setMaximumSize(new java.awt.Dimension(208, 20));
        manifestJLabel.setMinimumSize(new java.awt.Dimension(208, 20));
        manifestJLabel.setPreferredSize(new java.awt.Dimension(208, 20));
        jPanel1.add(manifestJLabel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
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

    private void copyJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyJButtonActionPerformed

        //
        final int row = checksumJTable.getSelectedRow();
        final int col = checksumJTable.getSelectedColumn();

        //
        if (row > -1 && col > -1) {

            //
            final String checksum = String.valueOf(checksumJTable.getValueAt(row, col));

            // Copy text to clipboard
            copy(checksum);
        }
    }//GEN-LAST:event_copyJButtonActionPerformed

    private void checksumJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checksumJTableMouseClicked

        // TODO add your handling code here:
        final int clickCount = evt.getClickCount();

        // If you double clicked a value
        if (clickCount == 2) {

            //
            final int row = checksumJTable.rowAtPoint(evt.getPoint());
            final int col = checksumJTable.columnAtPoint(evt.getPoint());

            //
            final String string = String.valueOf(checksumJTable.getValueAt(row, col));

            // Simple enough
            copy(string);
        }
    }//GEN-LAST:event_checksumJTableMouseClicked

    private void checksumJTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checksumJTableMouseMoved

        // Mouse position
        final Point point = evt.getPoint();

        // 
        final int row = checksumJTable.rowAtPoint(point);
        final int col = checksumJTable.columnAtPoint(point);
        final Object value = checksumJTable.getValueAt(row, col);

        // Make sure we dont generate for every move of the mouse
        if (row != sect[0] || col != sect[1]) {
            
            // Efficeincy check
            sect = new int[]{row, col};
            
            // Now change
            checksumJTable.setToolTipText("Double-click to copy ('" + value + "') to clipboard");
        }
    }//GEN-LAST:event_checksumJTableMouseMoved
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JTable checksumJTable;
    private javax.swing.JButton closeJButton;
    private javax.swing.JButton copyJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel manifestJLabel;
    // End of variables declaration//GEN-END:variables
}
