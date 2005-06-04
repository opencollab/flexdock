/* 
 * Copyright (c) 2005 FlexDock Development Team. All rights reserved. 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in the 
 * Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE.
 */
package org.flexdock.perspective.restore.handlers;

import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.perspective.PerspectiveManager;
import org.flexdock.util.DockingUtility;

/**
 * Created on 2005-05-12
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @author <a href="mailto:marius@eleritec.net">Christopher Butler</a>
 * @version $Id: RelativeHandler.java,v 1.3 2005-06-04 16:42:21 winnetou25 Exp $
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
