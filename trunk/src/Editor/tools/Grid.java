/**
    Copyright (c) 2013, Robert Cherry    
    
    All rights reserved.
  
    This file is part of the Faust Editor.

    The Faust Editor is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Faust Editor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with The Faust Editor.  If not, see <http://www.gnu.org/licenses/>.
*/
package Editor.tools;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

/**
 The purpose of this class is to draw, manage, store, and remove Cells from the
 active layer.

 @version 1.01
 @author Robert Cherry
 */
public class Grid {

    // Variable Declaration
    private Color attributeColor;
    // Project Classes
    private ArrayList<Rectangle> collection;
    // Data types
    private boolean attributeVisible;
    private int attributeWidth;
    private int attributeHeight;
    private int attributeRow;
    private int attributeColumn;

    public Grid(int newRow, int newColumn, int newWidth, int newHeight, Color newColor) {

        // Set values equal
        attributeRow = newRow;
        attributeColumn = newColumn;
        attributeWidth = newWidth;
        attributeHeight = newHeight;
        attributeColor = newColor;
        
        // Row across a stream and climb a mountain
        // Rows are horizontal
        // Columns are vertical

        // Initialize
        init();
    }

    private void init() {

        // Instantiation
        collection = new ArrayList<>();

        // Preset values
        attributeVisible = true;

        // Adjust
        validate();
    }

    public ArrayList<Rectangle> getCollection() {
        return collection;
    }

    public void setCellWidth(int newWidth) {
        attributeWidth = newWidth;
    }

    public void setCellHeight(int newHeight) {
        attributeHeight = newHeight;
    }

    public void setRowCount(int attributeRow) {
        this.attributeRow = attributeRow;
    }

    public void setColumnCount(int attributeColumn) {
        this.attributeColumn = attributeColumn;
    }
    
    public void setColor(Color color) {
        attributeColor = color;
    }

    public void validate() {

        // Reset the Cells.
        collection.clear();

        for (int row = 0; row < attributeRow; row++) {
            for (int column = 0; column < attributeColumn; column++) {
                Rectangle newRectangle = new Rectangle(row * attributeWidth, column * attributeHeight, attributeWidth, attributeHeight);
                collection.add(newRectangle);
            }
        }
    }

    public void setVisible(boolean newVisible) {
        attributeVisible = newVisible;
    }

    public void paint(Graphics monet, ImageObserver newImageObserver, float alpha) {
        if (attributeVisible) {
            Graphics2D manet = (Graphics2D) monet;
            manet.setColor(attributeColor);
            manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            manet.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 4f, new float[]{3f, 2f, 3f}, 3f));
            // Draw the actual Rectangle
            manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (Rectangle rectangleArea : collection) {
                manet.draw(rectangleArea);
            }
            manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    public boolean isVisible() {
        return attributeVisible;
    }

    public int getRowCount() {
        return attributeRow;
    }

    public int getColumnCount() {
        return attributeColumn;
    }

    public int getCellWidth() {
        return attributeWidth;
    }

    public int getCellHeight() {
        return attributeHeight;
    }

    public int getWidth() {
        return attributeWidth * attributeRow;
    }

    public int getHeight() {
        return attributeHeight * attributeColumn;
    }

    public Dimension getSize() {
        return new Dimension(attributeWidth, attributeHeight);
    }
    
    public Color getColor() {
        return attributeColor;
    }

}
