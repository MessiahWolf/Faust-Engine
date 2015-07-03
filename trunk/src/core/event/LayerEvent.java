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
public class LayerEvent extends EventObject {

    // Variable Declaration
    // Data Types
    private int action;
    public static final int ADDED = 0;
    public static final int REMOVED = 1;
    public static final int SORTED = 2;
    public static final int FLAG_MODIFIED = 3;
    // End of Variable Declaration

    public LayerEvent(Object source, int action) {
        super(source);

        //
        this.action = action;
    }

    public int getStateChange() {
        return action;
    }
}
