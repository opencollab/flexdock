/*
 * Created on Jun 23, 2005
 */
package org.flexdock.docking.defaults;

import java.util.List;
import java.util.Set;

/**
 * @author Christopher Butler
 */
public interface DockingStub {
	/**
	 * Returns a <code>List</code> of the <code>Components</code> that are event sources for drag operations.  
	 */
	public List getDragSources();
	
	/**
	 * Returns a <code>Set</code> of the <code>Components</code> that are used as frame drag sources.
	 * When a <code>Dockable</code> is floated into an external frame, that frame may or may not have
	 * a titlebar for repositioning.  The Components returned by this method will be setup with appropriate event 
	 * listeners such that dragging them will serve to reposition the containing frame as if they were
	 * the frame titlebar.  If a Component exists in both the Set returned by this method and the List
	 * returned by <code>getDragSources()</code>, the "frame reposition" behavior will supercede any
	 * "drag-to-dock" behavior while the Dockable is in a floating state.  
	 */
	public Set getFrameDragSources();

	/**
	 * Returns a <code>String</code> identifier that is unique within a JVM instance, but persistent 
	 * across JVM instances.  This is used for configuration mangement, allowing the JVM to recognize
	 * a <code>Dockable</code> instance within an application instance, persist the ID, and recall it
	 * in later application instances.  The ID should be unique within an appliation instance so that
	 * there are no collisions with other <code>Dockable</code> instances, but it should also be 
	 * consistent from JVM to JVM so that the association between a <code>Dockable</code> instance and
	 * its ID can be remembered from session to session.
	 * <br/>
	 * The framework performs indexing on the persistent ID.  Consequently, this method may 
	 * <b>not</code> return a <code>null</code> reference.
	 */		
	public String getPersistentId();
}
