/*
 * Created on Mar 14, 2005
 *
 */
package org.flexdock.docking.event;

/**
 * @author Christopher Butler
 *
 */
public class EventDispatcher {
	public static void notifyDockingMonitor(DockingMonitor monitor, DockingEvent evt) {
		if(monitor==null || evt==null)
			return;
		
		DockingListener[] listeners = monitor.getDockingListeners();
		for(int i=0; i<listeners.length; i++) {
			notifyDockingListener(listeners[i], evt);
		}
	}

	public static void notifyDockingListener(DockingListener listener, DockingEvent evt) {
		if(listener==null || evt==null)
			return;
		
		switch(evt.getEventType()) {
			case DockingEvent.DRAG_STARTED:
				listener.dragStarted(evt);
				break;
			case DockingEvent.DROP_STARTED:
				listener.dropStarted(evt);
				break;
			case DockingEvent.DOCKING_COMPLETE:
				listener.dockingComplete(evt);
				break;
			case DockingEvent.DOCKING_CANCELED:
				listener.dockingCanceled(evt);
				break;
		}
	}
}
