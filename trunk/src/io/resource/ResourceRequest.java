/**
    Copyright (c) 2013, Robert Cherry    
    
    All rights reserved.
  
    This file is part of the Faust Engine.

    The Faust Engine is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Faust Engine is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Faust Engine.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.resource;

import core.world.WorldResource;
import java.util.HashMap;

/**
 *
 * @author Robert A. Cherry
 */
public class ResourceRequest {

    // Variable Declaration
    // Java Native Collection Classes
    private HashMap<String, Object> attributes;
    // Protect Classes
    private WorldResource target;
    // Data Types
    private boolean dataPackage;
    private String referenceID;
    private String pluginRequest;
    private String optionalCheckSum = null;
    // End of Variable Declaration

    public ResourceRequest(String referenceID, WorldResource target) {

        // Set the values
        this.target = target;
        this.referenceID = referenceID;
        dataPackage = false;
    }

    public ResourceRequest(String pluginEditorId, String referenceName, WorldResource target) {

        //
        this(pluginEditorId, target);
        pluginRequest = referenceName;
        dataPackage = true;
    }

    public ResourceRequest(String referenceID, WorldResource target, HashMap<String, Object> attributes) {

        // Set
        this(referenceID, target);
        this.attributes = new HashMap<>();
        this.attributes.putAll(attributes);
        dataPackage = false;
    }
    
    public void filter(String optionalCheckSum) {
        this.optionalCheckSum = optionalCheckSum;
    }

    public void setPreloadOptions(HashMap<String, Object> set) {

        //
        attributes = new HashMap<>();
        attributes.putAll(set);
    }

    public HashMap<String, Object> getPreloadOptions() {
        return attributes;
    }
    
    public String getOptionalCheckSum() {
        return optionalCheckSum;
    }

    public String getEditorId() {
        return referenceID;
    }

    public WorldResource getTarget() {
        return target;
    }

    public String getPluginRequest() {
        return pluginRequest;
    }

    public boolean isPackageRequest() {
        return dataPackage;
    }
    
    public boolean isFiltered() {
        return optionalCheckSum != null;
    }
}
