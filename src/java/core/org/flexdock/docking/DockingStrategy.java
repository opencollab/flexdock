/*
 * Created on Mar 14, 2005
 */
package org.flexdock.docking;

import org.flexdock.docking.drag.DragToken;

/**
 * @author Christopher Butler
 */
public interface DockingStrategy {
	public void dock(Dockable dockable, DockingPort port, String region);
	public void dock(Dockable dockable, DockingPort port, String region, DragToken token);
	public void undock(Dockable dockable);
}
