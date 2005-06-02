/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.props;

import java.util.Map;

import javax.swing.Icon;

import org.flexdock.docking.DockingPort;
import org.flexdock.util.TypedHashtable;

/**
 * @author Christopher Butler
 */
public class BasicDockableProps extends TypedHashtable implements DockableProps {

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

	public static String getSiblingSizeKey(String region) {
		if(DockingPort.NORTH_REGION.equals(region))
			return SIBLING_SIZE_NORTH;
		if(DockingPort.SOUTH_REGION.equals(region))
			return SIBLING_SIZE_SOUTH;
		if(DockingPort.EAST_REGION.equals(region))
			return SIBLING_SIZE_EAST;
		if(DockingPort.WEST_REGION.equals(region))
			return SIBLING_SIZE_WEST;
		return null;
	}
	
	public static String getTerritoryBlockedKey(String region) {
		if(DockingPort.NORTH_REGION.equals(region))
			return TERRITORY_BLOCKED_NORTH;
		if(DockingPort.SOUTH_REGION.equals(region))
			return TERRITORY_BLOCKED_SOUTH;
		if(DockingPort.EAST_REGION.equals(region))
			return TERRITORY_BLOCKED_EAST;
		if(DockingPort.WEST_REGION.equals(region))
			return TERRITORY_BLOCKED_WEST;
		if(DockingPort.CENTER_REGION.equals(region))
			return TERRITORY_BLOCKED_CENTER;
		return null;
	}
	
	public BasicDockableProps() {
		super();
	}

	public BasicDockableProps(int initialCapacity) {
		super(initialCapacity);
	}

	public BasicDockableProps(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public BasicDockableProps(Map t) {
		super(t);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Icon getDockbarIcon() {
		return (Icon)get(DOCKBAR_ICON);
	}

	public String getDockableDesc() {
		return (String)get(DESCRIPTION);
	}
	
	public Boolean isDockingEnabled() {
		return getBoolean(DOCKING_ENABLED);
	}

	public Boolean isMouseMotionListenersBlockedWhileDragging() {
		return getBoolean(MOUSE_MOTION_DRAG_BLOCK);
	}
	

	public Float getRegionInset(String region) {
		String key = getRegionInsetKey(region);
		return key==null? null: (Float)get(key);
	}

	public Float getSiblingSize(String region) {
		String key = getSiblingSizeKey(region);
		return key==null? null: (Float)get(key);
	}
	
	public Boolean isTerritoryBlocked(String region) {
		String key = getTerritoryBlockedKey(region);
		return key==null? null: (Boolean)get(key);
	}
	
	public Float getDragThreshold() {
		return getFloat(DRAG_THRESHOLD);
	}
	
	public Float getPreviewSize() {
		return getFloat(PREVIEW_SIZE);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	public void setDockbarIcon(Icon icon) {
		put(DOCKBAR_ICON, icon);
	}
	
	public void setDockableDesc(String dockableDesc) {
		put(DESCRIPTION, dockableDesc);
	}
	
	public void setDockingEnabled(boolean enabled) {
		put(DOCKING_ENABLED, enabled);
	}
	
	public void setMouseMotionListenersBlockedWhileDragging(boolean blocked) {
		put(MOUSE_MOTION_DRAG_BLOCK, blocked);
	}
	
	public void setRegionInset(String region, float inset) {
		String key = getRegionInsetKey(region);
		if(key!=null) {
			Float f = new Float(inset);
			put(key, f);
		}
	}

	public void setSiblingSize(String region, float size) {
		String key = getSiblingSizeKey(region);
		if(key!=null) {
			Float f = new Float(size);
			put(key, f);
		}
	}
	
	public void setTerritoryBlocked(String region, boolean blocked) {
		String key = getTerritoryBlockedKey(region);
		if(key!=null) {
			Boolean bool = blocked? Boolean.TRUE: Boolean.FALSE;
			put(key, bool);
		}
	}


	public void setDragTheshold(float threshold) {
		threshold = Math.max(threshold, 0);
		put(DRAG_THRESHOLD, threshold);
	}
	
	public void setPreviewSize(float previewSize) {
		previewSize = Math.max(previewSize, 0f);
		previewSize = Math.min(previewSize, 1f);
		put(PREVIEW_SIZE, previewSize);
	}

}
