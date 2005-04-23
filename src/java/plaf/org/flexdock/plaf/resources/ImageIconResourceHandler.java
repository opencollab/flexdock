/*
 * Created on Feb 27, 2005
 */
package org.flexdock.plaf.resources;

import org.flexdock.util.ResourceManager;

/**
 * @author Christopher Butler
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
