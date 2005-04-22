/*
 * Created on Apr 21, 2005
 */
package org.flexdock.dockbar.event;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author Christopher Butler
 */
public class EventDispatcher {
	private Vector listeners;
	
	public EventDispatcher() {
		listeners = new Vector();
	}
	
	public void addListener(DockbarListener listener) {
		if(listener!=null)
			listeners.add(listener);
	}
	
	public boolean removeListener(DockbarListener listener) {
		return listeners.remove(listener);
	}
	
	
	public void dispatch(DockbarEvent evt) {
		for(Iterator it=listeners.iterator(); it.hasNext();) {
			DockbarListener listener = (DockbarListener)it.next();
			dispatchEvent(evt, listener);
		}
	}
	
	private void dispatchEvent(DockbarEvent evt, DockbarListener listener) {
		switch(evt.getType()) {
			case DockbarEvent.ACTIVATED:
				listener.dockableActivated(evt);
				break;
			case DockbarEvent.DEACTIVATED:
				listener.dockableDeactivated(evt);
				break;
		}
	}
}
