/*
 * Created on Feb 27, 2005
 */
package org.flexdock.plaf.resources;



import org.flexdock.util.ResourceManager;

/**
 * @author Christopher Butler
 */
public class ImageResourceHandler extends ResourceHandler {

    public Object getResource(String url) {
        try {
            return ResourceManager.createImage(url);
        } catch(NullPointerException e) {
            System.err.println("Exception: " +e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
