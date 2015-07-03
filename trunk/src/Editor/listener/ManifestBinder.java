/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.listener;

import core.world.Picture;
import core.world.WorldResource;
import Editor.form.DelegateCheckBox;
import io.resource.DataPackage;
import io.resource.ResourceDelegate;
import java.awt.Color;
import java.awt.image.ImageObserver;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author Robert A. Cherry
 */
public class ManifestBinder {

    // Variable Declaration
    // Swing Classes
    private JButton button_generate;
    private JTextField field_reference;
    private JTextField field_display;
    private JTextField field_name;
    private JTextField field_location;
    private JTextField field_width;
    private JTextField field_height;
    private JTextField field_plugin;
    // Java Native Classes
    private DelegateCheckBox box;
    private DelegateFieldListener listener_name;
    private DelegateFieldListener listener_reference;
    private DelegateFieldListener listener_display;
    // Project Classes
    private DataPackage pack;
    private ResourceDelegate delegate;
    private WorldResource resource;
    // Data Types
    private boolean edit;
    public final static int BUTTON_GENERATE = 4000;
    public final static int BOX_DELEGATE = 0;
    public final static int FIELD_REFERENCE = 3000;
    public final static int FIELD_DISPLAY = 2000;
    public final static int FIELD_NAME = 1000;
    public final static int FIELD_WIDTH = 5000;
    public final static int FIELD_HEIGHT = 6000;
    public final static int FIELD_PLUGIN = 7000;
    public final static int FIELD_LOCATION = 8000;
    // End of Variable Declaration

    public ManifestBinder(ResourceDelegate delegate, WorldResource resource) {

        //
        this.delegate = delegate;
        this.resource = resource;
    }

    public ManifestBinder(ResourceDelegate delegate, DataPackage pack) {

        //
        this.delegate = delegate;
        this.pack = pack;
    }

    public void bind(int key, JComponent component) throws ClassCastException {

        switch (key) {
            case BUTTON_GENERATE:
                button_generate = (JButton) component;
                break;
            case BOX_DELEGATE:
                box = (DelegateCheckBox) component;
                break;
            case FIELD_REFERENCE:
                field_reference = (JTextField) component;
                break;
            case FIELD_DISPLAY:
                field_display = (JTextField) component;
                break;
            case FIELD_NAME:
                field_name = (JTextField) component;
                break;
            case FIELD_PLUGIN:
                field_plugin = (JTextField) component;
                break;
            case FIELD_LOCATION:
                field_location = (JTextField) component;
                break;
            case FIELD_WIDTH:
                field_width = (JTextField) component;
                break;
            case FIELD_HEIGHT:
                field_height = (JTextField) component;
                break;
        }
    }

    private void init() {

        //
        button_generate.setFocusPainted(false);

        //
        box.init(field_reference, field_name, field_display);
    }

    public void bindImage(Picture graphic, ImageObserver observer) {

        //
        if (graphic != null) {

            //
            resource = graphic;

            // Update Graphic Location
            field_location.setText(graphic.getDisplayName());

            // Update Width Label
            field_width.setText(String.valueOf(graphic.getImage().getWidth(observer)));
            field_width.setToolTipText(field_width.getText());

            // Update Height Label
            field_height.setText(String.valueOf(graphic.getImage().getHeight(observer)));
            field_height.setToolTipText(field_height.getText());

            // Update Package Field
            field_plugin.setText(pack == null ? ResourceDelegate.UNPACKAGED_STATEMENT : pack.getDisplayName());
        }
    }

    public void testButton() {

        // Click this button to auto-generate all three forms of delegate identification
        String a = null;
        String b = null;
        String c = null;

        // Solve for packages and resources
        if (resource == null && pack != null) {

            //
            a = delegate.generateID(ResourceDelegate.ID_EDITOR_REFERENCE, pack.getClass(), 0, 15);
            b = delegate.generateID(ResourceDelegate.ID_EDITOR_NAME, pack.getClass(), 0, 15);
            c = delegate.generateID(ResourceDelegate.ID_EDITOR_DISPLAY, pack.getClass(), 0, 15);
        } else if (resource != null && pack == null) {

            //
            a = delegate.generateID(ResourceDelegate.ID_EDITOR_REFERENCE, resource.getClass(), 0, 15);
            b = delegate.generateID(ResourceDelegate.ID_EDITOR_NAME, resource.getClass(), 0, 15);
            c = delegate.generateID(ResourceDelegate.ID_EDITOR_DISPLAY, resource.getClass(), 0, 15);
        }

        // Apply to fields.
        field_reference.setText(a);
        field_name.setText(b);
        field_display.setText(c);
    }

    public void invoke() {

        //
        init();

        // Copy from here
        final Color colorValid = new Color(46, 164, 4);
        final Color colorConflict = new Color(255, 180, 0);
        final Color colorError = new Color(255, 32, 32);

        //
        final Color[] colors = {colorValid, colorConflict, colorError};

        // Nifty :|
        listener_name = new DelegateFieldListener(delegate, this, field_name, box, ResourceDelegate.ID_EDITOR_NAME, colors);
        listener_reference = new DelegateFieldListener(delegate, this, field_reference, box, ResourceDelegate.ID_EDITOR_REFERENCE, colors);
        listener_display = new DelegateFieldListener(delegate, this, field_display, box, ResourceDelegate.ID_EDITOR_DISPLAY, colors);

        // Grab the name of the animation
        field_name.getDocument().addDocumentListener(listener_name);
        field_reference.getDocument().addDocumentListener(listener_reference);
        field_display.getDocument().addDocumentListener(listener_display);

        //
        if (resource == null) {

            //
            field_name.setText(pack.getReferenceName());
            field_reference.setText(pack.getReferenceId());
            field_display.setText(pack.getDisplayName());
        } else {

            //
            field_name.setText(resource.getReferenceName());
            field_reference.setText(resource.getReferenceID());
            field_display.setText(resource.getDisplayName());
        }

        //@nyi
        if (delegate.isGeneratingEditorIds() == false) {
        }

        // Disable changing tags
        if (edit) {
            field_name.setEnabled(false);
            field_reference.setEnabled(false);
            field_display.setEnabled(false);
            box.setSelected(true);
        }
    }

    public String getReferenceID() {
        return field_reference.getText();
    }

    public String getReferenceName() {
        return field_name.getText();
    }

    public String getDisplayName() {
        return field_display.getText();
    }

    public WorldResource getResource() {
        return resource;
    }

    public boolean isEditting() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;

        // Disable changing tags
        if (edit) {

            //
            field_name.setEnabled(false);
            field_reference.setEnabled(false);
            field_display.setEnabled(false);

            //
            listener_name.update();
            listener_reference.update();
            listener_display.update();

            //
            box.setSelected(true);
            box.setEdit(true);
        }
    }

    public void setResource(WorldResource resource) {
        this.resource = resource;
    }
}
