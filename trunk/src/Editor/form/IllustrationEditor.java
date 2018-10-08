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
import core.world.Room;
import core.world.Tileset;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Robert Antuan Cherry
 */
public class IllustrationEditor extends javax.swing.JDialog {

    // Variable Declaraction
    // Java Native Classes
    private ArrayList<Rectangle> array;
    private ImageIcon iconAnimationOn;
    private ImageIcon iconAnimationOff;
    private Color colorOutline = Color.BLACK;
    // Project Classes
    private AnimationViewer viewer;
    private ManifestBinder binder;
    private Picture picture;
    private DelegateCheckBox box_delegate;
    private final Illustration illustration;
    private ImagePanel imageJPanel;
    private final ResourceDelegate delegate;
    // End of Variable Declaration

    public IllustrationEditor(Window editor, ResourceDelegate delegate, Illustration illustration, boolean modal) {

        // Super stuff
        super(editor);
        super.setModal(modal);
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
        setupManifest();
        setupManifestBinder();
    }

    private void commit() {

        // Clear the list
        updateImagePanel();

        //
        updateIllustration();

        // Easy Peasy
        ResourceWriter.write(delegate, illustration);

        // Add to delegate
        delegate.addResource(illustration);
    }

    private void updateIllustration() {

        // Place the values in the graphic attributes map
        try {

            // Binder exists from init();
            illustration.setReferenceID(binder.getReferenceID());
            illustration.setReferenceName(binder.getReferenceName());
            illustration.setDisplayName(binder.getDisplayName());

            // Picture must exist
            if (picture != null) {
                illustration.setPicture(picture);
                illustration.setPictureInfo(picture.getPackageID(), picture.getReferenceID());
            }

            // Update the controls
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
            illustration.setBlockHeight(format.parse(String.valueOf(blockHeightJField.getValue())).intValue());

            // Ensure value reflection in attributes map
            illustration.updateAttributes();

            // Validate the graphicset
            illustration.validate();
        } catch (ParseException pe) {
            System.err.println(pe);
        }
    }

    private void updateImagePanel() {

        // Clear the current Collection
        if (array != null) {

            // Clear the current rectangle list / invalidate
            array.clear();

            //
            final int graphicWidth = picture == null || picture.getImage() == null ? 0 : picture.getImage().getWidth(this);
            final int graphicHeight = picture == null || picture.getImage() == null ? 0 : picture.getImage().getHeight(this);

            // Store for a second and solve for backdrops
            int blockWidth = ((Number) blockWidthJField.getValue()).intValue();
            int blockHeight = ((Number) blockHeightJField.getValue()).intValue();
            final Number nRows = ((Number) blockRowJField.getValue());
            final Number nColumns = ((Number) blockColumnJField.getValue());

            // Just in case its zero.
            blockWidth = blockWidth <= 0 ? graphicWidth : blockWidth;
            blockHeight = blockHeight <= 0 ? graphicHeight : blockHeight;

            //
            final int blockXOffset = ((Number) blockXOffsetJField.getValue()).intValue();
            final int blockYOffset = ((Number) blockYOffsetJField.getValue()).intValue();
            final int blockHGap = ((Number) blockHGapJField.getValue()).intValue();
            final int blockVGap = ((Number) blockVGapJField.getValue()).intValue();
            final int blockRows = nRows == null ? illustration.getBlockRows() : nRows.intValue();
            final int blockColumns = nColumns == null ? illustration.getBlockColumns() : nColumns.intValue();

            // Apply those values.
            illustration.setBlockRows(blockRows);
            illustration.setBlockColumns(blockColumns);
            illustration.setBlockXOffset(blockXOffset);
            illustration.setBlockYOffset(blockYOffset);
            illustration.setBlockHGap(blockHGap);
            illustration.setBlockVGap(blockVGap);

            // Create the boxes which mimic the slices to be made
            for (int col = 0; col < blockColumns; col++) {

                //
                for (int row = 0; row < blockRows; row++) {

                    // Create the new rectangle at the precise location of the cut
                    final Rectangle rectangle = new Rectangle(blockXOffset + (col * (blockWidth + blockHGap)), blockYOffset + (row * (blockHeight + blockVGap)), blockWidth, blockHeight);

                    // Populate the rectangle arraylist
                    array.add(rectangle);
                }
            }

            //
            imageJPanel.setPreferredSize(new Dimension(blockXOffset + (blockColumns * (blockWidth + blockHGap)) + 1, blockYOffset + (blockRows * (blockHeight + blockVGap)) + 1));
            imageJScrollPane.setViewportView(imageJPanel);
        }

        // Will only be alive when dealing with animations.
        if (viewer != null && illustration instanceof Animation) {

            //
            Animation animation;

            //
            if (viewer.getAnimation() == null) {
                // Giving an animation to the viewer
                animation = new Animation(null, null, null, null, null);
            } else {
                animation = viewer.getAnimation();
            }

            // Setting those values, mang.
            animation.setBlockRows(((Number) blockRowJSpinner.getValue()).intValue());
            animation.setBlockColumns(((Number) blockColumnJSpinner.getValue()).intValue());
            animation.setBlockWidth(((Number) blockWidthJSpinner.getValue()).intValue());
            animation.setBlockHeight(((Number) blockHeightJSpinner.getValue()).intValue());
            animation.setBlockHGap(((Number) blockHGapJSpinner.getValue()).intValue());
            animation.setBlockVGap(((Number) blockVGapJSpinner.getValue()).intValue());
            animation.setBlockXOffset(((Number) blockXOffsetJSpinner.getValue()).intValue());
            animation.setBlockYOffset(((Number) blockYOffsetJSpinner.getValue()).intValue());

            // Depends
            animation.setWrapper(((Animation) illustration).getWrapper());
            animation.setDelay(((Animation) illustration).getDelay());
            animation.setPicture(picture);
            animation.updateAttributes();
            animation.validate();

            // Give it the animation
            viewer.setAnimation(animation);
        }

        // Repaint
        repaint();
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
    }

    // <editor-fold desc="Editor Fold: Setup Methods" defaultstate="collapsed">
    private void setupResource() {

        // @note Creating or Adapting to resource given
        if (illustration != null) {

            // If is existing tileset grab image as well
            if (illustration.getPicture() != null) {

                // Grab the graphic from existing
                picture = illustration.getPicture();
            }
        }
    }

    private void setupDialog() {

        //
        imageJPanel = new ImagePanel(imageJScrollPane) {
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
        imageJPanel.setPreferredSize(dimension);
        imageJPanel.updatePanel(illustration);
        imageJPanel.setShowTextile(true);
        imageJPanel.setShowImage(false);

        // Set as viewport view
        imageJPanel.updatePanelSize();

        // Set TItle -- I will reorganize all Dialogs soon so that the code appears cleaner
        if (illustration.getDisplayName() != null) {
            setTitle("Illustration Editing: " + illustration.getDisplayName());
        } else {
            setTitle("Illustration Editor");
        }

        // Giving models to spinners
        blockRowJSpinner.setModel(new SpinnerNumberModel(1, 1, Short.MAX_VALUE, 1));
        blockColumnJSpinner.setModel(new SpinnerNumberModel(1, 1, Short.MAX_VALUE, 1));
        blockWidthJSpinner.setModel(new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1));
        blockHeightJSpinner.setModel(new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1));
        blockHGapJSpinner.setModel(new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1));
        blockVGapJSpinner.setModel(new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1));
        blockXOffsetJSpinner.setModel(new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1));
        blockYOffsetJSpinner.setModel(new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1));

        // Attempt to set to preset values from image attributes
        blockRowJField.setValue(illustration.getBlockRows() <= 0 ? 1 : illustration.getBlockRows());
        blockColumnJField.setValue(illustration.getBlockColumns() <= 0 ? 1 : illustration.getBlockColumns());
        blockXOffsetJField.setValue(illustration.getBlockXOffset());
        blockYOffsetJField.setValue(illustration.getBlockYOffset());
        blockVGapJField.setValue(illustration.getBlockVGap());
        blockHGapJField.setValue(illustration.getBlockHGap());
        blockWidthJField.setValue(illustration.getBlockWidth());
        blockHeightJField.setValue(illustration.getBlockHeight());

        //
        settingJPanel.setLayout(null);

        // @ICONS
        // Grab the toolkit to grab some icons from the classpath
        final Class closs = getClass();
        completeJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-complete16.png"));
        gridJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-grid-color16.png"));
        textileJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-color-background16r.png"));
        collisionJButton.setIcon(ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-boundary16.png"));
        iconAnimationOn = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-animation16r.png");
        iconAnimationOff = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-animation16.png");
        viewerJToggleButton.setIcon(iconAnimationOff);

        final Toolkit kit = Toolkit.getDefaultToolkit();

        // Change the icon for the application
        final ArrayList<Image> list = new ArrayList<>();

        // Resource Disbaling section
        if (illustration instanceof Backdrop) {

            // Change the title and icon for the dialog.
            setTitle(illustration.getDisplayName()!=null&&
                    !illustration.getDisplayName().equals("null") &&
                    delegate.exists(illustration) ?
                    "Backdrop Editting: " + illustration.getDisplayName() : "Backdrop Editor");
            list.add(kit.getImage(closs.getResource("/Editor/icons/icon-background16.png")));

            //
            imageJPanel.setShowImage(true);

            //
            final Backdrop backdrop = (Backdrop) illustration;

            // Set and simulate a user click
            stretchJCheckBox.setSelected(backdrop.isStretching());

            // Disable these controls if stretch is selected
            if (backdrop.isStretching()) {
                blockRowJSpinner.setEnabled(false);
                blockColumnJSpinner.setEnabled(false);
                blockRowJField.setEnabled(false);
                blockColumnJField.setEnabled(false);
            }

            // Change the title of the scroll pane for backdrops.
            ((TitledBorder) imageJScrollPane.getBorder()).setTitle("Backdrop Preview");

            //
            nullJLabel1.setText("Change how the Image will be Repeated");
            viewerJToggleButton.setSelected(false);
            viewerJToggleButton.setEnabled(false);

            // Certain controls need to be removed.
            toolJPanel.remove(viewerJToggleButton);
            toolJPanel.remove(collisionJButton);
            remove(toolJPanel);
        } else if (illustration instanceof Animation) {

            // Change the title and icon for the dialog.
            setTitle(illustration.getDisplayName()!=null&&
                    !illustration.getDisplayName().equals("null") &&
                    delegate.exists(illustration) ?
                    "Animation Editting: " + illustration.getDisplayName() : "Animation Editor");
            list.add(kit.getImage(closs.getResource("/Editor/icons/icon-animation16.png")));

            //
            stretchJCheckBox.setSelected(false);
            stretchJCheckBox.setEnabled(false);
            altJPanel.remove(stretchJCheckBox);

            // Change the title of the scroll pane for backdrops.
            ((TitledBorder) imageJScrollPane.getBorder()).setTitle("Animation Cut Preview");

            // System.out.println("Wrapper: " + ((Animation) illustration).getWrapper());

            // @ANIMATION VIEWER
            setupAnimationViewer((Animation) illustration);
        } else if (illustration instanceof Tileset) {

            // Change the title and icon for the dialog.
            setTitle(illustration.getDisplayName()!=null&&
                    !illustration.getDisplayName().equals("null") &&
                    delegate.exists(illustration) ?
                    "Tileset Editting: " + illustration.getDisplayName() : "Tileset Editor");
            list.add(kit.getImage(closs.getResource("/Editor/icons/icon-tileset16.png")));

            //
            imageJPanel.setShowImage(true);
            imageJPanel.setImageCentered(false);

            // Tiles use none of this stuff.
            stretchJCheckBox.setSelected(false);
            stretchJCheckBox.setEnabled(false);
            altJPanel.remove(stretchJCheckBox);
            viewerJToggleButton.setSelected(false);
            viewerJToggleButton.setEnabled(false);
            toolJPanel.remove(viewerJToggleButton);
            toolJPanel.remove(collisionJButton);
            remove(toolJPanel);

            // Change the title of the scroll pane for backdrops.
            ((TitledBorder) imageJScrollPane.getBorder()).setTitle("Tileset Cut Preview");
        }
        this.setIconImages(list);
    }

    private void setupManifestBinder() {

        //
        box_delegate = new DelegateCheckBox(delegate);
        buttonJPanel.add(box_delegate, 0);

        // Testing it out.
        binder = new ManifestBinder(delegate, illustration);

        // Binding stuff manually.
        binder.bind(ManifestBinder.BOX_DELEGATE, box_delegate);
        binder.bind(ManifestBinder.BUTTON_FINISH, saveJButton);
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
        binder.setEdit(delegate.exists(illustration));
        generateJButton.setEnabled(!binder.isEditting());
    }

    private void setupAnimationViewer(Animation animation) {

        // Don't feed null animations.
        if (animation != null && delegate.exists(animation)) {

            //
            updateImagePanel();

            //
            viewer = new AnimationViewer(this, animation, false);
            viewerJToggleButton.setSelected(true);

            // TODO add your handling code here:
            viewer.setLocation((getX() - viewer.getWidth()) - 10, getY() + 32);
            viewer.setVisible(true);
            viewer.requestFocus();
        }
    }

    /*
     *  Quick method to copy across similar classes to ensure that the graphic is up to date -- Keeps init cleaner
     */
    private void setupManifest() {

        //
        if (illustration == null) {
            return;
        }

        // Grab its picture
        picture = illustration.getPicture();

        //
        if (picture != null) {

            //
            final String pack = picture.getPackageID();
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
    // </editor-fold>

    private void performComponentSync(JFormattedTextField field, JSpinner spinner) {

        // Sometimes the model isn't ready.
        if (field == null || spinner == null || spinner.getModel() == null) {
            return;
        }

        final Number fieldValue = ((Number) field.getValue());
        final Number spinnerValue = ((Number) spinner.getValue());

        // Grab the Value
        int value = fieldValue == null ? spinnerValue.intValue() : fieldValue.intValue();

        // Apply to this field
        field.setValue((int) value);

        // Apply to newSpinner
        spinner.setValue((int) value);

        // Refresh
        updateImagePanel();
    }

    /**
     * Overrides the ImageJPanel's paintComponent method to draw additional
     * rectangles
     *
     * @param monet
     */
    private void paintGuidelines(Graphics monet) {

        //
        if (imageJPanel == null || monet == null) {
            return;
        }

        //
        Image image;
        BufferedImage buffered;
        Graphics2D graphics;

        // Increasing offsets based on ImagePanels pointFocal
        int width = imageJPanel.getPreferredSize().width;
        int height = imageJPanel.getPreferredSize().height;

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
            if (illustration instanceof Animation) {
                monet.drawImage(image, 0, 0, this);
            }

            //
            width = image.getWidth(this) < width && width > 0 ? width : image.getWidth(this);
            height = image.getHeight(this) < height && height > 0 ? height : image.getHeight(this);
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
            graphics.setStroke(new BasicStroke(1f));
            graphics.draw(rectangle);
        }

        // Draw the buffered image
        monet.drawImage(buffered, 0, 0, this);
        monet.dispose();
    }

    private void autoComplete(Image image) {

        //
        final int imageWidth = image.getWidth(this);
        final int imageHeight = image.getHeight(this);

        // Should probably use a good old Least Common Denominator kind of deal.
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
        updateImagePanel();
    }

    public Illustration getIllustration() {
        return illustration;
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

        buttonJPanel = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        completeJButton = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        saveJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        closeJButton = new javax.swing.JButton();
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
        altJPanel = new javax.swing.JPanel();
        stretchJCheckBox = new javax.swing.JCheckBox();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        imageJButton = new javax.swing.JButton();
        manifestJPanel = new javax.swing.JPanel();
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
        jPanel1 = new javax.swing.JPanel();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        generateJButton = new javax.swing.JButton();
        imageJScrollPane = new javax.swing.JScrollPane();
        toolJPanel1 = new javax.swing.JPanel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        textileJButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        gridJButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        toolJPanel = new javax.swing.JPanel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        viewerJToggleButton = new javax.swing.JToggleButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        collisionJButton = new javax.swing.JToggleButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));

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

        completeJButton.setToolTipText("Auto Complete Form");
        completeJButton.setEnabled(false);
        completeJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        completeJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        completeJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        completeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                completeJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(completeJButton);
        buttonJPanel.add(filler7);

        saveJButton.setText("Save");
        saveJButton.setToolTipText("Save Changes");
        saveJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        saveJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        saveJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        saveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(saveJButton);
        buttonJPanel.add(filler2);

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
        buttonJPanel.add(closeJButton);

        mainJTabbedPane.setMaximumSize(new java.awt.Dimension(272, 284));
        mainJTabbedPane.setMinimumSize(new java.awt.Dimension(272, 284));
        mainJTabbedPane.setPreferredSize(new java.awt.Dimension(272, 284));

        sliceTabJPanel.setPreferredSize(new java.awt.Dimension(306, 233));

        sliceJPanel.setMaximumSize(new java.awt.Dimension(247, 211));
        sliceJPanel.setMinimumSize(new java.awt.Dimension(247, 211));
        sliceJPanel.setPreferredSize(new java.awt.Dimension(247, 211));
        java.awt.GridBagLayout jPanel3Layout = new java.awt.GridBagLayout();
        jPanel3Layout.columnWidths = new int[] {0, 10, 0, 10, 0};
        jPanel3Layout.rowHeights = new int[] {0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0};
        sliceJPanel.setLayout(jPanel3Layout);

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

        altJPanel.setLayout(new javax.swing.BoxLayout(altJPanel, javax.swing.BoxLayout.LINE_AXIS));

        stretchJCheckBox.setForeground(new java.awt.Color(51, 51, 51));
        stretchJCheckBox.setText("Stretch to Room Boundry");
        stretchJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stretchJCheckBoxActionPerformed(evt);
            }
        });
        altJPanel.add(stretchJCheckBox);
        altJPanel.add(filler10);

        imageJButton.setLabel("Change Image");
        imageJButton.setMaximumSize(new java.awt.Dimension(108, 26));
        imageJButton.setMinimumSize(new java.awt.Dimension(108, 26));
        imageJButton.setPreferredSize(new java.awt.Dimension(108, 26));
        imageJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageJButtonActionPerformed(evt);
            }
        });
        altJPanel.add(imageJButton);

        javax.swing.GroupLayout sliceTabJPanelLayout = new javax.swing.GroupLayout(sliceTabJPanel);
        sliceTabJPanel.setLayout(sliceTabJPanelLayout);
        sliceTabJPanelLayout.setHorizontalGroup(
            sliceTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sliceTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sliceTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sliceJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                    .addComponent(nullJLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                    .addComponent(altJPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        sliceTabJPanelLayout.setVerticalGroup(
            sliceTabJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sliceTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nullJLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sliceJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(altJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        mainJTabbedPane.addTab("Slice Settings", sliceTabJPanel);

        settingJPanel.setMaximumSize(new java.awt.Dimension(247, 184));
        settingJPanel.setMinimumSize(new java.awt.Dimension(247, 184));
        settingJPanel.setPreferredSize(new java.awt.Dimension(247, 184));
        java.awt.GridBagLayout settingJPanelLayout = new java.awt.GridBagLayout();
        settingJPanelLayout.columnWidths = new int[] {0, 30, 0};
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

        widthJLabel.setText("Image Width :");
        widthJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        widthJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        widthJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        settingJPanel.add(widthJLabel, gridBagConstraints);

        heightJLabel.setText("Image Height:");
        heightJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        heightJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        heightJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        settingJPanel.add(heightJLabel, gridBagConstraints);

        referenceJLabel.setText("Reference ID:");
        referenceJLabel.setMaximumSize(new java.awt.Dimension(104, 22));
        referenceJLabel.setMinimumSize(new java.awt.Dimension(104, 22));
        referenceJLabel.setPreferredSize(new java.awt.Dimension(104, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        settingJPanel.add(referenceJLabel, gridBagConstraints);

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

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        jPanel1.add(filler11);

        generateJButton.setText("Generate ID's");
        generateJButton.setMaximumSize(new java.awt.Dimension(104, 26));
        generateJButton.setMinimumSize(new java.awt.Dimension(104, 26));
        generateJButton.setPreferredSize(new java.awt.Dimension(104, 26));
        generateJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(generateJButton);

        javax.swing.GroupLayout manifestJPanelLayout = new javax.swing.GroupLayout(manifestJPanel);
        manifestJPanel.setLayout(manifestJPanelLayout);
        manifestJPanelLayout.setHorizontalGroup(
            manifestJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manifestJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(manifestJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(settingJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        manifestJPanelLayout.setVerticalGroup(
            manifestJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, manifestJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(settingJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mainJTabbedPane.addTab("Manifest Settings", manifestJPanel);

        imageJScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Idle Animation Preview"));
        imageJScrollPane.setToolTipText("");
        imageJScrollPane.setMaximumSize(new java.awt.Dimension(275, 280));
        imageJScrollPane.setMinimumSize(new java.awt.Dimension(275, 280));
        imageJScrollPane.setPreferredSize(new java.awt.Dimension(275, 280));

        toolJPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview Colors"));
        toolJPanel1.setLayout(new javax.swing.BoxLayout(toolJPanel1, javax.swing.BoxLayout.LINE_AXIS));
        toolJPanel1.add(filler9);

        textileJButton.setToolTipText("Change Background Color");
        textileJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        textileJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        textileJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        textileJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                textileJButtonMouseClicked(evt);
            }
        });
        toolJPanel1.add(textileJButton);
        toolJPanel1.add(filler3);

        gridJButton.setToolTipText("Change Grid Color");
        gridJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        gridJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        gridJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        gridJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gridJButtonActionPerformed(evt);
            }
        });
        toolJPanel1.add(gridJButton);
        toolJPanel1.add(filler5);

        toolJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Animation Tools"));
        toolJPanel.setLayout(new javax.swing.BoxLayout(toolJPanel, javax.swing.BoxLayout.LINE_AXIS));
        toolJPanel.add(filler6);

        viewerJToggleButton.setToolTipText("Toggle Animation Viewer");
        viewerJToggleButton.setFocusPainted(false);
        viewerJToggleButton.setMaximumSize(new java.awt.Dimension(26, 26));
        viewerJToggleButton.setMinimumSize(new java.awt.Dimension(26, 26));
        viewerJToggleButton.setPreferredSize(new java.awt.Dimension(26, 26));
        viewerJToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewerJToggleButtonActionPerformed(evt);
            }
        });
        toolJPanel.add(viewerJToggleButton);
        toolJPanel.add(filler8);

        collisionJButton.setToolTipText("Change Collision Boundaries");
        collisionJButton.setFocusPainted(false);
        collisionJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        collisionJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        collisionJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        collisionJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                collisionJButtonActionPerformed(evt);
            }
        });
        toolJPanel.add(collisionJButton);
        toolJPanel.add(filler4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(imageJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(toolJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(toolJPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(mainJTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(imageJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(toolJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(toolJPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 10, Short.MAX_VALUE))
                    .addComponent(mainJTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
        if (blockRowJField.getValue() != null) {

            //
            final int num = ((Number) blockRowJField.getValue()).intValue();

            //
            if (((Number) blockRowJField.getValue()).intValue() >= 1) {
                performComponentSync(blockRowJField, blockRowJSpinner);
            } else {
                blockRowJField.setValue(1);
            }
        }
    }//GEN-LAST:event_blockRowJFieldPropertyChange

    private void blockColumnJFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_blockColumnJFieldPropertyChange
        if (blockColumnJField.getValue() != null) {
            if (((Number) blockColumnJField.getValue()).intValue() >= 1) {
                performComponentSync(blockColumnJField, blockColumnJSpinner);
            } else {
                blockColumnJField.setValue(1);
            }
        }
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

    private void saveJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveJButtonActionPerformed

        // Ask first
        if (box_delegate.isSelected()) {
            commit();
        }
    }//GEN-LAST:event_saveJButtonActionPerformed

    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeJButtonActionPerformed

        // Set invisible
        setVisible(false);
    }//GEN-LAST:event_closeJButtonActionPerformed

    private void gridJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gridJButtonActionPerformed

        // Show the color chooser
        final Color color = JColorChooser.showDialog(this, "Change Outline Color", colorOutline);

        if (color != null) {
            colorOutline = color;
        }

        // Repaint the JPanel
        repaint();
    }//GEN-LAST:event_gridJButtonActionPerformed

    private void imageJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageJButtonActionPerformed

        // Allow the user to select a resource
        final ResourceSelector manager = new ResourceSelector(this, delegate, true);
        manager.setFilterType(Picture.class);
        manager.setResource(picture);
        manager.setLocationRelativeTo(this);
        manager.setVisible(true);

        // User is finished with the dialog
        manager.dispose();

        // It will close on its own
        picture = manager.getResource() == null && picture != null ? picture : (Picture) manager.getResource();

        // Just for good measure
        try {

            //
            if (picture != null) {

                // Give this illustration the picture
                illustration.setBlockRows(1);
                illustration.setBlockColumns(1);
                illustration.setPicture(picture);

                //
                final Image image = picture.getImage();
                final int imageWidth = image.getWidth(this);
                final int imageHeight = image.getHeight(this);
                final int fieldWidth = ((Number) blockWidthJField.getValue()).intValue();
                final int fieldHeight = ((Number) blockHeightJField.getValue()).intValue();

                //
                illustration.setBlockWidth(fieldWidth <= 0 ? imageWidth : fieldWidth);
                illustration.setBlockHeight(fieldHeight <= 0 ? imageHeight : fieldHeight);

                // Make sure the Image Panel is displaying the new picture
                imageJPanel.updatePanel(illustration);

                // Bind the width and height to the respective JFields
                binder.bindImage(picture, this);

                // Quick check for backdrops
                if (illustration instanceof Backdrop) {

                    //
                    blockRowJField.setValue(1);
                    blockColumnJField.setValue(1);
                    blockWidthJField.setValue(fieldWidth <= 0 ? image.getWidth(this) : fieldWidth);
                    blockHeightJField.setValue(fieldHeight <= 0 ? image.getHeight(this) : fieldHeight);
                }
            }
        } catch (ClassCastException cce) {
            //
        }

        // Refresh everything.
        updateImagePanel();
    }//GEN-LAST:event_imageJButtonActionPerformed

    private void generateJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateJButtonActionPerformed

        // Click this button to auto-generate all three forms of manifest-to-delegate identification
        binder.testButton();
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

    private void completeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_completeJButtonActionPerformed
        // TODO add your handling code here:
        if (picture == null) {

            //
            final String message = "Please choose an Image before Attempting to Automatically Fill\nout Form.";

            //
            JOptionPane.showMessageDialog(this, message);
        } else {

            //
            final String message = "Are you sure you want to Auto-Complete this form?";

            // Change the icon for the application
            if (JOptionPane.showConfirmDialog(rootPane, message, "Auto-Complete Form", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                //
                final Image image = picture.getImage();

                // Not fully working yet
                autoComplete(image);
            }
        }
    }//GEN-LAST:event_completeJButtonActionPerformed

    private void stretchJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stretchJCheckBoxActionPerformed
        //
        if (illustration instanceof Backdrop) {
            updateStretchBox();
        }
    }//GEN-LAST:event_stretchJCheckBoxActionPerformed

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
                blockHeightJField.setValue(number);
                //blockHeightJField.setValue((number % 2 == 1) ? ((number / 2) - 1) : (number / 2));
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
            viewer.setLocation((getX() - viewer.getWidth()) - 10, getY() + 32);
            viewer.setVisible(true);
            viewer.requestFocus();
        }
    }//GEN-LAST:event_formWindowOpened

    private void viewerJToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewerJToggleButtonActionPerformed
        // TODO add your handling code here:
        if (viewer == null || !viewer.isVisible()) {

            // Only if the animation exists
            if (illustration != null) {

                // @ANIMATION VIEWER
                if (illustration instanceof Animation) {

                    //
                    setupAnimationViewer((Animation) illustration);
                }
            }
        } else {
            viewer.setVisible(false);
            viewer.dispose();
            viewer = null;
            viewerJToggleButton.setSelected(false);
            viewerJToggleButton.setIcon(iconAnimationOff);
        }
    }//GEN-LAST:event_viewerJToggleButtonActionPerformed

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

    private void collisionJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_collisionJButtonActionPerformed
        // TODO add your handling code here:
        if (illustration instanceof Animation) {

            //
            final Animation animation = (Animation) illustration;

            // Animation must have images.
            if (!animation.isEmpty()) {

                //
                updateIllustration();

                //
                final BoundaryEditor editor = new BoundaryEditor(this, animation, true);
                editor.setVisible(true);
            }
        }
    }//GEN-LAST:event_collisionJButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel altJPanel;
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
    private javax.swing.JButton closeJButton;
    private javax.swing.JToggleButton collisionJButton;
    private javax.swing.JButton completeJButton;
    private javax.swing.JTextField displayJField;
    private javax.swing.JLabel displayJLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton generateJButton;
    private javax.swing.JButton gridJButton;
    private javax.swing.JTextField heightJField;
    private javax.swing.JLabel heightJLabel;
    private javax.swing.JButton imageJButton;
    private javax.swing.JScrollPane imageJScrollPane;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField locationJField;
    private javax.swing.JLabel locationJLabel;
    private javax.swing.JTabbedPane mainJTabbedPane;
    private javax.swing.JPanel manifestJPanel;
    private javax.swing.JTextField nameJField;
    private javax.swing.JLabel nameJLabel;
    private javax.swing.JLabel nullJLabel1;
    private javax.swing.JTextField packageJField;
    private javax.swing.JLabel packageJLabel;
    private javax.swing.JTextField referenceJField;
    private javax.swing.JLabel referenceJLabel;
    private javax.swing.JButton saveJButton;
    private javax.swing.JPanel settingJPanel;
    private javax.swing.JPanel sliceJPanel;
    private javax.swing.JPanel sliceTabJPanel;
    private javax.swing.JCheckBox stretchJCheckBox;
    private javax.swing.JButton textileJButton;
    private javax.swing.JPanel toolJPanel;
    private javax.swing.JPanel toolJPanel1;
    private javax.swing.JToggleButton viewerJToggleButton;
    private javax.swing.JTextField widthJField;
    private javax.swing.JLabel widthJLabel;
    // End of variables declaration//GEN-END:variables
}
