/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf;

import java.awt.Color;
import java.awt.Image;
import java.util.HashMap;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PropertySet {
	private HashMap properties;
	
	public PropertySet() {
		properties = new HashMap();
	}
	
	public PropertySet(int size) {
		properties = new HashMap(size);
	}

	public void setAll(PropertySet set) {
		if(set!=null)
			properties.putAll(set.properties);
	}
	
	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}
	
	public Object getProperty(String key) {
		return properties.get(key);
	}
	
	public Color getColor(String key) {
		Object property = getProperty(key);
		return property instanceof Color? (Color)property: null;
	}
	
	public Image getImage(String key) {
		Object property = getProperty(key);
		return property instanceof Image? (Image)property: null;
	}
	
	public String getString(String key) {
		Object property = getProperty(key);
		return property instanceof String? (String)property: null;
	}
	
	public int getInt(String key) {
		String string = getString(key);
		if(string==null)
			return 0;
		
		try {
			return Integer.parseInt(string);
		} catch(NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
