/*
 * Created on May 18, 2005
 */
package org.flexdock.docking.event;

import java.util.EventListener;

import org.flexdock.event.Event;
import org.flexdock.event.EventHandler;

/**
 * @author Christopher Butler
 */
public class DockingEventHandler extends EventHandler {

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
