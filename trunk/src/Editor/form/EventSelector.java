/**
 * Copyright (c) 2013, Robert Cherry  *
 * All rights reserved.
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
import core.world.WorldObject;
import io.resource.ResourceDelegate;
import java.awt.Window;

/**
 *
 * @author robert
 */
public class EventSelector extends javax.swing.JDialog {

    // Variable Declaration
    // Project Classes
    private Animation animation;
    private ResourceDelegate delegate;
    // Data Types
    private int event;
    // End of Variable Declaration

    public EventSelector(Window parent, ResourceDelegate delegate, boolean modal) {
        super(parent);
        setModal(modal);
        initComponents();

        //
        this.delegate = delegate;

        // Initialize
        init();
    }

    private void init() {

        //
        descriptionJTextArea.setLineWrap(true);
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getEvent() {
        return event;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        eventJPanel = new javax.swing.JPanel();
        createJButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        stepSJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        stepEJButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        animationSJButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        animationEJButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        deathJButton = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionJTextArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        animationJButton = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        cancelJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(400, 245));
        setResizable(false);

        eventJPanel.setLayout(new javax.swing.BoxLayout(eventJPanel, javax.swing.BoxLayout.PAGE_AXIS));

        createJButton.setText("Creation Event");
        createJButton.setMaximumSize(new java.awt.Dimension(134, 26));
        createJButton.setMinimumSize(new java.awt.Dimension(134, 26));
        createJButton.setPreferredSize(new java.awt.Dimension(134, 26));
        createJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createJButtonActionPerformed(evt);
            }
        });
        eventJPanel.add(createJButton);
        eventJPanel.add(filler1);

        stepSJButton.setText("Step Start Event");
        stepSJButton.setMaximumSize(new java.awt.Dimension(134, 26));
        stepSJButton.setMinimumSize(new java.awt.Dimension(134, 26));
        stepSJButton.setPreferredSize(new java.awt.Dimension(134, 26));
        stepSJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepSJButtonActionPerformed(evt);
            }
        });
        eventJPanel.add(stepSJButton);
        eventJPanel.add(filler2);

        stepEJButton.setText("Step End Event");
        stepEJButton.setMaximumSize(new java.awt.Dimension(134, 26));
        stepEJButton.setMinimumSize(new java.awt.Dimension(134, 26));
        stepEJButton.setPreferredSize(new java.awt.Dimension(134, 26));
        stepEJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepEJButtonActionPerformed(evt);
            }
        });
        eventJPanel.add(stepEJButton);
        eventJPanel.add(filler4);

        animationSJButton.setText("Anim. Step Event");
        animationSJButton.setMaximumSize(new java.awt.Dimension(134, 26));
        animationSJButton.setMinimumSize(new java.awt.Dimension(134, 26));
        animationSJButton.setPreferredSize(new java.awt.Dimension(134, 26));
        animationSJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animationSJButtonActionPerformed(evt);
            }
        });
        eventJPanel.add(animationSJButton);
        eventJPanel.add(filler3);

        animationEJButton.setText("Anim. End Event");
        animationEJButton.setMaximumSize(new java.awt.Dimension(134, 26));
        animationEJButton.setMinimumSize(new java.awt.Dimension(134, 26));
        animationEJButton.setPreferredSize(new java.awt.Dimension(134, 26));
        animationEJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animationEJButtonActionPerformed(evt);
            }
        });
        eventJPanel.add(animationEJButton);
        eventJPanel.add(filler5);

        deathJButton.setText("Death Event");
        deathJButton.setMaximumSize(new java.awt.Dimension(134, 26));
        deathJButton.setMinimumSize(new java.awt.Dimension(134, 26));
        deathJButton.setPreferredSize(new java.awt.Dimension(134, 26));
        deathJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deathJButtonActionPerformed(evt);
            }
        });
        eventJPanel.add(deathJButton);
        eventJPanel.add(filler6);

        descriptionJTextArea.setEditable(false);
        descriptionJTextArea.setColumns(20);
        descriptionJTextArea.setRows(5);
        descriptionJTextArea.setEnabled(false);
        jScrollPane1.setViewportView(descriptionJTextArea);

        jPanel1.setMinimumSize(new java.awt.Dimension(100, 26));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        animationJButton.setText("Sync Animation");
        animationJButton.setMaximumSize(new java.awt.Dimension(134, 26));
        animationJButton.setMinimumSize(new java.awt.Dimension(134, 26));
        animationJButton.setPreferredSize(new java.awt.Dimension(134, 26));
        animationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animationJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(animationJButton);
        jPanel1.add(filler7);

        cancelJButton.setText("Cancel");
        cancelJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        cancelJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        cancelJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelJButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(eventJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(eventJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createJButtonActionPerformed

        // Creation Event
        event = WorldObject.FLAG_CREATE;

        //
        descriptionJTextArea.setText("Catches the Object as it is created in the world.");
    }//GEN-LAST:event_createJButtonActionPerformed

    private void stepSJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepSJButtonActionPerformed

        // Step Start Event
        event = WorldObject.FLAG_STEP_START;

        //
        descriptionJTextArea.setText("Catches the World at the start of its step process.");
    }//GEN-LAST:event_stepSJButtonActionPerformed

    private void stepEJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepEJButtonActionPerformed

        // Step End Event
        event = WorldObject.FLAG_STEP_END;

        //
        descriptionJTextArea.setText("Catches the World at the end of its step process.");
    }//GEN-LAST:event_stepEJButtonActionPerformed

    private void animationSJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_animationSJButtonActionPerformed

        // Animation Start Event
        event = WorldObject.FLAG_ANIMATION_START;

        //
        descriptionJTextArea.setText("Catches the Event of the animation starting its first frame.");
    }//GEN-LAST:event_animationSJButtonActionPerformed

    private void animationEJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_animationEJButtonActionPerformed

        // Animation End Event
        event = WorldObject.FLAG_ANIMATION_END;

        //
        descriptionJTextArea.setText("Catches the Event of the animation reaching its last frame.");
    }//GEN-LAST:event_animationEJButtonActionPerformed

    private void deathJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deathJButtonActionPerformed

        // Death Event
        event = WorldObject.FLAG_DESTROY;

        //
        descriptionJTextArea.setText("Script to execute when the Actor has been forcibly killed.");
    }//GEN-LAST:event_deathJButtonActionPerformed

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed

        // Close this EventJDialog
        setVisible(false);
    }//GEN-LAST:event_cancelJButtonActionPerformed

    private void animationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_animationJButtonActionPerformed

        // TODO add your handling code here:
        final ResourceSelector maker = new ResourceSelector(this, delegate, true);
        maker.setFilterType(Animation.class);
        maker.setLocationRelativeTo(this);
        maker.setVisible(true);

        // Grab
        animation = (Animation) maker.getResource();

        //
        maker.dispose();
    }//GEN-LAST:event_animationJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton animationEJButton;
    private javax.swing.JButton animationJButton;
    private javax.swing.JButton animationSJButton;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JButton createJButton;
    private javax.swing.JButton deathJButton;
    private javax.swing.JTextArea descriptionJTextArea;
    private javax.swing.JPanel eventJPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton stepEJButton;
    private javax.swing.JButton stepSJButton;
    // End of variables declaration//GEN-END:variables
}