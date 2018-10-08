/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author rcher
 */
public class PolygonFromImage extends javax.swing.JFrame {

    // Variable Declaration
    // Data Types
    private int transLean;
    // Java Native Classes
    private ArrayList<Polygon> polygonList;
    private ArrayList<ArrayList> pair3List;
    private BufferedImage image;
    //private Polygon polygon;
    //private final Random generator = new Random(0);
    // Java Swing Classes
    private JPanel panel;
    // End of Variable Declaration

    public PolygonFromImage() {
        initComponents();
        init();
    }

    private void init() {
        //
        final Toolkit kit = Toolkit.getDefaultToolkit();

        // Create the JPanel here.
        panel = new JPanel() {
            @Override
            public void paint(Graphics monet) {
                super.paintComponent(monet);
                customPaint(monet);
            }
        };

        // Create it here so we can calculate and reserve some array space.
        pair3List = new ArrayList();
        polygonList = new ArrayList();

        // Setting up the panel.
        panel.setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));

        // So the viewport will stretch if the user loads their own image larger than the default viewport size.
        mainJScrollPane.setViewportView(panel);

        // Position the frame
        setLocation(kit.getScreenSize().width / 2 - getWidth() / 2, kit.getScreenSize().height / 2 - getHeight() / 2);
        setTitle("Polygon Boundary Maker");
    }

    // The next goal is to search for separation in X to further separate chunks of pixels.
    private void polygonize(BufferedImage image) {

        // Null case
        if (image == null) {
            return;
        }

        // Store some values for use
        final int iWidth = image.getWidth(this);
        final int iHeight = image.getHeight(this);

        // So terrain is essentially all the space until the next full line of transparency.
        // Otherwise assume the whole polygon is connected.
        ArrayList<Pair3> terrain = new ArrayList();

        // Go down y-coord and sweep left for first non-trans pixel then right for first non-trans pixel.
        for (int yPixel = 0; yPixel < iHeight; yPixel++) {

            int minX = -1;
            // Now we're going to search for min and max width values
            // Initially in this example we're solely using transparent pixels
            // but we can bit-wise push to account for any user selected color.
            // Go from the left first then stop at first not transparent pixel
            for (int xPixel = 0; xPixel < iWidth; xPixel++) {
                // If the current pixel is not transparent.
                if (!isTransparent(image.getRGB(xPixel, yPixel))) {
                    minX = xPixel;
                    break;
                }

                // If we've made it to the width of the image and every pixel has been transparent move down in y.
                if (xPixel == iWidth - 1 && minX == -1) {

                    // Oh and don't add transparent lines to the pair3List.
                    if (terrain.size() > 0) {

                        // Save current terrain and start a new one.
                        pair3List.add(terrain);
                        terrain = new ArrayList();
                    }
                }
            }

            // Now from the other side
            for (int xPixel = iWidth - 1; xPixel >= 0; xPixel--) {
                // If the current pixel is not transparent.
                if (!isTransparent(image.getRGB(xPixel, yPixel))) {
                    terrain.add(new Pair3(minX, xPixel, yPixel));

                    // Ask if we're at the end and add it.
                    if (yPixel == iHeight - 1) {
                        pair3List.add(terrain);
                    }

                    // Break
                    break;
                }
            }
        }

        // So we take our pair of min, max, and y and make a polygon from that.
        for (ArrayList<Pair3> n : pair3List) {

            // Takes terrain which is all pixels ascrending down y-coord until transparency is found.
            connectDots(n);
        }
    }

    private void connectDots(ArrayList<Pair3> terrainList) {
        // My idea is to cut array of points in half.
        // and then essentially flattening out the cut in half sides of the array.
        // To do that we grab every other point and assign it to the right half and the other to the left.
        // These would be our min on left and max on right.
        int lIndex = 0;
        int rIndex = 0;
        // If the TerrainList has an odd number of points. Assign extra space to the left array.
        // I'm just shooting code out my ass at this point. If it errors out, I'll fix it.
        final Pair3[] arrLeft = new Pair3[(terrainList.size() % 2 == 1) ? (terrainList.size() / 2) + 1 : terrainList.size() / 2];
        final Pair3[] arrRight = new Pair3[terrainList.size() - arrLeft.length];

        // Our polygon. :)
        final Polygon polygon = new Polygon();

        // Every other is assigned. So it'll go MinX, MaxX, MinX, etc.
        // because that's how the points were stored in the polygonize() method, alternating.
        for (int i = 0; i < terrainList.size(); i++) {
            if (i % 2 == 0) {
                arrLeft[lIndex] = terrainList.get(i);
                lIndex++;
            } else {
                arrRight[rIndex] = terrainList.get(i);
                rIndex++;
            }
        }

        // Forwards from MaxX; essentially clockwise
        // So think of a circle with minX's on the left and maxX's on the right side.
        // We cut that in half so minX's are separate from maxX.
        // Then move clockwise because of how polygons work.
        for (Pair3 p : arrRight) {
            polygon.addPoint(p.max, p.y);
        }

        // Backwards from minX
        for (int i = arrLeft.length - 1; i >= 0; i--) {
            final Pair3 p = arrLeft[i];
            polygon.addPoint(p.min, p.y);
        }

        // Add this complete polygon to the polygon list.
        polygonList.add(polygon);

        // Repaint that bitch!!! We're almost done.
        repaint();
    }

    private void customPaint(Graphics monet) {

        // If the image exists.
        if (image != null) {

            // First and foremost draw the Image
            //monet.drawImage(image, panel.getWidth() - image.getWidth(this), panel.getHeight() - image.getHeight(this), this);
            monet.drawImage(image, 0, 0, this);
        }

        // We're going to use Hoang's random seed color code snippet (we stole it from him)
        //monet.setColor(new Color((int) (generator.nextFloat() * 255), (int) (generator.nextFloat() * 255), (int) (generator.nextFloat() * 255)));
        // Not this time though; it looks awful over the top of the images.
        monet.setColor(Color.RED);

        // Draw our polygons. WE MADE IT!!!
        for (Polygon p : polygonList) {
            ((Graphics2D) monet).draw(p);
        }
    }

    private boolean isTransparent(int testPixel) {

        // This pixel is completely transparent
        // So I started this code after afew weeks of reasearch.
        // Basically we're pushing past the initial ARGB pixels each with a record size of 8
        // So ARGB is like 32 bits. 8 for A, R, G, and B. So we bitwise push to the left (>>) because the
        // arranement is B, G, R, A until we arrive at the 8 bits of alpha and the 0xFF removes
        // The rest of those bits (RGB) and leaves us with just the 8 alpha bits.
        // At least that's how I understood it when I wrote this a couple years ago.
        // Lean is how much alpha we want to accept as transparent because the alpha pixels might not be
        // completely transparent.
        return ((testPixel >> 24) & 0xFF) <= transLean;
    }

    private BufferedImage bufferImage(Image image, ImageObserver obs, int type) {

        //
        int imageWidth = image.getWidth(obs);
        int imageHeight = image.getHeight(obs);

        // Try to force it this way
        if (imageWidth <= 0 || imageHeight <= 0) {
            final ImageIcon icon = new ImageIcon(image);
            imageWidth = icon.getIconWidth();
            imageHeight = icon.getIconHeight();
        }

        // Shell for the new BufferedImage
        final BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, type);

        // Create the raster space
        final Graphics2D manet = bufferedImage.createGraphics();

        // Draw the image and then Close it
        manet.drawImage(image, 0, 0, obs);
        manet.dispose();

        // Return the image
        return bufferedImage;
    }
    
    public Polygon[] getPolygon() {
        return polygonList.toArray(new Polygon[]{});
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonJPanel = new javax.swing.JPanel();
        polgonJButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        selectJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        closeJButton = new javax.swing.JButton();
        mainJScrollPane = new javax.swing.JScrollPane();
        leanJPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        leanJSlider = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));

        polgonJButton.setText("Polygonize");
        polgonJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        polgonJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        polgonJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        polgonJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                polgonJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(polgonJButton);
        buttonJPanel.add(filler1);

        selectJButton.setText("Select");
        selectJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        selectJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        selectJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        selectJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(selectJButton);
        buttonJPanel.add(filler2);

        closeJButton.setText("Close");
        closeJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        closeJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        closeJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        closeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(closeJButton);

        mainJScrollPane.setMaximumSize(new java.awt.Dimension(297, 230));
        mainJScrollPane.setMinimumSize(new java.awt.Dimension(297, 230));

        leanJPanel.setLayout(new javax.swing.BoxLayout(leanJPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText("Tolerance:");
        leanJPanel.add(jLabel1);
        leanJPanel.add(filler3);

        leanJSlider.setMaximum(255);
        leanJSlider.setToolTipText("8");
        leanJSlider.setValue(8);
        leanJSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                leanJSliderStateChanged(evt);
            }
        });
        leanJPanel.add(leanJSlider);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addComponent(mainJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(leanJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 215, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(leanJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectJButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_selectJButtonActionPerformed

    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeJButtonActionPerformed
        // Clear those polygons.
        pair3List.clear();
        polygonList.clear();

        //
        setVisible(false);
    }//GEN-LAST:event_closeJButtonActionPerformed

    private void polgonJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_polgonJButtonActionPerformed
        // Create the polygons...guh.
        polygonList.clear();
        pair3List.clear();
        polygonize(image);

        // Repaint after making polygons.
        repaint();
    }//GEN-LAST:event_polgonJButtonActionPerformed

    private void leanJSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_leanJSliderStateChanged
        // TODO add your handling code here:
        if (!leanJSlider.getValueIsAdjusting()) {

            // Allow the user to adjust how much alpha tolerance there is
            // Higher values with ignore more of the transparency.
            transLean = leanJSlider.getValue();
            leanJSlider.setToolTipText(String.valueOf(transLean));

            // Clear the list and polygonize.
            pair3List.clear();
            polygonList.clear();
            polygonize(image);

            //
            repaint();
        }
    }//GEN-LAST:event_leanJSliderStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton closeJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel leanJPanel;
    private javax.swing.JSlider leanJSlider;
    private javax.swing.JScrollPane mainJScrollPane;
    private javax.swing.JButton polgonJButton;
    private javax.swing.JButton selectJButton;
    // End of variables declaration//GEN-END:variables
    // So I needed a class to store three points so I nested this little bastard here.
    class Pair3 {

        // Variable Declaration
        public int min, max, y;
        // End of Variable Declaration

        Pair3(int min, int max, int y) {
            this.min = min;
            this.max = max;
            this.y = y;
        }
    }
}
