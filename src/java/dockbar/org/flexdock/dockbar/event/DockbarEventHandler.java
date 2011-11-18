/*
 * Created on Apr 21, 2005
 */
package org.flexdock.dockbar.event;

import java.util.EventListener;

import org.flexdock.event.Event;
import org.flexdock.event.EventHandler;

/**
 * An event handler to match {@code DockbarEvent} types to the appropriate
 * {@code DockbarListener} method.
 *
 * @author Christopher Butler
 */
public class DockbarEventHandler extends EventHandler {

    /**
     * This class accepts {@code DockbarEvent}s.
     *
     * @param evt
     */
    public boolean acceptsEvent(Event evt) {
        return evt instanceof DockbarEvent;
    }

    public boolean acceptsListener(EventListener listener) {
        return listener instanceof DockbarListener;
    }

    public void handleEvent(Event event, EventListener consumer, int eventType) {
        DockbarEvent evt = (DockbarEvent) event;
        DockbarListener listener = (DockbarListener) consumer;

        switch (eventType) {
        case DockbarEvent.EXPANDED:
            listener.dockableExpanded(evt);
            break;
        case DockbarEvent.LOCKED:
            listener.dockableLocked(evt);
            break;
        case DockbarEvent.COLLAPSED:
            listener.dockableCollapsed(evt);
            break;
        case DockbarEvent.MINIMIZE_STARTED:
            listener.minimizeStarted(evt);
            break;
        case DockbarEvent.MINIMIZE_COMPLETED:
            listener.minimizeCompleted(evt);
            break;
        }
    }
}
