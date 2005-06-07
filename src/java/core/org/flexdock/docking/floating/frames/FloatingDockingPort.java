/*
 * Created on Mar 10, 2005
 */
package org.flexdock.docking.floating.frames;

import java.awt.Component;
import java.awt.Point;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JTabbedPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.event.DockingEvent;

/**
 * @author Christopher Butler
 */
public class FloatingDockingPort extends DefaultDockingPort {
	protected DockingFrame frame;
	protected FrameDragListener dragListener;
	
	public FloatingDockingPort(DockingFrame frame, String persistentId) {
		super(persistentId);
		getDockingProperties().setSingleTabsAllowed(true);
		setTabsAsDragSource(true);
		this.frame = frame;
		dragListener = new FrameDragListener(frame);
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


	

	
	public boolean dock(Component comp, String desc, String region) {
		// only dock to the CENTER region
		boolean ret = super.dock(comp, desc, CENTER_REGION);
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

		boolean listenerEnabled = dockable.getFrameDragSources().contains(dragSrc);
		dragListener.setEnabled(listenerEnabled);
		if(listenerEnabled) {
			evt.consume();
		}
	}

	public void undockingComplete(DockingEvent evt) {
		super.undockingComplete(evt);
		if(evt.getOldDockingPort()==this && getDockableCount()==0) {
			frame.destroy();
			frame = null;
		}
	}

	protected void toggleListeners(Component comp, boolean add) {
		Dockable dockable = DockingManager.getDockable(comp);
		if(add)
			installListeners(dockable);
		else
			uninstallListeners(dockable);
	}

	protected void installListeners(Dockable dockable) {
		Set frameDraggers = dockable.getFrameDragSources();
		for(Iterator it=frameDraggers.iterator(); it.hasNext();) {
			Component frameDragSrc = (Component)it.next();
			frameDragSrc.addMouseListener(dragListener);
			frameDragSrc.addMouseMotionListener(dragListener);			
		}

		dockable.addDockingListener(this);
	}
	
	protected void uninstallListeners(Dockable dockable) {
		Set frameDraggers = dockable.getFrameDragSources();
		for(Iterator it=frameDraggers.iterator(); it.hasNext();) {
			Component frameDragSrc = (Component)it.next();
			frameDragSrc.removeMouseListener(dragListener);
			frameDragSrc.removeMouseMotionListener(dragListener);			
		}
		dockable.removeDockingListener(this);
	}
	
	public int getDockableCount() {
		Component comp = getDockedComponent();
		if(!(comp instanceof JTabbedPane))
			return 0;
		return ((JTabbedPane)comp).getTabCount();
	}

}
