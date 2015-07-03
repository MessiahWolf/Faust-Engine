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

import io.util.PackageUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class is a set of instructions for a modified Resource plug-in to make
 * the un-zipping and re-zipping as painless as possible, by keeping a small log
 * of the change made and how to commit the change to the archive in a single
 * pass.
 *
 * @author Robert A. Cherry (Messiah Wolf)
 */
public class PackageQueue {

    // Variable Delcaration
    private HashMap<Pair, File> changeMap;
    // Data Types
    public static final int FLAG_ADD = 0x001;
    public static final int FLAG_REMOVE = 0x002;
    public static final int FLAG_REPLACE = 0x003;
    // End of Variable Declaration

    public PackageQueue() {

        // Instantiate the hashmap
        changeMap = new HashMap<>();
    }

    public void logChange(String previousId, File file, int flag) {

        // Create a new pair
        final Pair<String, Integer> pair = new Pair<>(previousId, flag);

        // Store by the old id to change, and the new resource to be replaced
        changeMap.put(pair, file);
    }

    public void adjustArchive(ResourceDelegate delegate, DataPackage dataPackage) {

        // Grab all the instructions and give it over to the dataPackage utils
        try {
            
            // Modift the archive
            PackageUtils.modify(delegate, dataPackage, this);
        } catch (IOException ioe) {
            //
        }
    }

    public boolean hasChanges() {
        return changeMap.isEmpty() ? false : true;
    }

    public HashMap<Pair, File> getChanges() {
        return changeMap;
    }

    public class Pair<K, V> {

        // Variable Declaration
        private K value1;
        private V value2;
        // End of Variable Declaration

        Pair(K value1, V value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        public K getFirstValue() {
            return value1;
        }

        public V getSecondValue() {
            return value2;
        }
    }
}
