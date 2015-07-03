/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.listener;

import Editor.form.DelegateCheckBox;
import io.resource.ResourceDelegate;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Robert A. Cherry
 */
public class DelegateFieldListener implements DocumentListener {

    // Java Native Classes
    private Color colorConflict;
    private Color colorValid;
    private Color colorError;
    // Swing Native Classes
    private JTextField field;
    // Project Classes
    private DelegateCheckBox box;
    private ManifestBinder binder;
    private ResourceDelegate delegate;
    // Data Types
    private int type;
    // End of Variable Declaration

    public DelegateFieldListener(ResourceDelegate delegate, ManifestBinder binder, JTextField field, DelegateCheckBox box, int type, Color[] colors) {

        //
        try {

            //
            this.delegate = delegate;
            this.binder = binder;
            this.field = field;
            this.type = type;
            this.box = box;

            // Our Colors
            colorValid = colors[0];
            colorConflict = colors[1];
            colorError = colors[2];

            //
            //final Dimension size = new Dimension(128, 26);

            //
            //field.setPreferredSize(size);
            //field.setMaximumSize(size);
            //field.setMinimumSize(size);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException aioobe) {
            System.err.println("EOF.");
        }
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        updateLabel(event);
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        updateLabel(event);
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        updateLabel(event);
    }

    public void update() {
        generateRunnable(null);
    }

    private void updateLabel(DocumentEvent event) {

        // We did it. :D
        EventQueue.invokeLater(generateRunnable(event));
    }

    private Runnable generateRunnable(DocumentEvent event) {

        // This tells the sytem to do this as soon as it can
        final Runnable run = new Runnable() {
            @Override
            public void run() {

                // Grab the text from the nameJField
                final String text = field.getText();

                // Check if this is a valid resource name
                if (delegate.isAcceptableID(type, text)) {

                    // Deliver the message
                    field.setToolTipText("\'" + text + "\' is a valid resource identifier");

                    // Change to GREEN
                    field.setForeground(colorValid);
                } else {

                    //
                    if (binder == null) {
                        return;
                    }

                    // So if we are not editing an existing resource
                    if (binder.isEditting() == false) {

                        // Deliver the message
                        field.setToolTipText("\'" + text + (delegate.isConflicted(type, text) ? "\' is already in use" : "\' contains illegal characters"));

                        //
                        field.setForeground(delegate.isConflicted(type, text) ? colorConflict : colorError);
                    }
                }

                // Update Checkbox
                box.update();
            }
        };

        // Return our new runnable object
        return run;
    }
}
