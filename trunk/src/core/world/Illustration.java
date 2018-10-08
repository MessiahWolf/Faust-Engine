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

import io.resource.ResourceRequest;
import io.util.FileUtils;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Robert A. Cherry
 */
public abstract class Illustration implements WorldResource {

    // Variable Declaration
    protected HashMap<String, ResourceRequest> requests;
    protected HashMap<String, Object> attributes;
    // Project Classes
    protected Picture picture;
    // Data Types
    protected boolean visible = true;
    protected int blockWidth;
    protected int blockHeight;
    protected int blockXOffset;
    protected int blockHGap;
    protected int blockVGap;
    protected int blockYOffset;
    protected int blockRows;
    protected int blockColumns;
    protected String referenceName;
    protected String pictureReferenceID;
    protected String picturePackageID;
    protected String pictureTag;
    protected String pictureSum;
    protected String referenceID;
    protected String packageID;
    protected String displayName;
    // Not Implemented Yet
    protected String sha1CheckSum;
    // End of Variable Declaration

    public Illustration() {

        // Initialize
        attributes = new HashMap<>();
        requests = new HashMap<>();

        //
        referenceID = null;
        referenceName = null;
        displayName = null;
        packageID = null;

        //
        pictureReferenceID = null;
        picturePackageID = null;
        pictureTag = null;
        pictureSum = null;

        //
        sha1CheckSum = null;
    }

    public Illustration(String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {

        //
        this();

        // File stuff
        this.sha1CheckSum = sha1CheckSum;

        // The actual referenceName of the graphic -- including file extension
        this.referenceID = referenceID;
        this.packageID = packageID;
        this.displayName = displayName;
        this.referenceName = referenceName;
    }

    protected void matchFieldValues(Class closs) {

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
    public String getSHA1CheckSum() {
        return sha1CheckSum;
    }

    @Override
    public HashMap<String, Object> getAttributeMap() {

        //
        updateAttributes();

        //
        return attributes;
    }

    public Picture getPicture() {
        return picture;
    }

    @Override
    public final String getReferenceID() {
        return referenceID;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public String getPictureID() {
        return pictureReferenceID;
    }

    public String getPicturePackageID() {
        return picturePackageID;
    }

    @Override
    public String getPackageID() {
        return packageID;
    }

    @Override
    public String getReferenceName() {
        return referenceName;
    }

    public String getPictureTag() {
        return pictureTag;
    }

    public int getBlockWidth() {
        return blockWidth;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public int getBlockHGap() {
        return blockHGap;
    }

    public int getBlockVGap() {
        return blockVGap;
    }

    public int getBlockXOffset() {
        return blockXOffset;
    }

    public int getBlockYOffset() {
        return blockYOffset;
    }

    public int getBlockColumns() {
        return blockColumns;
    }

    public int getBlockRows() {
        return blockRows;
    }
    
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setAttributeMap(HashMap<String, Object> newMap) {

        // Place all those values into the attributes map
        attributes.putAll(newMap);

        // Match those new values to fields
        matchFieldValues(getClass());
        matchFieldValues(getClass().getSuperclass());
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    @Override
    public void setPackageID(String packageID) {
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

    public void setPictureInfo(String picturePackageID, String pictureReferenceID) {
        this.picturePackageID = picturePackageID;
        this.pictureReferenceID = pictureReferenceID;
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

    public void setPictureTag(String newTag) {
        pictureTag = newTag;
    }

    public void setBlockRows(int newRows) {
        blockRows = newRows;
    }

    public void setBlockColumns(int newCols) {
        blockColumns = newCols;
    }

    public void setBlockVGap(int newVGap) {
        blockVGap = newVGap;
    }

    public void setBlockHGap(int newHGap) {
        blockHGap = newHGap;
    }

    public void setBlockXOffset(int newXOff) {
        blockXOffset = newXOff;
    }

    public void setBlockYOffset(int newYOff) {
        blockYOffset = newYOff;
    }

    public void setBlockWidth(int newWidth) {
        blockWidth = newWidth;
    }

    public void setBlockHeight(int newHeight) {
        blockHeight = newHeight;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void updateAttributes() {

        // Setup the property map
        attributes.put("referenceName", referenceName);
        attributes.put("referenceID", referenceID);
        attributes.put("packageID", packageID);
        attributes.put("displayName", displayName);

        //
        attributes.put("pictureReferenceID", pictureReferenceID);
        attributes.put("picturePackageID", picturePackageID);
        attributes.put("pictureSum", pictureSum);

        //
        attributes.put("blockWidth", blockWidth);
        attributes.put("blockHeight", blockHeight);
        attributes.put("blockColumns", blockColumns);
        attributes.put("blockRows", blockRows);
        attributes.put("blockXOffset", blockXOffset);
        attributes.put("blockYOffset", blockYOffset);
        attributes.put("blockHGap", blockHGap);
        attributes.put("blockVGap", blockVGap);
    }

    @Override
    public void shadow(String referenceID, ResourceRequest request) {

        //
        // System.out.println(displayName + " Shadowing Resrouce: " + referenceID);
        requests.put(referenceID, request);
    }

    @Override
    public void receive(String referenceID, WorldResource resource) {

        // Only accept things we are looking for...
        if (requests.containsKey(referenceID)) {

            // Check for resource image
            if (resource.getClass() == Picture.class) {

                // Set the new graphic object
                picture = (Picture) resource;

                //
                pictureReferenceID = picture.getReferenceID();
                picturePackageID = picture.getPackageID();

                // Every Scenic Object needs to validate after being given its resource.
                validate();
            }
        }
    }

    public abstract BufferedImage draw(ImageObserver obs, float alpha);
}
