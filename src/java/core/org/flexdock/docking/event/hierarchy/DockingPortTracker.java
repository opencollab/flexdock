/*
 * Created on Apr 27, 2005
 */
package org.flexdock.docking.event.hierarchy;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.flexdock.docking.DockingPort;
import org.flexdock.util.RootWindow;

/**
 * @author Christopher Butler
 */
public class DockingPortTracker implements HierarchyListener {
	private static final DockingPortTracker SINGLETON = new DockingPortTracker();
	private static WeakHashMap TRACKERS_BY_WINDOW = new WeakHashMap();
	

	public static HierarchyListener getInstance() {
		return SINGLETON;
	}
	
	public static RootDockingPortInfo getRootDockingPortInfo(Component c) {
		RootWindow window = RootWindow.getRootContainer(c);
		return getRootDockingPortInfo(window);
	}
	
	public static RootDockingPortInfo getRootDockingPortInfo(RootWindow window) {
		if(window==null)
			return null;

		RootDockingPortInfo info = (RootDockingPortInfo)TRACKERS_BY_WINDOW.get(window);
		if(info==null) {
			synchronized(TRACKERS_BY_WINDOW) {
				info = new RootDockingPortInfo(window);
				TRACKERS_BY_WINDOW.put(window, info);
			}
		}
		return info;
	}
	
	
	private boolean isParentChange(HierarchyEvent evt) {
		if(evt.getID()!=HierarchyEvent.HIERARCHY_CHANGED || evt.getChangeFlags()!=HierarchyEvent.PARENT_CHANGED)
			return false;
		return true;
	}
	
	private boolean isRemoval(HierarchyEvent evt) {
		return evt.getChanged().getParent()==null;
	}
	

	public void hierarchyChanged(HierarchyEvent evt) {
		// only work with DockingPorts
		if(!(evt.getSource() instanceof DockingPort))
			return;
		
		// we don't want to work with sub-ports
		DockingPort port = (DockingPort)evt.getSource();
		if(port.isTransient())
			return;
		
		// only work with parent-change events
		if(!isParentChange(evt))
			return;
		
		// root-ports are tracked by window.  if we can't find a parent window, then we 
		// can track the dockingport.
		Container changedParent = evt.getChangedParent();
		RootWindow window = RootWindow.getRootContainer(changedParent);
		if(window==null)
			return;
		
		boolean removal = isRemoval(evt);
		if(removal)
			dockingPortRemoved(window, port);
		else
			dockingPortAdded(window, port);
	}
	
	public void dockingPortAdded(RootWindow window, DockingPort port) {
		RootDockingPortInfo info = getRootDockingPortInfo(window);
		if(info!=null)
			info.add(port);
	}
	
	public void dockingPortRemoved(RootWindow window, DockingPort port) {
		RootDockingPortInfo info = getRootDockingPortInfo(window);
		if(info!=null)
			info.remove(port);
	}

	public static Set getDockingWindows() {
		synchronized(TRACKERS_BY_WINDOW) {
			return new HashSet(TRACKERS_BY_WINDOW.keySet());	
		}
	}
}
