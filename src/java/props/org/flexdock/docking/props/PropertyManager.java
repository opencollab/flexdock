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
	public static final String DOCKABLE_PROPERTIES_KEY = DockableProps.class.getName();
	public static final String DOCKINGPORT_PROPERTIES_KEY = DockingPortProps.class.getName();
	private static final ClassMapping DOCKABLE_PROPS_MAPPING = new ClassMapping(ScopedDockableProps.class, null);
	
	public static DockingPortProps getDockingPortRoot() {
		return ScopedDockingPortProps.ROOT_PROPS;
	}
	
	public static DockableProps getDockableRoot() {
		return ScopedDockableProps.ROOT_PROPS;
	}
	
	
	public static void setDockablePropertyType(Class dockable, Class propType) {
		if(dockable==null || propType==null)
			return;
		
		if(!Dockable.class.isAssignableFrom(dockable) || !DockableProps.class.isAssignableFrom(propType))
			return;

		DOCKABLE_PROPS_MAPPING.addClassMapping(dockable, propType);
	}
	
	
	
	
	public static DockableProps getDockableProps(Dockable dockable) {
		if(dockable==null)
			return null;
		
		Object obj = dockable.getClientProperty(DOCKABLE_PROPERTIES_KEY);
		if(!(obj instanceof DockableProps)) {
			obj = createDockableProps(dockable);
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
	
	private static DockableProps createDockableProps(Dockable d) {
		Class key = d.getClass();
		Class c = DOCKABLE_PROPS_MAPPING.getClassMapping(key);
		return (DockableProps)Utilities.createInstance(c.getName());
	}

}
