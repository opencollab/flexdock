/*
 * Created on Jun 23, 2005
 */
package org.flexdock.docking;

import java.awt.Component;

/**
 * @author Christopher Butler
 */
public interface DockingStub {

	Component getDragSource();

	Component getFrameDragSource();

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
	String getPersistentId();
	
	String getTabText();
    
}
