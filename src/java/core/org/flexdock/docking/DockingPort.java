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
package org.flexdock.docking;

import java.awt.Component;
import java.awt.Point;

import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.event.DockingMonitor;
import org.flexdock.docking.props.DockingPortProps;

/**
 * This interface is designed to specify the API's required by <code>DockingManager</code> for placing
 * <code>Dockable</code> instances within a container.  A <code>DockingPort</code> is the parent 
 * container inside of which <code>Dockable</code> instances may be placed.
 * 
 * @author Chris Butler
 */
public interface DockingPort extends DockingListener, DockingMonitor {
	public static final String CENTER_REGION = "CENTER";
	public static final String EAST_REGION = "EAST";
	public static final String EMPTY_REGION = "EMPTY";
	public static final String NORTH_REGION = "NORTH";
	public static final String SOUTH_REGION = "SOUTH";
	public static final String UNKNOWN_REGION = "UNKNOWN";
	public static final String WEST_REGION = "WEST";
	public static final String INITIAL_TAB_POSITION = "DockingPort.init.tab.position";
	
	
	/**
	 * Returns a boolean indicating whether or not docking is allowed within the specified region.  Used
	 * by <code>DockingManager</code> during drag operations.
	 */	
	public boolean isDockingAllowed(String region, Component c);

	/**
	 * Removes all docked components from the <code>DockingPort</code>.
	 */		
	public void clear();
	
	/**
	 * Docks the specified Dockable in the specified region.  The <code>Dockable's</code>
	 * <code>getDockable()</code> component is used as the docking component.
	 */
	public boolean dock(Dockable dockable, String region);
	
	/**
	 * Docks the specified Component in the specified region.  <code>desc</code> is used as a 
	 * tab-title description in the event the specified component is docked into a tabbed pane.
	 * Returns <code>true</code> for success and <code>false</code> for failure.
	 */	
	public boolean dock(Component comp, String desc, String region);

	/**
	 * Returns a reference to the currently docked component.
	 */	
	public Component getDockedComponent();

	/**
	 * Returns a <code>String</code> identifier that is unique within a JVM instance, but persistent 
	 * across JVM instances.  This is used for configuration mangement, allowing the JVM to recognize
	 * a <code>DockingPort</code> instance within an application instance, persist the ID, and recall it
	 * in later application instances.  The ID should be unique within an appliation instance so that
	 * there are no collisions with other <code>DockingPort</code> instances, but it should also be 
	 * consistent from JVM to JVM so that the association between a <code>DockingPort</code> instance and
	 * its ID can be remembered from session to session.
	 */		
	public String getPersistentId();
	
	/**
	 * Indicates whether or not the specified component is a child component docked within the 
	 * <code>DockingPort</code>.
	 */		
	public boolean isParentDockingPort(Component comp);
	
	/**
	 * Removes the specified Component in from the <code>DockingPort</code>. 
	 * Returns <code>true</code> for success and <code>false</code> for failure.
	 */	
	public boolean undock(Component comp);
	
	public String getRegion(Point p);
	
	public Object getClientProperty(Object key);
	
	public void putClientProperty(Object key, Object value);
	
	public DockingPortProps getDockingProperties();
	
}
