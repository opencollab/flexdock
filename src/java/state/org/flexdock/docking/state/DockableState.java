/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.state;

import org.flexdock.docking.CursorProvider;
import org.flexdock.docking.ScaledInsets;

/**
 * @author Christopher Butler
 */
public interface DockableState {
	public static final String CURSOR_PROVIDER = "Dockable.CURSOR_PROVIDER";
	public static final String DESCRIPTION = "Dockable.DESCRIPTION";
	public static final String DOCKING_ENABLED = "Dockable.DOCKING_ENABLED";
	public static final String MOUSE_MOTION_DRAG_BLOCK = "Dockable.MOUSE_MOTION_DRAG_BLOCK";
	public static final String REGION_INSETS = "Dockable.REGION_INSETS";
	public static final String SIBLING_INSETS = "Dockable.SIBLING_INSETS";
	
	public CursorProvider getCursorProvider();

	public String getDockableDesc();
	
	public Boolean isDockingEnabled();

	public Boolean isMouseMotionListenersBlockedWhileDragging();
	
	public ScaledInsets getRegionInsets();

	public ScaledInsets getSiblingInsets();
}
