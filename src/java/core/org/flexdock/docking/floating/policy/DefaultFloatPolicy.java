/*
 * Created on May 31, 2005
 */
package org.flexdock.docking.floating.policy;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.floating.frames.FloatingDockingPort;

/**
 * @author Christopher Butler
 */
public class DefaultFloatPolicy extends FloatPolicy.Stub {
	
	private static final DefaultFloatPolicy SINGLETON = new DefaultFloatPolicy();
	
	public static DefaultFloatPolicy getInstance() {
		return SINGLETON;
	}
	
	public boolean isFloatDropAllowed(DockingEvent evt) {
		DockingPort oldPort = evt.getOldDockingPort();
		// if we're already floating, and we're the only dockable
		// in a floating dockingport, then we don't want to undock
		// from the port and re-float (dispose and create a new DockingFrame).
		if(oldPort instanceof FloatingDockingPort) {
			FloatingDockingPort dockingPort = (FloatingDockingPort)oldPort;
			if(dockingPort.getDockableCount()<2)
				evt.consume();
		}
		
		return super.isFloatDropAllowed(evt);
	}
	
	public boolean isFloatingAllowed(Dockable dockable) {
		if(dockable==null || FloatPolicyManager.isGlobalFloatingBlocked())
			return false;
		
		if(dockable.getFrameDragSources().size()==0)
			return false;
		
		return super.isFloatingAllowed(dockable);
	}
}
