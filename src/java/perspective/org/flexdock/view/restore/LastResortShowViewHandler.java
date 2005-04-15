/*
 * Created on 2005-04-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.restore;

import java.util.Map;

import org.flexdock.docking.DockingPort;
import org.flexdock.docking.RegionChecker;
import org.flexdock.view.View;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LastResortShowViewHandler implements ShowViewHandler {

	/**
	 * @see org.flexdock.view.restore.ShowViewHandler#showView(org.flexdock.view.View, java.util.Map)
	 */
	public boolean showView(View view, Map context) {
		View territoralView = (View) context.get("territoral.view");
		if (territoralView == null) return false;
		
		return territoralView.dock(view, DockingPort.EAST_REGION, RegionChecker.DEFAULT_SIBLING_SIZE);
	}

}
