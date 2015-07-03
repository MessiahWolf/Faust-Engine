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
import core.world.Tileset;
import io.resource.ResourceDelegate;
import core.world.Picture;
import core.world.Illustration;
import core.world.Backdrop;
import io.resource.ResourceReader;
import io.resource.ResourceWriter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.JColorChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import Editor.listener.ManifestBinder;

/**
 *
 * @author Robert Antuan Cherry
 */
public class IllustrationEditor extends javax.swing.JDialog {

    // Variable Declaraction
    // Java Native Classes
    private ArrayList<Rectangle> array;
    private Color colorOutline = Color.BLACK;
    // Project Classes
    private AnimationViewer viewer;
    private ManifestBinder binder;
    private Picture picture;
    private DelegateCheckBox box;
    private Illustration illustration;
    private ImagePanel imagePanel;
    private ResourceDelegate delegate;
    // End of Variable Declaration

    public IllustrationEditor(Window editor, ResourceDelegate delegate, Illustration illustration, boolean modal) {

        // Super stuff
        super(editor);
        setModal(modal);
        initComponents();

        // Assign
        this.delegate = delegate;
        this.illustration = illustration;

        // Initialize
        init();
    }

    private void init() {

        //
        array = new ArrayList<>();

        //
        setupResource();

        // Setup the controls
        setupDialog();

        // Set as viewport view
        imageJScrollPane.setViewportView(imagePanel);

        // Refresh everything
        refresh();
    }

    private void updateAnimationViewer() {

        //
        if (viewer != null) {

            // Giving an animation to the viewer
            final Animation animation = new Animation(null, null, null, null, null);

            // Setting those values, mang.
            animation.setBlockRows(((Number) blockRowJSpinner.getValue()).intValue());
            animation.setBlockColumns(((Number) blockColumnJSpinner.getValue()).intValue());
            animation.setBlockWidth(((Number) blockWidthJSpinner.getValue()).intValue());
            animation.setBlockHeight(((Number) blockHeightJSpinner.getValue()).intValue());
            animation.setBlockHGap(((Number) blockHGapJSpinner.getValue()).intValue());
            animation.setBlockVGap(((Number) blockVGapJSpinner.getValue()).intValue());
            animation.setBlockXOffset(((Number) blockXOffsetJSpinner.getValue()).intValue());
            animation.setBlockYOffset(((Number) blockYOffsetJSpinner.getValue()).intValue());
            animation.setPicture(picture);
            animation.updateAttributes();

            try {
                //
                animation.validate();
            } catch (java.awt.image.RasterFormatException rfe) {
                System.out.println("Found it");
            }

            // Give it the animation
            viewer.setAnimation(animation);
        }
    }

    private void setupResource() {

        // @note Creating or Adapting to resource given
        if (illustration != null) {

            // If is existing tileset grab image as well
            if (illustration.getPicture() != null) {

                // Grab the graphic from existing
                picture = illustration.getPicture();

                // Do the graphic check
                performGraphicCheck();
            }
        }
    }

    private void setupDialog() {

        // Giving models to spinners
        blockRowJSpinner.setModel(getSpinnerModelInstance());
        blockColumnJSpinner.setModel(getSpinnerModelInstance());
        blockWidthJSpinner.setModel(getSpinnerModelInstance());
        blockHeightJSpinner.setModel(getSpinnerModelInstance());
        blockHGapJSpinner.setModel(getSpinnerModelInstance());
        blockVGapJSpinner.setModel(getSpinnerModelInstance());
        blockXOffsetJSpinner.setModel(getSpinnerModelInstance());
        blockYOffsetJSpinner.setModel(getSpinnerModelInstance());

        // Attempt to set to preset values from image attributes
        blockRowJSpinner.setValue(illustration.getBlockRows());
        blockColumnJField.setValue(illustration.getBlockColumns());
        blockXOffsetJField.setValue(illustration.getBlockXOffset());
        blockYOffsetJField.setValue(illustration.getBlockYOffset());
        blockVGapJField.setValue(illustration.getBlockHGap());
        blockHGapJField.setValue(illustration.getBlockVGap());
        blockWidthJField.setValue(illustration.getBlockWidth());
        blockHeightJField.setValue(illustration.getBlockHeight());

        //
        swapCheckboxLimits(true);

        //
        final SpinnerNumberModel model = new SpinnerNumberModel(illustration instanceof Animation ? ((Animation) illustration).getDelay() : 0, 0, 10000, 1);
        delayJSpinner.setModel(model);

        //
        setupManifestBinder();

        //
        settingJPanel.setLayout(null);

        //
        imagePanel = new ImagePanel(imageJScrollPane) {
            @Override
            public void paintComponent(Graphics monet) {

                //
                super.paintComponent(monet);

                //
                paintGuidelines(monet);
            }
        };

        //
        final Dimension dimension = imageJScrollPane.getPreferredSize();
        dimension.setSize(dimension.width - 2, dimension.height - 2);

        //
        imagePanel.setPreferredSize(dimension);
        imagePanel.updatePanel(illustration);
        imagePanel.setShowTextile(true);
        imagePanel.setShowImage(false);

        // Set TItle -- I will reorganize all Dialogs soon so that the code appears cleaner
        if (illustration.getDisplayName() != null) {
            setTitle("Illustration Editing: " + illustration.getDisplayName());
        } else {
            setTitle("Illustration Editor");
        }

        // Grab the toolkit to grab some icons from the classpath
        final Class closs = getClass();

        //
        autoCompleteJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/icons/icon-complete16.png"));
        colorJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/icons/icon-color-chooser16.png"));

        // Resource Disbaling section
        if (illustration instanceof Backdrop) {

            //
            imagePanel.setShowImage(true);

            //
            final Backdrop backdrop = (Backdrop) illustration;

            // Set and simulate a user click
            stretchJCheckBox.setSelected(backdrop.isStretching());
            updateStretchBox();

            // Disabling stuff.
            delayJField.setEnabled(false);
            delayJField.setEditable(false);
            delayJLabel.setEnabled(false);
            delayJSpinner.setEnabled(false);

            //
            nullJLabel1.setText("Change how the Image will be Repeated");

            // Changing the Title text
            titleJLabel.setText("Change and View Backdrops");
        } else if (illustration instanceof Animation) {

            //
            delayJField.setEnabled(true);
            delayJField.setEditable(true);
            delayJLabel.setEnabled(true);

            //
            nullJLabel6.setEnabled(false);

            //
            titleJLabel.setText("Change and View Animations");

            //
            stretchJCheckBox.setEnabled(false);

            //
            setupAnimationViewer();
        } else if (illustration instanceof Tileset) {

            //
            delayJField.setEnabled(false);
            delayJField.setEditable(false);
            delayJLabel.setEnabled(false);

            //
            nullJLabel2.setEnabled(false);
            nullJLabel6.setEnabled(false);

            //
            titleJLabel.setText("Change and View Tilesets");

            //
            stretchJCheckBox.setEnabled(false);
        }
    }

    private void setupManifestBinder() {

        //
        box = new DelegateCheckBox(delegate);
        buttonJPanel.add(box, 0);

        // Testing it out.
        binder = new ManifestBinder(delegate, illustration);

        // Binding stuff manually.
        binder.bind(ManifestBinder.BOX_DELEGATE, box);
        binder.bind(ManifestBinder.BUTTON_GENERATE, generateJButton);

        //
        binder.bind(ManifestBinder.FIELD_DISPLAY, displayJField);
        binder.bind(ManifestBinder.FIELD_REFERENCE, referenceJField);
        binder.bind(ManifestBinder.FIELD_NAME, nameJField);
        binder.bind(ManifestBinder.FIELD_LOCATION, locationJField);
        binder.bind(ManifestBinder.FIELD_WIDTH, widthJField);
        binder.bind(ManifestBinder.FIELD_HEIGHT, heightJField);
        binder.bind(ManifestBinder.FIELD_PLUGIN, packageJField);

        binder.invoke();

        //
        binder.setEdit(delegate.getInstanceCount(illustration) >= 1 ? true : false);
        generateJButton.setEnabled(!binder.isEditting());
    }

    private void setupAnimationViewer() {

        //
        viewer = new AnimationViewer(this, null, false);

        //
        updateAnimationViewer();
    }

    private void finish() {

        // Clear the list
        refresh();

        // Place the values in the graphic attributes map
        try {

            // Ask first
            if (box.isSelected()) {

                // Number formatter
                final NumberFormat format = NumberFormat.getInstance();

                // Set Rows and Columns
                illustration.setBlockRows(format.parse(String.valueOf(blockRowJField.getValue())).intValue());
                illustration.setBlockColumns(format.parse(String.valueOf(blockColumnJField.getValue())).intValue());

                // Set H and V Gap
                illustration.setBlockHGap(format.parse(String.valueOf(blockHGapJField.getValue())).intValue());
                illustration.setBlockVGap(format.parse(String.valueOf(blockVGapJField.getValue())).intValue());

                // Set X and Y Offset
                illustration.setBlockXOffset(format.parse(String.valueOf(blockXOffsetJField.getValue())).intValue());
                illustration.setBlockYOffset(format.parse(String.valueOf(blockYOffsetJField.getValue())).intValue());

                // Set width and height
                illustration.setBlockWidth(format.parse(String.valueOf(blockWidthJField.getValue())).intValue());
                illustration.setBlockHeight(format.parse(String.valueOf(blockWidthJField.getValue())).intValue());

                //
                write();
            }
        } catch (ParseException pe) {
            System.err.println(pe);
        }
    }

    private void write() {

        // If we can cut the image up into a graphic set
        if (box.isSelected()) {

            //
            illustration.setReferenceID(binder.getReferenceID());
            illustration.setReferenceName(binder.getReferenceName());
            illustration.setDisplayName(binder.getDisplayName());
            illustration.setPicture(picture);
            illustration.setPictureInfo(picture.getPackageId(), picture.getReferenceID());

            // Ensure value reflection in attributes map
            illustration.updateAttributes();

            // Validate the graphicset
            illustration.validate();

            // Easy Peasy
            ResourceWriter.write(delegate, illustration);

            // Add to delegate
            delegate.addResource(illustration);

            // Close this Dialog
            setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "Please check the information provided for fields marked invalid");
        }
    }

    private void refresh() {

        // Clear the current Collection
        if (array != null) {

            // Clear the current rectangle list / invalidate
            array.clear();

            //
            final int graphicWidth = picture == null || picture.getImage() == null ? 0 : picture.getImage().getWidth(this);
            final int graphicHeight = picture == null || picture.getImage() == null ? 0 : picture.getImage().getHeight(this);

            // Store for a second and solve for backdrops
            final int blockWidth = illustration instanceof Backdrop ? graphicWidth : ((Number) blockWidthJSpinner.getValue()).intValue();
            final int blockHeight = illustration instanceof Backdrop ? graphicHeight : ((Number) blockHeightJSpinner.getValue()).intValue();

            //
            final int blockXOffset = ((Number) blockXOffsetJSpinner.getValue()).intValue();
            final int blockYOffset = ((Number) blockYOffsetJSpinner.getValue()).intValue();
            final int blockHGap = ((Number) blockHGapJSpinner.getValue()).intValue();
            final int blockVGap = ((Number) blockVGapJSpinner.getValue()).intValue();
            final int blockRows = ((Number) blockRowJSpinner.getValue()).intValue();
            final int blockColumns = ((Number) blockColumnJSpinner.getValue()).intValue();

            //
            int totalWidth;
            int totalHeight;

            // Create the boxes which mimic the slices to be made
            for (int row = 0; row < blockRows; row++) {

                //
                for (int col = 0; col < blockColumns; col++) {

                    // Create the new rectangle at the precise location of the cut
                    final Rectangle rectangle = new Rectangle(blockXOffset + (row * (blockWidth + blockHGap)), blockYOffset + (col * (blockHeight + blockVGap)), blockWidth, blockHeight);

                    // Populate the rectangle arraylist
                    array.add(rectangle);
                }
            }

            //
            totalWidth = blockXOffset + (blockRows * (blockWidth + blockHGap));
            totalWidth -= blockRows > 1 ? blockHGap : 0;
            totalHeight = blockYOffset + (blockColumns * (blockHeight + blockVGap));
            totalHeight -= blockColumns > 1 ? blockVGap : 0;

            //
            updateAnimationViewer();

            // Repaint
            repaint();
        }
    }

    /*
     *  Quick method to copy across similar classes to ensure that the graphic is up to date -- Keeps init cleaner
     */
    private void performGraphicCheck() {

        //
        if (illustration == null) {
            return;
        }

        // Grab its picture
        picture = illustration.getPicture();

        //
        if (picture != null) {

            //
            final String pack = picture.getPackageId();
            final String location = picture.getPictureLocation();

            //
            packageJField.setText(pack);
            locationJField.setText(location);

            //
            if (picture.getImage() != null) {

                // Grab some information from the faustImage
                final int width = picture.getImage().getWidth(this);
                final int height = picture.getImage().getHeight(this);

                // Set width and height of graphic
                widthJField.setText(String.valueOf(width));
                heightJField.setText(String.valueOf(height));
            }
        }
    }

    private void performComponentSync(JFormattedTextField field, JSpinner spinner) {

        //
        if (field == null || spinner == null) {
            return;
        }

        // @Null-Check
        if (field.getValue() == null) {
            return;
        }

        // Grab the Value
        int value = ((Number) field.getValue()).intValue();

        //
        if (value < 0) {
            return;
        }

        // Apply to this field
        field.setValue((int) value);

        // Apply to newSpinner
        spinner.setValue((int) value);

        // Refresh
        refresh();
    }

    /**
     * Overrides the ImageJPanel's paintComponent method to draw additional
     * rectangles
     *
     * @param monet
     */
    private void paintGuidelines(Graphics monet) {

        //
        if (imagePanel == null || monet == null) {
            return;
        }

        //
        Image image = null;
        BufferedImage buffered;
        Graphics2D graphics;

        // Increasing offsets based on ImagePanels pointFocal
        int width = imagePanel.getPreferredSize().width;
        int height = imagePanel.getPreferredSize().height;

        //
        if (width <= 0 || height <= 0) {

            //
            width = imageJScrollPane.getPreferredSize().width;
            height = imageJScrollPane.getPreferredSize().height;
        }

        // First order of business is to acquire the Image from the Illustration
        if (picture != null) {

            //
            image = picture.getImage();

            //
            //if (illustration instanceof Backdrop) {

            width = image.getWidth(this) < width && width > 0 ? width : image.getWidth(this);
            height = image.getHeight(this) < height && height > 0 ? height : image.getHeight(this);
            //}
        }

        // Create our new buffered image and grab its graphics
        buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // We need this for actual rendering
        graphics = (Graphics2D) buffered.getGraphics();
        graphics.setColor(colorOutline);

        // Draw a preview of the slices to be made
        for (int i = 0; i < array.size(); i++) {

            // Adjust the Rectangle a little
            final Rectangle rectangle = new Rectangle(array.get(i));

            // Draw the outline after that
            graphics.setStroke(new BasicStroke(2f));
            graphics.draw(rectangle);
        }

        //
        if (image != null) {

            //
            monet.drawImage(image, 0, 0, this);
        }

        // Draw the buffered image
        monet.drawImage(buffered, 0, 0, this);
        monet.dispose();
    }

    private SpinnerNumberModel getSpinnerModelInstance() {
        return new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1);
    }

    private void autoComplete(Image image) {

        //
        final int imageWidth = image.getWidth(this);
        final int imageHeight = image.getHeight(this);

        //
        final int rows = 32;
        final int columns = imageHeight;

        // Automatically attempt to divide by 32, 32
        final int baseRows = imageWidth / rows;
        final int baseColumns = imageHeight / columns;

        //
        blockWidthJSpinner.setValue(rows);
        blockHeightJSpinner.setValue(columns);
        blockRowJSpinner.setValue(baseRows);
        blockColumnJSpinner.setValue(baseColumns);

        // Update other stuff here.

        // Doing reference id's and such here
        generateJButton.doClick();

        //
        refresh();
    }

    private void swapCheckboxLimits(boolean lock) {

        // Lock those maximums
        if (lock) {

            // Set maximums First
            ((SpinnerNumberModel) blockRowJSpinner.getModel()).setMaximum((illustration.getBlockRows() <= 0) ? Short.MAX_VALUE : illustration.getBlockRows());
            ((SpinnerNumberModel) blockColumnJSpinner.getModel()).setMaximum((illustration.getBlockColumns() <= 0) ? Short.MAX_VALUE : illustration.getBlockColumns());
            ((SpinnerNumberModel) blockXOffsetJSpinner.getModel()).setMaximum((illustration.getBlockXOffset() <= 0) ? Short.MAX_VALUE : illustration.getBlockXOffset());
            ((SpinnerNumberModel) blockYOffsetJSpinner.getModel()).setMaximum((illustration.getBlockYOffset() <= 0) ? Short.MAX_VALUE : illustration.getBlockYOffset());
            ((SpinnerNumberModel) blockVGapJSpinner.getModel()).setMaximum((illustration.getBlockVGap() <= 0) ? Short.MAX_VALUE : illustration.getBlockVGap());
            ((SpinnerNumberModel) blockHGapJSpinner.getModel()).setMaximum((illustration.getBlockHGap() <= 0) ? Short.MAX_VALUE : illustration.getBlockHGap());
            ((SpinnerNumberModel) blockWidthJSpinner.getModel()).setMaximum((illustration.getBlockWidth() <= 0) ? Short.MAX_VALUE : illustration.getBlockWidth());
            ((SpinnerNumberModel) blockHeightJSpinner.getModel()).setMaximum((illustration.getBlockHeight() <= 0) ? Short.MAX_VALUE : illustration.getBlockHeight());
        } else {

            // Release the maximums to Short.max_value
            ((SpinnerNumberModel) blockRowJSpinner.getModel()).setMaximum(Short.MAX_VALUE);
            ((SpinnerNumberModel) blockColumnJSpinner.getModel()).setMaximum(Short.MAX_VALUE);
            ((SpinnerNumberModel) blockXOffsetJSpinner.getModel()).setMaximum(Short.MAX_VALUE);
            ((SpinnerNumberModel) blockYOffsetJSpinner.getModel()).setMaximum(Short.MAX_VALUE);
            ((SpinnerNumberModel) blockVGapJSpinner.getModel()).setMaximum(Short.MAX_VALUE);
            ((SpinnerNumberModel) blockHGapJSpinner.getModel()).setMaximum(Short.MAX_VALUE);
            ((SpinnerNumberModel) blockWidthJSpinner.getModel()).setMaximum(Short.MAX_VALUE);
            ((SpinnerNumberModel) blockHeightJSpinner.getModel()).setMaximum(Short.MAX_VALUE);
        }
    }

    private void updateStretchBox() {

        //
        final Backdrop backdrop = (Backdrop) illustration;

        //
        final boolean bool = stretchJCheckBox.isSelected();

        // Set it in the backdrop
        backdrop.setStretching(bool);

        // The backdrop is set to stretch to bounds
        if (bool == true) {

            // When true disable the row and col fields and spinners
            blockRowJSpinner.setEnabled(false);
            blockRowJField.setEnabled(false);
            blockColumnJSpinner.setEnabled(false);
            blockColumnJField.setEnabled(false);
        } else {

            //
            blockRowJSpinner.setEnabled(true);
            blockColumnJSpinner.setEnabled(true);
            blockColumnJField.setEnabled(true);
            blockRowJField.setEnabled(true);
        }

        //
        swapCheckboxLimits(bool);
        System.err.println("USB :" + ((SpinnerNumberModel) blockRowJSpinner.getModel()).getMaximum());
    }

    public AnimationViewer getAnimationViewer() {
        return viewer;
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

        delayJField = new javax.swing.JFormattedTextField();
        buttonJPanel = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        colorJButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        autoCompleteJButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        finishJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        cancelJButton = new javax.swing.JButton();
        mainJTabbedPane = new javax.swing.JTabbedPane();
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
        nullJLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        settingJPanel = new javax.swing.JPanel();
        locationJField = new javax.swing.JTextField();
        locationJLabel = new javax.swing.JLabel();
        packageJLabel = new javax.swing.JLabel();
        packageJField = new javax.swing.JTextField();
        widthJLabel = new javax.swing.JLabel();
        heightJLabel = new javax.swing.JLabel();
        referenceJLabel = new javax.swing.JLabel();
        nameJLabel = new javax.swing.JLabel();
        referenceJField = new javax.swing.JTextField();
        nameJField = new javax.swing.JTextField();
        displayJField = new javax.swing.JTextField();
        displayJLabel = new javax.swing.JLabel();
        widthJField = new javax.swing.JTextField();
        heightJField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        generateJButton = new javax.swing.JButton();
        imageJButton = new javax.swing.JButton();
        backgroundRenderJPanel = new javax.swing.JPanel();
        animationRenderJPanel = new javax.swing.JPanel();
        delayJLabel = new javax.swing.JLabel();
        nullJLabel3 = new javax.swing.JLabel();
        nullJLabel4 = new javax.swing.JLabel();
        nullJLabel5 = new javax.swing.JLabel();
        delayJSpinner = new javax.swing.JSpinner();
        nullJLabel2 = new javax.swing.JLabel();
        nullJLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        nullJLabel7 = new javax.swing.JLabel();
        nullJLabel8 = new javax.swing.JLabel();
        stretchJCheckBox = new javax.swing.JCheckBox();
        imageJScrollPane = new javax.swing.JScrollPane();
        titleJLabel = new javax.swing.JLabel();

        delayJField.setText("1000");
        delayJField.setEnabled(false);
        delayJField.setMaximumSize(new java.awt.Dimension(110, 22));
        delayJField.setMinimumSize(new java.awt.Dimension(110, 22));
        delayJField.setPreferredSize(new java.awt.Dimension(110, 22));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Illustration Editing");
        setMinimumSize(new java.awt.Dimension(577, 394));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        buttonJPanel.setMaximumSize(new java.awt.Dimension(32995, 26));
        buttonJPanel.setMinimumSize(new java.awt.Dimension(228, 26));
        buttonJPanel.setPreferredSize(new java.awt.Dimension(484, 26));
        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));
        buttonJPanel.add(filler1);

        colorJButton.setToolTipText("Change Slice outline Color");
        colorJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        colorJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        colorJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        colorJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(colorJButton);
        buttonJPanel.add(filler4);

        autoCompleteJButton.setToolTipText("Auto Complete Form");
        autoCompleteJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        autoCompleteJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        autoCompleteJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        autoCompleteJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoCompleteJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(autoCompleteJButton);
        buttonJPanel.add(filler3);

        finishJButton.setText("Finish");
        finishJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        finishJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        finishJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        finishJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(finishJButton);
        buttonJPanel.add(filler2);

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

        mainJTabbedPane.setMaximumSize(new java.awt.Dimension(272, 284));
        mainJTabbedPane.setMinimumSize(new java.awt.Dimension(272, 284));
        mainJTabbedPane.setPreferredSize(new java.awt.Dimension(272, 284));

        sliceTabJPanel.setPreferredSize(new java.awt.Dimension(306, 233));

        sliceJPanel.setMaximumSize(new java.awt.Dimension(247, 211));
        sliceJPanel.setMinimumSize(new java.awt.Dimension(247, 211));
        sliceJPanel.setPreferredSize(new java.awt.Dimension(247, 211));
        java.awt.GridBagLayout jPanel3Layout = new java.awt.GridBagLayout();
        jPanel3Layout.columnWidths = new int[] {0, 5, 0, 5, 0};
        jPanel3Layout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        sliceJPanel.setLayout(jPanel3Layout);

        blockYOffsetJLabel.setForeground(new java.awt.Color(51, 51, 51));
        blockYOffsetJLabel.setText("Block Y-Offset:");
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

        blockYOffsetJField.setColumns(10);
        blockYOffsetJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockYOffsetJField.setText("0");
        blockYOffsetJField.setMaximumSize(new java.awt.Dimension(84, 22));
        blockYOffsetJField.setMinimumSize(new java.awt.Dimension(84, 22));
        blockYOffsetJField.setName(""); // NOI18N
        blockYOffsetJField.setPreferredSize(new java.awt.Dimension(84, 22));
        blockYOffsetJField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                blockYOffsetJFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockYOffsetJField, gridBagConstraints);

        blockYOffsetJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockYOffsetJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockYOffsetJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        blockYOffsetJSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                blockYOffsetJSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockYOffsetJSpinner, gridBagConstraints);

        blockWidthJLabel.setForeground(new java.awt.Color(51, 51, 51));
        blockWidthJLabel.setText("Block Width:");
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

        blockWidthJField.setColumns(10);
        blockWidthJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockWidthJField.setText("0");
        blockWidthJField.setMaximumSize(new java.awt.Dimension(84, 22));
        blockWidthJField.setMinimumSize(new java.awt.Dimension(84, 22));
        blockWidthJField.setName(""); // NOI18N
        blockWidthJField.setPreferredSize(new java.awt.Dimension(84, 22));
        blockWidthJField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                blockWidthJFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockWidthJField, gridBagConstraints);

        blockWidthJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockWidthJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockWidthJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        blockWidthJSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                blockWidthJSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockWidthJSpinner, gridBagConstraints);

        blockHeightJLabel.setForeground(new java.awt.Color(51, 51, 51));
        blockHeightJLabel.setText("Block Height:");
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

        blockHeightJField.setColumns(10);
        blockHeightJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockHeightJField.setText("0");
        blockHeightJField.setMaximumSize(new java.awt.Dimension(84, 22));
        blockHeightJField.setMinimumSize(new java.awt.Dimension(84, 22));
        blockHeightJField.setName(""); // NOI18N
        blockHeightJField.setPreferredSize(new java.awt.Dimension(84, 22));
        blockHeightJField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                blockHeightJFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockHeightJField, gridBagConstraints);

        blockHeightJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockHeightJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockHeightJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        blockHeightJSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                blockHeightJSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockHeightJSpinner, gridBagConstraints);

        blockXOffsetJLabel.setForeground(new java.awt.Color(51, 51, 51));
        blockXOffsetJLabel.setText("Block X-Offset:");
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

        blockXOffsetJField.setColumns(10);
        blockXOffsetJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockXOffsetJField.setText("0");
        blockXOffsetJField.setMaximumSize(new java.awt.Dimension(84, 22));
        blockXOffsetJField.setMinimumSize(new java.awt.Dimension(84, 22));
        blockXOffsetJField.setName(""); // NOI18N
        blockXOffsetJField.setPreferredSize(new java.awt.Dimension(84, 22));
        blockXOffsetJField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                blockXOffsetJFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockXOffsetJField, gridBagConstraints);

        blockXOffsetJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockXOffsetJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockXOffsetJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        blockXOffsetJSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                blockXOffsetJSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockXOffsetJSpinner, gridBagConstraints);

        blockColumnJLabel.setForeground(new java.awt.Color(51, 51, 51));
        blockColumnJLabel.setText("Block Columns:");
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

        blockVGapJLabel.setForeground(new java.awt.Color(51, 51, 51));
        blockVGapJLabel.setText("Block V-Gap:");
        blockVGapJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        blockVGapJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        blockVGapJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        blockVGapJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockVGapJLabel, gridBagConstraints);

        blockHGapJLabel.setForeground(new java.awt.Color(51, 51, 51));
        blockHGapJLabel.setText("Block H-Gap:");
        blockHGapJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        blockHGapJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        blockHGapJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        blockHGapJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockHGapJLabel, gridBagConstraints);

        blockRowJField.setColumns(10);
        blockRowJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockRowJField.setText("0");
        blockRowJField.setMaximumSize(new java.awt.Dimension(84, 22));
        blockRowJField.setMinimumSize(new java.awt.Dimension(84, 22));
        blockRowJField.setName(""); // NOI18N
        blockRowJField.setPreferredSize(new java.awt.Dimension(84, 22));
        blockRowJField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                blockRowJFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockRowJField, gridBagConstraints);

        blockColumnJField.setColumns(10);
        blockColumnJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockColumnJField.setText("0");
        blockColumnJField.setMaximumSize(new java.awt.Dimension(84, 22));
        blockColumnJField.setMinimumSize(new java.awt.Dimension(84, 22));
        blockColumnJField.setName(""); // NOI18N
        blockColumnJField.setPreferredSize(new java.awt.Dimension(84, 22));
        blockColumnJField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                blockColumnJFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockColumnJField, gridBagConstraints);

        blockVGapJField.setColumns(10);
        blockVGapJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockVGapJField.setText("0");
        blockVGapJField.setMaximumSize(new java.awt.Dimension(84, 22));
        blockVGapJField.setMinimumSize(new java.awt.Dimension(84, 22));
        blockVGapJField.setName(""); // NOI18N
        blockVGapJField.setPreferredSize(new java.awt.Dimension(84, 22));
        blockVGapJField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                blockVGapJFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockVGapJField, gridBagConstraints);

        blockHGapJField.setColumns(10);
        blockHGapJField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        blockHGapJField.setText("0");
        blockHGapJField.setMaximumSize(new java.awt.Dimension(84, 22));
        blockHGapJField.setMinimumSize(new java.awt.Dimension(84, 22));
        blockHGapJField.setName(""); // NOI18N
        blockHGapJField.setPreferredSize(new java.awt.Dimension(84, 22));
        blockHGapJField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                blockHGapJFieldPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockHGapJField, gridBagConstraints);

        blockRowJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockRowJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockRowJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        blockRowJSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                blockRowJSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockRowJSpinner, gridBagConstraints);

        blockColumnJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockColumnJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockColumnJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        blockColumnJSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                blockColumnJSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockColumnJSpinner, gridBagConstraints);

        blockVGapJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockVGapJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockVGapJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        blockVGapJSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                blockVGapJSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockVGapJSpinner, gridBagConstraints);

        blockHGapJSpinner.setMaximumSize(new java.awt.Dimension(64, 22));
        blockHGapJSpinner.setMinimumSize(new java.awt.Dimension(64, 22));
        blockHGapJSpinner.setPreferredSize(new java.awt.Dimension(64, 22));
        blockHGapJSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                blockHGapJSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        sliceJPanel.add(blockHGapJSpinner, gridBagConstraints);

        blockRowJLabel.setForeground(new java.awt.Color(51, 51, 51));
        blockRowJLabel.setText("Block Rows:");
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

        nullJLabel1.setText("Change how the Image will be Sliced Up");
        nullJLabel1.setEnabled(false);

        javax.swing.GroupLayout sliceTabJPanelLayout = new javax.swing.GroupLayout(sliceTabJPanel);
        sliceTabJPanel.setLayout(sliceTabJPanelLayout);
        sliceTabJPanelLayout.setHorizontalGroup(
            sliceTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sliceTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sliceTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nullJLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sliceJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        sliceTabJPanelLayout.setVerticalGroup(
            sliceTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sliceTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nullJLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sliceJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        mainJTabbedPane.addTab("Slice Settings", sliceTabJPanel);

        settingJPanel.setMaximumSize(new java.awt.Dimension(247, 184));
        settingJPanel.setMinimumSize(new java.awt.Dimension(247, 184));
        settingJPanel.setPreferredSize(new java.awt.Dimension(247, 184));
        java.awt.GridBagLayout settingJPanelLayout = new java.awt.GridBagLayout();
        settingJPanelLayout.columnWidths = new int[] {0, 5, 0};
        settingJPanelLayout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        settingJPanel.setLayout(settingJPanelLayout);

        locationJField.setColumns(20);
        locationJField.setEnabled(false);
        locationJField.setMaximumSize(new java.awt.Dimension(134, 22));
        locationJField.setMinimumSize(new java.awt.Dimension(134, 22));
        locationJField.setPreferredSize(new java.awt.Dimension(134, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(locationJField, gridBagConstraints);

        locationJLabel.setForeground(new java.awt.Color(51, 51, 51));
        locationJLabel.setText("Image Location:");
        locationJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        locationJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        locationJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        locationJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(locationJLabel, gridBagConstraints);

        packageJLabel.setForeground(new java.awt.Color(51, 51, 51));
        packageJLabel.setText("Image Package:");
        packageJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        packageJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        packageJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        settingJPanel.add(packageJLabel, gridBagConstraints);

        packageJField.setColumns(20);
        packageJField.setEnabled(false);
        packageJField.setMaximumSize(new java.awt.Dimension(134, 22));
        packageJField.setMinimumSize(new java.awt.Dimension(134, 22));
        packageJField.setPreferredSize(new java.awt.Dimension(134, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(packageJField, gridBagConstraints);

        widthJLabel.setForeground(new java.awt.Color(51, 51, 51));
        widthJLabel.setText("Image Width :");
        widthJLabel.setEnabled(false);
        widthJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        widthJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        widthJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        settingJPanel.add(widthJLabel, gridBagConstraints);

        heightJLabel.setForeground(new java.awt.Color(51, 51, 51));
        heightJLabel.setText("Image Height:");
        heightJLabel.setEnabled(false);
        heightJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        heightJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        heightJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        settingJPanel.add(heightJLabel, gridBagConstraints);

        referenceJLabel.setForeground(new java.awt.Color(51, 51, 51));
        referenceJLabel.setText("Reference ID:");
        referenceJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        referenceJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        referenceJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        settingJPanel.add(referenceJLabel, gridBagConstraints);

        nameJLabel.setForeground(new java.awt.Color(51, 51, 51));
        nameJLabel.setText("Reference Name:");
        nameJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        nameJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        nameJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        settingJPanel.add(nameJLabel, gridBagConstraints);

        referenceJField.setColumns(20);
        referenceJField.setToolTipText("");
        referenceJField.setMaximumSize(new java.awt.Dimension(134, 22));
        referenceJField.setMinimumSize(new java.awt.Dimension(134, 22));
        referenceJField.setPreferredSize(new java.awt.Dimension(134, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(referenceJField, gridBagConstraints);

        nameJField.setColumns(20);
        nameJField.setMaximumSize(new java.awt.Dimension(134, 22));
        nameJField.setMinimumSize(new java.awt.Dimension(134, 22));
        nameJField.setPreferredSize(new java.awt.Dimension(134, 22));
        nameJField.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                nameJFieldComponentResized(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(nameJField, gridBagConstraints);

        displayJField.setColumns(20);
        displayJField.setMaximumSize(new java.awt.Dimension(134, 22));
        displayJField.setMinimumSize(new java.awt.Dimension(134, 22));
        displayJField.setPreferredSize(new java.awt.Dimension(134, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(displayJField, gridBagConstraints);

        displayJLabel.setForeground(new java.awt.Color(51, 51, 51));
        displayJLabel.setText("Display Name:");
        displayJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        displayJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        displayJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        settingJPanel.add(displayJLabel, gridBagConstraints);

        widthJField.setEditable(false);
        widthJField.setColumns(20);
        widthJField.setMaximumSize(new java.awt.Dimension(134, 22));
        widthJField.setMinimumSize(new java.awt.Dimension(134, 22));
        widthJField.setPreferredSize(new java.awt.Dimension(134, 22));
        widthJField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                widthJFieldMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(widthJField, gridBagConstraints);

        heightJField.setEditable(false);
        heightJField.setColumns(20);
        heightJField.setMaximumSize(new java.awt.Dimension(134, 22));
        heightJField.setMinimumSize(new java.awt.Dimension(134, 22));
        heightJField.setPreferredSize(new java.awt.Dimension(134, 22));
        heightJField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                heightJFieldMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        settingJPanel.add(heightJField, gridBagConstraints);

        jLabel2.setText("Edit Information about this Resource");
        jLabel2.setEnabled(false);

        generateJButton.setText("Generate ID's");
        generateJButton.setMaximumSize(new java.awt.Dimension(104, 26));
        generateJButton.setMinimumSize(new java.awt.Dimension(104, 26));
        generateJButton.setPreferredSize(new java.awt.Dimension(104, 26));
        generateJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateJButtonActionPerformed(evt);
            }
        });

        imageJButton.setLabel("Change Image");
        imageJButton.setMaximumSize(new java.awt.Dimension(134, 26));
        imageJButton.setMinimumSize(new java.awt.Dimension(134, 26));
        imageJButton.setPreferredSize(new java.awt.Dimension(134, 26));
        imageJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(generateJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(imageJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(settingJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(settingJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(generateJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imageJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mainJTabbedPane.addTab("Manifest Settings", jPanel1);

        delayJLabel.setForeground(new java.awt.Color(51, 51, 51));
        delayJLabel.setText("Timer Delay (ms):");
        delayJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        delayJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        delayJLabel.setPreferredSize(new java.awt.Dimension(104, 22));

        nullJLabel3.setForeground(new java.awt.Color(51, 51, 51));
        nullJLabel3.setText("Sets just how long the engine should wait");

        nullJLabel4.setForeground(new java.awt.Color(51, 51, 51));
        nullJLabel4.setText("(in Miliseconds) before changing to the next image");

        nullJLabel5.setForeground(new java.awt.Color(51, 51, 51));
        nullJLabel5.setText("in the Animation queue");

        delayJSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                delayJSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout animationRenderJPanelLayout = new javax.swing.GroupLayout(animationRenderJPanel);
        animationRenderJPanel.setLayout(animationRenderJPanelLayout);
        animationRenderJPanelLayout.setHorizontalGroup(
            animationRenderJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(animationRenderJPanelLayout.createSequentialGroup()
                .addComponent(delayJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(delayJSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(nullJLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(nullJLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(nullJLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        animationRenderJPanelLayout.setVerticalGroup(
            animationRenderJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(animationRenderJPanelLayout.createSequentialGroup()
                .addComponent(nullJLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nullJLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nullJLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(animationRenderJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(delayJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(delayJSpinner))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        nullJLabel2.setText("Animation Settings");
        nullJLabel2.setEnabled(false);

        nullJLabel6.setText("Background Settings");
        nullJLabel6.setEnabled(false);

        nullJLabel7.setForeground(new java.awt.Color(51, 51, 51));
        nullJLabel7.setText("Sets whether or not the background should stretch");

        nullJLabel8.setForeground(new java.awt.Color(51, 51, 51));
        nullJLabel8.setText("width and length-wise to the boundry of the room");

        stretchJCheckBox.setForeground(new java.awt.Color(51, 51, 51));
        stretchJCheckBox.setText("Stretch to Room Boundry");
        stretchJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stretchJCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(stretchJCheckBox)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(nullJLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(nullJLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(nullJLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nullJLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(stretchJCheckBox))
        );

        javax.swing.GroupLayout backgroundRenderJPanelLayout = new javax.swing.GroupLayout(backgroundRenderJPanel);
        backgroundRenderJPanel.setLayout(backgroundRenderJPanelLayout);
        backgroundRenderJPanelLayout.setHorizontalGroup(
            backgroundRenderJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundRenderJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundRenderJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nullJLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(animationRenderJPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nullJLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        backgroundRenderJPanelLayout.setVerticalGroup(
            backgroundRenderJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundRenderJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nullJLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(animationRenderJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nullJLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainJTabbedPane.addTab("Render Settings", backgroundRenderJPanel);

        imageJScrollPane.setToolTipText("");
        imageJScrollPane.setMaximumSize(new java.awt.Dimension(275, 280));
        imageJScrollPane.setMinimumSize(new java.awt.Dimension(275, 280));
        imageJScrollPane.setPreferredSize(new java.awt.Dimension(275, 280));

        titleJLabel.setText("Change and view Illustrations");
        titleJLabel.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mainJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                        .addComponent(imageJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mainJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imageJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void blockYOffsetJFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_blockYOffsetJFieldPropertyChange
        performComponentSync(blockYOffsetJField, blockYOffsetJSpinner);
    }//GEN-LAST:event_blockYOffsetJFieldPropertyChange

    private void blockWidthJFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_blockWidthJFieldPropertyChange
        performComponentSync(blockWidthJField, blockWidthJSpinner);
    }//GEN-LAST:event_blockWidthJFieldPropertyChange

    private void blockHeightJFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_blockHeightJFieldPropertyChange
        performComponentSync(blockHeightJField, blockHeightJSpinner);
    }//GEN-LAST:event_blockHeightJFieldPropertyChange

    private void blockXOffsetJFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_blockXOffsetJFieldPropertyChange
        performComponentSync(blockXOffsetJField, blockXOffsetJSpinner);
    }//GEN-LAST:event_blockXOffsetJFieldPropertyChange

    private void blockRowJFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_blockRowJFieldPropertyChange
        performComponentSync(blockRowJField, blockRowJSpinner);
    }//GEN-LAST:event_blockRowJFieldPropertyChange

    private void blockColumnJFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_blockColumnJFieldPropertyChange
        performComponentSync(blockColumnJField, blockColumnJSpinner);
    }//GEN-LAST:event_blockColumnJFieldPropertyChange

    private void blockVGapJFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_blockVGapJFieldPropertyChange
        performComponentSync(blockVGapJField, blockVGapJSpinner);
    }//GEN-LAST:event_blockVGapJFieldPropertyChange

    private void blockHGapJFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_blockHGapJFieldPropertyChange
        performComponentSync(blockHGapJField, blockHGapJSpinner);
    }//GEN-LAST:event_blockHGapJFieldPropertyChange

    private void blockRowJSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_blockRowJSpinnerStateChanged
        if (blockRowJField.getValue() != blockRowJSpinner.getValue()) {
            blockRowJField.setValue(((Number) blockRowJSpinner.getValue()).intValue());
        }
    }//GEN-LAST:event_blockRowJSpinnerStateChanged

    private void blockColumnJSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_blockColumnJSpinnerStateChanged
        if (blockColumnJField.getValue() != blockColumnJSpinner.getValue()) {
            blockColumnJField.setValue(((Number) blockColumnJSpinner.getValue()).intValue());
        }
    }//GEN-LAST:event_blockColumnJSpinnerStateChanged

    private void blockVGapJSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_blockVGapJSpinnerStateChanged
        if (blockVGapJField.getValue() != blockVGapJSpinner.getValue()) {
            blockVGapJField.setValue(((Number) blockVGapJSpinner.getValue()).intValue());
        }
    }//GEN-LAST:event_blockVGapJSpinnerStateChanged

    private void blockHGapJSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_blockHGapJSpinnerStateChanged
        if (blockHGapJField.getValue() != blockHGapJSpinner.getValue()) {
            blockHGapJField.setValue(((Number) blockHGapJSpinner.getValue()).intValue());
        }
    }//GEN-LAST:event_blockHGapJSpinnerStateChanged

    private void blockWidthJSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_blockWidthJSpinnerStateChanged
        if (blockWidthJField.getValue() != blockWidthJSpinner.getValue()) {
            blockWidthJField.setValue(((Number) blockWidthJSpinner.getValue()).intValue());
        }
    }//GEN-LAST:event_blockWidthJSpinnerStateChanged

    private void blockHeightJSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_blockHeightJSpinnerStateChanged
        if (blockHeightJField.getValue() != blockHeightJSpinner.getValue()) {
            blockHeightJField.setValue(((Number) blockHeightJSpinner.getValue()).intValue());
        }
    }//GEN-LAST:event_blockHeightJSpinnerStateChanged

    private void blockXOffsetJSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_blockXOffsetJSpinnerStateChanged
        if (blockXOffsetJField.getValue() != blockXOffsetJSpinner.getValue()) {
            blockXOffsetJField.setValue(((Number) blockXOffsetJSpinner.getValue()).intValue());
        }
    }//GEN-LAST:event_blockXOffsetJSpinnerStateChanged

    private void blockYOffsetJSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_blockYOffsetJSpinnerStateChanged
        if (blockYOffsetJField.getValue() != blockYOffsetJSpinner.getValue()) {
            blockYOffsetJField.setValue(((Number) blockYOffsetJSpinner.getValue()).intValue());
        }
    }//GEN-LAST:event_blockYOffsetJSpinnerStateChanged

    private void finishJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishJButtonActionPerformed
        // Apply changes to Tileset
        finish();
    }//GEN-LAST:event_finishJButtonActionPerformed

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        // Set invisible
        setVisible(false);
    }//GEN-LAST:event_cancelJButtonActionPerformed

    private void colorJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorJButtonActionPerformed

        // Show the color chooser
        final Color color = JColorChooser.showDialog(this, "Change Outline Color", colorOutline);

        if (color != null) {
            colorOutline = color;
        }

        // Repaint the JPanel
        repaint();
    }//GEN-LAST:event_colorJButtonActionPerformed

    private void imageJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageJButtonActionPerformed

        // Open the PluginManager
        final ResourceSelector manager = new ResourceSelector(this, delegate, true);
        manager.setFilterType(Picture.class);
        manager.setResource(picture);
        manager.setLocationRelativeTo(this);
        manager.setVisible(true);
        manager.dispose();

        // It will close on its own
        picture = (Picture) manager.getResource();

        // Just for good measure
        try {

            //
            if (picture != null) {

                //
                illustration.setPicture(picture);

                //
                imagePanel.updatePanel(illustration);

                // Update and validate everything here
                //binder.setResource(illustration);
                binder.bindImage(picture, this);

                //
                blockRowJField.setValue(1);
                blockColumnJField.setValue(1);

                // Quick check for backdrops
                if (illustration instanceof Backdrop) {

                    //
                    final Image image = picture.getImage();

                    //
                    blockWidthJField.setValue(image == null ? 0 : image.getWidth(this));
                    blockHeightJField.setValue(image == null ? 0 : image.getHeight(this));
                }
            }
        } catch (ClassCastException cce) {
            //
        }

        //
        refresh();
    }//GEN-LAST:event_imageJButtonActionPerformed

    private void generateJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateJButtonActionPerformed

        // Just to make sure.
        if (binder.isEditting() == false) {

            // Click this button to auto-generate all three forms of manifest-to-delegate identification
            binder.testButton();
        }
    }//GEN-LAST:event_generateJButtonActionPerformed

    private void nameJFieldComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_nameJFieldComponentResized

        // TODO add your handling code here:
        final Dimension dimension = new Dimension(134, 22);

        // Destroy the layout manager to cool the auto resizing "feature"
        settingJPanel.setLayout(null);

        // TODO add your handling code here:
        //nameJField.setSize(dimension);
        nameJLabel.setSize(dimension);

        //
        referenceJField.setSize(dimension);
        referenceJLabel.setSize(dimension);

        //
        displayJField.setSize(dimension);
        displayJLabel.setSize(dimension);

        //
        packageJField.setSize(dimension);
        packageJLabel.setSize(dimension);

        //
        locationJField.setSize(dimension);
        locationJLabel.setSize(dimension);
    }//GEN-LAST:event_nameJFieldComponentResized

    private void autoCompleteJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoCompleteJButtonActionPerformed

        // TODO add your handling code here:
        if (picture == null) {

            //
            final String message = "Please choose an Image before Attempting to Automatically Fill\nout Form.";

            //
            JOptionPane.showMessageDialog(this, message);
        } else {

            //
            final Image image = picture.getImage();

            // Not fully working yet
            autoComplete(image);
        }
    }//GEN-LAST:event_autoCompleteJButtonActionPerformed

    private void stretchJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stretchJCheckBoxActionPerformed

        //
        if (illustration instanceof Backdrop) {

            //
            updateStretchBox();
        }
    }//GEN-LAST:event_stretchJCheckBoxActionPerformed

    private void delayJSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_delayJSpinnerStateChanged

        //
        if (viewer != null) {

            //
            final SpinnerNumberModel model = (SpinnerNumberModel) delayJSpinner.getModel();

            //
            viewer.setDelay((int) model.getValue());
        }
    }//GEN-LAST:event_delayJSpinnerStateChanged

    private void heightJFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_heightJFieldMouseClicked

        // TODO add your handling code here:
        final int click_count = evt.getClickCount();

        // Double Left Click Event
        if (evt.getButton() == MouseEvent.BUTTON3) {

            //
            int number = 0;

            //
            try {

                //
                number = Integer.parseInt(heightJField.getText());
            } catch (NumberFormatException npe) {
                //
            }

            // Double Click Event
            if (click_count == 1) {

                // E-Z-P-Z
                blockHeightJField.setValue(number);
            } else if (click_count == 2) {

                //
                blockHeightJField.setValue((number % 2 == 1) ? ((number / 2) - 1) : (number / 2));
            }
        }
    }//GEN-LAST:event_heightJFieldMouseClicked

    private void widthJFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_widthJFieldMouseClicked

        // TODO add your handling code here:
        final int click_count = evt.getClickCount();

        // Double Left Click Event
        if (evt.getButton() == MouseEvent.BUTTON3) {

            //
            int number = 0;

            //
            try {

                //
                number = Integer.parseInt(widthJField.getText());
            } catch (NumberFormatException npe) {
                //
            }

            // Double Click Event
            if (click_count == 1) {

                // E-Z-P-Z
                blockWidthJField.setValue(number);
            } else if (click_count == 2) {

                //
                blockWidthJField.setValue((number % 2 == 1) ? ((number / 2) - 1) : (number / 2));
            }
        }
    }//GEN-LAST:event_widthJFieldMouseClicked

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

        //
        if (viewer != null) {

            // TODO add your handling code here:
            viewer.setLocation((getX() - viewer.getWidth()) - 6, getY() + 32);
            viewer.setVisible(true);
            viewer.requestFocus();
        }
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel animationRenderJPanel;
    private javax.swing.JButton autoCompleteJButton;
    private javax.swing.JPanel backgroundRenderJPanel;
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
    private javax.swing.JFormattedTextField delayJField;
    private javax.swing.JLabel delayJLabel;
    private javax.swing.JSpinner delayJSpinner;
    private javax.swing.JTextField displayJField;
    private javax.swing.JLabel displayJLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JButton finishJButton;
    private javax.swing.JButton generateJButton;
    private javax.swing.JTextField heightJField;
    private javax.swing.JLabel heightJLabel;
    private javax.swing.JButton imageJButton;
    private javax.swing.JScrollPane imageJScrollPane;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField locationJField;
    private javax.swing.JLabel locationJLabel;
    private javax.swing.JTabbedPane mainJTabbedPane;
    private javax.swing.JTextField nameJField;
    private javax.swing.JLabel nameJLabel;
    private javax.swing.JLabel nullJLabel1;
    private javax.swing.JLabel nullJLabel2;
    private javax.swing.JLabel nullJLabel3;
    private javax.swing.JLabel nullJLabel4;
    private javax.swing.JLabel nullJLabel5;
    private javax.swing.JLabel nullJLabel6;
    private javax.swing.JLabel nullJLabel7;
    private javax.swing.JLabel nullJLabel8;
    private javax.swing.JTextField packageJField;
    private javax.swing.JLabel packageJLabel;
    private javax.swing.JTextField referenceJField;
    private javax.swing.JLabel referenceJLabel;
    private javax.swing.JPanel settingJPanel;
    private javax.swing.JPanel sliceJPanel;
    private javax.swing.JPanel sliceTabJPanel;
    private javax.swing.JCheckBox stretchJCheckBox;
    private javax.swing.JLabel titleJLabel;
    private javax.swing.JTextField widthJField;
    private javax.swing.JLabel widthJLabel;
    // End of variables declaration//GEN-END:variables
}
