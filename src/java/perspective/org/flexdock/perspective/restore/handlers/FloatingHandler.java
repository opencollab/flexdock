/*
 * Created on 2005-05-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.restore.handlers;

import java.awt.Component;
import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.floating.frames.DockingFrame;
import org.flexdock.docking.state.DockingState;
import org.flexdock.perspective.RestorationManager;

/**
 * 
 * @author Mateusz Szczap
 */
public class FloatingHandler implements RestorationHandler {

	public boolean restore(Dockable dockable, DockingState info, Map context) {
		if(info==null || !info.isFloating())
			return false;

		Component owner = RestorationManager.getRestoreContainer(dockable);
		if(owner==null)
			return false;
		
		DockingFrame frame = DockingManager.getFloatManager().floatDockable(dockable, owner);
		return frame!=null;
    }

}
