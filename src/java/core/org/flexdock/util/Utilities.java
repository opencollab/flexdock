/*
 * Created on Aug 29, 2004
 */
package org.flexdock.util;

/**
 * @author marius
 */
public class Utilities {
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
}
