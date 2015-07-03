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

import core.event.ActorEvent;
import core.event.ActorListener;
import core.event.LayerEvent;
import core.event.LayerListener;
import core.event.WorldObjectEvent;
import core.event.WorldObjectListener;
import core.world.item.Weapon;
import io.resource.ResourceRequest;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.EventListenerList;

/**
 * The purpose of this class is to hold instances of various types of world
 * objects to be rendered at different iterations of the room paint loop
 *
 * @version 3.0
 * @author Robert A. Cherry (Messiah Wolf)
 */
public class WorldCellLayer implements WorldResource, ActorListener, WorldObjectListener {

    // Variable Declaration
    // Java Native Classes
    private ArrayList<WorldObject> objectList;
    protected EventListenerList listenerList;
    private HashMap<String, ResourceRequest> requestMap;
    private HashMap<String, Object> attributeMap;
    // Project Classes
    private WorldCell worldCell;
    private World world;
    // Data types
    private boolean visible;
    private float alpha;
    private int index;
    private int width;
    private int height;
    private String referenceName;
    private String referenceID;
    private String packageID;
    private String displayName;
    // End of Variable Declaration

    public WorldCellLayer(WorldCell worldCell, String displayName) {

        // Set values equal
        this.displayName = displayName;
        this.worldCell = worldCell;

        // Set values from the map
        referenceID = worldCell.getReferenceID();
        referenceName = worldCell.getReferenceName();
        packageID = worldCell.getPackageId();

        // Initially match the dimensions of the map
        width = worldCell.getWidth();
        height = worldCell.getHeight();

        // Preset values
        alpha = 1.0f;
        visible = true;

        // Event Logger
        objectList = new ArrayList<>();
        requestMap = new HashMap<>();
        attributeMap = new HashMap<>();
        listenerList = new EventListenerList();
    }
    
    @Override
    public WorldCellLayer reproduce() {

        // Our output copy
        final WorldCellLayer copy = new WorldCellLayer(worldCell, displayName);

        // Iterate over all objects
        for (int i = 0; i < objectList.size(); i++) {

            // Grab current world object copy
            final WorldObject object = (WorldObject) objectList.get(i).reproduce();

            // Add a reproduction
            copy.add(object);
        }

        // Return the copy
        return copy;
    }

    @Override
    public void updateAttributes() {

        // Setup the Property Map
        attributeMap.put("displayName", displayName);
        attributeMap.put("index", index);
        attributeMap.put("alpha", alpha);
    }

    @Override
    public void validate() {
        // Do nothing
    }

    /* *
     * This class should only recieve World Objects and Tilesets.
     */
    @Override
    public void shadow(String referenceID, ResourceRequest request) {

        // Add to the pending resource list
        requestMap.put(referenceID, request);
    }

    @Override
    public void receive(String referenceID, WorldResource resource) {

        // Depending on the type of resource
        final Class closs = resource.getClass();
        
        //
        System.out.println("Recieved: " + resource.getDisplayName());

        // Only get what I ask for
        if (requestMap.containsKey(referenceID)) {

            // Is this resource an assignable world object
            if (WorldObject.class.isAssignableFrom(closs)) {

                // Cast to a world object
                final WorldObject worldObject = (WorldObject) resource.reproduce();

                // Tile or other world item (it matters)
                if (worldObject instanceof Weapon) {

                    // Then it should have been sent with a hashmap via the createReferenceRequest method in the ResourceDelegate.class
                    final HashMap<String, Object> options = requestMap.get(referenceID).getPreloadOptions();

                    // Give to world object
                    worldObject.setAttributeMap(options);
                    worldObject.validate();
                }

                // Add the content to this layer
                add(worldObject);

                // Remove from the request list
                requestMap.remove(referenceID);
            }
        }
    }

    public void sort() {

        // Create a way to compare the world objects
        final Comparator<WorldObject> comparator = new Comparator<WorldObject>() {
            @Override
            public int compare(WorldObject current, WorldObject other) {

                // Grab the depths from the two
                int currentDepth = (int) current.getDepth();
                int otherDepth = (int) other.getDepth();

                // Reorder
                if (currentDepth < otherDepth) {
                    return 1;
                } else if (currentDepth == otherDepth) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };

        // Actually sort it here
        if (objectList.isEmpty() == false) {
            Collections.sort(objectList, comparator);
        }

        // Notify to the map that a sort has occured and a repaint is nessecary
        fireStateChanged(objectList, LayerEvent.SORTED);
    }

    public void paint(Graphics monet, ImageObserver observer) {

        // Layers can be set invisible even in game
        if (visible) {

            // Render all the objects
            for (int i = 0; i < objectList.size(); i++) {

                // fMap Object
                final WorldObject worldObject = objectList.get(i);

                // Render the object on the Layer
                worldObject.draw(monet, observer, alpha);
            }
        }
    }

    public void add(WorldObject worldObject) {

        // Add to the list
        objectList.add(worldObject);

        // Make sure the worldobject knows that this is its layer
        worldObject.setLayer(this);
        worldObject.addWorldObjectListener(this);

        // Notify the world of the addition; adds bodies
        fireStateChanged(worldObject, LayerEvent.ADDED);
    }

    public void addAll(ArrayList<WorldObject> objects) {

        //
        for (int i = 0; i < objects.size(); i++) {

            //
            final WorldObject object = objects.get(i);

            //
            add(object);
        }
    }

    public void remove(WorldObject worldObject) {

        // Clear the instance from the storage
        objectList.remove(worldObject);

        // Notify the world of the removal; does not clear bodies
        fireStateChanged(worldObject, LayerEvent.REMOVED);
    }

    public boolean contains(WorldObject newObject) {
        return objectList.contains(newObject);
    }

    public WorldObject[] getEncompassingObjects(Rectangle bounds) {

        //
        final ArrayList<WorldObject> list = new ArrayList<>();

        //
        for (int i = 0; i < objectList.size(); i++) {

            //
            final WorldObject object = objectList.get(i);

            //
            if (object.getBounds().contains(bounds)) {
                list.add(object);
            }
        }

        //
        return list.toArray(new WorldObject[]{});
    }

    public WorldObject getInstanceAtPosition(Point position) {

        //
        for (int i = 0; i < objectList.size(); i++) {

            // Grab
            final WorldObject object = objectList.get(i);

            //
            if (object.getBounds().contains(position)) {
                return object;
            }
        }

        return null;
    }

    public boolean isInstanceAtPosition(Point position) {

        // Position must exist
        if (position == null) {
            return false;
        }

        // Iterate over all objects
        for (int i = 0; i < objectList.size(); i++) {

            // Grab the object
            final WorldObject object = objectList.get(i);

            // Grab from object
            final Rectangle2D.Float bounds = object.getBounds();

            // Bounds must exist
            if (bounds != null) {

                // Bounds must contain the position to be considered
                if (bounds.contains(position)) {
                    return true;
                }
            }
        }

        // Failed to find instanceof this position
        return false;
    }

    public void clear() {

        // Clear the array
        objectList.clear();
    }

    private void matchFieldValues(Class closs) {
        try {
            for (Field field : closs.getDeclaredFields()) {
                for (Map.Entry<String, Object> set : attributeMap.entrySet()) {

                    // Must match exactly.
                    if (field.getName().equals(set.getKey())) {

                        //
                        final String value = String.valueOf(set.getValue());
                        final Class type = field.getType();

                        // Ask
                        if (type == int.class) {

                            // Deal with integers
                            field.setInt(this, Integer.parseInt(value));
                        } else if (type == double.class) {

                            // Deal with doubles
                            field.setDouble(this, Double.parseDouble(value));
                        } else if (type == float.class) {

                            // Deal with floats
                            field.setFloat(this, Float.parseFloat(value));
                        } else {

                            // May fail
                            field.set(this, set.getValue());
                        }
                    }
                }
            }
        } catch (IllegalAccessException iae) {
            System.err.println("Unable to apply field values: access is denied. " + iae);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Accessors">
    public ArrayList<WorldObject> getInhabitants() {
        return objectList;
    }

    public float getAlpha() {
        return alpha * 100.0f;
    }

    /*
     * Layers and Tiles are not saved to file and thus do not have checksums
     */
    @Override
    public String getSHA1CheckSum() {
        return null;
    }

    @Override
    public String getReferenceName() {
        return referenceName;
    }

    @Override
    public String getPackageId() {
        return packageID;
    }

    public int getIndex() {
        return index;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public WorldCell getMap() {
        return worldCell;
    }

    @Override
    public HashMap<String, Object> getAttributeMap() {

        // Update Attributes Map before output
        updateAttributes();

        // Return the attribute map
        return attributeMap;
    }

    @Override
    public String getReferenceID() {
        return referenceID;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public World getWorld() {
        return world;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Mutators">
    @Override
    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    @Override
    public void setFile(File file) {
        // Do nothing.
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setAlpha(float alpha) {

        // Break it down to the tenths
        this.alpha = alpha * 0.01f;

        // Judge it then
        this.alpha = (alpha > 0.0099f && alpha <= 1.0f) ? alpha : .05f;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void setPackageId(String packageID) {
        this.packageID = packageID;
    }

    @Override
    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    @Override
    public void setAttributeMap(HashMap<String, Object> attributeMap) {

        // Set all those values into the hashmap
        this.attributeMap.putAll(attributeMap);

        // Match the field values
        matchFieldValues(getClass());
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setWorld(World world) {
        this.world = world;
    }
    //</editor-fold>

    public boolean isVisible() {
        return visible;
    }

    // <editor-fold defaultstate="collapsed" desc="Event Triggers">
    public void addLayerListener(LayerListener source) {
        listenerList.add(LayerListener.class, source);
    }

    public void removeLayerListener(LayerListener source) {
        listenerList.remove(LayerListener.class, source);
    }

    public void fireStateChanged(Object source, int action) {

        // The actual event
        final LayerEvent event = new LayerEvent(source, action);

        // Make a quick copy of the array
        final Object[] copy = Arrays.copyOf(listenerList.getListenerList(), listenerList.getListenerList().length);

        // Iterate over the Listener List
        for (int i = 0; i < copy.length; i += 2) {

            // Make sure the current is just the class
            if (copy[i] == LayerListener.class) {

                // The actual listener
                final LayerListener listener = (LayerListener) copy[i + 1];

                // Fire the event in all the implementing classes
                listener.layerModified(event);
            }
        }
    }

    @Override
    public void animationEnd(ActorEvent event) {

        // Notify to the map that a change has occured and that a repaint is nessecary
        fireStateChanged(event.getSource(), WorldObject.FLAG_ANIMATION_END);
    }

    @Override
    public void animationStep(ActorEvent event) {

        // Notify to the map that a change has occured and that a repaint is nessecary
        fireStateChanged(event.getSource(), WorldObject.FLAG_ANIMATION_STEP);
    }
    //</editor-fold>

    @Override
    public void worldObjectModified(WorldObjectEvent event) {

        // Notify the map that a change has occured to a world object
        fireStateChanged(event.getSource(), LayerEvent.FLAG_MODIFIED);
    }
}
