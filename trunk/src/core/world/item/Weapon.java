/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.world.item;

import core.world.WorldAction;
import core.world.Animation;
import core.world.WorldItem;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 *
 * @author Robert A. Cherry
 */
public class Weapon extends WorldItem {

    // Variable Declaration
    // Data Types
    private int statBaseDamage;
    private int statBaseDefense;
    // End of Variable Declaration
    
    public Weapon() {
        
        //
        this(null, null, null, null, null);
    }

    public Weapon(String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {

        // Call to super
        super(sha1CheckSum, packageID, referenceID, referenceName, displayName);

        // Update my own attributes
        updateAttributes();
    }

    @Override
    public Weapon reproduce() {

        // Weapon copy
        final Weapon copy = new Weapon(sha1CheckSum, packageID, referenceID, referenceName, displayName);
        copy.x = x;
        copy.y = y;

        // Copy actions and animations; do not make reproductions
        for (Map.Entry<WorldAction, Animation> map : animationMap.entrySet()) {
            copy.setAnimation(map.getKey(), map.getValue());
        }

        // Copy stats and attributes
        copy.setAttributeMap(attributeMap);
        copy.setStatMap(statMap);

        // Validate the weapon copy
        copy.validate();

        // Return the copy
        return copy;
    }

    @Override
    public final void updateAttributes() {

        // Call super
        super.updateAttributes();

        // Script must exist for these options
        if (script != null) {
            attributeMap.put("scriptEditorId", script.getReferenceID());
            attributeMap.put("scriptPackageId", script.getPackageID());
        }

        // Stat map
        statMap.put("base_value", statBaseValue);
        statMap.put("base_durability", statBaseDurability);
        statMap.put("base_damage", statBaseDamage);
        statMap.put("base_defense", statBaseDefense);
    }

    @Override
    public void validate() {

        // Validate Weapons' field's
        matchFieldValues(getClass());

        // Validate the FItems' fields
        matchFieldValues(getClass().getSuperclass());

        // Validate the WorldObjects' fields
        matchFieldValues(getClass().getSuperclass().getSuperclass());
    }

    @Override
    public Rectangle2D.Float getBounds() {

        // Animation must exist -- uses current
        if (animation != null) {

            // Grab current graphic
            final BufferedImage graphic = animation.getCurrentImage();

            // Graphic must exist for bounds
            if (graphic != null) {

                // Outline the shape
                return new Rectangle2D.Float(x, y, graphic.getWidth(), graphic.getHeight());
            }
        }

        // No bounds
        return null;
    }
    
    @Override public Polygon getPreciseBounds() {
        return null;
    }
}
