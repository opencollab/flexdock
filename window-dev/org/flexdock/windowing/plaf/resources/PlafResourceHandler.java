/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.resources;

import javax.swing.UIManager;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlafResourceHandler extends ResourceHandler {
	public Object getResource(String stringValue) {
		return UIManager.getDefaults().get(stringValue);
	}
}
