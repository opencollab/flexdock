/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.flexdock.docking.CursorProvider;
import org.flexdock.docking.ScaledInsets;


/**
 * @author Christopher Butler
 */
public class DockableStateManager extends BasicDockableState {
	public static final DockableState DEFAULT_STATE = new DefaultDockableState();
	public static final DockableState GLOBAL_STATE = new BasicDockableState(5);
	private ArrayList stateManagers;
	
	public static class DefaultDockableState extends BasicDockableState {
		public DefaultDockableState() {
			super(5);
		}
		
		public Boolean isDockingEnabled() {
			Boolean obj = super.isDockingEnabled();
			return obj==null? Boolean.TRUE: obj;
		}

		public Boolean isMouseMotionListenersBlockedWhileDragging() {
			Boolean obj = super.isMouseMotionListenersBlockedWhileDragging();
			return obj==null? Boolean.TRUE: obj;
		}
	}
	
	

	public DockableStateManager() {
		super();
		init();
	}

	public DockableStateManager(int initialCapacity) {
		super(initialCapacity);
		init();
	}

	public DockableStateManager(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		init();
	}

	public DockableStateManager(Map t) {
		super(t);
		init();
	}
	
	private void init() {
		stateManagers = new ArrayList(1);
		stateManagers.add(this);
	}
	
	public List getStateManagers() {
		return stateManagers;
	}
	
	
	public CursorProvider getCursorProvider() {
		CursorProvider provider = GLOBAL_STATE.getCursorProvider();
		for(Iterator it=stateManagers.iterator(); provider==null && it.hasNext();) {
			DockableState stateManager = (DockableState)it.next();
			provider = stateManager==this? super.getCursorProvider(): stateManager.getCursorProvider();
		}
		return provider==null? DEFAULT_STATE.getCursorProvider(): provider;
	}

	public String getDockableDesc() {
		String desc = GLOBAL_STATE.getDockableDesc();
		for(Iterator it=stateManagers.iterator(); desc==null && it.hasNext();) {
			DockableState stateManager = (DockableState)it.next();
			desc = stateManager==this? super.getDockableDesc(): stateManager.getDockableDesc();
		}
		return desc==null? DEFAULT_STATE.getDockableDesc(): desc;
	}

	public ScaledInsets getRegionInsets() {
		ScaledInsets insets = GLOBAL_STATE.getRegionInsets();
		for(Iterator it=stateManagers.iterator(); insets==null && it.hasNext();) {
			DockableState stateManager = (DockableState)it.next();
			insets = stateManager==this? super.getRegionInsets(): stateManager.getRegionInsets();
		}
		return insets==null? DEFAULT_STATE.getRegionInsets(): insets;
	}

	public ScaledInsets getSiblingInsets() {
		ScaledInsets insets = GLOBAL_STATE.getSiblingInsets();
		for(Iterator it=stateManagers.iterator(); insets==null && it.hasNext();) {
			DockableState stateManager = (DockableState)it.next();
			insets = stateManager==this? super.getSiblingInsets(): stateManager.getSiblingInsets();
		}
		return insets==null? DEFAULT_STATE.getSiblingInsets(): insets;
	}

	public Boolean isDockingEnabled() {
		Boolean enabled = GLOBAL_STATE.isDockingEnabled();
		for(Iterator it=stateManagers.iterator(); enabled==null && it.hasNext();) {
			DockableState stateManager = (DockableState)it.next();
			enabled = stateManager==this? super.isDockingEnabled(): stateManager.isDockingEnabled();
		}
		return enabled==null? DEFAULT_STATE.isDockingEnabled(): enabled;
	}

	public Boolean isMouseMotionListenersBlockedWhileDragging() {
		Boolean blocked = GLOBAL_STATE.isMouseMotionListenersBlockedWhileDragging();
		for(Iterator it=stateManagers.iterator(); blocked==null && it.hasNext();) {
			DockableState stateManager = (DockableState)it.next();
			blocked = stateManager==this? super.isMouseMotionListenersBlockedWhileDragging(): 
					stateManager.isMouseMotionListenersBlockedWhileDragging();
		}
		return blocked==null? DEFAULT_STATE.isMouseMotionListenersBlockedWhileDragging(): blocked;
	}
}
