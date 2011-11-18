package org.flexdock.view;

import java.util.Map;

import org.flexdock.docking.props.PropertyManager;
import org.flexdock.docking.props.RootDockablePropertySet;
import org.flexdock.docking.props.ScopedDockablePropertySet;

/**
 * @author Christopher Butler
 */
public class ViewProps extends ScopedDockablePropertySet {
    public static final String ACTIVE_STATE_LOCKED = "View.ACTIVE_STATE_LOCKED";

    public ViewProps(View view) {
        super(view);
        init();
    }

    public ViewProps(int initialCapacity, View view) {
        super(initialCapacity, view);
        init();
    }

    protected void init() {
        constrainRoot(ACTIVE_STATE_LOCKED, Boolean.FALSE);
    }

    protected void constrainRoot(Object key, Object value) {
        Map map = getRoot();
        if(map instanceof RootDockablePropertySet) {
            ((RootDockablePropertySet)map).constrain(key, value);
        }
    }

    private View getView() {
        return View.getInstance(getDockingId());
    }

    public Boolean isActiveStateLocked() {
        return (Boolean)PropertyManager.getProperty(ACTIVE_STATE_LOCKED, this);
    }

    public void setActiveStateLocked(boolean locked) {
        put(ACTIVE_STATE_LOCKED, locked);
    }

    public void setActive(boolean active) {
        View view = getView();
        if(view==null) {
            super.setActive(active);
            return;
        }

        if(!view.isActiveStateLocked() && active!=isActive().booleanValue()) {
            super.setActive(active);
            if (view.getTitlebar() != null) {
                view.getTitlebar().repaint();
            }
        }
    }
}
