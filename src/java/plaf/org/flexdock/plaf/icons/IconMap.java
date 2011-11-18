/*
 * Created on Mar 1, 2005
 */
package org.flexdock.plaf.icons;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christopher Butler
 *
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
