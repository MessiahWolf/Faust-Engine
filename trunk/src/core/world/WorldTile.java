/**
 * Copyright (c) 2013, Robert Cherry * All rights reserved.
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import tracer.AlphaTracer;

public class WorldTile extends WorldObject {

    // Variable Declaration
    // Java Native Classes
    private BufferedImage graphic;
    // Project Classes
    private Tileset tileset;
    // Data Types
    // Integer Declaration
    protected int index;
    // End of Variable Declaration

    public WorldTile(Tileset tileset, int index) {

        // Call to super
        super(null, tileset.packageID, tileset.referenceID, tileset.referenceName, tileset.displayName);

        // Set my graphic
        graphic = tileset.images[index];

        if (graphic != null) {

            //
            final BufferedImage bi = new BufferedImage(graphic.getWidth(), graphic.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // Cast to 2D Graphics
            final Graphics2D manet = (Graphics2D) bi.createGraphics();

            // Setup the initial Transformations
            final AffineTransform transformOriginal = manet.getTransform();
            final AffineTransform transformImage = new AffineTransform();

            // Begin the Transformation
            //transformImage.setToTranslation(x, y);
            transformImage.rotate(Math.toRadians(rotation), graphic.getWidth() / 2, graphic.getHeight() / 2);

            // Draw the image
            manet.drawImage(graphic, transformImage, null);

            //
            manet.setTransform(transformOriginal);
            manet.dispose();

            //
            final AlphaTracer tracer = new AlphaTracer(bi);
            tracer.setPrecision(32);
            tracer.flash();

            //
            polygon = tracer.getPolygonList().get(0);
            polygon.translate(x, y);
        }

        // Custom options
        this.tileset = tileset;
        this.index = index;
    }

    public WorldTile(int index) {

        //
        super(null, null, null, null, null);

        //
        this.index = index;
    }

    @Override
    public WorldTile reproduce() {

        // Just a simple copy
        final WorldTile copy = new WorldTile(tileset, index);
        copy.rotation = rotation;
        copy.x = x;
        copy.y = y;

        // Copy attributes
        copy.setAttributeMap(attributeMap);

        //
        copy.validate();

        // Return copy
        return copy;
    }

    public void rotateClockwise(float change) {
        if ((rotation - change) < 0) {
            rotation -= change - 360f;
        } else {
            rotation -= change;
        }
    }

    public void rotateCounterClockwise(float change) {
        if ((rotation + change) > 360) {
            rotation = change - 360f;
        } else {
            rotation += change;
        }
    }

    public void setGraphic(BufferedImage graphic) {
        this.graphic = graphic;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public String getReferenceID() {
        return referenceID;
    }

    public float getRotation() {
        return rotation;
    }

    public Tileset getTileset() {
        return tileset;
    }

    public int getIndexOfTileset() {
        return index;
    }

    public BufferedImage getGraphic() {
        return graphic;
    }

    @Override
    public Rectangle2D.Float getBounds() {

        // Graphic must exist for bounds
        if (graphic != null) {

            // Outline the shape
            return new Rectangle2D.Float(x, y, graphic.getWidth(), graphic.getHeight());
        }

        // No graphic, no bounds
        return null;
    }

    @Override
    public Polygon getPreciseBounds() {
        return polygon;
    }

    @Override
    public void draw(Graphics monet, ImageObserver obs, float alpha) {

        //
        if (visible == false) {
            return;
        }

        // Draw the image at location
        if (graphic != null) {

            // Cast to 2D Graphics
            final Graphics2D manet = (Graphics2D) monet;

            // Setup the initial Transformations
            final AffineTransform transformOriginal = manet.getTransform();
            final AffineTransform transformImage = new AffineTransform();

            // Begin the Transformation
            transformImage.setToTranslation(x, y);
            transformImage.rotate(Math.toRadians(rotation), graphic.getWidth(obs) / 2, graphic.getHeight(obs) / 2);

            // Draw the image
            manet.drawImage(graphic, transformImage, obs);

            // Border check
            if (border) {

                // Stroke of four width
                manet.setStroke(new BasicStroke(1.5f));

                // Change the color
                manet.setColor(new Color(65, 105, 255));

                // Fill the entire bounds
                manet.draw(getPreciseBounds());
                
                //
                manet.setStroke(new BasicStroke());
            }

            //
            manet.setTransform(transformOriginal);
        }
    }

    @Override
    public void receive(String referenceID, WorldResource resource) {

        // Grab the resources' class
        final Class closs = resource.getClass();

        // Recieves a graphicset for a moment
        if (requestMap.containsKey(referenceID)) {

            //
            if (closs == Tileset.class) {

                // Cast
                tileset = (Tileset) resource;

                //  Grab the graphic using the index given
                graphic = tileset.images[index];

                if (graphic != null) {

                    //
                    final BufferedImage bi = new BufferedImage(graphic.getWidth(), graphic.getHeight(), BufferedImage.TYPE_INT_ARGB);

                    // Cast to 2D Graphics
                    final Graphics2D manet = (Graphics2D) bi.createGraphics();

                    // Setup the initial Transformations
                    final AffineTransform transformOriginal = manet.getTransform();
                    final AffineTransform transformImage = new AffineTransform();

                    // Begin the Transformation
                    //transformImage.setToTranslation(x, y);
                    transformImage.rotate(Math.toRadians(rotation), graphic.getWidth() / 2, graphic.getHeight() / 2);

                    // Draw the image
                    manet.drawImage(graphic, transformImage, null);

                    //
                    manet.setTransform(transformOriginal);
                    manet.dispose();

                    //
                    final AlphaTracer tracer = new AlphaTracer(bi);
                    tracer.setPrecision(32);
                    tracer.flash();

                    //
                    polygon = tracer.getPolygonList().get(0);
                    polygon.translate(x, y);
                }

                // No longer needed
                requestMap.remove(referenceID);
            }
        }
    }

    @Override
    public void updateAttributes() {

        // Call to super
        super.updateAttributes();

        // Custom stuff...
        attributeMap.put("index", index);
        attributeMap.put("rotation", rotation);
    }

    @Override
    public void validate() {

        // Attributes from FTile
        matchFieldValues(getClass());

        // Grab attributes from World Object
        matchFieldValues(getClass().getSuperclass());
    }
}
