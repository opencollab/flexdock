/*
 * Created on Feb 27, 2005
 */
package org.flexdock.plaf.resources;

import org.flexdock.util.ResourceManager;
import org.flexdock.logging.Log;

/**
 * @author Christopher Butler
 */
public class ImageResourceHandler extends ResourceHandler {
	public Object getResource(String url) {
		try {
			return ResourceManager.createImage(url);
		} catch(NullPointerException e) {
			Log.debug(e.getMessage(),e);
			return null;
		}
	}
}
