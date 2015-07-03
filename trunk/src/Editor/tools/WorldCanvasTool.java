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
package Editor.tools;

import core.world.Actor;
import core.world.Animation;
import core.world.WorldTile;
import core.world.Tileset;
import core.world.WorldItem;
import core.world.WorldCellLayer;
import core.world.WorldObject;
import Editor.form.WorldCanvas;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 *
 * @author Robert A. Cherry
 */
public class WorldCanvasTool {

    // Variable Declaration
    private Color color;
    private Dimension gridSize;
    private Image markerImage;
    private Image eraserImage;
    private Image selectorImage;
    private Image toolImage;
    private Point pointLocation;
    private Rectangle rectangleCell;
    // Project Classes
    private WorldCanvas worldCanvas;
    private Animation animationShadow;
    private WorldTile tileShadow;
    // Data types
    private boolean visible;
    private boolean snapToCenter;
    private float lastRotation;
    private int lastX;
    private int lastY;
    private int mode;
    public static final int FLAG_MARKER = 0x001;
    public static final int FLAG_ERASER = 0x002;
    public static final int FLAG_SELECT = 0x003;
    // End of Variable Declaration

    public WorldCanvasTool(WorldCanvas worldCanvas) {

        // Set values equal
        this.worldCanvas = worldCanvas;

        // Initialize
        init();
    }

    private void init() {

        // Instantiation
        pointLocation = new Point();
        gridSize = worldCanvas.getGrid().getSize();
        rectangleCell = new Rectangle(pointLocation.x, pointLocation.y, gridSize.width, gridSize.height);

        //
        visible = true;
        snapToCenter = true;

        // Load the images for the fMap tool
        try {

            //
            final Toolkit kit = Toolkit.getDefaultToolkit();
            final Class closs = getClass();

            //
            selectorImage = kit.getImage(closs.getResource("/Editor/icons/icon-selector24.png"));
            markerImage = kit.getImage(closs.getResource("/Editor/icons/icon-marker24.png"));
            eraserImage = kit.getImage(closs.getResource("/Editor/icons/icon-eraser24.png"));
        } catch (NullPointerException npe) {
        }

        // Preset values
        setMode(FLAG_MARKER);
    }

    public void revalidate() {

        //
        gridSize.setSize(worldCanvas.getGrid().getSize());

        // Reset back to zero
        pointLocation.setLocation(gridSize.width, gridSize.height);
    }

    public void rotateTileClockwise(float change) {
        if (tileShadow != null) {
            tileShadow.rotateClockwise(change);
        }
    }

    public void rotateTileCounterClockwise(float change) {
        if (tileShadow != null) {
            tileShadow.rotateCounterClockwise(change);
        }
    }

    public void move(Point point) {

        // Placing at the new Location
        pointLocation.x = point.x - point.x % gridSize.width;
        pointLocation.y = point.y - point.y % gridSize.height;

        // Snap to center option
        if (snapToCenter == true) {

            //
            if (tileShadow == null) {
                return;
            }

            //
            if (tileShadow.getGraphic() != null) {
                return;
            }

            //
            final BufferedImage image = tileShadow.getGraphic();
            pointLocation.x = point.x - (point.x % gridSize.width) + (image.getWidth() / 2 + (toolImage.getWidth(worldCanvas) / 2));
            pointLocation.y = point.y - (point.y % gridSize.height) + (image.getHeight() / 2 + (toolImage.getWidth(worldCanvas) / 2));
        }

        // Record last position
        lastX = pointLocation.x;
        lastY = pointLocation.y;

        // Update on block enter and exit only. -- Saves useless mouse move events
        if (lastX != rectangleCell.x || lastY != rectangleCell.y) {

            // Cell changed
            rectangleCell.setBounds(pointLocation.x, pointLocation.y, gridSize.width, gridSize.height);

            // Tell WorldCanvas to repaint
            worldCanvas.repaint();
        }
    }

    public void shadow(WorldObject object) {

        if (object instanceof Actor) {

            // Just show a preview of its idle animation
            animationShadow = ((Actor) object).getAnimation();
        } else if (object instanceof WorldItem) {

            // Show a preview of its current animation
            animationShadow = ((WorldItem) object).getAnimation();
        }
    }

    public void paintTile(WorldCellLayer layer) {

        // The Tile to paint must exist
        if (tileShadow != null) {

            // Layer must exist
            if (layer != null) {

                // Do not allow overlap
                if (layer.isInstanceAtPosition(pointLocation) == false) {

                    // Add to fMap canvas at position
                    worldCanvas.addAt(tileShadow, pointLocation);

                    //
                    lastRotation = tileShadow.getRotation();

                    // Set as a copy of
                    tileShadow = new WorldTile(tileShadow.getTileset(), tileShadow.getIndexOfTileset());
                    tileShadow.setRotation(lastRotation);
                }
            }
        }
    }

    public void toggleCentered() {
        snapToCenter = !snapToCenter;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setMode(int mode) {

        // Grab and set the mode
        this.mode = mode;

        switch (mode) {
            case FLAG_MARKER:
                color = Color.GREEN;
                toolImage = markerImage;
                break;
            case FLAG_ERASER:
                color = Color.RED;
                toolImage = eraserImage;
                break;
            case FLAG_SELECT:
                color = Color.BLUE;
                toolImage = selectorImage;
                break;
        }
    }

    public void setTilePaint(Tileset tileset, int index) {

        // Make a new tile from that
        tileShadow = new WorldTile(tileset, index);
        tileShadow.setRotation(lastRotation);

        // Clear the actor drag shadow if exists
        clearShadow();
    }

    public void clearTilePaint() {
        tileShadow = null;
    }

    public void clearShadow() {
        animationShadow = null;
    }

    public boolean isVisible() {
        return visible;
    }

    public Point getLocation() {
        return pointLocation;
    }

    public int getMode() {
        return mode;
    }

    public WorldTile getTilePaint() {
        return tileShadow;
    }

    public Animation getShadow() {
        return animationShadow;
    }

    public void paint(Graphics monet, ImageObserver obs) {

        if (visible == true) {

            // Cast to 2D Graphics for stroke
            final Graphics2D manet = (Graphics2D) monet;

            // Adjust Graphics context
            manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

            // Draw the image by mode
            if (toolImage != null) {

                int newX = (int) (pointLocation.x + (gridSize.width / 2) - (toolImage.getWidth(obs) / 2));
                int newY = (int) (pointLocation.y + (gridSize.height / 2) - (toolImage.getHeight(obs) / 2));
                newX = toolImage.getWidth(obs) <= gridSize.width ? newX : pointLocation.x;
                newY = toolImage.getHeight(obs) <= gridSize.height ? newY : pointLocation.y;

                manet.drawImage(toolImage, newX, newY, obs);
            }

            // Attempt to draw the tile shadow
            if (tileShadow != null) {

                // Setup the initial Transformations
                final AffineTransform transformOriginal = manet.getTransform();
                final AffineTransform transformImage = new AffineTransform();

                //
                final BufferedImage image = tileShadow.getGraphic();

                //
                transformImage.setToTranslation(pointLocation.x, pointLocation.y);

                //
                transformImage.rotate(Math.toRadians(tileShadow.getRotation()), image.getWidth(obs) / 2, image.getHeight(obs) / 2);

                // Adjust the image alpha1
                manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .65f));

                // Draw the shadow of the image
                manet.drawImage(image, transformImage, obs);

                // Reset the image alpha to full opaque
                manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

                //
                manet.setTransform(transformOriginal);
            }

            // Attempt to draw the animation shadow
            if (animationShadow != null) {

                // Adjust the image alpha1
                manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .65f));

                //
                final BufferedImage image = animationShadow.getCurrentImage();

                // Draw the shadow of the image
                manet.drawImage(image, pointLocation.x, pointLocation.y, obs);

                // Reset the image alpha to full opaque
                manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }
}
