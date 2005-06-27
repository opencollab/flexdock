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

import org.flexdock.docking.state.tree.SplitNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created on 2005-06-23
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: SplitNodeSerializer.java,v 1.2 2005-06-27 15:08:11 winnetou25 Exp $
 */
public class SplitNodeSerializer implements ISerializer {

    public Element serialize(Document document, Object object) {
        SplitNode splitNode = (SplitNode) object;

        Element splitNodeElement = document.createElement(PersistenceConstants.SPLIT_NODE_ELEMENT_NAME);
        splitNodeElement.setAttribute(PersistenceConstants.SPLIT_NODE_ATTRIBUTE_SIBLING_ID, splitNode.getSiblingId());
        splitNodeElement.setAttribute(PersistenceConstants.SPLIT_NODE_ATTRIBUTE_ORIENTATION, splitNode.getOrientationDesc());
        splitNodeElement.setAttribute(PersistenceConstants.SPLIT_NODE_ATTRIBUTE_REGION, splitNode.getRegionDesc());
        splitNodeElement.setAttribute(PersistenceConstants.SPLIT_NODE_ATTRIBUTE_PERCENTAGE, String.valueOf(splitNode.getPercentage()));

        if (splitNode.getDockingRegion() != null) {
            splitNodeElement.setAttribute(PersistenceConstants.SPLIT_NODE_ATTRIBUTE_DOCKING_REGION, splitNode.getDockingRegion());
        }

        return splitNodeElement;
    }

}
