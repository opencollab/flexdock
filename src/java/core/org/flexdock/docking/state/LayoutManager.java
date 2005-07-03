/*
 * Created on May 27, 2005
 */
package org.flexdock.docking.state;

import java.io.IOException;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;
import org.flexdock.perspective.persist.PersisterException;

/**
 * @author Christopher Butler
 */
public interface LayoutManager {
	
    DockingState getDockingState(String dockableId);
	
	DockingState getDockingState(Dockable dockable);
	
	FloatManager getFloatManager();
	
	LayoutNode createLayout(DockingPort port);
	
	boolean display(Dockable dockable);
	
	boolean store() throws IOException, PersisterException;
	
	boolean load() throws IOException, PersisterException;
	
	boolean restore(boolean loadFromStorage) throws IOException, PersisterException;
    
}
