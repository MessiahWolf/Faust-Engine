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

import io.resource.ResourceProducer;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 *
 * @author Robert A. Cherry
 */
public class Tileset extends Illustration {

    // Variable Declaration
    // Java Native Classes
    public BufferedImage[] images;
    // Data Type
    public int length;
    // End of Variable Declaration

    public Tileset() {

        // Start with all nulls then
        this(null, null, null, null, null);
    }

    public Tileset(String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {

        // Call to super
        super(sha1CheckSum, packageID, referenceID, referenceName, displayName);
    }

    @Override
    public Tileset reproduce() {

        //
        final Tileset copy = new Tileset(sha1CheckSum, packageID, referenceID, referenceName, displayName);
        copy.blockRows = blockRows;
        copy.blockColumns = blockColumns;
        copy.blockHGap = blockHGap;
        copy.blockVGap = blockVGap;
        copy.blockWidth = blockWidth;
        copy.blockHeight = blockHeight;
        copy.blockXOffset = blockXOffset;
        copy.blockYOffset = blockYOffset;
        copy.picture = picture;

        //
        return copy;
    }

    @Override
    public void validate() {

        // Grab values from this and super class
        matchFieldValues(getClass().getSuperclass());
        matchFieldValues(getClass());

        //
        if (picture != null) {

            // Create the buffered images
            images = ResourceProducer.createImages(picture, attributes);

            // Save the lengh
            length = images.length;
        }
    }

    @Override
    public BufferedImage draw(ImageObserver obs, float alpha) {
        // Do nothing for TILESETS, but DRAW WORLDTILES.
        return null;
    }
}
