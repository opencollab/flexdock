/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf;

import java.awt.Color;
import java.awt.Image;
import java.util.HashMap;

import javax.swing.UIManager;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UIResource {
	private HashMap properties;
	
	public UIResource() {
		properties = new HashMap();
	}
	
	public UIResource(int size) {
		properties = new HashMap(size);
	}
	
	void setProperty(String key, Object value) {
		properties.put(key, value);
	}
	
	public Object getProperty(String key) {
		if(key==null)
			return null;
		Object property = properties.get(key);
		return property==null? UIManager.getDefaults().get(key): property;
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
