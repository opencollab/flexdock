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
import org.flexdock.perspective.PerspectiveManager;
import org.flexdock.util.DockingUtility;

/**
 * 
 * @author Mateusz Szczap
 */
public class RelativeHandler implements RestorationHandler {

	public boolean restore(Dockable dockable, DockingState info, Map context) {
		Dockable parent = info==null? null: info.getRelativeParent();
		// in order to do a relative docking, the parent dockable
		// must already be docked.
		if(!DockingManager.isDocked(parent))
			return false;
		
		// we can only do relative docking if the parent is embedded.
		// no relative docking if the parent is floating or minimized.
		DockingState parentInfo = PerspectiveManager.getInstance().getDockingState(parent);
		if(parentInfo==null || parentInfo.isFloating() || parentInfo.isMinimized())
			return false;
		
		float ratio = info.getSplitRatio();
		boolean ret = DockingUtility.dockRelative(parent, dockable, info.getRegion(), info.getSplitRatio());
		if(ret) {
			DockingUtility.setSplitProportion(dockable, ratio);
		}
		return ret;
    }

}
