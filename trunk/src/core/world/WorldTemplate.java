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

import io.resource.ResourceRequest;
import io.util.FileUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Robert A. Cherry
 */
public class WorldTemplate implements WorldResource {

    // Variable Declaration
    // Java Native Collection Classes
    private HashMap<String, Object> attributes;
    protected HashMap<String, ResourceRequest> requests;
    // Project Classes
    private WorldCell map;
    private World world;
    // Data Types
    private String referenceName;
    private String referenceID;
    private String packageID;
    private String displayName;
    private String worldDisplayName;
    private String worldPackageId;
    private String cellDisplayName;
    private String cellPackageId;
    // Not Implemented Yet
    private String sha1CheckSum;
    // End of Variable Declaration

    private WorldTemplate() {

        //
        referenceID = "";
        referenceName = "";
        displayName = "";
        packageID = "";

        //
        worldDisplayName = "";
        worldPackageId = "";

        //
        cellDisplayName = "";
        cellPackageId = "";

        //
        sha1CheckSum = "";

        // Initialize the collections
        attributes = new HashMap<>();
        requests = new HashMap<>();
    }

    public WorldTemplate(String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {

        //
        this.sha1CheckSum = sha1CheckSum;

        // Set resource values
        this.referenceID = referenceID;
        this.packageID = packageID;
        this.referenceName = referenceName;
        this.displayName = displayName;
    }

    @Override
    public WorldTemplate reproduce() {

        //
        final WorldTemplate copy = new WorldTemplate(sha1CheckSum, packageID, referenceID, referenceName, displayName);
        copy.world = world;
        copy.map = map.reproduce();

        //
        return copy;
    }

    @Override
    public void validate() {

        // Matching up field values
        matchFieldValues(getClass());
    }

    @Override
    public void shadow(String referenceID, ResourceRequest request) {

        // This class tracks FWorlds and FMaps only
        requests.put(referenceID, request);
    }

    @Override
    public void receive(String referenceID, WorldResource resource) {

        // This class can only recieve tracked FWorlds and FMaps
        // Only accept things we are looking for...
        if (requests.containsKey(referenceID)) {

            // Check for resource image
            if (resource.getClass() == WorldCell.class) {

                // We have our map :D
                map = (WorldCell) resource;
                cellDisplayName = map.getDisplayName();
                cellPackageId = map.getPackageId();

                // Remove request
                requests.remove(referenceID);
            } else if (resource.getClass() == World.class) {

                // We have our world :D
                world = (World) resource;
                worldDisplayName = world.getDisplayName();
                worldPackageId = world.getPackageId();

                // Remove request
                requests.remove(referenceID);
            }
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
                for (Map.Entry<String, Object> imap : attributes.entrySet()) {

                    // Grab value
                    final String key = imap.getKey();
                    final String value = String.valueOf(imap.getValue());

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
                            field.set(this, imap.getValue());
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
    public HashMap<String, Object> getAttributeMap() {
        return attributes;
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

    public WorldCell getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void setAttributeMap(HashMap<String, Object> attributes) {
        this.attributes.putAll(attributes);
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

    public void setWorld(World world) {

        // Set the world
        this.world = world;

        // Must exist before hand
        if (world != null) {

            // Grab from both world and map
            worldDisplayName = world.getDisplayName();
            worldPackageId = world.getPackageId();
        }
    }

    public void setMap(WorldCell map) {

        // Set the map
        this.map = map;

        // Must exist before hand
        if (map != null) {

            //
            cellDisplayName = map.getDisplayName();
            cellPackageId = map.getPackageId();
        }
    }

    @Override
    public void updateAttributes() {

        attributes.put("referenceID", referenceID);
        attributes.put("referenceName", referenceName);
        attributes.put("displayName", displayName);
        attributes.put("packageID", packageID);
        attributes.put("cellDisplayName", cellDisplayName);
        attributes.put("cellPackageId", cellPackageId);
        attributes.put("worldDisplayName", worldDisplayName);
        attributes.put("worldPackageId", worldPackageId);
        
        // Checksum values soon -- @TODO
    }
}
