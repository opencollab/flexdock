/*
 * Created on Mar 10, 2005
 */
package org.flexdock.view.floating;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.Titlebar;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * @author Christopher Butler
 */
public class FloatingViewport extends Viewport implements MouseListener, MouseMotionListener {
	protected ViewFrame frame;
	protected Point dragOffset;
	protected boolean titlebarDrag;
	
	public FloatingViewport(ViewFrame frame, String persistentId) {
		super(persistentId);
		getDockingProperties().setSingleTabsAllowed(true);
		setTabsAsDragSource(true);
		this.frame = frame;
	}
	
	public String getRegion(Point p) {
		// only allow docking in CENTER
		return CENTER_REGION;
	}
	
	public boolean isDockingAllowed(String region, Component comp) {
		// only allow docking in CENTER
		if(!CENTER_REGION.equals(region))
			return false;
		return super.isDockingAllowed(region, comp);
	}

	protected void toggleListeners(Component c, boolean add) {
		if(!(c instanceof View))
			return;
		
		if(add)
			installListeners((View)c);
		else
			uninstallListeners((View)c);
	}
	
	protected void installListeners(View view) {
		Titlebar titlebar = view.getTitlebar();
    	titlebar.addMouseListener(this);
    	titlebar.addMouseMotionListener(this);
    	view.addDockingListener(this);
	}
	
	protected void uninstallListeners(View view) {
		Titlebar titlebar = view.getTitlebar();
		titlebar.removeMouseListener(this);
		titlebar.removeMouseMotionListener(this);
		view.removeDockingListener(this);
	}
	
	public boolean dock(Component comp, String desc, String region) {
		// only dock to the CENTER region
		boolean ret = super.dock(comp, desc, DockingPort.CENTER_REGION);
		if(ret)
			toggleListeners(comp, true);
		return ret;
	}

	public boolean undock(Component comp) {
		boolean ret = super.undock(comp);
		if(ret)
			toggleListeners(comp, false);
		return ret;
	}
	
	public void dragStarted(DockingEvent evt) {
		super.dragStarted(evt);
		Component dragSrc = (Component)evt.getTriggerSource();
		Dockable dockable = (Dockable)evt.getSource();
		if(dockable instanceof View) {
			View view = (View)dockable;
			titlebarDrag = dragSrc==view.getTitlebar();
			if(titlebarDrag) {
				evt.consume();	
			}
		}
	}

	public void dockingComplete(DockingEvent evt) {
		super.dockingComplete(evt);
		if(evt.getOldDockingPort()==this && this.getViewset().size()==0) {
			frame.destroy();
			frame = null;
		}
	}



	
	public void mouseMoved(MouseEvent e) {
		// noop
	}
	
	public void mousePressed(MouseEvent e) {
		dragOffset = e.getPoint();
		Component c = (Component)e.getSource();
		if(c!=frame)
			dragOffset = SwingUtilities.convertPoint(c, dragOffset, frame);
	}
	
	public void mouseDragged(MouseEvent e) {
		if(titlebarDrag) {
			Point loc = e.getPoint();
			SwingUtilities.convertPointToScreen(loc, (Component)e.getSource());
			SwingUtility.subtract(loc, dragOffset);
			frame.setLocation(loc);			
		}
	}

	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
}
