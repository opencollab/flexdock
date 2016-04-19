/*
 * Created on May 14, 2005
 */
package org.flexdock.perspective.event;

import java.util.EventListener;



import org.flexdock.event.Event;
import org.flexdock.event.EventHandler;

/**
 * @author Christopher Butler
 */
public class PerspectiveEventHandler extends EventHandler {

    private static final PerspectiveEventHandler SINGLETON = new PerspectiveEventHandler();

    public static PerspectiveEventHandler getInstance() {
        return SINGLETON;
    }

    private PerspectiveEventHandler() {

    }

    public boolean acceptsEvent(Event evt) {
        return evt instanceof PerspectiveEvent;
    }

    public boolean acceptsListener(EventListener listener) {
        return listener instanceof PerspectiveListener;
    }

    public void handleEvent(Event evt, EventListener listener, int eventType) {
        PerspectiveEvent event = (PerspectiveEvent)evt;
        PerspectiveListener consumer = (PerspectiveListener)listener;
        switch(eventType) {
        case PerspectiveEvent.CHANGED:
            consumer.perspectiveChanged(event);
            break;
        case PerspectiveEvent.RESET:
            consumer.perspectiveReset(event);
            break;
        default:
            break;
        }
    }

    public PerspectiveListener[] getListeners() {
        synchronized(globalListeners) {
            return (PerspectiveListener[])globalListeners.toArray(new PerspectiveListener[0]);
        }
    }
}
