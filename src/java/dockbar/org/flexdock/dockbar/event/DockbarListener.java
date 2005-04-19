/*
 * Created on Apr 18, 2005
 */
package org.flexdock.dockbar.event;

/**
 * @author Christopher Butler
 */
public interface DockbarListener {
	public void dockableActivated(DockbarEvent evt);
	public void dockableDeactivated(DockbarEvent evt);
}
