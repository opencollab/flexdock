/*
 * Created on Mar 16, 2005
 */
package org.flexdock.docking.props;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;
import org.flexdock.util.ClassMapping;
import org.flexdock.util.Utilities;

/**
 * @author Christopher Butler
 */
public class PropertyManager {
	public static final String DOCKABLE_PROPERTIES_KEY = DockablePropertySet.class.getName();
	public static final String DOCKINGPORT_PROPERTIES_KEY = DockingPortPropertySet.class.getName();
	private static final ClassMapping DOCKABLE_PROPS_MAPPING = new ClassMapping(ScopedDockablePropertySet.class, null);
	
	public static DockingPortPropertySet getDockingPortRoot() {
		return ScopedDockingPortPropertySet.ROOT_PROPS;
	}
	
	public static DockablePropertySet getDockableRoot() {
		return ScopedDockablePropertySet.ROOT_PROPS;
	}
	
	
	public static void setDockablePropertyType(Class dockable, Class propType) {
		if(dockable==null || propType==null)
			return;
		
		if(!Dockable.class.isAssignableFrom(dockable) || !DockablePropertySet.class.isAssignableFrom(propType))
			return;

		DOCKABLE_PROPS_MAPPING.addClassMapping(dockable, propType);
	}
	
	
	
	
	public static DockablePropertySet getDockablePropertySet(Dockable dockable) {
		if(dockable==null)
			return null;
		
		Object obj = dockable.getClientProperty(DOCKABLE_PROPERTIES_KEY);
		if(!(obj instanceof DockablePropertySet)) {
			obj = createDockablePropertySet(dockable);
			dockable.putClientProperty(DOCKABLE_PROPERTIES_KEY, obj);
		}
		return (DockablePropertySet)obj;
	}

	public static DockingPortPropertySet getDockingPortPropertySet(DockingPort port) {
		if(port==null)
			return null;
		
		Object obj = port.getClientProperty(DOCKINGPORT_PROPERTIES_KEY);
		if(!(obj instanceof DockingPortPropertySet)) {
			obj = new ScopedDockingPortPropertySet(4);
			port.putClientProperty(DOCKINGPORT_PROPERTIES_KEY, obj);
		}
		return (DockingPortPropertySet)obj;
	}
	
	
	public static Object getProperty(Object key, ScopedMap map) {
		if(key==null || map==null)
			return null;
		
		// first, check the global property list
		Object value = getProperty(key, map.getGlobals());
		// if not in the global list, check the locals
		if(value==null)
			value = getProperty(key, map.getLocals());
		// if not in the local list, check the defaults
		if(value==null)
			value = getProperty(key, map.getLocals());
		// if not in the default list, check the root
		if(value==null)
			value = getProperty(key, map.getRoot());
		return value;
	}

	private static Object getProperty(Object key, List maps) {
		if(maps==null)
			return null;
		
		for(Iterator it=maps.iterator(); it.hasNext();) {
			Object map = it.next();
			Object value = getProperty(key, map);
			if(value!=null)
				return value;
		}
		return null;
	}
	
	private static Object getProperty(Object key, Object map) {
		if(map instanceof Map) {
			return ((Map)map).get(key);
		}
		return null;
	}
	
	private static DockablePropertySet createDockablePropertySet(Dockable d) {
		Class key = d.getClass();
		Class c = DOCKABLE_PROPS_MAPPING.getClassMapping(key);
		return (DockablePropertySet)Utilities.createInstance(c.getName());
	}

}
