/*
 * Created on May 31, 2005
 */
package org.flexdock.docking.floating.policy;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.event.DockingEvent;

/**
 * @author Christopher Butler
 */
public interface FloatPolicy {
	public boolean isFloatingAllowed(Dockable dockable);
	public boolean isFloatDropAllowed(DockingEvent evt);
	
	public static class Stub implements FloatPolicy {
		public boolean isFloatingAllowed(Dockable dockable) {
			return true;
		}
		public boolean isFloatDropAllowed(DockingEvent evt) {
			return true;
		}
	}
}
