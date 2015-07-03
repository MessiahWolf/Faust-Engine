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
package Editor.renderer;

import core.world.WorldCell;
import core.world.World;
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
public class WorldListRenderer extends JLabel implements ListCellRenderer {

    // Variable Declaration
    private ImageIcon iconMap;
    private ImageIcon iconWorld;
    // End of Variable Declaration

    public WorldListRenderer() {

        //
        final Class closs = getClass();

        //
        iconWorld = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-world24.png");
        iconMap = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-map24.png");
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        // Our special color
        final Color color = new Color(51, 153, 255);
        
        //
        setOpaque(isSelected);
        setForeground(isSelected ? color : Color.BLACK);

        // Ask
        if (value instanceof World) {

            // Cast to a FWorld
            final World world = (World) value;

            // Set values
            setIcon(iconWorld);
            setText(world.getDisplayName());
        } else if (value instanceof WorldCell) {

            // Cast to a FMap
            final WorldCell map = (WorldCell) value;
            
            // Set values
            setIcon(iconMap);
            setText(map.getDisplayName());
        }

        // Return it
        return this;
    }
}
