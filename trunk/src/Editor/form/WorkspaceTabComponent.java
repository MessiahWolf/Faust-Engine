/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.form;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Robert A. Cherry
 */
public class WorkspaceTabComponent extends JPanel {

    // Variable Declaration
    // Swing Classes
    private JButton closeJButton;
    private JLabel titleJLabel;
    private JTabbedPane workspacePane;
    // Data Types
    private String label;
    // End of Variable Declaration

    public WorkspaceTabComponent(JTabbedPane workspacePane, String label) {

        //
        super();

        //
        this.workspacePane = workspacePane;
        this.label = label;

        //
        init();
    }

    private void init() {

        //
        final Dimension buttonDimension = new Dimension(16, 16);
        final Dimension labelDimension = new Dimension(72, 16);
        final Dimension panelDimension = new Dimension(88, 16);
        //
        setPreferredSize(panelDimension);
        setMaximumSize(panelDimension);
        setMinimumSize(panelDimension);

        //
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setOpaque(false);

        //
        closeJButton = new JButton();
        closeJButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                //
                final int index = workspacePane.indexOfTabComponent(closeJButton.getParent());

                //
                if (index != 0) {

                    // Remove
                    workspacePane.removeTabAt(index);
                } else {

                    final String message = "The first workspace cannot be closed.";

                    //
                    Container editor = closeJButton;

                    try {
                        
                        //
                        editor = editor.getParent().getParent().getParent();

                        //
                        JOptionPane.showMessageDialog(editor, message);
                    } catch (NullPointerException npe) {
                        System.err.println("ERR.");
                    }
                }
            }
        });

        //
        closeJButton.setContentAreaFilled(false);
        closeJButton.setFocusPainted(false);
        closeJButton.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/icon-close16.png"))));
        closeJButton.setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/icon-close-hover16.png"))));

        //
        closeJButton.setPreferredSize(buttonDimension);
        closeJButton.setMaximumSize(buttonDimension);
        closeJButton.setMinimumSize(buttonDimension);

        //
        titleJLabel = new JLabel(label);

        //
        titleJLabel.setPreferredSize(labelDimension);
        titleJLabel.setMaximumSize(labelDimension);
        titleJLabel.setMinimumSize(labelDimension);

        //
        add(titleJLabel);
        add(closeJButton);
    }
}
