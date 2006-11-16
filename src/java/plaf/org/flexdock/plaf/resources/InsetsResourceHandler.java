/*
 * Created on 23.03.2005
 */
package org.flexdock.plaf.resources;

import java.awt.Insets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Claudio Romano
 */
public class InsetsResourceHandler extends ResourceHandler {
    private static Log log = LogFactory.getLog(InsetsResourceHandler.class);
    
    public Object getResource(String data) {
//      pattern should be "top, left, bottom, right"
		String[] args = getArgs(data);
		int top = getInt(args, 0);
		int left = getInt(args, 1);
		int bottom = getInt(args, 2);
		int right = getInt(args, 3);
		
		
		return new Insets(top, left, bottom, right);
	}
	
	private int getInt(String args[], int index) {
		return args.length>index? getInt(args[index]): 0;
	}
	
	private int getInt(String data) {
		try {
			return Integer.parseInt(data);
		} catch(Exception e) {
			log.debug(e.getMessage(), e);
			return 0;
		}
	}
}
