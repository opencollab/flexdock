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
    
    public static final String CLASSNAME = "classname";
    public static final String BACKGROUND_COLOR = "bgcolor";
	public static final String BACKGROUND_COLOR_ACTIVE = "bgcolor.active";
    
    public Object getResource(String stringValue) {
        PropertySet propertySet = Configurator.getProperties(stringValue, PAINTER_RESOURCE_KEY);
        PainterResource pr = createResource( propertySet);
        
        Painter p = createPainter( pr.getClassname());
        p.setPainterResource( pr);
		return p;
	}
    
    private static Painter createPainter(String className) {
		if(Configurator.isNull(className))
			return null;

		try {
			Class clazz = Class.forName(className);
			return (Painter)clazz.newInstance();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    
    private static PainterResource createResource(PropertySet properties) {
		PainterResource painterResource = new PainterResource();
		painterResource.setClassname( properties.getString(CLASSNAME));
		painterResource.setBgColor(properties.getColor(BACKGROUND_COLOR));
		painterResource.setBgColorActiv(properties.getColor(BACKGROUND_COLOR_ACTIVE));

		return painterResource;		
	}
}
