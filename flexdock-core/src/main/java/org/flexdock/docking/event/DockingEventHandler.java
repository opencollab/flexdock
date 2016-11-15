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
package org.flexdock.docking.event;

import java.util.EventListener;
import java.util.Vector;

import org.flexdock.docking.Dockable;
import org.flexdock.event.Event;
import org.flexdock.event.EventHandler;

/**
 * @author Christopher Butler
 */
public class DockingEventHandler extends EventHandler {
    private static final String DOCKING_LISTENERS_KEY = "EventManager.DOCKING_LISTENERS_KEY";

    public static DockingListener[] getDockingListeners(Dockable dockable) {
        Vector list = getDockingListenersList(dockable);
        return list==null? null: (DockingListener[])list.toArray(new DockingListener[0]);
    }

    public static void addDockingListener(Dockable dockable, DockingListener listener) {
        if(dockable!=null && listener!=null) {
            getDockingListenersList(dockable).add(listener);
        }
    }

    public static void removeDockingListener(Dockable dockable, DockingListener listener) {
        if(dockable!=null && listener!=null) {
            getDockingListenersList(dockable).remove(listener);
        }
    }

    private static Vector getDockingListenersList(Dockable dockable) {
        if(dockable==null) {
            return null;
        }

        Vector list = (Vector)dockable.getClientProperty(DOCKING_LISTENERS_KEY);
        if(list==null) {
            list = new Vector();
            dockable.putClientProperty(DOCKING_LISTENERS_KEY, list);
        }
        return list;
    }

    public boolean acceptsEvent(Event evt) {
        return evt instanceof DockingEvent;
    }
    public boolean acceptsListener(EventListener listener) {
        return listener instanceof DockingListener;
    }


    public void handleEvent(Event evt, EventListener listener, int eventType) {
        DockingEvent event = (DockingEvent)evt;
        DockingListener consumer = (DockingListener)listener;

        switch(event.getEventType()) {
            case DockingEvent.DRAG_STARTED:
                consumer.dragStarted(event);
                break;
            case DockingEvent.DROP_STARTED:
                consumer.dropStarted(event);
                break;
            case DockingEvent.DOCKING_COMPLETE:
                consumer.dockingComplete(event);
                break;
            case DockingEvent.DOCKING_CANCELED:
                consumer.dockingCanceled(event);
                break;
            case DockingEvent.UNDOCKING_COMPLETE:
                consumer.undockingComplete(event);
                break;
            case DockingEvent.UNDOCKING_STARTED:
                consumer.undockingStarted(event);
                break;
        }
    }

    public EventListener[] getListeners(Object eventTarget) {
        return eventTarget instanceof DockingMonitor?
               ((DockingMonitor)eventTarget).getDockingListeners(): null;
    }
}
