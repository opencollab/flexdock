/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.props;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.flexdock.docking.CursorProvider;


/**
 * @author Christopher Butler
 */
public class ScopedDockableProps extends BasicDockableProps implements ScopedMap {
	public static final RootDockableProps ROOT_PROPS = new RootDockableProps();
	public static final List DEFAULTS = new ArrayList(0);
	public static final List GLOBALS = new ArrayList(0);
	private ArrayList locals;

	public ScopedDockableProps() {
		this(6);
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
		locals = new ArrayList(1);
		locals.add(this);
	}
	
	public List getLocals() {
		return locals;
	}
	
	public List getDefaults() {
		return DEFAULTS;
	}
	
	public List getGlobals() {
		return GLOBALS;
	}
	
	public Map getRoot() {
		return ROOT_PROPS;
	}
	
	
	public CursorProvider getCursorProvider() {
		return (CursorProvider)PropertyManager.getProperty(CURSOR_PROVIDER, this);
	}

	public String getDockableDesc() {
		return (String)PropertyManager.getProperty(DESCRIPTION, this);
	}

	public Boolean isDockingEnabled() {
		return (Boolean)PropertyManager.getProperty(DOCKING_ENABLED, this);
	}

	public Boolean isMouseMotionListenersBlockedWhileDragging() {
		return (Boolean)PropertyManager.getProperty(MOUSE_MOTION_DRAG_BLOCK, this);
	}
	
	
	public Float getRegionInset(String region) {
		String key = getRegionInsetKey(region);
		return key==null? null: (Float)PropertyManager.getProperty(key, this);
	}

	public Float getSiblingSize(String region) {
		String key = getSiblingSizeKey(region);
		return key==null? null: (Float)PropertyManager.getProperty(key, this);
	}
	
	public Boolean isTerritoryBlocked(String region) {
		String key = getTerritoryBlockedKey(region);
		return key==null? null: (Boolean)PropertyManager.getProperty(key, this);
	}
	
	public Float getDragThreshold() {
		return (Float)PropertyManager.getProperty(DRAG_THRESHOLD, this);
	}

}
