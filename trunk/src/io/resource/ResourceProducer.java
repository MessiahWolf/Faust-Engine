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
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import sun.awt.image.ToolkitImage;

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
            ImageIcon icon = new ImageIcon(image);
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

    public static boolean challengeImage(Image image, ImageObserver obs) {

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

    public static Image createImage(byte[] byteStream) {

        // Recreate the Image from the stream of bytes.
        Image falseImage = Toolkit.getDefaultToolkit().createImage(byteStream);

        // Cheap way of getting around the false image thing -- Forces it to load
        falseImage = new ImageIcon(falseImage).getImage();

        //
        return falseImage;
    }

    public static BufferedImage[] createImages(Picture resource, HashMap<String, Object> map) {

        //
        Image image = resource.getImage();

        // Check first
        if (image instanceof ToolkitImage) {
            image = bufferImage(image, null);
        }

        // Create a new set of rectangles
        final ArrayList<Rectangle> rectangles = new ArrayList<>();

        //
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        // Grab from the hashmap
        int blockWidth = Integer.parseInt(String.valueOf(map.get("blockWidth")));
        int blockHeight = Integer.parseInt(String.valueOf(map.get("blockHeight")));
        int blockRows = Integer.parseInt(String.valueOf(map.get("blockRows")));
        int blockColumns = Integer.parseInt(String.valueOf(map.get("blockColumns")));
        int blockXOffset = Integer.parseInt(String.valueOf(map.get("blockXOffset")));
        int blockYOffset = Integer.parseInt(String.valueOf(map.get("blockYOffset")));
        int blockHGap = Integer.parseInt(String.valueOf(map.get("blockHGap")));
        int blockVGap = Integer.parseInt(String.valueOf(map.get("blockVGap")));

        // Create boxes to represent the cuts applied to the Image to create the Tileset or Animation
        for (int row = 0; row < blockRows; row++) {

            //
            for (int column = 0; column < blockColumns; column++) {

                // Create a new Rectangle
                final Rectangle rectangle = new Rectangle(blockXOffset + (row * (blockWidth + blockHGap)), blockYOffset + (column * (blockHeight + blockVGap)), blockWidth, blockHeight);

                // Add to the Rectangle Collection
                rectangles.add(rectangle);
            }
        }

        //
        final BufferedImage[] bufferedImages = new BufferedImage[rectangles.size()];

        // Fill the Image array with splitted image parts
        for (int i = 0; i < rectangles.size(); i++) {

            //
            final Rectangle rectangle = rectangles.get(i);

            // Creating a new array of pixels.
            final BufferedImage newImage = new BufferedImage(blockWidth, blockHeight, BufferedImage.TYPE_INT_ARGB);
            final BufferedImage blockImage = (BufferedImage) image;

            // Create a Raster of Data to be Manipulated
            Graphics2D manet = newImage.createGraphics();

            //
            try {

                // @RasterFormatException
                // Draw the specific Rectangle of the Tileset
                manet.drawImage(blockImage.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height), 0, 0, null);
            } catch (RasterFormatException rfe) {

                // So normall would be oob
                if ((rectangle.x + rectangle.width) > imageWidth || (rectangle.y + rectangle.height) > imageHeight) {

                    // This would then be the result
                    manet.drawImage(blockImage.getSubimage(blockXOffset, blockYOffset, rectangle.width - blockXOffset, rectangle.height - blockYOffset), 0, 0, null);
                }
            }

            // Free the memory this object holds.
            manet.dispose();

            // Add to collection
            bufferedImages[i] = newImage;
        }

        //
        return bufferedImages;
    }

    public static void displayShape(String newTitle, Shape newShape) {

        //
        int shapeWidth = newShape.getBounds().width;
        int shapeHeight = newShape.getBounds().height;

        // Mock Image
        BufferedImage bufferedImage = new BufferedImage(shapeWidth, shapeHeight, BufferedImage.TYPE_INT_ARGB);

        // Create Graphics from this new Image Canvas
        Graphics monet = bufferedImage.createGraphics();

        // Cast to 2D Graphics
        Graphics2D manet = (Graphics2D) monet;

        // Adjust the Graphics Context
        manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the color
        manet.setColor(Color.BLUE);

        // Draw the shape
        manet.fill(newShape);

        // Dispose of this graphics objects
        manet.dispose();
    }

    public static void displayPoint(Point[] newPoints, int imageWidth, int imageHeight) {

        BufferedImage newImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        //
        int gridWidth = 16;
        int gridHeight = 16;
        int gridRows = newImage.getWidth() / gridWidth;
        int gridColumns = newImage.getHeight() / gridHeight;

        //
        Graphics2D manet = newImage.createGraphics();

        //
        manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridColumns; j++) {

                // new Rectangle
                Rectangle newRect = new Rectangle(i * gridWidth, j * gridHeight, gridWidth, gridHeight);

                // Draw it
                manet.setColor(Color.WHITE);
                manet.fill(newRect);
                manet.setColor(Color.BLACK);
                manet.draw(newRect);
            }
        }

        manet.draw(new Rectangle(0, 0, imageWidth, imageHeight));

        for (int i = 0; i < newPoints.length; i++) {
            Point newPoint = newPoints[i];
            //
            Ellipse2D.Double newEllipse = new Ellipse2D.Double(newPoint.x, newPoint.y, 1, 1);

            // Plot the point
            manet.setColor(Color.RED);
            manet.fill(newEllipse);
        }
        //
        manet.dispose();
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

    public static BufferedImage changeColor(BufferedImage refImage, Color oldColor, Color newColor) {

        // Create a shadow copy
        BufferedImage newImage = refImage;

        // Determine which bits we need
        for (int rowPixel = 0; rowPixel < newImage.getWidth(); rowPixel++) {
            for (int columnPixel = 0; columnPixel < newImage.getHeight(); columnPixel++) {

                // Short all the way past the red pixels over to the alpha pixels
                int rgb = newImage.getRGB(rowPixel, columnPixel);
                if (rgb == oldColor.getRGB()) {
                    newImage.setRGB(rowPixel, columnPixel, newColor.getRGB());
                }
            }
        }

        return newImage;
    }

    public static Image makeColorTransparent(BufferedImage im, final Color color) {
        ImageFilter filter;
        filter = new RGBImageFilter() {
            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            @Override
            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    public static BufferedImage setTransparentPixels(BufferedImage refImage, int newColor) {

        // Create a shadow copy
        BufferedImage newImage = refImage;

        for (int rowPixel = 0; rowPixel < newImage.getWidth(); rowPixel++) {
            for (int columnPixel = 0; columnPixel < newImage.getHeight(); columnPixel++) {

                // Shift all the way of past the red pixels to the alpha pixels
                if (isTransparent(newImage.getRGB(rowPixel, columnPixel), 0)) {
                    // Since we have
                    newImage.setRGB(rowPixel, columnPixel, newColor);
                }
            }
        }

        return newImage;
    }

    public static boolean isTransparent(int testPixel, int lean) {
        if (((testPixel >> 24) & 0xFF) <= lean) {
            // This pixel is completely transparent
            return true;
        }

        // Its not completely transparent
        return false;
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
            //System.out.println(prop);

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
