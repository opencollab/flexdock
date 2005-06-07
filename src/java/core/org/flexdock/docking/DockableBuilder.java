/*
 * Created on May 26, 2005
 */
package org.flexdock.docking;

/**
 * This interface is designed to provide an API for allowing the <code>DockingManager</code> to
 * create <code>Dockable</code> instances on the fly.  It has a single method, 
 * <code>createDockable(String dockableId)</code>, responsible for constructing a new 
 * <code>Dockable</code> instance.
 * 
 * Implementations of this interface will be application-specific and may be plugged into the
 * <code>DockingManager</code> via the call <code>DockingManager.setDockableBuilder(myBuilder)</code>.
 * Throughout the framework, FlexDock makes many calls to 
 * <code>DockingManager.getDockable(String id)</code> under the assumption that at some point, the
 * requested <code>Dockable</code> instance has been registered via 
 * <code>DockingManager.registerDockable(Dockable dockable)</code>.  
 * 
 * In the event that a  
 * <code>Dockable</code> with the specified ID has never been formally registered, the 
 * <code>DockingManager</code> will check for a builder via 
 * <code>DockingManager.getDockableBuilder()</code>.  If a builder is present, its 
 * <code>createDockable()</code> method is invoked.  If a valid <code>Dockable</code> is returned
 * from <code>createDockable()</code>, it is automatically registered, docking-related event
 * listeners are properly configured, and the <code>DockingManager</code> returns the 
 * <code>Dockable</code>.
 * 
 * <code>DockableBuilder</code> implementations are especially useful for applications with
 * persisted layouts where the <code>Dockables</code> required during a layout restoration
 * may be constructed automatically on demand by the framework.
 * 
 * @author Christopher Butler
 */
public interface DockableBuilder {
	
	/**
	 * Creates and returns a <code>Dockable</code> instance for the specified ID.
	 * 
	 * @param dockableId the ID for the requested <code>Dockable</code>
	 * @return the <code>Dockable</code> for the specified ID
	 */
	public Dockable createDockable(String dockableId);
}
