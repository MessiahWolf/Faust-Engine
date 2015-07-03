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
package core.event;

import java.util.EventObject;

/**

 @author Robert A. Cherry
 */
public class MapEvent extends EventObject {

    // Variable Declaration
    // Data Types
    private int currentAction;
    public final static int ADDED = 0;
    public final static int REMOVED = 1;
    public final static int MODIFIED = 2;
    public final static int SORTED = 3;
    // End of Variable Declaration

    public MapEvent(Object newSource, int newAction) {
        super(newSource);

        // Set Current Action
        currentAction = newAction;
    }

    public int getStateChange() {
        return currentAction;
    }
}
