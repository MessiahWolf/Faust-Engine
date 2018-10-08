/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.form;

import core.world.WorldResource;
import io.util.FileUtils;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Robert A. Cherry
 */
public class ResourceViewer extends javax.swing.JDialog {

    // Variable Declaration
    // Swing Native Classes
    private ImagePanel imagePanel;
    // Java Native Classes
    private final Object resource;
    // End of Variable Declaration

    public ResourceViewer(java.awt.Window parent, Object resource, boolean modal) {

        //
        super(parent);
        setModal(modal);
        initComponents();

        //
        this.resource = resource;

        //
        init();
    }

    private void init() {

        // Easy-peasy.
        imagePanel = new ImagePanel(imageJScrollPane);
        imagePanel.setShowTextile(false);
        imagePanel.setShowImage(true);

        //
        imageJScrollPane.setViewportView(imagePanel);

        // Considering Files (on File System)
        if (resource instanceof File) {

            // Define our file type extensions
            final String[] imageExtensions = {"png"};
            final String[] textExtensions = {"txt", "docx"};

            // Cast
            final File file = (File) resource;

            // Consider Text Files; not supporting .docx or .doc files yet :[
            if (FileUtils.isSupported(file, textExtensions)) {

                // Remove the graphical view
                mainJTabbedPane.remove(graphicJPanel);

                // Only option for now
                setupNormalTextFile(file);
            } else if (FileUtils.isSupported(file, imageExtensions)) {

                // Remove the textual view
                mainJTabbedPane.remove(textJPanel);

                // Image solution
                setupNormalImageFile(file);
            }

            //
            setTitle("Viewing Resource: " + file.getName());
        } else if (resource instanceof WorldResource) {

            //
            mainJTabbedPane.remove(textJPanel);

            // Animation time.
            imagePanel.updatePanel(resource);
        } else {
            System.err.println("Given Something else -o-");
        }

        // Our JTable stuff
        final DefaultTableModel model = new DefaultTableModel();
        final Object[][] vector = setupVector(resource);

        // Setup the vector
        model.setDataVector(vector, new String[]{"Attribute", "Value"});

        // Apply to table
        attributeJTable.setModel(model);
        attributeJTable.setIntercellSpacing(new Dimension(1, 1));
        attributeJTable.setRowHeight(24);
    }

    private void setupNormalImageFile(File file) {

        // Cast to image
        imagePanel.updatePanel(resource);
    }

    private void setupNormalTextFile(File file) {

        // Cut all the current text
        mainJTextArea.selectAll();
        mainJTextArea.cut();

        //
        if (FileUtils.getExtension(file).equals("txt")) {

            // It matches so create a scanner
            try {

                // We use a normal scanner for Normal Text Files
                final Scanner scanner = new Scanner(file);

                // Consider each line
                while (scanner.hasNextLine()) {

                    //
                    final String string = scanner.nextLine();

                    // Append to the text area then move down a line.
                    mainJTextArea.append(string);
                    mainJTextArea.append("\n");
                }
            } catch (IOException ioe) {
                //
            }
        } else {
            try {
                //
                System.err.println(FileUtils.getFileLineCount(file));
            } catch (IOException ioe) {
            }
        }
    }

    private Object[][] setupVector(Object object) {

        //
        Object[][] output = null;

        //
        if (object instanceof File) {

            //
            final File file = (File) object;

            //
            output = new Object[5][2];

            // Our labels
            output[0][0] = "Name";
            output[1][0] = "Extension";
            output[2][0] = "Absolute Path";
            output[3][0] = "SHA-1 Checksum";
            output[4][0] = "MD5 Checksum";

            //
            try {

                // Our values
                output[0][1] = file.getName();
                output[1][1] = FileUtils.getExtension(file);
                output[2][1] = file.getAbsolutePath();
                output[3][1] = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");
                output[4][1] = FileUtils.generateChecksum(file.getAbsolutePath(), "MD5");
            } catch (Exception ioe) {
                // Do not throw anything yet.
            }
        } else if (object instanceof Image) {
            //@TODO
            final Image image = (Image) resource;
        }

        //
        return output;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        exploreJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        closeJButton = new javax.swing.JButton();
        mainJTabbedPane = new javax.swing.JTabbedPane();
        graphicJPanel = new javax.swing.JPanel();
        imageJScrollPane = new javax.swing.JScrollPane();
        jLabel2 = new javax.swing.JLabel();
        textJPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainJTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        detailJPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        attributeJTable = new javax.swing.JTable() {

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Viewing Resource");

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        jPanel1.add(filler1);

        exploreJButton.setText("Explore");
        exploreJButton.setToolTipText("Open File in Explorer");
        exploreJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        exploreJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        exploreJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        exploreJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploreJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(exploreJButton);
        jPanel1.add(filler2);

        closeJButton.setText("Close");
        closeJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        closeJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        closeJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        closeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(closeJButton);

        jLabel2.setText("Graphical view of Image Files");
        jLabel2.setEnabled(false);

        javax.swing.GroupLayout graphicJPanelLayout = new javax.swing.GroupLayout(graphicJPanel);
        graphicJPanel.setLayout(graphicJPanelLayout);
        graphicJPanelLayout.setHorizontalGroup(
            graphicJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(graphicJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(graphicJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(imageJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                    .addGroup(graphicJPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        graphicJPanelLayout.setVerticalGroup(
            graphicJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, graphicJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imageJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainJTabbedPane.addTab("Graphical View", graphicJPanel);

        mainJTextArea.setEditable(false);
        mainJTextArea.setColumns(20);
        mainJTextArea.setLineWrap(true);
        mainJTextArea.setRows(5);
        mainJTextArea.setEnabled(false);
        jScrollPane1.setViewportView(mainJTextArea);

        jLabel1.setText("Scanned lines of File");
        jLabel1.setEnabled(false);

        javax.swing.GroupLayout textJPanelLayout = new javax.swing.GroupLayout(textJPanel);
        textJPanel.setLayout(textJPanelLayout);
        textJPanelLayout.setHorizontalGroup(
            textJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(textJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                    .addGroup(textJPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        textJPanelLayout.setVerticalGroup(
            textJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, textJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainJTabbedPane.addTab("Textual View", textJPanel);

        attributeJTable.setModel(new javax.swing.table.DefaultTableModel(
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
        attributeJTable.setEnabled(false);
        attributeJTable.setMaximumSize(new java.awt.Dimension(360, 330));
        attributeJTable.setMinimumSize(new java.awt.Dimension(360, 330));
        attributeJTable.setPreferredSize(new java.awt.Dimension(360, 330));
        jScrollPane2.setViewportView(attributeJTable);

        jLabel3.setText("View of basic file properties");
        jLabel3.setEnabled(false);

        javax.swing.GroupLayout detailJPanelLayout = new javax.swing.GroupLayout(detailJPanel);
        detailJPanel.setLayout(detailJPanelLayout);
        detailJPanelLayout.setHorizontalGroup(
            detailJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        detailJPanelLayout.setVerticalGroup(
            detailJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainJTabbedPane.addTab("File Details", detailJPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mainJTabbedPane)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainJTabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeJButtonActionPerformed

        //
        setVisible(false);
    }//GEN-LAST:event_closeJButtonActionPerformed

    private void exploreJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploreJButtonActionPerformed
        // TODO add your handling code here:
        if (resource instanceof File) {
            
            //
            final File file = (File) resource;

            //
            try {

                // Grab the Windows Operating Systems Explorer and ask it to open the file
                final Desktop explorer = Desktop.getDesktop();

                //
                if (file.exists()) {

                    // Open in Windows Explorer
                    explorer.open(file);
                }
            } catch (IOException ioe) {
                // Do nothing for now.
            }
        }
    }//GEN-LAST:event_exploreJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable attributeJTable;
    private javax.swing.JButton closeJButton;
    private javax.swing.JPanel detailJPanel;
    private javax.swing.JButton exploreJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JPanel graphicJPanel;
    private javax.swing.JScrollPane imageJScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane mainJTabbedPane;
    private javax.swing.JTextArea mainJTextArea;
    private javax.swing.JPanel textJPanel;
    // End of variables declaration//GEN-END:variables
}
