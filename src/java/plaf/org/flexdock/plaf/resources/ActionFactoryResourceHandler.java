/*
 * Created on Apr 26, 2005
 */
package org.flexdock.plaf.resources;

import org.flexdock.plaf.resources.action.AbstractActionFactory;
import org.flexdock.plaf.resources.action.DefaultActionFactory;
import org.flexdock.util.Utilities;

/**
 * @author Christopher Butler
 */
public class ActionFactoryResourceHandler extends ResourceHandler {
    public static final String ACTION_RESOURCE_KEY = "action-resource";
    
	public Object getResource(String stringValue) {
		Object obj = Utilities.createInstance(stringValue);
		if(!(obj instanceof AbstractActionFactory))
			obj = new DefaultActionFactory();
		return (AbstractActionFactory)obj;
	}
}
