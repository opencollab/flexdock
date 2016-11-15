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
package org.flexdock.demos.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingEventHandler;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.props.DockablePropertySet;
import org.flexdock.docking.props.PropertyManager;

/**
 * @author Christopher Butler
 */
public class DockableTitlepane extends Titlepane implements Dockable {
    private String dockingId;
    private ArrayList dragSources;
    private HashSet frameDragSources;

    public DockableTitlepane(String id, String title) {
        super(title);
        dockingId = id;

        // initialize the drag sources
        dragSources = new ArrayList();
        frameDragSources = new HashSet();
        // use the titlebar as a drag source
        dragSources.add(getTitlebar());
        frameDragSources.add(getTitlebar());
    }

    // Begin user-defined methods
    public Component getComponent() {
        return this;
    }

    public List getDragSources() {
        return dragSources;
    }
    public Set getFrameDragSources() {
        return frameDragSources;
    }

    public String getPersistentId() {
        return dockingId;
    }
    // End user-defined methods






    // Begin framework-provided methods

    public boolean dock(Dockable dockable, String relativeRegion, float ratio) {
        return DockingManager.dock(dockable, this, relativeRegion, ratio);
    }

    public boolean dock(Dockable dockable, String relativeRegion) {
        return DockingManager.dock(dockable, this, relativeRegion);
    }

    public boolean dock(Dockable dockable) {
        return DockingManager.dock(dockable, this);
    }

    public DockingPort getDockingPort() {
        return DockingManager.getDockingPort((Dockable)this);
    }

    public DockablePropertySet getDockingProperties() {
        return PropertyManager.getDockablePropertySet(this);
    }

    public void addDockingListener(DockingListener listener) {
        DockingEventHandler.addDockingListener(this, listener);
    }

    public DockingListener[] getDockingListeners() {
        return DockingEventHandler.getDockingListeners(this);
    }

    public void removeDockingListener(DockingListener listener) {
        DockingEventHandler.removeDockingListener(this, listener);
    }

    // End framework-provided methods





    // Begin event handler methods

    public void dockingCanceled(DockingEvent evt) {
    }

    public void dockingComplete(DockingEvent evt) {
    }

    public void dragStarted(DockingEvent evt) {
    }

    public void dropStarted(DockingEvent evt) {
    }

    public void undockingComplete(DockingEvent evt) {
    }

    public void undockingStarted(DockingEvent evt) {
    }

    // End event handler methods
}
