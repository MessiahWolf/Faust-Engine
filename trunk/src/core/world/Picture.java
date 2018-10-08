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
import java.awt.Image;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Robert A. Cherry
 */
public class Picture implements WorldResource {

    // Variable Declaration
    // Java Native Classes
    private Image graphic;
    // Java Native Collection Classes
    private HashMap<String, Object> attributes;
    // Data Types
    private int height;
    private int width;
    private String referenceName;
    private String referenceID;
    private String displayName;
    private String packageID;
    private String pictureSum;
    private String pictureLocation;
    // End of Variable Declaration

    public Picture(String graphicCheckSum, String referenceID, String packageID, String displayName, String referenceName, Image graphic) {

        // File Stuff
        this.pictureSum = graphicCheckSum;

        // Save the image
        this.referenceID = referenceID;
        this.packageID = packageID;
        this.displayName = displayName;
        this.referenceName = referenceName;
        this.graphic = graphic;

        // Create the hash map
        attributes = new HashMap<>();
    }

    @Override
    public Picture reproduce() {

        //
        final Picture copy = new Picture(pictureSum, "temporary", "temporary", "temporary", "temporary", graphic);
        copy.validate();

        //
        return copy;
    }

    @Override
    public void validate() {

        //
        if (graphic != null) {

            //
            width = graphic.getWidth(null);
            height = graphic.getHeight(null);
        }
    }

    @Override
    public void shadow(String referenceID, ResourceRequest request) {
        throw new UnsupportedOperationException("Image does not shadow requests");
    }

    @Override
    public void receive(String referenceID, WorldResource resource) {
        throw new UnsupportedOperationException("Image cannot recieve requests");
    }

    /*
     * FImage's Checksum is that of its paired graphic
     */
    @Override
    public String getSHA1CheckSum() {
        return pictureSum;
    }

    @Override
    public HashMap<String, Object> getAttributeMap() {

        // Update before sending out
        updateAttributes();

        // Return attribute map
        return attributes;
    }

    public Image getImage() {
        return graphic;
    }

    @Override
    public String getReferenceID() {
        return referenceID;
    }

    @Override
    public String getPackageID() {
        return packageID;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getReferenceName() {
        return referenceName;
    }

    public String getGraphicCheckSum() {

        // Attempt to refresh it
        try {
            pictureSum = FileUtils.generateChecksum(referenceName, "SHA-1");
        } catch (Exception ioe) {
        }

        // Return the files checksum
        return pictureSum;
    }

    public String getPictureLocation() {
        return pictureLocation;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void setAttributeMap(HashMap<String, Object> newMap) {

        //
        attributes.putAll(newMap);

        //
        matchFieldValues(getClass());
    }

    @Override
    public void setReferenceID(String newStr) {
        referenceID = newStr;
    }

    @Override
    public void setPackageID(String newStr) {
        packageID = newStr;
    }

    @Override
    public void setDisplayName(String newStr) {
        displayName = newStr;
    }

    @Override
    public void setReferenceName(String newStr) {
        referenceName = newStr;
    }

    @Override
    public void setFile(File file) throws Exception {

        // File must exist
        if (file != null) {
            pictureSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");
            pictureLocation = file.getAbsolutePath();
        }
    }

    @Override
    public void updateAttributes() {
        attributes.put("referenceName", referenceName);
        attributes.put("displayName", displayName);
        attributes.put("packageID", packageID);
        attributes.put("referenceID", referenceID);
        attributes.put("graphicCheckSum", pictureSum);
    }

    private void matchFieldValues(Class newClass) {
        try {
            for (Field field : newClass.getDeclaredFields()) {
                for (Map.Entry<String, Object> map : attributes.entrySet()) {

                    //
                    if (field.getName().equals(map.getKey())) {
                        String mapValue = String.valueOf(map.getValue());
                        Class type = field.getType();
                        if (type == int.class) {
                            field.setInt(this, Integer.parseInt(mapValue));
                        } else if (type == double.class) {
                            field.setDouble(this, Double.parseDouble(mapValue));
                        } else if (type == float.class) {
                            field.setFloat(this, Float.parseFloat(mapValue));
                        } else {
                            field.set(this, map.getValue());
                        }
                    }
                }
            }
        } catch (IllegalAccessException iae) {
            System.err.println("Unable to apply field values: access is denied. " + iae);
        }
    }
}
