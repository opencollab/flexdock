/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.props;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;

/**
 * @author Christopher Butler
 */
public class PropertyManager {
	public static final String DOCKABLE_PROPERTIES_KEY = DockableProps.class.getName();
	public static final String DOCKINGPORT_PROPERTIES_KEY = DockingPortProps.class.getName();

	public static DockableProps getDockableProps(Dockable dockable) {
		if(dockable==null)
			return null;
		
		Object obj = dockable.getClientProperty(DOCKABLE_PROPERTIES_KEY);
		if(!(obj instanceof DockableProps)) {
			obj = new ScopedDockableProps(6);
			dockable.putClientProperty(DOCKABLE_PROPERTIES_KEY, obj);
		}
		return (DockableProps)obj;
	}

	public static DockingPortProps getDockingPortProps(DockingPort port) {
		if(port==null)
			return null;
		
		Object obj = port.getClientProperty(DOCKINGPORT_PROPERTIES_KEY);
		if(!(obj instanceof DockingPortProps)) {
			obj = new ScopedDockingPortProps(4);
			port.putClientProperty(DOCKINGPORT_PROPERTIES_KEY, obj);
		}
		return (DockingPortProps)obj;
	}
	
	
	public static Object getProperty(Object key, ScopedMap map) {
		if(key==null || map==null)
			return null;
		
		// first, check the global property set
		Map globals = map.getGlobals();
		Object value = globals==null? null: globals.get(key);
		if(value!=null)
			return value;
		
		// not in the globals, so iterate through all the user-supplied properties
		List list = map.getPropertyMaps();
		if(list!=null) {
			for(Iterator it=list.iterator(); it.hasNext();) {
				Object obj = it.next();
				if(obj instanceof Map) {
					Map props = (Map)obj;
					value = props.get(key);
					// if we found a user-defined property, return it
					if(value!=null)
						return value;
				}
			}
		}
		
		// not in globals or user-defined properties.  return the default
		Map defaults = map.getDefaults();
		return defaults==null? null: defaults.get(key);
	}

	

}
