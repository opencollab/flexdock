/*
 * Created on Apr 18, 2005
 */
package org.flexdock.dockbar;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.props.DockableProps;
import org.flexdock.util.RootWindow;
import org.flexdock.util.SwingUtility;

/**
 * @author Christopher Butler
 */
public class ResizeListener extends MouseAdapter implements MouseMotionListener, SwingConstants {
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
		dockable = manager.getDockbarPane().getDockable();
		rootWindow = manager.getWindow();
		cachedGlassPane = rootWindow.getGlassPane();
		rootWindow.setGlassPane(dragGlassPane);
		dragGlassPane.setCursor(manager.getDockbarPane().getResizeCursor());
		dragGlassPane.setVisible(true);
	}
	
	public void mouseReleased(MouseEvent e) {
		dockable = null;
		dragGlassPane.setVisible(false);
		
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
		DockbarPane pane = manager.getDockbarPane();
		Point p = SwingUtilities.convertPoint((Component)me.getSource(), me.getPoint(), pane);
		int w = pane.getWidth();
		int h = pane.getHeight();
		
		p.x = Math.max(p.x, 0);
		p.x = Math.min(p.x, w);
		p.y = Math.max(p.y, 0);
		p.y = Math.min(p.y, h);
		
		int orientation = pane.getOrientation();
		int loc = orientation==LEFT || orientation==RIGHT? p.x: p.y;
		int dim = orientation==LEFT || orientation==RIGHT? w: h;
		
		if(orientation==RIGHT || orientation==BOTTOM)
			loc = dim - loc;
		
		float percent = (float)loc/(float)dim;
		DockableProps props = dockable.getDockingProperties();
		props.setPinSize(percent);
		SwingUtility.revalidateComponent(pane);
	}

}
