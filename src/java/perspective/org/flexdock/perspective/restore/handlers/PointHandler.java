/*
 * Created on May 26, 2005
 */
package org.flexdock.perspective.restore.handlers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.state.DockingState;
import org.flexdock.perspective.RestorationManager;
import org.flexdock.util.ComponentNest;
import org.flexdock.util.RootWindow;

/**
 * @author Christopher Butler
 */
public class PointHandler implements RestorationHandler {
	
	private boolean restoreDockable(Dockable dockable, Component win, DockingState info) {
		RootWindow window = RootWindow.getRootContainer(win);
		Container contentPane = window.getContentPane();
		
		Point dropPoint = getDropPoint(dockable, contentPane, info);
		if(dropPoint==null)
			return false;
		
		Component deep = SwingUtilities.getDeepestComponentAt(contentPane, dropPoint.x, dropPoint.y);
		ComponentNest dropTargets = ComponentNest.find(deep, Dockable.class, DockingPort.class);	
		
		DockingPort port = dropTargets==null? null: (DockingPort)dropTargets.parent;
		Point mousePoint = port==null? null: SwingUtilities.convertPoint(contentPane, dropPoint, (Component)port);
		String region = port==null? DockingPort.UNKNOWN_REGION: port.getRegion(mousePoint);

		return DockingManager.dock(dockable, port, region);
	}
	
	
	private Point getDropPoint(Dockable dockable, Container contentPane, DockingState info) {
		if(!info.hasCenterPoint())
			return null;
		
		float percentX = (float)info.getCenterX()/100f;
		float percentY = (float)info.getCenterY()/100f;
		
		Point dropPoint = new Point();
		dropPoint.x = Math.round((float)contentPane.getWidth() * percentX);
		dropPoint.y = Math.round((float)contentPane.getHeight() * percentY);
		return dropPoint;
	}
	
	public boolean restore(Dockable dockable, DockingState info, Map context) {
		if(DockingManager.isDocked(dockable))
			return false;
		
		Component owner = RestorationManager.getRestoreContainer(dockable);
		return restoreDockable(dockable, owner, info);
	}
}
