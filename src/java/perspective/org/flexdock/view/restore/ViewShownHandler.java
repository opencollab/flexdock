/*
 * Created on 2005-04-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.restore;

import java.util.Map;

import org.flexdock.dockbar.DockbarManager;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;
import org.flexdock.view.tracking.ViewTracker;

/**
 * Mateusz Szczap
 */
public class ViewShownHandler implements ShowViewHandler {

	public boolean showView(View view, Map context) {
		if (view.isMinimized()) {
			DockbarManager.getCurrent().setActiveDockable(view);
			//DockingManager.setMinimized(view, false);
			return true;
		}
		if (DockingManager.isDocked((Dockable) view)) {
			ViewTracker.requestViewActivation(view);
			SwingUtility.focus(view);

			return true;
		}

		return false;
	}

}
