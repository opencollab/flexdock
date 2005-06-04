/*
 * Created on Jun 3, 2005
 */
package org.flexdock.perspective.restore.handlers;

import java.awt.Component;
import java.util.Map;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.state.DockingState;
import org.flexdock.perspective.PerspectiveManager;

/**
 * @author Christopher Butler
 */
public class UnknownStateHandler implements RestorationHandler {
	private static final String[] REGIONS = {
		DockingPort.CENTER_REGION, DockingPort.WEST_REGION, DockingPort.EAST_REGION, 
		DockingPort.SOUTH_REGION, DockingPort.NORTH_REGION
	};
	
	
	public boolean restore(Dockable dockable, DockingState info, Map context) {
		DockingPort port = PerspectiveManager.getMainDockingPort();
		if(port==null)
			return false;
		
		Component comp = port.getDockedComponent();
		if(comp==null)
			return dock(dockable, port);
		
		DockingInfo dockingInfo = getDeepestWest(port);
		if(dockingInfo.dockable==null)
			return dock(dockable, dockingInfo.port);
		return dock(dockable, dockingInfo.dockable);
	}
	

	
	private boolean dock(Dockable dockable, DockingPort port) {
		return dock(dockable, null, port);
	}
	
	private boolean dock(Dockable dockable, Dockable parent) {
		return dock(dockable, parent, null);
	}
	
	private boolean dock(Dockable dockable, Dockable parent, DockingPort port) {
		boolean ret = false;
		for(int i=0; i<REGIONS.length; i++) {
			if(parent==null) {
				ret = DockingManager.dock(dockable, port, REGIONS[i]);
			}
			else {
				ret = DockingManager.dock(dockable, parent, REGIONS[i]);
			}
			if(ret)
				return true;
		}
		return false;
	}

	
	private DockingInfo getDeepestWest(DockingPort port) {
		Component comp = port.getDockedComponent();
		if(comp instanceof JTabbedPane) {
			Dockable d = port.getDockable(DockingPort.CENTER_REGION);
			return new DockingInfo(d, port);
		}
		
		if(comp instanceof JSplitPane) {
			comp = ((JSplitPane)comp).getLeftComponent();
			if(comp instanceof DockingPort)
				return getDeepestWest((DockingPort)comp);
		}
		
		return new DockingInfo(DockingManager.getDockable(comp), port);
	}
	
	private static class DockingInfo {
		private Dockable dockable;
		private DockingPort port;
		
		private DockingInfo(Dockable d, DockingPort p) {
			dockable = d;
			port = p;
		}
	}
}
