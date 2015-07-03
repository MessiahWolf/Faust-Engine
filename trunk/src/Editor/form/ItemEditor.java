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

import core.world.WorldAction;
import core.world.Animation;
import core.world.WorldItem;
import core.world.item.Weapon;
import io.resource.ResourceDelegate;
import io.resource.DataPackage;
import io.resource.ResourceReader;
import io.resource.ResourceWriter;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import Editor.listener.ManifestBinder;

/**
 *
 * @author Robert A. Cherry
 */
public class ItemEditor extends javax.swing.JDialog implements TableModelListener {

    // Variable Declaration
    // Java Classes
    private HashMap<String, Object> editedStatMap;
    // Project Classes
    private ManifestBinder binder;
    private DataPackage pack;
    private DelegateCheckBox box_delegate;
    private ImagePanel imageJPanel;
    private ResourceDelegate delegate;
    private WorldItem weapon;
    // End of Variable Declaration

    public ItemEditor(Window window, ResourceDelegate delegate, Weapon weapon, boolean modal) {

        //
        super(window);
        super.setModal(modal);

        //;
        this.delegate = delegate;
        this.weapon = weapon;

        // Init Components
        initComponents();

        // Initialize
        init();
    }

    private void init() {

        //
        final Class closs = getClass();

        //
        final ImageIcon iconAdd = ResourceReader.readClassPathIcon(closs, "/icons/icon-add24.png");

        //
        setupPackage();

        //
        setupTables();

        // Our wrapper class for the delegate stuff.
        box_delegate = new DelegateCheckBox(delegate);
        buttonJPanel.add(box_delegate, 0);

        //
        boolean edit = false;

        // Testing it out.
        binder = new ManifestBinder(delegate, weapon);

        // Binding stuff manually.
        binder.bind(ManifestBinder.BOX_DELEGATE, box_delegate);
        binder.bind(ManifestBinder.BUTTON_GENERATE, button_generate);

        // Fields
        binder.bind(ManifestBinder.FIELD_DISPLAY, field_display);
        binder.bind(ManifestBinder.FIELD_REFERENCE, field_name);
        binder.bind(ManifestBinder.FIELD_NAME, field_name);
        binder.bind(ManifestBinder.FIELD_PLUGIN, field_plugin);
        binder.bind(ManifestBinder.FIELD_LOCATION, field_location);
        //binder.bind(ManifestBinder.FIELD_WIDTH, field_width);
        //binder.bind(ManifestBinder.FIELD_HEIGHT, field_height);

        // Invoke the wrath of the Manifest Binder. >:[
        binder.invoke();
        binder.setEdit(edit);

        //
        settingJPanel.setLayout(null);

        //
        if (edit) {

            //
            button_generate.setEnabled(false);

            // Keeps JFields from being automatically resized to abstract widths and heights by layout manager. My quick fix.
            settingJPanel.setLayout(null);
        }

        // Attribute hashmap copy
        editedStatMap = new HashMap<>();
        editedStatMap.putAll(weapon.getStatMap());

        //
        imageJPanel = new ImagePanel(renderJScrollPane);

        //
        addJButton.setIcon(iconAdd);

        //
        renderJScrollPane.setViewportView(imageJPanel);
    }

    private void write() {

        // Write the resource to file
        ResourceWriter.write(delegate, weapon);

        // Add to the delegate
        delegate.addResource(weapon);

        // Close this dialog
        setVisible(false);
    }

    private void finish() {

        // The way to check is far below this
        if (box_delegate.isSelected()) {

            // Apply attribute changes
            weapon.setAttributeMap(editedStatMap);

            //
            for (int row = 0; row < actionJTable.getRowCount(); row++) {

                //
                final WorldAction action = (WorldAction) actionJTable.getValueAt(row, 0);
                final Animation anim = (Animation) actionJTable.getValueAt(row, 1);

                // Set all he new Animations
                weapon.setAnimation(action, anim);
            }

            // Grab from JTextFields and apply name change
            weapon.setReferenceName(binder.getReferenceName());
            weapon.setReferenceID(binder.getReferenceID());
            weapon.setDisplayName(binder.getDisplayName());
            weapon.setStatMap(editedStatMap);
            weapon.validate();

            // Write to system
            write();
        } else {
            JOptionPane.showMessageDialog(this, "Please check the information provided for fields marked RED");
        }
    }

    private void setupPackage() {

        //
        if (pack == null) {

            // Find the package associated with this weapon
            pack = delegate.getPackageForResource(weapon);
        }
    }

    private void setupTables() {

        // Custom Models
        final DefaultTableModel statModel = new DefaultTableModel();

        // Filling Attribute JTable
        final String[][] dataVector = new String[weapon.getStatMap().size()][];
        int dataIndex = 0;

        //
        for (Map.Entry<String, Object> set : weapon.getStatMap().entrySet()) {

            //
            dataVector[dataIndex] = new String[]{set.getKey(), String.valueOf(set.getValue())};
            dataIndex++;
        }

        // Apply Data taken from Actor to JTable Model
        statModel.setDataVector(dataVector, new String[]{"Stat", "Value"});

        // Apply Models to JTables
        statJTable.setModel(statModel);

        // Custom Table Options for First Table
        statJTable.setRowHeight(18);
        statJTable.getModel().addTableModelListener(this);

        //
        final DefaultTableModel actionModel = new DefaultTableModel();

        final Object[][] objectVector = new Object[WorldAction.values().length][];

        //
        for (int i = 0; i < WorldAction.values().length; i++) {

            //
            final WorldAction action = WorldAction.values()[i];

            if (weapon == null) {
                objectVector[i] = new Object[]{action, null};
            } else {
                objectVector[i] = new Object[]{action, weapon.getAnimation(action)};
            }
        }

        //
        actionModel.setDataVector(objectVector, new String[]{"Action", "Animation"});

        //
        actionJTable.setModel(actionModel);
        actionJTable.setRowHeight(18);
        actionJTable.getModel().addTableModelListener(this);
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

        renderJScrollPane = new JScrollPane();
        buttonJPanel = new JPanel();
        filler5 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        filler1 = new Filler(new Dimension(8, 0), new Dimension(8, 0), new Dimension(8, 32767));
        finishJButton = new JButton();
        filler3 = new Filler(new Dimension(8, 0), new Dimension(8, 0), new Dimension(8, 32767));
        cancelJButton = new JButton();
        mainTabbedPane = new JTabbedPane();
        statTabJPanel = new JPanel();
        attributeJScrollPane = new JScrollPane();
        statJTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return false;
                }
                return true;
            }
        };
        actionJPanel1 = new JPanel();
        actionJLabel1 = new JLabel();
        filler2 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        addJButton = new JButton();
        actionTabJPanel = new JPanel();
        actionJPanel = new JPanel();
        actionJLabel = new JLabel();
        actionJScrollPane = new JScrollPane();
        actionJTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable the editoing of all rows and columns
                return false;
            }
        };
        manifestJPanel = new JPanel();
        settingJPanel = new JPanel();
        field_location = new JTextField();
        locationJLabel = new JLabel();
        pluginJLabel = new JLabel();
        field_plugin = new JTextField();
        referenceJLabel = new JLabel();
        nameJLabel = new JLabel();
        field_id = new JTextField();
        field_name = new JTextField();
        field_display = new JTextField();
        displayJLabel = new JLabel();
        nullLabel3 = new JLabel();
        button_generate = new JButton();
        jLabel2 = new JLabel();
        guideJButton = new JButton();
        worldJPanel = new JPanel();
        jLabel1 = new JLabel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Item Editing");
        setMinimumSize(new Dimension(560, 384));
        setResizable(false);

        renderJScrollPane.setMaximumSize(new Dimension(196, 196));
        renderJScrollPane.setMinimumSize(new Dimension(196, 196));
        renderJScrollPane.setPreferredSize(new Dimension(196, 196));

        buttonJPanel.setMaximumSize(new Dimension(620, 26));
        buttonJPanel.setMinimumSize(new Dimension(620, 26));
        buttonJPanel.setPreferredSize(new Dimension(620, 26));
        buttonJPanel.setLayout(new BoxLayout(buttonJPanel, BoxLayout.LINE_AXIS));
        buttonJPanel.add(filler5);
        buttonJPanel.add(filler1);

        finishJButton.setText("Finish");
        finishJButton.setMaximumSize(new Dimension(88, 26));
        finishJButton.setMinimumSize(new Dimension(88, 26));
        finishJButton.setPreferredSize(new Dimension(88, 26));
        finishJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                finishJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(finishJButton);
        buttonJPanel.add(filler3);

        cancelJButton.setText("Cancel");
        cancelJButton.setMaximumSize(new Dimension(88, 26));
        cancelJButton.setMinimumSize(new Dimension(88, 26));
        cancelJButton.setPreferredSize(new Dimension(88, 26));
        cancelJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(cancelJButton);

        mainTabbedPane.setMaximumSize(new Dimension(248, 380));
        mainTabbedPane.setMinimumSize(new Dimension(248, 380));
        mainTabbedPane.setPreferredSize(new Dimension(276, 390));

        statTabJPanel.setMaximumSize(new Dimension(300, 320));
        statTabJPanel.setMinimumSize(new Dimension(300, 320));
        statTabJPanel.setPreferredSize(new Dimension(300, 320));

        attributeJScrollPane.setMaximumSize(new Dimension(248, 202));
        attributeJScrollPane.setMinimumSize(new Dimension(248, 202));
        attributeJScrollPane.setPreferredSize(new Dimension(248, 202));

        statJTable.setModel(new DefaultTableModel(
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
        statJTable.setFillsViewportHeight(true);
        statJTable.setMaximumSize(new Dimension(248, 202));
        statJTable.setMinimumSize(new Dimension(248, 202));
        statJTable.setPreferredSize(new Dimension(248, 202));
        attributeJScrollPane.setViewportView(statJTable);

        actionJPanel1.setMaximumSize(new Dimension(287, 22));
        actionJPanel1.setMinimumSize(new Dimension(287, 22));
        actionJPanel1.setPreferredSize(new Dimension(287, 22));
        actionJPanel1.setLayout(new BoxLayout(actionJPanel1, BoxLayout.LINE_AXIS));

        actionJLabel1.setText("Adjust In-Game Stats for this Item");
        actionJLabel1.setToolTipText("The list of Items that this actor drops upon death and the chance to drop each item");
        actionJLabel1.setEnabled(false);
        actionJLabel1.setMaximumSize(new Dimension(204, 22));
        actionJLabel1.setMinimumSize(new Dimension(204, 22));
        actionJLabel1.setPreferredSize(new Dimension(204, 22));
        actionJPanel1.add(actionJLabel1);
        actionJPanel1.add(filler2);

        addJButton.setContentAreaFilled(false);
        addJButton.setMaximumSize(new Dimension(24, 24));
        addJButton.setMinimumSize(new Dimension(24, 24));
        addJButton.setPreferredSize(new Dimension(24, 24));
        addJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addJButtonActionPerformed(evt);
            }
        });
        actionJPanel1.add(addJButton);

        GroupLayout statTabJPanelLayout = new GroupLayout(statTabJPanel);
        statTabJPanel.setLayout(statTabJPanelLayout);
        statTabJPanelLayout.setHorizontalGroup(
            statTabJPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(statTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statTabJPanelLayout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(attributeJScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(actionJPanel1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        statTabJPanelLayout.setVerticalGroup(
            statTabJPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(statTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(actionJPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(attributeJScrollPane, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(94, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Stats", statTabJPanel);

        actionJPanel.setMaximumSize(new Dimension(287, 22));
        actionJPanel.setMinimumSize(new Dimension(287, 22));
        actionJPanel.setPreferredSize(new Dimension(287, 22));
        actionJPanel.setLayout(new BoxLayout(actionJPanel, BoxLayout.LINE_AXIS));

        actionJLabel.setText("Sync Animations to Actions");
        actionJLabel.setToolTipText("The list of Items that this actor drops upon death and the chance to drop each item");
        actionJLabel.setEnabled(false);
        actionJLabel.setMaximumSize(new Dimension(204, 22));
        actionJLabel.setMinimumSize(new Dimension(204, 22));
        actionJLabel.setPreferredSize(new Dimension(204, 22));
        actionJPanel.add(actionJLabel);

        actionJScrollPane.setMaximumSize(new Dimension(248, 202));
        actionJScrollPane.setMinimumSize(new Dimension(248, 202));
        actionJScrollPane.setPreferredSize(new Dimension(248, 202));

        actionJTable.setModel(new DefaultTableModel(
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
        actionJTable.setFillsViewportHeight(true);
        actionJTable.setMaximumSize(new Dimension(248, 204));
        actionJTable.setMinimumSize(new Dimension(248, 204));
        actionJTable.setPreferredSize(new Dimension(248, 204));
        actionJTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                actionJTableMouseClicked(evt);
            }
        });
        actionJScrollPane.setViewportView(actionJTable);

        GroupLayout actionTabJPanelLayout = new GroupLayout(actionTabJPanel);
        actionTabJPanel.setLayout(actionTabJPanelLayout);
        actionTabJPanelLayout.setHorizontalGroup(
            actionTabJPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, actionTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(actionTabJPanelLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(actionJPanel, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(actionJScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
        );
        actionTabJPanelLayout.setVerticalGroup(
            actionTabJPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(actionTabJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(actionJPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(actionJScrollPane, GroupLayout.PREFERRED_SIZE, 187, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainTabbedPane.addTab("Action Settings", actionTabJPanel);

        settingJPanel.setMaximumSize(new Dimension(213, 130));
        settingJPanel.setMinimumSize(new Dimension(213, 130));
        settingJPanel.setPreferredSize(new Dimension(213, 130));
        GridBagLayout settingJPanelLayout = new GridBagLayout();
        settingJPanelLayout.columnWidths = new int[] {0, 5, 0};
        settingJPanelLayout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0};
        settingJPanel.setLayout(settingJPanelLayout);

        field_location.setColumns(20);
        field_location.setEnabled(false);
        field_location.setMaximumSize(new Dimension(132, 22));
        field_location.setMinimumSize(new Dimension(132, 22));
        field_location.setPreferredSize(new Dimension(132, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        settingJPanel.add(field_location, gridBagConstraints);

        locationJLabel.setText("File Location:");
        locationJLabel.setInheritsPopupMenu(false);
        locationJLabel.setMaximumSize(new Dimension(104, 22));
        locationJLabel.setMinimumSize(new Dimension(104, 22));
        locationJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        settingJPanel.add(locationJLabel, gridBagConstraints);

        pluginJLabel.setText("Part of Package:");
        pluginJLabel.setMaximumSize(new Dimension(104, 22));
        pluginJLabel.setMinimumSize(new Dimension(104, 22));
        pluginJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        settingJPanel.add(pluginJLabel, gridBagConstraints);

        field_plugin.setColumns(20);
        field_plugin.setEnabled(false);
        field_plugin.setMaximumSize(new Dimension(132, 22));
        field_plugin.setMinimumSize(new Dimension(132, 22));
        field_plugin.setPreferredSize(new Dimension(132, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        settingJPanel.add(field_plugin, gridBagConstraints);

        referenceJLabel.setText("Reference ID:");
        referenceJLabel.setMaximumSize(new Dimension(104, 22));
        referenceJLabel.setMinimumSize(new Dimension(104, 22));
        referenceJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        settingJPanel.add(referenceJLabel, gridBagConstraints);

        nameJLabel.setText("Reference Name:");
        nameJLabel.setMaximumSize(new Dimension(104, 22));
        nameJLabel.setMinimumSize(new Dimension(104, 22));
        nameJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        settingJPanel.add(nameJLabel, gridBagConstraints);

        field_id.setColumns(20);
        field_id.setToolTipText("");
        field_id.setMaximumSize(new Dimension(132, 22));
        field_id.setMinimumSize(new Dimension(132, 22));
        field_id.setPreferredSize(new Dimension(132, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        settingJPanel.add(field_id, gridBagConstraints);

        field_name.setColumns(20);
        field_name.setMaximumSize(new Dimension(132, 22));
        field_name.setMinimumSize(new Dimension(132, 22));
        field_name.setPreferredSize(new Dimension(132, 22));
        field_name.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                field_nameComponentResized(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        settingJPanel.add(field_name, gridBagConstraints);

        field_display.setColumns(20);
        field_display.setMaximumSize(new Dimension(132, 22));
        field_display.setMinimumSize(new Dimension(132, 22));
        field_display.setPreferredSize(new Dimension(132, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        settingJPanel.add(field_display, gridBagConstraints);

        displayJLabel.setText("Display Name:");
        displayJLabel.setMaximumSize(new Dimension(104, 22));
        displayJLabel.setMinimumSize(new Dimension(104, 22));
        displayJLabel.setPreferredSize(new Dimension(104, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        settingJPanel.add(displayJLabel, gridBagConstraints);

        nullLabel3.setText("Edit information about this Item");
        nullLabel3.setEnabled(false);

        button_generate.setText("Generate ID's");
        button_generate.setMaximumSize(new Dimension(100, 26));
        button_generate.setMinimumSize(new Dimension(100, 26));
        button_generate.setPreferredSize(new Dimension(100, 26));
        button_generate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                button_generateActionPerformed(evt);
            }
        });

        jLabel2.setText("Manifest ID Guidlines");
        jLabel2.setEnabled(false);

        guideJButton.setMaximumSize(new Dimension(24, 24));
        guideJButton.setMinimumSize(new Dimension(24, 24));
        guideJButton.setPreferredSize(new Dimension(24, 24));
        guideJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guideJButtonActionPerformed(evt);
            }
        });

        GroupLayout manifestJPanelLayout = new GroupLayout(manifestJPanel);
        manifestJPanel.setLayout(manifestJPanelLayout);
        manifestJPanelLayout.setHorizontalGroup(
            manifestJPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(manifestJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(manifestJPanelLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(nullLabel3, GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                    .addGroup(manifestJPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(guideJButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
                    .addGroup(manifestJPanelLayout.createSequentialGroup()
                        .addComponent(button_generate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(settingJPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        manifestJPanelLayout.setVerticalGroup(
            manifestJPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, manifestJPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(nullLabel3)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(settingJPanel, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(button_generate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(manifestJPanelLayout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(guideJButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mainTabbedPane.addTab("Manifest Settings", manifestJPanel);

        GroupLayout worldJPanelLayout = new GroupLayout(worldJPanel);
        worldJPanel.setLayout(worldJPanelLayout);
        worldJPanelLayout.setHorizontalGroup(
            worldJPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 271, Short.MAX_VALUE)
        );
        worldJPanelLayout.setVerticalGroup(
            worldJPanelLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 237, Short.MAX_VALUE)
        );

        mainTabbedPane.addTab("World Settings", worldJPanel);

        jLabel1.setText("Animation Cycle");
        jLabel1.setEnabled(false);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonJPanel, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(renderJScrollPane, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(mainTabbedPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(renderJScrollPane, GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                .addPreferredGap(ComponentPlacement.UNRELATED, 13, Short.MAX_VALUE)
                .addComponent(buttonJPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        //
        setVisible(false);
    }//GEN-LAST:event_cancelJButtonActionPerformed

    private void finishJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_finishJButtonActionPerformed
        // Apply current changes
        finish();
    }//GEN-LAST:event_finishJButtonActionPerformed

    private void actionJTableMouseClicked(MouseEvent evt) {//GEN-FIRST:event_actionJTableMouseClicked

        // TODO add your handling code here:
        int row = actionJTable.rowAtPoint(evt.getPoint());
        int col = actionJTable.columnAtPoint(evt.getPoint());

        //
        if (evt.getClickCount() == 2 && row > -1 && col > -1) {

            //
            WorldAction act = (WorldAction) actionJTable.getValueAt(row, 0);
            Animation anim = (Animation) actionJTable.getValueAt(row, 1);

            // Show the action editor
            final AnimationBinder maker = new AnimationBinder(null, delegate, act, anim, true);
            maker.setLocationRelativeTo(this);
            maker.setVisible(true);

            //
            maker.dispose();

            // Grab the created action and trigger
            anim = maker.getAnimation();
            act = maker.getAction();

            if (act != null && anim != null) {

                //
                final Object[] dataVector = {act, anim};

                // Place values into the JTable
                final DefaultTableModel model = (DefaultTableModel) actionJTable.getModel();
                model.removeRow(row);
                model.insertRow(row, dataVector);
            }
        }
    }//GEN-LAST:event_actionJTableMouseClicked

    private void addJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addJButtonActionPerformed
        //
    }//GEN-LAST:event_addJButtonActionPerformed

    private void field_nameComponentResized(ComponentEvent evt) {//GEN-FIRST:event_field_nameComponentResized

        //
        final Dimension dimension = new Dimension(132, 22);

        // Destroy the layout manager to cool the auto resizing "feature"
        settingJPanel.setLayout(null);

        // TODO add your handling code here:
        field_name.setSize(dimension);
        nameJLabel.setSize(dimension);

        //
        field_id.setSize(dimension);
        referenceJLabel.setSize(dimension);

        //
        field_display.setSize(dimension);
        displayJLabel.setSize(dimension);

        //
        field_plugin.setSize(dimension);
        pluginJLabel.setSize(dimension);

        //
        field_location.setSize(dimension);
        locationJLabel.setSize(dimension);
    }//GEN-LAST:event_field_nameComponentResized

    private void button_generateActionPerformed(ActionEvent evt) {//GEN-FIRST:event_button_generateActionPerformed

        // Just to make sure.
        if (binder.isEditting() == false) {

            // Click this button to auto-generate all three forms of manifest-to-delegate identification
            binder.testButton();
        }
    }//GEN-LAST:event_button_generateActionPerformed

    private void guideJButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guideJButtonActionPerformed
        // TODO add your handling code here:
        // GuideJDialog dialog = new GuideJDialog();
    }//GEN-LAST:event_guideJButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel actionJLabel;
    private JLabel actionJLabel1;
    private JPanel actionJPanel;
    private JPanel actionJPanel1;
    private JScrollPane actionJScrollPane;
    private JTable actionJTable;
    private JPanel actionTabJPanel;
    private JButton addJButton;
    private JScrollPane attributeJScrollPane;
    private JPanel buttonJPanel;
    private JButton button_generate;
    private JButton cancelJButton;
    private JLabel displayJLabel;
    private JTextField field_display;
    private JTextField field_id;
    private JTextField field_location;
    private JTextField field_name;
    private JTextField field_plugin;
    private Filler filler1;
    private Filler filler2;
    private Filler filler3;
    private Filler filler5;
    private JButton finishJButton;
    private JButton guideJButton;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel locationJLabel;
    private JTabbedPane mainTabbedPane;
    private JPanel manifestJPanel;
    private JLabel nameJLabel;
    private JLabel nullLabel3;
    private JLabel pluginJLabel;
    private JLabel referenceJLabel;
    private JScrollPane renderJScrollPane;
    private JPanel settingJPanel;
    private JTable statJTable;
    private JPanel statTabJPanel;
    private JPanel worldJPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void tableChanged(TableModelEvent e) {

        // Grab the Model of the Changed Table in this case AncestorTable is a given.
        final TableModel model = (TableModel) e.getSource();

        // Get the selected Row and Column
        final int selectedRow = e.getFirstRow();
        final int selectedColumn = e.getColumn();

        if (selectedRow > -1 && selectedColumn > -1) {

            // Get the Name of the Column Changed for Parse Method
            final String selectedColumnName = model.getColumnName(selectedColumn);

            // Get the Value of the Changed Column for Parse Method
            final String selectedColumnValue = String.valueOf(model.getValueAt(selectedRow, selectedColumn));

            //
            if (statJTable.getModel() == model) {

                // Apply settings to stat map
                editedStatMap.put(selectedColumnName, selectedColumnValue);
            }
        }
    }
}
