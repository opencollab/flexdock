/*
 * Created on Apr 18, 2005
 */
package org.flexdock.view.tracking;

import org.flexdock.dockbar.event.DockbarEvent;
import org.flexdock.dockbar.event.DockbarListener;
import org.flexdock.docking.Dockable;
import org.flexdock.view.View;

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
		// block minimization on floating views
		if(view!=null && view.isFloating())
			evt.consume();
	}
	
	public void minimizeCompleted(DockbarEvent evt) {
		
	}
}
