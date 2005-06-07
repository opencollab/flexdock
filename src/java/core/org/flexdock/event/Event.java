/*
 * Created on May 17, 2005
 */
package org.flexdock.event;

import java.util.EventObject;

import org.flexdock.util.DockingConstants;

/**
 * @author Christopher Butler
 */
public class Event extends EventObject implements DockingConstants {
	private int eventType;
	private Object target;
	
	public Event(Object src, int evtType) {
		super(src);
		eventType = evtType;
	}
	
	public int getEventType() {
		return eventType;
	}
}
