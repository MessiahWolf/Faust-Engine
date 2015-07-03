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

import core.world.Animation;
import core.world.WorldResource;
import core.world.Picture;
import core.world.WorldScript;
import io.resource.DataPackage;
import io.resource.ResourceReader;
import io.util.FileUtils;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class DataListRenderer extends JLabel implements ListCellRenderer {

    // Variable Declaration
    private ImageIcon iconAnimation;
    private ImageIcon iconFolder;
    private ImageIcon iconImage;
    private ImageIcon iconPackage;
    private ImageIcon iconScript;
    // End of Variable Declaration

    public DataListRenderer() {

        //
        final Class closs = getClass();

        //
        iconAnimation = ResourceReader.readClassPathIcon(closs,"/icons/icon-animation24.png");
        iconImage = ResourceReader.readClassPathIcon(closs,"/icons/icon-image24.png");
        iconScript = ResourceReader.readClassPathIcon(closs,"/icons/icon-script24.png");
        iconFolder = ResourceReader.readClassPathIcon(closs,"/icons/icon-folder24.png");

        //
        iconPackage = ResourceReader.readClassPathIcon(closs,"/icons/icon-package24.png");
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        // Our special color
        final Color color = new Color(51, 153, 255);

        //
        setOpaque(isSelected);
        setForeground(isSelected ? color : Color.BLACK);

        if (value instanceof WorldResource) {

            //
            final WorldResource resource = (WorldResource) value;

            //
            if (resource instanceof Animation) {

                //
                setIcon(iconAnimation);
                setText(resource.getDisplayName());
            } else if (resource instanceof Picture) {

                //
                setIcon(iconImage);
                setText(resource.getDisplayName());
            } else if (resource instanceof WorldScript) {

                //
                setIcon(iconScript);
                setText(resource.getDisplayName());
            }
        } else if (value instanceof File) {

            //
            final File file = (File) value;

            //
            if (file.isFile()) {

                //
                if (FileUtils.getExtension(file).equalsIgnoreCase("png")) {
                    
                    //
                    setIcon(iconImage);
                }
            } else {
                
                //
                setIcon(iconFolder);
            }

            //
            setText(file.getName());
        } else if (value instanceof DataPackage) {

            //
            setIcon(iconPackage);
            setText(((DataPackage) value).getDisplayName());
        } else {
            setText(value.toString());
        }

        //
        return this;
    }
}
