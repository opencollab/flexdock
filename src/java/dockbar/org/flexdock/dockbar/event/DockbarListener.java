/*
 * Created on Apr 18, 2005
 */
package org.flexdock.dockbar.event;

/**
 * @author Christopher Butler
 */
public interface DockbarListener {
	public void dockableExpanded(DockbarEvent evt);
	public void dockableLocked(DockbarEvent evt);
	public void dockableCollapsed(DockbarEvent evt);
}
