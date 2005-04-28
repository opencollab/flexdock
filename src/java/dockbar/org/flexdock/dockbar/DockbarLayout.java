/*
 * Created on Apr 21, 2005
 */
package org.flexdock.dockbar;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.props.DockableProps;
import org.flexdock.util.RootWindow;

/**
 * @author Christopher Butler
 */
public class DockbarLayout implements SwingConstants {
	public static final int MINIMUM_VIEW_SIZE = 20;
	
	private DockbarManager manager;
	
	
	public DockbarLayout(DockbarManager mgr) {
		manager = mgr;
	}
	
	private Rectangle getLayoutArea() {
		RootWindow window = manager.getWindow();
		if(window==null)
			return new Rectangle(0, 0, 0, 0);
		
		Container contentPane = window.getContentPane();
		JLayeredPane layeredPane = window.getLayeredPane();

		// no rectangle translation required because layeredPane is already the direct
		// parent of contentPane.

		return contentPane.getBounds();
	}

	public void layout() {
		Rectangle rect = getLayoutArea();
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
	
	public Rectangle getViewpaneArea() {
		Rectangle leftBar = manager.getLeftBar().getBounds();
		Rectangle bottomBar = manager.getBottomBar().getBounds();
		
		return new Rectangle(leftBar.x + leftBar.width, leftBar.y, bottomBar.width, leftBar.height);
	}
	
	public int getDesiredViewpaneSize() {
		Dockable dockable = manager.getActiveDockable();
		if(dockable==null)
			return 0;
		
		Rectangle rect = getViewpaneArea();
		DockableProps props = dockable.getDockingProperties();
		
		// determine what percentage of the viewable area we want the viewpane to take up
		float viewSize = props.getPreviewSize().floatValue();
		int edge = manager.getActiveEdge();
		if(edge==LEFT || edge==RIGHT) {
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
		
		Rectangle rect = getViewpaneArea();
		if(edge==LEFT || edge==RIGHT) {
			if(edge==RIGHT) {
				rect.x = rect.x + rect.width - viewpaneSize;
			}
			rect.width = viewpaneSize;
		}
		else {
			if(edge==BOTTOM) {
				rect.y = rect.height - viewpaneSize;
			}
			rect.height = viewpaneSize;
		}
		
		viewPane.setBounds(rect);
	}
}
