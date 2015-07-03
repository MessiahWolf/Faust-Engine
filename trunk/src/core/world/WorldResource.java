/**
 * Copyright (c) 2013, Robert Cherry  *
 * All rights reserved.
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
import java.awt.image.RasterFormatException;
import java.io.File;
import java.util.HashMap;

/**
 * This Interface allows any implementing classes to be able to be written and
 * read to an XML file that will be used by this application
 */
public interface WorldResource {

    // Reproduce an copy instance of this resource
    WorldResource reproduce();

    // Validate this resource
    void validate() throws RasterFormatException;

    // Shadow a resource that has not yet been validated by the delegate
    void shadow(String referenceID, ResourceRequest request);

    // Recieve a resource that has been properly validated by the delegate
    void receive(String referenceID, WorldResource resource);

    // Public Accessors
    HashMap<String, Object> getAttributeMap();

    String getReferenceID();

    String getDisplayName();

    String getPackageId();

    String getReferenceName();

    String getSHA1CheckSum();

    // Public Mutators
    void setAttributeMap(HashMap<String, Object> attributes);

    void setDisplayName(String displayName);

    void setReferenceID(String referenceID);

    void setPackageId(String packageID);

    void setReferenceName(String referenceName);

    void setFile(File file) throws Exception;

    // Updating the Hash Table
    void updateAttributes();
}
