/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.plaf.resources;

import org.flexdock.util.ResourceManager;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ImageIconResourceHandler extends ResourceHandler {
	public Object getResource(String url) {
		try {
			return ResourceManager.createIcon(url);
		} catch(NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}
}
