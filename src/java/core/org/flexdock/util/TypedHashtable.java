/*
 * Created on Mar 16, 2005
 */
package org.flexdock.util;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author Christopher Butler
 */
public class TypedHashtable extends Hashtable {
	
	public TypedHashtable() {
		super();
	}

	public TypedHashtable(int initialCapacity) {
		super(initialCapacity);
	}

	public TypedHashtable(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public TypedHashtable(Map t) {
		super(t);
	}
	
	
	public void put(Object key, boolean value) {
		Boolean b = value? Boolean.TRUE: Boolean.FALSE;
		put(key, b);
	}
	
	public void put(Object key, byte value) {
		put(key, new Byte(value));	
	}
	
	public void put(Object key, short value) {
		put(key, new Short(value));
	}
	
	public void put(Object key, int value) {
		put(key, new Integer(value));
	}
	
	public void put(Object key, long value) {
		put(key, new Long(value));
	}
	
	public void put(Object key, float value) {
		put(key, new Float(value));
	}
	
	public void put(Object key, double value) {
		put(key, new Double(value));
	}
	
	public void put(Object key, char value) {
		put(key, new Character(value));
	}
	
	public Object put(Object key, Object value) {
		if(value==null)
			return super.remove(key);
		else
			return super.put(key, value);
	}
	
	
	
	
	
	
	
	
	

	public boolean get(Object key, boolean defaultValue) {
		Boolean obj = (Boolean)get(key);
		return obj==null? defaultValue: obj.booleanValue();
	}
	
	public byte get(Object key, byte defaultValue) {
		Byte obj = (Byte)get(key);
		return obj==null? defaultValue: obj.byteValue();	
	}
	
	public short get(Object key, short defaultValue) {
		Short obj = (Short)get(key);
		return obj==null? defaultValue: obj.shortValue();
	}
	
	public int get(Object key, int defaultValue) {
		Integer obj = (Integer)get(key);
		return obj==null? defaultValue: obj.intValue();
	}
	
	public long get(Object key, long defaultValue) {
		Long obj = (Long)get(key);
		return obj==null? defaultValue: obj.longValue();
	}
	
	public float get(Object key, float defaultValue) {
		Float obj = (Float)get(key);
		return obj==null? defaultValue: obj.floatValue();
	}
	
	public double get(Object key, double defaultValue) {
		Double obj = (Double)get(key);
		return obj==null? defaultValue: obj.doubleValue();
	}
	
	public char get(Object key, char defaultValue) {
		Character obj = (Character)get(key);
		return obj==null? defaultValue: obj.charValue();
	}
	
	
	
	
	
	public Boolean getBoolean(Object key) {
		return (Boolean)get(key);
	}
	
	public Byte getByte(Object key) {
		return (Byte)get(key);
	}
	
	public Short getShort(Object key) {
		return (Short)get(key);
	}
	
	public Integer getInt(Object key) {
		return (Integer)get(key);
	}
	
	public Long getLong(Object key) {
		return (Long)get(key);
	}
	
	public Float getFloat(Object key) {
		return (Float)get(key);
	}
	
	public Double getDouble(Object key) {
		return (Double)get(key);
	}
	
	public Character getChar(Object key) {
		return (Character)get(key);
	}

}
