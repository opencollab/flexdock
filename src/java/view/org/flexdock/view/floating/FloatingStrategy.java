/*
 * Created on Mar 14, 2005
 */
package org.flexdock.view.floating;

import java.awt.Dimension;
import java.awt.Point;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingStrategy;
import org.flexdock.docking.drag.DragToken;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;

/**
 * @author Christopher Butler
 *
 */
public class FloatingStrategy extends DefaultDockingStrategy {
	
	protected boolean isFloatable(Dockable dockable, DragToken token) {
		// can't float on a fake drag operation 
		if(token.isPseudoDrag() || !(dockable instanceof View))
			return false;
		
		// TODO: break this check out into a separate DropPolicy class.
		// should be any customizable criteria, not hardcoded to checking
		// for being outside the bounds of a window
		if(token.isOverWindow())
			return false;
		
		return true;
	}
	
	protected DockingResults dropComponent(Dockable dockable, DockingPort target, String region, DragToken token) {
		// if we're not floatable, then proceed with the default behavior
		if(!isFloatable(dockable, token))
			return super.dropComponent(dockable, target, region, token);
		
		DockingResults results = new DockingResults(target, false);
		region = DockingPort.CENTER_REGION;
		View view = (View)dockable;

		Point screenLoc = token.getCurrentMouse(true);
		SwingUtility.add(screenLoc, token.getMouseOffset());
		Dimension size = view.getSize();
		
		ViewFrame frame = ViewFrame.create(view);
		frame.setBounds(screenLoc.x, screenLoc.y, size.width, size.height);
		
		results.dropTarget = frame.getDockingPort();

		// undock the current Dockable instance from it's current parent container
		undock(dockable);

		// add to the floating frame
		frame.addView(view);
		frame.setVisible(true);
		results.success = true;
		return results;
	}
}
