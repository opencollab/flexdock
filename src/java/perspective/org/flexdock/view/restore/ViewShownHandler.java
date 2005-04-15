/*
 * Created on 2005-04-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.restore;

import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;

/**
 * Mateusz Szczap
 */
public class ViewShownHandler implements ShowViewHandler {

	public boolean showView(View view, Map context) {

		if (DockingManager.isDocked((Dockable) view)) {
			view.setActive(true);
			SwingUtility.focus(view);
			return true;
		}

		return false;
	}

}
