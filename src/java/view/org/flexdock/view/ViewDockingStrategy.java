/*
 * Created on Mar 14, 2005
 */
package org.flexdock.view;

import java.awt.Dimension;
import java.awt.Point;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingStrategy;
import org.flexdock.docking.drag.DragToken;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.floating.FloatingViewport;
import org.flexdock.view.floating.ViewFrame;

/**
 * @author Christopher Butler
 *
 */
public class ViewDockingStrategy extends DefaultDockingStrategy {
	
	private static final ViewDockingStrategy SINGLETON = new ViewDockingStrategy();
	
	public static ViewDockingStrategy getInstance() {
		return SINGLETON;
	}
	
	protected boolean isFloatable(Dockable dockable, DragToken token) {
		// can't float null objects
		if(dockable==null || dockable.getDockable()==null || token==null)
			return false;
		
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

	protected boolean isDockingPossible(Dockable dockable, DockingPort port, String region, DragToken token) {
		// superclass blocks docking if the 'port' or 'region' are null.  If we've dragged outside
		// the bounds of the parent frame, then both of these will be null.  This is expected here and
		// we intend to float in this case.
		if(isFloatable(dockable, token))
			return true;
		
		
		// check to see if we're already floating and we're trying to drop into the 
		// same dialog.
		DockingPort oldPort = DockingManager.getDockingPort(dockable);
		if(oldPort instanceof FloatingViewport && oldPort==port) {
			// only allow this situation if we're not the *last* dockable
			// in the viewport.  if we're removing the last dockable, then
			// the dialog will disappear before we redock, and we don't want this
			// to happen.
			FloatingViewport viewport = (FloatingViewport)oldPort;
			if(viewport.getViewset().size()==1)
				return false;
		}
		

		// if not floatable, then use the default validation algorithm
		return super.isDockingPossible(dockable, port, region, token);
	}

	
	protected DockingResults dropComponent(Dockable dockable, DockingPort target, String region, DragToken token) {
		// if we're not floatable, then proceed with the default behavior
		if(!isFloatable(dockable, token))
			return super.dropComponent(dockable, target, region, token);

		// otherwise,  setup a new ViewFrame and retarget to the CENTER region
		DockingResults results = new DockingResults(target, false);
		region = DockingPort.CENTER_REGION;
		View view = (View)dockable;

		// determine the bounds of the new frame
		Point screenLoc = token.getCurrentMouse(true);
		SwingUtility.add(screenLoc, token.getMouseOffset());
		Dimension size = view.getSize();
		
		// create the frame
		ViewFrame frame = ViewFrame.create(view);
		frame.setBounds(screenLoc.x, screenLoc.y, size.width, size.height);
		
		// grab a reference to the frame's dockingPort for posterity
		results.dropTarget = frame.getDockingPort();

		// undock the current Dockable instance from it's current parent container
		undock(dockable);

		// add to the floating frame
		frame.addView(view);
		
		// display and return
		frame.setVisible(true);
		results.success = true;
		return results;
	}
	
	protected DockingPort createDockingPortImpl(DockingPort base) {
		return new Viewport();
	}
}
