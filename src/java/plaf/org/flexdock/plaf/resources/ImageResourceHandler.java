/*
 * Created on Feb 27, 2005
 */
package org.flexdock.plaf.resources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flexdock.util.ResourceManager;

/**
 * @author Christopher Butler
 */
public class ImageResourceHandler extends ResourceHandler {
    private static Log log = LogFactory.getLog(ImageResourceHandler.class);
    
	public Object getResource(String url) {
		try {
			return ResourceManager.createImage(url);
		} catch(NullPointerException e) {
			log.debug(e.getMessage(), e);
			return null;
		}
	}
}
