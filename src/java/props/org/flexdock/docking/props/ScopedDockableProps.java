/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.props;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.flexdock.docking.CursorProvider;
import org.flexdock.docking.ScaledInsets;


/**
 * @author Christopher Butler
 */
public class ScopedDockableProps extends BasicDockableProps implements ScopedMap {
	public static final DefaultDockableProps DEFAULT_STATE = new DefaultDockableProps();
	public static final BasicDockableProps GLOBAL_STATE = new BasicDockableProps(5);
	private ArrayList propertyMaps;
	
	public static class DefaultDockableProps extends BasicDockableProps {
		public DefaultDockableProps() {
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
	
	

	public ScopedDockableProps() {
		super();
		init();
	}

	public ScopedDockableProps(int initialCapacity) {
		super(initialCapacity);
		init();
	}

	public ScopedDockableProps(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		init();
	}

	public ScopedDockableProps(Map t) {
		super(t);
		init();
	}
	
	private void init() {
		propertyMaps = new ArrayList(1);
		propertyMaps.add(this);
	}
	
	public List getPropertyMaps() {
		return propertyMaps;
	}
	
	public Map getDefaults() {
		return DEFAULT_STATE;
	}
	
	public Map getGlobals() {
		return GLOBAL_STATE;
	}
	
	
	public CursorProvider getCursorProvider() {
		return (CursorProvider)PropertyManager.getProperty(CURSOR_PROVIDER, this);
	}

	public String getDockableDesc() {
		return (String)PropertyManager.getProperty(DESCRIPTION, this);
	}

	public ScaledInsets getRegionInsets() {
		return (ScaledInsets)PropertyManager.getProperty(REGION_INSETS, this);
	}

	public ScaledInsets getSiblingInsets() {
		return (ScaledInsets)PropertyManager.getProperty(SIBLING_INSETS, this);
	}

	public Boolean isDockingEnabled() {
		return (Boolean)PropertyManager.getProperty(DOCKING_ENABLED, this);
	}

	public Boolean isMouseMotionListenersBlockedWhileDragging() {
		return (Boolean)PropertyManager.getProperty(MOUSE_MOTION_DRAG_BLOCK, this);
	}
}
