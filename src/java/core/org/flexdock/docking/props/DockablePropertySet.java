/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.props;

import javax.swing.Icon;

/**
 * @author Christopher Butler
 */
public interface DockablePropertySet {
	public static final String DESCRIPTION = "Dockable.DESCRIPTION";
	public static final String DOCKING_ENABLED = "Dockable.DOCKING_ENABLED";
	public static final String MOUSE_MOTION_DRAG_BLOCK = "Dockable.MOUSE_MOTION_DRAG_BLOCK";
	public static final String DRAG_THRESHOLD = "Dockable.DRAG_THRESHOLD";
	
	public static final String REGION_SIZE_NORTH = "Dockable.REGION_SIZE_NORTH";
	public static final String SIBLING_SIZE_NORTH = "Dockable.SIBLING_SIZE_NORTH";
	public static final String TERRITORY_BLOCKED_NORTH = "Dockable.TERRITORY_BLOCKED_NORTH";
	
	public static final String REGION_SIZE_SOUTH = "Dockable.REGION_SIZE_SOUTH";
	public static final String SIBLING_SIZE_SOUTH = "Dockable.SIBLING_SIZE_SOUTH";
	public static final String TERRITORY_BLOCKED_SOUTH = "Dockable.TERRITORY_BLOCKED_SOUTH";
	
	public static final String REGION_SIZE_EAST = "Dockable.REGION_SIZE_EAST";
	public static final String SIBLING_SIZE_EAST = "Dockable.SIBLING_SIZE_EAST";
	public static final String TERRITORY_BLOCKED_EAST = "Dockable.TERRITORY_BLOCKED_EAST";
	
	public static final String REGION_SIZE_WEST = "Dockable.REGION_SIZE_WEST";
	public static final String SIBLING_SIZE_WEST = "Dockable.SIBLING_SIZE_WEST";
	public static final String TERRITORY_BLOCKED_WEST = "Dockable.TERRITORY_BLOCKED_WEST";

	public static final String TERRITORY_BLOCKED_CENTER = "Dockable.TERRITORY_BLOCKED_CENTER";
	public static final String DOCKBAR_ICON = "Dockable.DOCKBAR_ICON";
	public static final String PREVIEW_SIZE = "Dockable.PREVIEW_SIZE";



	public String getDockableDesc();
	
	public Boolean isDockingEnabled();

	public Boolean isMouseMotionListenersBlockedWhileDragging();
	
	public Float getRegionInset(String region);
	
	public Float getSiblingSize(String region);
	
	public Boolean isTerritoryBlocked(String region);
	
	public Float getDragThreshold();
	
	public Icon getDockbarIcon();
	
	public Float getPreviewSize();

	
	
	
	
	
	
	

	public void setDockableDesc(String desc);
	
	public void setDockingEnabled(boolean enabled);

	public void setMouseMotionListenersBlockedWhileDragging(boolean blocked);
	
	public void setRegionInset(String region, float inset);

	public void setSiblingSize(String region, float size);
	
	public void setTerritoryBlocked(String region, boolean blocked);
	
	public void setDragTheshold(float threshold);
	
	public void setDockbarIcon(Icon icon);
	
	public void setPreviewSize(float size);

}
