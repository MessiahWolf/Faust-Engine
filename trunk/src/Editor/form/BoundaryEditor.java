/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.form;

import core.coll.CollisionWrapper;
import core.world.Animation;
import io.resource.ResourceReader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import tracer.AlphaTracer;

/**
 *
 * @author rcher
 */
public class BoundaryEditor extends javax.swing.JDialog {

    // Variable Declaration
    // External Project Classes
    private AlphaTracer tracer;
    // Project Classes
    private CollisionWrapper wrapperIn;
    private CollisionWrapper wrapperOut;
    private final Animation animation;
    private ImagePanel mainJPanel;
    // Java Native Classes
    private Point mousePos;
    // Data Types
    private boolean hoverHorizontal;
    private boolean hoverVertical;
    private int fillIndex = -1;
    // End of Variable Declaration

    public BoundaryEditor(java.awt.Dialog parent, Animation animation, boolean modal) {
        super(parent, modal);
        initComponents();

        //
        this.animation = animation;

        //
        init();
    }

    private void init() {

        //
        mousePos = new Point();

        // Panel for rendering.
        mainJPanel = new ImagePanel(mainJScrollPane) {
            @Override
            public void paint(Graphics monet) {

                //
                super.paint(monet);

                // Cast to 2D for easier polygon rendering.
                final Graphics2D manet = (Graphics2D) monet;

                //
                if (tracer != null) {

                    //
                    final ArrayList<Polygon> list = tracer.getPolygonList();

                    // Draw the image under
                    manet.drawImage(tracer.getTraceImage() == null ? tracer.getOriginalImage() : tracer.getTraceImage(), 0, 0, this);
                    manet.setColor(Color.BLACK);

                    // Draw those polygons over.
                    for (int i = 0; i < list.size(); i++) {

                        //
                        final Polygon p = list.get(i);
                        //
                        if (p.contains(mousePos) || i == fillIndex) {
                            manet.setColor(Color.GREEN);
                            manet.fill(p);
                        }

                        // Always draw the outline.
                        manet.setColor(Color.BLACK);
                        manet.draw(p);
                    }

                    //
                    if (hoverHorizontal || hoverVertical) {

                        // Hover line
                        final Point[] points = hoverHorizontal
                                ? tracer.getHorizontalLineTrace(tracer.getTraceImage(), mousePos) : tracer.getVerticalLineTrace(tracer.getTraceImage(), mousePos);
                        manet.setColor(Color.BLACK);

                        // Drawing the line to indicate the cut
                        if (points[0] != null && points[1] != null) {

                            //
                            if (points[0].distance(points[1]) > 1) {

                                //
                                if (hoverHorizontal) {
                                    manet.drawLine(points[0].x, points[0].y, points[1].x, points[0].y);
                                } else if (hoverVertical) {
                                    manet.drawLine(points[0].x, points[0].y, points[0].x, points[1].y);
                                }

                                //
                                manet.setColor(Color.BLACK);
                                manet.fillOval(points[0].x - 2, points[0].y - 2, 4, 4);
                                manet.fillOval(points[1].x - 2, points[1].y - 2, 4, 4);
                            }
                        }
                    }
                }
            }
        };
        mainJPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                mousePos = evt.getPoint();
                repaint();
            }
        });
        mainJPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {

                //
                if (evt.getButton() == MouseEvent.BUTTON1) {

                    //
                    if (hoverHorizontal) {
                        tracer.addHorizontalLine(evt.getPoint());
                        wrapperOut.setPointForIndex(tracer.getPointMap(), animation.getIndex());
                        hoverVertical = false;
                    } else if (hoverVertical) {
                        tracer.addVerticalLine(evt.getPoint());
                        wrapperOut.setPointForIndex(tracer.getPointMap(), animation.getIndex());
                        hoverHorizontal = false;
                    } else {

                        //
                        final ArrayList<Polygon> list = tracer.getPolygonList();

                        // Otherwise check if you clicked a polygon
                        for (int i = 0; i < list.size(); i++) {
                            Polygon p = list.get(i);
                            if (p.contains(evt.getPoint())) {
                                fillIndex = i;
                                rectangleJTable.getSelectionModel().setSelectionInterval(i, i);
                                break;
                            }
                        }
                    }
                } else if (evt.getButton() == MouseEvent.BUTTON3) {
                    hoverVertical = false;
                    hoverHorizontal = false;
                }

                //
                repaint();
            }
        });
        mainJPanel.setPreferredSize(new Dimension(640, 480));
        mainJPanel.setSize(mainJPanel.getPreferredSize());
        mainJPanel.setShowTextile(true);
        mainJPanel.setShowImage(false);
        mainJPanel.setImageCentered(false);
        mainJScrollPane.setViewportView(mainJPanel);

        // As a default.
        rectangleJTable.setModel(new DefaultTableModel());

        // My probably inefficient way of getting images that will soon be deprecated.
        final Toolkit kit = Toolkit.getDefaultToolkit();
        final Class closs = getClass();

        // @DIALOG ICONS.
        final ImageIcon iconBoundary = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-boundary16.png");
        final ImageIcon iconBoundaryAll = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-boundary-all16.png");
        final ImageIcon iconError = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-error16.png");
        final ImageIcon iconRefresh = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-refresh16.png");
        final ImageIcon iconHorizontal = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-line-horizontal24.png");
        final ImageIcon iconVertical = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-line-vertical24.png");
        final ImageIcon iconMirror = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-mirror24.png");
        final ImageIcon iconSet = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-set16.png");

        // Assigning those icons to the proper buttons.
        polygonJButton.setIcon(iconBoundary);
        polygonAllJButton.setIcon(iconBoundaryAll);
        refreshJButton.setIcon(iconRefresh);
        horizontalJButton.setIcon(iconHorizontal);
        verticalJButton.setIcon(iconVertical);
        errorJButton.setIcon(iconError);
        setJButton.setIcon(iconSet);
        mirrorJButton.setIcon(iconMirror);
        nextJButton.setIcon(new ImageIcon(closs.getResource("/Editor/icons/icon-next16r.png")));
        previousJButton.setIcon(new ImageIcon(closs.getResource("/Editor/icons/icon-previous16r.png")));
        clearJButton.setIcon(new ImageIcon(closs.getResource("/Editor/icons/icon-clear16.png")));
        clearAllJButton.setIcon(new ImageIcon(closs.getResource("/Editor/icons/icon-clear-all16.png")));

        // @SETUP FOR ILLUSTRATION
        // Setup the tracer after grabbing the image
        tracer = new AlphaTracer(animation.getCurrentImage());

        // Setup the wrapper after the tracer
        if (animation.getWrapper() == null) {

            // Our end result, the collision wrapper
            wrapperOut = new CollisionWrapper(animation.length());

            //
            tracer.setPrecision(32);
            alphaJSlider.setValue(32);
        } else {

            //
            final int index = animation.getIndex();

            // In the event that the animation loaded with collision data
            // Create a wrapper with that info and set the lines from that data
            // in the tracer so it can recreate the polygons properly.
            wrapperIn = new CollisionWrapper(animation.getWrapper().getData());

            // Flash that polygon capture
            if (wrapperIn.hasDataForIndex(index)) {

                // Making sure the trace for the current image has the point map
                tracer.setPointMap(wrapperIn.getPointsForIndex(index));
                tracer.setPrecision(wrapperIn.getPrecisionForIndex(index));

                // Flash will reset the tracer -- Does not clear lines
                tracer.flash();

                // Updating the slider to the precision
                alphaJSlider.setValue(wrapperIn.getPrecisionForIndex(index));
            }
            
            //
            wrapperOut = new CollisionWrapper(wrapperIn.getData());
        }

        //
        resetWrapperTable();

        // We're going to have to render a little different here.
        // If it's an animation
        mainJPanel.updatePanel(animation);

        // For your viewing pleasure.
        frameJLabel.setText("Frame Index: " + animation.getIndex());

        // @FINAL DIALOG SETUP.
        final ArrayList<Image> list = new ArrayList();
        list.add(ResourceReader.readClassImage(closs, "/Editor/icons/icon-boundary16.png"));
        setIconImages(list);

        // Change the cursor to nothing so it's easier to tell where we're clicking.
        setLocation(kit.getScreenSize().width / 2 - getWidth() / 2, kit.getScreenSize().height / 2 - getHeight() / 2);
        setTitle("Assigning Boundaries for: " + (animation != null ? animation.getDisplayName() : "Unknown"));
        setResizable(false);
    }

    private void resetWrapperTable() {

        //
        final DefaultTableModel model = new DefaultTableModel();
        final int index = animation.getIndex();

        //
        final String[] cols = new String[]{"Index", "Points", "Region", "Multiplier"};

        // If and only if the wrapper has data for the current index.
        if (wrapperOut.hasDataForIndex(index)) {

            //
            final Polygon[] polygons = tracer.getPolygonList().isEmpty() ? wrapperOut.getPolygonsForIndex(index) : tracer.getPolygonList().toArray(new Polygon[]{});

            // Regions and Mutlipliers.
            String[] regions = wrapperOut.getRegionsForIndex(index);
            double[] mults = wrapperOut.getMultipliersForIndex(index);

            //
            final Object[][] data = new Object[polygons.length][5];

            // Set the data in the vector properly.
            for (int i = 0; i < polygons.length; i++) {
                data[i][0] = i;
                data[i][1] = polygons[i].npoints;
                data[i][2] = regions[i];
                data[i][3] = mults[i];
            }

            //
            model.setDataVector(data, cols);
        } else {

            // If no data for index then set to empty table.
            model.setDataVector(new Object[0][], cols);
        }

        //
        rectangleJTable.setModel(model);
        rectangleJTable.revalidate();

        //
        repaint();
    }

    private void wrapTableValues() {

        // Must have acquired polygons.
        if (tracer.getPolygonList().isEmpty()) {
            return;
        }

        // Grab the polygons from the tracer
        final Polygon[] polygons = tracer.getPolygonList().toArray(new Polygon[]{});

        // These are going to hold the values from the table
        final String[] regionNames = new String[polygons.length];
        final double[] mults = new double[polygons.length];
        final HashMap<Point, Integer> points = tracer.getPointMap();

        if (rectangleJTable.getRowCount() > 0) {
            // So for every row fill in those holders with the values from the table.
            for (int i = 0; i < rectangleJTable.getRowCount(); i++) {

                //
                if (rectangleJTable.getValueAt(i, 2) == null) {
                    regionNames[i] = "Undefined";
                } else {
                    regionNames[i] = String.valueOf(rectangleJTable.getValueAt(i, 2));
                }

                //
                if (rectangleJTable.getValueAt(i, 3) == null) {
                    mults[i] = 1.0;
                } else {
                    mults[i] = (double) rectangleJTable.getValueAt(i, 3);
                }
            }
        } else {
            Arrays.fill(regionNames, "Undef.");
            Arrays.fill(mults, 1.0d);
        }

        // Set those for this index in the selected wrapper
        wrapperOut.setDataForIndex(polygons,
                points,
                regionNames,
                mults,
                alphaJSlider.getValue(),
                animation.getIndex());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setJButton = new javax.swing.JButton();
        mainJScrollPane = new javax.swing.JScrollPane();
        animationJPanel = new javax.swing.JPanel();
        frameJLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        previousJButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        nextJButton = new javax.swing.JButton();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        jSeparator2 = new javax.swing.JSeparator();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        clearJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        clearAllJButton = new javax.swing.JButton();
        buttonJPanel = new javax.swing.JPanel();
        polygonJButton = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        polygonAllJButton = new javax.swing.JButton();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        jSeparator3 = new javax.swing.JSeparator();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        horizontalJButton = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        verticalJButton = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        mirrorJButton = new javax.swing.JButton();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        jSeparator1 = new javax.swing.JSeparator();
        refreshJButton = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        errorJButton = new javax.swing.JButton();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        polygonJLabel = new javax.swing.JLabel();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        saveJButton = new javax.swing.JButton();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        closeJButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        rectangleJTable = new javax.swing.JTable() {
            @Override
            public boolean isCellEditable(int row, int col) {
                if (col <=1) {
                    return false;
                }

                //
                return true;
            }
        };
        jPanel1 = new javax.swing.JPanel();
        alphaJLabel = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        alphaJSlider = new javax.swing.JSlider();

        setJButton.setToolTipText("Set these Changes to be Saved");
        setJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        setJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        setJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        setJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setJButtonActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(2147483647, 326));
        setMinimumSize(new java.awt.Dimension(0, 326));

        mainJScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview Panel"));
        mainJScrollPane.setMaximumSize(new java.awt.Dimension(275, 32767));
        mainJScrollPane.setMinimumSize(new java.awt.Dimension(275, 23));
        mainJScrollPane.setPreferredSize(new java.awt.Dimension(275, 100));

        animationJPanel.setMaximumSize(new java.awt.Dimension(32767, 26));
        animationJPanel.setMinimumSize(new java.awt.Dimension(100, 26));
        animationJPanel.setPreferredSize(new java.awt.Dimension(0, 26));
        animationJPanel.setLayout(new javax.swing.BoxLayout(animationJPanel, javax.swing.BoxLayout.LINE_AXIS));

        frameJLabel.setText("Frame Index:");
        frameJLabel.setMaximumSize(new java.awt.Dimension(88, 22));
        frameJLabel.setMinimumSize(new java.awt.Dimension(88, 22));
        frameJLabel.setName(""); // NOI18N
        frameJLabel.setPreferredSize(new java.awt.Dimension(88, 22));
        animationJPanel.add(frameJLabel);
        animationJPanel.add(filler1);

        previousJButton.setToolTipText("Rewind Animation Cycle");
        previousJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        previousJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        previousJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        previousJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousJButtonActionPerformed(evt);
            }
        });
        animationJPanel.add(previousJButton);
        animationJPanel.add(filler3);

        nextJButton.setToolTipText("Advance Animation Cycle");
        nextJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        nextJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        nextJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        nextJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextJButtonActionPerformed(evt);
            }
        });
        animationJPanel.add(nextJButton);
        animationJPanel.add(filler13);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setMaximumSize(new java.awt.Dimension(4, 32767));
        jSeparator2.setMinimumSize(new java.awt.Dimension(4, 10));
        jSeparator2.setPreferredSize(new java.awt.Dimension(4, 10));
        animationJPanel.add(jSeparator2);
        animationJPanel.add(filler4);

        clearJButton.setToolTipText("Clear Polygons for this Index");
        clearJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        clearJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        clearJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        clearJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearJButtonActionPerformed(evt);
            }
        });
        animationJPanel.add(clearJButton);
        animationJPanel.add(filler2);

        clearAllJButton.setToolTipText("Clear Polygons for this Index");
        clearAllJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        clearAllJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        clearAllJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        clearAllJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAllJButtonActionPerformed(evt);
            }
        });
        animationJPanel.add(clearAllJButton);

        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));

        polygonJButton.setToolTipText("Capture Polygons for this Image");
        polygonJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        polygonJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        polygonJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        polygonJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                polygonJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(polygonJButton);
        buttonJPanel.add(filler8);

        polygonAllJButton.setToolTipText("Capture Polygons for this Image");
        polygonAllJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        polygonAllJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        polygonAllJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        polygonAllJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                polygonAllJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(polygonAllJButton);
        buttonJPanel.add(filler15);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setMaximumSize(new java.awt.Dimension(4, 32767));
        jSeparator3.setMinimumSize(new java.awt.Dimension(4, 10));
        jSeparator3.setPreferredSize(new java.awt.Dimension(4, 10));
        buttonJPanel.add(jSeparator3);
        buttonJPanel.add(filler14);

        horizontalJButton.setToolTipText("Add a Horizontal cut to the Image");
        horizontalJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        horizontalJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        horizontalJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        horizontalJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                horizontalJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(horizontalJButton);
        buttonJPanel.add(filler9);

        verticalJButton.setToolTipText("Add a Vertical cut to the Image");
        verticalJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        verticalJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        verticalJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        verticalJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verticalJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(verticalJButton);
        buttonJPanel.add(filler7);

        mirrorJButton.setToolTipText("Mirror Lines to Other Indices");
        mirrorJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        mirrorJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        mirrorJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        mirrorJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mirrorJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(mirrorJButton);
        buttonJPanel.add(filler10);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setMaximumSize(new java.awt.Dimension(8, 24));
        jSeparator1.setMinimumSize(new java.awt.Dimension(8, 24));
        jSeparator1.setPreferredSize(new java.awt.Dimension(8, 24));
        buttonJPanel.add(jSeparator1);

        refreshJButton.setToolTipText("Reset Image");
        refreshJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        refreshJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        refreshJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        refreshJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(refreshJButton);
        buttonJPanel.add(filler6);

        errorJButton.setToolTipText("No Errors");
        errorJButton.setEnabled(false);
        errorJButton.setMaximumSize(new java.awt.Dimension(26, 26));
        errorJButton.setMinimumSize(new java.awt.Dimension(26, 26));
        errorJButton.setPreferredSize(new java.awt.Dimension(26, 26));
        errorJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errorJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(errorJButton);
        buttonJPanel.add(filler11);

        polygonJLabel.setText("Polygons:");
        polygonJLabel.setToolTipText("How many Polygons this Image contains");
        polygonJLabel.setMaximumSize(new java.awt.Dimension(72, 24));
        polygonJLabel.setMinimumSize(new java.awt.Dimension(72, 24));
        polygonJLabel.setPreferredSize(new java.awt.Dimension(72, 24));
        buttonJPanel.add(polygonJLabel);
        buttonJPanel.add(filler12);

        saveJButton.setText("Save");
        saveJButton.setToolTipText("Save this Collision Info.");
        saveJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        saveJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        saveJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        saveJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(saveJButton);
        buttonJPanel.add(filler16);

        closeJButton.setText("Close");
        closeJButton.setToolTipText("Close this Window");
        closeJButton.setMaximumSize(new java.awt.Dimension(88, 26));
        closeJButton.setMinimumSize(new java.awt.Dimension(88, 26));
        closeJButton.setPreferredSize(new java.awt.Dimension(88, 26));
        closeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(closeJButton);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Polygon Info."));

        rectangleJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        rectangleJTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        rectangleJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rectangleJTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(rectangleJTable);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        alphaJLabel.setText("Capture Precision:");
        alphaJLabel.setToolTipText("How close should the Polygon hug the Image");
        alphaJLabel.setMaximumSize(new java.awt.Dimension(124, 24));
        alphaJLabel.setMinimumSize(new java.awt.Dimension(124, 24));
        alphaJLabel.setPreferredSize(new java.awt.Dimension(124, 24));
        jPanel1.add(alphaJLabel);
        jPanel1.add(filler5);

        alphaJSlider.setMaximum(64);
        alphaJSlider.setMinorTickSpacing(8);
        alphaJSlider.setPaintTicks(true);
        alphaJSlider.setToolTipText("Transparency Threshold");
        alphaJSlider.setValue(16);
        alphaJSlider.setMaximumSize(new java.awt.Dimension(32767, 24));
        alphaJSlider.setMinimumSize(new java.awt.Dimension(36, 24));
        alphaJSlider.setPreferredSize(new java.awt.Dimension(200, 24));
        alphaJSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                alphaJSliderStateChanged(evt);
            }
        });
        jPanel1.add(alphaJSlider);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(mainJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(animationJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)))
                    .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mainJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(animationJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void previousJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousJButtonActionPerformed
        // TODO add your handling code here:
        if (animation != null) {

            // First and foremost, rewind the animation back one index.
            animation.rewind();

            // Stored Var.
            final int index = animation.getIndex();

            // Tell the tracer to grab the current image
            tracer.reset(animation.getCurrentImage());

            //
            if (wrapperOut.hasDataForIndex(index)) {

                // Set the point map and precison
                tracer.setPointMap(wrapperOut.getPointsForIndex(index));
                tracer.setPrecision(wrapperOut.getPrecisionForIndex(index));
                tracer.flash();

                //
                alphaJSlider.setValue(tracer.getPrecision());
            } else {

                //
                alphaJSlider.setValue(32);
            }

            //
            resetWrapperTable();

            // Re-enable these controls.
            polygonJButton.setEnabled(true);
            polygonAllJButton.setEnabled(true);
            horizontalJButton.setEnabled(true);
            verticalJButton.setEnabled(true);

            // For your viewing pleasure.
            frameJLabel.setText("Frame Index: " + index);

            // Reset the Fill Index
            fillIndex = -1;

            // Repaint after.
            repaint();
        }
    }//GEN-LAST:event_previousJButtonActionPerformed

    private void clearJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearJButtonActionPerformed

        //
        tracer.reset(animation.getCurrentImage());

        //
        wrapperOut.clearDataForIndex(animation.getIndex());

        //
        wrapTableValues();

        //
        resetWrapperTable();

        // Repaint
        repaint();
    }//GEN-LAST:event_clearJButtonActionPerformed

    private void nextJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextJButtonActionPerformed

        // TODO add your handling code here:
        if (animation != null) {

            // Advance the animation
            animation.advance();

            // Stored Var.
            final int index = animation.getIndex();

            // Tell the tracer to grab the current image
            tracer.reset(animation.getCurrentImage());

            //
            if (wrapperOut.hasDataForIndex(index)) {

                // Set the point map and precison
                tracer.setPointMap(wrapperOut.getPointsForIndex(index));
                tracer.setPrecision(wrapperOut.getPrecisionForIndex(index));
                tracer.flash();

                //
                alphaJSlider.setValue(tracer.getPrecision());
            } else {

                //
                alphaJSlider.setValue(32);
            }

            //
            resetWrapperTable();

            // Re-enable these controls.
            polygonJButton.setEnabled(true);
            polygonAllJButton.setEnabled(true);
            horizontalJButton.setEnabled(true);
            verticalJButton.setEnabled(true);

            // For your viewing pleasure.
            frameJLabel.setText("Frame Index: " + index);

            // Reset the Fill Index
            fillIndex = -1;

            // Repaint after.
            repaint();
        }
    }//GEN-LAST:event_nextJButtonActionPerformed

    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeJButtonActionPerformed
        // TODO add your handling code here:
        animation.setWrapper(wrapperIn);
        
        //
        setVisible(false);
    }//GEN-LAST:event_closeJButtonActionPerformed

    private void saveJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveJButtonActionPerformed
        // TODO add your handling code here:
        animation.setWrapper(wrapperOut);

        //
        setVisible(false);
    }//GEN-LAST:event_saveJButtonActionPerformed

    private void rectangleJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rectangleJTableMouseClicked
        // TODO add your handling code here:
        final Point position = evt.getPoint();

        // Grab the row and column
        final int row = rectangleJTable.rowAtPoint(position);

        // Set that index to be filled in.
        fillIndex = row;

        //
        hoverVertical = false;
        hoverHorizontal = false;

        //
        repaint();
    }//GEN-LAST:event_rectangleJTableMouseClicked

    private void polygonJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_polygonJButtonActionPerformed
        // TODO add your handling code here:
        if (tracer != null && tracer.getTraceImage() != null) {

            // Flash
            tracer.flash();

            //
            wrapTableValues();

            //
            resetWrapperTable();

            // Disable some controls once we get the polygons.
            polygonJButton.setEnabled(false);
            polygonAllJButton.setEnabled(false);
            horizontalJButton.setEnabled(false);
            verticalJButton.setEnabled(false);

            // Reset the hovers
            hoverVertical = false;
            hoverHorizontal = false;

            // Reset the Fill Index
            fillIndex = -1;

            //
            repaint();
        }
    }//GEN-LAST:event_polygonJButtonActionPerformed

    private void alphaJSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_alphaJSliderStateChanged
        // TODO add your handling code here:
        if (tracer != null) {

            //
            if (!alphaJSlider.getValueIsAdjusting()) {

                //
                final int val = alphaJSlider.getValue();
                final int max = alphaJSlider.getMaximum();

                //
                tracer.setPrecision(val);

                // Re-enable the polygonize Button
                polygonJButton.setEnabled(true);

                // Paint the picture.
                repaint();

                // Just describes how tight the tracer will stick to the image.
                if (val < 24) {
                    alphaJSlider.setToolTipText("Minimal (" + val + ")");
                } else if (val >= 24 && val < 48) {
                    alphaJSlider.setToolTipText("Weak (" + val + ")");
                } else if (val >= 48 && val < 64) {
                    alphaJSlider.setToolTipText("Tight (" + val + ")");
                } else if (val >= 64) {
                    alphaJSlider.setToolTipText("Strong (" + val + ")");
                }

                // Show the message
                if (tracer.getMessage() != null) {
                    errorJButton.setEnabled(true);
                    errorJButton.setToolTipText(tracer.getMessage());
                } else {
                    errorJButton.setEnabled(false);
                    errorJButton.setToolTipText("Finished without Error.");
                }

                // Update JLabel
                polygonJLabel.setText("Polygons: " + tracer.getPolygonList().size());

                //
                alphaJLabel.setText("Polygon Precision: " + ((val * 100) / max) + "%");
            }
        }
    }//GEN-LAST:event_alphaJSliderStateChanged

    private void refreshJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshJButtonActionPerformed

        //
        final int index = animation.getIndex();

        //
        if (wrapperOut.hasDataForIndex(index)) {

            // Set the point map and precison
            tracer.setPointMap(wrapperOut.getPointsForIndex(index));
            tracer.setPrecision(wrapperOut.getPrecisionForIndex(index));
            tracer.flash();

            //
            alphaJSlider.setValue(tracer.getPrecision());
        } else {

            //
            tracer.reset(animation.getCurrentImage());

            //
            alphaJSlider.setValue(32);
        }

        //
        wrapTableValues();

        //
        resetWrapperTable();

        // Update buttons.
        polygonJButton.setEnabled(true);
        polygonAllJButton.setEnabled(true);
        horizontalJButton.setEnabled(true);
        verticalJButton.setEnabled(true);

        // Update JLabel
        polygonJLabel.setText("Polygons: " + tracer.getPolygonList().size());

        // Show the message
        if (tracer.getMessage() != null) {
            errorJButton.setEnabled(true);
            errorJButton.setToolTipText(tracer.getMessage());
        } else {
            errorJButton.setEnabled(false);
            errorJButton.setToolTipText("Finished without Error.");
        }

        // Reset the Fill Index
        fillIndex = -1;

        // Repaint.
        repaint();
    }//GEN-LAST:event_refreshJButtonActionPerformed

    private void horizontalJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_horizontalJButtonActionPerformed
        // TODO add your handling code here:
        hoverHorizontal = true;
        hoverVertical = false;

        //
        repaint();
    }//GEN-LAST:event_horizontalJButtonActionPerformed

    private void verticalJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verticalJButtonActionPerformed
        // TODO add your handling code here:
        hoverVertical = true;
        hoverHorizontal = false;

        //
        repaint();
    }//GEN-LAST:event_verticalJButtonActionPerformed

    private void errorJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorJButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_errorJButtonActionPerformed

    private void setJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setJButtonActionPerformed

        // Give the output wrapper the values from the table and tracer
        //wrap(wrapperOut);
    }//GEN-LAST:event_setJButtonActionPerformed

    private void mirrorJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mirrorJButtonActionPerformed
        // TODO add your handling code here:
        final int startIndex = animation.getIndex();

        //
        if (wrapperOut.hasDataForIndex(startIndex)) {

            //
            final int rowCount = rectangleJTable.getRowCount();
            final int polyLength = wrapperOut.getPolygonsForIndex(startIndex).length;
            final HashMap<Point, Integer> points = wrapperOut.getPointsForIndex(startIndex);

            // Mirror the cuts on certain animation indices.
            final MirrorSelector dialog = new MirrorSelector(this, animation, wrapperOut, true);
            dialog.setVisible(true);

            // It's run its course and now we should have out indices
            final int[] indices = dialog.getSelectedIndices();

            // Determine the auto fill data
            final String[] regionNames = new String[polyLength];
            final double[] mults = new double[polyLength];

            //
            for (int i = 0; i < rowCount; i++) {
                regionNames[i] = String.valueOf(rectangleJTable.getValueAt(i, 2));
                mults[i] = (double) rectangleJTable.getValueAt(i, 3);
            }

            //
            if (indices != null) {

                // Iterate over them and wrap them.
                for (int i = 0; i < indices.length; i++) {

                    //
                    final int index = indices[i];

                    // Must be withing animations length; just checking.
                    if (index < animation.length() && index != startIndex) {

                        //
                        animation.setIndex(index);

                        // Reset the tracer -- Does not clear lines
                        tracer.reset(animation.getCurrentImage());

                        // Apply the point map for this index and create the polygons.
                        tracer.setPointMap(points);
                        tracer.flash();

                        // Same deal as setJButton's actionPerformed Event
                        final Polygon[] polygons = tracer.getPolygonList().toArray(new Polygon[]{});

                        //
                        wrapperOut.setDataForIndex(polygons,
                                points,
                                regionNames,
                                mults,
                                dialog.getPrecision(),
                                index);
                    }
                }

                // Update the JTable with those polygons.
                //updateWrapperTable();
            }
        }
    }//GEN-LAST:event_mirrorJButtonActionPerformed

    private void polygonAllJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_polygonAllJButtonActionPerformed
        // TODO add your handling code here:
        if (tracer != null) {

            //
            for (int i = 0; i < animation.length(); i++) {

                //
                animation.setIndex(i);
                tracer.reset(animation.getCurrentImage());
                tracer.flash();

                //
                wrapTableValues();

                //
                resetWrapperTable();
            }

            //
            polygonJButton.setEnabled(false);
            polygonAllJButton.setEnabled(false);
            horizontalJButton.setEnabled(false);
            verticalJButton.setEnabled(false);

            // Reset the Fill Index
            fillIndex = -1;

            //
            repaint();
        }
    }//GEN-LAST:event_polygonAllJButtonActionPerformed

    private void clearAllJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAllJButtonActionPerformed

        // TODO add your handling code here:
        if (tracer != null) {

            //
            for (int i = 0; i < animation.length(); i++) {

                //
                animation.setIndex(i);

                //
                tracer.reset(animation.getCurrentImage());

                //
                wrapperOut.clearDataForIndex(i);

                //
                wrapTableValues();

                //
                resetWrapperTable();
            }

            // Repaint
            repaint();
        }
    }//GEN-LAST:event_clearAllJButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel alphaJLabel;
    private javax.swing.JSlider alphaJSlider;
    private javax.swing.JPanel animationJPanel;
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton clearAllJButton;
    private javax.swing.JButton clearJButton;
    private javax.swing.JButton closeJButton;
    private javax.swing.JButton errorJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel frameJLabel;
    private javax.swing.JButton horizontalJButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JScrollPane mainJScrollPane;
    private javax.swing.JButton mirrorJButton;
    private javax.swing.JButton nextJButton;
    private javax.swing.JButton polygonAllJButton;
    private javax.swing.JButton polygonJButton;
    private javax.swing.JLabel polygonJLabel;
    private javax.swing.JButton previousJButton;
    private javax.swing.JTable rectangleJTable;
    private javax.swing.JButton refreshJButton;
    private javax.swing.JButton saveJButton;
    private javax.swing.JButton setJButton;
    private javax.swing.JButton verticalJButton;
    // End of variables declaration//GEN-END:variables
}
