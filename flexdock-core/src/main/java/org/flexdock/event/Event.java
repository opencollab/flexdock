/*
 * Created on May 17, 2005
 */
package org.flexdock.event;

import java.util.EventObject;

import org.flexdock.docking.DockingConstants;

/**
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class Event extends EventObject implements DockingConstants {
    private int eventType;

    /**
     * An event object.
     *
     * @param src
     *            the source of the event.
     * @param evtType
     *            the type of the event.
     */
    public Event(Object src, int evtType) {
        super(src);
        eventType = evtType;
    }

    public int getEventType() {
        return eventType;
    }
}
