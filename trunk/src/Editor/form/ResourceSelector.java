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
import java.awt.Dimension;
import java.awt.event.MouseEvent;

/**
 *
 * @author Robert A. Cherry
 */
public class ResourceSelector extends javax.swing.JDialog {

    // Variable Declaration
    private Class classFilter;
    private final Color backgroundColor = new Color(240, 240, 240);
    // Java Classes
    private DefaultListModel packageModel;
    private DefaultListModel resourceModel;
    // Project Classes
    private ImagePanel imageJPanel;
    private WorldResource resource;
    private final ResourceDelegate delegate;
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
            for (DataPackage plugin : plugins) {
                packageModel.addElement(plugin);
            }
        }

        //
        final Class closs = getClass();

        //
        //textileJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-color-palette24.png"));
        //backgroundJButton.setContentAreaFilled(false);

        //
        //informationJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-view18.png"));
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
        imageJPanel.setPreferredSize(new Dimension(240, 240));
        imageJPanel.setShowTextile(true);
        imageJPanel.setShowImage(true);

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

        jPanel1 = new javax.swing.JPanel();
        filterJLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        filterJField = new javax.swing.JTextField();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        informationJButton = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        textileJButton = new javax.swing.JButton();
        contentJScrollPane = new javax.swing.JScrollPane();
        buttonJPanel = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        acceptJButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        declineJButton = new javax.swing.JButton();
        mainJTabbedPane = new javax.swing.JTabbedPane();
        packageJScrollPane = new javax.swing.JScrollPane();
        packageJList = new javax.swing.JList();
        resourceJScrollPane = new javax.swing.JScrollPane();
        resourceJList = new javax.swing.JList();

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        filterJLabel.setText("Filter:");
        filterJLabel.setEnabled(false);
        filterJLabel.setMaximumSize(new java.awt.Dimension(40, 26));
        filterJLabel.setMinimumSize(new java.awt.Dimension(40, 26));
        filterJLabel.setPreferredSize(new java.awt.Dimension(40, 26));
        jPanel1.add(filterJLabel);
        jPanel1.add(filler1);

        filterJField.setEditable(false);
        filterJField.setEnabled(false);
        filterJField.setMaximumSize(new java.awt.Dimension(108, 26));
        filterJField.setMinimumSize(new java.awt.Dimension(108, 26));
        filterJField.setPreferredSize(new java.awt.Dimension(108, 26));
        jPanel1.add(filterJField);
        jPanel1.add(filler6);

        informationJButton.setToolTipText("View File Properties");
        informationJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        informationJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        informationJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        informationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                informationJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(informationJButton);
        jPanel1.add(filler8);

        textileJButton.setToolTipText("Change Panel Background Color");
        textileJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        textileJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        textileJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        textileJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                textileJButtonMouseClicked(evt);
            }
        });
        jPanel1.add(textileJButton);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Resource Selecting");
        setMinimumSize(new java.awt.Dimension(522, 340));

        contentJScrollPane.setPreferredSize(new java.awt.Dimension(288, 281));

        buttonJPanel.setPreferredSize(new java.awt.Dimension(100, 26));
        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));
        buttonJPanel.add(filler2);

        acceptJButton.setText("Accept");
        acceptJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        acceptJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        acceptJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        acceptJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(acceptJButton);
        buttonJPanel.add(filler3);

        declineJButton.setText("Decline");
        declineJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        declineJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        declineJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        declineJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                declineJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(declineJButton);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(contentJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(mainJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mainJTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(contentJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(11, 11, 11)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void declineJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_declineJButtonActionPerformed
        // Null resource out then close
        resource = null;

        // Close this dialog
        setVisible(false);
    }//GEN-LAST:event_declineJButtonActionPerformed

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

            // Solve for Animation (Listener)
            if (resourceJList.getSelectedValue() instanceof Animation) {

                // Animation cast
                final Animation animation = (Animation) resourceJList.getSelectedValue();

                // Remove and add
                animation.removeAnimationListener(imageJPanel);
                animation.addAnimationListener(imageJPanel);

                // Start the Animation
                animation.setCycles(-1);
                animation.setDelay(60);
                animation.start();
            }

            // Change the contentPane according to resource type
            imageJPanel.updatePanel(resourceJList.getSelectedValue());
        }
    }//GEN-LAST:event_resourceJListValueChanged

    private void acceptJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptJButtonActionPerformed
        // @todo Resource could be null
        if (resourceJList.getSelectedValue() != null) {

            // Final check before closing
            pack = delegate.getPackageForResource((WorldResource) resourceJList.getSelectedValue());

            //
            setVisible(false);
        }
    }//GEN-LAST:event_acceptJButtonActionPerformed

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
            for (DataRef reference : citations) {
                // grab the current Citation
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
                for (Object reference : citations) {
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
        if (resourceJList.getSelectedValue() != null) {

            //
            final ResourceViewer viewer = new ResourceViewer(this, resourceJList.getSelectedValue(), true);
            viewer.setLocationRelativeTo(this);
            viewer.setVisible(true);
            viewer.dispose();
        }
    }//GEN-LAST:event_informationJButtonActionPerformed

    private void resourceJListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resourceJListMouseClicked

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
        }
        
        // Close on double click..
        if (evt.getClickCount() == 2) {

            // After the double click we're done.
            setVisible(false);
        }
    }//GEN-LAST:event_resourceJListMouseClicked

    private void textileJButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textileJButtonMouseClicked

        //
        final Color picked;

        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {

            // TODO add your handling code here:
            picked = JColorChooser.showDialog(this, "Change Background Color", imageJPanel.getTextileBackground());

            // @Ternary
            imageJPanel.setTextileBackground(picked == null ? Color.LIGHT_GRAY : picked);

            //
            imageJPanel.repaint();
        } else if (evt.getButton() == MouseEvent.BUTTON3) {

            //
            picked = JColorChooser.showDialog(this, "Change Foreground Color", imageJPanel.getTextileForeground());

            //
            imageJPanel.setTextileForeground(picked == null ? Color.WHITE : picked);

            //
            imageJPanel.repaint();
        }
    }//GEN-LAST:event_textileJButtonMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton acceptJButton;
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JScrollPane contentJScrollPane;
    private javax.swing.JButton declineJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler8;
    private javax.swing.JTextField filterJField;
    private javax.swing.JLabel filterJLabel;
    private javax.swing.JButton informationJButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane mainJTabbedPane;
    private javax.swing.JList packageJList;
    private javax.swing.JScrollPane packageJScrollPane;
    private javax.swing.JList resourceJList;
    private javax.swing.JScrollPane resourceJScrollPane;
    private javax.swing.JButton textileJButton;
    // End of variables declaration//GEN-END:variables
}
