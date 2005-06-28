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

import org.flexdock.docking.Dockable;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.FloatingGroup;
import org.flexdock.docking.state.LayoutNode;
import org.flexdock.perspective.Layout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created on 2005-06-03
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: LayoutSerializer.java,v 1.7 2005-06-28 23:00:29 winnetou25 Exp $
 */
public class LayoutSerializer implements ISerializer {
    
    /**
     * @see org.flexdock.perspective.persist.xml.ISerializer#serialize(org.w3c.dom.Document, java.lang.Object)
     */
    public Element serialize(Document document, Object object) {
        Layout layout = (Layout) object;
        
        Element layoutElement = document.createElement(PersistenceConstants.LAYOUT_ELEMENT_NAME);
        
        Dockable[] dockables = layout.getDockables();
        ISerializer dockingStateSerializer = SerializerRegistry.getSerializer(DockingState.class);
        for (int i = 0; i < dockables.length; i++) {
            Dockable dockable = dockables[i];
            DockingState dockingState = layout.getDockingState(dockable);
            Element dockingStateElement = dockingStateSerializer.serialize(document, dockingState);
            layoutElement.appendChild(dockingStateElement);
        }
        
        ISerializer floatingGroupSerializer = SerializerRegistry.getSerializer(FloatingGroup.class);
        for (int i = 0; i < dockables.length; i++) {
            Dockable dockable = dockables[i];
            if (layout.contains(dockable)) {
                FloatingGroup floatingGroup = layout.getGroup(dockable);
                if (floatingGroup != null) {
                    Element floatingGroupElement = floatingGroupSerializer.serialize(document, floatingGroup);
                    layoutElement.appendChild(floatingGroupElement);
                }
            }
        }
        
        LayoutNode layoutNode = layout.getRestorationLayout();
        if (layoutNode != null) {
            ISerializer layoutNodeSerializer = SerializerRegistry.getSerializer(LayoutNode.class);
            Element layoutNodeElement = layoutNodeSerializer.serialize(document, layoutNode);
            layoutElement.appendChild(layoutNodeElement);
        }
        
        return layoutElement;
    }

    public Object deserialize(Document document, Element element) {
        
        Layout layout = new Layout();
        NodeList dockingStateNodeList = element.getElementsByTagName(PersistenceConstants.DOCKING_STATE_ELEMENT_NAME);
        ISerializer dockingStateSerializer = SerializerRegistry.getSerializer(DockingState.class);
        for (int i = 0; i < dockingStateNodeList.getLength(); i++) {
            Node node = dockingStateNodeList.item(i);
            if (node instanceof Element) {
                Element dockingStateElement = (Element) node;
                DockingState dockingState = (DockingState) dockingStateSerializer.deserialize(document, dockingStateElement);
                String dockableId = dockingState.getDockableId();
                layout.setDockingState(dockableId, dockingState);
            }
        }
        
        return layout;
    }
    
}
