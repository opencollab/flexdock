/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.icons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.flexdock.windowing.plaf.Configurator;
import org.flexdock.windowing.plaf.PropertySet;
import org.flexdock.windowing.plaf.XMLConstants;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IconResourceFactory implements XMLConstants {
	public static final String ICON_RESOURCE_KEY = "icon-resource";
	public static final String ICON_MAP_KEY = "icon-map";
	
	public static final String DEFAULT = "default";
	public static final String HOVER = "hover";
	public static final String ACTIVE = "active";
	public static final String ACTIVE_HOVER = "active.hover";
	public static final String PRESSED = "pressed";
	private static final HashMap RESOURCE_CACHE = new HashMap();
	private static final HashMap RESOURCE_MAP_CACHE = new HashMap();

	public static IconMap getIconMap(String name) {
		if(Configurator.isNull(name))
			return null;
		
		IconMap map = (IconMap)RESOURCE_MAP_CACHE.get(name);
		if(map==null) {
			map = loadIconMap(name);
			if(map!=null) {
				synchronized(RESOURCE_MAP_CACHE) {
					RESOURCE_MAP_CACHE.put(name, map);		
				}
			}
		}
		return map;
	}
	
	public static IconResource getResource(String name) {
		if(Configurator.isNull(name))
			return null;
		
		IconResource icons = getCachedResource(name);
		if(icons==null) {
			icons = loadIcons(name);
			cacheResource(name, icons);
		}
		return icons;
	}
	
	private static IconResource getCachedResource(String name) {
		return (IconResource)RESOURCE_CACHE.get(name);
	}
	
	private static void cacheResources(IconMap map) {
		if(map!=null) {
			for(Iterator it=map.keySet().iterator(); it.hasNext();) {
				String key = (String)it.next();
				IconResource resource = map.getIcons(key);
				cacheResource(key, resource);
			}
		}
	}
	private static void cacheResource(String name, IconResource icons) {
		if(icons!=null) {
			synchronized(RESOURCE_CACHE) {
				RESOURCE_CACHE.put(name, icons);		
			}
		}
	}
	
	private static IconResource loadIcons(String name) {
		PropertySet properties = Configurator.getProperties(name, ICON_RESOURCE_KEY);
		return createResource(properties);
	}
	
	private static IconResource createResource(PropertySet properties) {
		IconResource icons = new IconResource();
		icons.setIcon(properties.getIcon(DEFAULT));
		icons.setIconHover(properties.getIcon(HOVER));
		icons.setIconActive(properties.getIcon(ACTIVE));
		icons.setIconActiveHover(properties.getIcon(ACTIVE_HOVER));
		icons.setIconPressed(properties.getIcon(PRESSED));
		
		return icons;		
	}
	
	private static IconMap loadIconMap(String name) {
		PropertySet icons = Configurator.getProperties(name, ICON_RESOURCE_KEY);
		IconMap map = new IconMap();
		
		ArrayList notCached = new ArrayList();
		for(Iterator it=icons.keys(); it.hasNext();) {
			String iconName = (String)it.next();
			// load all the cached icon resources
			IconResource icon = getCachedResource(iconName);
			if(icon==null) {
				// track the non-cached icons
				notCached.add(iconName);
			}
			else {
				map.addIcons(iconName, icon);
			}
		}
		
		// load and cache all the non-cached icons
		if(notCached.size()>0) {
			String[] iconNames = (String[])notCached.toArray(new String[0]);
			IconMap loadedMap = loadResources(iconNames);
			map.addAll(loadedMap);
			cacheResources(loadedMap);
		}
		
		return map;
	}
	
	private static IconMap loadResources(String[] names) {
		PropertySet[] resourceData = Configurator.getProperties(names, ICON_RESOURCE_KEY);
		
		IconMap map = new IconMap();
		for(int i=0; i<resourceData.length; i++) {
			IconResource resource = createResource(resourceData[i]);
			map.addIcons(resourceData[i].getName(), resource);
		}
		
		return map;
	}
}
