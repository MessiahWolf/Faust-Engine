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

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author robert
 */
public class DataTableCheckBoxRenderer extends JCheckBox implements TableCellRenderer {

    public DataTableCheckBoxRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (isSelected == false) {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // Parse boolean from string
        if (value instanceof String) {

            try {
                value = Boolean.parseBoolean((String) value);
            } catch (NumberFormatException nfe) {
                value = false;
            }

            // Change selected
            setSelected((Boolean) value);
        } else if (value instanceof Boolean) {
            setSelected((Boolean) value);
        }

        // Return this
        return this;
    }
}
