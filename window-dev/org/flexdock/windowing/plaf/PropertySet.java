/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.border.Border;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PropertySet {
	private HashMap properties;
	private String name;
	
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
	
	public Icon getIcon(String key) {
		Object property = getProperty(key);
		return property instanceof Icon? (Icon)property: null;
	}
	
	public String getString(String key) {
		Object property = getProperty(key);
		return property instanceof String? (String)property: null;
	}
	
	public Border getBorder(String key) {
		Object property = getProperty(key);
		return property instanceof Border? (Border)property: null;		
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
	
	public Integer getInteger(String key) {
		String string = getString(key);
		if(string==null)
			return null;
		
		try {
			return new Integer(string);
		} catch(NumberFormatException e) {
			return null;
		}
	}
	
	public Iterator keys() {
		return properties.keySet().iterator();
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public int size() {
		return properties.size();
	}
	
	public List getNumericKeys() {
		return getNumericKeys(false);
	}
	
	public List getNumericKeys(boolean sort) {
		ArrayList list = new ArrayList(size());
		for(Iterator it=properties.keySet().iterator(); it.hasNext();) {
			String key = (String)it.next();
			if(isNumeric(key)) {
				list.add(key);
			}
		}
		
		if(sort) {
			Collections.sort(list, new NumericStringSort());
		}

		return list;
	}
	
	private boolean isNumeric(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	
	public Class toClass(String key) throws ClassNotFoundException {
		String type = getString(key);
		if(type==null)
			return null;

		if("int".equals(type))
			return int.class;
		if("long".equals(type))
			return long.class;
		if("boolean".equals(type))
			return boolean.class;
		if("float".equals(type))
			return float.class;
		if("double".equals(type))
			return double.class;
		if("byte".equals(type))
			return byte.class;
		if("short".equals(type))
			return short.class;
		
		return Class.forName(type);
	}
	
	private static class NumericStringSort implements Comparator {

		public int compare(Object o1, Object o2) {
			int i1 = Integer.parseInt((String)o1);
			int i2 = Integer.parseInt((String)o2);
			return i1-i2;
		}
	}
}
