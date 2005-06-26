/* 
 * Copyright (c) 2005 FlexDock Development Team. All rights reserved. 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in the 
 * Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE.
 */
package org.flexdock.perspective.persist.xml;

import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.persist.PerspectiveModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created on 2005-06-03
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: PerspectiveModelSerializer.java,v 1.2 2005-06-26 16:04:29 marius Exp $
 */
public class PerspectiveModelSerializer implements ISerializer {

    public Element serialize(Document document, Object object) {
        PerspectiveModel perspectiveModel = (PerspectiveModel) object;
        
        Element perspectiveModelElement = document.createElement(PersistenceConstants.PERSPECTIVE_MODEL_ELEMENT_NAME);
        
        Element currentPerspectiveElement = document.createElement(PersistenceConstants.CURRENT_PERSPECTIVE_ELEMENT_NAME);
        setTextContent(document, currentPerspectiveElement, perspectiveModel.getCurrentPerspective());
        perspectiveModelElement.appendChild(currentPerspectiveElement);
        
        Element defaultPerspectiveElement = document.createElement(PersistenceConstants.DEFAULT_PERSPECTIVE_ELEMENT_NAME);
        setTextContent(document, defaultPerspectiveElement, perspectiveModel.getDefaultPerspective());
        perspectiveModelElement.appendChild(defaultPerspectiveElement);

        ISerializer perspectiveSerializer = SerializerRegistry.getSerializer(Perspective.class);

        Element perspectivesElement = document.createElement(PersistenceConstants.PERSPECTIVES_ELEMENT_NAME);
        Perspective[] perspectives = perspectiveModel.getPerspectives();
        for (int i = 0; i < perspectives.length; i++) {
            Perspective perspective = perspectives[i];
            Element perspectiveElement = perspectiveSerializer.serialize(document, perspective);
            perspectivesElement.appendChild(perspectiveElement);
        }
        
        perspectiveModelElement.appendChild(currentPerspectiveElement);
        perspectiveModelElement.appendChild(defaultPerspectiveElement);
        perspectiveModelElement.appendChild(perspectivesElement);

        return perspectiveModelElement;
    }
    
    /**
     * This method provides a java 1.4 equivalent of the Element.setTextContent() that exists
     * under 1.5.
     */
    private void setTextContent(Document document, Element elem, String text) {
    	// remove any existing child nodes
    	while(elem.getChildNodes().getLength()>0) {
    		Node lastChild = elem.getLastChild();
    		elem.removeChild(lastChild);
    	}
    	
    	if(text==null)
    		return;
    	
    	// now insert the desired text content
    	Node textNode = document.createTextNode(text);
    	elem.appendChild(textNode);
    }

}
