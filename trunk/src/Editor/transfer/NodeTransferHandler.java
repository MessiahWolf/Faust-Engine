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

import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Robert A. Cherry
 */
public class NodeTransferHandler extends TransferHandler {

    @Override
    protected Transferable createTransferable(JComponent component) {

        // Grab the JTree
        final JTree tree = (JTree) component;

        // Grab the last selected path
        final TreePath[] paths = tree.getSelectionPaths();

        // Null check
        if (paths != null) {

            // Grab the selected object
            final Object object = tree.getLastSelectedPathComponent();

            //
            if (object instanceof DefaultMutableTreeNode) {
                
                //
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                // Create a new transferable object
                return new TransferableNode(node);
            }
        }

        // Return null if path is unselected
        return null;
    }

    @Override
    public int getSourceActions(JComponent comp) {
        return COPY;
    }
}
