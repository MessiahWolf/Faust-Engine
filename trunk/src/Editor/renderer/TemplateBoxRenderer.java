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

import core.world.WorldTemplate;
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
public class TemplateBoxRenderer extends JLabel implements ListCellRenderer {

    // Variable Declaration
    private ImageIcon iconLab;
    // End of Variable Declaration

    public TemplateBoxRenderer() {

        //
        final Class closs = getClass();

        //
        iconLab = ResourceReader.readClassPathIcon(closs,"/icons/icon-lab24.png");
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        if (value != null) {

            if (value instanceof WorldTemplate) {
                
                // Cast value to Resource
                final WorldTemplate resource = (WorldTemplate) value;

                //
                setText(resource.getDisplayName());
            } else if (value instanceof String) {
                setText(String.valueOf(value));
            }
        }

        // Return this
        return this;
    }
}
