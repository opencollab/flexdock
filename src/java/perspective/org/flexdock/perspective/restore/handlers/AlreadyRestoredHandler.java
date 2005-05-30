/*
 * Created on 2005-04-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.restore.handlers;

import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.tracking.ViewTracker;

/**
 * Mateusz Szczap
 */
public class AlreadyRestoredHandler implements RestorationHandler {

	public boolean restore(Dockable dockable, DockingState info, Map context) {
		if (!DockingManager.isDocked(dockable))
			return false;

		ViewTracker.requestViewActivation(dockable.getDockable());
		SwingUtility.focus(dockable.getDockable());
		return true;
	}

}
