package org.flexdock.view;

import java.util.Map;

import org.flexdock.docking.props.PropertyManager;
import org.flexdock.docking.props.RootDockableProps;
import org.flexdock.docking.props.ScopedDockableProps;

/**
 * @author Christopher Butler
 */
public class ViewProps extends ScopedDockableProps {
	public static final String ACTIVE_STATE_LOCKED = "View.ACTIVE_STATE_LOCKED";
	
	public ViewProps() {
		super();
		init();
	}

	public ViewProps(int initialCapacity) {
		super(initialCapacity);
		init();
	}
	
	protected void init() {
		constrainRoot(ACTIVE_STATE_LOCKED, Boolean.FALSE);
	}
	
	protected void constrainRoot(Object key, Object value) {
		Map map = getRoot();
		if(map instanceof RootDockableProps) {
			((RootDockableProps)map).constrain(key, value);
		}
	}
	
	public Boolean isActiveStateLocked() {
		return (Boolean)PropertyManager.getProperty(ACTIVE_STATE_LOCKED, this);
	}
	
	public void setActiveStateLocked(boolean locked) {
		put(ACTIVE_STATE_LOCKED, locked);
	}
}
