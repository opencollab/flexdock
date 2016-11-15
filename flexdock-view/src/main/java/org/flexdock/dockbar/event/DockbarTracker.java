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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingUtilities;

import org.flexdock.dockbar.DockbarManager;
import org.flexdock.dockbar.ViewPane;
import org.flexdock.docking.DockingConstants;
import org.flexdock.util.SwingUtility;

/**
 * @author Christopher Butler
 */
public class DockbarTracker implements DockingConstants, PropertyChangeListener, AWTEventListener {

    public static void register() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                DockbarTracker tracker = new DockbarTracker();
                // register a propertyChangeListener to update the 'currrent'
                // DockbarManager each time the focused window changes
                KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                focusManager.addPropertyChangeListener(tracker);

                // register an AWTEventListener to handle low-level mouse events
                long evtType = AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK;
                Toolkit.getDefaultToolkit().addAWTEventListener(tracker, evtType);
            }
        });
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if(ACTIVE_WINDOW.equals(evt.getPropertyName())) {
            Component newWindow = SwingUtility.toComponent(evt.getNewValue());
            DockbarManager.windowChanged(newWindow);
        }
    }

    public void eventDispatched(AWTEvent event) {
        //catch all mouseMoved events
        int evtType = event.getID();
        if(evtType!=MouseEvent.MOUSE_MOVED && evtType!=MouseEvent.MOUSE_PRESSED) {
            return;
        }

        // get the activation listener for the current dockbarManager
        DockbarManager mgr = DockbarManager.getCurrent();
        ActivationListener listener = mgr==null? null: mgr.getActivationListener();
        if(listener==null || !listener.isAvailable()) {
            return;
        }

        // translate the mouse event to the viewpane parent
        MouseEvent evt = (MouseEvent)event;
        Point p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getPoint(), mgr.getViewPane().getParent());
        ViewPane viewPane = mgr.getViewPane();
        boolean mouseOver = viewPane.getBounds().contains(p);

        if(evtType==MouseEvent.MOUSE_PRESSED) {
            // check mousePressed for activation/deactivation
            listener.mousePressed(p, mouseOver);
        } else {
            // check for mouseEnter and mouseExit events
            handleMouseMove(listener, p, mouseOver);
        }
    }

    private void handleMouseMove(ActivationListener listener, Point mousePoint, boolean mouseOver) {
        if(mouseOver) {
            if(!listener.isMouseOver()) {
                listener.mouseEntered(mousePoint);
            }
        } else {
            if(listener.isMouseOver()) {
                listener.mouseExited(mousePoint);
            }
        }
    }
}
