/* Copyright (c) 2004 Christopher M Butler

Permission is hereby granted, free of charge, to any person obtaining a copy of 
this software and associated documentation files (the "Software"), to deal in the 
Software without restriction, including without limitation the rights to use, 
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the 
Software, and to permit persons to whom the Software is furnished to do so, subject 
to the following conditions:

The above copyright notice and this permission notice shall be included in all 
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. */
package org.flexdock.docking.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.swing.JSplitPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLConfigHandler {
	private static final String DIRECTIVE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
	private static final String DOCKING_CONFIG = "DockingConfiguration";
	private static final String DOCKING_PORT = "DockingPort";
	private static final String DOCKABLE = "Dockable";
	private static final String SPLIT_PANE = "SplitPane";
	private static final String TABBED_PANE = "TabbedPane";
	
	private static final String TAB_PLACEMENT_ATTRIB = "tabPlacement";
	private static final String VERTICAL_ATTRIB = "vertical";
	private static final String DIVIDER_LOCATION_ATTRIB = "divider";
	private static final String ID_ATTRIB = "id";
	

	public static DockingConfiguration load(String url) {
		try {
			return load(new URL(url));
		} catch(MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static DockingConfiguration load(File file) {
		try {
			InputStream in = new FileInputStream(file);
			DockingConfiguration config = load(in);
			in.close();
			return config;
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static DockingConfiguration load(URL url) {
		try {
			InputStream in = url.openStream();
			DockingConfiguration config = load(in);
			in.close();
			return config;
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static DockingConfiguration load(InputStream inStream) {
		Document doc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(inStream);
		} catch(IOException e) {
			e.printStackTrace();
		} catch(SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		if(doc==null)
			return null;
			
		Element root = doc.getDocumentElement();
		NodeList dockingPorts = root.getChildNodes();
		int len = dockingPorts.getLength();
		DockingConfiguration config = new DockingConfiguration();
		
		for(int i=0; i<len; i++) {
			Node port = dockingPorts.item(i);
			if(port instanceof Element && DOCKING_PORT.equals(port.getNodeName())) {
				DockedItem item = processDockingPort((Element)port);
				config.addDockingPort((DockingPortConfiguration)item);
			}
		}
		return config;
	}
	
	private static DockedItem processDockingPort(Element port) {
		DockedItem item = new DockingPortConfiguration(port.getAttribute(ID_ATTRIB));
		readElement(port, (DockingPortConfiguration)item);
		return item;
	}	
	
	private static DockedItem processSplitPane(Element node) {
		int orient = getSplitOrientation(node);
		int divLoc = getSplitDividerLocation(node);
		DockedItem item = new SplitConfiguration(orient, divLoc);
		readElement(node, (SplitConfiguration)item);
		return item;
	}
	
	private static DockedItem processTabbedPane(Element node) {
		DockedItem item = new TabbedConfiguration(getTabPlacement(node));
		readElement(node, (TabbedConfiguration)item);
		return item;
	}
	
	
	
	private static void readElement(Element port, DockingPortConfiguration config) {
		if(port.getChildNodes().getLength()==0)
			return;
			
		Node child = port.getFirstChild();
		while(child!=null && !(child instanceof Element))
			child = child.getNextSibling();
		if(child==null)
			return;

		DockedItem item = null;
		if(SPLIT_PANE.equals(child.getNodeName())) 
			item = processSplitPane((Element)child);
		else if(TABBED_PANE.equals(child.getNodeName())) 
			item = processTabbedPane((Element)child);
		else if(DOCKABLE.equals(child.getNodeName()))
			item = new DockableInstance(((Element)child).getAttribute(ID_ATTRIB));
		
		if(item!=null)
			config.setChild(item);
	}
	
	private static void readElement(Element split, SplitConfiguration config) {
		int len = split.getChildNodes().getLength();
		if(len==0)
			return;
			
		NodeList children = split.getChildNodes();
		boolean left = true;
		// there should only be two NamedNodes in this NodeList.  There may be more
		// child Nodes of different types (like #text), but we only care about the 
		// NamedNodes.
		for(int i=0; i<len; i++) {
			DockedItem item = null;
			Node child = children.item(i);
			if(DOCKING_PORT.equals(child.getNodeName())) 
				item = processDockingPort((Element)child);
			else if(DOCKABLE.equals(child.getNodeName())) 
				item = new DockableInstance(((Element)child).getAttribute(ID_ATTRIB));

			if(item!=null) {
				// the first encountered NamedNode will be the left one.
				if(left) {
					config.setLeftComponent(item);
					// any others encountered after here will be treated
					// as the 'right' node.
					left = false;
				}
				else
					config.setRightComponent(item);
			}
		}

	}
	
	private static void readElement(Element tabs, TabbedConfiguration config) {
		if(tabs.getChildNodes().getLength()==0)
			return;
			
		NodeList children = tabs.getChildNodes();
		int len = children.getLength();
		for(int i=0; i<len; i++) {
			DockedItem item = null;
			Node child = children.item(i);
			if(SPLIT_PANE.equals(child.getNodeName())) 
				item = processSplitPane((Element)child);
			else if(DOCKING_PORT.equals(child.getNodeName())) 
				item = processDockingPort((Element)child);
			else if(DOCKABLE.equals(child.getNodeName())) 
				item = new DockableInstance(((Element)child).getAttribute(ID_ATTRIB));
				
			if(item!=null)
				config.addTab(item);
		}
	}
	
	

	

	
	
	
	
	private static int getSplitOrientation(Element elem) {
		if("true".equals(elem.getAttribute(VERTICAL_ATTRIB)))
			return JSplitPane.VERTICAL_SPLIT;
		return JSplitPane.HORIZONTAL_SPLIT;
	}
	
	private static int getSplitDividerLocation(Element elem) {
		return getIntAttribute(DIVIDER_LOCATION_ATTRIB, elem);
	}
	
	private static int getTabPlacement(Element elem) {
		return getIntAttribute(TAB_PLACEMENT_ATTRIB, elem);
	}
	
	
	private static int getIntAttribute(String key, Element elem) {
		try {
			return Integer.parseInt(elem.getAttribute(key));
		} catch(NumberFormatException e) {
			return 0;
		}
	}
	
	
	
	
	
	
	
	
	
	
	public static void store(DockingConfiguration config, String filePath) {
		File f = new File(filePath);
		store(config, f);
	}
	
	public static void store(DockingConfiguration config, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			store(config, fos);
			fos.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void store(DockingConfiguration config, OutputStream outStream) {
		PrintStream out = outStream instanceof PrintStream? 
				(PrintStream)outStream: new PrintStream(outStream);
				
		out.println(DIRECTIVE + getXML(config));
	}
	
	public static String getXML(DockingConfiguration config) {
		StringBuffer xml = new StringBuffer("<").append(DOCKING_CONFIG).append(">");
		
		for(Iterator it=config.getDockingPorts().iterator(); it.hasNext();) {
			DockingPortConfiguration portConfig = (DockingPortConfiguration)it.next();
			xml.append(getXML(portConfig));
		}
		
		xml.append("</").append(DOCKING_CONFIG).append(">");
		
		// computer doesn't care about formatting, but we'll put this in for readability's sake.
		// just to be nice;-)	
		return format(xml.toString());
	}

	public static String getXML(DockingPortConfiguration config) {
		StringBuffer xml = new StringBuffer();
		xml.append("<").append(DOCKING_PORT).append(" id=\"");
		xml.append(config.getId()).append("\"");
		
		DockedItem child = config.getChild();
		if(child==null) {
			xml.append("/>");
			return xml.toString();
		}
		xml.append(">");
		xml.append(getXML(child));
		xml.append("</").append(DOCKING_PORT).append(">");
		return xml.toString();
	}
	
	public static String getXML(SplitConfiguration config) {
		StringBuffer xml = new StringBuffer();
		boolean vertical = config.getOrientation()==JSplitPane.VERTICAL_SPLIT;
		xml.append("<").append(SPLIT_PANE).append(" ");
		xml.append("").append(VERTICAL_ATTRIB).append("=\"").append(vertical).append("\" ");
		xml.append("").append(DIVIDER_LOCATION_ATTRIB).append("=\"").append(config.getDividerLocation()).append("\" ");
		xml.append(">");
		xml.append(getXML(config.getLeftComponent()));
		xml.append(getXML(config.getRightComponent()));
		xml.append("</").append(SPLIT_PANE).append(">");
		return xml.toString();
	}
	
	public static String getXML(TabbedConfiguration config) {
		StringBuffer xml = new StringBuffer();
		xml.append("<").append(TABBED_PANE).append(" ").append(TAB_PLACEMENT_ATTRIB).append("=\"").append(config.getTabPosition()).append("\">");
		for(Iterator it=config.getTabs().iterator(); it.hasNext();) {
			DockedItem item = (DockedItem)it.next();
			xml.append(getXML(item));
		}
		xml.append("</").append(TABBED_PANE).append(">");
		return xml.toString();
	}
	
	public static String getXML(DockableInstance dockable) {
		StringBuffer sb = new StringBuffer("<");
		sb.append(DOCKABLE+" ").append(ID_ATTRIB).append("=\"").append(dockable.getId()).append("\"/>");
		return sb.toString();
	}
	
	private static String getXML(DockedItem item) {
		if(item instanceof DockingPortConfiguration)
			return getXML((DockingPortConfiguration)item);
		if(item instanceof SplitConfiguration)
			return getXML((SplitConfiguration)item);
		if(item instanceof TabbedConfiguration)
			return getXML((TabbedConfiguration)item);
		if(item instanceof DockableInstance)
			return getXML((DockableInstance)item);
		return null;
	}
	


	private static String format(String xml) {
		// this isn't a very efficient algorithm for formatting XML, but it was easy to write 
		// and it gets the job done.  For our purposes, speed and efficiency isn't really
		// critical.  The XML generated by a docking configuration isn't very large, and its 
		// format is rather predictable.
		StringBuffer sb = new StringBuffer();
		int start = 0;
		int indentLevel = 0;

		// for each tag in the XML output, check the indentation
		for(int indx=xml.indexOf('<', start); indx!=-1; indx=xml.indexOf('<', start)) {
			int tagEnd = xml.indexOf('>', indx)+1;
			String tag = xml.substring(indx, tagEnd);
			
			// for the last tag in the document, we can just append and return
			if(tagEnd==xml.length()) {
				sb.append(tag);
				break;
			}
		
			// tags that don't have a slash are opening tags.  we'll want to increment the 
			// indentation level within them
			if(tag.indexOf('/')==-1) {
				indentLevel++;
			}
			else {
				// otherwise, we're dealing with a closing tag.  in this case, we'll have to check 
				// to see what the next tag is.
				int nextTagStart = xml.indexOf('<', tagEnd);
				int nextTagEnd = xml.indexOf('>', nextTagStart)+1;
				String nextTag = xml.substring(nextTagStart, nextTagEnd);
				int slash = nextTag.indexOf('/');
				// if the next tag is also a closing tag, then we'll either keep the same indentation
				// level, or decrement it.
				if(slash!=-1) {
					String tmp = nextTag.substring(0, slash).trim();
					// if the closing slash was at the beginning, then the element itself had content
					// and we're closing it out.  we'll decrement the indentation level here.
					if(tmp.length()==1)
						indentLevel--;
				}
			}
			// append a line-break and indentation after each tag. note that this only works because
			// we know there isn't any text content and all of our data for a docking configuration
			// resides within the actual tags themselves.  you wouldn't want to do this for a more 
			// generic XML document.
			sb.append(tag).append('\n').append(getIndents(indentLevel));
			start = indx+1;
		}
		return sb.toString();
	}
	
	private static String getIndents(int lvl) {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<lvl; i++)
			sb.append('\t');
		return sb.toString();
	}
	

	
}
