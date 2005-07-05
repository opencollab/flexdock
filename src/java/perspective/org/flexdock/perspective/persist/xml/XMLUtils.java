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

import org.apache.crimson.tree.TextNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created on 2005-06-27
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: XMLUtils.java,v 1.3 2005-07-05 14:53:28 marius Exp $
 */
public class XMLUtils {

    /**
     * This method provides a java 1.4 equivalent of the Element.setTextContent() that exists
     * under 1.5.
     */
    public static void setTextContent(Document document, Element elem, String text) {
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
    
    public static String getStrictTextContent(Element element) {
    	if(element==null)
    		return null;
    	
    	// get only the text nodes.  igore any other types
    	StringBuffer sb = new StringBuffer();
    	NodeList nodes = element.getChildNodes();
    	for(int i=0; i<nodes.getLength(); i++) {
    		Node node = nodes.item(i);
    		if(node instanceof TextNode) {
    			String txt = ((TextNode)node).getData();
    			sb.append(txt);
    		}
    	}
    	return sb.toString();
    }
    
//    public static String getTextContent(Element element) {
//        Node child = element.getFirstChild();
//        if (child != null) {
//            Node next = child.getNextSibling();
//            if (next == null) {
//                return element.hasTextContent(child) ? ((NodeImpl) child).getTextContent() : "";
//            }
//            if (fBufferStr == null){
//                fBufferStr = new StringBuffer();
//            }
//            else {
//                fBufferStr.setLength(0);
//            }
//            getTextContent(fBufferStr);
//            return fBufferStr.toString();
//        }
//        return "";
//    }
    
}
