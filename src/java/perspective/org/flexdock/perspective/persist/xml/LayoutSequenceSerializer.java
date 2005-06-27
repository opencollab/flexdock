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

import java.util.List;

import org.flexdock.docking.state.DockingState;
import org.flexdock.perspective.LayoutSequence;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created on 2005-06-03
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: LayoutSequenceSerializer.java,v 1.3 2005-06-27 17:32:53 winnetou25 Exp $
 */
public class LayoutSequenceSerializer implements ISerializer {
    
    /**
     * @see org.flexdock.perspective.persist.xml.ISerializer#serialize(org.w3c.dom.Document, java.lang.Object)
     */
    public Element serialize(Document document, Object object) {
        LayoutSequence layoutSequence = (LayoutSequence) object;
        
        Element layoutSequenceElement = document.createElement(PersistenceConstants.LAYOUT_SEQUENCE_ELEMENT_NAME);
        
        List dockingStates = layoutSequence.getDockingStates();
        for (int i = 0; i < dockingStates.size(); i++) {
            //TODO do we have to serialize whole DockingState object? No please no
            //we could only serialize some unique id but it seems that DockingState does
            //not have unique id, does it?
            DockingState dockingState = (DockingState) dockingStates.get(i);
            //dockableId should be unique within perspective
            //that it is, it is not possible for two dockables to be included in the same perspective?
            String dockableId = dockingState.getDockableId();
            //By using this dockableId we will have to find DockingState object.
            
            Element dockableElement = document.createElement(PersistenceConstants.LAYOUT_SEQUENCE_DOCKABLE_ELEMENT);
            dockableElement.setAttribute(PersistenceConstants.LAYOUT_SEQUENCE_DOCKABLE_ATTRIBUTE_ID, dockableId);
        
            layoutSequenceElement.appendChild(dockableElement);
        }
        
        return layoutSequenceElement;
    }
    
}
