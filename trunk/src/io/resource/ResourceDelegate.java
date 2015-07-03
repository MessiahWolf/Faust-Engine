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

import core.world.WorldTemplate;
import core.world.WorldResource;
import core.event.DelegateEvent;
import core.event.DelegateListener;
import core.world.Actor;
import core.world.Animation;
import core.world.Picture;
import core.world.Backdrop;
import core.world.WorldCellLayer;
import core.world.WorldCell;
import core.world.World;
import core.world.Tileset;
import core.world.WorldEffect;
import core.world.WorldItem;
import core.world.WorldTile;
import core.world.WorldScript;
import io.util.FileSearch;
import io.util.FileUtils;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

public class ResourceDelegate {

    // Variable Declaration
    private ArrayList<DataRef> referenceList;
    private ArrayList<DataPackage> packageList;
    private HashMap<String, ResourceRequest> requestMap;
    private EventListenerList listenerList;
    // Project Classes
    private TemporaryQueue temporaryQueue;
    // Data Types
    private boolean generateEditorIds;
    private boolean resourcesLoaded;
    private boolean loadPackages;
    private boolean loadImages;
    private int scanMode = SCAN_BIASED;
    public static final int SCAN_UNBIASED = 0x001;
    public static final int SCAN_BIASED = 0x002;
    private String addonDirectory;
    private String dataDirectory;
    private String cacheDirectory;
    private String actorDirectory;
    private String itemDirectory;
    private String backdropDirectory;
    private String tilesetDirectory;
    private String animationDirectory;
    private String cellDirectory;
    private String worldDirectory;
    private String templateDirectory;
    private String scriptDirectory;
    private String pictureFolder;
    public static final String UNPACKAGED_STATEMENT = "No Association";
    public static final int ID_EDITOR_REFERENCE = 65760;
    public static final int ID_EDITOR_NAME = 65770;
    public static final int ID_EDITOR_DISPLAY = 65780;
    // End of Variable Declaration

    public ResourceDelegate() {

        // Initialize Loading
        listenerList = new EventListenerList();
        requestMap = new HashMap<>();
        referenceList = new ArrayList<>();
        packageList = new ArrayList<>();

        //
        temporaryQueue = new TemporaryQueue();
    }

    public void initialize(String dataFolder, int scanMode, boolean loadPackages, boolean generateEditorIds, boolean loadImages) {
        this.dataDirectory = dataFolder;
        this.scanMode = scanMode;
        this.loadPackages = loadPackages;
        this.generateEditorIds = generateEditorIds;
        this.loadImages = loadImages;

        //
        final File dataFile = new File(dataFolder);

        //
        if (dataFile.exists()) {
            makeStructure();
        }
    }

    public void addDataReference(DataRef reference) {

        // Do not allow duplicate entries by destroying the previous; it is sorta safe in the implementing classes that I create.
        fireEventNotifier(reference, DelegateEvent.REFERENCE_REMOVED);

        //
        String id = reference.getEditorId();

        // Cannot already contain it
        if (!containsReference(ID_EDITOR_REFERENCE, id)) {

            // ADD
            logChange(TemporaryQueue.FLAG_ADD, reference.getResource());

            // Place the resource in the resource map
            referenceList.add(reference);
        } else {

            // REPLACE
            logChange(TemporaryQueue.FLAG_REPLACE, reference.getResource());
        }

        // @FIRE STATE CHANGED
        fireEventNotifier(reference, DelegateEvent.REFERENCE_ADDED);
    }

    public void removeDataReference(DataRef reference) {

        // Must contain it
        if (referenceList.contains(reference)) {

            // Log the change in the queue
            logChange(TemporaryQueue.FLAG_REMOVE, reference.getResource());

            // Remove the reference from list
            referenceList.remove(reference);
        }

        // Fire event notifier to remove from implementing classes
        fireEventNotifier(reference, DelegateEvent.REFERENCE_REMOVED);
    }

    public void addResource(WorldResource resource) {

        // Add a reference to this new resource
        addDataReference(new DataRef(resource));
    }

    public void removeResource(WorldResource resource) {

        // Find it first
        final DataRef reference = echoReference(resource);

        // Remove it
        removeDataReference(reference);
    }

    public void addDataPackage(DataPackage dataPackage) {

        // Precheck
        if (getPackage(ID_EDITOR_REFERENCE, dataPackage.getReferenceId()) != null) {

            // Remove it first
            packageList.remove(dataPackage);

            // Remove the package before hand
            fireEventNotifier(new DataRef(dataPackage), DelegateEvent.PACKAGE_REMOVED);
        }

        // Add to the list of plugins
        packageList.add(dataPackage);

        // Create citations for the dataPackage
        makePackageReference(dataPackage);
    }

    public String generateID(int type, Class closs, int attempts, int maxAttempts) {

        // Prefix
        String prefix;

        // Determine prefix
        if (closs == DataPackage.class) {
            prefix = "DP";
        } else if (closs == Animation.class) {
            prefix = "AN";
        } else if (closs == Tileset.class) {
            prefix = "TS";
        } else if (closs == Actor.class) {
            prefix = "WA";
        } else if (closs == Backdrop.class) {
            prefix = "BD";
        } else if (closs == WorldEffect.class) {
            prefix = "WE";
        } else if (closs == WorldItem.class) {
            prefix = "WI";
        } else {
            prefix = "UK";
        }

        // Create the random ID
        final String createdID = String.valueOf(prefix + "" + UUID.randomUUID()).substring(0, 10);
        // Check if we contain that one
        if (containsReference(type, createdID)) {

            // Limited number of attempts
            if (attempts <= maxAttempts) {
                return generateID(type, closs, attempts++, maxAttempts).toUpperCase();
            } else {
                return null;
            }
        }

        //
        return createdID.toUpperCase();
    }

    public String createID(int attempts, int maxAttempts) {

        // Create the random ID
        final String createdId = String.valueOf("TEMP" + UUID.randomUUID()).substring(0, 10);

        // Check if we contain that one
        if (containsReference(ID_EDITOR_REFERENCE, createdId)) {

            // Limited number of attempts
            if (attempts <= maxAttempts) {
                return createID(attempts++, maxAttempts);
            } else {
                return null;
            }

        } else {
            return createdId;
        }
    }

    public String createReferenceID(DataPackage dataPackage, int attempts, int maxAttempts) {

        // Create a somewhat random 17 character ID
        final String createdId = String.valueOf("U" + UUID.randomUUID()).substring(0, 17).replaceAll("-", "");

        //
        if (containsReference(ID_EDITOR_REFERENCE, createdId)) {

            // Limited number of attempts
            if (attempts <= maxAttempts) {
                return createID(attempts++, maxAttempts);
            } else {
                return null;
            }

        } else {
            return createdId;
        }
    }

    // Use this method to load base objects into the editor -- no attributes; not ideal for actual game loading, instead use createReferenceRequest() method.
    public void makeRequest(String referenceID, WorldResource target) {

        // Create the new Resource Request
        final ResourceRequest request = new ResourceRequest(referenceID, target);

        // Force the target to acknowledge the request
        target.shadow(referenceID, request);

        // Add as a request to watch for. After loading
        requestMap.put(createID(0, 10), request);
    }

    // Use this method to load specific files using the SHA-1 Check Sum
    public void makeRequest(String referenceID, WorldResource target, String sha1CheckSum) {

        // Create the new Resource Request
        final ResourceRequest request = new ResourceRequest(referenceID, target);

        // Only difference is that the result will be the exact resource, not the first found.
        request.filter(sha1CheckSum);

        // Force the target to acknoledge the request
        target.shadow(referenceID, request);

        // Add as a request to watch for. After loading
        requestMap.put(createID(0, 10), request);
    }

    // Use this method to load a reference of a base object
    public void makeReferenceRequest(String referenceID, WorldResource target, HashMap<String, Object> attributes) {

        // Create the new Resource Request
        final ResourceRequest request = new ResourceRequest(referenceID, target, attributes);

        // Force the target to acknowledge the request
        target.shadow(referenceID, request);

        // Add as a request to watch for. After loading
        requestMap.put(createID(0, 10), request);
    }

    public void makePackageRequest(String packageID, String referenceID, WorldResource requestTarget) {

        // Create the request
        final ResourceRequest request = new ResourceRequest(packageID, referenceID, requestTarget);

        // Shadow that request
        requestTarget.shadow(referenceID, request);

        // Place the request under a temporary id
        requestMap.put(createID(0, 10), request);
    }

    // This method is called to create a reference from a file on the hard disk
    private void makeReference(File file) {

        // Object parsed from the file
        WorldResource resource = null;

        try {

            // Attempt a read
            resource = ResourceReader.read(this, file);
        } catch (Exception ioe) {
            System.err.println("Error creating citation for resource: " + ioe);
        }

        if (resource == null) {
            return;
        }

        // Add and create the new reference
        addDataReference(new DataRef(resource));
    }

    // This method is called to create a reference from a dataPackage
    private void makePackageReference(DataPackage dataPackage) {

        // First check to see if this reference is already contained in one of the visual classes (ResourceManager, GraphicsetManager)
        fireEventNotifier(new DataRef(dataPackage), DelegateEvent.PACKAGE_REMOVED);

        // Fire Package added event
        fireEventNotifier(new DataRef(dataPackage), DelegateEvent.PACKAGE_ADDED);
    }

    // Use the matching referenceID provided from creating the Reference request
    public void removeRequest(String referenceID) {

        // Check if we even have one by that name
        if (requestMap.containsKey(referenceID)) {
            requestMap.remove(referenceID);
        }

        // Check
        if (requestMap.isEmpty()) {
            // May throw a resources filled event soon.
        }
    }

    public void performScan(String dataFolder, boolean add, boolean acceptDataPackages) {

        // This is the location of the static Data Folder for Faust Files
        this.dataDirectory = dataFolder;
        this.loadPackages = acceptDataPackages;

        // Find the data Folder
        final File dataFile = new File(dataFolder);

        // Do a resource scan of data folder
        if (dataFile.exists()) {

            // Create the directory structure if non-existent
            makeStructure();

            if (add) {

                // !-Recursion; to scan subfolders for accepted resources
                performScan(dataFile, acceptDataPackages);

                // Aftercheck
                if (referenceList.isEmpty() && packageList.isEmpty()) {

                    // Show a failed to find resources message
                    JOptionPane.showMessageDialog(null, "Failed to find any resources.");
                }
            }
        } else {

            // Attempt to create the directory structure if it failed to find the base data folder.
            makeStructure();

            // Exit the application if we failed to locate the Data Folder; may soon show a file window to allow user to locate the directory.
            JOptionPane.showMessageDialog(null, "Could not find the Faust Resource directory\nProgram Exitting...");

            // Exit the application
            System.exit(0);
        }

        // We added resources :D
        if (add) {

            // Resource have been loaded
            resourcesLoaded = true;

            // Fire the event for all resources loaded in Data Folder
            fireEventNotifier(referenceList, DelegateEvent.FINISHED);
        }
    }

    // Use this method to scan the data directory for resources; Not to be confused with data importing. This one has no bias.
    private boolean performScan(File file, boolean acceptDataPackages) {

        // Sub Directory
        final File[] listFiles = file.listFiles();

        // Do not scan empty sub directories
        if (listFiles != null) {

            // The current list of files in the current sub directory
            for (int i = 0; i < listFiles.length; i++) {

                // Current file in list
                final File current = listFiles[i];

                // Grab the extension from the current file
                final String extension = FileUtils.getExtension(current);

                // Search recursively
                performScan(current, acceptDataPackages);

                // Check if this file is of an accepted type
                if (isResourceExtension(extension)) {

                    // Creates a resource for this file and closes handles afterwards
                    makeReference(current);
                } else if (isPackageExtension(extension)) {

                    // If you chose to accept data packages during the search
                    if (acceptDataPackages) {

                        // Create a package citation for it
                        makePackageReference(loadPackage(current, true));
                    }
                }
            }
        }

        // Kick out -- Do not remove this line or change to false
        return true;
    }

    private void makeStructure() {

        // Find the data directory
        if (new File(dataDirectory).exists()) {

            // Place folders in the data directory
            cacheDirectory = dataDirectory + File.separator + "temp";
            addonDirectory = dataDirectory + File.separator + "addons";

            // Out main folders
            final String[] folderArray = {cacheDirectory, addonDirectory};

            // Iterate
            for (int i = 0; i < folderArray.length; i++) {

                // Current File in list
                final File file = new File(folderArray[i]);

                // Create if not exists
                if (file.exists() == false) {

                    // Make the folder if it does not already exist
                    file.mkdir();
                }
            }

            // Ensure the directory structure
            performStructureCheck();
        }
    }

    public void performStructureCheck() {

        // Data folder must exist
        if (new File(dataDirectory).exists()) {

            // Temp folder must exist
            if (new File(cacheDirectory).exists()) {

                // Manually assign each one.
                actorDirectory = cacheDirectory + File.separator + "actor";
                itemDirectory = cacheDirectory + File.separator + "item";
                animationDirectory = cacheDirectory + File.separator + "animation";
                tilesetDirectory = cacheDirectory + File.separator + "tileset";
                backdropDirectory = cacheDirectory + File.separator + "backdrop";
                worldDirectory = cacheDirectory + File.separator + "world";
                cellDirectory = cacheDirectory + File.separator + "worldcell";
                templateDirectory = cacheDirectory + File.separator + "template";
                scriptDirectory = cacheDirectory + File.separator + "script";
                pictureFolder = cacheDirectory + File.separator + "pictures";

                // Array of folders
                final String[] folderArray = {actorDirectory, itemDirectory, animationDirectory, tilesetDirectory, backdropDirectory,
                    worldDirectory, cellDirectory, templateDirectory, scriptDirectory, pictureFolder};

                // Iterate
                for (int i = 0; i < folderArray.length; i++) {

                    // Grab current
                    final File file = new File(folderArray[i]);

                    // Create the directory if not exists
                    if (file.exists() == false) {

                        // Make the file directory
                        file.mkdir();
                    }
                }
            }
        }
    }

    public boolean exists(WorldResource resource) {

        // Should only be a single count for each resource
        final int count = getInstanceCount(resource);

        // Could return as integer value (0 for none found | 1 for if we found just one | 2 for if we found an extra entry)
        return (count == 1 ? true : false);
    }

    public DataRef[] detectConflictions() {

        //
        final ArrayList<DataRef> conflictList = new ArrayList<>();

        //
        for (int i = 0; i < referenceList.size(); i++) {

            //
            final DataRef reference = referenceList.get(i);
            final String referenceEditorId = reference.getEditorId();
            final String referenceDisplayName = reference.getDisplayName();
            final String referenceEditorName = reference.getEditorName();

            for (int j = i + 1; j < referenceList.size() - 1; j++) {

                //
                final DataRef current = referenceList.get(j);
                final String currentEditorId = current.getEditorId();
                final String currentDisplayName = current.getDisplayName();
                final String currentEditorName = current.getEditorName();

                // If any of them match we have a confliction
                if (reference != current) {
                    if (currentDisplayName.equalsIgnoreCase(referenceDisplayName)) {
                        conflictList.add(current);
                        conflictList.add(reference);
                        break;
                    } else if (currentEditorId.equalsIgnoreCase(referenceEditorId)) {
                        conflictList.add(current);
                        conflictList.add(reference);
                        break;
                    } else if (currentEditorName.equalsIgnoreCase(referenceEditorName)) {
                        conflictList.add(current);
                        conflictList.add(reference);
                        break;
                    }
                }
            }
        }

        //
        return conflictList.toArray(new DataRef[]{});
    }

    public DataRef[] detectConflictionsWith(DataRef reference) {

        //
        final ArrayList<DataRef> references = new ArrayList<>();

        //
        if (reference != null) {

            //
            final String referenceEditorId = reference.getEditorId();
            final String referenceDisplayName = reference.getDisplayName();
            final String referenceEditorName = reference.getEditorName();

            for (int i = 0; i < referenceList.size(); i++) {

                //
                final DataRef current = referenceList.get(i);
                final String currentEditorId = current.getEditorId();
                final String currentDisplayName = current.getDisplayName();
                final String currentEditorName = current.getEditorName();

                // If any of them match we have a confliction
                if (reference != current) {
                    if (currentDisplayName.equalsIgnoreCase(referenceDisplayName)) {
                        references.add(current);
                        continue;
                    } else if (currentEditorId.equalsIgnoreCase(referenceEditorId)) {
                        references.add(current);
                        continue;
                    } else if (currentEditorName.equalsIgnoreCase(referenceEditorName)) {
                        references.add(current);
                        continue;
                    }

                }
            }
        }

        //
        return references.toArray(new DataRef[]{});
    }

    public void flush() {

        // Iterate over packages
        for (int i = 0; i < packageList.size(); i++) {

            // The event notifier will tell the resource manager to empty all of its data packages
            fireEventNotifier(new DataRef(packageList.get(i)), DelegateEvent.PACKAGE_REMOVED);
        }

        // Iterate over loose references
        for (int i = 0; i < referenceList.size(); i++) {

            // The event notifier will tell the visual components to emtpy their collections of resources
            fireEventNotifier(referenceList.get(i), DelegateEvent.REFERENCE_REMOVED);
        }

        // Clear it all
        packageList.clear();
        referenceList.clear();

        // No resources are loaded now
        resourcesLoaded = false;
    }

    public void validate() {

        // If I had a better way It would be here. So deal with this or change it yourself, also respect the load order please.  :(

        // ! FImages are loaded by default inside of Data Packages and in the temp folder
        // Base resources
        loadRequestType(Tileset.class);
        loadRequestType(Animation.class);
        loadRequestType(Backdrop.class);

        // Script
        loadRequestType(WorldScript.class);

        // World Objects
        loadRequestType(WorldTile.class);
        loadRequestType(WorldItem.class);
        loadRequestType(Actor.class);

        // World Types
        loadRequestType(WorldCellLayer.class);
        loadRequestType(WorldCell.class);
        loadRequestType(World.class);

        // Templates Last
        loadRequestType(WorldTemplate.class);
    }

    public WorldResource loadExternalResource(File file) {

        // Use this method to load maps, actors, dlls, sounds, items.
        if (file == null) {
            return null;
        }

        // File must exist
        if (file.exists()) {

            // Check the file extension
            if (isResourceExtension(FileUtils.getExtension(file))) {

                try {

                    // Return what we read in; could be null depending on XML root name, not value
                    return ResourceReader.read(this, file);
                } catch (Exception ioe) {
                    System.err.println("Error loading external resource: " + ioe);
                }
            }
        }

        // Otherwise return null
        return null;
    }

    public DataPackage loadPackage(File file, boolean add) {

        // File must exist
        if (file == null) {
            return null;
        }

        // File must exist
        if (file.exists()) {

            // Make sure its the archive extension
            if (isPackageExtension(FileUtils.getExtension(file))) {

                // Make sure we don't already have this dataPackage loaded
                return ResourceReader.readPackage(this, file, add);
            }
        }

        //
        return null;
    }

    public void loadLoose() {

        // Temp folder must exist; should probally search entire data folder, not just assume temp. Bah!
        // If you want it to search the entire data folder change both instances of 'tempFolder' with 'dataFolder' in the below code
        if (new File(cacheDirectory).exists()) {

            // Iterate over it; This is a deep scan.
            final File[] list = FileUtils.getDirectoryContents(new File(cacheDirectory));

            // Iterate
            for (int i = 0; i < list.length; i++) {

                // Current file in list
                final File current = list[i];

                // Grab the extension
                final String extension = FileUtils.getExtension(current);

                // Grab the extension and do not load loose data packages
                if (extension.equalsIgnoreCase(ResourceReader.MW_ARCHIVE_EXTENSION) == false) {

                    // This is a valid resource extension
                    if (isResourceExtension(extension)) {

                        // Try
                        try {

                            // Grab the current resource
                            final WorldResource resource = ResourceReader.read(this, current);

                            // Resource must exist; we do not add null resources.
                            if (resource == null) {
                                return;
                            }

                            // Must not have a valid pluginid to be tagged as loose
                            if (resource.getPackageId().isEmpty()) {

                                // I Fixed a major performance issue with the creation of animations. Do not start an empty animaton with negative one repeat cycles and zero delay.
                                // Add the loose resource
                                addResource(resource);
                            }
                        } catch (Exception ioe) {
                            System.err.println("Failed to read file: " + ioe);
                        }
                    }
                }
            }
        }
    }

    public void loadLooseImages() {

        // Temp folder must exist; should probally search entire data folder, not just assume temp. Bah!
        // If you want it to search the entire data folder change both instances of 'tempFolder' with 'dataFolder' in the below code
        if (new File(pictureFolder).exists()) {

            // Iterate over it; This is a deep scan.
            final File[] list = FileUtils.getDirectoryContents(new File(pictureFolder));

            // Iterate
            for (int i = 0; i < list.length; i++) {

                // Current file in list
                final File current = list[i];

                // Grab the extension
                final String extension = FileUtils.getExtension(current);

                // Grab the extension and do not load loose data packages
                if (ResourceReader.isImageExtension(extension)) {

                    // Try
                    try {

                        //
                        final Image image = ResourceReader.readImage(current);

                        //
                        final Picture graphic = new Picture("", "", "", "", "", image);
                        graphic.validate();

                        //
                        addResource(graphic);
                    } catch (Exception ioe) {
                        System.err.println("Failed to read file: " + ioe);
                    }
                }
            }
        }
    }

    public void loadPackages() {

        // Find the addon directory and it must exist
        if (new File(addonDirectory).exists()) {

            // Deep search the addon folder
            final File[] list = FileUtils.getDirectoryContents(new File(addonDirectory));

            // Iterate over deep search
            for (int i = 0; i < list.length; i++) {

                // Read the plugins
                final File current = list[i];

                // Grab the extension
                final String extension = FileUtils.getExtension(current);

                // Ask
                if (extension.equalsIgnoreCase(ResourceReader.MW_ARCHIVE_EXTENSION)) {

                    // Read the dataPackage in
                    ResourceReader.readPackage(this, current, true);
                }
            }
        }
    }

    private void loadRequestType(Class closs) {

        // Load files by type
        for (Map.Entry<String, ResourceRequest> map : requestMap.entrySet()) {

            // Target
            final WorldResource target = map.getValue().getTarget();

            // Meep.
            if (target.getClass() == closs || closs.isAssignableFrom(target.getClass())) {

                // Iterate over the list of requests and fill them all
                fillRequest(map.getKey());
            }
        }
    }

    public void logChange(int flag, WorldResource resource) {
        temporaryQueue.logChange(resource, flag);
    }

    public void validateTemporary() {
        temporaryQueue.adjustTempFolder(this);
    }

    public void validateWorlds() {

        // Grab all the Worlds currently loaded
        final World[] worlds = (World[]) this.getType(World.class);

        // Iterate over the collection of world instances
        for (int i = 0; i < worlds.length; i++) {

            // Grab current world
            final World world = worlds[i];

            // Validate the world
            validateWorld(world);
        }
    }

    public void validateWorld(World world) {

        // Grab packageID to determine data package
        final String packageID = world.getPackageId();

        // No data package?
        if (packageID.isEmpty()) {

            // Create a File Search object
            final FileSearch search = new FileSearch(new File(dataDirectory), world.getReferenceName(), true);

            // Perform the search to populate the list
            search.perform();

            // Make sure the file doesn't already exist
            final File found = search.check(world.getSHA1CheckSum());

            //
            if (found != null) {

                //
                if (found.exists()) {
                    try {

                        // Delete previous
                        FileUtils.eraseFile(found);
                    } catch (IOException ioe) {
                        //
                    }
                }
            }

            // Save the world to temp
            ResourceWriter.write(this, world);

            // rewrite all the maps as well
            final ArrayList<WorldCell> cellList = world.getCellList();

            // Iterate over the worlds collection of cells
            for (int i = 0; i < cellList.size(); i++) {

                // Grab the world cell
                final WorldCell worldCell = cellList.get(i);

                // Write the cell out; overwrite if nessecary
                ResourceWriter.write(this, worldCell);
            }
        } else {

            // Find its data package
            final DataPackage pack = getPackage(ID_EDITOR_REFERENCE, packageID);

            //
            if (pack != null) {

                // Write the world out again, but to a temp
                final File file = ResourceWriter.write(this, world);

                // Replace the entry for the world with this updated one :)
                pack.replaceEntry(world.getReferenceID(), world, file);

                // Write all the maps as well
                final ArrayList<WorldCell> cellList = world.getCellList();

                //
                for (int i = 0; i < cellList.size(); i++) {

                    // Grab the world cell
                    final WorldCell worldCell = cellList.get(i);

                    // Write the map out to disk
                    final File worldCellFile = ResourceWriter.write(this, worldCell);

                    // Replace the entry in the data package that it is tied to with this updated one :)
                    pack.replaceEntry(worldCell.getReferenceID(), worldCell, worldCellFile);
                }
            }
        }
    }

    public void validatePackages() {

        // Iterate over the list of plugins
        for (int i = 0; i < packageList.size(); i++) {

            // Grab the current dataPackage
            final DataPackage dataPackage = packageList.get(i);

            // Apply all the recorded changes; adjusts all the content to the changes the user made during this run of the editor
            dataPackage.applyChanges(this);
        }
    }

    public DataRef echoReference(WorldResource resource) {

        // Iterate over entire reference list
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current reference
            final DataRef reference = referenceList.get(i);

            // Grab its resource
            final WorldResource current = reference.getResource();

            // Compare
            if (current == resource) {
                return reference;
            }
        }

        // We found nothing
        return null;
    }

    public DataRef echoPackage(DataPackage dataPackage) {

        // Iterate over entire reference list
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current reference
            final DataRef reference = referenceList.get(i);

            //
            final DataPackage current = reference.getPackage();

            //
            if (current != null) {

                // Compare
                if (current == dataPackage) {
                    return reference;
                }
            }
        }

        // We found nothing
        return null;
    }

    // Echoing a reference given a file is a little tricker; The delegate does not keep instances of files so we'll have to do
    // our own search using the FileSearch.java class and attempt to match sha1 Checksum generated on the spot with saved sha1CheckSum
    public DataRef echoFile(File file) {

        // Iterate over entire reference list
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current reference
            final DataRef reference = referenceList.get(i);

            // Grab its resource
            final WorldResource current = reference.getResource();

            try {

                //
                final String sha1CheckSum = current.getSHA1CheckSum();

                // Generate a checksum on the spot
                final String generatedCheckSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");

                // Compare
                if (generatedCheckSum.equals(sha1CheckSum)) {
                    return reference;
                }
            } catch (Exception e) {
                return null;
            }
        }

        // We found nothing
        return null;
    }

    public void fillRequest(String referenceID) {

        try {

            // Must be a valid request
            if (requestMap.containsKey(referenceID)) {

                // Grab the request
                final ResourceRequest request = requestMap.get(referenceID);

                //Fill that request
                allocateResource(request);
            }
        } catch (NullPointerException npe) {
            // Something in the resource request is unsatisfied
        }
    }

    private void allocateResource(ResourceRequest request) {

        // Request must exist
        if (request != null) {

            // The resource to find
            String referenceID;

            // Citation base
            DataRef reference;

            // Does this resource exist inside of a data package?
            if (request.isPackageRequest()) {

                // Find the dataPackage; the request's referenceID would actually be the referenceID of the data package
                final DataPackage dataPackage = getPackage(ID_EDITOR_REFERENCE, request.getEditorId());

                // Find the requested resource inside the dataPackage
                reference = dataPackage.findByEditorId(request.getPluginRequest());

                // Set referenceID
                referenceID = request.getPluginRequest();
            } else {

                // Filtered means it was provided with the optional checksum
                if (request.isFiltered()) {

                    // Find by checksum of file
                    reference = findCheckSum(request.getOptionalCheckSum());
                } else {

                    // This file is loose and can be simply found by searching the non dataPackage files
                    reference = getReference(ID_EDITOR_REFERENCE, request.getEditorId());
                }

                // Set referenceID
                referenceID = request.getEditorId();
            }

            // Extract the target
            final WorldResource target = request.getTarget();

            // Send the resource to the target :)
            target.receive(referenceID, (WorldResource) reference.getResource());

            // Remove the request after sending
            requestMap.remove(referenceID);
        }
    }

    public DataPackage getPackageForResource(WorldResource resource) {

        // @null-check
        if (resource == null) {
            return null;
        }

        // Plugin check
        final String packageID = resource.getPackageId();

        // Grab all the plugins
        final DataPackage[] plugins = getDataPackages();

        // Iterate over the list of ResourcePlugins
        for (int i = 0; i < plugins.length; i++) {

            // Grab the current ResourcePlugin
            final DataPackage pack = plugins[i];

            // If and only if dataPackage exists
            if (packageID != null) {

                // Quick check
                if (pack.getReferenceId().equalsIgnoreCase(packageID)) {

                    //
                    return pack;
                }
            }
        }

        //
        return null;
    }

    public int getInstanceCount(WorldResource resource) {

        //
        if (resource == null) {
            return 0;
        }

        //
        int count = 0;

        // Iterate over the Citation List
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current reference
            final DataRef reference = referenceList.get(i);

            //
            final WorldResource current = reference.getResource();

            //
            if (current.getReferenceName().equalsIgnoreCase(resource.getReferenceName())) {
                count++;
            }
        }

        //
        return count;
    }

    public Object[] getLooseType(Class closs) {

        // Base case
        if (closs == null) {
            return null;
        }

        // ArrayList output
        final ArrayList<Object> output = new ArrayList<>();

        // Iterate over the reference list
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current Citation
            final DataRef reference = referenceList.get(i);

            // Grab the current resource
            final Object resource = reference.getResource();

            // Ask
            if (resource.getClass() == closs || resource.getClass().isAssignableFrom(closs)) {

                // Only return loose files.
                if (reference.getPackage() == null || reference.getPackageId().isEmpty()) {

                    // Never actually give out the base object
                    output.add(resource);
                }
            }
        }

        // Return the found resources of type:
        return output.toArray();
    }

    public Object[] getType(Class type) {

        // ArrayList output
        final ArrayList<Object> output = new ArrayList<>();

        // Iterate over the reference list
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current Citation
            final DataRef reference = referenceList.get(i);

            // Grab the current resource
            final Object resource = reference.getResource();

            // Ask
            if (resource.getClass() == type || resource.getClass().isAssignableFrom(type)) {

                // Never actually give out the base object
                output.add(resource);
            }
        }

        // Return the found resources of type:
        return output.toArray();
    }

    public DataPackage getPackage(int type, String id) {

        //
        if (id == null || id.isEmpty()) {
            return null;
        }

        // Iterate over the Plugin List
        for (int i = 0; i < packageList.size(); i++) {

            // Grab the current dataPackage
            final DataPackage pack = packageList.get(i);

            switch (type) {
                case ID_EDITOR_REFERENCE:
                    // Now Ask
                    if (pack.getReferenceId().equals(id)) {
                        return pack;
                    }
                case ID_EDITOR_NAME:
                    if (pack.getReferenceName().equals(id)) {
                        return pack;
                    }
                case ID_EDITOR_DISPLAY:
                    if (pack.getDisplayName().equals(id)) {
                        return pack;
                    }
            }
        }

        //
        return null;
    }

    public DataRef getReference(int type, String id) {
        //
        if (id == null || id.isEmpty()) {
            return null;
        }

        // Iterate over the Citation List
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current Citation
            final DataRef reference = referenceList.get(i);

            switch (type) {
                case ID_EDITOR_REFERENCE:
                    // Now Ask
                    if (reference.getEditorId().equals(id)) {
                        return reference;
                    }
                case ID_EDITOR_NAME:
                    if (reference.getEditorName().equals(id)) {
                        return reference;
                    }
                case ID_EDITOR_DISPLAY:
                    if (reference.getDisplayName().equals(id)) {
                        return reference;
                    }
            }

        }

        // Otherwise there is no reference by that reference id
        return null;
    }

    public HashMap<String, ResourceRequest> getRequestMap() {
        return requestMap;
    }

    public void printRequestMap() {

        //
        for (Map.Entry<String, ResourceRequest> m : requestMap.entrySet()) {

            //
            String tempKey = m.getKey();
            ResourceRequest request = (ResourceRequest) m.getValue();

            //
            System.err.println("TP Print: (Key): " + tempKey + " | (Value): " + request.getTarget().getReferenceName());
        }
    }

    public DataRef findCheckSum(String checkSum) {

        //
        if (checkSum == null || checkSum.length() == 0) {
            return null;
        }

        // Iterate over the Citation List
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current Citation
            final DataRef reference = referenceList.get(i);

            // Grab the current resource
            final WorldResource resource = reference.getResource();

            // Now ask
            if (resource.getSHA1CheckSum().equals(checkSum)) {
                return reference;
            }
        }

        // Otherwise there is no reference by that editor id
        return null;
    }

    public boolean containsReference(int type, String id) {

        //
        if (id == null || id.isEmpty()) {
            return false;
        }

        // Iterate over the Citation List
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab the current reference
            final DataRef reference = referenceList.get(i);

            switch (type) {
                case ID_EDITOR_REFERENCE:
                    // Now Ask
                    if (reference.getEditorId().equals(id)) {
                        return true;
                    }
                case ID_EDITOR_NAME:
                    if (reference.getEditorName().equals(id)) {
                        return true;
                    }
                case ID_EDITOR_DISPLAY:
                    if (reference.getDisplayName().equals(id)) {
                        return true;
                    }
            }
        }

        // Otherwise there is no reference by that editor id
        return false;
    }

    public boolean isResourceExtension(String string) {

        // Pre-check
        if (string == null || string.length() == 0) {
            return false;
        }

        // Check for accepted Resource Reader Extensions
        for (int i = 0; i < ResourceReader.getReaderFormatNames().length; i++) {
            if (string.equals(ResourceReader.getReaderFormatNames()[i])) {
                return true;
            }
        }

        // Return false otherwise
        return false;
    }

    public boolean isPackageExtension(String string) {

        // Pre-check
        if (string == null || string.length() == 0) {
            return false;
        }

        // Check
        if (string.equals(ResourceReader.MW_ARCHIVE_EXTENSION)) {
            return true;
        }

        // Return false otherwise
        return false;
    }

    public boolean isAcceptableID(int type, String id) {

        // Base case
        if (id == null || id.isEmpty()) {
            return false;
        }

        // Conflictions count as not valid
        if (isConflicted(type, id)) {
            return false;
        }

        //
        switch (type) {
            case ID_EDITOR_REFERENCE:
                return isValid("~,`!@#$%^&*()-_=+|}{][;':<>,./?\" ", id);
            case ID_EDITOR_DISPLAY:
                return isValid("\\<>()*&^%$#@!?+-~`';.,:[]{}\"", id);
            case ID_EDITOR_NAME:
                return isValid("\\<>()*&^%$#@!~?+=`';,:[]{}\" ", id);
            default:
                return true;
        }
    }

    private boolean isValid(String regex, String string) {

        // Base case
        if (string == null) {
            return false;
        }

        // Base Case 2
        if (string.length() == 0) {
            return false;
        }

        //
        for (int i = 0; i < regex.length(); i++) {
            if (string.contains(String.valueOf(regex.charAt(i)))) {
                return false;
            }
        }

        // Invalid Strings
        final String[] invalid = getInvalidStrings();

        // Secondary check
        for (int i = 0; i < invalid.length; i++) {
            if (string.equalsIgnoreCase(invalid[i])) {
                return false;
            }
        }

        // Is a valid resource name otherwise
        return true;
    }

    /*
     * I may add a database containing a list of inappropiate words as well as locally invalid
     */
    public static String[] getInvalidStrings() {
        return new String[]{ResourceDelegate.UNPACKAGED_STATEMENT, "Content", "Scenery", "WorldCellLayer", "WorldCell",
            "WorldInstance", "WorldTile", "WorldBackground", "AnimatedSprite", "DataPackage", "", " ",
            "WorldActor", "WorldItem", "WorldEffect", "WorldTemplate", "temporary", "temp", "null", "undefined", "manifest", "manifest.xml"};
    }

    public boolean isLoaded() {
        return resourcesLoaded;
    }

    public boolean isConflicted(int type, String id) {

        //
        if (id == null || id.isEmpty()) {
            return false;
        }

        // Our return value
        boolean conflicted = false;
        int count = 0;

        //
        for (int i = 0; i < referenceList.size(); i++) {

            // Grab current data reference;
            final DataRef reference = referenceList.get(i);

            //
            switch (type) {
                case ID_EDITOR_REFERENCE:

                    // Ask
                    if (reference.getEditorId().equals(id)) {
                        count++;
                    }
                case ID_EDITOR_DISPLAY:

                    // Ask
                    if (reference.getDisplayName().equals(id)) {
                        count++;
                    }
                case ID_EDITOR_NAME:

                    // Ask
                    if (reference.getEditorName().equals(id)) {
                        count++;
                    }
            }

            //
            conflicted = count > 0 ? true : false;
        }

        //
        return conflicted;
    }

    // <editor-fold defaultstate="collapsed" desc="Mutators and Accesors">
    public void setLoadPackages(boolean loadPackages) {
        this.loadPackages = loadPackages;
    }

    public void setLoadImages(boolean loadImages) {
        this.loadImages = loadImages;
    }

    public void setScanMode(int scanMode) {
        this.scanMode = scanMode;
    }

    public void setGenerateEditorIds(boolean generateEditorIds) {
        this.generateEditorIds = generateEditorIds;
    }

    public boolean isLoadingPackages() {
        return loadPackages;
    }

    public boolean isLoadingImages() {
        return loadImages;
    }

    public boolean isGeneratingEditorIds() {
        return generateEditorIds;
    }

    public TemporaryQueue getTemporaryQueue() {
        return temporaryQueue;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public String getCacheDirectory() {
        return cacheDirectory;
    }

    public String getActorDirectory() {
        return actorDirectory;
    }

    public String getItemDirectory() {
        return itemDirectory;
    }

    public String getAddonDirectory() {
        return addonDirectory;
    }

    public String getBackdropDirectory() {
        return backdropDirectory;
    }

    public String getTilesetDirectory() {
        return tilesetDirectory;
    }

    public String getCellDirectory() {
        return cellDirectory;
    }

    public String getWorldDirectory() {
        return worldDirectory;
    }

    public String getAnimationDirectory() {
        return animationDirectory;
    }

    public String getTemplateDirectory() {
        return templateDirectory;
    }

    public String getScriptDirectory() {
        return scriptDirectory;
    }

    public int getScanMode() {
        return scanMode;
    }

    public DataPackage[] getDataPackages() {
        return packageList.toArray(new DataPackage[]{});
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Event Notifiers">
    public void addDelegateListener(DelegateListener source) {
        listenerList.add(DelegateListener.class, source);
    }

    public void removeDelegateListener(DelegateListener source) {
        listenerList.remove(DelegateListener.class, source);
    }

    private void fireEventNotifier(Object resource, int action) {

        // The actual event
        final DelegateEvent event = new DelegateEvent(resource);

        // Grab a quick copy of the listeners
        final Object[] tempListeners = Arrays.copyOf(listenerList.getListenerList(), listenerList.getListenerList().length);

        // Iterate
        for (int i = 0; i < tempListeners.length; i += 2) {

            // Must be a Delegate listener
            if (tempListeners[i] == DelegateListener.class) {

                // Grab the implementing class here
                final DelegateListener listener = (DelegateListener) tempListeners[i + 1];

                // Depends on the action
                switch (action) {
                    case DelegateEvent.PACKAGE_ADDED:
                        listener.packageAdded(event);
                        break;
                    case DelegateEvent.PACKAGE_REMOVED:
                        listener.packageRemoved(event);
                        break;
                    case DelegateEvent.REFERENCE_ADDED:
                        listener.referenceAdded(event);
                        break;
                    case DelegateEvent.REFERENCE_REMOVED:
                        listener.referenceRemoved(event);
                        break;
                    case DelegateEvent.FINISHED:
                        listener.dataLoaded(event);
                        break;
                }
            }
        }
    } //</editor-fold>
}
