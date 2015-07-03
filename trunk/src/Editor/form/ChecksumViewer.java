/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.form;

import core.world.World;
import core.world.WorldCell;
import io.resource.DataPackage;
import io.resource.ResourceDelegate;
import io.resource.ResourceReader;
import io.util.FileSearch;
import io.util.FileUtils;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import Editor.renderer.ChecksumListRenderer;
import Editor.transfer.TransferableNode;

/**
 *
 * @author Robert A. Cherry
 */
public class ChecksumViewer extends javax.swing.JDialog implements DropTargetListener {

    // Variable Declaration
    // Java Native Classes
    private File file;
    // Project Classes
    private ResourceDelegate delegate;
    // Data Types
    private String checksum;
    private String flavor = "MD5";
    // End of Variable Declaration

    public ChecksumViewer(Window window, ResourceDelegate delegate, boolean modal) {
        super(window);
        setModal(modal);
        initComponents();

        //
        this.delegate = delegate;

        //
        init();
    }

    private void init() {

        //
        setTitle("Checksum Generator");

        // Disabling buttons until we need them active
        generateJButton.setEnabled(false);
        copyJButton.setEnabled(false);

        //
        final DefaultListModel model = new DefaultListModel();
        model.addElement("MD5");
        model.addElement("SHA-1");

        //
        flavorJList.setModel(model);
        flavorJList.setCellRenderer(new ChecksumListRenderer());
        flavorJList.setSelectedValue(flavor, true);

        // Grab source folder
        final Class closs = getClass();

        // @icon-time :D
        fileJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/icons/icon-open16.png"));

        //
        final DropTarget drop = new DropTarget(this, DnDConstants.ACTION_MOVE, this, true);
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

    private void copy() {

        // No checksum no copies
        if (checksum == null || checksum.equals("")) {

            //
            JOptionPane.showMessageDialog(this, "There is nothing to copy.");
            return;
        }

        //
        if (checksum.length() > 0) {

            // Copy to clipboard and show dialog box
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(checksum), null);

            // Show a quick message
            JOptionPane.showMessageDialog(this, "Checksum successfully copied to clipboard.");
        }
    }

    private void updateTextField(File file, String checksum) {

        // Don't process
        if (file == null || flavor == null) {
            return;
        }

        // TODO add your handling code here:
        checksum = generate(file, flavor);

        //
        this.checksum = checksum;

        //
        mainJTextArea.setText(checksum);
        mainJTextArea.setToolTipText(checksum);

        //
        if (checksum.length() > 0) {
            copyJButton.setEnabled(true);
        } else {
            copyJButton.setEnabled(false);
        }
    }

    public JMenuItem getItem(String label, AbstractAction action) {

        //
        final JMenuItem item = new JMenuItem(label);

        //
        item.addActionListener(action);

        //
        return item;
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
        fileJButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        generateJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        closeJButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        flavorJList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        mainJTextArea = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        locationJLabel = new javax.swing.JLabel();
        copyJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("View File Checksums");
        setResizable(false);

        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));

        fileJButton.setText("File");
        fileJButton.setIconTextGap(8);
        fileJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        fileJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        fileJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        fileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(fileJButton);
        buttonJPanel.add(filler1);

        generateJButton.setText("Regenerate");
        generateJButton.setMaximumSize(new java.awt.Dimension(98, 26));
        generateJButton.setMinimumSize(new java.awt.Dimension(98, 26));
        generateJButton.setPreferredSize(new java.awt.Dimension(98, 26));
        generateJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(generateJButton);
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

        flavorJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        flavorJList.setFixedCellHeight(24);
        flavorJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                flavorJListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(flavorJList);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setMaximumSize(new java.awt.Dimension(88, 68));
        jScrollPane2.setMinimumSize(new java.awt.Dimension(88, 68));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(88, 68));

        mainJTextArea.setColumns(20);
        mainJTextArea.setLineWrap(true);
        mainJTextArea.setRows(5);
        mainJTextArea.setInheritsPopupMenu(true);
        mainJTextArea.setMaximumSize(new java.awt.Dimension(70, 50));
        mainJTextArea.setMinimumSize(new java.awt.Dimension(70, 50));
        mainJTextArea.setPreferredSize(new java.awt.Dimension(70, 50));
        mainJTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainJTextAreaMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(mainJTextArea);

        jLabel2.setText("Supported Flavors");
        jLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel2.setEnabled(false);

        locationJLabel.setEnabled(false);
        locationJLabel.setMaximumSize(new java.awt.Dimension(229, 14));
        locationJLabel.setMinimumSize(new java.awt.Dimension(229, 14));
        locationJLabel.setPreferredSize(new java.awt.Dimension(229, 14));

        copyJButton.setText("Copy");
        copyJButton.setToolTipText("Copy to Clipboard");
        copyJButton.setFocusPainted(false);
        copyJButton.setMaximumSize(new java.awt.Dimension(24, 26));
        copyJButton.setMinimumSize(new java.awt.Dimension(24, 26));
        copyJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        copyJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(locationJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(95, 95, 95)
                                .addComponent(copyJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(copyJButton, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(locationJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeJButtonActionPerformed

        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_closeJButtonActionPerformed

    private void generateJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateJButtonActionPerformed

        // Single files stay in this dialog, multi get tossed to the multi viewer
        if (!file.isFile()) {

            //
            ChecksumMultiViewer viewer = new ChecksumMultiViewer(this, file, flavor, false);
            viewer.setLocationRelativeTo(this);
            viewer.setVisible(true);
        }

        //
        updateTextField(file, checksum);
    }//GEN-LAST:event_generateJButtonActionPerformed

    private void fileJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileJButtonActionPerformed

        // Open a file Menu
        final JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(delegate.getDataDirectory()));
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        // Show the dialog
        final int value = chooser.showOpenDialog(this);

        //
        if (value == JFileChooser.APPROVE_OPTION) {

            // Grab the file or directory
            file = chooser.getSelectedFile();

            //
            if (file != null) {

                //
                locationJLabel.setText(file.getAbsolutePath());
                locationJLabel.setToolTipText(locationJLabel.getText());

                //
                updateTextField(file, checksum);
            }

            // Enable buttons
            generateJButton.setEnabled(true);
        }
    }//GEN-LAST:event_fileJButtonActionPerformed

    private void copyJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyJButtonActionPerformed

        // Copy text to clipboard
        copy();
    }//GEN-LAST:event_copyJButtonActionPerformed

    private void flavorJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_flavorJListValueChanged

        // TODO add your handling code here:
        if (!evt.getValueIsAdjusting()) {

            //
            final Object object = flavorJList.getSelectedValue();

            // Cast
            flavor = String.valueOf(object);

            //
            updateTextField(file, checksum);
        }
    }//GEN-LAST:event_flavorJListValueChanged

    private void mainJTextAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainJTextAreaMouseClicked

        // Only show on Right-Click
        if (evt.getButton() == MouseEvent.BUTTON3) {

            // TODO add your handling code here:
            final JPopupMenu menu = new JPopupMenu();
            menu.removeAll();
            menu.setVisible(false);

            // @idea; from my WorldCanvas.java addKeyStrokes method
            menu.add(getItem("Copy", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {

                    //
                    mainJTextArea.selectAll();
                    mainJTextArea.copy();
                }
            }));

            // Add the cut option
            menu.add(getItem("Cut", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {

                    //
                    mainJTextArea.selectAll();
                    mainJTextArea.cut();
                }
            }));

            //
            menu.show(mainJTextArea, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_mainJTextAreaMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton closeJButton;
    private javax.swing.JButton copyJButton;
    private javax.swing.JButton fileJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JList flavorJList;
    private javax.swing.JButton generateJButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel locationJLabel;
    private javax.swing.JTextArea mainJTextArea;
    // End of variables declaration//GEN-END:variables

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

        //
        requestFocus();

        // Try--
        try {

            // Grab the transfered object
            final Transferable trans = dtde.getTransferable();

            //
            if (dtde.isDataFlavorSupported(TransferableNode.nodeFlavor)) {

                //
                //System.out.println("Supported Flavor entered.");

                // Cast to node
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) trans.getTransferData(TransferableNode.nodeFlavor);

                // Grab its user object
                final Object userObject = node.getUserObject();

                // Background solving
                if (userObject instanceof DataPackage) {

                    // Accept backgrounds
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);

                    // Kick out
                    return;
                }

                //
                if (userObject instanceof World || userObject instanceof WorldCell) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                } else {
                    dtde.rejectDrag();
                }
            }
        } catch (UnsupportedFlavorException | IOException ioe) {
            System.err.println("Unsupported Flavor " + ioe);
        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        //
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        //
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        //
    }

    @Override
    public void drop(DropTargetDropEvent event) {

        // Try--
        try {

            // Collect data about the object dropped onto the panel
            final Transferable transferable = event.getTransferable();

            // Cast a fMap object out of the transferable object
            final DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) transferable.getTransferData(TransferableNode.nodeFlavor);

            // Check if it is an accepted type of dropped object
            if (event.isDataFlavorSupported(TransferableNode.nodeFlavor)) {

                //
                //System.out.println("Supported Flavor");

                // Accept this flavor of dropped object
                event.acceptDrop(DnDConstants.ACTION_COPY);

                //
                final Object userObject = newNode.getUserObject();

                if (userObject instanceof DataPackage) {

                    //
                    final DataPackage pack = (DataPackage) userObject;

                    //
                    final FileSearch search = new FileSearch(new File(delegate.getDataDirectory()), pack.getReferenceName(), false);

                    // Perform the file search
                    search.perform();

                    //
                    try {

                        //
                        checksum = FileUtils.generateChecksum(search.getFirstResult(), flavor);

                        //
                        mainJTextArea.setText(checksum);
                        mainJTextArea.setToolTipText(checksum);

                        // Show us where D:
                        locationJLabel.setText(search.getFirstResult());
                        locationJLabel.setToolTipText(locationJLabel.getText());

                        //
                        if (checksum.length() > 0) {
                            copyJButton.setEnabled(true);
                        } else {
                            copyJButton.setEnabled(false);
                        }

                    } catch (Exception ioe) {
                        //
                    }
                }

                // Complete the dropping operation
                event.dropComplete(true);
                repaint();
                return;
            }

            // Otherwise reject the dropped object
            event.rejectDrop();
        } catch (UnsupportedFlavorException | IOException ex) {
            System.err.println("Drop Rejected.");
            //
            event.rejectDrop();
        }
    }
}
