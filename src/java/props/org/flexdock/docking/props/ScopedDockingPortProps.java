/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.props;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JTabbedPane;

import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.ScaledInsets;
import org.flexdock.docking.defaults.DefaultRegionChecker;

/**
 * @author Christopher Butler
 */
public class ScopedDockingPortProps extends BasicDockingPortProps implements ScopedMap {
	public static final DefaultDockingPortProps DEFAULT_STATE = new DefaultDockingPortProps();
	public static final BasicDockingPortProps GLOBAL_STATE = new BasicDockingPortProps(5);
	
	private ArrayList propertyMaps;

	public static class DefaultDockingPortProps extends BasicDockingPortProps {
		private static final RegionChecker DEFAULT_REGION_CHECKER = new DefaultRegionChecker();
		private static final Integer DEFAULT_TAB_PLACEMENT = new Integer(JTabbedPane.BOTTOM);
		
		public DefaultDockingPortProps() {
			super(5);
		}
		
		public RegionChecker getRegionChecker() {
			RegionChecker obj = super.getRegionChecker();
			return obj==null? DEFAULT_REGION_CHECKER: obj;
		}

		public Integer getTabPlacement() {
			Integer obj = super.getTabPlacement();
			return obj==null? DEFAULT_TAB_PLACEMENT: obj;
		}

		public Boolean isSingleTabsAllowed() {
			Boolean obj = super.isSingleTabsAllowed();
			return obj==null? Boolean.FALSE: obj;
		}
	}
	
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

	public RegionChecker getRegionChecker() {
		return (RegionChecker)PropertyManager.getProperty(REGION_CHECKER, this);
	}

	public ScaledInsets getRegionInsets() {
		return (ScaledInsets)PropertyManager.getProperty(REGION_INSETS, this);
	}

	public Integer getTabPlacement() {
		return (Integer)PropertyManager.getProperty(TAB_PLACEMENT, this);
	}

	public Boolean isSingleTabsAllowed() {
		return (Boolean)PropertyManager.getProperty(SINGLE_TABS, this);
	}
}
