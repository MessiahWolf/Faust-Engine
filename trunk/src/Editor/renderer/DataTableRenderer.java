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

import core.world.WorldResource;
import io.resource.DataRef;
import io.util.FileUtils;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author robert
 */
public class DataTableRenderer extends JLabel implements TableCellRenderer {

    public DataTableRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (isSelected) {
            setForeground(new Color(65, 105, 255));
            //super.setBackground(table.getSelectionBackground());
            //setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        //
        if (value instanceof  File) {

            // Cast to a file
           final File file = (File) value;

            // Change the text
            setText(FileUtils.contract(file));
        } else if (value instanceof DataRef) {
            
            // Cast to a data reference
            final DataRef reference = (DataRef) value;
            
            // Change the text
            setText(reference.getDisplayName());
        } else if (value instanceof WorldResource) {
            
            // Cast to a world resource
            final WorldResource resource = (WorldResource) value;
            
            // Display Name
            setText(resource.getDisplayName());
        }

        return this;
    }
}
