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

import java.awt.Point;

import org.flexdock.docking.state.DockingPath;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.MinimizationManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created on 2005-06-03
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: DockingStateSerializer.java,v 1.6 2005-06-23 16:21:30 winnetou25 Exp $
 */
public class DockingStateSerializer implements ISerializer {

    private final static String OPENED_STATE = "opened";
    private final static String CLOSED_STATE = "closed";
    private final static String MINIMIZED_STATE = "minimized";
    private final static String FLOATING_STATE = "floating";
    
    /**
     * @see org.flexdock.perspective.persist.xml.ISerializer#serialize(org.w3c.dom.Document, java.lang.Object)
     */
    public Element serialize(Document document, Object object) {
        DockingState dockingState = (DockingState) object;

        Element dockingStateElement = document.createElement(PersistenceConstants.DOCKING_STATE_ELEMENT_NAME);
        dockingStateElement.setAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_DOCKABLE_ID, dockingState.getDockableId());
        dockingStateElement.setAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_RELATIVE_PARENT_ID, dockingState.getRelativeParentId());
        dockingStateElement.setAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_REGION, dockingState.getRegion());
        dockingStateElement.setAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_SPLIT_RATIO, String.valueOf(dockingState.getSplitRatio()));
        dockingStateElement.setAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_LAYOUT_WEIGHT, String.valueOf(dockingState.getLayoutWeight()));

        if (dockingState.isFloating()) {
            dockingStateElement.setAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_FLOATING_GROUP_NAME, dockingState.getFloatingGroup());
        }
        if (dockingState.isMinimized()) {
            int edge = dockingState.getMinimizedConstraint();
            String presEdge = getPresentationDockbarEdge(edge);
            dockingStateElement.setAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_DOCK_BAR_EDGE, presEdge);
        }
        
        handleDockingState(dockingStateElement, dockingState);
        
        if (dockingState.hasCenterPoint()) {
            ISerializer pointSerializer = SerializerRegistry.getSerializer(Point.class);
            Element pointElement = pointSerializer.serialize(document, dockingState.getCenterPoint());
            dockingStateElement.appendChild(pointElement);
        }
        
        if (dockingState.hasDockingPath()) {
            ISerializer dockingPathSerializer = SerializerRegistry.getSerializer(DockingPath.class);
            Element dockingPathElement = dockingPathSerializer.serialize(document, dockingState.getPath());
            dockingStateElement.appendChild(dockingPathElement);
        }
        
        return dockingStateElement;
    }
    
    private void handleDockingState(Element dockingStateElement, DockingState dockingState) {
        if (dockingState.isMinimized()) {
            dockingStateElement.setAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_STATE, MINIMIZED_STATE);
        } else if (dockingState.isFloating()) {
            dockingStateElement.setAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_STATE, FLOATING_STATE);
        } else if (!dockingState.isDisplayed()) {
            dockingStateElement.setAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_STATE, CLOSED_STATE);
        } else {
            dockingStateElement.setAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_STATE, OPENED_STATE);
        }
    }
    
    private String getPresentationDockbarEdge(int dockbarEdge) {
        switch (dockbarEdge) {
        
        	case MinimizationManager.LEFT: return "left";
        	case MinimizationManager.BOTTOM: return "bottom";
        	case MinimizationManager.CENTER: return "center";
        	case MinimizationManager.RIGHT: return "right";
        	case MinimizationManager.TOP: return "top";
        	case MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT: return "unspecified";
        	
        	default: throw new RuntimeException("Unknown dockbarEdge");
        }
    }

}
