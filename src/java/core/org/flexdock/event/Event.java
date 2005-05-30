/*
 * Created on May 17, 2005
 */
package org.flexdock.event;

import java.util.EventObject;

/**
 * @author Christopher Butler
 */
public class Event extends EventObject {
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
