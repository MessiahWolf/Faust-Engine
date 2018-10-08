/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.renderer;

import java.awt.Color;
import java.awt.Rectangle;

/**
 *
 * @author rcher
 */
public class SnapRectangle extends Rectangle {
    
    // Variable Declaration
    // Java Native Classes
    private final Color color;
    // End of Variable Declaration
    
    public SnapRectangle(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        this.color = color;
    }
    
    public Color getColor() {
        return color;
    }
}
