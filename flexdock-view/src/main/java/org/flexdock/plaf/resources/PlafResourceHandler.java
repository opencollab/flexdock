/*
 * Created on Feb 27, 2005
 */
package org.flexdock.plaf.resources;

import javax.swing.UIManager;

/**
 * @author Christopher Butler
 */
public class PlafResourceHandler extends ResourceHandler {
    public Object getResource(String stringValue) {
        return UIManager.getDefaults().get(stringValue);
    }
}
