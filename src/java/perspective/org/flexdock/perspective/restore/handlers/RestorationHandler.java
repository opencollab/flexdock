package org.flexdock.perspective.restore.handlers;

import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.state.DockingState;

/**
 *
 * @author Mateusz Szczap 
 */
public interface RestorationHandler {

	boolean restore(Dockable dockable, DockingState info, Map context);
	
}
