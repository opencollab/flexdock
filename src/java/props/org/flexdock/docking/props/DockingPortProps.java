/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.props;

import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.ScaledInsets;

/**
 * @author Christopher Butler
 */
public interface DockingPortProps {
	public static final String REGION_CHECKER = "DockingPort.REGION_CHECKER";
	public static final String SINGLE_TABS = "DockingPort.SINGLE_TABS";
	public static final String TAB_PLACEMENT = "DockingPort.TAB_PLACEMENT";
	public static final String REGION_INSETS = "DockingPort.REGION_INSETS";
	
	public RegionChecker getRegionChecker();
	
	public Boolean isSingleTabsAllowed();
	
	public Integer getTabPlacement();
	
	public ScaledInsets getRegionInsets();
	
	
	
	
	public void setRegionChecker(RegionChecker checker);
	
	public void setSingleTabsAllowed(boolean allowed);
	
	public void setTabPlacement(int placement);
	
	public void setRegionInsets(ScaledInsets insets);
}
