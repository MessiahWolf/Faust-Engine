/**
 * Copyright (c) 2013, Robert Cherry * All rights reserved.
 *
 * This file is part of the Faust Engine.
 *
 * The Faust Engine is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * The Faust Engine is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * the Faust Engine. If not, see <http://www.gnu.org/licenses/>.
 */
package core.world;

import core.event.ZoneEvent;
import core.event.ZoneListener;
import io.resource.ResourceReader;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Robert A. Cherry
 */
public class Zone {

    // Variable Declaration
    private EventListenerList listeners;
    private Image image;
    private Point position;
    private Rectangle bounds;
    // Project Classes
    private WorldObject host = null;
    // Data Types
    private boolean dynamic = false;
    private boolean entered = false;
    // End of Variable Declaration

    public Zone(Point position) {

        // Place at position
        this.position = position;

        // Inst. Listeners and Lists
        listeners = new EventListenerList();

        //
        final Class closs = getClass();

        // The image
        image = ResourceReader.readClassPathIcon(closs,"/stock/stock-marker32.png").getImage();

        //
        bounds = new Rectangle(position.x, position.y, image.getWidth(null), image.getHeight(null));
    }

    public void move(Point position) {
        this.position.setLocation(position);
        bounds.setLocation(position);
    }

    public void draw(Graphics monet, ImageObserver obs) {

        // If the zone is dynamic
        if (image != null) {

            if (entered) {
                
                //Background color is yellow
                monet.setColor(Color.YELLOW);

                // Fill the rectagnle
                monet.fillRect(bounds.x + 1, bounds.y + 1, bounds.width-1, bounds.height-1);
            }

            // Draw the image
            monet.drawImage(image, position.x, position.y, obs);
        }
    }

    public void setHost(WorldObject host) {
        this.host = host;

        // Add listener to this class for the world object, or give a reference of this zone to the world object to update position on world object move event.
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public void setEntered(boolean entered) {
        this.entered = entered;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public boolean isEntered() {
        return entered;
    }

    public boolean isMasking() {
        return host != null;
    }

    public WorldObject getHost() {
        return host;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void addZoneListener(ZoneListener listener) {
        listeners.add(ZoneListener.class, listener);
    }

    public void removeZoneListener(ZoneListener listener) {
        listeners.remove(ZoneListener.class, listener);
    }

    public void fireEvent(int action, Object source) {

        // Create a copy of the Event Listeners
        final Object[] copy = Arrays.copyOf(listeners.getListenerList(), listeners.getListenerList().length);

        // Derive an event from the object
        final ZoneEvent event = new ZoneEvent(source);

        // Every other object is the actual class
        for (int i = 0; i < copy.length; i += 2) {

            // Grab the listener
            final ZoneListener listener = (ZoneListener) copy[i + 1];

            // Which event was triggered?
            switch (action) {
                case ZoneEvent.ENTERED:
                    listener.zoneEntered(event);
                    break;
                case ZoneEvent.EXITED:
                    listener.zoneExited(event);
                    break;
                case ZoneEvent.MOVED:
                    listener.zoneMoved(event);
                    break;
                case ZoneEvent.RESHAPED:
                    listener.zoneReshaped(event);
                    break;
            }
        }
    }
}
