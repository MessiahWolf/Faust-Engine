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

import core.world.World;
import io.resource.ResourceDelegate;
import io.resource.ResourceReader;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class WorldBoxRenderer extends JLabel implements ListCellRenderer {

    // Variable Declaration
    private ImageIcon iconWorld;
    // End of Variable Declaration

    public WorldBoxRenderer() {

        //
        final Class closs = getClass();

        //
        iconWorld = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-world24.png");
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        if (value != null) {

            //
            if (value instanceof World) {
                
                // Cast value to Resource
                final World resource = (World) value;

                //
                setText(resource.getDisplayName());
            } else if (String.valueOf(value).equalsIgnoreCase(ResourceDelegate.UNPACKAGED_STATEMENT)) {
                setText(ResourceDelegate.UNPACKAGED_STATEMENT);
            }
        }

        // Return this
        return this;
    }
}
