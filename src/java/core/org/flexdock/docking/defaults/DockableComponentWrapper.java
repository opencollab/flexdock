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
package org.flexdock.docking.defaults;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.props.DockableProps;
import org.flexdock.docking.props.PropertyManager;
import org.flexdock.util.Utilities;

/**
 * This class models a <code>Dockable</code> implementation for wrapping a <code>Component</code>.  It is 
 * essentially the simplest means to turning a generic <code>Component</code> into a <code>Dockable</code>
 * instance.  Compound <code>Dockables</code> may have separate child components that are responsible for 
 * drag initiation, whereas another component is the actual drag source.  This is shown in the manner that a  
 * <code>JInternalFrame</code> would be a draggable component, while the frame's title pane is the actual drag 
 * initiator.
 * <p>
 * The class, conversely, deals with the <i>simple</i> case, where a <code>Component</code> itself must be 
 * docking-enabled.  <code>DockableComponentWrapper</code> wraps a <code>Component</code> and implements the
 * <code>Dockable</code> interface.  Since the <code>Component</code> itself is being docking-enabled, it
 * serves as both the drag source and drag initiator.  Thus, <code>getDockable()</code> and 
 * <code>getInitiator()</code> return a reference to the same wrapped <code>Component</code> object.
 * <p>
 * This class may be used by application code to enable docking capabilities on a given <code>Component</code>.
 * However, it is recommended that 
 * <code>DockingManager.registerDockable(Component evtSrc, String desc, boolean allowResize)</code> 
 * be used as a more automated, less invasive means of enabling docking on a component.  
 * <code>DockingManager.registerDockable(Component evtSrc, String desc, boolean allowResize)</code>
 * will automatically create a <code>DockableComponentWrapper</code> instance and register the required
 * MouseMotionListeners.
 *
 * @author Chris Butler 
 */
public class DockableComponentWrapper implements Dockable {
	private Component dragSrc;
	private String persistentId;
	private ArrayList dockingListeners;
	private ArrayList dragListeners;
	private Hashtable clientProperties;
	private HashSet frameDragSources;

	/**
	 * Creates a <code>DockableComponentWrapper</code> instance using the specified source component, 
	 * docking description, and resizing policy.  If <code>src</code> is null, this method returns 
	 * <code>null</code>.  This is used to create <code>Dockable</code> instances for simple Components
	 * where the drag source and drag initiator are the same Component.
	 *
	 * @param src the source component
	 * @param id the persistent ID for the Dockable instance
	 * @param desc the docking description
	 * @param resizable the resizing policy
	 */
	public static DockableComponentWrapper create(Component src, String id, String desc) {
		if(src==null || id==null)
			return null;
			
		return new DockableComponentWrapper(src, id, desc);
	}

	/**
	 * @param src
	 * @param id
	 * @param desc
	 * @param resizable
	 */
	private DockableComponentWrapper(Component src, String id, String desc) {
		dragSrc = src;
		getDockingProperties().setDockableDesc(desc);
		persistentId = id;
		
		dockingListeners = new ArrayList(0);
		dragListeners = new ArrayList(1);
		dragListeners.add(getComponent());
	}
	
	private Hashtable getClientProperties() {
		if(clientProperties==null)
			clientProperties = new Hashtable(2);
		return clientProperties;
	}
	
	/**
	 * @see org.flexdock.docking.Dockable#getComponent()
	 */
	public Component getComponent() {
		return dragSrc;
	}

	/**
	 * @see org.flexdock.docking.Dockable#dockingCanceled()
	 */
	public void dockingCanceled() {
	}

	/**
	 * @see org.flexdock.docking.Dockable#dockingCompleted()
	 */
	public void dockingCompleted() {
	}

	/**
	 * @see org.flexdock.docking.Dockable#getInitiator()
	 */
	public List getDragSources() {
		return dragListeners;
	}
	
	/**
	 * @see org.flexdock.docking.Dockable#getPersistentId()
	 */
	public String getPersistentId() {
		return persistentId;
	}
	
	

	public void addDockingListener(DockingListener listener) {
		dockingListeners.add(listener);
	}

	public DockingListener[] getDockingListeners() {
		return (DockingListener[])dockingListeners.toArray(new DockingListener[0]);
	}

	public void removeDockingListener(DockingListener listener) {
		dockingListeners.remove(listener);
	}
	
	public void dockingCanceled(DockingEvent evt) {
	}

	public void dockingComplete(DockingEvent evt) {
	}

	/**
	 * @see org.flexdock.docking.event.DockingListener#undockingComplete(org.flexdock.docking.event.DockingEvent)
	 */
	public void undockingComplete(DockingEvent evt) {
	}
	
	public void undockingStarted(DockingEvent evt) {
	}
	
	public void dragStarted(DockingEvent evt) {
	}
	
	public void dropStarted(DockingEvent evt) {
	}
	
	public Object getClientProperty(Object key) {
		return getClientProperties().get(key);
	}

	public void putClientProperty(Object key, Object value) {
		Utilities.put(getClientProperties(), key, value);
	}

	public DockableProps getDockingProperties() {
		return PropertyManager.getDockableProps(this);
	}
	
	public DockingPort getDockingPort() {
		return DockingManager.getDockingPort(this);
	}
	
	public Dockable getSibling(String region) {
		return DefaultDockingStrategy.getSibling(this, region);
	}
	
	public Set getFrameDragSources() {
		if(frameDragSources==null)
			frameDragSources = new HashSet();
		return frameDragSources;
	}
}
