/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.form;

import io.resource.ResourceDelegate;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

/**
 *
 * @author Robert A. Cherry
 */
public class DelegateCheckBox extends JCheckBox {

    // Variable Declaration
    // Swing Classes
    private JTextField nameJField;
    private JTextField displayJField;
    private JTextField referenceJField;
    // Project Classes
    private ResourceDelegate delegate;
    // Data Types
    private boolean edit;
    // End of Variable Delcaration

    public DelegateCheckBox(ResourceDelegate delegate) {

        // Call to super
        super();

        //
        this.delegate = delegate;
    }

    public void init(JTextField referenceJField, JTextField nameJField, JTextField displayJField) {

        //
        this.nameJField = nameJField;
        this.referenceJField = referenceJField;
        this.displayJField = displayJField;

        //
        setText("Valid Manifest Credentials");
        setEnabled(false);
        setSelected(false);

        //
        final Dimension size = new Dimension(180, 26);

        //
        setPreferredSize(size);
        setMaximumSize(size);
    }

    public void update() {

        //
        int count = 0;

        if (edit == false) {

            //
            count += delegate.isAcceptableID(ResourceDelegate.ID_EDITOR_NAME,nameJField.getText()) ? 1 : 0;
            count += delegate.isAcceptableID(ResourceDelegate.ID_EDITOR_REFERENCE, referenceJField.getText()) ? 1 : 0;
            count += delegate.isAcceptableID(ResourceDelegate.ID_EDITOR_DISPLAY, displayJField.getText()) ? 1 : 0;

            //
            if (count == 3) {

                //
                setSelected(true);
            } else {
                setSelected(false);
            }
        } else {
            
            // Auto-accept
            setSelected(true);
        }
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
}
