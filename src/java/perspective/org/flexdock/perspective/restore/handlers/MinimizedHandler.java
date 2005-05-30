/*
 * Created on 2005-05-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.restore.handlers;

import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.util.DockingUtility;

/**
 * 
 * @author Mateusz Szczap
 */
public class MinimizedHandler implements RestorationHandler {

	public boolean restore(Dockable dockable, DockingState info, Map context) {
		if(info==null || !info.isMinimized())
			return false;
		
		DockingManager.getMinimizeManager().preview(dockable, true);
		DockingManager.setMinimized(dockable, true, info.getDockbarEdge());
		return DockingUtility.isMinimized(dockable);
    }

}
