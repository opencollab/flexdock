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

import org.flexdock.perspective.Layout;
import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created on 2005-06-03
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: PerspectiveSerializer.java,v 1.3 2005-06-04 14:00:29 winnetou25 Exp $
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
