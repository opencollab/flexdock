/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.plaf.icons;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IconMap extends HashMap {
	public Object put(Object key, Object value) {
		// do nothing
		return null;
	}

	public void putAll(Map m) {
		// do nothing
	}
	
	public void addAll(IconMap map) {
		if(map!=null)
			super.putAll(map);
	}
	
	public IconResource getIcons(String key) {
		return key==null? null: (IconResource)get(key);
	}
	
	public void addIcons(String key, IconResource icons) {
		if(key!=null && icons!=null)
			super.put(key, icons);
	}
}
