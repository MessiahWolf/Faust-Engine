/**
 * Copyright (c) 2013, Robert Cherry * All rights reserved.
 *
 * This file is part of the Faust Engine.
 *
 * The Faust Engine is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * The Faust Engine is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * the Faust Engine. If not, see <http://www.gnu.org/licenses/>.
 */
package core.world;

import core.event.AnimationEvent;
import core.event.AnimationListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of this class is to hold a spot in an Inventory instance by
 * holding attributes that reflect the Life.class and its sub-classes.
 *
 * @version 1.01
 * @author Robert Cherry
 */
public abstract class WorldItem extends WorldObject implements AnimationListener {

    // Variable Declaration
    // Java Classes
    protected HashMap<WorldAction, Animation> animationMap;
    protected HashMap<WorldEffect, Integer> effectMap;
    protected HashMap<String, Object> statMap;
    // Project Classes
    protected Animation animation;
    protected WorldScript script;
    // Data Types
    protected boolean isQuestItem;
    protected int statBaseValue;
    protected int statBaseDurability;
    protected String scriptEditorId;
    protected String scriptPackageId;
    // End of Variable Declaration
    
    public WorldItem() {
        
        // Just replace with nulls all across
        this(null, null, null, null, null);
    }

    public WorldItem(String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {

        // Call to super
        super(sha1CheckSum, packageID, referenceID, referenceName, displayName);

        // Build our collections
        animationMap = new HashMap<>();
        effectMap = new HashMap<>();
        statMap = new HashMap<>();

        // Update attributes
        updateAttributes();
    }

    public void shadowAction(String referenceID, WorldAction action) {
        actionMap.put(referenceID, action);
    }

    public void addEffect(WorldEffect newEffect, int newLevel) {
        effectMap.put(newEffect, newLevel);
    }

    // Value Acessors
    public boolean isQuestItem() {
        return isQuestItem;
    }

    public Animation getAnimation() {
        return animation;
    }

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

    public HashMap<WorldAction, Animation> getAnimationMap() {
        return animationMap;
    }

    public HashMap<String, Object> getStatMap() {
        updateAttributes();
        return statMap;
    }

    public HashMap<WorldEffect, Integer> getEffectMap() {
        return effectMap;
    }

    public int getBaseValue() {
        return statBaseValue;
    }

    public WorldScript getScript() {
        return script;
    }

    // Value Mutators
    public void setStatMap(HashMap<String, Object> statMap) {
        this.statMap.putAll(statMap);
    }

    // Normal Parent Methods
    public void setAnimation(WorldAction action, Animation animation) {

        // Store the action with an animation
        animationMap.put(action, animation);

        // Set default animation
        if (this.animation == null) {
            this.animation = animation;
        }
    }

    public void setEffectList(HashMap<WorldEffect, Integer> effectMap) {
        this.effectMap.putAll(effectMap);
    }

    public void setQuestItem(boolean isQuestItem) {
        this.isQuestItem = isQuestItem;
    }

    public void setValue(int statBaseValue) {
        this.statBaseValue = statBaseValue;
    }

    public void setScript(WorldScript script) {
        this.script = script;
    }

    @Override
    protected void draw(Graphics monet, ImageObserver obs, float alpha) {

        // If and only if visible is true;
        if (visible) {

            // Must have an active animation
            if (animation != null) {

                // Cast to 2D Graphics
                final Graphics2D manet = (Graphics2D) monet;

                // Setup the initial Transformations
                final AffineTransform transformOriginal = manet.getTransform();
                final AffineTransform transformImage = new AffineTransform();

                // Grab the current image
                final BufferedImage graphic = animation.getCurrentImage();

                // Begin the Transformation
                transformImage.setToTranslation(x, y);
                transformImage.rotate(Math.toRadians(rotation), graphic.getWidth(obs) / 2, graphic.getHeight(obs) / 2);

                // Draw the image
                manet.drawImage(graphic, transformImage, obs);

                // Border check
                if (border) {

                    // Stroke of four width
                    manet.setStroke(new BasicStroke(4f));
                    
                    // Change the color
                    manet.setColor(new Color(65, 105, 255));

                    // Fill the entire bounds
                    manet.fill(getBounds());
                }

                // Reset the transformation (Do not remove this line)
                manet.setTransform(transformOriginal);
            }
        }
    }

    @Override
    public void validate() {

        // Nothing for now
        if (animation == null) {

            // Grab default animation as IDLE
            animation = getAnimation(WorldAction.IDLE);

            // Must have found something
            if (animation != null) {
                animation.addAnimationListener(this);
            }
        }
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
    public void receive(String referenceID, WorldResource resource) {

        // Depending on the type of resource
        final Class closs = resource.getClass();

        // Only accept what we requested
        if (requestMap.containsKey(referenceID)) {

            // Solve for animations
            if (closs == Animation.class) {

                // Action Map must have requested this animation for this action
                if (actionMap.containsKey(referenceID)) {

                    // Set it now
                    setAnimation(actionMap.get(referenceID), (Animation) resource);

                    // Remove from both forms of requests
                    requestMap.remove(referenceID);
                    actionMap.remove(referenceID);
                }
            }
        }
    }
}
