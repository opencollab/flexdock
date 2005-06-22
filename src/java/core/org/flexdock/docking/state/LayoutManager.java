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
	public DockingState getDockingState(String dockableId);
	
	public DockingState getDockingState(Dockable dockable);
	
	public FloatManager getFloatManager();
	
	public LayoutNode createLayout(DockingPort port);
	
	public boolean display(Dockable dockable);
	
	public boolean store() throws IOException;
	
	public boolean load() throws IOException;
	
	public boolean restore(boolean loadFromStorage) throws IOException;
}
