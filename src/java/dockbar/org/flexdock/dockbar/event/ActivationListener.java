/*
 * Created on Apr 22, 2005
 */
package org.flexdock.dockbar.event;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import org.flexdock.dockbar.DockbarManager;
import org.flexdock.dockbar.ViewPane;
import org.flexdock.util.Utilities;

/**
 * @author Christopher Butler
 */
public class ActivationListener implements MouseListener, MouseMotionListener {
	private DockbarManager manager;
	private Deactivator deactivator;
	private boolean enabled;
	private boolean mouseOver;
	
	
	public ActivationListener(DockbarManager mgr) {
		manager = mgr;
		setEnabled(true);
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e) {
	}
	public void mouseMoved(MouseEvent e) {
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isActive() {
		return manager.isActive() && !manager.isAnimating() && !manager.isDragging();
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private boolean isAvailable() {
		return isEnabled() && isActive();
	}
	
	private Rectangle getViewpaneBounds() {
		ViewPane viewPane = manager.getViewPane();
		Rectangle bounds = viewPane.getBounds();
		bounds.setLocation(0, 0);
		return bounds; 
	}
	
	private boolean isOverDockbars(MouseEvent evt) {
		Container pane = manager.getLeftBar().getParent();
		Point p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getPoint(), pane);

		return manager.getLeftBar().getBounds().contains(p)
			|| manager.getRightBar().getBounds().contains(p)
			|| manager.getBottomBar().getBounds().contains(p);
	}
	
	private boolean isValidMouseExit(MouseEvent evt) {
		Rectangle bounds = getViewpaneBounds();
		
		Point p = evt.getPoint();
		p = SwingUtilities.convertPoint((Component)evt.getSource(), p, manager.getViewPane());
		return !bounds.contains(p);
	}
	
	
	
	public void mouseEntered(MouseEvent e) {
		if(!isAvailable() || mouseOver)
			return;

		mouseOver = true;
		if(deactivator!=null)
			deactivator.setEnabled(false);
		deactivator = null;
	}
	
	public void mouseExited(MouseEvent e) {
		if(!isAvailable() || !isValidMouseExit(e))
			return;
		
		mouseOver = false;
		
		if(!isOverDockbars(e)) {
			deactivator = new Deactivator(manager.getActiveDockableId());
			deactivator.setEnabled(true);
			deactivator.start();			
		}
	}
	
	private class Deactivator extends Thread {
		private String dockableId;
		private boolean enabled;
		
		private Deactivator(String id) {
			dockableId = id;
			enabled = true;
		}
		
		private synchronized void setEnabled(boolean b) {
			enabled = b;
		}
		
		private synchronized boolean isEnabled() {
			return enabled;
		}
		
		public void run() {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			if(isEnabled() && !Utilities.isChanged(dockableId, manager.getActiveDockableId()) )
				manager.setActiveDockable((String)null);
		}

	}

}
