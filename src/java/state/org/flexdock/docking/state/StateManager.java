/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.state;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;

/**
 * @author Christopher Butler
 */
public class StateManager {
	public static final String DOCKABLE_PROPERTIES_KEY = DockableState.class.getName();
	public static final String DOCKINGPORT_PROPERTIES_KEY = DockingPortState.class.getName();

	public static DockableState getDockableState(Dockable dockable) {
		if(dockable==null)
			return null;
		
		Object obj = dockable.getClientProperty(DOCKABLE_PROPERTIES_KEY);
		if(!(obj instanceof DockableState)) {
			obj = new DockableStateManager(6);
			dockable.putClientProperty(DOCKABLE_PROPERTIES_KEY, obj);
		}
		return (DockableState)obj;
	}

	public static DockingPortState getDockingPortState(DockingPort port) {
		if(port==null)
			return null;
		
		Object obj = port.getClientProperty(DOCKINGPORT_PROPERTIES_KEY);
		if(!(obj instanceof DockingPortState)) {
			obj = new DockingPortStateManager(4);
			port.putClientProperty(DOCKINGPORT_PROPERTIES_KEY, obj);
		}
		return (DockingPortState)obj;
	}
	
	

	

}
