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

import javax.swing.tree.MutableTreeNode;

import org.flexdock.docking.state.LayoutNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created on 2005-06-27
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: AbstractLayoutNodeSerializer.java,v 1.1 2005-06-27 19:00:06 winnetou25 Exp $
 */
public abstract class AbstractLayoutNodeSerializer implements ISerializer {
    
    public Element serialize(Document document, Object object) {
        LayoutNode layoutNode = (LayoutNode) object;

        Element layoutNodeElement = getElement(document, object);
        Element childrenElement = serializeTreeNodeChildren(document, layoutNode);

        layoutNodeElement.appendChild(childrenElement);
        
        return layoutNodeElement;
    }
    
    private Element serializeTreeNodeChildren(Document document, MutableTreeNode treeNode) {
        ISerializer layoutNodeSerializer = SerializerRegistry.getSerializer(LayoutNode.class);
        Element childrenElement = document.createElement(PersistenceConstants.LAYOUT_NODE_ELEMENT_CHILDREN);
        for (int i=0; i<treeNode.getChildCount(); i++) {
            MutableTreeNode childTreeNode = (MutableTreeNode) treeNode.getChildAt(i);
            if (childTreeNode.isLeaf()) {
                Element element = layoutNodeSerializer.serialize(document, childTreeNode);
                childrenElement.appendChild(element);
            } else {
                Element element = serializeTreeNodeChildren(document, childTreeNode); //recursion
                childrenElement.appendChild(element);
            }
        }
        
        return childrenElement;
    }
    
    protected abstract Element getElement(Document document, Object o);
    
}
