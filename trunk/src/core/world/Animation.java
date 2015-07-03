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
import io.resource.ResourceProducer;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RasterFormatException;
import java.util.Arrays;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;

/**
 * This class allows objects to be drawn using this class.
 *
 * @author Robert A. Cherry
 * @version 1.0.1
 */
public class Animation extends Illustration {

    // Variable Declaration
    private BufferedImage[] subImages;
    private EventListenerList listenerList;
    private Timer timer;
    // Data types
    private boolean borderDrawn;
    private float rotation;
    private int shapeType;
    private int repeatCycles;
    // By default all animation will animate at 1/7 frame a second. (FOR NOW)
    protected int delay = 142;
    private int index;
    private int width;
    private int height;
    // End of Variable Declaration

    public Animation() {

        // Start with all nulls then
        this(null, null, null, null, null);
    }

    public Animation(String sha1CheckSum, String packageID, String referenceID, String referenceName, String displayName) {

        // Call to super
        super(sha1CheckSum, packageID, referenceID, referenceName, displayName);

        // Initialize
        init();
    }

    private void init() {

        //
        listenerList = new EventListenerList();

        // Create the actionListener
        final ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent newEvent) {

                //
                if (timer.isRunning()) {

                    // I do not allow animatons to run with zero milliseconds of delay. It is a huge performance issue if the cycles are indefinite.
                    // If you start an animation with zero delay and negative one repeat cycles you are looking at a CPU usage with the TimerQueue Thread of about 50 - 70%
                    if (repeatCycles == -1 || repeatCycles > 0 && delay > 0) {

                        // Change the Image over time
                        animate();
                    }
                }
            }
        };

        // Creating the timer, but do not start it.
        timer = new Timer(delay, listener);
    }

    @Override
    public Animation reproduce() throws RasterFormatException {

        //
        final Animation copy = new Animation(sha1CheckSum, referenceID, packageID, displayName, referenceName);
        copy.blockRows = blockRows;
        copy.blockColumns = blockColumns;
        copy.blockHGap = blockHGap;
        copy.blockVGap = blockVGap;
        copy.blockWidth = blockWidth;
        copy.blockHeight = blockHeight;
        copy.blockXOffset = blockXOffset;
        copy.blockYOffset = blockYOffset;
        copy.index = index;
        copy.rotation = rotation;
        copy.picture = picture;
        copy.repeatCycles = repeatCycles;
        copy.setDelay(delay);
        copy.setAttributeMap(attributes);

        //
        copy.validate();

        //
        return copy;
    }

    private void animate() {

        //
        if (subImages == null) {
            return;
        }

        // Restart Animation code
        if (index >= subImages.length - 1 || index < 0) {

            // Restart
            index = 0;

            // Fire end of Animation Event
            fireEndEvent();
        } else {

            // Increment the animation index
            index++;

            // Fire the step event to signal that this animation should change images
            fireStepEvent();
        }
    }

    @Override
    public void validate() {

        // Recreate the subImages on validate
        if (picture != null) {

            // Pre checks
            if (blockWidth > 0 && blockHeight > 0) {

                // Recreate entirely
                subImages = ResourceProducer.createImages(picture, attributes);

                //
                width = picture.getWidth();
                height = picture.getHeight();
            }
        }
    }

    @Override
    public BufferedImage draw(ImageObserver obs, float alpha) {

        // Grab the current image
        final BufferedImage image = subImages[index];

        // Some info about it.
        final int imageWidth = (image == null) ? 0 : image.getWidth();
        final int imageHeight = (image == null) ? 0 : image.getHeight();

        // Our output image
        final BufferedImage output = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        // Casting to Graphics2D Object for method setComposite(AlphaComposite ac);
        final Graphics2D manet = (Graphics2D) output.createGraphics();

        // Set the Alpha to that of the Layer
        manet.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Setup the initial Transformations
        //final AffineTransform transformOriginal = manet.getTransform();
        //final AffineTransform transformImage = new AffineTransform();

        // Begin the Transformation
        //transformImage.setToTranslation(point.x, point.y);

        // Draw the actual image
        manet.drawImage(image, 0, 0, obs);

        // Return to the original transform
        //manet.setTransform(transformOriginal);

        // Return the Alpha Composite to 1.0f (Fully Visible);
        manet.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // Draw the border.
        if (borderDrawn) {

            //
            manet.setColor(Color.BLACK);
            manet.drawRect(0, 0, imageWidth - 1, imageHeight - 1);
        }

        //
        manet.dispose();

        //
        return output;
    }

    public void start() {

        // Start the timer
        timer.start();
    }

    public void restart() {

        //
        index = 0;
    }

    public void pause() {

        // Pause the timer
        timer.stop();
    }

    public int length() {
        return subImages == null ? -1 : subImages.length;
    }

    // Questions
    public boolean isPaintingBorder() {
        return borderDrawn;
    }

    public boolean isEmpty() {
        return subImages == null || subImages.length == 0;
    }

    public boolean isRunning() {
        return timer.isRunning();
    }

    @Override
    public void updateAttributes() {

        // Grab attributes from parent
        super.updateAttributes();

        // Addon my special fields
        attributes.put("delay", delay);
    }

    // Mutators
    public BufferedImage getImageAt(int newIndex) throws ArrayIndexOutOfBoundsException {
        return (subImages == null || subImages.length == 0) ? null : subImages[newIndex];
    }

    public BufferedImage getCurrentImage() {
        return (subImages == null || subImages.length == 0) ? null : subImages[index];
    }

    public BufferedImage[] getImages() {
        return subImages;
    }

    public int getDelay() {
        return delay;
    }

    public int getIndex() {
        return index;
    }

    public int getShapeType() {
        return shapeType;
    }

    public float getRotation() {
        return rotation;
    }

    public int getCycles() {
        return repeatCycles;
    }

    public void setDelay(int delay) {

        //
        this.delay = delay;

        // Apply To Timer
        timer.setDelay(delay);
    }

    public void setIndex(int newIndex) {
        index = (newIndex >= 0 && newIndex < subImages.length) ? newIndex : 0;
    }

    public void setRotation(float newFloat) {
        rotation = (newFloat >= 0.0f && newFloat <= 360.0f) ? newFloat : 0.0f;
    }

    public void setCycles(int newInt) {
        repeatCycles = newInt;
    }

    public void setPaintShape(boolean newState) {
        borderDrawn = newState;
    }

    private void fireEndEvent() {

        // Stop the animation if we're at zero
        if (repeatCycles == 0) {
            timer.stop();
        }

        // Subtract from cycles
        if (repeatCycles > 0) {
            repeatCycles--;
        }

        // Fire those listeners
        Object[] tempListeners = Arrays.copyOf(listenerList.getListenerList(), listenerList.getListenerList().length);

        for (int i = 0; i < tempListeners.length; i += 2) {
            if (tempListeners[i] == AnimationListener.class) {
                ((AnimationListener) tempListeners[i + 1]).animationEnd(new AnimationEvent(this));
            }
        }
    }

    private void fireStepEvent() {

        final Object[] tempListeners = Arrays.copyOf(listenerList.getListenerList(), listenerList.getListenerList().length);

        for (int i = 0; i < tempListeners.length; i += 2) {
            if (tempListeners[i] == AnimationListener.class) {
                ((AnimationListener) tempListeners[i + 1]).animationStep(new AnimationEvent(this));
            }
        }
    }

    public void addAnimationListener(AnimationListener newListener) {
        listenerList.add(AnimationListener.class, newListener);
    }

    public void removeAnimationListener(AnimationListener newListener) {
        listenerList.remove(AnimationListener.class, newListener);
    }
}
