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
public class ImageIconResourceHandler extends ResourceHandler {
    private static Log log = LogFactory.getLog(ImageIconResourceHandler.class);

    public Object getResource(String url) {
        try {
            return ResourceManager.createIcon(url);
        } catch(NullPointerException e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }
}
