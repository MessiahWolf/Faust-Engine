/**
 * Copyright (c) 2013, Robert Cherry * All rights reserved.
 *
 * This file is part of the Faust Engine.
 *
 * The Faust Engine is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * The Faust Engine is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * the Faust Engine. If not, see <http://www.gnu.org/licenses/>.
 */
package core.world;

import core.event.LayerEvent;
import core.event.LayerListener;
import core.event.MapEvent;
import core.event.MapListener;
import core.event.ZoneEvent;
import core.event.ZoneListener;
import io.resource.ResourceDelegate;
import io.resource.ResourceRequest;
import io.util.FileUtils;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
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
 * A fMap is a collection of objects of type <worldObject>, not to be confused
 * with the Box2D's World which is a collection of Bodies and Fixtures.
 *
 * The fMap and World are designed to work side by side
 *
 * @version 1.01
 * @author Robert Cherry
 */
public class WorldCell implements WorldResource, LayerListener, ZoneListener {

    // Variable Declaration
    // Java Native Collection Classes
    private ArrayList<Backdrop> backgroundList;
    private ArrayList<WorldCellLayer> layerList;
    private ArrayList<Zone> zoneList;
    private HashMap<String, ResourceRequest> requests;
    private HashMap<String, Object> attributeMap;
    protected EventListenerList listeners;
    // Project Classes
    private WorldCellLayer layerSelected;
    private World world;
    // Data types
    private boolean viewActors;
    private boolean viewTiles;
    private boolean viewBackgrounds;
    private boolean viewEffects;
    private int width;
    private int height;
    private String referenceName;
    private String referenceID;
    private String packageID;
    private String displayName;
    // Not Implemented Yet
    private String sha1CheckSum;
    // End of Variable Declaration

    public WorldCell(World world) {
        this(world, null, null, null, null, null, 0, 0);
    }

    public WorldCell(World world, String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName, int width, int height) {

        //
        this.sha1CheckSum = sha1CheckSum;

        // Set values
        this.world = world;
        this.referenceID = referenceID;
        this.packageID = packageID;
        this.referenceName = referenceName;
        this.displayName = displayName;
        this.width = width;
        this.height = height;

        //
        init();

        // Update fMap Attributes
        updateAttributes();
    }

    private void init() {

        // Instantiation section
        attributeMap = new HashMap<>();
        requests = new HashMap<>();
        backgroundList = new ArrayList<>();
        layerList = new ArrayList<>();
        zoneList = new ArrayList<>();
        listeners = new EventListenerList();
    }

    @Override
    public WorldCell reproduce() {

        // FMap to return; The reason for this method is for use in the test lab of the editor. Otherwise I would not have a reproduce method. :(
        final WorldCell copy = new WorldCell(world, sha1CheckSum, packageID, referenceID, referenceName, displayName, width, height);

        // Iterate over collection of layers
        for (int i = 0; i < layerList.size(); i++) {

            // Grab the current layer
            final WorldCellLayer layer = layerList.get(i);

            // Add the reproduction of that layer
            copy.addWorldCellLayer(world, layer.reproduce());
        }

        // Iterave over collection of backgrounds
        for (int i = 0; i < backgroundList.size(); i++) {

            // Grab the current background
            final Backdrop background = backgroundList.get(i);

            // Add the reproduction of that background
            copy.addBackground(background.reproduce());
        }

        // Return this copy of the map.
        return copy;
    }

    public void add(WorldObject object, WorldCellLayer layer) {

        // Must contain that layer
        if (layerList.contains(layer)) {

            // Add to the list
            layer.add(object);
        }
    }

    public void addBackground(Backdrop background) {

        // No duplicates allowed
        if (!backgroundList.contains(background)) {
            backgroundList.add(background);
        }

        // Force the background to identify with the map
        background.setMap(this);

        // Added a new background
        fireEventNotifier(background, MapEvent.ADDED);
    }

    public void addZone(Zone zone) {

        if (!zoneList.contains(zone)) {
            zoneList.add(zone);
        }

        // Fire event notifier
        fireEventNotifier(zone, MapEvent.ADDED);
    }

    public void addAt(WorldObject object, WorldCellLayer layer, Point position) {

        // Adjust the location
        object.setX(position.x);
        object.setY(position.y);

        // Add to the specified layer
        add(object, layer);
    }

    @Override
    public final void updateAttributes() {

        // Setup the Property Map
        attributeMap.put("referenceID", referenceID);
        attributeMap.put("packageID", packageID);
        attributeMap.put("displayName", displayName);
        attributeMap.put("referenceName", referenceName);
        attributeMap.put("width", width);
        attributeMap.put("height", height);

        // If it has a world
        if (world != null) {
            attributeMap.put("worldPackageId", world.getPackageId());
            attributeMap.put("worldEditorId", world.getReferenceID());
            attributeMap.put("worldDisplayName", world.getDisplayName());
        }
    }

    public void sort() {

        // This is how we are going to compare each element
        Comparator<WorldCellLayer> comparator = new Comparator<WorldCellLayer>() {
            @Override
            public int compare(WorldCellLayer current, WorldCellLayer next) {
                if (current.getIndex() > next.getIndex()) {
                    return 1;
                } else if (current.getIndex() == next.getIndex()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };

        // Do not sort an empty list
        if (layerList.isEmpty() == false) {
            Collections.sort(layerList, comparator);
        }

        // Fire an event change
        fireEventNotifier(layerList, MapEvent.SORTED);
    }

    public void addWorldCellLayer(World world, WorldCellLayer layer) {

        // Placing
        layerList.add(layer);

        // Set default layer as last added
        if (layerSelected == null) {
            layerSelected = layerList.get(layerList.size() - 1);
        }

        // Set it up with a Listener
        layer.setWorld(world);
        layer.addLayerListener(this);
        layer.validate();

        // Fire an event change
        fireEventNotifier(layer, MapEvent.ADDED);
    }

    public void removeLayer(WorldCellLayer layer) {

        // First check if it contains the layer
        if (layerList.contains(layer) == true) {

            // Remove the index of this object
            layerList.remove(layer);
        }

        // Fire an event change
        fireEventNotifier(layer, MapEvent.REMOVED);
    }

    public boolean containsLayerByName(String search) {

        if (search == null) {
            return false;
        }
        if (search.length() == 0) {
            return false;
        }

        for (int i = 0; i < layerList.size(); i++) {
            if (layerList.get(i).getReferenceName().equalsIgnoreCase(search)) {
                return true;
            }
        }

        return false;
    }

    public void step() {

        //
        for (int i = 0; i < layerList.size(); i++) {

            //
            final WorldCellLayer currentLayer = layerList.get(i);

            //
            if (currentLayer == null) {
                continue;
            }
        }
    }

    public BufferedImage paint(ImageObserver obs) {

        if (width <= 0 || height <= 0) {
            return null;
        }

        // The image to display
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Grab graphics from that
        final Graphics monet = image.getGraphics();

        // Don't render if there arent any
        if (backgroundList.isEmpty() == false) {

            // Iterate over collection
            for (int i = backgroundList.size() - 1; i >= 0; i--) {

                // Current background to paint
                final Backdrop current = backgroundList.get(i);

                // Must exist
                if (current != null) {
                    System.err.println("Drawing the backdrop");
                    monet.drawImage(current.draw(obs, 1.0f), 0, 0, obs);
                }
            }
        }

        // Don't paint empty layers
        if (layerList.isEmpty() == false) {

            // Paint all the layers from last to first.
            for (int i = layerList.size() - 1; i >= 0; i--) {

                // Grab current
                final WorldCellLayer current = layerList.get(i);

                // Must exist
                if (current != null) {
                    current.paint(monet, obs);
                }
            }
        }

        // Don't render if there aren't any
        if (zoneList.isEmpty()) {

            // Iterate over all zones
            for (int i = 0; i < zoneList.size(); i++) {

                // Grab the zone
                final Zone current = zoneList.get(i);

                // Must exist
                if (current != null) {
                    current.draw(monet, obs);
                }
            }
        }

        // Our pretty image :)
        return image;
    }

    @Override
    public void layerModified(LayerEvent evt) {

        // Types of events
        if (evt.getStateChange() == LayerEvent.ADDED) {

            // Fire an event inside of self
            fireEventNotifier(evt.getSource(), MapEvent.ADDED);
        } else if (evt.getStateChange() == LayerEvent.REMOVED) {

            // Fire an event inside of self
            fireEventNotifier(evt.getSource(), MapEvent.REMOVED);
        } else if (evt.getStateChange() == LayerEvent.SORTED) {

            // Fire an event inside of self
            fireEventNotifier(evt.getSource(), MapEvent.SORTED);
        } else if (evt.getStateChange() == LayerEvent.FLAG_MODIFIED) {

            // Fire an event inside of self
            fireEventNotifier(evt.getSource(), MapEvent.MODIFIED);
        }
    }

    @Override
    public void zoneEntered(ZoneEvent event) {
        // Do nothing
    }

    @Override
    public void zoneExited(ZoneEvent event) {
        // Do nothing
    }

    @Override
    public void zoneMoved(ZoneEvent event) {
        // Do nothing
    }

    @Override
    public void zoneReshaped(ZoneEvent event) {
        // Do nothing
    }

    public void addMapListener(MapListener source) {
        listeners.add(MapListener.class, source);
    }

    public void removeRoomListener(MapListener source) {
        listeners.remove(MapListener.class, source);
    }

    public void fireEventNotifier(Object object, int action) {

        // Create the event
        final MapEvent event = new MapEvent(object, action);

        // Do not actually use the existing listener array, but a copy of it.
        final Object[] copy = Arrays.copyOf(listeners.getListenerList(), listeners.getListenerList().length);

        // Iterate
        for (int i = 0; i < copy.length; i += 2) {

            // Only accepts properly labeled events from MapEvent.class
            if (copy[i] == MapListener.class) {

                // Grab the listener
                final MapListener listener = (MapListener) copy[i + 1];

                // Throw the actual event in the implementing classes
                listener.mapModified(event);
            }
        }
    }

    public Zone getZoneAtPosition(Point position) {

        // Iterate over all the zones
        for (int i = 0; i < zoneList.size(); i++) {

            // Grab current
            final Zone current = zoneList.get(i);

            // Ask
            if (current.getBounds().contains(position)) {
                return current;
            }
        }

        return null;
    }

    public ArrayList<Zone> getZoneList() {
        return zoneList;
    }

    @Override
    public String getSHA1CheckSum() {
        return sha1CheckSum;
    }

    @Override
    public String getReferenceID() {
        return referenceID;
    }

    @Override
    public String getPackageId() {
        return packageID;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public ArrayList<WorldCellLayer> getLayerList() {
        return layerList;
    }

    public ArrayList<Backdrop> getBackgroundList() {
        return backgroundList;
    }

    @Override
    public String getReferenceName() {
        return referenceName;
    }

    public WorldCellLayer getSelectedLayer() {
        return layerSelected;
    }

    public World getWorld() {
        return world;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isViewActors() {
        return viewActors;
    }

    public boolean isViewTiles() {
        return viewTiles;
    }

    public boolean isViewBackgrounds() {
        return viewBackgrounds;
    }

    public boolean isViewEffects() {
        return viewEffects;
    }

    public boolean isValidLayerName(String string) {

        // Invalid charaters for layer names
        final String regex = "<>|_-+=()*&^%$#@!~`,.?/;'[]{}";

        //
        if (string == null) {
            return false;
        }

        //
        if (string.length() == 0) {
            return false;
        }

        // Duplicate name check
        for (int i = 0; i < layerList.size(); i++) {

            //
            final WorldCellLayer layer = layerList.get(i);

            // Duplicate check
            if (layer.getDisplayName().equalsIgnoreCase(string)) {
                return false;
            }
        }

        // Invalid character check
        for (int i = 0; i < regex.length(); i++) {
            if (string.contains(String.valueOf(regex.charAt(i)))) {
                return false;
            }
        }

        // Invalid Strings
        final String[] invalid = ResourceDelegate.getInvalidStrings();

        // Secondary check
        for (int i = 0; i < invalid.length; i++) {
            if (string.equalsIgnoreCase(invalid[i])) {
                return false;
            }
        }

        // Its a valid layer name
        return true;
    }

    @Override
    public void setDisplayName(String newStr) {
        displayName = newStr;
    }

    @Override
    public void setReferenceID(String newStr) {
        referenceID = newStr;
    }

    @Override
    public void setPackageId(String newStr) {
        packageID = newStr;
    }

    @Override
    public void setFile(File file) throws Exception {

        // File must exist
        if (file != null) {
            sha1CheckSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");
        }
    }

    @Override
    public void setReferenceName(String newStr) {
        referenceName = newStr;
    }

    public void setBackgrounds(ArrayList<Backdrop> newBackground) {
        backgroundList = newBackground;
    }

    public void setSelectedLayer(WorldCellLayer newLayer) {
        layerSelected = newLayer;
    }

    public void setWidth(int newWidth) {
        width = newWidth;
    }

    public void setHeight(int newHeight) {
        height = newHeight;
    }

    public void setViewActors(boolean newState) {
        viewActors = newState;
    }

    public void setViewBackgrounds(boolean newState) {
        viewBackgrounds = newState;
    }

    public void setViewEffects(boolean newState) {
        viewEffects = newState;
    }

    public void setViewTiles(boolean newState) {
        viewTiles = newState;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void validate() {

        // Iterate over the collection of layers
        for (int i = 0; i < layerList.size(); i++) {

            //
            final WorldCellLayer layer = layerList.get(i);

            // Give it the world
            layer.setWorld(world);
            layer.validate();
        }
    }

    @Override
    public void shadow(String referenceID, ResourceRequest request) {

        System.out.println("World Cell shadowing request: " + request.getEditorId());

        // FMap only recieves layers, backgrounds, and a single FWorld
        requests.put(referenceID, request);
    }

    @Override
    public void receive(String referenceID, WorldResource resource) {

        // The class of the resource
        final Class closs = resource.getClass();

        System.err.println("World Cell recieved something");

        // Map does not recieve resources for now...
        if (requests.containsKey(referenceID)) {

            if (closs == World.class) {

                System.err.println("World Instance recieved.");

                // Just take it
                world = (World) resource;

                // Remove request
                requests.remove(referenceID);
            } else if (closs == WorldCellLayer.class) {

                // Cast to a layer
                final WorldCellLayer layer = (WorldCellLayer) resource;

                // adding a layer; soon to add indexes
                addWorldCellLayer(world, layer);

                // Remove request
                requests.remove(referenceID);
            } else if (closs == Backdrop.class) {

                // Cast to a background
                final Backdrop background = (Backdrop) resource;

                // adding a background; soon to add indexes
                addBackground(background);

                // Remove reqest
                requests.remove(referenceID);
            }
        }
    }

    @Override
    public HashMap<String, Object> getAttributeMap() {

        // Update Attributes Map before output
        updateAttributes();

        // Return the attributes map
        return attributeMap;
    }

    private void matchFieldValues(Class closs) {
        try {
            for (Field field : closs.getDeclaredFields()) {
                for (Map.Entry<String, Object> set : attributeMap.entrySet()) {

                    //
                    if (field.getName().equals(set.getKey())) {

                        //
                        String value = String.valueOf(set.getValue());
                        Class type = field.getType();
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

    @Override
    public void setAttributeMap(HashMap<String, Object> attributes) {

        // Place all the values from the previous map into this one
        this.attributeMap.putAll(attributes);

        // Match field values
        matchFieldValues(getClass());
    }
}
