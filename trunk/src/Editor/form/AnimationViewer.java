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

import core.event.AnimationEvent;
import core.event.AnimationListener;
import core.world.Animation;
import io.resource.ResourceReader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author mcherry
 */
public class AnimationViewer extends javax.swing.JDialog implements AnimationListener {

    // Variable Delcaration
    // Swing Classes
    private ImagePanel imageJPanel;
    // Project Classes
    private Animation animation;
    // Data Inst.
    private int frameIndex;
    private int milliseconds;
    // End of Variable Declaration

    public AnimationViewer(javax.swing.JDialog editor, Animation animation, boolean modal) {
        super(editor, modal);
        initComponents();

        // We only want the copy not the original
        this.animation = animation == null ? null : animation.reproduce();

        // Initialize some custom commands
        init();
    }

    private void init() {

        // Custom paint override
        imageJPanel = new ImagePanel(mainJScrollPane);
        imageJPanel.setPreferredSize(new Dimension(250, 175));
        imageJPanel.setShowTextile(true);
        imageJPanel.setShowImage(true);

        //
        final AnimationViewer self = this;

        //
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evt) {
                animation.removeAnimationListener((AnimationListener) self);
            }
        });
        // Set the viewport as the imageJPanel
        mainJScrollPane.setViewportView(imageJPanel);

        final Class closs = getClass();

        //
        resetJButton.setIcon(new ImageIcon(closs.getResource("/Editor/icons/icon-refresh16.png")));

        //
        textileJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-color-background16r.png"));
        playJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-color-play24.png"));
        pauseJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-color-pause24.png"));
        advanceJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-next16r.png"));
        rewindJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-previous16r.png"));
        polygonJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-boundary16.png"));
        
        //
        final Toolkit kit = Toolkit.getDefaultToolkit();

        // Final call
        this.setIconImage(kit.getImage(closs.getResource("/Editor/icons/icon-animation16.png")));
        setAnimation(animation);
        setTitle("Animation Delay Viewer: " + animation.getDisplayName() == null ? 
                "[Remove Viewer]" : animation.getDisplayName());
    }

    private void refresh() {

        //
        if (animation != null) {

            // Kick start the animation to begin triggering animation events
            if (animation.isRunning() == false) {
                animation.start();
            }

            // Refresh this as well
            delayJSpinner.setValue(animation.getDelay());
        }

        //
        this.validate();
        this.repaint();
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getDelay() {
        return (animation == null ? 0 : animation.getDelay());
    }

    public void setAnimation(Animation other) {

        //
        //// System.out.println("Anim Length: " + animation.length());
        final SpinnerNumberModel frameModel = new SpinnerNumberModel(0, -1, animation == null ? 1 : animation.length(), 1);
        final SpinnerNumberModel delayModel = new SpinnerNumberModel(milliseconds < 30 ? 30 : milliseconds, 30, 10000, 1);

        //
        frameJSpinner.setModel(frameModel);
        delayJSpinner.setModel(delayModel);

        //
        if (other != null) {

            if (animation != null) {
                //
                animation.removeAnimationListener(this);
            }
            
            //
            animation = other.reproduce();
            animation.addAnimationListener(this);
            animation.setCycles(-1);

            // Draw the outline
            animation.setPaintShape(true);

            //
            milliseconds = animation.getDelay() < 30 ? 30 : animation.getDelay();

            //
            animation.start();
            imageJPanel.updatePanel(animation);
        }

        //
        refresh();
    }

    public void setDelay(int ms) {

        //
        delayJSpinner.setValue(ms);

        //
        refresh();
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

        mainJScrollPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        resetJButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        saveJButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        closeJButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        speedJLabel = new javax.swing.JLabel();
        delayJSpinner = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        frameJLabel = new javax.swing.JLabel();
        frameJSpinner = new javax.swing.JSpinner();
        animationJPanel = new javax.swing.JPanel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        rewindJButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        advanceJButton = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        pauseJButton = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        playJButton = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        animationJPanel1 = new javax.swing.JPanel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        textileJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        polygonJButton = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Viewing Animations");
        setMinimumSize(new java.awt.Dimension(290, 315));
        setResizable(false);

        mainJScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview Panel"));
        mainJScrollPane.setMaximumSize(new java.awt.Dimension(271, 178));
        mainJScrollPane.setMinimumSize(new java.awt.Dimension(271, 178));
        mainJScrollPane.setPreferredSize(new java.awt.Dimension(271, 178));

        jPanel1.setMaximumSize(new java.awt.Dimension(32903, 26));
        jPanel1.setMinimumSize(new java.awt.Dimension(136, 26));
        jPanel1.setPreferredSize(new java.awt.Dimension(352, 26));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        resetJButton.setToolTipText("Reset to Defaults");
        resetJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        resetJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        resetJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        resetJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(resetJButton);
        jPanel1.add(filler3);

        saveJButton.setText("Save");
        saveJButton.setToolTipText("Accept Delay Changes");
        saveJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        saveJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        saveJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        saveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(saveJButton);
        jPanel1.add(filler1);

        closeJButton.setText("Close");
        closeJButton.setToolTipText("Close this Window");
        closeJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        closeJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        closeJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        closeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(closeJButton);

        java.awt.GridBagLayout jPanel2Layout = new java.awt.GridBagLayout();
        jPanel2Layout.columnWidths = new int[] {0, 25, 0};
        jPanel2Layout.rowHeights = new int[] {0};
        jPanel2.setLayout(jPanel2Layout);

        speedJLabel.setForeground(new java.awt.Color(0, 102, 255));
        speedJLabel.setText("Animation Delay (Millis):");
        speedJLabel.setMaximumSize(new java.awt.Dimension(174, 24));
        speedJLabel.setMinimumSize(new java.awt.Dimension(174, 24));
        speedJLabel.setPreferredSize(new java.awt.Dimension(174, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(speedJLabel, gridBagConstraints);

        delayJSpinner.setMaximumSize(new java.awt.Dimension(72, 24));
        delayJSpinner.setMinimumSize(new java.awt.Dimension(72, 24));
        delayJSpinner.setPreferredSize(new java.awt.Dimension(72, 24));
        delayJSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                delayJSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel2.add(delayJSpinner, gridBagConstraints);

        jPanel3.setMaximumSize(new java.awt.Dimension(246, 24));
        jPanel3.setMinimumSize(new java.awt.Dimension(246, 24));
        jPanel3.setPreferredSize(new java.awt.Dimension(246, 24));
        java.awt.GridBagLayout jPanel3Layout = new java.awt.GridBagLayout();
        jPanel3Layout.columnWidths = new int[] {0, 25, 0};
        jPanel3Layout.rowHeights = new int[] {0};
        jPanel3.setLayout(jPanel3Layout);

        frameJLabel.setText("Current Frame:");
        frameJLabel.setMaximumSize(new java.awt.Dimension(174, 24));
        frameJLabel.setMinimumSize(new java.awt.Dimension(174, 24));
        frameJLabel.setPreferredSize(new java.awt.Dimension(174, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel3.add(frameJLabel, gridBagConstraints);

        frameJSpinner.setEnabled(false);
        frameJSpinner.setMaximumSize(new java.awt.Dimension(72, 24));
        frameJSpinner.setMinimumSize(new java.awt.Dimension(72, 24));
        frameJSpinner.setPreferredSize(new java.awt.Dimension(72, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel3.add(frameJSpinner, gridBagConstraints);

        animationJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Animation Reel"));
        animationJPanel.setMaximumSize(new java.awt.Dimension(156, 24));
        animationJPanel.setMinimumSize(new java.awt.Dimension(156, 24));
        animationJPanel.setPreferredSize(new java.awt.Dimension(156, 24));
        animationJPanel.setLayout(new javax.swing.BoxLayout(animationJPanel, javax.swing.BoxLayout.LINE_AXIS));
        animationJPanel.add(filler5);

        rewindJButton.setToolTipText("Rewind Animation Cycle");
        rewindJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        rewindJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        rewindJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        rewindJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rewindJButtonActionPerformed(evt);
            }
        });
        animationJPanel.add(rewindJButton);
        animationJPanel.add(filler4);

        advanceJButton.setToolTipText("Advance Animation Cycle");
        advanceJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        advanceJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        advanceJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        advanceJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advanceJButtonActionPerformed(evt);
            }
        });
        animationJPanel.add(advanceJButton);
        animationJPanel.add(filler9);

        pauseJButton.setToolTipText("Pause Animation");
        pauseJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        pauseJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        pauseJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        pauseJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseJButtonActionPerformed(evt);
            }
        });
        animationJPanel.add(pauseJButton);
        animationJPanel.add(filler7);

        playJButton.setToolTipText("Play / Resume Animation");
        playJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        playJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        playJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        playJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playJButtonActionPerformed(evt);
            }
        });
        animationJPanel.add(playJButton);
        animationJPanel.add(filler8);

        animationJPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview Colors"));
        animationJPanel1.setMaximumSize(new java.awt.Dimension(156, 24));
        animationJPanel1.setMinimumSize(new java.awt.Dimension(156, 24));
        animationJPanel1.setPreferredSize(new java.awt.Dimension(156, 24));
        animationJPanel1.setLayout(new javax.swing.BoxLayout(animationJPanel1, javax.swing.BoxLayout.LINE_AXIS));
        animationJPanel1.add(filler10);

        textileJButton.setToolTipText("Change Background Color");
        textileJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        textileJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        textileJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        textileJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                textileJButtonMouseClicked(evt);
            }
        });
        animationJPanel1.add(textileJButton);
        animationJPanel1.add(filler2);

        polygonJButton.setToolTipText("Change Background Color");
        polygonJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        polygonJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        polygonJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        polygonJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                polygonJButtonMouseClicked(evt);
            }
        });
        polygonJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                polygonJButtonActionPerformed(evt);
            }
        });
        animationJPanel1.add(polygonJButton);
        animationJPanel1.add(filler6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(mainJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(animationJPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(animationJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(animationJPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .addComponent(animationJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeJButtonActionPerformed
        // Close this out
        animation.pause();
        animation.removeAnimationListener(this);
        animation = null;
        setVisible(false);
    }//GEN-LAST:event_closeJButtonActionPerformed

    private void resetJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetJButtonActionPerformed
        // TODO add your handling code here:
        delayJSpinner.setValue(milliseconds);

        //
        //animation.start();
        animation.restart();
    }//GEN-LAST:event_resetJButtonActionPerformed

    private void delayJSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_delayJSpinnerStateChanged
        // TODO add your handling code here:
        final int ms = ((Number) delayJSpinner.getValue()).intValue();

        if (animation != null) {
            //
            animation.setDelay(ms);
        }
        //animation.restart();
    }//GEN-LAST:event_delayJSpinnerStateChanged

    private void saveJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveJButtonActionPerformed
        // TODO add your handling code here:
        if (getParent() instanceof IllustrationEditor) {

            //
            final Animation dummy = (Animation) ((IllustrationEditor) getParent()).getIllustration();

            //
            if (dummy != null) {
                dummy.setDelay(animation.getDelay());
            }
        }
    }//GEN-LAST:event_saveJButtonActionPerformed

    private void playJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playJButtonActionPerformed
        // TODO add your handling code here:
        if (animation != null) {
            // Play indefinitely
            animation.setCycles(-1);
            animation.setDelay(133);

            // Start or restart
            if (animation.isRunning() == false) {

                //
                animation.start();
            } else {
                animation.restart();
            }
        }
    }//GEN-LAST:event_playJButtonActionPerformed

    private void pauseJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseJButtonActionPerformed
        // TODO add your handling code here:
        if (animation != null) {

            // Pause the animation
            animation.setCycles(0);
            animation.setDelay(0);
            animation.pause();
        }
    }//GEN-LAST:event_pauseJButtonActionPerformed

    private void textileJButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textileJButtonMouseClicked
        // TODO add your handling code here:
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

    private void advanceJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advanceJButtonActionPerformed
        // TODO add your handling code here:
        animation.pause();
        animation.advance();
    }//GEN-LAST:event_advanceJButtonActionPerformed

    private void rewindJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rewindJButtonActionPerformed
        // TODO add your handling code here:
        animation.pause();
        animation.rewind();
    }//GEN-LAST:event_rewindJButtonActionPerformed

    private void polygonJButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_polygonJButtonMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_polygonJButtonMouseClicked

    private void polygonJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_polygonJButtonActionPerformed
        // TODO add your handling code here:
        animation.setWrapperDrawn(!animation.isDrawingWrapper());
        repaint();
    }//GEN-LAST:event_polygonJButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton advanceJButton;
    private javax.swing.JPanel animationJPanel;
    private javax.swing.JPanel animationJPanel1;
    private javax.swing.JButton closeJButton;
    private javax.swing.JSpinner delayJSpinner;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel frameJLabel;
    private javax.swing.JSpinner frameJSpinner;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane mainJScrollPane;
    private javax.swing.JButton pauseJButton;
    private javax.swing.JButton playJButton;
    private javax.swing.JButton polygonJButton;
    private javax.swing.JButton resetJButton;
    private javax.swing.JButton rewindJButton;
    private javax.swing.JButton saveJButton;
    private javax.swing.JLabel speedJLabel;
    private javax.swing.JButton textileJButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void animationEnd(AnimationEvent event) {

        //
        if (event.getSource() == animation) {

            //
            frameIndex = animation.getIndex();

            //
            frameJSpinner.setValue(frameIndex);

            //
            imageJPanel.repaint();
        }
    }

    @Override
    public void animationStep(AnimationEvent event) {

        //
        if (event.getSource() == animation) {

            //
            frameIndex = animation.getIndex();

            //
            frameJSpinner.setValue(frameIndex);

            //
            imageJPanel.repaint();
        }
    }
}
