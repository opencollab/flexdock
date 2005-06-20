/*
 * Created on Apr 5, 2005
 */
package org.flexdock.util;

import java.util.WeakHashMap;

/**
 * This class manages associations between classes and object instances.  It allows for mappings 
 * between a class and its subclasses and another associated class, or an associated instance of
 * a class.
 * <br/>
 * This class is useful for "handler" type logic in which a handler class must be mapped to 
 * the classes it is designed to handle.  Consider the class hierarchy of <code>Foo</code>, 
 * <code>Bar</code>, and <code>Baz</code>, 
 * where <code>Bar</code> extends <code>Foo</code> and <code>Baz</code> extends <code>Bar</code>.  
 * <pre>
 * Foo.class
 *   |-Bar.class
 *       |-Baz.class
 * </pre>
 * Each of these classes is ultimately a type of <code>Foo</code>.  Some operation is performed on 
 * instances of <code>Foo</code> and a set of handler classes are used to handle different types of 
 * <code>Foo</code>.  Adding a mapping between <code>Foo.class</code> and <code>Handler1.class</code> 
 * will create an association between <code>Foo</code> and all <i>strict, non-specific</i> subclasses 
 * of <code>Foo</code> and <code>Handler1.class</code>.
 * <br/>
 * This means that given any instance of <code>Foo</code>, calling <code>getClassMapping(Object obj)</code>
 * will return <code>Handler1.class</code> as the class responsible for handling the <code>Foo</code>
 * instance.  This includes <code>Bar</code> and <code>Baz</code>.  All types of <code>Foo</code>
 * now have a implicit association with <code>Handler1.class</code>
 * <br/>
 * However, if this method is subsequently called with arguments of <code>Baz.class</code> and 
 * <code>Handler2.class</code>, then a <i>specific</i> subclass mapping has been introduced for 
 * <code>Baz</code>.  Associations apply to the given class and <i>non-specific</i> subclasses.  
 * Thus, the <code>Handler1.class</code> association remains for <code>Foo</code> and 
 * <code>Bar</code>, but no longer for <code>Baz</code>.  Calling 
 * <code>getClassMapping(Object obj)</code> with an instance of <code>Baz</code> will now return 
 * <code>Handler2.class</code>.
 * <pre>
 * Foo.class --------------->(maps to Handler1.class)
 *   |-Bar.class------------>(maps to Handler1.class)
 *       |-Baz.class-------->(maps to Handler2.class)
 * </pre>
 * Polymorphic identity within the class association uses <i>strict</i> subclasses.  This means that 
 * the <code>Handler1.class</code> mapping for <code>Foo</code>, <code>Bar</code>, and all non-specific 
 * subclasses will hold true.  However, if <code>Foo</code> happens to implement the interface 
 * <code>Qwerty</code>, the class mapping relationship will not hold true for all implementations of 
 * <code>Qwerty</code>.  Only subclasses of <code>Foo</code>.
 * <pre>
 * Foo.class (implements Qwerty)---------------->(maps to Handler1.class)
 *   |-Bar.class (implements Qwerty)------------>(maps to Handler1.class)
 *       |-Baz.class (implements Qwerty)-------->(maps to Handler2.class)
 * Asdf.class (implements Qwerty) -------------->(maps to nothing)
 * </pre>
 * @author Christopher Butler
 */
public class ClassMapping {
	private WeakHashMap classes;
	private WeakHashMap instances;
	
	private Class defaultClass;
	private Object defaultInstance;
	
	/**
	 * Creates a new <code>ClassMapping</code> instance with the specified default values.
	 * All calls to <code>getClassMapping(Class key)</code> for this <code>ClassMapping</code> in which
	 * a specific mapping cannot be found will return the specified <code>defaultClass</code>.  All
	 * calls to <code>getClassInstance(Class key)</code> in which a specific mapping cannot be found will
	 * return the specified <code>defaultInstance</code>.
	 * @param defaultClass the default class used by this <code>ClassMapping</code>
	 * @param defaultInstance the default object instance used by this <code>ClassMapping</code>
	 */
	public ClassMapping(Class defaultClass, Object defaultInstance) {
		this.defaultClass = defaultClass;
		this.defaultInstance = defaultInstance;
		
		classes = new WeakHashMap(4);
		instances = new WeakHashMap(4);
	}
	
	/**
	 * Adds a mapping between the <code>Class</code> type of the specified <code>Object</code>
	 * and the specified <code>value</code>.  This method calls <code>getClass()</code> on the 
	 * specified <code>Object</code> and dispatches to <code>addClassMapping(Class key, Class value)</code>.
	 * If either <code>obj</code> or <code>value</code> are <code>null</code>, then this method returns with no
	 * action taken.  
	 * The <code>value</code> class may later be retrieved by calling 
	 * <code>getClassMapping(Class key)</code>
	 * using the specified <code>key</code> class (<code>obj.getClass()</code>) or any subclass 
	 * thereof for which a specific class mapping does not already exist.
	 * 
	 * @param obj the <code>Object</code> whose <code>Class</code> will be mapped to the
	 * specified <code>value</code>.
	 * @param value the <code>Class</code> to be associated with the specified <b>key</b>
	 * @see #addClassMapping(Object, Class)
	 * @see #getClassMapping(Object)
	 * @see #getClassMapping(Class)
	 * @see #removeClassMapping(Object)
	 * @see #removeClassMapping(Class)
	 */
	public void addClassMapping(Object obj, Class value) {
		Class key = obj==null? null: obj.getClass();
		addClassMapping(key, value);
	}

	/**
	 * Adds a mapping between the key <code>Class</code> and the specified <code>value</code>.  If either 
	 * <code>key</code> or <code>value</code> are <code>null</code>, then this method returns with no action 
	 * taken. This method creates an association between the specified <code>key</code> <code>Class</code>
	 * and all strict, non-specific subclasses and the specified <code>value</code> <code>Class</code>.
	 * The <code>value</code> class may later be retrieved by calling getClassMapping(Class key)
	 * using the specified <code>key</code> class or any subclass thereof for which a 
	 * specific class mapping does not already exist.
	 * 
	 * @param key the <code>Class</code> to be mapped to the specified <code>value</code>.
	 * @param value the <code>Class</code> to be associated with the specified <b>key</b>
	 * @see #addClassMapping(Class, Class, Object)
	 * @see #getClassMapping(Class)
	 * @see #removeClassMapping(Class)
	 */
	public void addClassMapping(Class key, Class value) {
		addClassMapping(key, value, null);
	}
	
	/**
	 * Adds a mapping between the key <code>Class</code> and both the specified <code>value</code>
	 * and specified object instance..  If either <code>key</code> or <code>value</code> are 
	 * <code>null</code>, then this method returns with no action taken. This method creates an 
	 * association between the specified <code>key</code> <code>Class</code> and all strict, 
	 * non-specific subclasses and the specified <code>value</code> <code>Class</code>.  The
	 * <code>value</code> class may later be retrieved by calling 
	 * <code>getClassMapping(Class key)</code>
	 * using the specified <code>key</code> class or any subclass thereof for which a 
	 * specific class mapping does not already exist.
	 * <br/>
	 * This method also creates an optional mapping between the <code>key</code> and a 
	 * particular object instance, defined by the <code>instance</code> parameter.  If 
	 * <code>instance</code> is non-<code>null</code>, then a mapping is defined between
	 * <code>key</code> and all strict, non-specific subclasses and the object instance itself.
	 * The <code>instance</code> object may later be retrieved by calling 
	 * <code>getClassInstance(Class key)</code>
	 * using the specified <code>key</code> class or any subclass thereof for which a 
	 * specific instance mapping does not already exist.  If <code>instance</code> is 
	 * <code>null</code>, then no instance mapping is created.
	 * 
	 * @param key the <code>Class</code> to be mapped to the specified <code>value</code>.
	 * @param value the <code>Class</code> to be associated with the specified <b>key</b>
	 * @param instance the object instance to be associated with the specified <b>key</b>
	 * @see #getClassMapping(Class)
	 * @see #getClassInstance(Class)
	 * @see #removeClassMapping(Class)
	 */
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
	
	/**
	 * Removes any existing class mappings for the <code>Class</code> type of the specified 
	 * <code>Object</code>.  This method calls <code>getClass()</code> on the specified 
	 * <code>Object</code> and dispatches to <code>removeClassMapping(Class key)</code>.
	 * If <code>obj</code> is <code>null</code>, then this method returns <code>null</code>.
	 * <br/>
	 * Removing the mapping for the specified <code>Class</code> will also remove it for 
	 * all non-specific subclasses.  This means that subclasses of the specified 
	 * <code>Class</code> will require specific mappings if the it is desired for the existing 
	 * mapping behavior for these classes to remain the same.
	 * <br/>
	 * If any instance mappings exist for the specified <code>Class</code>, they are also 
	 * removed.  This means non-specific subclass instance mappings will also be removed.
	 * 
	 * @param obj the <code>Object</code> whose <code>Class</code> will be removed from the
	 * internal mapping
	 * @return the <code>Class</code> whose mapping has been removed
	 * @see #removeClassMapping(Class)
	 * @see #addClassMapping(Object, Class)
	 * @see #getClassMapping(Object)
	 * @see #getClassInstance(Class)
	 */
	public Class removeClassMapping(Object obj) {
		Class key = obj==null? null: obj.getClass();
		return removeClassMapping(key);		
	}

	/**
	 * Removes any existing class mappings for the <code>Class</code> type of the specified 
	 * <code>Object</code>.  This method calls <code>getClass()</code> on the specified 
	 * <code>Object</code> and dispatches to <code>removeClassMapping(Class key)</code>.
	 * If <code>obj</code> is <code>null</code>, then this method returns <code>null</code>.
	 * <br/>
	 * Removing the mapping for the specified <code>Class</code> will also remove it for 
	 * all non-specific subclasses.  This means that subclasses of the specified 
	 * <code>Class</code> will require specific mappings if the it is desired for the existing 
	 * mapping behavior for these classes to remain the same.
	 * <br/>
	 * If any instance mappings exist for the specified <code>Class</code>, they are also 
	 * removed.  This means non-specific subclass instance mappings will also be removed.
	 * 
	 * @param key the <code>Class</code> whose internal mapping will be removed
	 * @return the <code>Class</code> whose mapping has been removed
	 * @see #addClassMapping(Class, Class)
	 * @see #addClassMapping(Class, Class, Object)
	 * @see #getClassMapping(Object)
	 * @see #getClassInstance(Class)
	 */
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

	/** 
	 * Returns the <code>Class</code> associated with the <code>Class</code> of the specified
	 * <code>Object</code>.  If <code>obj</code> is <code>null</code>, this method will return 
	 * the value retrieved from <code>getDefaultMapping()</code>.   Otherwise, this method calls 
	 * <code>obj.getClass()</code> and dispatches to <code>getClassMapping(Class key)</code>.
	 * <br/>
	 * If no mapping has been defined for the specified <code>Class</code>, then
	 * it's superclass is checked, and then that classes' superclass, and so on until 
	 * <code>java.lang.Object</code> is reached.  If a mapping is found anywhere within
	 * the superclass hierarchy, then the mapped <code>Class</code> is returned.  Otherwise, 
	 * the value returned by <code>getDefaultMapping()</code> is returned.
	 * 
	 * @param obj the <code>Object</code> whose <code>Class's</code> internal mapping will be 
	 * returned
	 * @return the <code>Class</code> that is mapped internally to the specified key 
	 * <code>Class</code>
	 * @see #getDefaultMapping()
	 * @see #addClassMapping(Object, Class)
	 * @see #removeClassMapping(Object)
	 */
	public Class getClassMapping(Object obj) {
		Class key = obj==null? null: obj.getClass();
		return getClassMapping(key);
	}
	
	/** 
	 * Returns the <code>Class</code> associated with the specified <code>Class</code>.  
	 * If <code>key</code> is <code>null</code>, this method will return the value 
	 * retrieved from <code>getDefaultMapping()</code>. 
	 * If no mapping has been defined for the specified <code>Class</code>, then
	 * it's superclass is checked, and then that classes' superclass, and so on until 
	 * <code>java.lang.Object</code> is reached.  If a mapping is found anywhere within
	 * the superclass hierarchy, then the mapped <code>Class</code> is returned.  Otherwise, 
	 * the value returned by <code>getDefaultMapping()</code> is returned.
	 * 
	 * @param key the <code>Class</code> whose internal mapping will be returned
	 * @return the <code>Class</code> that is mapped internally to the specified <code>key</code>
	 * @see #getDefaultMapping()
	 * @see #addClassMapping(Class, Class)
	 * @see #removeClassMapping(Class)
	 */
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
	
	/** 
	 * Returns the <code>Object</code> instance associated with the specified <code>Class</code>.  
	 * If <code>key</code> is <code>null</code>, this method will return the value 
	 * retrieved from <code>getDefaultInstance()</code>.  
	 * If no mapping has been defined for the specified <code>Class</code>, then
	 * it's superclass is checked, and then that classes' superclass, and so on until 
	 * <code>java.lang.Object</code> is reached.  If an instance mapping is found anywhere within
	 * the superclass hierarchy, then the mapped <code>Object</code> is returned.  Otherwise, 
	 * the value returned by <code>getDefaultInstance()</code> is returned.
	 * 
	 * @param key the <code>Class</code> whose internal mapping will be returned
	 * @return the <code>Object</code> instance that is mapped internally to the specified 
	 * <code>key</code>
	 * @see #getDefaultInstance()
	 * @see #addClassMapping(Class, Class, Object)
	 * @see #removeClassMapping(Class)
	 */
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
	
	/**
	 * Returns the default <code>Class</code> used for situations in which there is no
	 * internal class mapping.  This property is read-only and is initialized within the 
	 * <code>ClassMapping</code> constructor.
	 * 
	 * @return the default <code>Class</code> used for situations in which there is no
	 * internal class mapping.
	 * @see #ClassMapping(Class, Object)
	 */
	public Class getDefaultMapping() {
		return defaultClass;
	}
	
	/**
	 * Returns the default <code>Object</code> used for situations in which there is no
	 * internal instance mapping.  This property is read-only and is initialized within the 
	 * <code>ClassMapping</code> constructor.
	 * 
	 * @return the default <code>Object</code> used for situations in which there is no
	 * internal instance mapping.
	 * @see #ClassMapping(Class, Object)
	 */
	public Object getDefaultInstance() {
		return defaultInstance;
	}
}
