/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.flexdock.dockbar.activation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.flexdock.dockbar.DockbarManager;
import org.flexdock.dockbar.ViewPane;

/**
 * @author Christopher Butler
 */
public class Animation implements Runnable, ActionListener {

    private static final int ANIMATION_INTERVAL = 20;
    private static final int TOTAL_FRAME_COUNT = 5;

    private DockbarManager dockManager;
    private Timer timer;
    private float frameDelta;
    private int frameCount;
    private boolean hiding;
    private Runnable next;
    private Object lock;

    public Animation(DockbarManager mgr, boolean hide) {
        dockManager = mgr;
        timer = new Timer(ANIMATION_INTERVAL, this);
        frameDelta = (100f/(float)getTotalFrameCount())/100f;
        hiding = hide;
        lock = new Object();
    }

    public void run() {
        timer.start();
        sleep();
    }

    public void actionPerformed(ActionEvent e) {
        resetViewpaneSize();
        dockManager.revalidate();
        if(frameCount==getTotalFrameCount()-1) {
            timer.stop();
            wakeUp();
        } else {
            frameCount++;
        }
    }

    private void resetViewpaneSize() {
        ViewPane viewPane = dockManager.getViewPane();
        int prefSize = dockManager.getPreferredViewpaneSize();

        if(frameCount==0) {
            prefSize = getStartSize(prefSize);
        } else if(frameCount==getTotalFrameCount()-1) {
            prefSize = getEndSize(prefSize);
        } else {
            int newSize = (int)((float)prefSize * (frameCount*frameDelta));
            prefSize = hiding? prefSize-newSize: newSize;
        }

        viewPane.setPrefSize(prefSize);
    }

    private int getStartSize(int prefSize) {
        if(hiding) {
            return prefSize;
        }
        return 0;
    }

    private int getEndSize(int prefSize) {
        if(hiding) {
            return 0;
        }
        return prefSize;
    }

    private int getTotalFrameCount() {
        return TOTAL_FRAME_COUNT;
    }

    public Runnable getNext() {
        return next;
    }
    public void setNext(Runnable next) {
        this.next = next;
    }

    private void sleep() {
        synchronized(lock) {
            try {
                lock.wait();
            } catch(InterruptedException e) {
                System.err.println("Exception: " +e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void wakeUp() {
        synchronized(lock) {
            lock.notifyAll();
        }
    }
}