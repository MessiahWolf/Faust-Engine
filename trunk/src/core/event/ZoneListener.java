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

import java.util.EventListener;

/**
 *
 * @author Robert A. Cherry
 */
public interface ZoneListener extends EventListener {

    public void zoneEntered(ZoneEvent newEvent);

    public void zoneExited(ZoneEvent newEvent);

    public void zoneMoved(ZoneEvent newEvent);

    public void zoneReshaped(ZoneEvent newEvent);
}
