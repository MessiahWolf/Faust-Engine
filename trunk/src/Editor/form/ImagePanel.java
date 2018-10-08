/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.form;

import core.event.AnimationEvent;
import core.event.AnimationListener;
import core.world.Animation;
import core.world.Backdrop;
import core.world.Illustration;
import core.world.Picture;
import core.world.Tileset;
import io.resource.ResourceReader;
import io.util.FileUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *
 * @author Robert A. Cherry
 */
public class ImagePanel extends javax.swing.JPanel implements AnimationListener {

    // Variable Declaration
    // Swing Native Classes
    private final JScrollPane parentPane;
    // Java Native Classes
    private Object resource;
    private Point pointFocal;
    // Java Class
    private final Color backgroundColor = new Color(200, 200, 200);
    private Color textileBackground = Color.LIGHT_GRAY;
    private Color textileForeground = Color.WHITE;
    // Data Types
    private boolean showTextile = false;
    private boolean showImage = false;
    private boolean imageCentered = true;
    private float widthPercent = 1.0f;
    private float heightPercent = 1.0f;
    // End of Variable Declaration

    public ImagePanel(JScrollPane parentPane) {
        initComponents();

        //
        this.parentPane = parentPane;

        //
        init();
    }

    private void init() {

        //
        pointFocal = new Point(0, 0);
    }

    public void updatePanel(Object resource) {

        // Set as new resource
        this.resource = resource;

        // * Accepted classes at this time are WorldResources, and soon Files.
        // Change the content panel
        changeContentPane();

        // Repaint entire thing
        repaint();
    }

    private void changePaneDimension(Image image) {

        //
        if (image != null) {

            //
            final Dimension dimension = new Dimension(image.getWidth(this), image.getHeight(this));

            // Fit to imagePanel size
            setPreferredSize(dimension);

            final JViewport port = new JViewport();
            port.setPreferredSize(dimension);
            port.setView(this);

            //
            parentPane.setViewport(port);
        }
    }

    public void updatePanelSize() {

        if (resource == null) {
            return;
        }

        //
        Dimension dimension = getPreferredSize();

        //
        if (resource instanceof Illustration) {

            //
            Picture picture = ((Illustration) resource).getPicture();

            //
            if (picture != null) {
                dimension = new Dimension(picture.getWidth(), picture.getHeight());
            }
        } else if (resource instanceof Picture) {

            //
            Picture picture = (Picture) resource;

            //
            if (picture != null) {
                dimension = new Dimension(picture.getWidth(), picture.getHeight());
            }
        }

        // Fit to imagePanel size
        setPreferredSize(dimension);

        final JViewport port = parentPane.getViewport();
        port.setPreferredSize(dimension);
        port.setView(this);

        parentPane.setViewport(port);
    }

    private void changeContentPane() {

        //
        if (resource != null) {

            // Change to imageJPanel
            if (resource instanceof Picture) {

                // Grab its image for easy reference
                final Image image = ((Picture) resource).getImage();

                // Change to the new size
                changePaneDimension(image);
            } else if (resource instanceof Animation) {

                //
                final Animation animation = (Animation) resource;

                //
                final Image image = animation.getCurrentImage();

                // Change to new size
                changePaneDimension(image);
            } else if (resource instanceof Backdrop) {

                //
                final Backdrop backdrop = (Backdrop) resource;

                //
                final Image image = backdrop.draw(this, 1.0f);

                //
                changePaneDimension(image);
            } else if (resource instanceof Tileset) {

                //
                final Tileset tileset = (Tileset) resource;

                //
                final Picture picture = tileset.getPicture();

                //
                if (picture != null) {

                    //
                    final Image image = picture.getImage();

                    //
                    changePaneDimension(image);
                }
            } else if (resource instanceof File) {

                // Cast
                final File file = (File) resource;

                //
                final String extension = FileUtils.getExtension(file).toUpperCase();

                //
                switch (extension) {
                    case "PNG":
                    case "BMP":
                        updatePanel(ResourceReader.readImage(file));
                        break;
                    case "TXT":
                        break;
                }

                //
            } else if (resource instanceof Image) {

                //
                final Image image = (Image) resource;

                // Change once again.
                changePaneDimension(image);
            }
        }
    }

    @Override
    public void paintComponent(Graphics monet) {

        //
        final Graphics2D manet = (Graphics2D) monet;

        // Paint super.
        super.paintComponent(manet);

        // Draw the textile background or not.
        if (showTextile) {
            drawTextileBackground(manet);
        } else {
            // Use our chosen background color.
            manet.setColor(backgroundColor);
            manet.fillRect(0, 0, getWidth(), getHeight());
        }

        // Quick kick out for no image.
        if (!showImage) {
            return;
        }
        
        // Null Check ->
        if (resource != null) {

            // Solve for Pictures
            if (resource instanceof Picture) {
                //
                drawSingleImage( ((Picture) resource).getImage(), monet);
            } else if (resource instanceof Animation) {
                // Draw animation style
                drawAnimation((Animation)resource, monet);
            } else if (resource instanceof Backdrop) {
                //
                drawBackdrop((Backdrop) resource, monet);
            } else if (resource instanceof Tileset) {

                //
                final Tileset set = (Tileset) resource;
                final Picture graphic = set.getPicture();

                //
                if (graphic != null) {

                    //
                    final Image image = set.getPicture().getImage();

                    //
                    drawSingleImage(image, monet);
                }
            } else if (resource instanceof Image) {
                //
                drawSingleImage((Image) resource, monet);
            }
        }
    }

    private void drawSingleImage(Image image, Graphics monet) {

        //
        final Graphics2D manet = (Graphics2D) monet;

        //
        if (image != null) {

            // Grab width and height
            final int width = image.getWidth(this);
            final int height = image.getHeight(this);

            // Just in case we need to scale the image.
            image = image.getScaledInstance((int) (width * widthPercent), (int) (height * heightPercent), Image.SCALE_SMOOTH);

            // Center the point about the center of the imageJPanel
            pointFocal = new Point(((getWidth() / 2) - (image.getWidth(this) / 2)) + 1, ((getHeight() / 2) - (image.getHeight(this) / 2)) + 1);

            // Draw the image on the imageJPanel at the focus point
            manet.drawImage(image, imageCentered ? pointFocal.x : 0, imageCentered ? pointFocal.y : 0, this);
        }
    }

    private void drawAnimation(Animation animation, Graphics monet) {

        //
        final Graphics2D manet = (Graphics2D) monet;

        // Null Check ->
        if (animation != null) {

            // Empty Check ->
            if (!animation.isEmpty()) {

                // Grab the entire image the animation is sending out (includes border)
                final BufferedImage image = animation.draw(this, 1.0f);

                // Image must exist
                if (image != null) {

                    // Center the point about the center of the imageJPanel
                    final Point centerPoint = new Point(((getWidth() / 2) - (image.getWidth(this) / 2)) + 1, ((getHeight() / 2) - (image.getHeight(this) / 2)) + 1);

                    // Draw the image on the imageJPanel at the focus point
                    manet.drawImage(image, imageCentered ? centerPoint.x : 0, imageCentered ? centerPoint.y : 0, this);
                }
            }
        } else {

            // Apply the rendering hint
            manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Overlay
            manet.setColor(new Color(160, 160, 160));

            //
            final String message = "No Animation Set";

            // Draw Text
            final int width = (int) manet.getFontMetrics().getStringBounds(message, manet).getWidth();
            final int height = (int) manet.getFontMetrics().getStringBounds(message, manet).getHeight();

            // Draw the string
            manet.drawString(message, getWidth() / 2 - width / 2, getHeight() / 2 + height / 2);
        }
    }

    private void drawBackdrop(Backdrop backdrop, Graphics monet) {
        //
        final Graphics2D manet = (Graphics2D) monet;

        // Null Check ->
        if (backdrop != null) {

            // Grab the entire image the animation is sending out (includes border)
            final BufferedImage image = backdrop.draw(this, 1.0f);

            // Image must exist
            if (image != null) {

                //
                manet.drawImage(image, 0, 0, this);
            }
        }
    }

    private void drawTextileBackground(Graphics2D manet) {

        //
        final int rowSplit = 16;
        final int colSplit = 16;

        //
        final int parentWidth = parentPane == null ? getWidth() : parentPane.getWidth();
        final int parentHeight = parentPane == null ? getHeight() : parentPane.getHeight();

        //
        final int imageWidth = getPreferredSize().width;
        final int imageHeight = getPreferredSize().height;

        //
        final int width = parentWidth > imageWidth ? parentWidth : imageWidth;
        final int height = parentHeight > imageHeight ? parentHeight : imageHeight;

        //
        final int rowExtra = 2;
        final int colExtra = 2;

        // Calc. Rows and columns
        final int rows = (width / rowSplit) + rowExtra;
        final int cols = (height / colSplit) + colExtra;

        // Initial color
        Color color = textileForeground;

        // Drawing the rectangles.
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                // Alternating colors (Took a lot of playing around to get to this code)
                // @Ternary Option
                color = color == textileBackground ? textileForeground : textileBackground;

                // Giving it a little offset in columns
                if (j % 2 == 0) {
                    if (j == cols) {
                        //@Ternary Option
                        color = color == textileForeground ? textileBackground : textileForeground;
                    }
                } else if (j == cols - 1) {
                    //@Ternary Option
                    color = color == textileForeground ? textileBackground : textileForeground;
                }

                // Set the color
                manet.setColor(color);

                // Fill the rectangle
                manet.fill(new Rectangle(i * 16, j * 16, 16, 16));
            }
        }
    }

    @Override
    public void animationEnd(AnimationEvent event) {
        repaint();
    }

    @Override
    public void animationStep(AnimationEvent event) {
        repaint();
    }

    public void setShowTextile(boolean showTextile) {
        this.showTextile = showTextile;
    }

    public void setTextileBackground(Color textileBackground) {
        this.textileBackground = textileBackground;
    }

    public void setTextileForeground(Color textileForeground) {
        this.textileForeground = textileForeground;
    }

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }
    
    public void setImageCentered(boolean imageCentered) {
        this.imageCentered = imageCentered;
    }

    public void setWidthPercent(float widthPercent) {
        this.widthPercent = widthPercent;
    }

    public void setHeightPercent(float heightPercent) {
        this.heightPercent = heightPercent;
    }

    public Color getTextileBackground() {
        return textileBackground;
    }

    public Color getTextileForeground() {
        return textileForeground;
    }

    public float getWidthPercent() {
        return widthPercent;
    }

    public float getHeightPercent() {
        return heightPercent;
    }

    public Point getFocalPoint() {
        return pointFocal;
    }
    
    public boolean getImageCentered() {
        return imageCentered;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMaximumSize(new java.awt.Dimension(325, 325));
        setMinimumSize(new java.awt.Dimension(325, 325));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 325, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 325, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
