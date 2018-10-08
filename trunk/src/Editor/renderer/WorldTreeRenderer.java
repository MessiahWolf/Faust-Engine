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

import core.world.Actor;
import core.world.Backdrop;
import core.world.RoomLayer;
import core.world.Room;
import core.world.WorldTile;
import io.resource.ResourceReader;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Robert A. Cherry
 */
public class WorldTreeRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {

    // Variable Declaration
    private ImageIcon iconActor;
    private ImageIcon iconBackground;
    private ImageIcon iconLayer;
    private ImageIcon iconMap;
    private ImageIcon iconTile;
    private ImageIcon iconWorld;
    // End of Variable Declaration

    public WorldTreeRenderer() {

        // Initialize
        init();
    }

    private void init() {

        final Class closs = getClass();

        // Grab Icons from Classpath
        iconWorld = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-world24.png");
        iconLayer = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-layer24.png");
        iconMap = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-map24.png");
        iconActor = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-actor24.png");
        iconTile = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-tile24.png");
        iconBackground = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-background24.png");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        // Super call
        final Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        // The Value
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        final Object object = node.getUserObject();

        // Paint Icon based on userObject type
        if (object instanceof Room) {
            setIcon(iconMap);
            setText(((Room) object).getDisplayName());
        } else if (object instanceof RoomLayer) {
            setIcon(iconLayer);
            setText(((RoomLayer) object).getDisplayName());
        } else if (object instanceof Actor) {
            setIcon(iconActor);
            setText(((Actor) object).getDisplayName());
        } else if (object instanceof WorldTile) {

            // Momentary cast
            final WorldTile tile = (WorldTile) object;
            setIcon(iconTile);
            setText(tile.getTileset().getDisplayName() + ": " + tile.getIndexOfTileset()
             + "[" + tile.getX() + ", " + tile.getY() + "]");
        } else if (object instanceof Backdrop) {
            setIcon(iconBackground);
            setText(((Backdrop) object).getDisplayName());
        }

        // Return the component
        return component;
    }
}