/*
 * Created on Aug 29, 2004
 */
package org.flexdock.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



/**
 * @author Christopher Butler
 */
public class Utilities {
	private static final String OS_FAMILIES_URI = "org/flexdock/util/os-families.xml";
	private static final String UNKNOWN_FAMILY = "unknown";
	private static final String FAMILY_KEY = "family";
	private static final String OS_KEY = "os";
	private static final String NAME_KEY = "name";
	public static final String OS_FAMILY = loadOSFamily();
	public static final String[] OS_CHAIN = loadOSChain();
	
	public static void pause(long millis) {
		try {
			Thread.sleep(millis);
		} catch(InterruptedException ignored) {
		}
	}
	
	public static int getInt(String data) {
		return getInt(data, 0);
	}
	
	public static int getInt(String data, int defaultValue) {
		try {
			return Integer.parseInt(data);
		} catch(Exception e) {
			return defaultValue;
		}
	}
	
	public static boolean isEmpty(String data) {
		return data==null? true: data.trim().length()==0;
	}
	
	private static String loadOSFamily() {
		Document document = ResourceManager.getDocument(OS_FAMILIES_URI);
		if(document==null)
			return UNKNOWN_FAMILY;
		
		String osName = System.getProperty("os.name");
		
		NodeList systems = document.getElementsByTagName(OS_KEY);
		for(int i=0; i<systems.getLength(); i++) {
			Element osElem = (Element)systems.item(i);
			String testName = osElem.getAttribute(NAME_KEY);
			if(osName.equals(testName)) {
				Element familyElem = (Element)osElem.getParentNode();
				return familyElem.getAttribute(NAME_KEY);
			}
		}
		return UNKNOWN_FAMILY;
	}
	
	private static String[] loadOSChain() {
		String osName = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		String fullName = osName + "." + arch;
		
		return new String[] {
			fullName, osName, OS_FAMILY	
		};
	}
	
	
	public static Object getInstance(String className) {
		return getInstance(className, false);
	}
	
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
	
	
	
	public static Object createInstance(String className) {
		return createInstance(className, null);
	}
	
	public static Object createInstance(String className, boolean failSilent) {
		return createInstance(className, null, failSilent);
	}
	
	public static Object createInstance(String className, Class superType) {
		return createInstance(className, superType, false);
	}
	
	public static Object createInstance(String className, Class superType, boolean failSilent) {
		try {
			Class c = Class.forName(className);
			if(superType!=null && !superType.isAssignableFrom(c))
				throw new ClassCastException("'" + c.getName() + "' is not a type of " + superType + ".");
			return c.newInstance();
		} catch(Throwable e) {
			if(!failSilent)
				e.printStackTrace();
			return null;
		}
	}

	public static boolean isEqual(Object oldObj, Object newObj) {
		return !isChanged(oldObj, newObj);
	}
	
	public static boolean isChanged(Object oldObj, Object newObj) {
		if(oldObj==newObj)
			return false;
		
		if(oldObj==null || newObj==null)
			return true;
		
		return !oldObj.equals(newObj);
	}
	
	public static boolean sysTrue(String key) {
		String value = key==null? null: System.getProperty(key);
		return value==null? false: "true".equals(value);
	}
	
	public static void put(Map map, Object key, Object value) {
		if(map==null || key==null)
			return;
		
		if(value==null)
			map.remove(key);
		else
			map.put(key, value);
	}
	
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
			throw t instanceof IllegalAccessException?
					(IllegalAccessException)t: 
					new IllegalAccessException(t.getMessage());
		}
	}
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
