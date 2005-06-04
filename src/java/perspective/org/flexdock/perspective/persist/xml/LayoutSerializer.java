/*
 * Created on 2005-06-03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.persist.xml;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.LayoutNode;
import org.flexdock.perspective.Layout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
        
        LayoutNode layoutNode = layout.getRestorationLayout();
        ISerializer layoutNodeSerializer = SerializerRegistry.getSerializer(LayoutNode.class);
        Element layoutNodeElement = layoutNodeSerializer.serialize(document, layoutNode);

        layoutElement.appendChild(layoutNodeElement);
        
        return layoutElement;
    }

}
