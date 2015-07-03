/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.form;

import core.event.AnimationEvent;
import core.event.AnimationListener;
import core.world.Animation;
import core.world.Backdrop;
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
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *
 * @author Robert A. Cherry
 */
public class ImagePanel extends javax.swing.JPanel implements AnimationListener {

    // Variable Declaration
    // Swing Native Classes
    private JScrollPane parentPane;
    // Java Native Classes
    private ImageIcon iconPlay;
    private ImageIcon iconPause;
    private Object resource;
    private Point pointFocal;
    // Data Types
    private boolean showTextile = false;
    private boolean showImage = true;
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
        final Class closs = getClass();

        //
        iconPlay = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-color-play24.png");
        iconPause = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-color-pause24.png");

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

            //
            animationJPanel.setOpaque(false);
            animationJPanel.setEnabled(true);

            //
            playJButton.setEnabled(true);
            playJButton.setVisible(true);
            playJButton.setIcon(iconPlay);
            playJButton.setOpaque(false);

            pauseJButton.setEnabled(true);
            pauseJButton.setVisible(true);
            pauseJButton.setIcon(iconPause);
            pauseJButton.setOpaque(false);
        } else {

//            //
//            final Dimension dimension = parentPane.getViewport().getPreferredSize();
//
//            // Fit to imagePanel size
//            setPreferredSize(dimension);
//            setMaximumSize(dimension);
//            setMinimumSize(dimension);
//
//            //
//            parentPane.setViewportView(this);

            //
            animationJPanel.setOpaque(false);
            animationJPanel.setEnabled(false);

            //
            playJButton.setEnabled(false);
            playJButton.setVisible(false);

            pauseJButton.setEnabled(false);
            pauseJButton.setVisible(false);
        }
    }

    public void forceResize(Dimension dimension) {

        // Fit to imagePanel size
        setPreferredSize(dimension);

        final JViewport port = new JViewport();
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

                // Enabled togglability
                animationJPanel.setEnabled(true);
                animationJPanel.setVisible(true);

                //
                final Animation animation = (Animation) resource;

                //
                final Image image = animation.getCurrentImage();

                // Change to new size
                changePaneDimension(image);
            } else if (resource instanceof Backdrop) {

                //
                Backdrop backdrop = (Backdrop) resource;

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
        }

        // Null Check ->
        if (resource != null) {

            // Solve for FImages
            if (resource instanceof Picture) {

                // Cast to Image
                final Picture graphic = (Picture) resource;

                //
                final Image image = graphic.getImage();

                //
                if (showImage) {
                    drawSingleImage(image, monet);
                }
            } else if (resource instanceof Animation) {
                //
                final Animation animation = (Animation) resource;

                // Draw animation style
                if (showImage) {
                    drawAnimation(animation, monet);
                }
            } else if (resource instanceof Backdrop) {

                //
                final Backdrop backdrop = (Backdrop) resource;

                //
                if (showImage) {
                    drawBackdrop(backdrop, monet);
                }
            } else if (resource instanceof Tileset) {

                //
                final Tileset set = (Tileset) resource;
                final Picture graphic = set.getPicture();

                //
                if (graphic != null) {

                    //
                    final Image image = set.getPicture().getImage();

                    //
                    if (showImage) {
                        drawSingleImage(image, monet);
                    }
                }
            } else if (resource instanceof Image) {
                //
                if (showImage) {
                    drawSingleImage((Image) resource, monet);
                }
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

            //
            image = image.getScaledInstance((int) (width * widthPercent), (int) (height * heightPercent), Image.SCALE_SMOOTH);

            // Center the point about the center of the imageJPanel
            pointFocal = new Point(((getWidth() / 2) - (image.getWidth(this) / 2)) + 1, ((getHeight() / 2) - (image.getHeight(this) / 2)) + 1);

            // Draw the image on the imageJPanel at the focus point
            manet.drawImage(image, pointFocal.x, pointFocal.y, this);
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
                    manet.drawImage(image, centerPoint.x, centerPoint.y, this);
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
        Color color = Color.WHITE;

        //
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                // Alternating colors
                if (color == Color.LIGHT_GRAY) {
                    color = Color.WHITE;
                } else {
                    color = Color.LIGHT_GRAY;
                }

                // Giving it a little offset in columns
                if (j % 2 == 0) {

                    if (j == cols) {
                        if (color == Color.WHITE) {
                            color = Color.LIGHT_GRAY;
                        } else {
                            color = Color.WHITE;
                        }
                    }
                } else {

                    if (j == cols - 1) {
                        if (color == Color.WHITE) {
                            color = Color.LIGHT_GRAY;
                        } else {
                            color = Color.WHITE;
                        }
                    }
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

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    public void setWidthPercent(float widthPercent) {
        this.widthPercent = widthPercent;
    }

    public void setHeightPercent(float heightPercent) {
        this.heightPercent = heightPercent;
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        animationJPanel = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        playJButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        pauseJButton = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(325, 325));
        setMinimumSize(new java.awt.Dimension(325, 325));

        animationJPanel.setMaximumSize(new java.awt.Dimension(156, 24));
        animationJPanel.setMinimumSize(new java.awt.Dimension(156, 24));
        animationJPanel.setPreferredSize(new java.awt.Dimension(156, 24));
        animationJPanel.setLayout(new javax.swing.BoxLayout(animationJPanel, javax.swing.BoxLayout.LINE_AXIS));
        animationJPanel.add(filler2);

        playJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        playJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        playJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        playJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playJButtonActionPerformed(evt);
            }
        });
        animationJPanel.add(playJButton);
        animationJPanel.add(filler1);

        pauseJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        pauseJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        pauseJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        pauseJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseJButtonActionPerformed(evt);
            }
        });
        animationJPanel.add(pauseJButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(159, Short.MAX_VALUE)
                .addComponent(animationJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(290, Short.MAX_VALUE)
                .addComponent(animationJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void playJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playJButtonActionPerformed

        // TODO add your handling code here:
        if (resource instanceof Animation) {

            // Cast
            final Animation animation = (Animation) resource;

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
        if (resource instanceof Animation) {

            //
            final Animation animation = (Animation) resource;

            // Pause the animation
            animation.setCycles(0);
            animation.setDelay(0);
            animation.pause();
        }
    }//GEN-LAST:event_pauseJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel animationJPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton pauseJButton;
    private javax.swing.JButton playJButton;
    // End of variables declaration//GEN-END:variables
}
