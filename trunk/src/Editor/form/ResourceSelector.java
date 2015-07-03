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

import core.world.Animation;
import core.world.Picture;
import core.world.WorldResource;
import io.resource.ResourceDelegate;
import io.resource.DataPackage;
import io.resource.DataRef;
import io.resource.ResourceReader;
import java.awt.Color;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.ListSelectionModel;
import Editor.renderer.DataListRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class ResourceSelector extends javax.swing.JDialog {

    // Variable Declaration
    private Class classFilter;
    private Color backgroundColor = new Color(240, 240, 240);
    // Java Classes
    private DefaultListModel packageModel;
    private DefaultListModel resourceModel;
    // Project Classes
    private ImagePanel imageJPanel;
    private WorldResource resource;
    private ResourceDelegate delegate;
    private DataPackage pack;
    // End of Variable Declaration

    public ResourceSelector(java.awt.Dialog parent, ResourceDelegate delegate, boolean modal) {

        //
        super(parent, modal);
        initComponents();

        //
        this.delegate = delegate;

        // Initialize my custom commands
        init();
    }

    private void init() {

        //
        resourceModel = new DefaultListModel();
        packageModel = new DefaultListModel();
        packageModel.addElement(ResourceDelegate.UNPACKAGED_STATEMENT);

        //
        packageJList.setFixedCellHeight(24);

        // Grab all the plugins
        final DataPackage[] plugins = delegate.getDataPackages();

        // If there are any plugins
        if (plugins != null) {
            //
            for (int i = 0; i < plugins.length; i++) {
                packageModel.addElement(plugins[i]);
            }
        }

        //
        final Class closs = getClass();

        //
        backgroundJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-color-chooser16.png"));
        //backgroundJButton.setContentAreaFilled(false);

        //
        informationJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-view18.png"));
        //informationJButton.setFocusPainted(false);
        //informationJButton.setContentAreaFilled(false);

        // Custom cell renderer
        resourceJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resourceJList.setCellRenderer(new DataListRenderer());
        resourceJList.setModel(resourceModel);

        //
        packageJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packageJList.setCellRenderer(new DataListRenderer());
        packageJList.setModel(packageModel);

        // Default index
        packageJList.setSelectedIndex(0);
        resourceJList.setSelectedIndex(0);

        // Override the default paint operations for imageJPanel
        imageJPanel = new ImagePanel(contentJScrollPane);
        imageJPanel.setShowTextile(true);

        // Change the viewport of jScrollPane2
        contentJScrollPane.setViewportView(imageJPanel);
    }

    public WorldResource getResource() {
        return resource;
    }

    public DataPackage getResourcePackage() {
        return pack;
    }

    public void setFilterType(Class filter) {
        classFilter = filter;

        //
        filterJField.setText(filter.getSimpleName());
    }

    public void setResource(WorldResource resource) {

        //
        this.resource = resource;

        // Do not allow null resources to update.
        if (resource != null) {

            // Do not remove.
            packageJList.setSelectedValue(delegate.getPackageForResource(resource), true);

            // Solving for Base Graphics for now.
            if (resource instanceof Picture) {

                //
                final int index = resourceModel.indexOf(resource);

                // Change the resource JList
                resourceJList.setSelectedIndex(index);
            }

            // Change active Tab.
            mainJTabbedPane.setSelectedComponent(resourceJScrollPane);
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

        contentJScrollPane = new javax.swing.JScrollPane();
        buttonJPanel = new javax.swing.JPanel();
        filterJLabel = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        filterJField = new javax.swing.JTextField();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        informationJButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        backgroundJButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        chooseJButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        cancelJButton = new javax.swing.JButton();
        mainJTabbedPane = new javax.swing.JTabbedPane();
        packageJScrollPane = new javax.swing.JScrollPane();
        packageJList = new javax.swing.JList();
        resourceJScrollPane = new javax.swing.JScrollPane();
        resourceJList = new javax.swing.JList();
        nullJLabel2 = new javax.swing.JLabel();
        nullJLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Resource Selecting");
        setMinimumSize(new java.awt.Dimension(560, 396));

        contentJScrollPane.setPreferredSize(new java.awt.Dimension(288, 281));

        buttonJPanel.setPreferredSize(new java.awt.Dimension(100, 26));
        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));

        filterJLabel.setText("Filtered to:");
        filterJLabel.setEnabled(false);
        filterJLabel.setMaximumSize(new java.awt.Dimension(53, 26));
        filterJLabel.setMinimumSize(new java.awt.Dimension(53, 26));
        filterJLabel.setPreferredSize(new java.awt.Dimension(53, 26));
        buttonJPanel.add(filterJLabel);
        buttonJPanel.add(filler5);

        filterJField.setEditable(false);
        filterJField.setEnabled(false);
        filterJField.setMaximumSize(new java.awt.Dimension(128, 26));
        filterJField.setMinimumSize(new java.awt.Dimension(128, 26));
        filterJField.setPreferredSize(new java.awt.Dimension(128, 26));
        buttonJPanel.add(filterJField);
        buttonJPanel.add(filler2);

        informationJButton.setToolTipText("Change Panel Background Color");
        informationJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        informationJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        informationJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        informationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                informationJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(informationJButton);
        buttonJPanel.add(filler1);

        backgroundJButton.setToolTipText("Change Panel Background Color");
        backgroundJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        backgroundJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        backgroundJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        backgroundJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backgroundJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(backgroundJButton);
        buttonJPanel.add(filler4);

        chooseJButton.setText("Choose");
        chooseJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        chooseJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        chooseJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        chooseJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(chooseJButton);
        buttonJPanel.add(filler3);

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

        mainJTabbedPane.setPreferredSize(new java.awt.Dimension(242, 281));

        packageJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        packageJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                packageJListValueChanged(evt);
            }
        });
        packageJScrollPane.setViewportView(packageJList);

        mainJTabbedPane.addTab("Package List", packageJScrollPane);

        resourceJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        resourceJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        resourceJList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resourceJListMouseClicked(evt);
            }
        });
        resourceJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                resourceJListValueChanged(evt);
            }
        });
        resourceJScrollPane.setViewportView(resourceJList);

        mainJTabbedPane.addTab("Internal Resource List", resourceJScrollPane);

        nullJLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        nullJLabel2.setText("Image View");
        nullJLabel2.setEnabled(false);

        nullJLabel1.setText("Showing All Imported resources for this Session");
        nullJLabel1.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(nullJLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(mainJTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(contentJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(243, 243, 243)
                                .addComponent(nullJLabel2)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nullJLabel2)
                    .addComponent(nullJLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mainJTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(contentJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed

        // Close this dialog
        setVisible(false);
    }//GEN-LAST:event_cancelJButtonActionPerformed

    private void resourceJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_resourceJListValueChanged

        // Check for value is Adjusting
        if (!evt.getValueIsAdjusting()) {

            // Capture
            WorldResource res;

            // Not Implemented yet
            if (classFilter != null) {

                // Change
                res = (WorldResource) resourceJList.getSelectedValue();

                // Null check
                if (res == null) {
                    return;
                }

                // Ask
                if (res.getClass() != classFilter) {
                    return;
                }
            }

            // Change
            resource = (WorldResource) resourceJList.getSelectedValue();

            // Solve for Animation (Listener)
            if (resource instanceof Animation) {

                //
                resource = resource.reproduce();

                // Animation cast
                final Animation animation = (Animation) resource;

                // Remove and add
                animation.removeAnimationListener(imageJPanel);
                animation.addAnimationListener(imageJPanel);

                // Start the Animation
                animation.setCycles(-1);
                animation.setDelay(60);
                animation.start();
            }

            // Change the contentPane according to resource type
            imageJPanel.updatePanel(resource);
        }
    }//GEN-LAST:event_resourceJListValueChanged

    private void chooseJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseJButtonActionPerformed

        // @todo Resource could be null
        if (resource != null) {

            // Final check before closing
            pack = delegate.getPackageForResource(resource);

            //
            setVisible(false);
        }
    }//GEN-LAST:event_chooseJButtonActionPerformed

    private void backgroundJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backgroundJButtonActionPerformed

        // TODO add your handling code here:
        imageJPanel.setBackground(JColorChooser.showDialog(this, "Pick a color", backgroundColor));
        imageJPanel.repaint();
    }//GEN-LAST:event_backgroundJButtonActionPerformed

    private void packageJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_packageJListValueChanged

        // TODO add your handling code here:
        resourceModel.clear();

        // Solve for data packages
        if (packageJList.getSelectedValue() instanceof DataPackage) {

            // Fill the pluginJTree with the contents of the Plugin
            pack = (DataPackage) packageJList.getSelectedValue();

            // Grab all references from the data package
            final DataRef[] citations = pack.getCitations();

            // Iterate over the collection of citations
            for (int i = 0; i < citations.length; i++) {

                // grab the current Citation
                final DataRef reference = citations[i];

                // Check for filter
                if (classFilter != null) {

                    // Add only of type filter
                    if (reference.getResource().getClass() == classFilter) {

                        //
                        resourceModel.addElement(reference.getResource());
                    }
                } else {

                    // Just add it for the no filter option
                    resourceModel.addElement(reference.getResource());
                }
            }

            // Try it out
            resourceJList.setSelectedIndex(0);
        } else if (packageJList.getSelectedValue().equals(ResourceDelegate.UNPACKAGED_STATEMENT)) {

            // Grab "No Association" Files
            final Object[] citations = delegate.getLooseType(classFilter);

            // Must return resources to continue
            if (citations != null) {

                // Iterate over the citations
                for (int i = 0; i < citations.length; i++) {

                    // Grab the current reference
                    final Object reference = citations[i];

                    // Feed to list model
                    resourceModel.addElement(reference);
                }

                // Try it out
                resourceJList.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_packageJListValueChanged

    private void informationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_informationJButtonActionPerformed

        // TODO add your handling code here:
        if (resource != null) {

            //
            final ResourceViewer viewer = new ResourceViewer(this, resource, true);
            viewer.setLocationRelativeTo(this);
            viewer.setVisible(true);
            viewer.dispose();
        }
    }//GEN-LAST:event_informationJButtonActionPerformed

    private void resourceJListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resourceJListMouseClicked

        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {

            //
            final Object object = resourceJList.getSelectedValue();

            //
            if (object == null) {
                return;
            }

            //
            if (object instanceof WorldResource) {

                //
                resource = (WorldResource) object;

                //
                pack = delegate.getPackageForResource(resource);

                //
                setVisible(false);
            }
        }
    }//GEN-LAST:event_resourceJListMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backgroundJButton;
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JButton chooseJButton;
    private javax.swing.JScrollPane contentJScrollPane;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JTextField filterJField;
    private javax.swing.JLabel filterJLabel;
    private javax.swing.JButton informationJButton;
    private javax.swing.JTabbedPane mainJTabbedPane;
    private javax.swing.JLabel nullJLabel1;
    private javax.swing.JLabel nullJLabel2;
    private javax.swing.JList packageJList;
    private javax.swing.JScrollPane packageJScrollPane;
    private javax.swing.JList resourceJList;
    private javax.swing.JScrollPane resourceJScrollPane;
    // End of variables declaration//GEN-END:variables
}
