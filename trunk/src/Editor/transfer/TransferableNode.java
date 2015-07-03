/**
    Copyright (c) 2013, Robert Cherry    
    
    All rights reserved.
  
    This file is part of the Faust Editor.

    The Faust Editor is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Faust Editor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with The Faust Editor.  If not, see <http://www.gnu.org/licenses/>.
*/
package Editor.transfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Robert A. Cherry
 */
public class TransferableNode implements Transferable {

    // Variable Declaration
    public static DataFlavor nodeFlavor = createFlavor(DefaultMutableTreeNode.class);
    private DataFlavor chosenFlavor;
    private DataFlavor[] supportedFlavors = {nodeFlavor};
    private DefaultMutableTreeNode resourceNode;
    // Project classes
    // End of Variable Declaration

    public TransferableNode(DefaultMutableTreeNode newNode) {

        //
        resourceNode = newNode;

        //
        chosenFlavor = nodeFlavor;
    }

    public static DataFlavor createFlavor(Class closs) {

        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType
                    + ";class=\""
                    + closs.getName()
                    + "\"";
            return new DataFlavor(mimeType);
        } catch (ClassNotFoundException cnfe) {
            //
        }

        //
        return null;
    }

    public DataFlavor getChosenFlavor() {
        return chosenFlavor;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {

        // Check
        if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        } else if (nodeFlavor.equals(flavor)) {
            return resourceNode;
        }

        //
        return null;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return nodeFlavor.equals(flavor);
    }
}
