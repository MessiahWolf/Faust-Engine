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
package io.resource;

import core.world.WorldResource;
import io.util.FileSearch;
import io.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a set of instructions for a modified Resource plug-in to make
 * the un-zipping and re-zipping as painless as possible, by keeping a small log
 * of the change made and how to commit the change to the archive in a single
 * pass.
 *
 * @author Robert A. Cherry (Messiah Wolf)
 */
public class TemporaryQueue {

    // Variable Delcaration
    private final HashMap<Integer, WorldResource> changeMap;
    // Data Types
    public static final int FLAG_ADD = 0x001;
    public static final int FLAG_REMOVE = 0x002;
    public static final int FLAG_REPLACE = 0x003;
    // End of Variable Declaration

    public TemporaryQueue() {

        // Instantiate the hashmap
        changeMap = new HashMap<>();
    }

    public void logChange(WorldResource resource, int flag) {

        // Store by the old id to change, and the new resource to be replaced
        changeMap.put(flag, resource);

        //
        //printChanges();
    }

    public void adjustTempFolder(ResourceDelegate delegate) {

        // Grab all the instructions and give it over to the dataPackage utils
        for (Map.Entry<Integer, WorldResource> map : changeMap.entrySet()) {

            // Current change to make
            final int flag = map.getKey();

            //
            final WorldResource resource = map.getValue();

            // Describe the change to be made
            if (flag == FLAG_ADD) {

                // A simple add operation
                ResourceWriter.write(delegate, resource);
            } else if (flag == FLAG_REMOVE) {

                //
                final FileSearch search = new FileSearch(new File(delegate.getCacheDirectory()), resource.getReferenceName(), true);

                // Perform the search
                search.perform();

                // Find the file
                final File found = search.check(resource.getSHA1CheckSum());

                // Cannot delete what we didn't find
                if (found != null) {
                    // Delete the file
                    try {
                        FileUtils.eraseFile(found);
                    } catch (IOException ioe) {
                        // Throw an error soon
                    }
                }
            } else if (flag == FLAG_REPLACE) {

                //
                final FileSearch search = new FileSearch(new File(delegate.getCacheDirectory()), resource.getReferenceName(), true);

                // Perform the search
                search.perform();

                // Find the file
                final File found = search.check(resource.getSHA1CheckSum());

                // Did we find it?
                if (found != null) {

                    // Delete the file
                    try {
                        FileUtils.eraseFile(found);
                    } catch (IOException ioe) {
                        // Throw an error soon
                    }
                }

                // Simple write out to file
                ResourceWriter.write(delegate, resource);
            }
        }

        // Clear the change map
        changeMap.clear();
    }

    public boolean hasChanges() {
        return changeMap.isEmpty();
    }

    public HashMap<Integer, WorldResource> getChanges() {
        return changeMap;
    }

    public void printChanges() {
        
        //
        for (Map.Entry<Integer, WorldResource> changes : changeMap.entrySet()) {
            System.out.println("Change Detected: " + changes.getKey() + " | " + changes.getValue().getDisplayName());
        }
    }
}
