/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JTabbedPane;

import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.ScaledInsets;
import org.flexdock.docking.defaults.DefaultRegionChecker;

/**
 * @author Christopher Butler
 */
public class DockingPortStateManager extends BasicDockingPortState {
	public static final DockingPortState DEFAULT_STATE = new DefaultDockingPortState();
	public static final DockingPortState GLOBAL_STATE = new BasicDockingPortState(5);
	
	private ArrayList stateManagers;

	public static class DefaultDockingPortState extends BasicDockingPortState {
		private static final RegionChecker DEFAULT_REGION_CHECKER = new DefaultRegionChecker();
		private static final Integer DEFAULT_TAB_PLACEMENT = new Integer(JTabbedPane.BOTTOM);
		
		public DefaultDockingPortState() {
			super(5);
		}
		
		public RegionChecker getRegionChecker() {
			RegionChecker obj = super.getRegionChecker();
			return obj==null? DEFAULT_REGION_CHECKER: obj;
		}

		public Integer getTabPlacement() {
			Integer obj = super.getTabPlacement();
			return obj==null? DEFAULT_TAB_PLACEMENT: obj;
		}

		public Boolean isSingleTabsAllowed() {
			Boolean obj = super.isSingleTabsAllowed();
			return obj==null? Boolean.FALSE: obj;
		}
	}
	
	public DockingPortStateManager() {
		super();
		init();
	}

	public DockingPortStateManager(int initialCapacity) {
		super(initialCapacity);
		init();
	}

	public DockingPortStateManager(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		init();
	}

	public DockingPortStateManager(Map t) {
		super(t);
		init();
	}
	
	protected void init() {
		stateManagers = new ArrayList(1);
		stateManagers.add(this);
	}
	
	public List getStateManagers() {
		return stateManagers;
	}
	

	public RegionChecker getRegionChecker() {
		RegionChecker checker = GLOBAL_STATE.getRegionChecker();
		for(Iterator it=stateManagers.iterator(); checker==null && it.hasNext();) {
			DockingPortState stateManager = (DockingPortState)it.next();
			checker = stateManager==this? super.getRegionChecker(): stateManager.getRegionChecker();
		}
		return checker==null? DEFAULT_STATE.getRegionChecker(): checker;
	}

	public ScaledInsets getRegionInsets() {
		ScaledInsets insets = GLOBAL_STATE.getRegionInsets();
		for(Iterator it=stateManagers.iterator(); insets==null && it.hasNext();) {
			DockingPortState stateManager = (DockingPortState)it.next();
			insets = stateManager==this? super.getRegionInsets(): stateManager.getRegionInsets();
		}
		return insets==null? DEFAULT_STATE.getRegionInsets(): insets;
	}

	public Integer getTabPlacement() {
		Integer placement = GLOBAL_STATE.getTabPlacement();
		for(Iterator it=stateManagers.iterator(); placement==null && it.hasNext();) {
			DockingPortState stateManager = (DockingPortState)it.next();
			placement = stateManager==this? super.getTabPlacement(): stateManager.getTabPlacement();
		}
		return placement==null? DEFAULT_STATE.getTabPlacement(): placement;
	}

	public Boolean isSingleTabsAllowed() {
		Boolean allowed = GLOBAL_STATE.isSingleTabsAllowed();
		for(Iterator it=stateManagers.iterator(); allowed==null && it.hasNext();) {
			DockingPortState stateManager = (DockingPortState)it.next();
			allowed = stateManager==this? super.isSingleTabsAllowed(): stateManager.isSingleTabsAllowed();
		}
		return allowed==null? DEFAULT_STATE.isSingleTabsAllowed(): allowed;
	}
}
