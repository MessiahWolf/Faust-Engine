/**
 * *****************************************************************************
 * Copyright (c) 2013, Daniel Murphy All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * ****************************************************************************
 */
package core.world;

/**
 * This class contains most control logic for the testbed and the update loop.
 * It also watches the model to switch tests and populates the model with some
 * loop statistics.
 *
 * @author Daniel Murphy (This version was edited by Robert Cherry; source
 * belongs to Daniel Murphy)
 */
public class WorldController implements Runnable {

    // Variable Declaration
    // Java Native Classes
    private Thread thread;
    // Project Classes
    private World world;
    // Data Types
    public static final int DEFAULT_FPS = 60;
    private long startTime;
    private long frameCount;
    private int targetFrameRate;
    private float frameRate = 0;
    private boolean animating = false;
    // End of Variable Declaration

    public WorldController(int fps) {

        if (fps < 1 || fps > 60) {
            fps = DEFAULT_FPS;
        }

        // Set frame rate
        targetFrameRate = fps;
        frameRate = fps;

        // Our data thread
        thread = new Thread(this, "Sample Map Tests");
    }

    public void connect(World world) {
        this.world = world;
    }

    public void setFrameRate(int fps) {
        if (fps <= 0) {
            throw new IllegalArgumentException("Fps cannot be less than or equal to zero");
        }
        targetFrameRate = fps;
        frameRate = fps;
    }

    public int getFrameRate() {
        return targetFrameRate;
    }

    public float getCalculatedFrameRate() {
        return frameRate;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFrameCount() {
        return frameCount;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void restart() {

        // Reset the start time
        startTime = 0;

        // Reset the frame count
        frameCount = 0;
    }

    public synchronized void resume() {

        // Continue animation
        animating = true;

        // Create new thread
        thread = new Thread(this, "Sample Map Tests");
        thread.start();
    }

    public synchronized void start() {

        // Dont start an already started thread
        if (animating == false) {

            // Reset the frame count to zero
            frameCount = 0;

            // Set animating
            animating = true;

            // Stort the thread
            thread.start();
        }
    }

    public synchronized void stop() {

        // No longer animation
        animating = false;

        // Interrupt the thread
        thread.interrupt();
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {

        //
        long beforeTime, afterTime, updateTime, timeDiff, sleepTime, timeSpent;
        float timeInSecs;
        beforeTime = startTime = updateTime = System.nanoTime();

        // This is how we halt the thread without actually calling interupt
        while (animating) {

            //
            timeSpent = beforeTime - updateTime;

            //
            if (timeSpent > 0) {
                timeInSecs = timeSpent * 1.0f / 1000000000.0f;
                updateTime = System.nanoTime();
                frameRate = (frameRate * 0.9f) + (1.0f / timeInSecs) * 0.1f;
            } else {
                updateTime = System.nanoTime();
            }

            // Connect data with map
            if (world != null && animating) {
                
                //
                world.step();
            }

            // Increase the frame count
            frameCount++;

            //
            afterTime = System.nanoTime();

            // How long this thread took to do all of its calculations
            timeDiff = afterTime - beforeTime;

            // Derive sleep time from that
            sleepTime = (1000000000 / targetFrameRate - timeDiff) / 1000000;

            // 
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    // Throw nothing
                }
            }

            // Set before time
            beforeTime = System.nanoTime();
        }
    }
}
