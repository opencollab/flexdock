/*
 * Created on 18.03.2005
 *
 */
package org.flexdock.view.plaf.resources;

import org.flexdock.view.ext.Painter;
import org.flexdock.view.ext.PainterResource;
import org.flexdock.view.plaf.Configurator;
import org.flexdock.view.plaf.PropertySet;

/**
 * @author Claudio Romano
 *
 */
public class PainterResourceHandler extends ResourceHandler {
    public static final String PAINTER_RESOURCE_KEY = "painter-resource";
    
    public Object getResource(String stringValue) {
        PropertySet propertySet = Configurator.getProperties(stringValue, PAINTER_RESOURCE_KEY);
        PainterResource pr = createResource( propertySet);
        
        Painter p = createPainter( pr.getImplClass());
        p.setPainterResource( pr);
		return p;
	}
    
    private static Painter createPainter(Class clazz) {
        
		if(clazz ==null)
			return null;
		
		try {
			return (Painter)clazz.newInstance();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    
    private static PainterResource createResource(PropertySet properties) {        
		PainterResource painterResource = new PainterResource();
		painterResource.setAll( properties);
		return painterResource;		
	}
}
