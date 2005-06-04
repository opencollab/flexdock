/*
 * Created on May 27, 2005
 */
package org.flexdock.docking.state;

import java.io.IOException;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;

/**
 * @author Christopher Butler
 */
public interface LayoutManager {
	public DockingState getDockingState(String dockable);
	public DockingState getDockingState(Dockable dockable);
	public FloatManager getFloatManager();
	public LayoutNode createLayout(DockingPort port);
	public boolean restore(Dockable dockable);
	
	public boolean persist(String applicationKey) throws IOException;
	public boolean loadFromStorage(String applicationKey) throws IOException;
}
