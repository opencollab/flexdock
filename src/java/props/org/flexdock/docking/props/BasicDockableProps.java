/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.props;

import java.util.Map;

import org.flexdock.docking.CursorProvider;
import org.flexdock.docking.ScaledInsets;
import org.flexdock.util.TypedHashtable;

/**
 * @author Christopher Butler
 */
public class BasicDockableProps extends TypedHashtable implements DockableProps {

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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public CursorProvider getCursorProvider() {
		return (CursorProvider)get(CURSOR_PROVIDER);
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
	

	public ScaledInsets getRegionInsets() {
		return (ScaledInsets)get(REGION_INSETS);
	}

	public ScaledInsets getSiblingInsets() {
		return (ScaledInsets)get(SIBLING_INSETS);
	}
	
	
	
	
	public void setCursorProvider(CursorProvider cursorProvider) {
		put(CURSOR_PROVIDER, cursorProvider);
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
	
	public void setRegionInsets(ScaledInsets insets) {
		put(REGION_INSETS, insets);
	}

	public void setSiblingInsets(ScaledInsets insets) {
		put(SIBLING_INSETS, insets);
	}
	
	
}
