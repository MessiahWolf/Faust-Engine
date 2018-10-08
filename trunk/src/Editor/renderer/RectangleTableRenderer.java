/**
 * Copyright (c) 2013, Robert Cherry  *
 * All rights reserved.
 *
 * This file is part of the Faust Editor.
 *
 * The Faust Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Faust Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The Faust Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package Editor.renderer;

import core.world.WorldResource;
import io.resource.DataRef;
import io.util.FileUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author robert
 */
public class RectangleTableRenderer extends JLabel implements TableCellRenderer {

    public RectangleTableRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof String) {

            //
            final Color color = new Color(Integer.parseInt((String) value));

            // Add a space then display the rectangles dimenions my way
            BufferedImage image = new BufferedImage(20, 7, BufferedImage.TYPE_INT_ARGB);
            Graphics monet = image.createGraphics();
            monet.setColor(color);
            monet.fillRect(2, 0, 20, 7);
            monet.setColor(Color.BLACK);
            monet.drawRect(2, 0, 20, 7);
            monet.dispose();
            setIcon(new ImageIcon(image));
        }

        //
        return this;
    }
}
