/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.state;

import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.ScaledInsets;

/**
 * @author Christopher Butler
 */
public interface DockingPortState {
	public static final String REGION_CHECKER = "DockingPort.REGION_CHECKER";
	public static final String SINGLE_TABS = "DockingPort.SINGLE_TABS";
	public static final String TAB_PLACEMENT = "DockingPort.TAB_PLACEMENT";
	public static final String REGION_INSETS = "DockingPort.REGION_INSETS";
	
	public RegionChecker getRegionChecker();
	
	public Boolean isSingleTabsAllowed();
	
	public Integer getTabPlacement();
	
	public ScaledInsets getRegionInsets();
}
