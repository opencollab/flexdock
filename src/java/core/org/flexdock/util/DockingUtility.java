/*
 * Created on Mar 14, 2005
 */
package org.flexdock.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;

/**
 * @author Christopher Butler
 */
public class DockingUtility {
	
	public static DockingPort getParentDockingPort(Dockable d) {
		return d==null? null: getParentDockingPort(d.getDockable());
	}

	public static DockingPort getParentDockingPort(Component comp) {
		DockingPort port = comp==null? null: (DockingPort)SwingUtilities.getAncestorOfClass(DockingPort.class, comp);
		if(port==null)
			return null;
			
		return port.isParentDockingPort(comp)? port: null;
	}
	
	public static DockingPort findDockingPort(Container c, Point p) {
		if(c==null || p==null)
			return null;
		
		Component deepestComponent = SwingUtilities.getDeepestComponentAt(c, p.x, p.y);
		if (deepestComponent == null)
			return null;
	
		// we're assured here that the deepest component is both a Component and DockingPort in
		// this case, so we're okay to return here.
		if (deepestComponent instanceof DockingPort)
			return (DockingPort) deepestComponent;
	
		// getAncestorOfClass() will either return a null or a Container that is also an instance of
		// DockingPort.  Since Container is a subclass of Component, we're fine in returning both
		// cases.
		return (DockingPort) SwingUtilities.getAncestorOfClass(DockingPort.class, deepestComponent);
	}
	
	public static String translateRegion(JSplitPane splitPane, String region) {
		if(splitPane==null || !DockingManager.isValidDockingRegion(region))
			return null;
		
		boolean horizontal = splitPane.getOrientation()==JSplitPane.HORIZONTAL_SPLIT;
		if(horizontal) {
			if(DockingPort.NORTH_REGION.equals(region))
				region = DockingPort.WEST_REGION;
			else if(DockingPort.SOUTH_REGION.equals(region))
				region = DockingPort.EAST_REGION;
		}
		else {
			if(DockingPort.WEST_REGION.equals(region))
				region = DockingPort.NORTH_REGION;
			else if(DockingPort.EAST_REGION.equals(region))
				region = DockingPort.SOUTH_REGION;
		}
		return region;
	}
}
