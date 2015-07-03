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

import Editor.FaustEditor;
import core.world.Animation;
import core.world.WorldResource;
import io.resource.ResourceDelegate;
import core.world.Picture;
import io.resource.DataPackage;
import io.resource.ResourceReader;
import io.util.FileSearch;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import Editor.renderer.DataBoxRenderer;

/**
 *
 * @author mcherry
 */
public class ResourceMiniViewer extends javax.swing.JDialog {

    // Variable Declaraction
    // Java Native Classes
    private ArrayList<Rectangle> rectangles;
    private Color sliceColor = Color.BLACK;
    // Swing Classes
    private JPanel imageJPanel;
    // Project Classes
    private Animation resource;
    private FaustEditor editor;
    private Picture faustImage;
    private ResourceDelegate delegate;
    private DataPackage dataPackage;
    // Data Types
    private boolean edit;
    private int milliseconds;
    // End of Variable Declaration

    public ResourceMiniViewer(FaustEditor editor, ResourceDelegate delegate, Animation resource, boolean modal) {

        //
        super(editor, modal);
        initComponents();

        //
        this.editor = editor;
        this.delegate = delegate;
        this.resource = resource;

        // Initialize
        init();
    }

    private void init() {

        // List of rectangles that define the cuts to be made to the source image
        rectangles = new ArrayList<>();

        // Model for the plugins.
        final DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
        comboModel.addElement(ResourceDelegate.UNPACKAGED_STATEMENT);

        // Apply the model and other options to the JComboBox
        pluginJComboBox.setRenderer(new DataBoxRenderer());
        pluginJComboBox.setModel(comboModel);

        // Quick check to see if you provided an existing animation; If not create a default one
        if (resource != null) {

            // Grab its graphic
            faustImage = resource.getPicture();

            // If is existing animation grab image as well
            if (faustImage != null) {

                // Do graphic check
                doGraphicCheck();
            }
        } else {

            // Create empty animation, and leave the PluginJComboBox enabled
            resource = new Animation();

            // Label as 'loose' by default
            pluginJComboBox.setSelectedIndex(0);
        }

        // Do the dataPackage check
        doPluginCheck(comboModel);

        // Quick check to see if the animation viewer can even be opened; if not disable it.
        if (resource.length() <= 0) {
            animationJButton.setEnabled(false);
        }

        // Attempt to set to preset values from image attributes
        blockRowJField.setValue(resource.getBlockRows());
        blockColumnJField.setValue(resource.getBlockColumns());
        blockXOffsetJField.setValue(resource.getBlockXOffset());
        blockYOffsetJField.setValue(resource.getBlockYOffset());
        blockVGapJField.setValue(resource.getBlockHGap());
        blockHGapJField.setValue(resource.getBlockVGap());
        blockWidthJField.setValue(resource.getBlockWidth());
        blockHeightJField.setValue(resource.getBlockHeight());


        // Grab the toolkit to grab some icons from the classpath
        final Toolkit kit = Toolkit.getDefaultToolkit();
        final Class closs = getClass();

        // Define the color icon
        final ImageIcon icon = ResourceReader.readClassPathIcon(closs, "/icons/icon-color-chooser24.png");

        // Adjust the colorJButton
        colorJButton.setIcon(icon);
        colorJButton.setContentAreaFilled(false);

        // Set as viewport view
        imageJScrollPane.setViewportView(imageJPanel);

        ToolTipManager.sharedInstance().registerComponent(nameJField);

        ToolTipManager.sharedInstance().registerComponent(idJField);

        ToolTipManager.sharedInstance().registerComponent(displayJField);

        // Refresh everything
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

        contentJTabbedPane = new javax.swing.JTabbedPane();
        imageJScrollPane = new javax.swing.JScrollPane();
        sliceTabJPanel = new javax.swing.JPanel();
        sliceJPanel = new javax.swing.JPanel();
        blockYOffsetJLabel = new javax.swing.JLabel();
        blockYOffsetJField = new javax.swing.JFormattedTextField();
        blockYOffsetJSpinner = new javax.swing.JSpinner();
        blockWidthJLabel = new javax.swing.JLabel();
        blockWidthJField = new javax.swing.JFormattedTextField();
        blockWidthJSpinner = new javax.swing.JSpinner();
        blockHeightJLabel = new javax.swing.JLabel();
        blockHeightJField = new javax.swing.JFormattedTextField();
        blockHeightJSpinner = new javax.swing.JSpinner();
        blockXOffsetJLabel = new javax.swing.JLabel();
        blockXOffsetJField = new javax.swing.JFormattedTextField();
        blockXOffsetJSpinner = new javax.swing.JSpinner();
        blockColumnJLabel = new javax.swing.JLabel();
        blockVGapJLabel = new javax.swing.JLabel();
        blockHGapJLabel = new javax.swing.JLabel();
        blockRowJField = new javax.swing.JFormattedTextField();
        blockColumnJField = new javax.swing.JFormattedTextField();
        blockVGapJField = new javax.swing.JFormattedTextField();
        blockHGapJField = new javax.swing.JFormattedTextField();
        blockRowJSpinner = new javax.swing.JSpinner();
        blockColumnJSpinner = new javax.swing.JSpinner();
        blockVGapJSpinner = new javax.swing.JSpinner();
        blockHGapJSpinner = new javax.swing.JSpinner();
        blockRowJLabel = new javax.swing.JLabel();
        settingTabJPanel = new javax.swing.JPanel();
        settingJPanel = new javax.swing.JPanel();
        nameJLabel = new javax.swing.JLabel();
        nameJField = new javax.swing.JTextField();
        locationJField = new javax.swing.JTextField();
        locationJLabel = new javax.swing.JLabel();
        delayJLabel = new javax.swing.JLabel();
        delayJField = new javax.swing.JFormattedTextField();
        editorJLabel = new javax.swing.JLabel();
        idJField = new javax.swing.JTextField();
        referenceJLabel = new javax.swing.JLabel();
        displayJField = new javax.swing.JTextField();
        pluginJLabel = new javax.swing.JLabel();
        pluginJComboBox = new javax.swing.JComboBox();
        imageJButton = new javax.swing.JButton();
        animationJButton = new javax.swing.JButton();
        labelJPanel = new javax.swing.JPanel();
        widthJLabel = new javax.swing.JLabel();
        heightJLabel = new javax.swing.JLabel();
        buttonJPanel = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        colorJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        cancelJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Animation Editing");
        setMinimumSize(new java.awt.Dimension(296, 335));
        setResizable(false);

        contentJTabbedPane.setMaximumSize(new java.awt.Dimension(276, 276));
        contentJTabbedPane.setMinimumSize(new java.awt.Dimension(276, 276));
        contentJTabbedPane.setPreferredSize(new java.awt.Dimension(276, 276));

        imageJScrollPane.setMaximumSize(new java.awt.Dimension(240, 240));
        imageJScrollPane.setMinimumSize(new java.awt.Dimension(240, 240));
        imageJScrollPane.setPreferredSize(new java.awt.Dimension(240, 240));
        contentJTabbedPane.addTab("Graphical View", imageJScrollPane);

        java.awt.GridBagLayout jPanel3Layout = new java.awt.GridBagLayout();
        jPanel3Layout.columnWidths = new int[] {0, 5, 0, 5, 0};
        jPanel3Layout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        sliceJPanel.setLayout(jPanel3Layout);

        blockYOffsetJLabel.setText("Block Y-Offset:");
        blockYOffsetJLabel.setEnabled(false);
        blockYOffsetJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        blockYOffsetJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        blockYOffsetJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        blockYOffsetJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockYOffsetJLabel, gridBagConstraints);

        blockYOffsetJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockYOffsetJField.setText("0");
        blockYOffsetJField.setEnabled(false);
        blockYOffsetJField.setMaximumSize(new java.awt.Dimension(90, 22));
        blockYOffsetJField.setMinimumSize(new java.awt.Dimension(90, 22));
        blockYOffsetJField.setPreferredSize(new java.awt.Dimension(90, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockYOffsetJField, gridBagConstraints);

        blockYOffsetJSpinner.setEnabled(false);
        blockYOffsetJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockYOffsetJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockYOffsetJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockYOffsetJSpinner, gridBagConstraints);

        blockWidthJLabel.setText("Block Width:");
        blockWidthJLabel.setEnabled(false);
        blockWidthJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        blockWidthJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        blockWidthJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        blockWidthJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockWidthJLabel, gridBagConstraints);

        blockWidthJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockWidthJField.setText("0");
        blockWidthJField.setEnabled(false);
        blockWidthJField.setMaximumSize(new java.awt.Dimension(90, 22));
        blockWidthJField.setMinimumSize(new java.awt.Dimension(90, 22));
        blockWidthJField.setPreferredSize(new java.awt.Dimension(90, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockWidthJField, gridBagConstraints);

        blockWidthJSpinner.setEnabled(false);
        blockWidthJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockWidthJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockWidthJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockWidthJSpinner, gridBagConstraints);

        blockHeightJLabel.setText("Block Height:");
        blockHeightJLabel.setEnabled(false);
        blockHeightJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        blockHeightJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        blockHeightJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        blockHeightJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockHeightJLabel, gridBagConstraints);

        blockHeightJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockHeightJField.setText("0");
        blockHeightJField.setEnabled(false);
        blockHeightJField.setMaximumSize(new java.awt.Dimension(90, 22));
        blockHeightJField.setMinimumSize(new java.awt.Dimension(90, 22));
        blockHeightJField.setPreferredSize(new java.awt.Dimension(90, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockHeightJField, gridBagConstraints);

        blockHeightJSpinner.setEnabled(false);
        blockHeightJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockHeightJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockHeightJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockHeightJSpinner, gridBagConstraints);

        blockXOffsetJLabel.setText("Block X-Offset:");
        blockXOffsetJLabel.setEnabled(false);
        blockXOffsetJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        blockXOffsetJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        blockXOffsetJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        blockXOffsetJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockXOffsetJLabel, gridBagConstraints);

        blockXOffsetJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockXOffsetJField.setText("0");
        blockXOffsetJField.setEnabled(false);
        blockXOffsetJField.setMaximumSize(new java.awt.Dimension(90, 22));
        blockXOffsetJField.setMinimumSize(new java.awt.Dimension(90, 22));
        blockXOffsetJField.setPreferredSize(new java.awt.Dimension(90, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockXOffsetJField, gridBagConstraints);

        blockXOffsetJSpinner.setEnabled(false);
        blockXOffsetJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockXOffsetJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockXOffsetJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockXOffsetJSpinner, gridBagConstraints);

        blockColumnJLabel.setText("Block Columns:");
        blockColumnJLabel.setEnabled(false);
        blockColumnJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        blockColumnJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        blockColumnJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        blockColumnJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockColumnJLabel, gridBagConstraints);

        blockVGapJLabel.setText("Block V-Gap:");
        blockVGapJLabel.setEnabled(false);
        blockVGapJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        blockVGapJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        blockVGapJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        blockVGapJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockVGapJLabel, gridBagConstraints);

        blockHGapJLabel.setText("Block H-Gap:");
        blockHGapJLabel.setEnabled(false);
        blockHGapJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        blockHGapJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        blockHGapJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        blockHGapJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockHGapJLabel, gridBagConstraints);

        blockRowJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockRowJField.setText("0");
        blockRowJField.setEnabled(false);
        blockRowJField.setMaximumSize(new java.awt.Dimension(90, 22));
        blockRowJField.setMinimumSize(new java.awt.Dimension(90, 22));
        blockRowJField.setPreferredSize(new java.awt.Dimension(90, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockRowJField, gridBagConstraints);

        blockColumnJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockColumnJField.setText("0");
        blockColumnJField.setEnabled(false);
        blockColumnJField.setMaximumSize(new java.awt.Dimension(90, 22));
        blockColumnJField.setMinimumSize(new java.awt.Dimension(90, 22));
        blockColumnJField.setPreferredSize(new java.awt.Dimension(90, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockColumnJField, gridBagConstraints);

        blockVGapJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockVGapJField.setText("0");
        blockVGapJField.setEnabled(false);
        blockVGapJField.setMaximumSize(new java.awt.Dimension(90, 22));
        blockVGapJField.setMinimumSize(new java.awt.Dimension(90, 22));
        blockVGapJField.setPreferredSize(new java.awt.Dimension(90, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockVGapJField, gridBagConstraints);

        blockHGapJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockHGapJField.setText("0");
        blockHGapJField.setEnabled(false);
        blockHGapJField.setMaximumSize(new java.awt.Dimension(90, 22));
        blockHGapJField.setMinimumSize(new java.awt.Dimension(90, 22));
        blockHGapJField.setPreferredSize(new java.awt.Dimension(90, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockHGapJField, gridBagConstraints);

        blockRowJSpinner.setEnabled(false);
        blockRowJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockRowJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockRowJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockRowJSpinner, gridBagConstraints);

        blockColumnJSpinner.setEnabled(false);
        blockColumnJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockColumnJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockColumnJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockColumnJSpinner, gridBagConstraints);

        blockVGapJSpinner.setEnabled(false);
        blockVGapJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockVGapJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockVGapJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockVGapJSpinner, gridBagConstraints);

        blockHGapJSpinner.setEnabled(false);
        blockHGapJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockHGapJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockHGapJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockHGapJSpinner, gridBagConstraints);

        blockRowJLabel.setText("Block Rows:");
        blockRowJLabel.setEnabled(false);
        blockRowJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        blockRowJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        blockRowJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        blockRowJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockRowJLabel, gridBagConstraints);

        javax.swing.GroupLayout sliceTabJPanelLayout = new javax.swing.GroupLayout(sliceTabJPanel);
        sliceTabJPanel.setLayout(sliceTabJPanelLayout);
        sliceTabJPanelLayout.setHorizontalGroup(
            sliceTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sliceTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sliceJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sliceTabJPanelLayout.setVerticalGroup(
            sliceTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sliceTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sliceJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        contentJTabbedPane.addTab("Slice Settings", sliceTabJPanel);

        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWidths = new int[] {0, 5, 0};
        jPanel1Layout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        settingJPanel.setLayout(jPanel1Layout);

        nameJLabel.setText("Editor Name:");
        nameJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nameJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        nameJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        nameJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(nameJLabel, gridBagConstraints);

        nameJField.setMaximumSize(new java.awt.Dimension(140, 22));
        nameJField.setMinimumSize(new java.awt.Dimension(140, 22));
        nameJField.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(nameJField, gridBagConstraints);

        locationJField.setEnabled(false);
        locationJField.setMaximumSize(new java.awt.Dimension(140, 22));
        locationJField.setMinimumSize(new java.awt.Dimension(140, 22));
        locationJField.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(locationJField, gridBagConstraints);

        locationJLabel.setText("Image Location:");
        locationJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        locationJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        locationJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        locationJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(locationJLabel, gridBagConstraints);

        delayJLabel.setText("Timer Delay (ms):");
        delayJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        delayJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        delayJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(delayJLabel, gridBagConstraints);

        delayJField.setEditable(false);
        delayJField.setText("1000");
        delayJField.setEnabled(false);
        delayJField.setMaximumSize(new java.awt.Dimension(140, 22));
        delayJField.setMinimumSize(new java.awt.Dimension(140, 22));
        delayJField.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(delayJField, gridBagConstraints);

        editorJLabel.setText("Editor Id Tag:");
        editorJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editorJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        editorJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        editorJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(editorJLabel, gridBagConstraints);

        idJField.setMaximumSize(new java.awt.Dimension(140, 22));
        idJField.setMinimumSize(new java.awt.Dimension(140, 22));
        idJField.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(idJField, gridBagConstraints);

        referenceJLabel.setText("Display Name:");
        referenceJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        referenceJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        referenceJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        referenceJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(referenceJLabel, gridBagConstraints);

        displayJField.setMaximumSize(new java.awt.Dimension(140, 22));
        displayJField.setMinimumSize(new java.awt.Dimension(140, 22));
        displayJField.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(displayJField, gridBagConstraints);

        pluginJLabel.setText("Part of Package:");
        pluginJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        pluginJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        pluginJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        settingJPanel.add(pluginJLabel, gridBagConstraints);

        pluginJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        pluginJComboBox.setMaximumSize(new java.awt.Dimension(140, 22));
        pluginJComboBox.setMinimumSize(new java.awt.Dimension(140, 22));
        pluginJComboBox.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        settingJPanel.add(pluginJComboBox, gridBagConstraints);

        imageJButton.setText("Change Source Image");
        imageJButton.setMaximumSize(new java.awt.Dimension(145, 24));
        imageJButton.setMinimumSize(new java.awt.Dimension(145, 24));
        imageJButton.setPreferredSize(new java.awt.Dimension(145, 24));
        imageJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageJButtonActionPerformed(evt);
            }
        });

        animationJButton.setText("Resource");
        animationJButton.setMaximumSize(new java.awt.Dimension(102, 24));
        animationJButton.setMinimumSize(new java.awt.Dimension(102, 24));
        animationJButton.setPreferredSize(new java.awt.Dimension(102, 24));
        animationJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animationJButtonActionPerformed(evt);
            }
        });

        widthJLabel.setText("Width :");
        widthJLabel.setEnabled(false);
        widthJLabel.setMaximumSize(new java.awt.Dimension(50, 24));
        widthJLabel.setMinimumSize(new java.awt.Dimension(50, 24));
        widthJLabel.setPreferredSize(new java.awt.Dimension(50, 24));

        heightJLabel.setText("Height:");
        heightJLabel.setEnabled(false);
        heightJLabel.setMaximumSize(new java.awt.Dimension(50, 24));
        heightJLabel.setMinimumSize(new java.awt.Dimension(50, 24));
        heightJLabel.setPreferredSize(new java.awt.Dimension(50, 24));

        javax.swing.GroupLayout labelJPanelLayout = new javax.swing.GroupLayout(labelJPanel);
        labelJPanel.setLayout(labelJPanelLayout);
        labelJPanelLayout.setHorizontalGroup(
            labelJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(widthJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(heightJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        labelJPanelLayout.setVerticalGroup(
            labelJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelJPanelLayout.createSequentialGroup()
                .addComponent(widthJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(heightJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout settingTabJPanelLayout = new javax.swing.GroupLayout(settingTabJPanel);
        settingTabJPanel.setLayout(settingTabJPanelLayout);
        settingTabJPanelLayout.setHorizontalGroup(
            settingTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(settingJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                    .addGroup(settingTabJPanelLayout.createSequentialGroup()
                        .addComponent(labelJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(settingTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(imageJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(animationJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        settingTabJPanelLayout.setVerticalGroup(
            settingTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settingJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(settingTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingTabJPanelLayout.createSequentialGroup()
                        .addComponent(imageJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(animationJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 4, Short.MAX_VALUE))
                    .addComponent(labelJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        contentJTabbedPane.addTab("Manifest Settings", settingTabJPanel);

        buttonJPanel.setMinimumSize(new java.awt.Dimension(228, 26));
        buttonJPanel.setPreferredSize(new java.awt.Dimension(484, 26));
        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));
        buttonJPanel.add(filler1);

        colorJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        colorJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        colorJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        colorJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(colorJButton);
        buttonJPanel.add(filler2);

        cancelJButton.setText("Close");
        cancelJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        cancelJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        cancelJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(cancelJButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(contentJTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void imageJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageJButtonActionPerformed

        // Open the Content Manager
        final ResourceSelector manager = new ResourceSelector(this, delegate, true);
        manager.setFilterType(Picture.class);
        manager.setLocationRelativeTo(this);
        manager.setVisible(true);

        // It will close on its own
        final WorldResource resource = manager.getResource();

        if (resource != null) {

            // Cast to a resource image
            final Picture image = (Picture) resource;

            // Set the graphic
            faustImage = image;

            // Set the graphic name
            locationJField.setText(image.getDisplayName());
            widthJLabel.setText("Width: " + image.getImage().getWidth(this));
            heightJLabel.setText("Height: " + image.getImage().getHeight(this));
        }
    }//GEN-LAST:event_imageJButtonActionPerformed

    private void animationJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_animationJButtonActionPerformed

        // Animation must exist before the animation viewer can be shown
        if (resource != null) {

            // Animation must also have a single frame at minimum
            if (resource.length() > 0) {

                // Show the animation viewer
                final AnimationViewer viewer = new AnimationViewer(this, resource, true);
                viewer.setLocationRelativeTo(this);
                viewer.setVisible(true);

                // Grab the adjusted time
                milliseconds = viewer.getDelay();
                delayJField.setValue(milliseconds);

                // Also grab starting index -- soon
                viewer.dispose();
            }
        }
    }//GEN-LAST:event_animationJButtonActionPerformed

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed

        // Cancel
        setVisible(false);
    }//GEN-LAST:event_cancelJButtonActionPerformed

    private void colorJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorJButtonActionPerformed

        // Show the color chooser
        final Color color = JColorChooser.showDialog(this, "Change Outline Color", sliceColor);

        if (color != null) {
            sliceColor = color;
        }

        // Repaint the JPanel
        repaint();
    }//GEN-LAST:event_colorJButtonActionPerformed

    private void refresh() {

        // Get the width and height.
        if (faustImage != null) {

            // Store some temporary widths and heights
            final int graphicWidth = faustImage.getImage().getWidth(this);
            final int graphicHeight = faustImage.getImage().getHeight(this);
            final int panelWidth = imageJPanel.getPreferredSize().width;
            final int panelHeight = imageJPanel.getPreferredSize().height;

            // Update the user interface
            imageJPanel.updateUI();

            // Temporarily store a dimension to apply across the imageJPanel's size
            final Dimension dimension = new Dimension(graphicWidth < panelWidth ? panelWidth : graphicWidth, graphicHeight < panelHeight ? panelHeight : graphicHeight);

            // Apply this dimension to all the Sizes and revalidate the JPanel
            imageJPanel.setPreferredSize(dimension);
            imageJPanel.setMinimumSize(dimension);
            imageJPanel.setMaximumSize(dimension);
            imageJPanel.setSize(dimension);
            imageJPanel.revalidate();

            // Grab information from the graphic
            final int blockWidth = ((Number) blockWidthJSpinner.getValue()).intValue();
            final int blockHeight = ((Number) blockHeightJSpinner.getValue()).intValue();
            final int horiOffset = ((Number) blockXOffsetJSpinner.getValue()).intValue();
            final int vertOffset = ((Number) blockYOffsetJSpinner.getValue()).intValue();
            final int horiGap = ((Number) blockHGapJSpinner.getValue()).intValue();
            final int vertGap = ((Number) blockVGapJSpinner.getValue()).intValue();

            // Clear the current arraylist of rectangles to avoid overlap
            rectangles.clear();

            // Create the boxes which mimic the slices to be made
            for (int i = 0; i < ((Number) blockRowJSpinner.getValue()).intValue(); i++) {
                for (int j = 0; j < ((Number) blockColumnJSpinner.getValue()).intValue(); j++) {

                    // Create the new rectangle at the precise location of the cut to be made
                    Rectangle rectangle = new Rectangle(horiOffset + (i * (blockWidth + horiGap)), vertOffset + (j * (blockHeight + vertGap)), blockWidth, blockHeight);

                    // Populate the rectangle arraylist width this cut
                    rectangles.add(rectangle);
                }
            }
        }

        // Repaint
        repaint();
    }

    /*
     *  Quick method to copy across similar classes to ensure that the graphic is up to date -- Keeps init cleaner
     */
    private void doGraphicCheck() {

        // Grab some information from the faustImage
        final int graphicWidth = faustImage.getImage().getWidth(this);
        final int graphicHeight = faustImage.getImage().getHeight(this);

        // Grab the milliseonds from the animation that exists
        milliseconds = resource.getDelay();

        // Set width and height of graphic
        blockWidthJField.setValue(graphicWidth);
        blockHeightJField.setValue(graphicHeight);

        // Set the width and height JLabel to match above
        widthJLabel.setText("Width: " + graphicWidth);
        heightJLabel.setText("Height: " + graphicHeight);

        // Adjust the Delay and Location JFields
        delayJField.setValue(milliseconds);
        locationJField.setText(faustImage.getDisplayName());
    }

    /*
     *  Quick method to copy across similar classes to ensure all the dataPackage related information is up to date.
     */
    private void doPluginCheck(DefaultComboBoxModel model) {

        // Plugin check
        final String packageID = resource.getPackageId();

        // Grab all the plugins
        final DataPackage[] plugins = delegate.getDataPackages();

        // Iterate over the list of ResourcePlugins
        for (int i = 0; i < plugins.length; i++) {

            // Grab the current ResourcePlugin
            final DataPackage iPackage = plugins[i];

            // Add to the model
            model.addElement(iPackage);

            // If and only if dataPackage exists
            if (packageID != null) {

                // Quick check
                if (iPackage.getReferenceId().equalsIgnoreCase(packageID)) {

                    // Assign
                    dataPackage = iPackage;
                }
            }
        }

        // Quick check to see if the Resource belongs to a specific dataPackage, if not label as 'loose'
        if (dataPackage != null) {
            pluginJComboBox.setSelectedItem(dataPackage);

            // Disable the ComboBox if this animation existed before the dialog was opened
            pluginJComboBox.setEditable(false);
            pluginJComboBox.setEnabled(false);
            edit = true;
        } else {

            // No package does not nessecarily mean no edit
            pluginJComboBox.setSelectedItem(ResourceDelegate.UNPACKAGED_STATEMENT);

            // Editor Name includes extension
            if (resource.getReferenceName() != null) {

                //
                FileSearch search = new FileSearch(new File(delegate.getDataDirectory()), resource.getReferenceName(), true);

                //
                search.perform();

                // Search for it including file extension (Will find loose files and not files inside of data packages)
                final File file = search.check(resource.getSHA1CheckSum());

                // If we found the file set edit to true
                if (file != null) {
                    edit = true;
                }
            } else {
                edit = false;
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton animationJButton;
    private javax.swing.JFormattedTextField blockColumnJField;
    private javax.swing.JLabel blockColumnJLabel;
    private javax.swing.JSpinner blockColumnJSpinner;
    private javax.swing.JFormattedTextField blockHGapJField;
    private javax.swing.JLabel blockHGapJLabel;
    private javax.swing.JSpinner blockHGapJSpinner;
    private javax.swing.JFormattedTextField blockHeightJField;
    private javax.swing.JLabel blockHeightJLabel;
    private javax.swing.JSpinner blockHeightJSpinner;
    private javax.swing.JFormattedTextField blockRowJField;
    private javax.swing.JLabel blockRowJLabel;
    private javax.swing.JSpinner blockRowJSpinner;
    private javax.swing.JFormattedTextField blockVGapJField;
    private javax.swing.JLabel blockVGapJLabel;
    private javax.swing.JSpinner blockVGapJSpinner;
    private javax.swing.JFormattedTextField blockWidthJField;
    private javax.swing.JLabel blockWidthJLabel;
    private javax.swing.JSpinner blockWidthJSpinner;
    private javax.swing.JFormattedTextField blockXOffsetJField;
    private javax.swing.JLabel blockXOffsetJLabel;
    private javax.swing.JSpinner blockXOffsetJSpinner;
    private javax.swing.JFormattedTextField blockYOffsetJField;
    private javax.swing.JLabel blockYOffsetJLabel;
    private javax.swing.JSpinner blockYOffsetJSpinner;
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JButton colorJButton;
    private javax.swing.JTabbedPane contentJTabbedPane;
    private javax.swing.JFormattedTextField delayJField;
    private javax.swing.JLabel delayJLabel;
    private javax.swing.JTextField displayJField;
    private javax.swing.JLabel editorJLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel heightJLabel;
    private javax.swing.JTextField idJField;
    private javax.swing.JButton imageJButton;
    private javax.swing.JScrollPane imageJScrollPane;
    private javax.swing.JPanel labelJPanel;
    private javax.swing.JTextField locationJField;
    private javax.swing.JLabel locationJLabel;
    private javax.swing.JTextField nameJField;
    private javax.swing.JLabel nameJLabel;
    private javax.swing.JComboBox pluginJComboBox;
    private javax.swing.JLabel pluginJLabel;
    private javax.swing.JLabel referenceJLabel;
    private javax.swing.JPanel settingJPanel;
    private javax.swing.JPanel settingTabJPanel;
    private javax.swing.JPanel sliceJPanel;
    private javax.swing.JPanel sliceTabJPanel;
    private javax.swing.JLabel widthJLabel;
    // End of variables declaration//GEN-END:variables
}
