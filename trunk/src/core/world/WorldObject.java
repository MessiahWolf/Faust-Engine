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

import core.event.WorldObjectEvent;
import core.event.WorldObjectListener;
import io.resource.ResourceRequest;
import io.util.FileUtils;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.EventListenerList;

/**
 * The purpose of this class is to provide the basic information need for
 * sub-classes to be properly rendered in the Box2D world space
 *
 *
 * @version 3.7
 * @author Robert Cherry
 */
public abstract class WorldObject implements WorldResource {

    // Variable Declaration
    protected EventListenerList listenerList;
    protected HashMap<String, WorldAction> actionMap;
    protected HashMap<String, Object> attributeMap;
    protected HashMap<String, ResourceRequest> requestMap;
    // Project Classes
    protected WorldCellLayer layer;
    // Data Types
    protected boolean border;
    protected boolean selected;
    protected boolean visible;
    protected float rotation;
    protected int depth;
    protected int x;
    protected int y;
    protected String displayName;
    protected String referenceID;
    protected String referenceName;
    protected String packageID;
    // Not implemented yet.
    protected String instanceID;
    protected String sha1CheckSum;
    // Flags
    public static final int FLAG_CREATE = 0xe01;
    public static final int FLAG_DESTROY = 0xe02;
    public static final int FLAG_STEP_START = 0xe03;
    public static final int FLAG_STEP_END = 0xe04;
    public static final int FLAG_ANIMATION_START = 0xa01;
    public static final int FLAG_ANIMATION_STEP = 0xa02;
    public static final int FLAG_ANIMATION_END = 0xa03;
    // End of Variable Declaration

    public WorldObject() {

        // Inst. Section
        listenerList = new EventListenerList();
        actionMap = new HashMap<>();
        attributeMap = new HashMap<>();
        requestMap = new HashMap<>();
        
        //
        referenceID = "";
        referenceName = "";
        displayName = "";
        packageID = "";
        
        //
        instanceID = "";
        
        //
        sha1CheckSum = "";
    }

    public WorldObject(String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {

        // Do the default stuff first
        this();

        // Visiblity options
        visible = true;
        selected = false;

        // File Stuff
        this.sha1CheckSum = sha1CheckSum;

        // Set values
        this.referenceID = referenceID;
        this.packageID = packageID;
        this.displayName = displayName;
        this.referenceName = referenceName;
    }

    protected void matchFieldValues(Class closs) {

        //
        try {

            //
            for (Field field : closs.getDeclaredFields()) {

                //
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
                        } else if (type == long.class) {

                            // Deal with longs
                            field.setLong(this, Long.parseLong(value));
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

    protected abstract void draw(Graphics monet, ImageObserver obs, float alpha);

    @Override
    public void updateAttributes() {

        //
        attributeMap.put("referenceID", referenceID);
        attributeMap.put("displayName", displayName);
        attributeMap.put("packageID", packageID);
        attributeMap.put("referenceName", referenceName);
        attributeMap.put("x", x);
        attributeMap.put("y", y);
    }

    @Override
    public void shadow(String referenceID, ResourceRequest request) {
        requestMap.put(referenceID, request);
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isVisible() {
        return visible;
    }

    // File Stuff
    @Override
    public String getSHA1CheckSum() {
        return sha1CheckSum;
    }

    // Accessors
    @Override
    public HashMap<String, Object> getAttributeMap() {

        // Update the attribute map before sending out
        updateAttributes();

        // Send it out
        return attributeMap;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public String getPackageId() {
        return packageID;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getReferenceID() {
        return referenceID;
    }

    @Override
    public String getReferenceName() {
        return referenceName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public WorldCellLayer getLayer() {
        return layer;
    }

    public abstract Rectangle2D.Float getBounds();

    @Override
    public void setAttributeMap(HashMap<String, Object> attributeMap) {

        // Place all values from given map
        this.attributeMap.putAll(attributeMap);

        // This will be an FItem or FActor because this class is abstract
        matchFieldValues(getClass());

        // This will be the WorldObject class
        matchFieldValues(getClass().getSuperclass());
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setDrawBorder(boolean border) {
        this.border = border;
    }

    @Override
    public void setPackageId(String packageID) {
        this.packageID = packageID;
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setLayer(WorldCellLayer layer) {
        this.layer = layer;
    }

    public void addWorldObjectListener(WorldObjectListener listener) {
        listenerList.add(WorldObjectListener.class, listener);
    }

    public void removeWorldObjectListener(WorldObjectListener listener) {
        listenerList.remove(WorldObjectListener.class, listener);
    }

    protected void fireEventNotifier(Object source, int action) {

        // Wrap the event around the Actor Event class
        final WorldObjectEvent event = new WorldObjectEvent(source, action);

        // Automatic copy of Listeners
        final Object[] copy = Arrays.copyOf(listenerList.getListenerList(), listenerList.getListenerList().length);

        // Every other object is the actual class
        for (int i = 0; i < copy.length; i += 2) {

            // Check for listener
            if (copy[i] == WorldObjectListener.class) {

                // Implementing object
                final WorldObjectListener listener = (WorldObjectListener) copy[i + 1];

                // Trigger the event
                listener.worldObjectModified(event);
            }
        }
    }
}