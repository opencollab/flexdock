/*
 * Created on Apr 21, 2005
 */
package org.flexdock.dockbar.layout;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.flexdock.dockbar.Dockbar;
import org.flexdock.dockbar.DockbarManager;
import org.flexdock.dockbar.ViewPane;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.props.DockablePropertySet;
import org.flexdock.docking.state.MinimizationManager;

/**
 * @author Christopher Butler
 */
public class DockbarLayout {
	public static final int MINIMUM_VIEW_SIZE = 20;
	
	private DockbarManager manager;
	
	
	public DockbarLayout(DockbarManager mgr) {
		manager = mgr;
	}
	


	public void layout() {
		Rectangle rect = DockbarLayoutManager.getManager().getLayoutArea(manager);
		int rightX = rect.x + rect.width;
		int bottomY = rect.y + rect.height;
		
		Dockbar leftBar = manager.getLeftBar();
		Dockbar rightBar = manager.getRightBar();
		Dockbar bottomBar = manager.getBottomBar();
		
		Dimension leftPref = leftBar.getPreferredSize();
		Dimension rightPref = rightBar.getPreferredSize();
		Dimension bottomPref = bottomBar.getPreferredSize();
		
		// set the dockbar bounds
		leftBar.setBounds(rect.x, rect.y, leftPref.width, rect.height-bottomPref.height);
		rightBar.setBounds(rightX-rightPref.width, rect.y, rightPref.width, rect.height-bottomPref.height);
		bottomBar.setBounds(rect.x+leftPref.width, bottomY-bottomPref.height, rect.width-leftPref.width-rightPref.width, bottomPref.height);

		layoutViewpane();
	}

	
	public int getDesiredViewpaneSize() {
		Dockable dockable = manager.getActiveDockable();
		if(dockable==null)
			return 0;
		
		Rectangle rect = DockbarLayoutManager.getManager().getViewArea(manager, dockable);
		DockablePropertySet props = dockable.getDockingProperties();
		
		// determine what percentage of the viewable area we want the viewpane to take up
		float viewSize = props.getPreviewSize().floatValue();
		int edge = manager.getActiveEdge();
		if(edge==MinimizationManager.LEFT || edge==MinimizationManager.RIGHT) {
			return (int)(((float)rect.width)*viewSize);
		}
		return (int)(((float)rect.height)*viewSize);
	}
	
	private void layoutViewpane() {
		ViewPane viewPane = manager.getViewPane();
		Dockable dockable = manager.getActiveDockable();
		if(dockable==null) {
			viewPane.setBounds(0, 0, 0, 0);
			return;
		}

		int edge = manager.getActiveEdge();
		int viewpaneSize = viewPane.getPrefSize();
		if(viewpaneSize==ViewPane.UNSPECIFIED_PREFERRED_SIZE)
			viewpaneSize = getDesiredViewpaneSize();
		
		Rectangle rect = DockbarLayoutManager.getManager().getViewArea(manager, dockable);
		if(edge==MinimizationManager.LEFT || edge==MinimizationManager.RIGHT) {
			if(edge==MinimizationManager.RIGHT) {
				rect.x = rect.x + rect.width - viewpaneSize;
			}
			rect.width = viewpaneSize;
		}
		else {
			if(edge==MinimizationManager.BOTTOM) {
                rect.y = rect.y + rect.height - viewpaneSize;
			}
			rect.height = viewpaneSize;
		}
		
		viewPane.setBounds(rect);
	}
}
