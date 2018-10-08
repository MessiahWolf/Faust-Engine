/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.world;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.Random;
import tracer.AlphaTracer;

/**
 *
 * @author rcher
 */
public class LightSource extends WorldObject {

    // Variable Declaration
    // Java Native Classes
    private ArrayList<Lightgon> lightList;
    private ArrayList<Shadowgon> shadowList;
    private ArrayList<Polygon> atmosphereList;
    private double[] brightness;
    // Project Classes
    // Data Types
    private boolean filled = true;
    protected int angle = 15;
    protected float amps;
    protected float volts;
    private int watts;
    private int lumens;
    private int ranges;
    private final int size = 16;
    // ENd of Variable Declaration

    public LightSource(int x, int y, int angle, float amps, float volts) {

        //
        super(null, null, null, null, null);

        //
        this.x = x;
        this.y = y;
        this.angle = angle;

        // Assign
        this.amps = amps;
        this.volts = volts;
    }

    public void init() {

        // Calculate watts
        watts = (int) (amps * volts);
        
        //
        test();
    }

    private void test() {

        //
        if (layer != null) {

            // The total area we need to cover with light.
            final int area = layer.getWidth() * layer.getHeight();
            
            ranges = (int) (area / 39.370);
            ranges = 3;

            System.out.println("Calculating ranges[" + ranges + "] for area: " + area);

            // Let's suppose our area is 10000 * less than the range from the source.
            brightness = new double[ranges];
            
            //
            for (int n = 0; n < ranges; n++) {

                // Calculate the distance.
                final int dist = (int) ((2 * Math.PI) * Math.pow(n + 1, 2));

                //
                brightness[n] = (double) (lumens / dist) / area;

                //
                System.out.println("Distance: " + dist);
                System.out.println("Range: " + n + " Brightness: " + brightness[n]);
            }
        }
    }

    public void increaseAngle(int inc) {
        if (angle + inc <= 0) {
            angle = 360 - Math.abs(inc);
        } else if (angle + inc > 360) {
            angle = Math.abs(inc);
        } else {
            angle += inc;
        }
    }

    public void reprojectLightSource() {

        //
        if (layer == null) {
            return;
        }

        //
        lightList = new ArrayList();
        shadowList = new ArrayList();

        // Calculate a new polygon from the room
        atmosphereList = getAtmosphereSpace(layer);

        //
        if (atmosphereList == null) {
            return;
        }

        //
        Polygon atmospherePolygon = null;

        // We'll project our lightsource on the polygon that contains it to save time.
        for (Polygon p : atmosphereList) {

            // Might need to sort this array in terms of distance from the source.
            if (p.contains(getBounds())) {

                // Assign and break out.
                atmospherePolygon = p;
                break;
            }
        }

        //
        if (atmospherePolygon != null) {
            // Skip variable controls how many polygon points we skip; pretty much always angle 
            // or angle - 1;
            int skip;

            // Gap controls how much each ray cast overlaps the other
            final int gap = 1;
            final int push = 2;

            // Drawing the dots in a circular fashion about the center of the rectangle.
            for (int n = 0; n < atmospherePolygon.npoints - (angle + 1); n++) {

                // Always reset just in case it was set to angle - 1;
                skip = angle;

                // Whether or not to continue to create the lightgon for this angle.
                boolean cont = true;

                // The center point stretched out in the direction
                double posx = atmospherePolygon.xpoints[n];
                double posy = atmospherePolygon.ypoints[n];
                double newx = posx;
                double newy = posy;

                // The difference between them
                final double xdif = newx - x;
                final double ydif = newy - y;

                // The distance from the edge of the polygon to the center of the light source
                final int dist = (int) Math.sqrt((int) Math.pow(xdif, 2) + (int) Math.pow(ydif, 2));
                final int steps = 7;

                // Step towards the center of the light source, but skip some to save some time
                for (int i = 0; i <= (dist / steps); i++) {

                    // Move towards the center
                    newx -= xdif / (dist / steps);
                    newy -= ydif / (dist / steps);

                    // Going over each tile and checking if the tile contains the point we're extending
                    // If it does then don't draw this line because it's colliding with another tile.
                    for (WorldObject obj : layer.filter(WorldTile.class)) {

                        //
                        final WorldTile tile = (WorldTile) obj;

                        // Don't continue if a tile collides with the lines we're drawing out.
                        if (tile.getPreciseBounds().contains(newx, newy)) {
                            cont = false;
                            i = dist / steps;
                            skip = angle > push ? angle - push : angle;
                            break;
                        }
                    }
                }

                // Correct skip if gap makes it less than 1
                skip = skip + gap <= 0 ? Math.abs(skip + gap) + 2 : skip;

                // Calculate the next lightgon.
                final double nx = atmospherePolygon.xpoints[n + (skip + gap)];
                final double ny = atmospherePolygon.ypoints[n + (skip + gap)];

                // Reset to angle each time.
                skip = angle;

                //
                final Polygon poly = new Polygon();
                poly.addPoint(x, y);
                poly.addPoint((int) posx, (int) posy);
                poly.addPoint((int) nx, (int) ny);

                //In the case ther
                if (cont == true) {

                    // Add to the list.
                    lightList.add(new Lightgon(this, poly, .3f));
                } else {
                    //shadowList.addObject(new Shadowgon(this, poly, .4f));
                }

                // Skip forward.
                n += skip;
            }

            // Avoid division by zero.
            if (angle > 0) {

                //
                final int rem = atmospherePolygon.npoints % angle;

                // Fix for the lightgon we miss at the very end in the case that atmospherePolygon.npoints / angle
                //  does not divide evenly.
                if (rem > 0) {

                    //
                    final int len = atmospherePolygon.npoints;
                    final Polygon poly = new Polygon();
                    poly.addPoint(x, y);
                    poly.addPoint((int) atmospherePolygon.xpoints[len - (rem + push)], (int) atmospherePolygon.ypoints[len - (rem + push)]);
                    poly.addPoint((int) atmospherePolygon.xpoints[0], (int) atmospherePolygon.ypoints[0]);

                    // Our new Lightgon
                    final Lightgon lightgon = new Lightgon(this, poly, .3f);

                    // Add to the list.
                    lightList.add(lightgon);
                }
            }

            //
            test();
        }
    }

    private ArrayList<Polygon> getAtmosphereSpace(RoomLayer layer) {

        //
        if (layer == null) {
            return null;
        }

        // Create an image space from room space.
        final BufferedImage output = new BufferedImage(layer.getWidth(), layer.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D monet = output.createGraphics();

        // Arraylist of tiles
        final ArrayList<WorldObject> objectList = layer.filter(WorldTile.class);

        // Project all the tiles into their own image with a transparent background.
        for (WorldObject obj : objectList) {

            //
            final WorldTile tile = (WorldTile) obj;

            // Setup the initial Transformations
            final AffineTransform transformOriginal = monet.getTransform();
            final AffineTransform transformImage = new AffineTransform();

            // Begin the Transformation
            transformImage.setToTranslation(tile.getX(), tile.getY());
            transformImage.rotate(Math.toRadians(tile.getRotation()), tile.getGraphic().getWidth() / 2, tile.getGraphic().getHeight() / 2);

            //
            monet.drawImage(tile.getGraphic(), transformImage, null);
            monet.setTransform(transformOriginal);
        }

        // Give the trace the original image of just tiles.
        final AlphaTracer tracer = new AlphaTracer(output);
        tracer.setPrecision(32);

        // Invert the original image.
        tracer.invertOriginalImage(Color.RED);
        tracer.flash();

        // Git rid of the image data.
        monet.dispose();

        // Return the find.
        return tracer.getPolygonList();
    }

    public void draw(Graphics monet, ImageObserver obs, Point pos, float alpha) {

        //
        final Graphics2D manet = (Graphics2D) monet;
        manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //
        draw(monet, obs, alpha);

        //
        if (shadowList != null) {

            //
            for (Shadowgon shadowgon : shadowList) {
                shadowgon.draw(manet, pos);
            }
        }

        //
        if (lightList != null) {

            // Render every lightgon.
            for (Lightgon lightgon : lightList) {

                //
                lightgon.draw(manet, pos);
            }
        }

        //
        manet.setColor(Color.BLUE);
        manet.fillOval(x - 1, y - 1, 3, 3);

        // Draw the brightness at range
        if (brightness != null) {

            // Use a consistent seed for the RNG
            final Random generator = new Random(0);

            //
            Shape previousShape = null;
            final int scale = (layer.getWidth() * layer.getHeight()) / 200;

            //
            for (int i = 0; i < brightness.length; i++) {

                // Define the distance.
                int dist = (int) (brightness[i] * scale);

                //
                // Randomly determine the next rectangle's color
                manet.setColor(new Color((int) (generator.nextFloat() * 255), (int) (generator.nextFloat() * 255), (int) (generator.nextFloat() * 255)));
                manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) .64f));

                //
                Shape currentShape = new Ellipse2D.Double(x - (dist / 2), y - (dist / 2), dist, dist);

                // Carving a circle out the center of another shape using Java's Shape class.
                if (previousShape != null) {

                    //
                    //dist = (int) brightness[i] * scale;
                    // Compare and subtract areas.
                    //Area currentArea = new Area(currentShape);
                    //Area previousArea = new Area(new Ellipse2D.Double(x - dist, y - dist, dist, dist));
                    // Subtract and leave the hole in the middle.
                    //currentArea.subtract(previousArea);
                    // Reassign.
                    //currentShape = currentArea;
                }

                // Depends on mouse position
                if (currentShape.contains(pos)) {

                    // Draw that instead of the ellipse.
                    manet.setStroke(new BasicStroke(2f));
                    manet.draw(currentShape);
                    manet.setStroke(new BasicStroke(1f));
                    manet.setColor(Color.WHITE);
                    manet.fill(currentShape);
                } else {
                    manet.setStroke(new BasicStroke(1f));
                }

                //
                manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                //
                //previousShape = new Ellipse2D.Double(x - (dist / 2), y - (dist / 2), dist, dist);
            }
        }

        // Draw the angle we're using
        manet.drawString(String.valueOf(angle), x - size / 2 + 2, y + size / 2);
        manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    @Override
    public void draw(Graphics monet, ImageObserver obs, float alpha) {

        //
        final Graphics2D manet = (Graphics2D) monet;

        // Draw the detected atmosphere
        if (atmosphereList != null) {
            manet.setColor(new Color(194, 218, 224));

            //
            for (Polygon p : atmosphereList) {
                manet.draw(p);
            }
        }

        //
        manet.setColor(Color.ORANGE);
        manet.fill(getBounds());

        //
        manet.setColor(Color.WHITE);
        manet.draw(getBounds());
    }

    @Override
    public void updateAttributes() {

        // Grab attributes from parent
        super.updateAttributes();

        // Addon my special fields
        attributeMap.put("angle", angle);
        attributeMap.put("amps", amps);
        attributeMap.put("volts", volts);
    }

    public boolean isFilled() {
        return filled;
    }

    public float getWattage() {
        return amps * volts;
    }

    public int getAngle() {
        return angle;
    }

    @Override
    public Rectangle2D.Float getBounds() {
        //
        return new Rectangle2D.Float(x - size / 2, y - size / 2, size, size);
    }

    @Override
    public Polygon getPreciseBounds() {
        return null;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public void setVolts(float volts) {
        this.volts = volts;
    }

    public void setAmps(float amps) {
        this.amps = amps;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    @Override
    public void setLayer(RoomLayer layer) {

        //
        this.layer = layer;

        // Reproject after room addObject event.
        reprojectLightSource();
    }

    @Override
    public WorldResource reproduce() {

        //
        final LightSource source = new LightSource(x, y, angle, amps, volts);
        source.setLayer(layer);
        source.init();

        //
        return source;
    }

    @Override
    public void validate() throws RasterFormatException {

        // Attributes from FTile
        //matchFieldValues(getClass());
        // Grab attributes from World Object
        //matchFieldValues(getClass().getSuperclass());
    }

    @Override
    public void receive(String referenceID, WorldResource resource) {
        // Light source should never recieve any resources; resources recieve light sources.
    }
}
