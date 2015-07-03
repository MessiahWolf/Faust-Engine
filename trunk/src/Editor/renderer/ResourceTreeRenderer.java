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

import core.world.Actor;
import core.world.Animation;
import core.world.Backdrop;
import core.world.World;
import core.world.WorldCell;
import core.world.WorldResource;
import core.world.Tileset;
import core.world.WorldItem;
import io.resource.DataPackage;
import core.world.WorldScript;
import io.resource.ResourceReader;
import java.awt.Color;
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
public class ResourceTreeRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {

    // Variable Declaration
    private ImageIcon iconActor;
    private ImageIcon iconAnimation;
    private ImageIcon iconItem;
    private ImageIcon iconFolder;
    private ImageIcon iconMap;
    private ImageIcon iconPackage;
    private ImageIcon iconBackdrop;
    private ImageIcon iconWorld;
    private ImageIcon iconScript;
    private ImageIcon iconTileset;
    // End of Variable Declaration

    public ResourceTreeRenderer() {

        // Grab static icons
        final Class closs = getClass();

        // Grab Icons from jar
        iconActor = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-actor24.png");
        iconItem = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-item24.png");
        iconAnimation = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-animation24.png");
        iconBackdrop = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-background24.png");
        iconMap = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-map24.png");
        iconPackage = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-package24.png");
        iconWorld = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-world24.png");
        iconTileset = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-tileset24.png");
        iconScript = ResourceReader.readClassPathIcon(closs,"/Editor/icons/icon-script24.png");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        // Super call
        final Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        // The Value
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        // Grab Current node
        final Object userObject = node.getUserObject();

        // Default foreground color for text
        setForeground(new Color(60, 60, 60));

        // Change color of text for archived resources to green-ish?
        if (userObject instanceof WorldResource) {

            // Cast
            final WorldResource resource = (WorldResource) userObject;

            // Ask
            if (resource.getPackageId() == null || resource.getPackageId().isEmpty()) {
                setForeground(new Color(60, 60, 60));
            } else {
                setForeground(new Color(65, 105, 255));
            }

            // Check
            if (userObject instanceof Actor) {

                // Cast
                final Actor actor = (Actor) userObject;

                //
                setText(actor.getDisplayName());
                setIcon(iconActor);

                // Give a hint if hovered
                setToolTipText("Drag and drop onto active map");
            } else if (userObject instanceof WorldItem) {

                // Cast
                final WorldItem item = (WorldItem) userObject;

                //
                setText(item.getDisplayName());
                setIcon(iconItem);

                //
                setToolTipText("Drag and drop onto active map.");
            } else if (userObject instanceof Tileset) {

                // Cast
                final Tileset nodeValue = (Tileset) userObject;

                // 
                setText(nodeValue.getDisplayName());
                setIcon(iconTileset);

                // Give a hint if hovered
                setToolTipText("Double Click to view Tile Selector");
            } else if (userObject instanceof Animation) {

                // Cast
                Animation nodeValue = (Animation) userObject;
                setText(nodeValue.getDisplayName());
                setIcon(iconAnimation);
            } else if (userObject instanceof Backdrop) {

                // Cast
                final Backdrop background = (Backdrop) userObject;

                //
                setText(background.getDisplayName());
                setIcon(iconBackdrop);
            } else if (userObject instanceof WorldCell) {

                // Cast
                final WorldCell fMap = (WorldCell) userObject;

                //
                setText(fMap.getDisplayName());
                setIcon(iconMap);
            } else if (userObject instanceof World) {

                // Cast
                final World fworld = (World) userObject;
                setText(fworld.getDisplayName());

                //
                setIcon(iconWorld);
            } else if (userObject instanceof WorldScript) {

                //
                final WorldScript script = (WorldScript) userObject;
                setText(script.getDisplayName());

                //
                setIcon(iconScript);
            }
        } else if (userObject instanceof DataPackage) {

            // Automatically for resource plugins
            setForeground(new Color(65, 105, 255));

            // Cast
            final DataPackage dataPackage = (DataPackage) userObject;

            //
            setText(dataPackage.getDisplayName());

            // Set as a dataPackage
            setIcon(iconPackage);
        }

        // Return the component
        return component;
    }
}