/**
 * Copyright (c) 2013, Robert Cherry  *
 * All rights reserved.
 *
 * This file is part of the Faust Engine.
 *
 * The Faust Engine is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * The Faust Engine is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * the Faust Engine. If not, see <http://www.gnu.org/licenses/>.
 */
package core.world;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

/**
 * The purpose of this class is to show action being done upon an item, matter,
 * or life.
 *
 * @version 1.01
 * @author Robert Cherry
 */
public class WorldEffect extends WorldObject {

    public WorldEffect(String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {
        // Call to super
        super(sha1CheckSum, packageID, referenceID, referenceName, displayName);
    }

    @Override
    public WorldEffect reproduce() {
        return null;
    }

    @Override
    public void updateAttributes() {

        // Update the attribute map
        super.updateAttributes();
    }

    @Override
    protected void draw(Graphics monet, ImageObserver obs, float alpha) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void validate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void receive(String newId, WorldResource newResource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override public Rectangle2D.Float getBounds() {
        return null;
    }
    
    @Override public Polygon getPreciseBounds() {
        return null;
    }
}
