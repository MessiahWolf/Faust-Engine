/**
 * Copyright (c) 2013, Robert Cherry * All rights reserved.
 *
 * This file is part of the Faust Engine.
 *
 * The Faust Engine is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * The Faust Engine is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * the Faust Engine. If not, see <http://www.gnu.org/licenses/>.
 */
package io.resource;

import core.world.Picture;
import core.world.WorldObject;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RasterFormatException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Robert A. Cherry
 */
public class ResourceProducer {

    public static BufferedImage bufferImage(Image image, ImageObserver obs) {

        // Wait for it to load
        wait(image);

        //
        return bufferImage(image, obs, BufferedImage.TYPE_INT_ARGB);
    }

    public static BufferedImage bufferImage(Image image, ImageObserver obs, int type) {

        //
        int imageWidth = image.getWidth(obs);
        int imageHeight = image.getHeight(obs);

        // Try to force it this way
        if (imageWidth <= 0 || imageHeight <= 0) {
            final ImageIcon icon = new ImageIcon(image);
            imageWidth = icon.getIconWidth();
            imageHeight = icon.getIconHeight();
        }

        // Shell for the new BufferedImage
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, type);

        // Create the raster space
        Graphics2D manet = bufferedImage.createGraphics();

        // Draw the image and then Close it
        manet.drawImage(image, 0, 0, obs);
        manet.dispose();

        // Return the image
        return bufferedImage;
    }

    public static boolean isEmpty(Image image, ImageObserver obs) {

        // Buffer it so that we can read the bytes
        final BufferedImage buffered = bufferImage(image, obs);

        //
        final int width = image.getWidth(obs);
        final int height = image.getHeight(obs);

        //
        for (int i = 0; i < width; i++) {

            //
            for (int j = 0; j < height; j++) {

                // If its greater than zero than its a colored pixel
                if (buffered.getRGB(i, j) > 0) {
                    return false;
                }
            }
        }

        // This means that the image is completely blank
        return true;
    }

    public static BufferedImage createImage(byte[] byteStream) throws IOException {

        //
        return ImageIO.read(new ByteArrayInputStream(byteStream));
    }

    public static BufferedImage[] createImages(Picture resource, HashMap<String, Object> map) {

        //
        final BufferedImage image = bufferImage(resource.getImage(), null);

        // Grab from the hashmap
        int blockWidth = Integer.parseInt(String.valueOf(map.get("blockWidth")));
        int blockHeight = Integer.parseInt(String.valueOf(map.get("blockHeight")));
        int blockRows = Integer.parseInt(String.valueOf(map.get("blockRows")));
        int blockColumns = Integer.parseInt(String.valueOf(map.get("blockColumns")));
        int blockXOffset = Integer.parseInt(String.valueOf(map.get("blockXOffset")));
        int blockYOffset = Integer.parseInt(String.valueOf(map.get("blockYOffset")));
        int blockHGap = Integer.parseInt(String.valueOf(map.get("blockHGap")));
        int blockVGap = Integer.parseInt(String.valueOf(map.get("blockVGap")));

        //
        final BufferedImage[] bufferedImages = new BufferedImage[blockRows * blockColumns];

        //
        int count = -1;

        // Create boxes to represent the cuts applied to the Image to create the Tileset or Animation
        for (int row = 0; row < blockRows; row++) {

            //
            for (int column = 0; column < blockColumns; column++) {

                // Quick check
                final int x = blockXOffset + (column * (blockWidth + blockHGap));
                final int y = blockYOffset + (row * (blockHeight + blockVGap));

                // Creating a new array of pixels.
                final BufferedImage subImage = new BufferedImage(blockWidth, blockHeight, BufferedImage.TYPE_INT_ARGB);

                // Create a Raster of Data to be Manipulated
                final Graphics2D manet = subImage.createGraphics();

                //
                try {

                    // @RasterFormatException
                    // Draw the specific Rectangle of the Tileset
                    manet.drawImage(image.getSubimage(x, y, blockWidth, blockHeight), 0, 0, null);
                    count++;
                } catch (RasterFormatException rfe) {
                    
                    // Precheck
                    if (y >= image.getHeight() || x >= image.getWidth()) {
                        return null;
                    }                    

                    if (y + blockHeight > image.getHeight() && x + blockWidth <= image.getWidth()) {
                        manet.drawImage(image.getSubimage(x, y, blockWidth, blockHeight - blockYOffset), 0, 0, null);
                        count++;
                    } else {
                        //
                        count++;
                        continue;
                    }
                }

                // Free the memory this object holds.
                manet.dispose();

                // Add to collection
                bufferedImages[count] = subImage;
            }
        }

        //
        return bufferedImages;
    }

    public static BufferedImage mirrorHorizontal(BufferedImage newImage) {

        // Coordinates
        int imageWidth = newImage.getWidth();
        int imageHeight = newImage.getHeight();
        int startX = 0;

        // Mock Image
        BufferedImage mirrorImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        // Flipping the Image Horizontally
        for (int rowPixel = imageWidth - 1; rowPixel >= 0; rowPixel--) {
            for (int columnPixel = 0; columnPixel < imageHeight; columnPixel++) {
                mirrorImage.setRGB(rowPixel, columnPixel, newImage.getRGB(startX, columnPixel));
            }

            // No need to restart
            startX++;
        }

        // Return the Horizontally flipped Image
        return mirrorImage;
    }

    public static BufferedImage mirrorVertical(BufferedImage newImage) {

        // Coordinates
        int imageWidth = newImage.getWidth();
        int imageHeight = newImage.getHeight();
        int startY = 0;

        // Mock Image
        BufferedImage mirrorImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        // Flipping the Image Horizontally
        for (int rowPixel = 0; rowPixel < imageWidth; rowPixel++) {
            for (int columnPixel = imageHeight - 1; columnPixel >= 0; columnPixel--) {
                mirrorImage.setRGB(rowPixel, columnPixel, newImage.getRGB(rowPixel, startY));
                startY++;
            }

            // Restart nessecary
            startY = 0;
        }

        // Return the Vertically flipped Image
        return mirrorImage;
    }

    public static File writeImage(File outputDirectory, String neweditorName, BufferedImage outImage) {

        // Output File
        File newFile = null;

        // Soon
        try {

            // Name of the Image
            String imageName = neweditorName + ".png";

            // Create a new File Location
            newFile = new File(imageName);
            newFile.renameTo(new File(outputDirectory.getAbsolutePath() + "\\" + imageName));

            //
            int option = JOptionPane.OK_OPTION;

            // Gives User a choice to override an existing image
            if (newFile.exists()) {
                option = JOptionPane.showConfirmDialog(null, "A file named\"" + newFile.getName() + "\" already exists. Overwrite existing file?");
            }

            // Write over it.
            if (option == JOptionPane.OK_OPTION) {

                // Write the Image out
                ImageIO.write(outImage, "png", newFile);
            }

        } catch (IOException io) {
            System.err.println(io);
        }

        // Return the file
        return newFile;
    }

    public static boolean isTransparent(int testPixel, int lean) {

        // This pixel is completely transparent;
        // Took a lot of research when I was 16 to figure this solution out.
        return ((testPixel >> 24) & 0xFF) <= lean;
    }

    @SuppressWarnings("SleepWhileInLoop")
    public static void wait(Image image) {
        int count = 0;
        class ImageLoadStatus {

            public boolean widthDone = false;
            public boolean heightDone = false;
        }

        final ImageLoadStatus imageLoadStatus = new ImageLoadStatus();
        image.getHeight(new ImageObserver() {
            @Override
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                if (infoflags == ALLBITS) {
                    imageLoadStatus.heightDone = true;
                    return true;
                }
                return false;
            }
        });
        image.getWidth(new ImageObserver() {
            @Override
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                if (infoflags == ALLBITS) {
                    imageLoadStatus.widthDone = true;
                    return true;
                }
                return false;
            }
        });
        while ((!imageLoadStatus.widthDone && !imageLoadStatus.heightDone) && count < 1) {
            try {
                count++;
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                System.err.println(ie);
            }
        }

        // Close this thread
        System.gc();
    }

    // <editor-fold defaultstate="collapsed">
    private void createFrameIcon() {

        // Presets
        Color c1 = new Color(255, 0, 0);
        Color c2 = new Color(255, 102, 0);
        Color c3 = new Color(255, 148, 0);
        Color c4 = new Color(254, 197, 0);
        Color c5 = new Color(255, 255, 0);
        Color c6 = new Color(140, 199, 0);
        Color c7 = new Color(15, 173, 0);
        Color c8 = new Color(0, 163, 194);
        Color c9 = new Color(0, 100, 181);
        Color c10 = new Color(0, 16, 165);
        Color c11 = new Color(99, 0, 165);
        Color c12 = new Color(197, 0, 124);

        //Color[] colors = {c1, c2, c3, c4, c5, c6};
        Color[] colors = {Color.WHITE, Color.YELLOW, Color.WHITE, Color.YELLOW, Color.WHITE, Color.YELLOW};
        int sizeW = 24;
        int sizeH = 24;
        float gapX = (float) (sizeW / (Math.PI * 4));
        float gapY = (float) (sizeH / (Math.PI * 4));
        float centerW = (float) (sizeW / Math.PI);
        float centerH = (float) (sizeH / Math.PI);

        float prop;

        // Define our canvas and Graphics Object
        BufferedImage image = new BufferedImage(sizeW, sizeH, BufferedImage.TYPE_INT_ARGB);

        Graphics monet = image.createGraphics();

        Graphics2D manet = (Graphics2D) monet;
        manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw onto the canvas
        for (int i = 1; i < colors.length + 1; i++) {
            prop = (float) ((i * 360.0f) / colors.length);
            //// System.out.println(prop);

            Arc2D.Float newArc = new Arc2D.Float(gapX, gapY, sizeW - (2 * gapX), sizeH - (2 * gapY), prop, 360.0f / i, Arc2D.PIE);

            manet.setColor(colors[i - 1]);
            //manet.fill(newArc);

            // For the frame icon though we want a huge middle section cut out so we use the Area.class
            Area shape = new Area(newArc);
            Area center = new Area(new Ellipse2D.Float(sizeW / 2 - centerW / 2, sizeH / 2 - centerH / 2, centerW, centerH));

            shape.subtract(center);

            // draw that
            manet.fill(shape);
        }

        // Create an outline for the image
        Color outlineColor = new Color(235, 235, 235);
        manet.setColor(outlineColor);
        manet.setStroke(new BasicStroke(1f));
        manet.draw(new Ellipse2D.Float(gapX, gapY, sizeW - (2 * gapX), sizeH - (2 * gapY)));
        manet.draw(new Ellipse2D.Float(sizeW / 2 - centerW / 2, sizeH / 2 - centerH / 2, centerW, centerH));

        // Dispose of the graphics object
        manet.dispose();

        // Print out the Image
        try {
            ImageIO.write(image, "png", new File("data\\nuclearICON.png"));
        } catch (IOException io) {
            //
        }

        // Show me a preview
        JLabel label = new JLabel(new ImageIcon(image));

        // Joptionpane
        JOptionPane.showMessageDialog(null, label);

        // Not final so quit program afterwards
        System.exit(0);
    }

    public static BufferedImage createMissingAnimationImage(WorldObject worldObject) {

        int width = 24;
        int height = 24;

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D manet = (Graphics2D) newImage.createGraphics();
        manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        manet.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        // Call in some font metrics
        FontMetrics newMetrics = new FontMetrics(new Font("Arial Bold", Font.BOLD, 16)) {
            // Ignore
        };
        Rectangle2D stringBoundRect = (Rectangle2D) newMetrics.getStringBounds("X", manet);
        Rectangle iconRect = new Rectangle(0, 0, width - 1, height - 1);
        // Start to color them in
        manet.setColor(Color.WHITE);
        manet.fill(iconRect);
        manet.setColor(Color.BLACK);
        manet.draw(iconRect);
        manet.setColor(Color.RED);
        manet.setFont(new Font("Arial Bold", Font.BOLD, 16));
        manet.drawString("X", width / 2 - (int) (stringBoundRect.getWidth() / 2), height / 2 + (int) (stringBoundRect.getHeight() / 2) - 2);
        manet.dispose();

        //
        return newImage;
    }
    // </editor-fold>
}
