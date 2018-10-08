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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author robert
 */
public class DefaultCheckBoxRenderer implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean hasFocus) {

        //
        try {

            // Our panel and its contents.
            final JPanel panel = (JPanel) value;
            final JCheckBox box = (JCheckBox) panel.getComponent(0);
            final JLabel label = (JLabel) panel.getComponent(2);

            // Disabling the parent index.
            if (label.getText().contains("<Parent>")) {
                box.setEnabled(false);
            } else {
                box.setEnabled(true);
            }
            
            // Alternating the color whether selected.
            panel.setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
            panel.setForeground(isSelected ? Color.WHITE : Color.BLACK);
            
            //
            return panel;
        } catch (ClassCastException cce) {
            // Nothing for now.
        }

        // Return this
        return null;
    }
}
