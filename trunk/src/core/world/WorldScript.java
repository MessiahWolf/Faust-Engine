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
import io.script.WorldScriptInterface;
import io.util.FileUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Robert A. Cherry
 */
public class WorldScript implements WorldResource, WorldScriptInterface {

    // Variable Declaration
    // Java Native Collection Classes
    private HashMap<String, Object> attributeMap;
    // Java Native Classes
    private File file;
    private File scriptFile;
    // Project Classes
    private WorldScriptInterface scriptInterface;
    // Data Types
    private String referenceName;
    private String referenceID;
    private String packageID;
    private String displayName;
    private String scriptFileName;
    private String scriptCheckSum;
    // Not Implemented Yet
    private String sha1CheckSum;
    // End of Variable Declaration

    private WorldScript() {
        
        //
        referenceID = "";
        referenceName = "";
        displayName = "";
        packageID = "";
        
        //
        scriptFileName = "";
        scriptCheckSum = "";
        
        //
        sha1CheckSum = "";

        // Inst. Section
        attributeMap = new HashMap<>();
    }

    public WorldScript(File scriptFile, String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {

        //
        this.scriptFile = scriptFile;

        // Set values
        this.referenceID = referenceID;
        this.packageID = packageID;
        this.displayName = displayName;
        this.referenceName = referenceName;

        //
        if (scriptFile != null) {
            scriptFileName = scriptFile.getName();
        }
    }

    @Override
    public void create() {
        
        // In the .js file this method must be present
    }

    @Override
    public void destroy() {
        
        // In the .js file this method must be present
    }

    @Override
    public void action() {
        
        // In the .js file this method must be present
    }

    @Override
    public WorldResource reproduce() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void validate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    @Override
    public void shadow(String referenceID, ResourceRequest request) {
        // Does not shadow resources
    }

    @Override
    public void receive(String referenceID, WorldResource resource) {
        // Does not receive resources
    }

    @Override
    public HashMap<String, Object> getAttributeMap() {

        // Update before sending out
        updateAttributes();

        //
        return attributeMap;
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

    public File getFile() {
        return file;
    }

    public WorldScriptInterface getInterface() {
        return scriptInterface;
    }

    @Override
    public void setAttributeMap(HashMap<String, Object> attributeMap) {
        this.attributeMap.putAll(attributeMap);

        //
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
    public void setFile(File file) {
        this.file = file;
    }

    public void setScriptFile(File scriptFile) {
        this.scriptFile = scriptFile;

        try {

            // Grab the name from the JSON file
            scriptFileName = scriptFile.getName();

            // Grab Checksum at this point
            scriptCheckSum = FileUtils.generateChecksum(scriptFile.getAbsolutePath(), "SHA-1");
        } catch (Exception e) {
            System.err.println("World failed to accept JSON File: " + e);
        }
    }

    public void setInterface(WorldScriptInterface scriptInterface) {
        this.scriptInterface = scriptInterface;
    }

    @Override
    public void updateAttributes() {
        
        //
        attributeMap.put("referenceID", referenceID);
        attributeMap.put("referenceName", referenceName);
        attributeMap.put("displayName", displayName);
        attributeMap.put("packageID", packageID);
        
        //
        attributeMap.put("scriptFileName", scriptFileName);
        attributeMap.put("scriptCheckSum", scriptCheckSum);
    }
}
