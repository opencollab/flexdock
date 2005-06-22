/*
 * Created on May 26, 2005
 */
package org.flexdock.docking;

/**
 * This interface is designed to provide an API for allowing the <code>DockingManager</code> to
 * obtain <code>Dockable</code> instances on the fly.  It has a single method, 
 * <code>getDockable(String dockableId)</code>, responsible for returning <code>Dockable</code>
 * instances, possibly creating and registering them in the process.
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
 * <code>getDockable()</code> method is invoked.  If a valid <code>Dockable</code> is returned
 * from <code>getDockable()</code>, it is automatically registered, docking-related event
 * listeners are properly configured, and the <code>DockingManager</code> returns the 
 * <code>Dockable</code>.
 * 
 * <code>DockableFactory</code> implementations are especially useful for applications with
 * persisted layouts where the <code>Dockables</code> required during a layout restoration
 * may be constructed automatically on demand by the framework.
 * 
 * @author Christopher Butler
 */
public interface DockableFactory {
	
	/**
	 * Returns a <code>Dockable</code> instance for the specified ID, possibly creating the
	 * <code>Dockable</code> and registering it in the process.
	 * 
	 * @param dockableId the ID for the requested <code>Dockable</code>
	 * @return the <code>Dockable</code> for the specified ID
	 */
	public Dockable getDockable(String dockableId);
}
