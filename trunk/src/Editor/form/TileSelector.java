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
package Editor.form;

import core.world.Tileset;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author robert
 */
public class TileSelector implements ActionListener {

    // Variable Declaration
    // Java Native Classes
    private JScrollPane scrollPane;
    // Project Classes
    private Tileset tileset;
    private WorldCanvas worldCanvas;
    // End of Variable Declaration
    
    public TileSelector() {
        scrollPane = new JScrollPane();
    }

    public void setWorldCanvas(final WorldCanvas worldCanvas) {
        this.worldCanvas = worldCanvas;
    }

    public void setTileset(final Tileset tileset) {
        this.tileset = tileset;
    }

    public JScrollPane getComponent() {

        //
        final JPanel buttonJPanel = new JPanel();
        buttonJPanel.setLayout(new BoxLayout(buttonJPanel, BoxLayout.PAGE_AXIS));
        
        // Adjust the JScrollPane
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        //
        int height = 0;

        //
        final Dimension buttonDimension = new Dimension(32, 32);

        if (tileset != null) {

            //
            for (int i = 0; i < tileset.length; i++) {

                // Create the JButtons
                final JButton button = new JButton();
                button.setName(String.valueOf(i));
                button.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
                button.setPreferredSize(buttonDimension);
                button.setMaximumSize(buttonDimension);
                button.setSize(buttonDimension);
                button.setMinimumSize(buttonDimension);
                button.setContentAreaFilled(false);

                // Add to the width
                height += buttonDimension.height;

                // Set the graphic as the image of the tile, not just a default image
                button.setIcon(new ImageIcon(tileset.images[i]));
                button.addActionListener(this);

                // Add to this layout
                buttonJPanel.add(button);
                buttonJPanel.add(Box.createVerticalStrut(4));
            }
        }

        // Dimension for JScrollPane
        final Dimension paneDimension = new Dimension(36, height);

        //
        scrollPane.setPreferredSize(paneDimension);
        scrollPane.setMaximumSize(paneDimension);
        scrollPane.setMinimumSize(paneDimension);
        scrollPane.setSize(paneDimension);
        scrollPane.revalidate();
        
        //
        scrollPane.setViewportView(buttonJPanel);

        //
        return scrollPane;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {

        // Grab the object
        final Object obj = evt.getSource();

        // Cast to JButton
        final JButton sourceJButton = (JButton) obj;

        try {

            // Grab the index selected
            final int tileChosen = Integer.parseInt(sourceJButton.getName());

            // Soon -- communicate with fMap canvas
            if (worldCanvas != null) {
                if (tileset != null) {
                    worldCanvas.setTile(tileset, tileChosen);
                }
            }
        } catch (NumberFormatException nfe) {
            System.err.println("Failed to parse line " + sourceJButton.getName());
        }
    }
}
