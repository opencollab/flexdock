/*
 * Created on Apr 5, 2005
 */
package org.flexdock.util;

import java.util.WeakHashMap;

/**
 * @author Christopher Butler
 */
public class ClassMapping {
	private WeakHashMap classes;
	private WeakHashMap instances;
	
	private Class defaultClass;
	private Object defaultInstance;
	
	public ClassMapping(Class defaultClass, Object defaultInstance) {
		this.defaultClass = defaultClass;
		this.defaultInstance = defaultInstance;
		
		classes = new WeakHashMap(4);
		instances = new WeakHashMap(4);
	}
	
	
	public void addClassMapping(Object obj, Class value) {
		Class key = obj==null? null: obj.getClass();
		addClassMapping(key, value);
	}
	
	public Class removeClassMapping(Object obj) {
		Class key = obj==null? null: obj.getClass();
		return removeClassMapping(key);		
	}
	
	public Class getClassMapping(Object obj) {
		Class key = obj==null? null: obj.getClass();
		return getClassMapping(key);
	}
	
	
	public void addClassMapping(Class key, Class value) {
		addClassMapping(key, value, null);
	}
	
	public void addClassMapping(Class key, Class value, Object instance) {
		if(key==null || value==null)
			return;
		
		synchronized(classes) {
			classes.put(key, value);
		}
		
		if(instance!=null) {
			synchronized(instances) {
				instances.put(key, instance);
			}
		}
	}

	public Class removeClassMapping(Class key) {
		if(key==null)
			return null;
		
		Class c = null;
		synchronized(classes) {
			c = (Class)classes.remove(key);
		}
		
		synchronized(instances) {
			instances.remove(key);
		}
		
		return c;
	}

	
	public Class getClassMapping(Class key) {
		if(key==null)
			return defaultClass;
		
		Class value = null;
		
		synchronized(classes) {
			for(Class c=key; c!=null && value==null; c=c.getSuperclass()) {
				value = (Class)classes.get(c);
			}
		}
		
		return value==null? defaultClass: value;
	}
	
	public Object getClassInstance(Class key) {
		if(key==null)
			return defaultInstance;
		
		Object value = null;

		synchronized(instances) {
			for(Class c=key; c!=null && value==null; c=c.getSuperclass()) {
				value = instances.get(c);
			}
		}
		
		return value==null? defaultInstance: value;
	}
}
