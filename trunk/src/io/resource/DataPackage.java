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
package io.resource;

import core.world.WorldResource;
import core.world.Picture;
import io.util.FileUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Robert A. Cherry
 */
public class DataPackage {

    // Variable Declaration
    // Java Native Classes
    private ArrayList<DataRef> referenceList;
    private Document manifest;
    // Project Classes
    private PackageQueue queue;
    // Data Types
    private String author;
    private String email;
    private String version;
    private String referenceName;
    private String referenceID;
    private String displayName;
    // End of Variable Declaration

    // It just stores the data from the archive
    public DataPackage(String author, String email, String version, String referenceID, String displayName, String referenceName, Document manifest) {

        // Store the manifest file
        this.manifest = manifest;

        // Save information
        this.author = author;
        this.email = email;
        this.version = version;
        this.referenceID = referenceID;
        this.displayName = displayName;
        this.referenceName = referenceName;

        // Create the value holder
        referenceList = new ArrayList<>();
        queue = new PackageQueue();
    }

    public void update(HashMap<String, String> map) {

        //
        for (int i = 0; i < referenceList.size(); i++) {

            //
            final DataRef reference = referenceList.get(i);

            //
            final String refEditorId = reference.getEditorId();
            final String refEditorName = reference.getEditorName();
            final String refDisplayName = reference.getDisplayName();

            for (Map.Entry<String, String> values : map.entrySet()) {

                //
                if (values.getKey().equalsIgnoreCase(refEditorName)) {

                    // Only thing that changes is the referenceName
                    reference.change(refEditorId, values.getValue(), refDisplayName);
                    break;
                }
            }
        }
    }

    public void applyChanges(ResourceDelegate delegate) {

        // Give the queue to PluginUtils to write over the dataPackage and all its changes in a single go.
        if (queue.hasChanges()) {
            queue.adjustArchive(delegate, this);
        }
    }

    public void addFile(ResourceDelegate delegate, File file) {

        // 1. Check for existing file
        if (file == null) {
            return;
        }

        // 2. Do not add directories
        if (file.isDirectory()) {
            return;
        }

        // 3. Do not accept the manifest file
        if (file.getName().equalsIgnoreCase("manifest.xml")) {
            return;
        }

        // Option for a dataPackage without a manifest file
        if (manifest == null) {

            // Manifest a resource from file on disk
            final WorldResource resource = manifest(delegate, file);

            // Set the SHA1 Checksum here
            try {

                // Doesnt really set a file inside of the resource. It just makes it generate a SHA1 Checksum
                resource.setFile(file);
            } catch (Exception e) {
                // This occurs when you try to add a file that is not yet supported by the application
                //System.err.println("Data Package Add File: " + e);
            }

            // Must be an existing resource type to add
            if (resource != null) {

                // Create a reference from this resource
                final DataRef reference = new DataRef(resource);

                if (containsByEditorName(reference.getEditorName()) == false) {

                    // Add to the reference List
                    referenceList.add(reference);
                }
            }
        } else if (manifestContainsFile(file)) {

            // Manifest resource from file on disk
            final WorldResource resource = manifest(delegate, file);

            // Set the SHA1 Checksum here
            try {

                // Doesnt really set a file inside of the resource. It just makes it generate a SHA1 Checksum
                resource.setFile(file);
            } catch (Exception e) {
                //
                System.err.println("Data Package Add File: " + e);
            }

            // Must be an existing resource type to add
            if (resource != null) {

                // Create a reference from this resource
                final DataRef reference = new DataRef(resource);

                if (containsByEditorName(reference.getEditorName()) == false) {

                    // Add to the reference List
                    referenceList.add(reference);
                }
            }
        }
    }

    public void addEntry(WorldResource resource, File file) {

        // Create a reference from tihs resource
        DataRef reference = new DataRef(resource);

        // Add to the citaionList
        if (containsByEditorName(reference.getEditorName()) == false) {

            // Add the reference to the reference list
            referenceList.add(reference);

            // Log the change in the queue
            queue.logChange(resource.getReferenceID(), file, PackageQueue.FLAG_ADD);
        }
    }

    public void removeEntry(String referenceID, File file) {

        // First check to see if it even contains the resource
        DataRef found = findByEditorId(referenceID);

        // Find the reference inside of this dataPackage
        if (found != null) {

            // Remove the reference from the reference list
            referenceList.remove(found);

            // Log the change in the queue
            queue.logChange(referenceID, file, PackageQueue.FLAG_REMOVE);
        }
    }

    public void replaceEntry(String referenceID, WorldResource resource, File file) {

        // First check tos ee if it even contains the resource
        DataRef found = findByEditorId(referenceID);

        // Find the reference inside of this dataPackage
        if (found != null) {

            // Remove it
            referenceList.remove(found);

            // Add the new one
            referenceList.add(new DataRef(resource));

            // Log the change in the queue
            queue.logChange(referenceID, file, PackageQueue.FLAG_REPLACE);
        }
    }

    private boolean manifestContainsFile(File file) {

        if (manifest != null) {

            // Grab the manifest
            final Node root = manifest.getDocumentElement();

            //
            final NodeList children = root.getChildNodes();

            // Iterate over the node list
            for (int i = 0; i < children.getLength(); i++) {

                // Grab current entry
                final Node node = children.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // Grab the referenceName from the attributes of this node
                    final String name = node.getAttributes().getNamedItem("referenceName").getNodeValue();

                    // Ask
                    if (file.getName().equals(name)) {
                        return true;
                    }
                }
            }
        }

        // Return false otherwise
        return false;
    }

    private String echoId(String referenceName) {

        // Manifest must exist
        if (manifest != null) {

            // Grab the manifest
            final Node rootNode = manifest.getDocumentElement();

            // Grab all the children
            final NodeList children = rootNode.getChildNodes();

            // Iterate over the node list
            for (int i = 0; i < children.getLength(); i++) {

                // Grab current entry
                final Node node = children.item(i);

                // Do not accept text nodes only elements
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // Grab the referenceName from the attributes of this node
                    final String currentName = node.getAttributes().getNamedItem("referenceName").getNodeValue();

                    // Ask
                    if (referenceName.equals(currentName)) {

                        // Now grab referenceID
                        String currentId = node.getAttributes().getNamedItem("referenceID").getNodeValue();

                        // Return it
                        return currentId;
                    }
                }
            }
        }

        //
        return null;
    }

    private WorldResource manifest(ResourceDelegate delegate, File file) {

        // Resource Casing
        WorldResource resource = null;

        // Do not add folders
        if (file.isFile()) {

            // Info for the resource
            String newEditorName = file.getName();
            String newDisplayName = FileUtils.contract(file);
            String extension = FileUtils.getExtension(file);

            // Grab FImages
            if (ResourceReader.isImageExtension(extension)) {

                // Make an image out of it
                try {

                    // ImageIO.read Image
                    final BufferedImage image = ImageIO.read(file);

                    String pictureSum = null;

                    try {
                        // Generate a checksum of the file
                        pictureSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");

                    } catch (Exception ioe) {
                    }

                    // In the case that the resource is already listed inside the manifest with an appropiate referenceName and referenceID
                    // Do not confound other users resources by changing the id; instead replace with the same id
                    if (manifestContainsFile(file)) {

                        // Locate the referenceID from that
                        resource = new Picture(pictureSum, echoId(file.getName()), referenceID, newDisplayName, newEditorName, image);
                    } else {

                        // Create the Resource Image and assign it a randomly created 17 character ID; so you don't have to
                        resource = new Picture(pictureSum, delegate.createReferenceID(this, 0, 10), referenceID, newDisplayName, newEditorName, image);
                    }

                    //
                    resource.validate();
                } catch (IOException ioe) {
                    //
                }
            } else if (ResourceReader.isReaderExtension(extension)) {

                try {

                    // Craft Resource
                    resource = ResourceReader.read(delegate, file);

                    // Set the dataPackage Id afterwards
                    resource.setPackageId(referenceID);
                } catch (Exception ioe) {
                    System.err.println("Error reading resource into Data Package: " + ioe);
                }
            }
        }

        // Return the crafted resource
        return resource;
    }

    public Object[] getType(Class type) {

        // ArrayList output
        ArrayList<Object> output = new ArrayList<>();

        // Iterate over the reference list
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current Citation
            DataRef reference = referenceList.get(i);

            // Grab the current resource
            Object resource = reference.getResource();

            // Ask
            if (resource.getClass() == type || resource.getClass().isAssignableFrom(type)) {
                // Never actually give out the base object
                output.add(resource);
            }
        }

        // Return the found resources of type:
        return output.toArray();
    }

    public void flush() {

        // Clear the data map
        referenceList.clear();

        // Garbage collect
        System.gc();
    }

    // <editor-fold defaultstate="collapsed" desc="Contains and Find">
    public boolean containsByEditorName(String referenceName) {

        // Iterate over Citation List
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current Citation
            DataRef reference = referenceList.get(i);

            // Ask
            if (reference.getEditorName().equalsIgnoreCase(referenceName)) {
                return true;
            }
        }

        // Otherwise return false
        return false;
    }

    public boolean containsByEditorId(String referenceID) {

        // Iterate over Citation List
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current Citation
            DataRef reference = referenceList.get(i);

            // Ask
            if (reference.getEditorId().equalsIgnoreCase(referenceID)) {
                return true;
            }
        }

        // Otherwise return false
        return false;
    }

    public boolean containsByDisplayName(String displayName) {

        // Iterate over Citation List
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current Citation
            DataRef reference = referenceList.get(i);

            // Ask
            if (reference.getDisplayName().equalsIgnoreCase(displayName)) {
                return true;
            }
        }

        // Otherwise return false
        return false;
    }

    public DataRef findByEditorId(String referenceID) {

        // Iterate over the Citation List
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current Citation
            DataRef reference = referenceList.get(i);

            // Now ask
            if (reference.getEditorId().equals(referenceID)) {
                return reference;
            }
        }

        // Otherwise there is no reference by that editor id
        return null;
    }

    public DataRef findByEditorName(String referenceName) {

        // Iterate over the Citation List
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current Citation
            DataRef reference = referenceList.get(i);

            // Now ask
            if (reference.getEditorName().equals(referenceName)) {
                return reference;
            }
        }

        // Otherwise there is no reference by that editor id
        return null;
    }

    public DataRef findByDisplayName(String displayName) {

        // Iterate over the Citation List
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current Citation
            DataRef reference = referenceList.get(i);

            // Now ask
            if (reference.getDisplayName().equals(displayName)) {
                return reference;
            }
        }

        // Otherwise there is no reference by that editor id
        return null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Mutators and Accesors">
    public DataRef[] getCitations() {
        return referenceList.toArray(new DataRef[]{});
    }

    public String getAuthor() {
        return author;
    }

    public String getEmail() {
        return email;
    }

    public String getVersion() {
        return version;
    }

    public String getReferenceId() {
        return referenceID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public PackageQueue getQueue() {
        return queue;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setReferenceName(String newStr) {
        referenceName = newStr;
    }

    public void setReferenceId(String newStr) {
        referenceID = newStr;
    }

    public void setDisplayName(String newStr) {
        displayName = newStr;
    }
    //</editor-fold>
}
