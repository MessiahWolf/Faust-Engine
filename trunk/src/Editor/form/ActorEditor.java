/**
 * Copyright (c) 2013, Robert Cherry * All rights reserved.
 *
 * This file is part of the Faust Editor.
 *
 * The Faust Editor is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * The Faust Editor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * The Faust Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package Editor.form;

import Editor.FaustEditor;
import core.event.AnimationEvent;
import core.event.AnimationListener;
import core.event.WorldObjectEvent;
import core.world.Actor;
import core.world.WorldItem;
import io.resource.ResourceDelegate;
import io.resource.ResourceReader;
import io.resource.ResourceWriter;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import Editor.listener.ManifestBinder;
import java.awt.Insets;
import javax.swing.JToggleButton;

/**
 *
 * @author Robert A. Cherry
 */
public class ActorEditor extends javax.swing.JDialog implements AnimationListener, TableModelListener {

    // Variable Declaration
    // Java Swing Classes
    private JButton attackDamageJButton;
    private JButton attackDefenseJButton;
    private JButton magicDamageJButton;
    private JButton sourceButton;
    private JPanel imageJPanel;
    // Java Classes
    private HashMap<String, Object> editedStatMap;
    private HashMap<WorldItem, Double> editedDropMap;
    private Image imageMissingAnimation;
    private Image imageDialog;
    private ImageIcon iconLock;
    private ImageIcon iconUnlock;
    // Project Classes
    private DelegateCheckBox box_delegate;
    private Actor resource;
    private FaustEditor editor;
    private ManifestBinder binder;
    private ResourceDelegate delegate;
    // Data Types
    private int attackDamageDefault;
    private int attackDefenseDefault;
    //
    private int attackDamageChanged;
    private int attackDefenseChanged;
    //
    private int magicDamageDefault;
    private int magicDamageChanged;
    // End of Variable Declaration

    public ActorEditor(FaustEditor editor, ResourceDelegate delegate, Actor resource, boolean modal) {

        //
        super(editor);
        super.setModal(modal);

        //
        this.editor = editor;
        this.delegate = delegate;
        this.resource = resource;

        // Init Components
        initComponents();

        // Initialize
        init();
    }

    private void init() {

        // Create Actor if Null Actor supplied
        if (resource == null) {
            resource = new Actor();
        }

        // Create the ImagePanel
        imageJPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics monet) {

                // Paint super class method
                super.paintComponent(monet);

                // My custom commands
                imageJPanelPaint(monet);
            }
        };

        //
        renderJScrollPane.setViewportView(imageJPanel);

        // Inst. Arrays
        editedDropMap = new HashMap<>();

        // Attribute hashmap copy
        editedStatMap = new HashMap<>();
        editedStatMap.putAll(resource.getAttributeMap());

        //
        setupDefaultValues();

        //
        setupManifestBinder();

        //
        setupTables();

        //
        setupAttributePanel();
    }

    private void commit() {

        // Apply attribute changes
        resource.setAttributeMap(editedStatMap);

        // Resource Delegate information
        resource.setReferenceName(binder.getReferenceName());
        resource.setReferenceID(binder.getReferenceID());
        resource.setDisplayName(binder.getDisplayName());

        // Actor stats
        resource.setAttackDamage(attackDamageChanged);
        resource.setAttackDefense(attackDefenseChanged);

        // Build a new Drop Hash Map from Table Contents and apply drop list change
        resource.setDropList(editedDropMap);

        // Final resource call before Validate
        resource.updateAttributes();

        // Make sure the resource is up to date before writing it out to file.
        resource.validate();

        // Actually write it out to file
        ResourceWriter.write(delegate, resource);

        // Add to delegate so other dialogs have the updates. PackageQueue will add file to .zip if it needs to be there.
        delegate.addResource(resource);

        //
        JOptionPane.showMessageDialog(this, "Commit Successful");
    }

    // <editor-fold desc="Editor Fold: Setup Methods" defaultstate="collapsed">
    private void setupAttributePanel() {

        //
        final Class closs = getClass();
        final Toolkit kit = Toolkit.getDefaultToolkit();
        final Dimension dimension = new Dimension(32, 32);

        //
        final ImageIcon attackDamageIcon = ResourceReader.readClassPathIcon(closs, "/Editor/icons/skills/icon-attackdamage.png");
        final ImageIcon attackDamage16Icon = ResourceReader.readClassPathIcon(closs, "/Editor/icons/skills/icon-attackdamage16.png");
        final ImageIcon attackDefenseIcon = ResourceReader.readClassPathIcon(closs, "/Editor/icons/skills/icon-attackdefense.png");
        final ImageIcon attackDefense16Icon = ResourceReader.readClassPathIcon(closs, "/Editor/icons/skills/icon-attackdefense16.png");
        final ImageIcon magicDamageIcon = ResourceReader.readClassPathIcon(closs, "/Editor/icons/skills/icon-magicdamage.png");
        final ImageIcon magicDamage16Icon = ResourceReader.readClassPathIcon(closs, "/Editor/icons/skills/icon-magicdamage16.png");
        final ImageIcon reset16Icon = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-reset16.png");

        //
        iconLock = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-locked24.png");
        iconUnlock = ResourceReader.readClassPathIcon(closs, "/Editor/icons/icon-unlocked24.png");

        // Our custom Action Listener
        final ActionListener evt = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // Enable the FormattedJField
                attributeJLabel.setEnabled(true);
                valueJField.setEnabled(true);

                final Object source = evt.getSource();

                //
                sourceButton = (JButton) source;

                //
                if (sourceButton == attackDamageJButton) {

                    //
                    attributeJLabel.setText("Base Attack Damage");
                    attributeJLabel.setIcon(attackDamage16Icon);

                    //
                    valueJField.setValue(attackDamageChanged);
                    valueJField.selectAll();
                    valueJField.requestFocus(true);
                } else if (sourceButton == attackDefenseJButton) {

                    //
                    attributeJLabel.setText("Base Attack Defense");
                    attributeJLabel.setIcon(attackDefense16Icon);

                    //
                    valueJField.setValue(attackDefenseChanged);
                    valueJField.requestFocus(true);
                    valueJField.selectAll();
                } else if (sourceButton == magicDamageJButton) {

                    //
                    attributeJLabel.setText("Base Magic Damage");
                    attributeJLabel.setIcon(magicDamage16Icon);

                    //
                    valueJField.setValue(magicDamageChanged);
                    valueJField.selectAll();
                    valueJField.requestFocus(true);
                }
            }
        };

        //
        attackDamageJButton = new JButton();
        attackDamageJButton.setPreferredSize(dimension);
        attackDamageJButton.setIcon(attackDamageIcon);
        attackDamageJButton.setToolTipText("Physical Attack Damage");
        attackDamageJButton.addActionListener(evt);

        //
        attackDefenseJButton = new JButton();
        attackDefenseJButton.setPreferredSize(dimension);
        attackDefenseJButton.setIcon(attackDefenseIcon);
        attackDefenseJButton.setToolTipText("Physical Attack Defense");
        attackDefenseJButton.addActionListener(evt);

        //
        magicDamageJButton = new JButton();
        magicDamageJButton.setPreferredSize(dimension);
        magicDamageJButton.setIcon(magicDamageIcon);
        magicDamageJButton.setToolTipText("Magic Attack Damage");
        magicDamageJButton.addActionListener(evt);

        // Button Section
        boolean bool = delegate.exists(resource);

        // If the resource existed before dialog started, lock permission to change
        lockJButton.setContentAreaFilled(false);
        lockJButton.setFocusPainted(false);
        lockJButton.setIcon(bool ? iconLock : iconUnlock);
        lockJButton.setText(bool ? "Permission Locked" : "Permission Unlocked");
        lockJButton.setToolTipText(bool ? "Click to Unlock Change Permissions" : "Click to Lock Change Permissions");
        binder.lock(bool);

        //
        imageDialog = kit.getImage(closs.getResource("/Editor/stock/stock-dialogedit16.png"));
        imageMissingAnimation = kit.getImage(closs.getResource("/Editor/stock/stock-animation2.png"));

        //
        this.setIconImage(imageDialog);

        //
        resetJButton.setIcon(reset16Icon);

        //
        attributeJPanel.add(attackDamageJButton);
        attributeJPanel.add(attackDefenseJButton);
        attributeJPanel.add(magicDamageJButton);
    }

    private void setupManifestBinder() {

        //
        box_delegate = new DelegateCheckBox(delegate);
        buttonJPanel.add(box_delegate, 0);

        // Testing it out.
        binder = new ManifestBinder(delegate, resource);

        // Binding stuff manually.
        binder.bind(ManifestBinder.BOX_DELEGATE, box_delegate);
        binder.bind(ManifestBinder.BUTTON_FINISH, commitJButton);
        binder.bind(ManifestBinder.BUTTON_GENERATE, button_generate);

        // Fields
        binder.bind(ManifestBinder.FIELD_DISPLAY, field_display);
        binder.bind(ManifestBinder.FIELD_REFERENCE, field_reference);
        binder.bind(ManifestBinder.FIELD_NAME, field_name);
        binder.bind(ManifestBinder.FIELD_PLUGIN, field_plugin);
        binder.bind(ManifestBinder.FIELD_LOCATION, field_location);
        binder.bind(ManifestBinder.FIELD_WIDTH, field_width);
        binder.bind(ManifestBinder.FIELD_HEIGHT, field_height);

        // Invoke the wrath of the Manifest Binder. >:[
        binder.invoke();

        // We are in fact editting an existing resource.
        binder.setEdit(delegate.exists(resource));

        // Either or method above.
        //binder.setEdit(delegate.getInstanceCount(resource) >= 1 ? true : false);
        button_generate.setEnabled(!binder.isEditting());
    }

    private void setupTables() {

        // Custom Models
        final DefaultTableModel itemModel = new DefaultTableModel();
        final DefaultTableModel scriptModel = new DefaultTableModel();
        final DefaultTableModel statModel = new DefaultTableModel();

        // Filling Drop List JTable
        for (Map.Entry<WorldItem, Double> set : resource.getItemMap().entrySet()) {
            itemModel.addRow(new Object[]{set.getKey(), set.getValue()});
        }

        // Filling Attribute JTable
        final String[][] dataVector = new String[resource.getAttributeMap().size()][];
        int dataIndex = 0;
        for (Map.Entry<String, Object> set : resource.getAttributeMap().entrySet()) {
            dataVector[dataIndex] = new String[]{set.getKey(), String.valueOf(set.getValue())};
            dataIndex++;
        }

        //
        final String[] events = WorldObjectEvent.getEvents();
        final String[][] scriptVector = new String[events.length][];
        for (int i = 0; i < events.length; i++) {
            scriptVector[i] = new String[]{"", ""};
        }

        // Apply Data taken from Actor to JTable Model
        itemModel.setDataVector(new Object[][]{}, new String[]{"Item", "% Drop Chance"});
        scriptModel.setDataVector(scriptVector, new String[]{"Event", "Script Name"});
        statModel.setDataVector(dataVector, new String[]{"Attribute", "Value"});

        // Apply Models to JTables
        itemJTable.setModel(itemModel);
        scriptJTable.setModel(scriptModel);
        attributeJTable.setModel(statModel);

        // Custom Table Options for First Table
        attributeJTable.setRowHeight(18);
        attributeJTable.getModel().addTableModelListener(this);
    }

    private void setupDefaultValues() {

        //
        attackDamageDefault = resource.getAttackDamage();
        attackDefenseDefault = resource.getAttackDefense();

        //
        magicDamageDefault = resource.getMagicDamage();

        //
        attackDamageChanged = attackDamageDefault;
        attackDefenseChanged = attackDefenseDefault;

        //
        magicDamageChanged = magicDamageDefault;

        //
        if (sourceButton == attackDamageJButton) {

            //
            valueJField.setValue(attackDamageChanged);
            valueJField.selectAll();
        } else if (sourceButton == attackDefenseJButton) {

            //
            valueJField.setValue(attackDefenseChanged);
            valueJField.selectAll();
        } else if (sourceButton == magicDamageJButton) {

            //
            valueJField.setValue(magicDamageChanged);
            valueJField.selectAll();
        }
    }
    // </editor-fold>

    private void imageJPanelPaint(Graphics monet) {

        // Animation has to exist
        if (imageMissingAnimation != null) {

            //
            int posx, posy, width, height;

            //
            final String string = "No Animation Set";

            //
            final Rectangle2D rect = ((FontMetrics) monet.getFontMetrics()).getStringBounds(string, monet);

            // Actual width and height of the text
            width = (int) rect.getWidth();
            height = (int) rect.getHeight();

            //
            posx = (imageJPanel.getWidth() / 2) - (width / 2);
            posy = (imageJPanel.getHeight() / 2) - (height / 2);

            //
            //monet.drawImage(image_animation, posx, posy, this);
            monet.drawString(string, posx, posy);
        }
    }

    private int[] getModelInfo(DefaultTableModel newModel, WorldItem newSearch) {

        //
        if (newModel.getRowCount() < 1) {
            return null;
        }

        // Search through each column for each row
        for (int j = 0; j < newModel.getColumnCount(); j++) {

            //
            String itemAt = String.valueOf(newModel.getValueAt(0, j));

            //
            if (itemAt.equalsIgnoreCase(newSearch.getReferenceName())) {
                return new int[]{0, j};
            }
        }

        // Return the valueList
        return null;
    }

    @Override
    public void tableChanged(TableModelEvent e) {

        // Grab the Model of the Changed Table in this case AncestorTable is a given.
        TableModel model = (TableModel) e.getSource();

        // Get the selected Row and Column
        int selectedRow = e.getFirstRow();
        int selectedColumn = e.getColumn();

        // Get the Name of the Column Changed for Parse Method
        String selectedColumnName = model.getColumnName(selectedColumn);

        // Get the Value of the Changed Column for Parse Method
        String selectedColumnValue = String.valueOf(model.getValueAt(selectedRow, selectedColumn));

        if (this.attributeJTable.getModel() == model) {

            // Apply settings to stat map
            editedStatMap.put(selectedColumnName, selectedColumnValue);
        } else if (this.itemJTable.getModel() == model) {

            //
            double newChance;

            try {

                // Ask for chance to drop
                newChance = Double.parseDouble(selectedColumnValue);

                // Reset if out of bounds
                if (newChance > 100.0 || newChance < 0.0) {
                    //newChance = 50.0;
                }
            } catch (NumberFormatException nfe) {
                // Reset chance to .5 if you type in illegal chars
                //newChance = 50.0;
            }

            // Apply settings to item map
            //editedDropMap.put(((Item)delegate.getResource(Item.class, selectedColumnName).getValue()), newChance);
        }
    }

    @Override
    public void animationEnd(AnimationEvent event) {
        imageJPanel.repaint();
    }

    @Override
    public void animationStep(AnimationEvent event) {
        imageJPanel.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        scriptTabJPanel = new JPanel();
        scriptJScrollPane = new JScrollPane();
        scriptJTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return false;
                }
                return true;
            }
        };
        attributeJScrollPane = new JScrollPane();
        attributeJTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return false;
                }
                return true;
            }
        };
        renderJScrollPane = new JScrollPane();
        buttonJPanel = new JPanel();
        filler5 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        commitJButton = new JButton();
        filler3 = new Box.Filler(new Dimension(8, 0), new Dimension(8, 0), new Dimension(8, 32767));
        cancelJButton = new JButton();
        mainTabbedPane = new JTabbedPane();
        attributeTabJPanel = new JPanel();
        attributeJPanel = new JPanel();
        valueJField = new JFormattedTextField();
        resetJButton = new JButton();
        jLabel1 = new JLabel();
        attributeJLabel = new JLabel();
        itemTabJPanel = new JPanel();
        itemJScrollPane = new JScrollPane();
        itemJTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (row == 0) {
                    return false;
                }

                //
                return true;
            }
        };
        itemJPanel = new JPanel();
        itemJLabel = new JLabel();
        filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        itemJButton = new JButton();
        editorTabJPanel = new JPanel();
        settingJPanel = new JPanel();
        field_location = new JTextField();
        locationJLabel = new JLabel();
        packageJLabel = new JLabel();
        field_plugin = new JTextField();
        widthJLabel = new JLabel();
        heightJLabel = new JLabel();
        referenceJLabel = new JLabel();
        nameJLabel = new JLabel();
        field_reference = new JTextField();
        field_name = new JTextField();
        field_display = new JTextField();
        displayJLabel = new JLabel();
        field_width = new JTextField();
        field_height = new JTextField();
        button_generate = new JButton();
        imageJButton = new JButton();
        jLabel2 = new JLabel();
        jPanel1 = new JPanel();
        filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lockJButton = new JToggleButton();

        scriptJScrollPane.setMaximumSize(new Dimension(246, 184));
        scriptJScrollPane.setMinimumSize(new Dimension(246, 184));
        scriptJScrollPane.setPreferredSize(new Dimension(246, 184));

        scriptJTable.setModel(new DefaultTableModel(
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
        scriptJTable.setFillsViewportHeight(true);
        scriptJTable.setMaximumSize(new Dimension(284, 208));
        scriptJTable.setMinimumSize(new Dimension(284, 208));
        scriptJTable.setPreferredSize(new Dimension(284, 208));
        scriptJTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                scriptJTableMouseClicked(evt);
            }
        });
        scriptJScrollPane.setViewportView(scriptJTable);

        GroupLayout scriptTabJPanelLayout = new GroupLayout(scriptTabJPanel);
        scriptTabJPanel.setLayout(scriptTabJPanelLayout);
        scriptTabJPanelLayout.setHorizontalGroup(
            scriptTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(scriptTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scriptJScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        scriptTabJPanelLayout.setVerticalGroup(
            scriptTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(scriptTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scriptJScrollPane, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );

        attributeJScrollPane.setMaximumSize(new Dimension(246, 184));
        attributeJScrollPane.setMinimumSize(new Dimension(246, 184));
        attributeJScrollPane.setPreferredSize(new Dimension(246, 184));

        attributeJTable.setModel(new DefaultTableModel(
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
        attributeJTable.setFillsViewportHeight(true);
        attributeJTable.setMaximumSize(new Dimension(284, 208));
        attributeJTable.setMinimumSize(new Dimension(284, 208));
        attributeJTable.setPreferredSize(new Dimension(284, 208));
        attributeJScrollPane.setViewportView(attributeJTable);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Actor Editing");
        setMinimumSize(new Dimension(505, 298));
        setResizable(false);

        renderJScrollPane.setMaximumSize(new Dimension(196, 196));
        renderJScrollPane.setMinimumSize(new Dimension(196, 196));
        renderJScrollPane.setPreferredSize(new Dimension(196, 196));

        buttonJPanel.setMaximumSize(new Dimension(620, 26));
        buttonJPanel.setMinimumSize(new Dimension(620, 26));
        buttonJPanel.setPreferredSize(new Dimension(620, 26));
        buttonJPanel.setLayout(new BoxLayout(buttonJPanel, BoxLayout.LINE_AXIS));
        buttonJPanel.add(filler5);

        commitJButton.setText("Commit");
        commitJButton.setToolTipText("Finalize Changes");
        commitJButton.setMaximumSize(new Dimension(88, 26));
        commitJButton.setMinimumSize(new Dimension(88, 26));
        commitJButton.setPreferredSize(new Dimension(88, 26));
        commitJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                commitJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(commitJButton);
        buttonJPanel.add(filler3);

        cancelJButton.setText("Cancel");
        cancelJButton.setToolTipText("Close this Dialog");
        cancelJButton.setMaximumSize(new Dimension(88, 26));
        cancelJButton.setMinimumSize(new Dimension(88, 26));
        cancelJButton.setPreferredSize(new Dimension(88, 26));
        cancelJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(cancelJButton);

        mainTabbedPane.setMaximumSize(new Dimension(276, 290));
        mainTabbedPane.setMinimumSize(new Dimension(276, 290));
        mainTabbedPane.setPreferredSize(new Dimension(276, 290));

        attributeTabJPanel.setMaximumSize(new Dimension(276, 262));
        attributeTabJPanel.setMinimumSize(new Dimension(276, 262));
        attributeTabJPanel.setPreferredSize(new Dimension(276, 262));

        attributeJPanel.setBorder(BorderFactory.createEtchedBorder());
        attributeJPanel.setMaximumSize(new Dimension(250, 240));
        attributeJPanel.setMinimumSize(new Dimension(250, 240));
        attributeJPanel.setPreferredSize(new Dimension(250, 240));
        attributeJPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));

        valueJField.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(NumberFormat.getIntegerInstance())));
        valueJField.setHorizontalAlignment(JTextField.TRAILING);
        valueJField.setEnabled(false);
        valueJField.setPreferredSize(new Dimension(148, 24));
        valueJField.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                valueJFieldPropertyChange(evt);
            }
        });

        resetJButton.setToolTipText("Reset selected value");
        resetJButton.setMaximumSize(new Dimension(24, 24));
        resetJButton.setMinimumSize(new Dimension(24, 24));
        resetJButton.setPreferredSize(new Dimension(24, 24));
        resetJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resetJButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Value:");
        jLabel1.setMaximumSize(new Dimension(34, 24));
        jLabel1.setMinimumSize(new Dimension(34, 24));
        jLabel1.setPreferredSize(new Dimension(34, 24));

        attributeJLabel.setText(" No Attribute Selected");
        attributeJLabel.setBorder(BorderFactory.createEtchedBorder());
        attributeJLabel.setIconTextGap(8);
        attributeJLabel.setMaximumSize(new Dimension(240, 24));
        attributeJLabel.setMinimumSize(new Dimension(240, 24));
        attributeJLabel.setPreferredSize(new Dimension(240, 24));

        GroupLayout attributeTabJPanelLayout = new GroupLayout(attributeTabJPanel);
        attributeTabJPanel.setLayout(attributeTabJPanelLayout);
        attributeTabJPanelLayout.setHorizontalGroup(
            attributeTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(attributeTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(attributeTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(attributeJPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(attributeTabJPanelLayout.createSequentialGroup()
                        .addComponent(resetJButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(valueJField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(attributeJLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        attributeTabJPanelLayout.setVerticalGroup(
            attributeTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(attributeTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(attributeJPanel, GroupLayout.PREFERRED_SIZE, 171, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(attributeJLabel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(attributeTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(valueJField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetJButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        mainTabbedPane.addTab("Attributes", attributeTabJPanel);

        itemJScrollPane.setMaximumSize(new Dimension(287, 32767));
        itemJScrollPane.setMinimumSize(new Dimension(287, 23));
        itemJScrollPane.setPreferredSize(new Dimension(287, 202));

        itemJTable.setModel(new DefaultTableModel(
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
        itemJTable.setMaximumSize(new Dimension(246, 64));
        itemJTable.setMinimumSize(new Dimension(246, 64));
        itemJTable.setPreferredSize(new Dimension(246, 64));
        itemJTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                itemJTableMouseClicked(evt);
            }
        });
        itemJScrollPane.setViewportView(itemJTable);

        itemJPanel.setMaximumSize(new Dimension(287, 22));
        itemJPanel.setMinimumSize(new Dimension(287, 22));
        itemJPanel.setPreferredSize(new Dimension(287, 22));
        itemJPanel.setLayout(new BoxLayout(itemJPanel, BoxLayout.LINE_AXIS));

        itemJLabel.setText("Add to Drop List:");
        itemJLabel.setToolTipText("The list of Items that this actor drops upon death and the chance to drop each item");
        itemJLabel.setMaximumSize(new Dimension(88, 22));
        itemJLabel.setMinimumSize(new Dimension(88, 22));
        itemJLabel.setPreferredSize(new Dimension(88, 22));
        itemJPanel.add(itemJLabel);
        itemJPanel.add(filler2);

        itemJButton.setText("Add Item");
        itemJButton.setMaximumSize(new Dimension(128, 26));
        itemJButton.setMinimumSize(new Dimension(128, 26));
        itemJButton.setPreferredSize(new Dimension(128, 26));
        itemJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                itemJButtonActionPerformed(evt);
            }
        });
        itemJPanel.add(itemJButton);

        GroupLayout itemTabJPanelLayout = new GroupLayout(itemTabJPanel);
        itemTabJPanel.setLayout(itemTabJPanelLayout);
        itemTabJPanelLayout.setHorizontalGroup(
            itemTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(itemTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(itemTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(itemJPanel, GroupLayout.PREFERRED_SIZE, 251, Short.MAX_VALUE)
                    .addComponent(itemJScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        itemTabJPanelLayout.setVerticalGroup(
            itemTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(itemTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(itemJPanel, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(itemJScrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mainTabbedPane.addTab("Drops", itemTabJPanel);

        settingJPanel.setMaximumSize(new Dimension(247, 184));
        settingJPanel.setMinimumSize(new Dimension(247, 184));
        settingJPanel.setPreferredSize(new Dimension(247, 184));
        GridBagLayout settingJPanelLayout = new GridBagLayout();
        settingJPanelLayout.columnWidths = new int[] {0, 5, 0};
        settingJPanelLayout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        settingJPanel.setLayout(settingJPanelLayout);

        field_location.setColumns(20);
        field_location.setEnabled(false);
        field_location.setMaximumSize(new Dimension(134, 22));
        field_location.setMinimumSize(new Dimension(134, 22));
        field_location.setPreferredSize(new Dimension(134, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        settingJPanel.add(field_location, gridBagConstraints);

        locationJLabel.setText("Image Location:");
        locationJLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        locationJLabel.setMaximumSize(new Dimension(104, 22));
        locationJLabel.setMinimumSize(new Dimension(104, 22));
        locationJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        settingJPanel.add(locationJLabel, gridBagConstraints);

        packageJLabel.setText("Image Package:");
        packageJLabel.setMaximumSize(new Dimension(104, 22));
        packageJLabel.setMinimumSize(new Dimension(104, 22));
        packageJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        settingJPanel.add(packageJLabel, gridBagConstraints);

        field_plugin.setColumns(20);
        field_plugin.setEnabled(false);
        field_plugin.setMaximumSize(new Dimension(134, 22));
        field_plugin.setMinimumSize(new Dimension(134, 22));
        field_plugin.setPreferredSize(new Dimension(134, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        settingJPanel.add(field_plugin, gridBagConstraints);

        widthJLabel.setText("Image Width :");
        widthJLabel.setMaximumSize(new Dimension(104, 22));
        widthJLabel.setMinimumSize(new Dimension(104, 22));
        widthJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        settingJPanel.add(widthJLabel, gridBagConstraints);

        heightJLabel.setText("Image Height:");
        heightJLabel.setMaximumSize(new Dimension(104, 22));
        heightJLabel.setMinimumSize(new Dimension(104, 22));
        heightJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        settingJPanel.add(heightJLabel, gridBagConstraints);

        referenceJLabel.setText("Reference ID:");
        referenceJLabel.setMaximumSize(new Dimension(104, 22));
        referenceJLabel.setMinimumSize(new Dimension(104, 22));
        referenceJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        settingJPanel.add(referenceJLabel, gridBagConstraints);

        nameJLabel.setText("Reference Name:");
        nameJLabel.setMaximumSize(new Dimension(104, 22));
        nameJLabel.setMinimumSize(new Dimension(104, 22));
        nameJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        settingJPanel.add(nameJLabel, gridBagConstraints);

        field_reference.setColumns(20);
        field_reference.setToolTipText("");
        field_reference.setMaximumSize(new Dimension(134, 22));
        field_reference.setMinimumSize(new Dimension(134, 22));
        field_reference.setPreferredSize(new Dimension(134, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        settingJPanel.add(field_reference, gridBagConstraints);

        field_name.setColumns(20);
        field_name.setMaximumSize(new Dimension(134, 22));
        field_name.setMinimumSize(new Dimension(134, 22));
        field_name.setPreferredSize(new Dimension(134, 22));
        field_name.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                field_nameComponentResized(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        settingJPanel.add(field_name, gridBagConstraints);

        field_display.setColumns(20);
        field_display.setMaximumSize(new Dimension(134, 22));
        field_display.setMinimumSize(new Dimension(134, 22));
        field_display.setPreferredSize(new Dimension(134, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        settingJPanel.add(field_display, gridBagConstraints);

        displayJLabel.setText("Display Name:");
        displayJLabel.setMaximumSize(new Dimension(104, 22));
        displayJLabel.setMinimumSize(new Dimension(104, 22));
        displayJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        settingJPanel.add(displayJLabel, gridBagConstraints);

        field_width.setEditable(false);
        field_width.setColumns(20);
        field_width.setMaximumSize(new Dimension(134, 22));
        field_width.setMinimumSize(new Dimension(134, 22));
        field_width.setPreferredSize(new Dimension(134, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        settingJPanel.add(field_width, gridBagConstraints);

        field_height.setEditable(false);
        field_height.setColumns(20);
        field_height.setMaximumSize(new Dimension(134, 22));
        field_height.setMinimumSize(new Dimension(134, 22));
        field_height.setPreferredSize(new Dimension(134, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        settingJPanel.add(field_height, gridBagConstraints);

        button_generate.setText("Generate ID's");
        button_generate.setMaximumSize(new Dimension(104, 26));
        button_generate.setMinimumSize(new Dimension(104, 26));
        button_generate.setPreferredSize(new Dimension(104, 26));
        button_generate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                button_generateActionPerformed(evt);
            }
        });

        imageJButton.setLabel("Change Image");
        imageJButton.setMaximumSize(new Dimension(134, 26));
        imageJButton.setMinimumSize(new Dimension(134, 26));
        imageJButton.setPreferredSize(new Dimension(134, 26));
        imageJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                imageJButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Edit Information about this Resource");

        GroupLayout editorTabJPanelLayout = new GroupLayout(editorTabJPanel);
        editorTabJPanel.setLayout(editorTabJPanelLayout);
        editorTabJPanelLayout.setHorizontalGroup(
            editorTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(editorTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editorTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(editorTabJPanelLayout.createSequentialGroup()
                        .addGroup(editorTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(settingJPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGroup(editorTabJPanelLayout.createSequentialGroup()
                                .addComponent(button_generate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(imageJButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addGap(3, 3, 3))
                    .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        editorTabJPanelLayout.setVerticalGroup(
            editorTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(editorTabJPanelLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(settingJPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editorTabJPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(button_generate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(imageJButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mainTabbedPane.addTab("Editor Settings", editorTabJPanel);

        jPanel1.setMaximumSize(new Dimension(32767, 24));
        jPanel1.setMinimumSize(new Dimension(0, 24));
        jPanel1.setPreferredSize(new Dimension(456, 24));
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.LINE_AXIS));
        jPanel1.add(filler1);

        lockJButton.setToolTipText("");
        lockJButton.setHorizontalAlignment(SwingConstants.TRAILING);
        lockJButton.setHorizontalTextPosition(SwingConstants.LEADING);
        lockJButton.setMargin(new Insets(2, 0, 2, 0));
        lockJButton.setMaximumSize(new Dimension(326, 24));
        lockJButton.setMinimumSize(new Dimension(96, 24));
        lockJButton.setPreferredSize(new Dimension(136, 24));
        lockJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                lockJButtonActionPerformed(evt);
            }
        });
        jPanel1.add(lockJButton);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonJPanel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 546, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(renderJScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 276, GroupLayout.PREFERRED_SIZE)
                            .addComponent(mainTabbedPane, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 276, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(renderJScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonJPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed

        //
        setVisible(false);
    }//GEN-LAST:event_cancelJButtonActionPerformed

    private void commitJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_commitJButtonActionPerformed

        // The way to check is far below this
        if (box_delegate.isSelected()) {
            // Apply current changes
            commit();
        } else {
            JOptionPane.showMessageDialog(this, "Please check the information provided for fields marked RED");
        }
    }//GEN-LAST:event_commitJButtonActionPerformed

    private void scriptJTableMouseClicked(MouseEvent evt) {//GEN-FIRST:event_scriptJTableMouseClicked

        // TODO add your handling code here:
        int row = scriptJTable.rowAtPoint(evt.getPoint());
        int col = scriptJTable.columnAtPoint(evt.getPoint());

        if (evt.getClickCount() == 2 && row > -1 && col > -1) {
        }
    }//GEN-LAST:event_scriptJTableMouseClicked

    private void imageJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_imageJButtonActionPerformed
        // Open the PluginManager
        //        final ResourceSelector manager = new ResourceSelector(this, delegate, true);
        //        manager.setFilterType(Picture.class);
        //        manager.setResource(picture);
        //        manager.setLocationRelativeTo(this);
        //        manager.setVisible(true);
        //        manager.dispose();
        //
        //        // It will close on its own
        //        picture = (Picture) manager.getResource();
        //
        //        // Just for good measure
        //        try {
        //
        //            //
        //            if (picture != null) {
        //
        //                //
        //                illustration.setPicture(picture);
        //
        //                //
        //                imagePanel.updatePanel(illustration);
        //
        //                // Update and validate everything here
        //                binder.setResource(illustration);
        //                binder.setGraphic(picture);
        //                binder.updateControls(locationJField, packageJField, widthJField, heightJField, this);
        //
        //                //
        //                refresh();
        //
        //                // Quick check for backdrops
        //                if (illustration instanceof Backdrop) {
        //
        //                    //
        //                    final Image image = picture.getImage();
        //
        //                    //
        //                    blockWidthJField.setValue(image == null ? 0 : image.getWidth(this));
        //                    blockHeightJField.setValue(image == null ? 0 : image.getHeight(this));
        //                }
        //            }
        //        } catch (ClassCastException cce) {
        //            //
        //        }
    }//GEN-LAST:event_imageJButtonActionPerformed

    private void button_generateActionPerformed(ActionEvent evt) {//GEN-FIRST:event_button_generateActionPerformed

        // Just to make sure.
        //if (binder.isEditting() == false) {

        // Click this button to auto-generate all three forms of manifest-to-delegate identification
        binder.testButton();
        //}
    }//GEN-LAST:event_button_generateActionPerformed

    private void field_nameComponentResized(ComponentEvent evt) {//GEN-FIRST:event_field_nameComponentResized

        // TODO add your handling code here:
        final Dimension dimension = new Dimension(134, 22);

        // Destroy the layout manager to cool the auto resizing "feature"
        settingJPanel.setLayout(null);

        // TODO add your handling code here:
        //nameJField.setSize(dimension);
        nameJLabel.setSize(dimension);

        //
        field_reference.setSize(dimension);
        referenceJLabel.setSize(dimension);

        //
        field_display.setSize(dimension);
        displayJLabel.setSize(dimension);

        //
        field_plugin.setSize(dimension);
        packageJLabel.setSize(dimension);

        //
        field_location.setSize(dimension);
        locationJLabel.setSize(dimension);
    }//GEN-LAST:event_field_nameComponentResized

    private void itemJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_itemJButtonActionPerformed

        // Create an item shell
        double newChance;
        WorldItem newItem = null;

        // Create an item selector
        ItemSelector itemSelector = new ItemSelector(editor, newItem, true);
        itemSelector.setLocationRelativeTo(this);
        itemSelector.setVisible(true);

        // Item Selector takes its course and sets itself invisible on user input
        newChance = itemSelector.getChance();
        newItem = itemSelector.getItem();

        // Dispose of the Item Selector
        itemSelector.dispose();

        // Kick-Out
        if (newItem == null) {
            return;
        }

        // Adding a new Item to the Drop List
        DefaultTableModel newModel = (DefaultTableModel) itemJTable.getModel();

        //
        int[] modelInfo = getModelInfo(newModel, newItem);

        // Not Functioning Yet
        if (modelInfo == null) {
            newModel.addRow(new Object[]{newItem.getReferenceName(), newChance});
        } else {
            int foundRow = modelInfo[0];
            int foundColumn = modelInfo[1];
            newModel.setValueAt(newItem.getReferenceName(), foundRow, foundColumn);
            newModel.setValueAt(newChance, foundRow, foundColumn + 1);
        }
    }//GEN-LAST:event_itemJButtonActionPerformed

    private void itemJTableMouseClicked(MouseEvent evt) {//GEN-FIRST:event_itemJTableMouseClicked

        // TODO add your handling code here:
        int row = itemJTable.rowAtPoint(evt.getPoint());
        int col = itemJTable.columnAtPoint(evt.getPoint());

        if (evt.getClickCount() == 2 && row > -1 && col > -1) {

            // Open the Item Selector with preset values this time
            double newChance = (double) itemJTable.getValueAt(row, col + 1);
            //Item newItem = delegate.getItem(String.valueOf(itemJTable.getValueAt(row, col)));

            // Create an item selector
            //ItemSelector itemSelector = new ItemSelector(editor, newItem, true);

            // Load a few presets
            //itemSelector.setChance(newChance);
            //itemSelector.setItem(newItem);

            //
            //itemSelector.setLocationRelativeTo(this);
            //itemSelector.setVisible(true);

            //
            //itemJTable.setValueAt(itemSelector.getChance(), row, col + 1);
            //itemJTable.setValueAt(itemSelector.getItem().geteditorName(), row, col);

            //
            //itemSelector.dispose();
        }
    }//GEN-LAST:event_itemJTableMouseClicked

    private void resetJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_resetJButtonActionPerformed

        // TODO add your handling code here:
        setupDefaultValues();
    }//GEN-LAST:event_resetJButtonActionPerformed

    private void valueJFieldPropertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_valueJFieldPropertyChange

        //
        if (sourceButton == null) {
            return;
        }

        // TODO add your handling code here:
        final Object source = evt.getSource();

        //
        if (source == valueJField) {

            //
            final Number number = (Number) valueJField.getValue();

            //
            if (number == null) {
                return;
            }

            // Depends on the button
            if (sourceButton == attackDamageJButton) {

                //
                attackDamageChanged = number.intValue();
            } else if (sourceButton == attackDefenseJButton) {

                //
                attackDefenseChanged = number.intValue();
            } else if (sourceButton == magicDamageJButton) {

                //
                magicDamageChanged = number.intValue();
            }
        }
    }//GEN-LAST:event_valueJFieldPropertyChange

    private void lockJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_lockJButtonActionPerformed

        //
        final boolean bool = !lockJButton.isSelected();

        // Change to opposite icon
        lockJButton.setIcon(bool ? iconLock : iconUnlock);
        lockJButton.setText(bool ? "Permission Locked" : "Permission Unlocked");
        lockJButton.setToolTipText(bool ? "Click to Unlock Change Permissions" : "Click to Lock Change Permissions");
        binder.lock(bool);
    }//GEN-LAST:event_lockJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel attributeJLabel;
    private JPanel attributeJPanel;
    private JScrollPane attributeJScrollPane;
    private JTable attributeJTable;
    private JPanel attributeTabJPanel;
    private JPanel buttonJPanel;
    private JButton button_generate;
    private JButton cancelJButton;
    private JButton commitJButton;
    private JLabel displayJLabel;
    private JPanel editorTabJPanel;
    private JTextField field_display;
    private JTextField field_height;
    private JTextField field_location;
    private JTextField field_name;
    private JTextField field_plugin;
    private JTextField field_reference;
    private JTextField field_width;
    private Box.Filler filler1;
    private Box.Filler filler2;
    private Box.Filler filler3;
    private Box.Filler filler5;
    private JLabel heightJLabel;
    private JButton imageJButton;
    private JButton itemJButton;
    private JLabel itemJLabel;
    private JPanel itemJPanel;
    private JScrollPane itemJScrollPane;
    private JTable itemJTable;
    private JPanel itemTabJPanel;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JPanel jPanel1;
    private JLabel locationJLabel;
    private JToggleButton lockJButton;
    private JTabbedPane mainTabbedPane;
    private JLabel nameJLabel;
    private JLabel packageJLabel;
    private JLabel referenceJLabel;
    private JScrollPane renderJScrollPane;
    private JButton resetJButton;
    private JScrollPane scriptJScrollPane;
    private JTable scriptJTable;
    private JPanel scriptTabJPanel;
    private JPanel settingJPanel;
    private JFormattedTextField valueJField;
    private JLabel widthJLabel;
    // End of variables declaration//GEN-END:variables
}
