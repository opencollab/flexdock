/*
 * Created on Mar 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.docking.event;


/**
 * @author cb8167
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface DockingMonitor {
	public void addDockingListener(DockingListener listener);
	public void removeDockingListener(DockingListener listener);
	public DockingListener[] getDockingListeners();
}
