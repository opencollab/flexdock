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
import java.util.List;

import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.event.DockingMonitor;
import org.flexdock.docking.props.DockableProps;


/**
 * This interface is designed to specify the API's required by <code>DockingManager</code> and 
 * <code>DockingPort</code> for dealing with dockable components in a drag-n-drop fashion.  A
 * <code>Dockable</code> is the child component that is docked into a <code>DockingPort</code>.
 * 
 * @author Chris Butler
 */
public interface Dockable extends DockingListener, DockingMonitor {
	/**
	 * Returns the Component that is to be dragged and docked.  This may or may not be the same as the 
	 * Component returned by <code>getInitiator()</code>.
	 */
	public Component getDockable();
	
	/**
	 * Returns the Component that is the event source of drag operations.  This may or may not be the same 
	 * as the Component returned by <code>getDockable()</code>.
	 */
	public List getDragSources();

	/**
	 * Returns a <code>String</code> identifier that is unique within a JVM instance, but persistent 
	 * across JVM instances.  This is used for configuration mangement, allowing the JVM to recognize
	 * a <code>Dockable</code> instance within an application instance, persist the ID, and recall it
	 * in later application instances.  The ID should be unique within an appliation instance so that
	 * there are no collisions with other <code>Dockable</code> instances, but it should also be 
	 * consistent from JVM to JVM so that the association between a <code>Dockable</code> instance and
	 * its ID can be remembered from session to session.
	 */		
	public String getPersistentId();
	
	public Object getClientProperty(Object key);
	
	public void putClientProperty(Object key, Object value);
	
	public DockableProps getDockingProperties();
}
