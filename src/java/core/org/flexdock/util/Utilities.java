/*
 * Created on Aug 29, 2004
 */
package org.flexdock.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.flexdock.logging.Log;


/**
 * @author Christopher Butler
 */
public class Utilities {
	
	public static final boolean JAVA_1_4 = isJavaVersion("1.4");
	public static final boolean JAVA_1_5 = isJavaVersion("1.5");
	
	/**
	 * Returns an <code>int</code> value for the specified <code>String</code>.  This method calls 
	 * <code>Integer.parseInt(String s)</code> and returns the resulting <code>int</code> value.
	 * If any <code>Exception</code> is thrown, this method returns a value of <code>0</code>.
	 * 
	 * @param a <code>String</code> containing the <code>int</code> representation to be parsed
	 * @return the integer value represented by the argument in decimal
	 * @see #getInt(String, int)
	 * @see Integer#parseInt(java.lang.String)
	 */
	public static int getInt(String data) {
		return getInt(data, 0);
	}
	
	/**
	 * Returns an <code>int</code> value for the specified <code>String</code>.  This method calls 
	 * <code>Integer.parseInt(String s)</code> and returns the resulting <code>int</code> value.
	 * If any <code>Exception</code> is thrown, this method returns the value supplied by the
	 * <code>defaultValue</code> parameter.
	 * 
	 * @param a <code>String</code> containing the <code>int</code> representation to be parsed.
	 * @param defaultValue the value to return if an <code>Exception</code> is encountered.
	 * @return the integer value represented by the argument in decimal
	 * @see Integer#parseInt(java.lang.String)
	 */
	public static int getInt(String data, int defaultValue) {
		if(data==null)
			return defaultValue;
		
		try {
			return Integer.parseInt(data);
		} catch(Exception e) {
			return defaultValue;
		}
	}
	
	public static float getFloat(String data, float defaultValue) {
		if(data==null)
			return defaultValue;
		
		try {
			return Float.parseFloat(data);
		} catch(Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns <code>true</code> if the specified <code>String</code> is <code>null</code> or contains only 
	 * whitespace.  Otherwise, returns <code>false</code>.  The whitespace check is performed by calling
	 * <code>trim()</code> and checking to see if the trimmed string <code>length()</code> is zero.
	 * 
	 * @param data the <code>String</code> to check for non-whitespace content
	 * @return <code>true</code> if the specified <code>String</code> is <code>null</code> or contains only 
	 * whitespace, <code>false</code> otherwise.
	 */
	public static boolean isEmpty(String data) {
		return data==null? true: data.trim().length()==0;
	}
	
	
	/**
	 * Returns an instance of the specified class name.  If <code>className</code> is <code>null</code>, 
	 * then this method returns a <code>null</code> reference.  
	 * <br/>
	 * This method will try two different means of obtaining an instance of <code>className</code>.  First, 
	 * it will attempt to resolve the <code>Class</code> of <code>className</code> via 
	 * <code>Class.forName(String className)</code>.  It will then use reflection to search for a method
	 * on the class named <code>"getInstance()"</code>.  If the method is found, then it is invoked and
	 * the object instance is returned.
	 * <br/>
	 * If there are any problems encountered while attempting to invoke <code>getInstance()</code> on the 
	 * specified class, the <code>Throwable</code> is caught and this method dispatches to 
	 * <code>createInstance(String className, boolean failSilent)</code> with an argument of <code>false</code>
	 * for <code>failSilent</code>.  <code>createInstance(String className, boolean failSilent)</code>
	 * will attempt to invoke <code>newInstance()</code> on the <code>Class</code> for the specified 
	 * class name.  If any <code>Throwable</code> is encountered during this process, the value of 
	 * <code>false</code> for <code>failSilent</code> will cause the stack trace to be printed to the 
	 * <code>System.err</code> and a <code>null</code> reference will be returned.
	 * 
	 * @param className the fully qualified name of the desired class.
	 * @return an instance of the specified class
	 * @see #getInstance(String, boolean)
	 * @see #createInstance(String, boolean)
	 * @see Class#forName(java.lang.String)
	 * @see Class#getMethod(java.lang.String, java.lang.Class[])
	 * @see Method#invoke(java.lang.Object, java.lang.Object[])
	 * @see Class#newInstance()
	 */
	public static Object getInstance(String className) {
		return getInstance(className, false);
	}
	
	/**
	 * Returns an instance of the specified class name.  If <code>className</code> is <code>null</code>, 
	 * then this method returns a <code>null</code> reference.  
	 * <br/>
	 * This method will try two different means of obtaining an instance of <code>className</code>.  First, 
	 * it will attempt to resolve the <code>Class</code> of <code>className</code> via 
	 * <code>Class.forName(String className)</code>.  It will then use reflection to search for a method
	 * on the class named <code>"getInstance()"</code>.  If the method is found, then it is invoked and
	 * the object instance is returned.
	 * <br/>
	 * If there are any problems encountered while attempting to invoke <code>getInstance()</code> on the 
	 * specified class, the <code>Throwable</code> is caught and this method dispatches to 
	 * <code>createInstance(String className, boolean failSilent)</code>, passing the specified value
	 * for <code>failSilent</code>.  <code>createInstance(String className, boolean failSilent)</code>
	 * will attempt to invoke <code>newInstance()</code> on the <code>Class</code> for the specified 
	 * class name.  If any <code>Throwable</code> is encountered during this process, the value of 
	 * <code>failSilent</code> is checked to determine whether the stack stack trace should be printed to the 
	 * <code>System.err</code>.  A <code>null</code> reference will be returned if any problems are encountered.
	 * 
	 * @param className the fully qualified name of the desired class.
	 * @param failSilent <code>true</code> if the stack trace should <b>not</b> be printed to the 
	 * <code>System.err</code> when a <code>Throwable</code> is caught, <code>false</code> otherwise.
	 * @return an instance of the specified class
	 * @see #createInstance(String, boolean)
	 * @see Class#forName(java.lang.String)
	 * @see Class#getMethod(java.lang.String, java.lang.Class[])
	 * @see Method#invoke(java.lang.Object, java.lang.Object[])
	 * @see Class#newInstance()
	 */
	public static Object getInstance(String className, boolean failSilent) {
		if(className==null)
			return null;
		
		try {
			Class c = Class.forName(className);
			Method m = c.getMethod("getInstance", new Class[0]);
			return m.invoke(null, new Object[0]);
		} catch(Throwable e) {
			return createInstance(className, failSilent);
		}
	}
	
	
	/**
	 * Creates and returns an instance of the specified class name using <code>Class.newInstance()</code>.  
	 * If <code>className</code> is <code>null</code>, then this method returns a <code>null</code> reference.
	 * This dispatches to <code>createInstance(String className, Class superType, boolean failSilent)</code> 
	 * with an argument of <code>null</code> for <code>superType</code> and <code>false</code> for 
	 * <code>failSilent</code>.
	 * <br/>
	 * This method will attempt to resolve the <code>Class</code> of <code>className</code> via 
	 * <code>Class.forName(String className)</code>.  No class assignability checkes are performed because
	 * this method uses a <code>null</code> <code>superType</code>.
	 * <br/>
	 * Once the desired class has been resolved, a new instance of it is created and returned by invoking
	 * its <code>newInstance()</code> method.
	 * If there are any problems encountered during this process, the value of <code>false</code> for 
	 * <code>failSilent</code> will ensure the stack stack trace is be printed to the 
	 * <code>System.err</code>.  A <code>null</code> reference will be returned if any problems are encountered.
	 * 
	 * @param className the fully qualified name of the desired class.
	 * @param failSilent <code>true</code> if the stack trace should <b>not</b> be printed to the 
	 * <code>System.err</code> when a <code>Throwable</code> is caught, <code>false</code> otherwise.
	 * @return an instance of the specified class
	 * @see #createInstance(String, Class, boolean)
	 * @see Class#forName(java.lang.String)
	 * @see Class#newInstance()
	 */
	public static Object createInstance(String className) {
		return createInstance(className, null);
	}
	
	/**
	 * Creates and returns an instance of the specified class name using <code>Class.newInstance()</code>.  
	 * If <code>className</code> is <code>null</code>, then this method returns a <code>null</code> reference.  
	 * The <code>failSilent</code> parameter will determine whether error stack traces should be reported to 
	 * the <code>System.err</code> before this method returns <code>null</code>.  This method dispatches
	 * to <code>createInstance(String className, Class superType, boolean failSilent)</code> with an
	 * argument of <code>null</code> for <code>superType</code>.
	 * <br/>
	 * This method will attempt to resolve the <code>Class</code> of <code>className</code> via 
	 * <code>Class.forName(String className)</code>.  No class assignability checkes are performed because
	 * this method uses a <code>null</code> <code>superType</code>.
	 * <br/>
	 * Once the desired class has been resolved, a new instance of it is created and returned by invoking
	 * its <code>newInstance()</code> method.
	 * If there are any problems encountered during this process, the value of 
	 * <code>failSilent</code> is checked to determine whether the stack stack trace should be printed to the 
	 * <code>System.err</code>.  A <code>null</code> reference will be returned if any problems are encountered.
	 * 
	 * @param className the fully qualified name of the desired class.
	 * @param failSilent <code>true</code> if the stack trace should <b>not</b> be printed to the 
	 * <code>System.err</code> when a <code>Throwable</code> is caught, <code>false</code> otherwise.
	 * @return an instance of the specified class
	 * @see #createInstance(String, Class, boolean)
	 * @see Class#forName(java.lang.String)
	 * @see Class#newInstance()
	 */
	public static Object createInstance(String className, boolean failSilent) {
		return createInstance(className, null, failSilent);
	}
	
	/**
	 * Creates and returns an instance of the specified class name using <code>Class.newInstance()</code>.  
	 * If <code>className</code> is <code>null</code>, then this method returns a <code>null</code> reference.  
	 * If <code>superType</code> is non-<code>null</code>, then this method will enforce polymorphic identity 
	 * via <code>Class.isAssignableFrom(Class cls)</code>.  This method dispatches to 
	 * <code>createInstance(String className, Class superType, boolean failSilent)</code> with an argument
	 * of <code>false</code> for <code>failSilent</code>.
	 * <br/>
	 * This method will attempt to resolve the <code>Class</code> of <code>className</code> via 
	 * <code>Class.forName(String className)</code>.  If <code>superType</code> is non-<code>null</code>,
	 * then class identity is checked by calling <code>superType.isAssignableFrom(c)</code> to ensure
	 * the resolved class is an valid equivalent, descendent, or implementation of the specified 
	 * <code>className</code>.  If this check fails, then a <code>ClassCastException</code> is thrown and
	 * caught internally and this method returns <code>null</code>.  If <code>superType</code> is 
	 * <code>null</code>, then no assignability checks are performed on the resolved class.
	 * <br/>
	 * Once the desired class has been resolved, a new instance of it is created and returned by invoking
	 * its <code>newInstance()</code> method.
	 * If there are any problems encountered during this process, the value of <code>false</code> for 
	 * <code>failSilent</code> will ensure the stack stack trace is be printed to the 
	 * <code>System.err</code>.  A <code>null</code> reference will be returned if any problems are encountered.
	 * 
	 * @param className the fully qualified name of the desired class.
	 * @param superType optional paramter used as a means of enforcing the inheritance hierarchy
	 * @return an instance of the specified class
	 * @see #createInstance(String, Class, boolean)
	 * @see Class#forName(java.lang.String)
	 * @see Class#isAssignableFrom(java.lang.Class)
	 * @see Class#newInstance()
	 */
	public static Object createInstance(String className, Class superType) {
		return createInstance(className, superType, false);
	}
	
	/**
	 * Creates and returns an instance of the specified class name using <code>Class.newInstance()</code>.  
	 * If <code>className</code> is <code>null</code>, then this method returns a <code>null</code> reference.  
	 * If <code>superType</code> is non-<code>null</code>, then this method will enforce polymorphic identity 
	 * via <code>Class.isAssignableFrom(Class cls)</code>.  The <code>failSilent</code> parameter will
	 * determine whether error stack traces should be reported to the <code>System.err</code> before this
	 * method returns <code>null</code>.
	 * <br/>
	 * This method will attempt to resolve the <code>Class</code> of <code>className</code> via 
	 * <code>Class.forName(String className)</code>.  If <code>superType</code> is non-<code>null</code>,
	 * then class identity is checked by calling <code>superType.isAssignableFrom(c)</code> to ensure
	 * the resolved class is an valid equivalent, descendent, or implementation of the specified 
	 * <code>className</code>.  If this check fails, then a <code>ClassCastException</code> is thrown and
	 * caught internally and this method returns <code>null</code>.  If <code>superType</code> is 
	 * <code>null</code>, then no assignability checks are performed on the resolved class.
	 * <br/>
	 * Once the desired class has been resolved, a new instance of it is created and returned by invoking
	 * its <code>newInstance()</code> method.
	 * If there are any problems encountered during this process, the value of 
	 * <code>failSilent</code> is checked to determine whether the stack stack trace should be printed to the 
	 * <code>System.err</code>.  A <code>null</code> reference will be returned if any problems are encountered.
	 * 
	 * @param className the fully qualified name of the desired class.
	 * @param superType optional paramter used as a means of enforcing the inheritance hierarchy
	 * @param failSilent <code>true</code> if the stack trace should <b>not</b> be printed to the 
	 * <code>System.err</code> when a <code>Throwable</code> is caught, <code>false</code> otherwise.
	 * @return an instance of the specified class
	 * @see Class#forName(java.lang.String)
	 * @see Class#isAssignableFrom(java.lang.Class)
	 * @see Class#newInstance()
	 */
	public static Object createInstance(String className, Class superType, boolean failSilent) {
		if(className==null)
			return null;
		
		try {
			Class c = Class.forName(className);
			if(superType!=null && !superType.isAssignableFrom(c))
				throw new ClassCastException("'" + c.getName() + "' is not a type of " + superType + ".");
			return c.newInstance();
		} catch(Throwable e) {
			if(!failSilent)
				Log.warn(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Checks for equality between the two specified <code>Objects</code>.  If both arguments
	 * are the same <code>Object</code> reference using an <code>==</code> relationship, then this
	 * method returns <code>true</code>.  Failing that check, if either of the arguments is <code>null</code>, 
	 * then the other must not be and this method returns <code>false</code>.  Finally, if
	 * both arguments are non-<code>null</code> with different <code>Object</code> references, then
	 * this method returns the value of <code>obj1.equals(obj2)</code>.
	 * <br/>
	 * This method is the exact opposite of <code>isChanged(Object oldObj, Object newObj)</code>.
	 * 
	 * @param obj1 the first <code>Object</code> to be checked for equality
	 * @param obj2 the second <code>Object</code> to be checked for equality
	 * @return <code>true</code> if the <code>Objects</code> are equal, <code>false</code> otherwise.
	 * @see #isChanged(Object, Object)
	 * @see Object#equals(java.lang.Object)
	 */
	public static boolean isEqual(Object obj1, Object obj2) {
		return !isChanged(obj1, obj2);
	}
	
	/**
	 * Checks for inequality between the two specified <code>Objects</code>.  If both arguments
	 * are the same <code>Object</code> reference using an <code>==</code> relationship, then this
	 * method returns <code>false</code>.  Failing that check, if either of the arguments is <code>null</code>, 
	 * then the other must not be and this method returns <code>true</code>.  Finally, if
	 * both arguments are non-<code>null</code> with different <code>Object</code> references, then
	 * this method returns the opposite value of <code>obj1.equals(obj2)</code>.
	 * <br/>
	 * This method is the exact opposite of <code>isEqual(Object obj1, Object obj2)</code>.
	 * 
	 * @param oldObj the first <code>Object</code> to be checked for inequality
	 * @param newObj the second <code>Object</code> to be checked for inequality
	 * @return <code>false</code> if the <code>Objects</code> are equal, <code>true</code> otherwise.
	 * @see #isEqual(Object, Object)
	 * @see Object#equals(java.lang.Object)
	 */
	public static boolean isChanged(Object oldObj, Object newObj) {
		if(oldObj==newObj)
			return false;
		
		if(oldObj==null || newObj==null)
			return true;
		
		return !oldObj.equals(newObj);
	}
	
	/**
	 * Returns <code>true</code> if there is currently a <code>System</code> property with the specified 
	 * <code>key</code> whose value is "true".  If the <code>System</code> property does not exist, or the
	 * value is inequal to "true", this method returns <code>false</code>.  This method returns <code>false</code>
	 * if the specified <code>key</code> parameter is <code>null</code>.
	 * 
	 * @param the key for the <code>System</code> property to be tested.
	 * @return <code>true</code> if there is currently a <code>System</code> property with the specified 
	 * <code>key</code> whose value is "true".
	 * @see System#getProperty(java.lang.String)
	 * @see String#equals(java.lang.Object)
	 */
	public static boolean sysTrue(String key) {
		String value = key==null? null: System.getProperty(key);
		return value==null? false: "true".equals(value);
	}
	
	/**
	 * Puts the supplied <code>value</code> into the specified <code>Map</code> using the specified <code>key</code>.
	 * This is a convenience method to automate null-checks.  A <code>value</code> parameter of <code> is 
	 * interpreted as a removal from the specified <code>Map</code> rather than an <code>put</code> operation. 
	 * <br/>
	 * If either <code>map</code> or <code>key</code> are <code>null</code> then this method returns with no
	 * action taken.  If <code>value<code> is <code>null</code>, then this method calls <code>map.remove(key)</code>.  
	 * Otherwise, this method calls <code>map.put(key, value)</code>.
	 * 
	 * @param map the <code>Map</code> whose contents is to be modified
	 * @param key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 * @see Map#put(java.lang.Object, java.lang.Object)
	 * @see Map#remove(java.lang.Object)
	 */
	public static void put(Map map, Object key, Object value) {
		if(map==null || key==null)
			return;
		
		if(value==null)
			map.remove(key);
		else
			map.put(key, value);
	}
	
	/**
	 * Returns the value of the specified <code>fieldName</code> within the specified <code>Object</code>.
	 * This is a convenience method for reflection hacks to retrieve the value of protected, private, or 
	 * package-private field members while hiding the boilerplate reflection code within a single utility
	 * method call.  This method will return <code>true</code> if the operation was successful and 
	 * <code>false</code> if errors were encountered.
	 * <br/>
	 * This method calls <code>obj.getClass()</code> to retrieve the <code>Class</code> of the specified
	 * <code>Object</code>.  It then retrieves the desired field by calling the classes' 
	 * <code>getDeclaredField(String name)</code> method, passing the specified field name.  If the 
	 * field is deemed inaccessible via it's <code>isAccessible()</code> method, then it is made 
	 * accessible by calling <code>setAccessible(true)</code>.  The field value is set by invoking the
	 * field's <code>set(Object obj, Object value)</code> method and passing the original <code>Object</code> 
	 * and <code>value</code> parameter as arguments.  Before returning, the field's accessibility is reset to its 
	 * original state.
	 * <br/>
	 * If either <code>obj</code> or <code>fieldName</code> are <code>null</code>, then this method
	 * returns <code>false</code>.
	 * <br/>
	 * It should be understood that this method will not function properly for inaccessible fields in the 
	 * presence of a <code>SecurityManager</code>.  Nor will it function properly for non-existent fields
	 * (if a field called <code>fieldName</code> does not exist on the class).  All <code>Throwables</code>
	 * encountered by this method will be caught and eaten and the method will return <code>false</code>.
	 * This works under the assumption that the operation might likely fail because the method itself is, 
	 * in reality, a convenience hack.  Therefore, specifics of any generated errors on the call stack are
	 * discarded and only the final outcome (<code>true/false</code> of the operation is deemed relevant.
	 * <b>If call stack data is required within the application for any thrown exceptions, then this method
	 * should not be used.</code> 
	 * 
	 * @param obj the object for which the represented field's value is to be modified
	 * @param fieldName the name of the field to be set
	 * @param value the new value for the field of <code>obj</code> being modified
	 * @see Object#getClass()
	 * @see Class#getDeclaredField(java.lang.String)
	 * @see Field#isAccessible()
	 * @see Field#setAccessible()
	 * @see Field#set(java.lang.Object)
	 */
	public static boolean setValue(Object obj, String fieldName, Object value) {
		if(obj==null || fieldName==null)
			return false;
		
		try {
			Class c = obj.getClass();
			Field field = c.getDeclaredField(fieldName);
			if(field.isAccessible()) {
				field.set(obj, value);
				return true;
			}
			
			field.setAccessible(true);
			field.set(obj, value);
			field.setAccessible(false);
			return true;
		} catch(Throwable t) {
			// don't report the error. the purpse of this method is to try to
			// access the field, but fail silently if we can't.
			return false;
		}
	}
	
	/**
	 * Returns the value of the specified <code>fieldName</code> within the specified <code>Object</code>.
	 * This is a convenience method for reflection hacks to retrieve the value of protected, private, or 
	 * package-private field members while hiding the boilerplate reflection code within a single utility
	 * method call.
	 * <br/>
	 * This method calls <code>obj.getClass()</code> to retrieve the <code>Class</code> of the specified
	 * <code>Object</code>.  It then retrieves the desired field by calling the classes' 
	 * <code>getDeclaredField(String name)</code> method, passing the specified field name.  If the 
	 * field is deemed inaccessible via it's <code>isAccessible()</code> method, then it is made 
	 * accessible by calling <code>setAccessible(true)</code>.  The return value is retrieved by invoking the
	 * field's <code>get(Object obj)</code> method and passing the original <code>Object</code> parameter
	 * as an argument.  Before returning, the field's accessibility is reset to its original state.
	 * <br/>
	 * If either <code>obj</code> or <code>fieldName</code> are <code>null</code>, then this method
	 * returns <code>null</code>.
	 * <br/>
	 * It should be understood that this method will not function properly for inaccessible fields in the 
	 * presence of a <code>SecurityManager</code>.  Nor will it function properly for non-existent fields
	 * (if a field called <code>fieldName</code> does not exist on the class).  All <code>Throwables</code>
	 * encountered by this method will be rethrown as <code>IllegalAccessException</code>.  For wrapped
	 * <code>Throwables</code>, the original cause can be accessed via <code>IllegalAccessException's</code>
	 * <code>getCause()</code> method.
	 * 
	 * @param obj the object from which the represented field's value is to be extracted
	 * @param fieldName the name of the field to be checked
	 * @return the value of the represented field in object <code>obj</code>; primitive values are 
	 * wrapped in an appropriate object before being returned
	 * @see Object#getClass()
	 * @see Class#getDeclaredField(java.lang.String)
	 * @see Field#isAccessible()
	 * @see Field#setAccessible()
	 * @see Field#get(java.lang.Object)
	 * @see IllegalAccessException#getCause()
	 */
	public static Object getValue(Object obj, String fieldName) throws IllegalAccessException {
		if(obj==null || fieldName==null)
			return null;
		
		try {
			Class c = obj.getClass();
			Field field = c.getDeclaredField(fieldName);
			if(field.isAccessible()) {
				return field.get(obj);
			}
			
			field.setAccessible(true);
			Object ret = field.get(obj);
			field.setAccessible(false);
			return obj;
		} catch(Throwable t) {
			if(t instanceof IllegalAccessException)
				throw (IllegalAccessException)t;
			
			IllegalAccessException e = new IllegalAccessException(t.getMessage());
			e.initCause(t);
			throw e;
		}
	}
	
	/**
	 * Puts the current <code>Thread</code> to sleep for the specified timeout.  This method calls
	 * <code>Thread.sleep(long millis)</code>, catching any thrown <code>InterruptedException</code> and 
	 * printing a stack trace to the <code>System.err</code>.
	 * 
	 * @param millis the length of time to sleep in milliseconds.
	 * @see Thread#sleep(long)
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(InterruptedException e) {
			Log.debug(e.getMessage(), e);
		}
	}
	
	private static boolean isJavaVersion(String version) {
		if(version==null)
			return false;
		return System.getProperty("java.version").startsWith(version);
	}
}
