/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.state;

import java.util.Map;

import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.ScaledInsets;
import org.flexdock.util.TypedHashtable;

/**
 * @author Christopher Butler
 */
public class BasicDockingPortState extends TypedHashtable implements DockingPortState {

	
	public BasicDockingPortState() {
		super();
	}

	public BasicDockingPortState(int initialCapacity) {
		super(initialCapacity);
	}

	public BasicDockingPortState(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public BasicDockingPortState(Map t) {
		super(t);
	}

	
	
	
	
	
	
	public RegionChecker getRegionChecker() {
		return (RegionChecker)get(REGION_CHECKER);
	}
	
	public Boolean isSingleTabsAllowed() {
		return getBoolean(SINGLE_TABS);
	}
	
	public Integer getTabPlacement() {
		return getInt(TAB_PLACEMENT);
	}
	
	public ScaledInsets getRegionInsets() {
		return (ScaledInsets)get(REGION_INSETS);
	}
	
	
	
	public void setRegionChecker(RegionChecker checker) {
		put(REGION_CHECKER, checker);
	}
	
	public void setSingleTabsAllowed(boolean allowed) {
		put(SINGLE_TABS, allowed);
	}
	
	public void setTabPlacement(int placement) {
		put(TAB_PLACEMENT, placement);
	}
	
	public void setRegionInsets(ScaledInsets insets) {
		put(REGION_INSETS, insets);
	}
}
