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
package core.world;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * The purpose of this class is to stretch or tile an image across a map
 *
 * @version 1.30
 * @author Robert Cherry
 */
public class Backdrop extends Illustration {

    // Variable Declaration
    // Java Native Classes
    private Color color;
    // Project Classes
    private Room map;
    // Data Types
    protected boolean stretch = false;
    // End of Variable Declaration

    public Backdrop() {

        // Start with all nulls then
        this(null, null, null, null, null);
    }

    public Backdrop(String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {

        // Call to super
        super(sha1CheckSum, packageID, referenceID, referenceName, displayName);

        // Default options
        blockRows = 1;
        blockColumns = 1;
    }

    @Override
    public Backdrop reproduce() {

        //
        final Backdrop copy = new Backdrop(sha1CheckSum, packageID, referenceID, referenceName, displayName);
        copy.stretch = stretch;
        copy.blockRows = blockRows;
        copy.blockColumns = blockColumns;
        copy.blockHGap = blockHGap;
        copy.blockVGap = blockVGap;
        copy.blockWidth = blockWidth;
        copy.blockHeight = blockHeight;
        copy.blockXOffset = blockXOffset;
        copy.blockYOffset = blockYOffset;
        copy.color = color;
        copy.picture = picture;

        //
        return copy;
    }

    @Override
    public BufferedImage draw(ImageObserver obs, float alpha) {
        
        //
        if (visible == false) {
            return null;
        }

        //
        final int totalWidth = blockXOffset + (blockRows * (blockWidth + blockHGap));
        final int totalHeight = blockYOffset + (blockColumns * (blockHeight + blockVGap));
        
        // Kick out if no image
        if (totalWidth <= 0 || totalHeight <= 0) {
            return null;
        }

        //
        final BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);

        // Casting to Graphics2D Object for method setComposite(AlphaComposite ac);
        final Graphics2D manet = (Graphics2D) image.createGraphics();

        // Set the Alpha to that of the Layer
        manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        //
        if (picture != null) {

            // Begin drawing the attributeRows and attributeColumns;
            if (picture.getImage() != null) {
                
                // Match block rows and columns
                for (int i = 0; i < blockRows; i++) {
                    for (int j = 0; j < blockColumns; j++) {
                        
                        // Draw the image adjusted to offsets
                        manet.drawImage(picture.getImage(), blockXOffset + (i * (blockWidth + blockHGap)), blockYOffset + (j * (blockHeight + blockVGap)), obs);
                    }
                }
            }
        }

        // Return the Alpha Composite to 1.0f (Fully Visible);
        manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        manet.dispose();

        // !WIP
        return image;
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();

        attributes.put("stretch", stretch);
        
        // Special for backdrops
        if (stretch) {
            //blockRows = 1;
            //blockColumns = 1;
        }
    }

    @Override
    public void validate() {
        // Do nothing on validate, yet.
    }

    // Accessors
    public boolean isStretching() {
        return stretch;
    }

    public Color getBackgroundColor() {
        return color;
    }

    public Room getMap() {
        return map;
    }

    // Mutators
    public void setBackgroundColor(Color color) {
        this.color = color;
    }

    public void setStretching(boolean stretch) {
        this.stretch = stretch;
        
        // Special for backdrops
        if (stretch) {
            blockRows = 1;
            blockColumns = 1;
        }
    }

    public void setMap(Room map) {
        this.map = map;
        
        // This won't really matter.
        if (stretch) {
            blockRows = map.getWidth() / picture.getWidth() + 1;
            blockColumns = map.getHeight() / picture.getHeight() + 1;
        }
    }
}
