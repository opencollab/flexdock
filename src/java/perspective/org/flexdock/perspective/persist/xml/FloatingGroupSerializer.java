/*
 * Created on 2005-06-03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.persist.xml;

import org.flexdock.docking.state.FloatingGroup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FloatingGroupSerializer implements ISerializer {

    /**
     * @see org.flexdock.perspective.persist.xml.ISerializer#serialize(org.w3c.dom.Document, java.lang.Object)
     */
    public Element serialize(Document document, Object object) {
        FloatingGroup floatingGroup = (FloatingGroup) object;
        
        Element floatingGroupElement = document.createElement(PersistenceConstants.FLOATING_GROUP_ELEMENT_NAME);
        floatingGroupElement.setAttribute(PersistenceConstants.FLOATING_GROUP_ATTRIBUTE_NAME, floatingGroup.getName());
        
        return floatingGroupElement;
    }

}
