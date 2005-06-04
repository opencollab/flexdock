/*
 * Created on 2005-06-03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.persist.xml;

import org.flexdock.perspective.Layout;
import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PerspectiveSerializer implements ISerializer {

    /**
     * @see org.flexdock.perspective.persist.xml.ISerializer#serialize(java.lang.Object)
     */
    public Element serialize(Document document, Object object) {
        Perspective perspective = (Perspective) object;
        
        Element perspectiveElement = document.createElement(PersistenceConstants.PERSPECTIVE_ELEMENT_NAME);
        perspectiveElement.setAttribute(PersistenceConstants.PERSPECTIVE_ATTRIBUTE_ID, perspective.getPersistentId());
        perspectiveElement.setAttribute(PersistenceConstants.PERSPECTIVE_ATTRIBUTE_NAME, perspective.getName());
        
        ISerializer layoutSerializer = SerializerRegistry.getSerializer(Layout.class);
        Element layoutElement = layoutSerializer.serialize(document, perspective.getLayout());
        
        ISerializer layoutSequenceSerializer = SerializerRegistry.getSerializer(LayoutSequence.class);
        Element layoutSequenceElement = layoutSequenceSerializer.serialize(document, perspective.getInitialSequence());
        
        perspectiveElement.appendChild(layoutElement);
        perspectiveElement.appendChild(layoutSequenceElement);
        
        return perspectiveElement;
    }

}
