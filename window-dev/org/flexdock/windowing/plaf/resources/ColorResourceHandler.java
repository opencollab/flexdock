/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.resources;

import javax.swing.plaf.ColorUIResource;

import org.flexdock.windowing.plaf.Configurator;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ColorResourceHandler extends ResourceHandler {
	
	public Object getResource(String stringValue) {
		return parseHexColor(stringValue);
	}
	
	public static ColorUIResource parseHexColor(String hexColor) {
		if(Configurator.isNull(hexColor))
			return null;
		
		StringBuffer sb = new StringBuffer(6);
		int len = hexColor.length();
		
		// strip out non-hex characters
		for(int i=0; i<len; i++) {
			char c = hexColor.charAt(i);
			if(isHex(c))
				sb.append(c);
		}
		
		try {
			int color = Integer.parseInt(sb.toString(), 16);
			return new ColorUIResource(color);
		} catch(NumberFormatException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	private static boolean isHex(char c) {
		return c=='1' || c=='2' || c=='3' || c=='4' || c=='5' || c=='6' || c=='7' || c=='8' || 
		c=='9' || c=='0' || c=='A' || c=='B' || c=='C' || c=='D' || c=='E' || c=='F' ||
		c=='a' || c=='b' || c=='c' || c=='d' || c=='e' || c=='f';
	}
}
