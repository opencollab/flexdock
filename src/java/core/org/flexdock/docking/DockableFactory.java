/*
 * Created on May 26, 2005
 */
package org.flexdock.docking;

import java.awt.Component;

/**
 * This interface is designed to provide an API for allowing the <code>DockingManager</code> to
 * obtain <code>Dockable</code> instances on the fly.  It has a single method, 
 * <code>getDockableComponent(String dockableId)</code>, responsible for returning <code>Component</code>
 * instances, possibly creating and registering <code>Dockables</code> in the process.
 * 
 * Implementations of this interface will be application-specific and may be plugged into the
 * <code>DockingManager</code> via the call <code>DockingManager.setDockableFactory(myFactory)</code>.
 * Throughout the framework, FlexDock makes many calls to 
 * <code>DockingManager.getDockable(String id)</code> under the assumption that at some point, the
 * requested <code>Dockable</code> instance has been registered via 
 * <code>DockingManager.registerDockable(Dockable dockable)</code>.  
 * 
 * In the event that a  
 * <code>Dockable</code> with the specified ID has never been formally registered, the 
 * <code>DockingManager</code> will check for a factory via 
 * <code>DockingManager.getDockableFactory()</code>.  If a factory is present, its 
 * <code>getDockableComponent()</code> method is invoked.  If a valid <code>Component</code> is returned
 * from <code>getDockableComponent()</code>, the DockingManager will attemp to 
 * register it as a <code>Dockable</code> and return the <code>Dockable</code>.
 * 
 * <code>DockableFactory</code> implementations are especially useful for applications with
 * persisted layouts where the <code>Dockables</code> required during a layout restoration
 * may be constructed automatically on demand by the framework.
 * 
 * @author Christopher Butler
 */
public interface DockableFactory {
	
	/**
	 * Returns a <code>Component</code> specified Dockable ID, possibly creating and
	 * registering a <code>Dockable</code> in the process.
	 * 
	 * @param dockableId the ID for the requested dockable <code>Component</code>
	 * @return the <code>Component</code> for the specified ID
	 */
	public Component getDockableComponent(String dockableId);
}
