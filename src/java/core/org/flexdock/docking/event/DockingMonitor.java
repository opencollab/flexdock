/*
 * Created on Mar 10, 2005
 */
package org.flexdock.docking.event;


/**
 * @author cb8167
 */
public interface DockingMonitor {
	public void addDockingListener(DockingListener listener);
	public void removeDockingListener(DockingListener listener);
	public DockingListener[] getDockingListeners();
}
