/*
 * Created on Mar 10, 2005
 */
package org.flexdock.docking.event;

/**
 * @author Christopher Butler
 */
public interface DockingMonitor {

    void addDockingListener(DockingListener listener);

    void removeDockingListener(DockingListener listener);

    DockingListener[] getDockingListeners();

}
