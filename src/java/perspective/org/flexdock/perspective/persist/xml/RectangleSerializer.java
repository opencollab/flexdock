/*
 * Created on 2005-06-03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.persist.xml;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RectangleSerializer implements ISerializer {
    
    /**
     * @see org.flexdock.perspective.persist.xml.ISerializer#serialize(org.w3c.dom.Document, java.lang.Object)
     */
    public Element serialize(Document document, Object object) {
        Rectangle rectangle = (Rectangle) object;

        Element rectangleElement = document.createElement(PersistenceConstants.RECTANGLE_ELEMENT_NAME);
        
        //TODO consider writing PersistenceDelegateAdapter
        ISerializer pointSerializer = SerializerRegistry.getSerializer(Point.class);
        //TODO consider writing PersistenceDelegateAdapter
        ISerializer dimensionSerializer = SerializerRegistry.getSerializer(Dimension.class);

        Element pointElement = pointSerializer.serialize(document, rectangle.getLocation());
        Element dimensionElement = dimensionSerializer.serialize(document, rectangle.getBounds());
        
        rectangleElement.appendChild(pointElement);
        rectangleElement.appendChild(dimensionElement);
        
        return rectangleElement;
    }
    
}
