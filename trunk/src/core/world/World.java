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

import core.event.MapEvent;
import core.event.MapListener;
import core.event.WorldEvent;
import core.event.WorldListener;
import io.resource.ResourceRequest;
import io.util.FileUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.EventListenerList;

/**
 * FWorld acts as a wrapper class for Box2D's World class. FWorld allows a Box2D
 * World to be saved and properly interpreted by the Faust Editor.
 *
 *
 */
public class World implements WorldResource, MapListener {

    // Variable Declaration
    // Java Native Collection Classes
    private ArrayList<WorldCell> cellList;
    private EventListenerList listeners;
    private HashMap<String, Object> attributes;
    protected HashMap<String, ResourceRequest> requests;
    // Project Classes
    private WorldCell currentCell;
    // Data Types
    private String referenceName;
    private String referenceID;
    private String packageID;
    private String displayName;
    // Not Implemented Yet
    private String sha1CheckSum;
    // End of Variable Declaration

    public World() {

        this(null, null, null, null, null);
    }

    public World(String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {

        // File stuff
        this.sha1CheckSum = sha1CheckSum;

        // Delegate Stuff
        this.referenceID = referenceID;
        this.packageID = packageID;
        this.referenceName = referenceName;
        this.displayName = displayName;

        init();
    }

    private void init() {

        // Personal Stuff
        cellList = new ArrayList<>();
        attributes = new HashMap<>();
        requests = new HashMap<>();
        listeners = new EventListenerList();
    }

    @Override
    public World reproduce() {

        // FWorld to return
        final World copy = new World(sha1CheckSum, packageID, referenceID, referenceName, displayName);

        // Iterate over the collection of cells
        for (int i = 0; i < cellList.size(); i++) {

            // Grab current map
            final WorldCell worldCell = cellList.get(i);

            // Add a reproduction of the map
            copy.addCell(worldCell.reproduce());
        }

        // Here is our shallow copy
        return copy;
    }

    public void addCell(WorldCell worldCell) {

        // Add to the map list
        cellList.add(worldCell);

        //
        worldCell.setWorld(this);
        worldCell.addMapListener(this);

        // Fire an event notifier
        fireEventNotifier(worldCell, WorldEvent.ADDED);
    }

    public void addMapAtIndex(WorldCell map, int index) {

        // Add to the map list
        cellList.add(index, map);

        //
        map.setWorld(this);
        map.addMapListener(this);

        // Fire an event notifier
        fireEventNotifier(map, WorldEvent.ADDED);
    }

    public void step() {

        // Throw event for step
        fireEventNotifier(this, WorldEvent.STEPPED);
    }

    @Override
    public void validate() {

        //
        matchFieldValues(getClass());
    }

    @Override
    public void shadow(String referenceID, ResourceRequest request) {

        System.err.println("World Instance shadowing request: " + request.getEditorId());

        // Waits for maps only
        requests.put(referenceID, request);
    }

    @Override
    public void receive(String referenceID, WorldResource resource) {

        //
        System.out.println("World Instance recieving resource: " + resource.getDisplayName());

        // Only accept things we are looking for...
        if (requests.containsKey(referenceID)) {

            //
            System.out.println("Handling request for -> " + referenceID);

            // Check for resource image
            if (resource.getClass() == WorldCell.class) {

                // Set the new graphic object
                final WorldCell map = (WorldCell) resource;

                // Add the map; soon will grab index
                addCell(map);

                //
                validate();

                // Remove request
                requests.remove(referenceID);
            }
        } else {
            System.err.println("Key Ring does not contain such a request for -> " + referenceID);
        }
    }

    public boolean containsMap(WorldCell map) {
        return cellList.contains(map);
    }

    @Override
    public String getSHA1CheckSum() {
        return sha1CheckSum;
    }

    @Override
    public HashMap<String, Object> getAttributeMap() {

        // Update the Attributes
        updateAttributes();

        // Then return
        return attributes;
    }

    public WorldCell getFirstMap() {
        return cellList.isEmpty() ? null : cellList.get(0);
    }

    public WorldCell getMapByEditorId(String referenceID) {

        if (referenceID == null) {
            return null;
        }
        if (referenceID.length() == 0) {
            return null;
        }

        for (int i = 0; i < cellList.size(); i++) {

            // Grab from List
            final WorldCell fMap = cellList.get(i);

            //
            if (fMap.getReferenceID().equalsIgnoreCase(referenceID)) {
                return fMap;
            }
        }

        //
        return null;
    }

    public WorldCell getMapAtIndex(int index) {
        return (index >= 0 && index <= cellList.size() - 1) ? cellList.get(index) : null;
    }

    public WorldCell getLastMap() {
        return cellList.get(cellList.size() - 1);
    }

    public ArrayList<WorldCell> getCellList() {
        return cellList;
    }

    public int getIndexOfMap(WorldCell map) {
        return cellList.indexOf(map);
    }

    @Override
    public String getReferenceID() {
        return referenceID;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getPackageId() {
        return packageID;
    }

    @Override
    public String getReferenceName() {
        return referenceName;
    }

    @Override
    public void setAttributeMap(HashMap<String, Object> attributes) {
        this.attributes.putAll(attributes);

        matchFieldValues(getClass());
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    @Override
    public void setPackageId(String packageID) {
        this.packageID = packageID;
    }

    @Override
    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    @Override
    public void setFile(File file) throws Exception {

        // File must exist
        if (file != null) {
            sha1CheckSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");
        }
    }

    private void matchFieldValues(Class closs) {

        //
        try {

            // Iterate over all declared fields
            for (Field field : closs.getDeclaredFields()) {

                // Grab the type of field
                final String name = field.getName();
                final Class type = field.getType();

                // Iterate over attributes
                for (Map.Entry<String, Object> map : attributes.entrySet()) {

                    // Grab value
                    final String key = map.getKey();
                    final String value = String.valueOf(map.getValue());

                    // Attribute matches declared field name; this is really unsafe.
                    if (name.equals(key)) {

                        // Switch
                        if (type == int.class) {
                            field.setInt(this, Integer.parseInt(value));
                        } else if (type == double.class) {
                            field.setDouble(this, Double.parseDouble(value));
                        } else if (type == float.class) {
                            field.setFloat(this, Float.parseFloat(value));
                        } else if (type == boolean.class) {
                            field.setBoolean(this, Boolean.parseBoolean(value));
                        } else if (type == long.class) {
                            field.setLong(this, Long.parseLong(value));
                        } else if (type == short.class) {
                            field.setShort(this, Short.parseShort(value));
                        } else {
                            field.set(this, map.getValue());
                        }

                        // Break out
                        break;
                    }
                }
            }
        } catch (IllegalAccessException iae) {
            System.err.println("Unable to apply field values: access is denied. " + iae);
        }
    }

    @Override
    public void updateAttributes() {

        // Delegate stuff
        attributes.put("referenceID", referenceID);
        attributes.put("referenceName", referenceName);
        attributes.put("displayName", displayName);
        attributes.put("packageID", packageID);
    }

    public void addWorldListener(WorldListener source) {
        listeners.add(WorldListener.class, source);
    }

    public void removeWorldListener(WorldListener source) {
        listeners.remove(WorldListener.class, source);
    }

    public void fireEventNotifier(Object object, int action) {

        // The event
        final WorldEvent event = new WorldEvent(object, action);

        //
        final Object[] copy = Arrays.copyOf(listeners.getListenerList(), listeners.getListenerList().length);

        // Iterate over the copy of listeners
        for (int i = 0; i < copy.length; i += 2) {

            // Must be the implementing class
            if (copy[i] == WorldListener.class) {

                // Fire event notifier
                final WorldListener listener = (WorldListener) copy[i + 1];

                // Throw the event
                listener.worldModified(event);
            }
        }
    }

    @Override
    public void mapModified(MapEvent event) {

        // Just fire changed event
        fireEventNotifier(event.getSource(), WorldEvent.CHANGED);
    }
}
