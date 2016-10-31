/*
 * Created on Mar 22, 2005
 */
package org.flexdock.docking.props;

import java.util.HashSet;

import javax.swing.JTabbedPane;

import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.defaults.DefaultRegionChecker;

/**
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class RootDockingPortPropertySet extends BasicDockingPortPropertySet {
    private static final RegionChecker DEFAULT_REGION_CHECKER = new DefaultRegionChecker();
    private static final Integer DEFAULT_TAB_PLACEMENT = new Integer(JTabbedPane.BOTTOM);
    private static final Float DEFAULT_REGION_INSET = new Float(RegionChecker.DEFAULT_REGION_SIZE);

    private HashSet constraints;

    public RootDockingPortPropertySet() {
        super(5);
        constraints = new HashSet(5);

        initConstraint(REGION_CHECKER, DEFAULT_REGION_CHECKER);
        initConstraint(SINGLE_TABS, Boolean.FALSE);
        initConstraint(TAB_PLACEMENT, DEFAULT_TAB_PLACEMENT);

        initConstraint(REGION_SIZE_NORTH, DEFAULT_REGION_INSET);
        initConstraint(REGION_SIZE_SOUTH, DEFAULT_REGION_INSET);
        initConstraint(REGION_SIZE_EAST, DEFAULT_REGION_INSET);
        initConstraint(REGION_SIZE_WEST, DEFAULT_REGION_INSET);
    }

    private void initConstraint(Object key, Object value) {
        put(key, value);
        constraints.add(key);
    }

    public synchronized Object remove(Object key) {
        return constraints.contains(key)? null: super.remove(key);
    }
}
