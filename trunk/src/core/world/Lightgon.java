/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.world;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

/**
 *
 * @author rcher
 */
public class Lightgon {

    // Variable Declaration
    // Project Classes
    public final LightSource source;
    // Java Native Classes
    private final Polygon polygon;
    // Data Types
    private float alpha;
    private Color COLOR1 = Color.WHITE;
    private Color COLOR2 = Color.ORANGE;
    // End of Variable Declaration

    public Lightgon(LightSource source, Polygon polygon, float alpha) {

        //
        this.source = source;
        this.polygon = polygon;
        this.alpha = alpha;
    }

    public boolean isInside(int x, int y) {

        // Base Null Case
        if (polygon == null) {
            return false;
        }

        // Otherwise
        return polygon.contains(x, y);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
    
    public void setColorWave(Color c1, Color c2) {
        COLOR1 = c1;
        COLOR2 = c2;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void draw(Graphics2D manet, Point pos) {

        //
        manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        //
        manet.setStroke(new BasicStroke(2f));
        manet.setPaint(new GradientPaint(0, 0, COLOR1, 400, 0, COLOR2));
        
        //
        if (polygon.contains(pos) || source.isFilled()) {
            manet.fill(polygon);
        } else {
            manet.draw(polygon);
        }
        
        //
        manet.setStroke(new BasicStroke(1f));
        manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
