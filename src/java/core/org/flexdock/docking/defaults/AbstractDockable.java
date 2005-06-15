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
import org.flexdock.docking.props.DockableProps;
import org.flexdock.docking.props.PropertyManager;
import org.flexdock.util.SwingUtility;
import org.flexdock.util.Utilities;

/**
 * Provides a default implementation of the <code>Dockable</code> interface.  This class should be
 * extended by any application that wishes to make use of the <code>Dockable</code> interface without
 * the need for writing out an implementation for every method that isn't explicitly used.
 * 
 * @author Christopher Butler
 */
public abstract class AbstractDockable implements Dockable {
	private String persistentId;
	private ArrayList dockingListeners;
	private ArrayList dragListeners;
	private Hashtable clientProperties;
	private HashSet frameDragSources;
	
	/**
	 * Creates a new <code>AbstractDockable</code> instance.  This constructor is meant to be invoked
	 * by subclasses as it initializes the <code>Dockable's</code> persistent ID and drag sources.
	 * 
	 * @param id the persistent ID of the resulting <code>Dockable</code>
	 * @see Dockable#getPersistentId()
	 */
	public AbstractDockable(String id) {
		persistentId = id;
		dockingListeners = new ArrayList(2);
		dragListeners = new ArrayList();
		dragListeners.add(getComponent());
	}
	
	private Hashtable getInternalClientProperties() {
		if(clientProperties==null)
			clientProperties = new Hashtable(2);
		return clientProperties;
	}
	
	/**
	 * Returns the <code>Component</code> used to back this <code>Dockable</code> instance.
	 * 
	 * @return the <code>Component</code> used to back this <code>Dockable</code> instance.
	 * @see Dockable#getComponent()
	 */
	public abstract Component getComponent();

	/**
	 * Returns a <code>List</code> of <code>Components</code> used to initiate drag-to-dock operation.
	 * By default, the returned <code>List</code> contains the <code>Component</code> returned by
	 * <code>getComponent()</code>.
	 * 
	 * @return a <code>List</code> of <code>Components</code> used to initiate drag-to-dock operation.
	 * @see Dockable#getDragSources()
	 * @see #getComponent()
	 */
	public List getDragSources() {
		return dragListeners;
	}

	/**
	 * Returns the persistent ID of this <code>Dockable</code> instance provided when this object 
	 * was instantiated.
	 * 
	 * @return the persistent ID of this <code>Dockable</code>
	 * @see Dockable#getPersistentId()
	 * @see #AbstractDockable(String)
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
	 * Sets the <code>String</code> to be used for tab labels when this <code>Dockable</code> is 
	 * embedded within a tabbed layout.  <code>null</code> values are discouraged, but not illegal.
	 * 
	 * @param tabText the <code>String</code> to be used for tab labels when this <code>Dockable</code> is 
	 * embedded within a tabbed layout.
	 */
	public void setTabText(String tabText) {
		getDockingProperties().setDockableDesc(tabText);
	}
	
	/**
	 * Returns the <code>String</code> used for tab labels when this <code>Dockable</code> is 
	 * embedded within a tabbed layout.  It is possible for this method to return a <code>null</code>
	 * reference.
	 * 
	 * @return tabText the <code>String</code> used for tab labels when this <code>Dockable</code> is 
	 * embedded within a tabbed layout.
	 */
	public String getTabText() {
		return getDockingProperties().getDockableDesc();
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
     * Returns the value of the property with the specified key.  Only
     * properties added with <code>putClientProperty</code> will return
     * a non-<code>null</code> value.  If <code>key</code> is <code>null</code>, a 
     * <code>null</code> reference is returned.
     * <br/>
     * If the <code>Component</code> returned by <code>getComponent()</code> is an instance
     * of <code>JComponent</code>, then this method will dispatch to that <code>JComponent's</code>
     * <code>getClientProperty(Object, Object)</code> method.  Otherwise, this 
     * <code>Dockable</code> will provide its own internal mapping of client properties.
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
     * <code>Dockable</code> will provide its own internal mapping of client properties.
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
     * Returns a <code>DockableProps</code> instance associated with this <code>Dockable</code>.
     * This method returns the default implementation is supplied by the framework by invoking
     * <code>getDockableProps(Dockable dockable)</code> on 
     * <code>org.flexdock.docking.props.PropertyManager</code> and supplying an argument of 
     * <code>this</code>.
     * 
     * @return the <code>DockableProps</code> associated with this <code>Dockable</code>  This
     * method will not return a <code>null</code> reference.
     * @see org.flexdock.docking.props.DockableProps#
     * @see Dockable#getDockingProperties()
     * @see org.flexdock.docking.props.PropertyManager#getDockableProps(Dockable)
     */
	public DockableProps getDockingProperties() {
		return PropertyManager.getDockableProps(this);
	}
	
	/**
	 * Returns the <code>DockingPort</code> within which this <code>Dockable</code> is currently docked.  
	 * If not currently docked, this method will return <code>null</code>.
	 * <br/>
	 * This method defers processing to <code>getDockingPort(Dockable dockable)</code>, passing an 
	 * argument of <code>this</code>.  This <code>DockingPort</code> returned is based upon the 
	 * <code>Component</code> returned by this <code>Dockable's</code> abstract 
	 * <code>getComponent()</code> method.
	 * 
	 * @return the <code>DockingPort</code> within which this <code>Dockable</code> is currently docked.
	 * @see Dockable#getDockingPort()
	 * @see DockingManager#getDockingPort(Dockable)
	 */
	public DockingPort getDockingPort() {
		return DockingManager.getDockingPort(this);
	}
}
