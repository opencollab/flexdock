/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.props;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.flexdock.docking.RegionChecker;

/**
 * @author Christopher Butler
 */
public class ScopedDockingPortProps extends BasicDockingPortProps implements ScopedMap {
	public static final RootDockingPortProps ROOT_PROPS = new RootDockingPortProps();
	public static final List DEFAULTS = new ArrayList(0);
	public static final List GLOBALS = new ArrayList(0);
	
	private ArrayList locals;
	
	public ScopedDockingPortProps() {
		super();
		init();
	}

	public ScopedDockingPortProps(int initialCapacity) {
		super(initialCapacity);
		init();
	}

	public ScopedDockingPortProps(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		init();
	}

	public ScopedDockingPortProps(Map t) {
		super(t);
		init();
	}
	
	protected void init() {
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

	public RegionChecker getRegionChecker() {
		return (RegionChecker)PropertyManager.getProperty(REGION_CHECKER, this);
	}

	public Float getRegionInset(String region) {
		String key = getRegionInsetKey(region);
		return key==null? null: (Float)PropertyManager.getProperty(key, this);
	}

	public Integer getTabPlacement() {
		return (Integer)PropertyManager.getProperty(TAB_PLACEMENT, this);
	}

	public Boolean isSingleTabsAllowed() {
		return (Boolean)PropertyManager.getProperty(SINGLE_TABS, this);
	}
}
