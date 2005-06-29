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

import java.util.Iterator;
import java.util.List;

import org.flexdock.docking.state.DockingPath;
import org.flexdock.docking.state.tree.SplitNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created on 2005-06-23
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: DockingPathSerializer.java,v 1.4 2005-06-29 17:56:53 winnetou25 Exp $
 */
public class DockingPathSerializer implements ISerializer {
    
    public Element serialize(Document document, Object object) {
        DockingPath dockingPath = (DockingPath) object;
        
        Element dockingPathElement = document.createElement(PersistenceConstants.DOCKING_PATH_ELEMENT_NAME);
        dockingPathElement.setAttribute(PersistenceConstants.DOCKING_PATH_ATTRIBUTE_ROOT_PORT_ID, dockingPath.getRootPortId());

        if (dockingPath.getSiblingId() != null && !dockingPath.getSiblingId().equals("")) {
            dockingPathElement.setAttribute(PersistenceConstants.DOCKING_PATH_ATTRIBUTE_SIBLING_ID, dockingPath.getSiblingId());
        }

        dockingPathElement.setAttribute(PersistenceConstants.DOCKING_PATH_ATTRIBUTE_IS_TABBED, String.valueOf(dockingPath.isTabbed()));
        
        List splitNodes = dockingPath.getNodes();
        ISerializer splitNodeSerializer = SerializerRegistry.getSerializer(SplitNode.class);
        for (Iterator it = splitNodes.iterator(); it.hasNext();) {
            SplitNode splitNode = (SplitNode) it.next();
            Element splitNodeElement = splitNodeSerializer.serialize(document, splitNode);
            dockingPathElement.appendChild(splitNodeElement);
        }
        
        return dockingPathElement;
    }

    public Object deserialize(Document document, Element element) {
        //DockingPath dockingPath = DockingPath.create();

        return null;
    }
    
}
