/*
 * Created on 2005-06-03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.persist.xml;

import java.awt.Dimension;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DimensionSerializer implements ISerializer {

    /**
     * @see org.flexdock.perspective.persist.xml.ISerializer#serialize(org.w3c.dom.Document, java.lang.Object)
     */
    public Element serialize(Document document, Object object) {
        Dimension dimension = (Dimension) object;

        Element dimensionElement = document.createElement(PersistenceConstants.DIMENSION_ELEMENT_NAME);

        dimensionElement.setAttribute(PersistenceConstants.DIMENSION_ATTRIBUTE_HEIGHT, String.valueOf(dimension.height));
        dimensionElement.setAttribute(PersistenceConstants.DIMENSION_ATTRIBUTE_WIDTH, String.valueOf(dimension.width));

        return dimensionElement;
    }

}
