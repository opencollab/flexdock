/*
 * Created on Apr 26, 2005
 */
package org.flexdock.plaf.resources;

import javax.swing.Action;

import org.flexdock.plaf.resources.action.DefaultAction;
import org.flexdock.util.Utilities;

/**
 * @author Christopher Butler
 */
public class ActionResourceHandler extends ResourceHandler {

    public Object getResource(String stringValue) {
        Object obj = Utilities.createInstance(stringValue);
        if(!(obj instanceof Action))
            obj = new DefaultAction();
        return obj;
    }
}
