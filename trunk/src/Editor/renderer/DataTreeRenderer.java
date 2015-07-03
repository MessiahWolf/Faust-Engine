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

import io.resource.ResourceReader;
import io.util.FileUtils;
import java.awt.Component;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class DataTreeRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {

    // Variable Declaration
    private ImageIcon iconFolder;
    private ImageIcon iconPackage;
    private ImageIcon iconImage;
    private ImageIcon iconXml;
    private ImageIcon iconUnknown;
    // End of Variable Declaration

    public DataTreeRenderer() {

        // Initialize
        init();
    }

    private void init() {

        final Class closs = getClass();
        
        //
        iconPackage = ResourceReader.readClassPathIcon(closs,"/icons/icon-package24.png");
        iconImage = ResourceReader.readClassPathIcon(closs,"/icons/icon-image24.png");
        iconXml = ResourceReader.readClassPathIcon(closs,"/icons/icon-xml24.png");
        iconUnknown = ResourceReader.readClassPathIcon(closs,"/icons/icon-unknown24.png");
        iconFolder = ResourceReader.readClassPathIcon(closs,"/icons/icon-folder24.png");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        // Super call
        final Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        // The Value
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        final Object nodeValue = node.getUserObject();

        // Paint Icon based on userObject type
        if (nodeValue instanceof File) {

            // Capture
            final File file = (File) nodeValue;

            // Switch
            if (node.isRoot()) {
                setIcon(iconPackage);
            } else if (FileUtils.getExtension(file).equalsIgnoreCase("png")) {
                setIcon(iconImage);
            } else if (FileUtils.getExtension(file).equalsIgnoreCase("xml")) {
                setIcon(iconXml);
            } else if (file.isDirectory()) {
                setIcon(iconFolder);
            } else {
                setIcon(iconUnknown);
            }

            // Change the text
            setText(file.getName());
            //setText(file.getAbsolutePath());
        } else if (nodeValue == null) {
            
            //
            setText("Create a Root File");
        }

        // Return the component
        return component;
    }
}