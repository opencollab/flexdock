/*
 * Created on Apr 18, 2005
 */
package org.flexdock.view.tracking;

import org.flexdock.dockbar.event.DockbarEvent;
import org.flexdock.dockbar.event.DockbarListener;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.view.View;
import org.flexdock.view.floating.FloatingViewport;

/**
 * @author Christopher Butler
 */
public class DockbarMonitor implements DockbarListener {

	public void dockableLocked(DockbarEvent evt) {
		View view = getView(evt);
		if(view!=null)
			ViewTracker.requestViewActivation(view);
	}
	
	public void dockableCollapsed(DockbarEvent evt) {

	}
	
	public void dockableExpanded(DockbarEvent evt) {

	}
	
	private View getView(DockbarEvent evt) {
		Dockable d = (Dockable)evt.getSource();
		return d instanceof View? (View)d: null;
	}
	
	public void minimizeStarted(DockbarEvent evt) {
		View view = getView(evt);
		if(view!=null) {
			DockingPort port = DockingManager.getDockingPort((Dockable)view);
			// block minimization on floating views
			if(port instanceof FloatingViewport)
				evt.consume();
		}
	}
	
	public void minimizeCompleted(DockbarEvent evt) {
		
	}
}
