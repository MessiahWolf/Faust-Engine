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

/**
 *
 * @author Robert A. Cherry
 */
public class DataRef {

    // Variable Declaration
    // Project Classes
    private WorldResource resource;
    private DataPackage dataPackage;
    // Data Types
    private boolean sessionMarkedForChange = true;
    private String referenceID;
    private String referenceName;
    private String displayName;
    // End of Variable

    public DataRef(WorldResource resource) {

        // Grab from resource
        referenceID = resource.getReferenceID();
        referenceName = resource.getReferenceName();
        displayName = resource.getDisplayName();

        //
        this.resource = resource;
    }

    public DataRef(DataPackage dataPackage) {

        // Grab from dataPackage
        referenceID = dataPackage.getReferenceId();
        referenceName = dataPackage.getReferenceName();
        displayName = dataPackage.getDisplayName();

        //
        this.dataPackage = dataPackage;
    }
    
    public void change(String referenceID, String referenceName, String displayName) {
        this.referenceID = referenceID;
        this.referenceName = referenceName;
        this.displayName = displayName;
    }
    
    public boolean isMarkedForChange() {
        return sessionMarkedForChange;
    }

    public WorldResource getResource() {
        return resource;
    }

    public DataPackage getPackage() {
        return dataPackage;
    }
    
    public String getPackageId() {
        if (dataPackage == null) {
            return ResourceDelegate.UNPACKAGED_STATEMENT;
        }
        
        return dataPackage.getReferenceId();
    }

    public String getEditorId() {
        return referenceID;
    }

    public String getEditorName() {
        return referenceName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public void setMarkedForChange(boolean sessionMarkedForChange) {
        this.sessionMarkedForChange = sessionMarkedForChange;
    }
}
