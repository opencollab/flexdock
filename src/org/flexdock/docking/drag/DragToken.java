package org.flexdock.docking.drag;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;

import javax.swing.SwingUtilities;

import org.flexdock.docking.DockingPort;

public class DragToken {
	private Component dragSource;
	private Component dockable;
	private Point mouseOffset;
	private Point currentMouse;
	private EventListener[] cachedListeners;
	private MouseMotionListener pipelineListener;
	private DockingPort targetPort;
	private String targetRegion;
	

	
	public DragToken(Component dockable, MouseEvent evt) {
		if(dockable==null)
			throw new NullPointerException("'dockable' parameter cannot be null.");
		if(evt==null)
			throw new NullPointerException("'evt' parameter cannot be null.");
		if(!(evt.getSource() instanceof Component))
			throw new IllegalArgumentException("'evt.getSource()' must be an instance of java.awt.Component.");
		
		this.dockable = dockable;
		dragSource = (Component)evt.getSource();
		currentMouse = evt.getPoint();
		mouseOffset = calculateMouseOffset(evt.getPoint());
	}
	
	private Point calculateMouseOffset(Point evtPoint) {
		Point dockableLoc = dockable.getLocationOnScreen();
		SwingUtilities.convertPointToScreen(evtPoint, dragSource);
		Point offset = new Point();
		offset.x = dockableLoc.x - evtPoint.x;
		offset.y = dockableLoc.y - evtPoint.y;
		return offset;
	}
	
	public Component getDockable() {
		return dockable;
	}
	
	public Point getMouseOffset() {
		return (Point)mouseOffset.clone();
	}
	
	public void updateMouse(MouseEvent me) {
		if(me!=null && me.getSource()==dragSource)
			currentMouse = me.getPoint();
	}
	
	public Point getCurrentMouse() {
		return getCurrentMouse(false);
	}
	
	public Point getCurrentMouse(boolean relativeToScreen) {
		Point p = (Point)currentMouse.clone();
		if(relativeToScreen)
			SwingUtilities.convertPointToScreen(p, dragSource);
		return p;
	}
	
	public Rectangle getDragRect(boolean relativeToScreen) {
		Point p = getCurrentMouse(relativeToScreen);
		Point offset = getMouseOffset();
		p.x += offset.x;
		p.y += offset.y;
		
		Rectangle r = new Rectangle(getDragSize());
		r.setLocation(p);
		return r;
		
	}
	
	public Point getCurrentMouse(Component target) {
		if(target==null)
			return null;
		return SwingUtilities.convertPoint(dragSource, currentMouse, target);
	}
	
	public Dimension getDragSize() {
		return ((Component)dockable).getSize();
	}
	
	public Component getDragSource() {
		return dragSource;
	}
	
	public void setTarget(DockingPort port, String region) {
		targetPort = port;
		targetRegion = region==null? DockingPort.UNKNOWN_REGION: region;
	}
	
	public DockingPort getTargetPort() {
		return targetPort;
	}
	
	public String getTargetRegion() {
		return targetRegion;
	}

	public EventListener[] getCachedListeners() {
		return cachedListeners==null? new EventListener[0]: cachedListeners;
	}

	public void setCachedListeners(EventListener[] listeners) {
		cachedListeners = listeners;
	}

	public MouseMotionListener getPipelineListener() {
		return pipelineListener;
	}

	public void setPipelineListener(MouseMotionListener pipelineListener) {
		this.pipelineListener = pipelineListener;
	}

}