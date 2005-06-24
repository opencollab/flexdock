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

import javax.swing.JComponent;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.props.DockablePropertySet;
import org.flexdock.docking.props.PropertyManager;
import org.flexdock.util.SwingUtility;
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
 * serves as both the drag source and drag initiator.  Thus, <code>getComponent()</code> will return a 
 * reference to <code>'this'</code> and <code>getDragSources()</code> return a <code>List</code> containing
 * the same self-reference <code>Component</code>.
 * <p>
 * This class may be used by application code to enable docking capabilities on a given <code>Component</code>.
 * However, it is recommended that 
 * <code>DockingManager.registerDockable(Component evtSrc, String desc)</code> 
 * be used as a more automated, less invasive means of enabling docking on a component.  
 * <code>DockingManager.registerDockable(Component evtSrc, String desc)</code>
 * will automatically create a <code>DockableComponentWrapper</code> instance and register the required
 * drag listeners.
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
	 * persistent ID, and docking description.  This method is used to create <code>Dockable</code> instances for 
	 * simple <code>Components</code> where the drag source and drag initiator are the same 
	 * <code>Component</code>.
	 * <br/>
	 * If <code>src</code> or <code>id</code> are <code>null</code>, then this method returns a
	 * <code>null</code> reference.
	 * <br/>
	 * <code>src</code> will be the <code>Component</code> returned by invoking <code>getComponent()</code> on
	 * the resulting <code>Dockable</code> and will be included in the <code>List</code> returned by
	 * <code>getDragSources()</code>.  <code>id</code> will be the value returned by invoking
	 * <code>getPersistentId()</code> on the resulting <code>Dockable</code>.  <code>desc</code>
	 * may be used by the <code>Dockable</code> for descriptive purposes (such as tab-text in a 
	 * tabbed layout).  It is not recommended to supply a <code>null</code> value for <code>desc</code>, 
	 * but doing so is not illegal.
	 *
	 * @param src the source component
	 * @param id the persistent ID for the Dockable instance
	 * @param desc the docking description
	 * @return a new <code>DockableComponentWrapper</code> instance
	 * @see Dockable#getComponent()
	 * @see Dockable#getDragSources()
	 * @see Dockable#getPersistentId()
	 * @see DockingManager#registerDockable(Component, String)
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
	
	private Hashtable getInternalClientProperties() {
		if(clientProperties==null)
			clientProperties = new Hashtable(2);
		return clientProperties;
	}
	
	/**
	 * Returns the <code>Component</code> used to create this <code>DockableComponentWrapper</code>
	 * instance.
	 * 
	 * @return the <code>Component</code> used to create this <code>DockableComponentWrapper</code>
	 * instance.
	 * @see Dockable#getComponent()
	 * @see #create(Component, String, String)
	 */
	public Component getComponent() {
		return dragSrc;
	}

	/**
	 * Returns a <code>List</code> of <code>Components</code> used to initiate drag-to-dock operation.
	 * By default, the returned <code>List</code> contains the <code>Component</code> returned by
	 * <code>getComponent()</code>.
	 * 
	 * @return a <code>List</code> of <code>Components</code> used to initiate drag-to-dock operation.
	 * @see Dockable#getDragSources()
	 * @see #getComponent()
	 * @see #create(Component, String, String)
	 */
	public List getDragSources() {
		return dragListeners;
	}
	
	/**
	 * Returns the persistent ID of this <code>DockableComponentWrapper</code> instance provided 
	 * when this object was instantiated.
	 * 
	 * @return the persistent ID of this <code>DockableComponentWrapper</code>
	 * @see Dockable#getPersistentId()
	 * @see #create(Component, String, String)
	 */
	public String getPersistentId() {
		return persistentId;
	}
	
	/**
	 * Returns a <code>HashSet</code> of <code>Components</code> used as frame drag sources when this
	 * <code>Dockable</code> is floating in a non-decorated external dialog.  The <code>HashSet</code>
	 * returned by this method is initially empty.  Because it is mutable, however, new 
	 * <code>Components</code> may be added to it.
	 * 
	 * @return a <code>HashSet</code> of <code>Components</code> used as frame drag sources when this
	 * <code>Dockable</code> is floating in a non-decorated external dialog.
	 * @see Dockable#getFrameDragSources()
	 */
	public Set getFrameDragSources() {
		if(frameDragSources==null)
			frameDragSources = new HashSet();
		return frameDragSources;
	}
	
	/**
	 * Adds a <code>DockingListener</code> to observe docking events for this <code>Dockable</code>.
	 * <code>null</code> arguments are ignored.
	 * 
	 * @param listener the <code>DockingListener</code> to add to this <code>Dockable</code>.
	 * @see DockingMonitor#addDockingListener(DockingListener)
	 * @see #getDockingListeners()
	 * @see #removeDockingListener(DockingListener)
	 */
	public void addDockingListener(DockingListener listener) {
		if(listener!=null)
			dockingListeners.add(listener);
	}

	/**
	 * Returns an array of all <code>DockingListeners</code> added to this <code>Dockable</code>.
	 * If there are no listeners present for this <code>Dockable</code>, then a zero-length
	 * array is returned.
	 * 
	 * @return an array of all <code>DockingListeners</code> added to this <code>Dockable</code>.
	 * @see DockingMonitor#getDockingListeners()
	 * @see #addDockingListener(DockingListener)
	 * @see #removeDockingListener(DockingListener)
	 */
	public DockingListener[] getDockingListeners() {
		return (DockingListener[])dockingListeners.toArray(new DockingListener[0]);
	}

	/**
	 * Removes the specified <code>DockingListener</code> from this <code>Dockable</code>.
	 * If the specified <code>DockingListener</code> is <code>null</code>, or the listener
	 * has not previously been added to this <code>Dockable</code>, then no <code>Exception</code>
	 * is thrown and no action is taken.
	 * 
	 * @param the <code>DockingListener</code> to remove from this <code>Dockable</code>
	 * @see DockingMonitor#removeDockingListener(DockingListener)
	 * @see #addDockingListener(DockingListener)
	 * @see #getDockingListeners()
	 */
	public void removeDockingListener(DockingListener listener) {
		if(listener!=null)
			dockingListeners.remove(listener);
	}
	
	/**
	 * No operation.  Provided as a method stub to fulfull the <code>DockingListener<code> interface
	 * contract.
	 * 
	 * @param evt the <code>DockingEvent</code> to respond to.
	 * @see DockingListener#dockingCanceled(DockingEvent)
	 */
	public void dockingCanceled(DockingEvent evt) {
	}

	/**
	 * No operation.  Provided as a method stub to fulfull the <code>DockingListener<code> interface
	 * contract.
	 * 
	 * @param evt the <code>DockingEvent</code> to respond to.
	 * @see DockingListener#dockingComplete(DockingEvent)
	 */
	public void dockingComplete(DockingEvent evt) {
	}

	/**
	 * No operation.  Provided as a method stub to fulfull the <code>DockingListener<code> interface
	 * contract.
	 * 
	 * @param evt the <code>DockingEvent</code> to respond to.
	 * @see DockingListener#undockingComplete(DockingEvent)
	 */
	public void undockingComplete(DockingEvent evt) {
	}
	
	/**
	 * No operation.  Provided as a method stub to fulfull the <code>DockingListener<code> interface
	 * contract.
	 * 
	 * @param evt the <code>DockingEvent</code> to respond to.
	 * @see DockingListener#undockingStarted(DockingEvent)
	 */
	public void undockingStarted(DockingEvent evt) {
	}
	
	/**
	 * No operation.  Provided as a method stub to fulfull the <code>DockingListener<code> interface
	 * contract.
	 * 
	 * @param evt the <code>DockingEvent</code> to respond to.
	 * @see DockingListener#dragStarted(DockingEvent)
	 */
	public void dragStarted(DockingEvent evt) {
	}
	
	/**
	 * No operation.  Provided as a method stub to fulfull the <code>DockingListener<code> interface
	 * contract.
	 * 
	 * @param evt the <code>DockingEvent</code> to respond to.
	 * @see DockingListener#dropStarted(DockingEvent)
	 */
	public void dropStarted(DockingEvent evt) {
	}
	
    /**
     * Returns the value of the property with the specified key.  Only
     * properties added with <code>putClientProperty</code> will return
     * a non-<code>null</code> value.  If <code>key</code> is <code>null</code>, a 
     * <code>null</code> reference is returned.
     * <br/>
     * If the <code>Component</code> returned by <code>getComponent()</code> is an instance
     * of <code>JComponent</code>, then this method will dispatch to that <code>JComponent's</code>
     * <code>getClientProperty(Object, Object)</code> method.  Otherwise, this 
     * <code>DockableComponentWrapper</code> will provide its own internal mapping of client properties.
     * 
     * @param key the key that is being queried
     * @return the value of this property or <code>null</code>
     * @see Dockable#getClientProperty(Object)
     * @see javax.swing.JComponent#getClientProperty(java.lang.Object)
     */
	public Object getClientProperty(Object key) {
		if(key==null)
			return null;

		Component c = getComponent();
		if(c instanceof JComponent)
			return ((JComponent)c).getClientProperty(key);
		
		return getInternalClientProperties().get(key);
	}

    /**
     * Adds an arbitrary key/value "client property" to this <code>Dockable</code>.
     * <code>null</code> values are allowed.  If <code>key</code> is <code>null</code>, then
     * no action is taken.
     * <br/>
     * If the <code>Component</code> returned by <code>getComponent()</code> is an instance
     * of <code>JComponent</code>, then this method will dispatch to that <code>JComponent's</code>
     * <code>putClientProperty(Object, Object)</code> method.  Otherwise, this 
     * <code>DockableComponentWrapper</code> will provide its own internal mapping of client properties.
     * 
     * @param key the new client property key
     * @param value the new client property value; if <code>null</code> this method will remove 
     * the property
     * @see Dockable#putClientProperty(Object, Object)
     * @see javax.swing.JComponent#putClientProperty(java.lang.Object, java.lang.Object)
     */
	public void putClientProperty(Object key, Object value) {
		if(key==null)
			return;
		
		Component c = getComponent();
		if(c instanceof JComponent) {
			SwingUtility.putClientProperty(c, key, value);
		}
		else {
			Utilities.put(getInternalClientProperties(), key, value);
		}
	}

    /**
     * Returns a <code>DockablePropertySet</code> instance associated with this <code>Dockable</code>.
     * This method returns the default implementation supplied by the framework by invoking
     * <code>getDockablePropertySet(Dockable dockable)</code> on 
     * <code>org.flexdock.docking.props.PropertyManager</code> and supplying an argument of 
     * <code>this</code>.
     * 
     * @return the <code>DockablePropertySet</code> associated with this <code>Dockable</code>.  This
     * method will not return a <code>null</code> reference.
     * @see org.flexdock.docking.props.DockablePropertySet#
     * @see Dockable#getDockingProperties()
     * @see org.flexdock.docking.props.PropertyManager#getDockablePropertySet(Dockable)
     */
	public DockablePropertySet getDockingProperties() {
		return PropertyManager.getDockablePropertySet(this);
	}
	
	/**
	 * Returns the <code>DockingPort</code> within which this <code>Dockable</code> is currently docked.  
	 * If not currently docked, this method will return <code>null</code>.
	 * <br/>
	 * This method defers processing to <code>getDockingPort(Dockable dockable)</code>, passing an 
	 * argument of <code>this</code>.
	 * 
	 * @return the <code>DockingPort</code> within which this <code>Dockable</code> is currently docked.
	 * @see Dockable#getDockingPort()
	 * @see DockingManager#getDockingPort(Dockable)
	 */
	public DockingPort getDockingPort() {
		return DockingManager.getDockingPort(this);
	}
	
	/**
	 * Provides the default <code>Dockable</code> implementation of 
	 * <code>dock(Dockable dockable)</code> by calling and returning
	 * <code>DockingManager.dock(Dockable dockable, Dockable parent)</code>.
	 * <code>'this'</code> is passed as the <code>parent</code> parameter.
	 * 
     * @param dockable the <code>Dockable</code> to dock relative to this <code>Dockable</code>
     * @return <code>true</code> if the docking operation was successful; <code>false</code>
     * otherwise.
     * @see Dockable#dock(Dockable)
     * @see DockingManager#dock(Dockable, Dockable) 
	 */
	public boolean dock(Dockable dockable) {
		return DockingManager.dock(dockable, this);
	}
	
	/**
	 * Provides the default <code>Dockable</code> implementation of 
	 * <code>dock(Dockable dockable, String relativeRegion)</code> by calling and returning
	 * <code>DockingManager.dock(Dockable dockable, Dockable parent, String region)</code>.
	 * <code>'this'</code> is passed as the <code>parent</code> parameter.
	 * 
     * @param dockable the <code>Dockable</code> to dock relative to this <code>Dockable</code>
     * @param relativeRegion the docking region into which to dock the specified <code>Dockable</code>
     * @return <code>true</code> if the docking operation was successful; <code>false</code>
     * otherwise.
     * @see Dockable#dock(Dockable, String)
     * @see DockingManager#dock(Dockable, Dockable, String) 
	 */
	public boolean dock(Dockable dockable, String relativeRegion) {
		return DockingManager.dock(dockable, this, relativeRegion);
	}
	
	/**
	 * Provides the default <code>Dockable</code> implementation of 
	 * <code>dock(Dockable dockable, String relativeRegion, float ratio)</code> by calling 
	 * and returning 
	 * <code>DockingManager.dock(Dockable dockable, Dockable parent, String region, float proportion)</code>.
	 * <code>'this'</code> is passed as the <code>parent</code> parameter.
	 * 
     * @param dockable the <code>Dockable</code> to dock relative to this <code>Dockable</code>
     * @param relativeRegion the docking region into which to dock the specified <code>Dockable</code>
     * @param ratio the proportion of available space in the resulting layout to allot to the
     * new sibling <code>Dockable</code>.
     * @return <code>true</code> if the docking operation was successful; <code>false</code>
     * otherwise.
     * @see DockingManager#dock(Dockable, Dockable, String, float) 
	 */
	public boolean dock(Dockable dockable, String relativeRegion, float ratio) {
		return DockingManager.dock(dockable, this, relativeRegion, ratio);
	}
}
