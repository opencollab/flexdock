/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.props;

import java.util.Map;

import org.flexdock.docking.DockingPort;
import org.flexdock.docking.RegionChecker;
import org.flexdock.util.TypedHashtable;

/**
 * @author Christopher Butler
 */
public class BasicDockingPortProps extends TypedHashtable implements DockingPortProps {

	public static String getRegionInsetKey(String region) {
		if(DockingPort.NORTH_REGION.equals(region))
			return REGION_SIZE_NORTH;
		if(DockingPort.SOUTH_REGION.equals(region))
			return REGION_SIZE_SOUTH;
		if(DockingPort.EAST_REGION.equals(region))
			return REGION_SIZE_EAST;
		if(DockingPort.WEST_REGION.equals(region))
			return REGION_SIZE_WEST;
		return null;
	}
	
	public BasicDockingPortProps() {
		super();
	}

	public BasicDockingPortProps(int initialCapacity) {
		super(initialCapacity);
	}

	public BasicDockingPortProps(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public BasicDockingPortProps(Map t) {
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
	
	public Float getRegionInset(String region) {
		String key = getRegionInsetKey(region);
		return key==null? null: (Float)get(key);
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
	
	public void setRegionInset(String region, float inset) {
		String key = getRegionInsetKey(region);
		if(key!=null) {
			put(key, new Float(inset));			
		}
	}
}
