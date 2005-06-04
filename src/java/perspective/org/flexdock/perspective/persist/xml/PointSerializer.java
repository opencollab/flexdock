/*
 * Created on 2005-06-03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.persist.xml;

import java.awt.Point;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PointSerializer implements ISerializer {

    /**
     * @see org.flexdock.perspective.persist.xml.ISerializer#serialize(org.w3c.dom.Document, java.lang.Object)
     */
    public Element serialize(Document document, Object object) {
        Point point = (Point) object;

        Element pointElement = document.createElement(PersistenceConstants.POINT_ELEMENT_NAME);
        pointElement.setAttribute(PersistenceConstants.POINT_ATTRIBUTE_X, String.valueOf(point.x));
        pointElement.setAttribute(PersistenceConstants.POINT_ATTRIBUTE_Y, String.valueOf(point.y));
        
        return pointElement;
    }

}
