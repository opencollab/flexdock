/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.resources;

import java.util.HashMap;
import java.util.Iterator;

import org.flexdock.windowing.plaf.Configurator;
import org.flexdock.windowing.plaf.XMLConstants;
import org.w3c.dom.Element;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResourceHandlerFactory implements XMLConstants {
	private static final HashMap resourceHandlers = loadResourceHandlers();
	
	public static ResourceHandler getResourceHandler(String handlerName) {
		return (ResourceHandler)resourceHandlers.get(handlerName);
	}
	
	private static HashMap loadResourceHandlers() {
		HashMap elements = Configurator.getNamedElementsByTagName(HANDLER_KEY);
		HashMap handlers = new HashMap(elements.size());

		for(Iterator it=elements.keySet().iterator(); it.hasNext();) {
			Element elem = (Element)it.next();
			String key = elem.getAttribute(NAME_KEY);
			String className = elem.getAttribute(VALUE_KEY);
			ResourceHandler handler = createResourceHandler(className);
			if(handler!=null)
				handlers.put(key, handler);
		}
		return handlers;
	}
	
	private static ResourceHandler createResourceHandler(String className) {
		if(Configurator.isNull(className))
			return null;

		try {
			Class clazz = Class.forName(className);
			return (ResourceHandler)clazz.newInstance();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
