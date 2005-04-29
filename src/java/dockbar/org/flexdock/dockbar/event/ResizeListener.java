/*
 * Created on Apr 18, 2005
 */
package org.flexdock.dockbar.event;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.flexdock.dockbar.DockbarLayout;
import org.flexdock.dockbar.DockbarManager;
import org.flexdock.dockbar.ViewPane;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.props.DockableProps;
import org.flexdock.util.DockingConstants;
import org.flexdock.util.RootWindow;

/**
 * @author Christopher Butler
 */
public class ResizeListener extends MouseAdapter implements MouseMotionListener, DockingConstants {
	private DockbarManager manager;
	private Dockable dockable;
	
	private JPanel dragGlassPane;
	private Component cachedGlassPane;
	private RootWindow rootWindow;
	
	public ResizeListener(DockbarManager mgr) {
		manager = mgr;
		dragGlassPane = new JPanel();
		dragGlassPane.setOpaque(false);
	}
	
	public void mouseMoved(MouseEvent e) {
		// noop
	}
	
	public void mousePressed(MouseEvent e) {
		dockable = manager.getActiveDockable();
		rootWindow = manager.getWindow();
		cachedGlassPane = rootWindow.getGlassPane();
		rootWindow.setGlassPane(dragGlassPane);
		dragGlassPane.setCursor(manager.getResizeCursor());
		dragGlassPane.setVisible(true);
		manager.setDragging(true);
	}
	
	public void mouseReleased(MouseEvent e) {
		dockable = null;
		dragGlassPane.setVisible(false);
		manager.setDragging(false);
		
		if(rootWindow!=null && cachedGlassPane!=null) {
			rootWindow.setGlassPane(cachedGlassPane);
			cachedGlassPane = null;
			rootWindow = null;			
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		if(dockable!=null)
			handleResizeEvent(e);
	}
	
	private void handleResizeEvent(MouseEvent me) {
		ViewPane viewPane = manager.getViewPane();
		Point p = SwingUtilities.convertPoint((Component)me.getSource(), me.getPoint(), viewPane.getParent());
		Rectangle viewArea = manager.getViewPaneArea();
		
		p.x = Math.max(p.x, 0);
		p.x = Math.min(p.x, viewArea.width);
		p.y = Math.max(p.y, 0);
		p.y = Math.min(p.y, viewArea.height);
		
		int orientation = manager.getActiveEdge();
		int loc = orientation==LEFT || orientation==RIGHT? p.x: p.y;
		int dim = orientation==LEFT || orientation==RIGHT? viewArea.width: viewArea.height;
		
		if(orientation==RIGHT || orientation==BOTTOM)
			loc = dim - loc;
		
		float percent = (float)loc/(float)dim;
		float minPercent = (float)DockbarLayout.MINIMUM_VIEW_SIZE/(float)dim;
		percent = Math.max(percent, minPercent);
		
		DockableProps props = dockable.getDockingProperties();
		props.setPreviewSize(percent);
		manager.revalidate();
	}

}
