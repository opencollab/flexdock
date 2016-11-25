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
package org.flexdock.dockbar.event;

import java.awt.Point;



import org.flexdock.dockbar.DockbarManager;
import org.flexdock.docking.Dockable;
import org.flexdock.event.EventManager;
import org.flexdock.util.Utilities;

/**
 * @author Christopher Butler
 */
public class ActivationListener {

    private DockbarManager manager;
    private Deactivator deactivator;
    private boolean enabled;
    private boolean mouseOver;


    public ActivationListener(DockbarManager mgr) {
        manager = mgr;
        setEnabled(true);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isActive() {
        return manager.isActive() && !manager.isAnimating() && !manager.isDragging();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAvailable() {
        return isEnabled() && isActive();
    }

    public boolean isViewpaneLocked() {
        return manager.getViewPane().isLocked();
    }

    private boolean isOverDockbars(Point mousePoint) {
        return manager.getLeftBar().getBounds().contains(mousePoint)
               || manager.getRightBar().getBounds().contains(mousePoint)
               || manager.getBottomBar().getBounds().contains(mousePoint);
    }



    public void mouseEntered(Point mousePoint) {
        if(mouseOver) {
            return;
        }

        mouseOver = true;
        if(deactivator!=null) {
            deactivator.setEnabled(false);
        }
        deactivator = null;
    }

    public void mouseExited(Point mousePoint) {
        if(!mouseOver) {
            return;
        }

        mouseOver = false;
        if(!isOverDockbars(mousePoint)) {
            deactivator = new Deactivator(manager.getActiveDockableId());
            deactivator.setEnabled(true);
            deactivator.start();
        }
    }

    public void mousePressed(Point mousePoint, boolean mouseOver) {
        if(mouseOver) {
            if(!isViewpaneLocked()) {
                lockViewpane();
            }
        } else {
            if(!isOverDockbars(mousePoint)) {
                manager.setActiveDockable((String)null);
            }
        }
    }

    public void lockViewpane() {
        manager.getViewPane().setLocked(true);
        dispatchDockbarEvent(DockbarEvent.LOCKED);
    }


    private void dispatchDockbarEvent(int type) {
        Dockable dockable = manager.getActiveDockable();
        int edge = manager.getActiveEdge();
        DockbarEvent evt = new DockbarEvent(dockable, type, edge);
        EventManager.dispatch(evt);
    }

    private class Deactivator extends Thread {
        private String dockableId;
        private boolean enabled;

        private Deactivator(String id) {
            dockableId = id;
            enabled = true;
        }

        private synchronized void setEnabled(boolean b) {
            enabled = b;
        }

        private synchronized boolean isEnabled() {
            return enabled;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                System.err.println("Exception: " +e.getMessage());
                e.printStackTrace();
            }

            if(isEnabled() && !Utilities.isChanged(dockableId, manager.getActiveDockableId()) &&
                    !isViewpaneLocked()) {
                manager.setActiveDockable((String)null);
            }
        }

    }

    public boolean isMouseOver() {
        return mouseOver;
    }

}
