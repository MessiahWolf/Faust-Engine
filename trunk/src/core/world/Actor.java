/**
 * Copyright (c) 2013, Robert Cherry * All rights reserved.
 *
 * This file is part of the Faust Engine.
 *
 * The Faust Engine is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * The Faust Engine is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * the Faust Engine. If not, see <http://www.gnu.org/licenses/>.
 */
package core.world;

import core.event.AnimationEvent;
import core.event.AnimationListener;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Map;

/**
 * This classes purpose is to represent and provide default code that may be
 * overridden by child classes.
 *
 * @version 1.01
 * @author Robert Cherry
 */
public class Actor extends WorldObject implements AnimationListener {

    // Variable Declaration
    // Java Native Classes
    private HashMap<WorldItem, Double> dropMap;
    protected HashMap<WorldAction, Animation> animationMap;
    // Project Classes
    protected Animation animation;
    private WorldScript script;
    // Data Types
    protected int value;
    // Actor Values
    private int attributeAttackDamage;
    private int attributeAttackDefense;
    private int attributeMagicDamage;
    // End of Variable Declaration

    public Actor() {

        //
        this(null, null, null, null, null);
    }

    public Actor(String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {

        // Call to super
        super(sha1CheckSum, packageID, referenceID, referenceName, displayName);

        // Build our collections
        animationMap = new HashMap<>();
        dropMap = new HashMap<>();
    }

    @Override
    public Actor reproduce() {
        return null;
    }

    @Override
    public void validate() {

        //
        if (animation == null) {

            //
            animation = getAnimation(WorldAction.IDLE);

            //
            if (animation != null) {
                animation.addAnimationListener(this);
            }
        }
    }

    @Override
    public void receive(String referenceID, WorldResource resource) {

        // Depending on the type of resource
        final Class closs = resource.getClass();

        if (requestMap.containsKey(referenceID)) {

            // Solve for animations
            if (closs == Animation.class) {

                //
                if (actionMap.containsKey(resource.getReferenceName())) {

                    // Set it now
                    setAnimation(actionMap.get(resource.getReferenceName()), (Animation) resource);

                    //
                    actionMap.remove(referenceID);
                }
            }
        }
    }

    public void shadowAction(String referenceID, WorldAction action) {
        actionMap.put(referenceID, action);
    }

    @Override
    public final void updateAttributes() {

        // Call to super
        super.updateAttributes();

        // Normal Attributes
        attributeMap.put("attrAttackDamage", attributeAttackDamage);
        attributeMap.put("attrAttackDefense", attributeAttackDefense);
        attributeMap.put("attrMagicDamage", attributeMagicDamage);

        // Script must exist for these options
        if (script != null) {
            attributeMap.put("scriptEditorId", script.getReferenceID());
            attributeMap.put("scriptPackageId", script.getPackageId());
        }
    }

    public void act() {

        if (script != null) {

            // Perform the script's act function
            script.action();
        }
    }

    public void addItem(WorldItem item, double chance) {
        dropMap.put(item, chance);
    }

    // Booleans
    public Animation getAnimation(WorldAction action) {

        // Scan and report
        for (Map.Entry<WorldAction, Animation> map : animationMap.entrySet()) {
            if (map.getValue() != null) {
                if (action == map.getKey()) {
                    return map.getValue();
                }
            }
        }

        // Return null otherwise
        return null;
    }

    @Override
    public Rectangle2D.Float getBounds() {
        return null;
    }

    public HashMap<WorldAction, Animation> getAnimationMap() {
        return animationMap;
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getAttackDamage() {
        return attributeAttackDamage;
    }

    public int getAttackDefense() {
        return attributeAttackDefense;
    }

    public int getMagicDamage() {
        return attributeMagicDamage;
    }

    public HashMap<WorldItem, Double> getItemMap() {
        return dropMap;
    }

    public WorldScript getScript() {
        return script;
    }

    // Normal Parent Methods
    public void setAnimation(WorldAction action, Animation animation) {

        // Grab it first
        WorldAction storedAction = null;

        // Scan and report
        for (Map.Entry<WorldAction, Animation> map : animationMap.entrySet()) {
            if (map.getKey() == action) {
                storedAction = map.getKey();
            }
        }

        // Store the action with an animation
        animationMap.put(storedAction, animation);
    }

    public void setAttackDamage(int attr) {
        attributeAttackDamage = attr;
    }

    public void setAttackDefense(int attr) {
        attributeAttackDefense = attr;
    }

    public void setMagicDamage(int attr) {
        attributeMagicDamage = attr;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

        // update animation
        if (animation != null) {
            animation.setPaintShape(selected);
        }
    }

    public void setDropList(HashMap<WorldItem, Double> newList) {
        dropMap.clear();
        dropMap.putAll(newList);
    }

    public void setScript(WorldScript script) {
        this.script = script;
    }

    @Override
    public void animationEnd(AnimationEvent event) {
        fireEventNotifier(this, WorldObject.FLAG_ANIMATION_END);
    }

    @Override
    public void animationStep(AnimationEvent event) {
        fireEventNotifier(this, WorldObject.FLAG_ANIMATION_STEP);
    }

    @Override
    public void draw(Graphics monet, ImageObserver obs, float alpha) {

        // If and only if visible is true;
        if (visible) {

            // Must have an active existing animation
            if (animation != null) {

                // Draw the animation
                animation.draw(obs, alpha);
            }
        }
    }
}
