/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author rcher
 */
public class RectangleListRenderer extends JLabel implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        setOpaque(isSelected);

        //
        if (value instanceof SnapRectangle) {

            //
            final SnapRectangle rect = (SnapRectangle) value;

            // Add a space then display the rectangles dimenions my way
            setText(" [" + rect.x + ", " + rect.y + ", " + rect.width + ", " + rect.height + "]");
            BufferedImage image = new BufferedImage(20, 7, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.createGraphics();
            g.setColor(rect.getColor());
            g.fillRect(2, 0, 20, 7);
            g.setColor(Color.BLACK);
            g.drawRect(2, 0, 20, 7);
            g.dispose();
            setIcon(new ImageIcon(image));
        }

        //
        return this;
    }

}
