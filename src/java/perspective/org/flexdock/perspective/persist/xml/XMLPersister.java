/*
 * Created on 2005-06-03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.persist.xml;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.LayoutNode;
import org.flexdock.perspective.Layout;
import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.persist.Persister;
import org.flexdock.perspective.persist.PerspectiveInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class XMLPersister implements Persister {
    
    /**
     * @see org.flexdock.perspective.persist.Persister#store(java.lang.String, org.flexdock.perspective.persist.PerspectiveInfo)
     */
    public boolean store(String appKey, PerspectiveInfo info) throws IOException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("flex-dock-perspectives");
            Element currentPerspectiveElement = document.createElement("current-perspective");
            
            document.appendChild(rootElement);
            
            return false;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * @see org.flexdock.perspective.persist.Persister#load(java.lang.String)
     */
    public PerspectiveInfo load(String appKey) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    
    private void registerSerializers() {
        SerializerRegistry.registerSerializer(Perspective.class, new PerspectiveSerializer());
        SerializerRegistry.registerSerializer(Layout.class, new LayoutSerializer());
        SerializerRegistry.registerSerializer(LayoutNode.class, new LayoutNodeSerializer());
        SerializerRegistry.registerSerializer(LayoutSequence.class, new LayoutSequenceSerializer());
        SerializerRegistry.registerSerializer(DockingState.class, new DockingStateSerializer());
        SerializerRegistry.registerSerializer(Point.class, new PointSerializer());
        SerializerRegistry.registerSerializer(Dimension.class, new DimensionSerializer());
        SerializerRegistry.registerSerializer(Rectangle.class, new RectangleSerializer());
    }
    
}
