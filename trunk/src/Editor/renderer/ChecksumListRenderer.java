/**
 * Copyright (c) 2013, Robert Cherry * All rights reserved.
 *
 * This file is part of the Faust Editor.
 *
 * The Faust Editor is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * The Faust Editor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * The Faust Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package Editor.renderer;

import io.resource.ResourceReader;
import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class ChecksumListRenderer extends JLabel implements ListCellRenderer {

    // Variable Declaration
    private ImageIcon iconMD5;
    private ImageIcon iconSHA1;
    // End of Variable Declaration

    public ChecksumListRenderer() {

        //
        final Class closs = getClass();

        //
        iconMD5 = ResourceReader.readClassPathIcon(closs, "/icons/icon-md30x9.png");
        iconSHA1 = ResourceReader.readClassPathIcon(closs, "/icons/icon-sha30x9.png");
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        // Our special color
        final Color color = new Color(51, 153, 255);

        //
        setOpaque(isSelected);
        setForeground(isSelected ? color : Color.BLACK);

        //
        if (value instanceof String) {

            //
            final String string = String.valueOf(value).toUpperCase();

            //
            if (string.equals("MD5")) {

                //
                setIcon(iconMD5);
            } else if (string.equals("SHA-1")) {
                
                //
                setIcon(iconSHA1);
            }
            
            //
            setText(string);
        }

        //
        return this;
    }
}
