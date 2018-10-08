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
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import Editor.renderer.DefaultCheckBoxRenderer;
import core.coll.CollisionWrapper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import tracer.AlphaTracer;

/**
 *
 * @author Robert A. Cherry
 */
public class MirrorSelector extends javax.swing.JDialog {

    // Variable Declaration
    // Imported Project Classes
    private AlphaTracer tracer;
    // Java Classes
    private DefaultListModel indexModel;
    private Point pointMouse;
    // Swing Classes
    private JPanel[] panels;
    private JCheckBox[] boxes;
    private JLabel[] labels;
    // Project Classes
    private final Animation animation;
    private final CollisionWrapper wrapper;
    private ImagePanel imageJPanel;
    // Data Types
    private final int startIndex;
    private int[] output;
    // End of Variable Declaration

    public MirrorSelector(java.awt.Dialog parent, Animation animation, CollisionWrapper wrapper, boolean modal) {

        //
        super(parent, modal);
        initComponents();

        // Work with a copy of the animation, not the original.
        this.animation = animation.reproduce();
        startIndex = animation.getIndex();
        this.wrapper = wrapper;

        // Initialize my custom commands
        init();
    }

    private void init() {

        //
        pointMouse = new Point();
        indexModel = new DefaultListModel();

        //
        final int length = animation.length();

        //
        panels = new JPanel[length];
        boxes = new JCheckBox[length];
        labels = new JLabel[length];

        //
        final Dimension size = new Dimension(8, 24);

        //
        for (int i = 0; i < length; i++) {

            // We use a panel so we can separate when the user clicks the small checkbox
            // and the large label
            // If we didn't use a panel and just used a checkbox (in the renderer),
            // We would be processing input for the entire length of the cell on each click.
            final JPanel panel = new JPanel();

            // Define a nice layout
            BoxLayout layout = new BoxLayout(panel, BoxLayout.LINE_AXIS);
            panel.setLayout(layout);

            // Checkbox and text
            final JCheckBox box = new JCheckBox();
            final JLabel label = new JLabel();

            // Add the box, separator, and label to the panel.
            panel.add(box);
            panel.add(new javax.swing.Box.Filler(size, size, size));
            panel.add(label);

            // Preset the parent index to selected and add an identifier.
            // Renderer will disable it.
            if (i == startIndex) {
                box.setSelected(true);
                label.setText("<Parent> Index: " + i);
                indexModel.addElement(panel);
            } else {
                label.setText("Index: " + i);
                indexModel.addElement(panel);
            }

            //
            panels[i] = panel;
            boxes[i] = box;
            labels[i] = label;
        }

        //
        indexJList.setFixedCellHeight(24);

        //
        indexJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        indexJList.setCellRenderer(new DefaultCheckBoxRenderer());
        indexJList.setModel(indexModel);
        indexJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {

                //
                final int index = indexJList.getSelectedIndex();

                // No need for holder variables.
                if (new Rectangle(0, index * 24, 24, 24).contains(indexJList.getMousePosition())) {

                    //
                    boxes[index].setSelected(!boxes[index].isSelected());
                }

                // Repaint.
                repaint();
            }
        });

        // Override the default paint operations for imageJPanel
        imageJPanel = new ImagePanel(contentJScrollPane) {
            @Override
            public void paint(Graphics monet) {

                //
                super.paint(monet);

                // Cast to 2D for easier polygon rendering.
                final Graphics2D manet = (Graphics2D) monet;

                //
                if (tracer != null) {

                    //
                    final ArrayList<Polygon> list = tracer.getPolygonList();

                    //
                    final BufferedImage image = tracer.getTraceImage() == null ? tracer.getOriginalImage() : tracer.getTraceImage();

                    // Draw the image under
                    manet.drawImage(image, 0, 0, this);
                    manet.setColor(Color.BLACK);

                    // Draw those polygons over.
                    for (int i = 0; i < list.size(); i++) {

                        //
                        final Polygon p = new Polygon(list.get(i).xpoints, list.get(i).ypoints, list.get(i).npoints);

                        // Translate the polygon to the center
                        p.translate(imageJPanel.getWidth() / 2 - image.getWidth() / 2,
                                imageJPanel.getHeight() / 2 - image.getHeight() / 2);

                        //
                        if (p.contains(pointMouse)) {
                            manet.setColor(Color.GREEN);
                            manet.fill(p);
                        }

                        // Always draw the outline.
                        manet.setColor(Color.BLACK);
                        manet.draw(p);
                    }
                }
            }
        };
        imageJPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                pointMouse = evt.getPoint();
                repaint();
            }
        });
        imageJPanel.setPreferredSize(new Dimension(240, 240));
        imageJPanel.setShowTextile(true);
        imageJPanel.setShowImage(true);
        imageJPanel.setImageCentered(true);

        //
        tracer = new AlphaTracer(animation.getCurrentImage());

        //
        tracer.setPrecision(wrapper.getPrecisionForIndex(startIndex));
        tracer.setPointMap(wrapper.getPointsForIndex(startIndex));
        tracer.flash();

        //
        polygonJLabel.setText("Polygons: " + tracer.getPolygonList().size());

        // Change the viewport of jScrollPane2
        contentJScrollPane.setViewportView(imageJPanel);

        //
        repaint();
    }

    public int[] getSelectedIndices() {
        return output;
    }

    public int getPrecision() {
        return alphaJSlider.getValue();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        packageJScrollPane = new javax.swing.JScrollPane();
        indexJList = new javax.swing.JList();
        contentJScrollPane = new javax.swing.JScrollPane();
        buttonJPanel = new javax.swing.JPanel();
        selectJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        polygonJLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        acceptJButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        declineJButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        alphaJLabel = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        alphaJSlider = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mirror Index Selection");
        setMinimumSize(new java.awt.Dimension(522, 340));

        packageJScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Mirror Indices"));

        indexJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        indexJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        indexJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                indexJListValueChanged(evt);
            }
        });
        packageJScrollPane.setViewportView(indexJList);

        contentJScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview Pane"));
        contentJScrollPane.setPreferredSize(new java.awt.Dimension(288, 281));

        buttonJPanel.setPreferredSize(new java.awt.Dimension(100, 26));
        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));

        selectJButton.setText("Select All");
        selectJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        selectJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        selectJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        selectJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(selectJButton);
        buttonJPanel.add(filler2);

        polygonJLabel.setText("Polygons:");
        polygonJLabel.setToolTipText("How many Polygons this Image contains");
        polygonJLabel.setMaximumSize(new java.awt.Dimension(72, 24));
        polygonJLabel.setMinimumSize(new java.awt.Dimension(72, 24));
        polygonJLabel.setPreferredSize(new java.awt.Dimension(72, 24));
        buttonJPanel.add(polygonJLabel);
        buttonJPanel.add(filler1);

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

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        alphaJLabel.setText("Capture Precision:");
        alphaJLabel.setMaximumSize(new java.awt.Dimension(124, 24));
        alphaJLabel.setMinimumSize(new java.awt.Dimension(124, 24));
        alphaJLabel.setPreferredSize(new java.awt.Dimension(124, 24));
        jPanel1.add(alphaJLabel);
        jPanel1.add(filler5);

        alphaJSlider.setMaximum(64);
        alphaJSlider.setMinorTickSpacing(8);
        alphaJSlider.setPaintTicks(true);
        alphaJSlider.setMaximumSize(new java.awt.Dimension(32767, 24));
        alphaJSlider.setMinimumSize(new java.awt.Dimension(200, 24));
        alphaJSlider.setPreferredSize(new java.awt.Dimension(200, 24));
        alphaJSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                alphaJSliderStateChanged(evt);
            }
        });
        jPanel1.add(alphaJSlider);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(packageJScrollPane)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(contentJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 4, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(packageJScrollPane)
                    .addComponent(contentJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void declineJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_declineJButtonActionPerformed
        // Ensure it's null even though it always is.
        output = null;

        // Close this dialog
        setVisible(false);
    }//GEN-LAST:event_declineJButtonActionPerformed

    private void acceptJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptJButtonActionPerformed
        // Grab the selected indexes.
        int len = 0;
        int cur = 0;

        // Scanning for the number of boxes selected to define output array size.
        for (JCheckBox box : boxes) {
            if (box.isSelected()) {
                len++;
            }
        }

        // Defining our array.
        output = new int[len];

        // Only adding selected boxes.
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i].isSelected()) {
                output[cur] = i;
                //
                cur++;
            }
        }

        //
        setVisible(false);
    }//GEN-LAST:event_acceptJButtonActionPerformed

    private void indexJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_indexJListValueChanged
        //
        if (!evt.getValueIsAdjusting()) {

            // Grab the selected index
            final int selectedIndex = indexJList.getSelectedIndex();

            //
            animation.setIndex(selectedIndex);
            tracer.reset(animation.getCurrentImage());
            tracer.setPointMap(wrapper.getPointsForIndex(startIndex));
            tracer.flash();

            //
            polygonJLabel.setText("Polygons: " + tracer.getPolygonList().size());

            //
            repaint();
        }
    }//GEN-LAST:event_indexJListValueChanged

    private void selectJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectJButtonActionPerformed
        // TODO add your handling code here:
        if (selectJButton.getText().equals("Select All")) {
            for (int i = 0; i < boxes.length; i++) {

                if (i != startIndex) {
                    //
                    boxes[i].setSelected(true);
                }
            }
            selectJButton.setText("Deselect All");
        } else {

            // Skip 
            for (int i = 0; i < boxes.length; i++) {

                if (i != startIndex) {
                    //
                    boxes[i].setSelected(false);
                }
            }
            selectJButton.setText("Select All");
        }

        //
        repaint();
    }//GEN-LAST:event_selectJButtonActionPerformed

    private void alphaJSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_alphaJSliderStateChanged

        //
        if (!alphaJSlider.getValueIsAdjusting()) {

            //
            final int val = alphaJSlider.getValue();
            final int max = alphaJSlider.getMaximum();

            //
            tracer.setPrecision(val);
            tracer.flash();

            //
            alphaJLabel.setText("Capture Precision: " + ((val * 100) / max) + "%");

            //
            repaint();
        }
    }//GEN-LAST:event_alphaJSliderStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton acceptJButton;
    private javax.swing.JLabel alphaJLabel;
    private javax.swing.JSlider alphaJSlider;
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JScrollPane contentJScrollPane;
    private javax.swing.JButton declineJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JList indexJList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane packageJScrollPane;
    private javax.swing.JLabel polygonJLabel;
    private javax.swing.JButton selectJButton;
    // End of variables declaration//GEN-END:variables
}
