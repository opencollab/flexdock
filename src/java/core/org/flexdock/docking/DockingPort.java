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
import java.util.Set;

import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.event.DockingMonitor;
import org.flexdock.docking.props.DockingPortProps;
import org.flexdock.docking.state.LayoutNode;

/**
 * This interface is designed to specify the API's required by <code>DockingManager</code> for placing
 * <code>Dockable</code> instances within a container.  A <code>DockingPort</code> is the parent 
 * container inside of which <code>Dockable</code> instances may be placed.
 * 
 * @author Chris Butler
 */
public interface DockingPort extends DockingListener, DockingMonitor {
	String INITIAL_TAB_POSITION = "DockingPort.init.tab.position";
	
	/**
	 * Returns a boolean indicating whether or not docking is allowed within the specified region.  Used
	 * by <code>DockingManager</code> during drag operations.
	 */	
	public boolean isDockingAllowed(Component comp, String region);

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
	 * Docks the specified Component in the specified region.
	 * Returns <code>true</code> for success and <code>false</code> for failure.
	 */	
	public boolean dock(Component comp, String region);

	/**
	 * Returns a reference to the currently docked component.
	 */	
	public Component getDockedComponent();
	
	/**
	 * Returns a reference to Dockable currently docked in the target region.  Returns null if there is
	 * no Dockable there.  If a tabbed layout is present, this method will return the Dockable in the
	 * currently selected tab. 
	 */	
	public Dockable getDockable(String region);

	/**
	 * Returns a reference to Component currently docked in the target region.  Returns null if there is
	 * no Component there.  If a tabbed layout is present, this method will return the Component in the
	 * currently selected tab. 
	 */	
	public Component getComponent(String region);

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
	 * Sets the persistent ID String to be returned by <code>getPersistentId()</code>.
	 * @param id the persistent ID to be applied.
	 * @see #getPersistentId()
	 */
	public void setPersistentId(String id);
	
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
	
	/**
	 * Returns the region of this <code>DockingPort</code> containing the coordinates within
	 * the specified <code>Point</code>.  The return value will be one of the regions specified
	 * in <code>org.flexdock.util.DockingConstants</code>, including <code>CENTER_REGION</code>, 
	 * <code>NORTH_REGION</code>, <code>SOUTH_REGION</code>, <code>EAST_REGION</code>, 
	 * <code>WEST_REGION</code>, or <code>UNKNOWN_REGION</code>.
	 * 
	 * @return the region containing the specified <code>Point</code>.
	 */
	public String getRegion(Point p);
	
	
    /**
     * Returns the value of the property with the specified key.  Only
     * properties added with <code>putClientProperty</code> will return
     * a non-<code>null</code> value.  
     * 
     * @param key the being queried
     * @return the value of this property or <code>null</code>
     * @see javax.swing.JComponent#getClientProperty(java.lang.Object)
     */
	public Object getClientProperty(Object key);
	
    /**
     * Adds an arbitrary key/value "client property" to this <code>DockingPort</code>.
     * <code>null</code> values are allowed.
     * @see javax.swing.JComponent#putClientProperty(java.lang.Object, java.lang.Object)
     */
	public void putClientProperty(Object key, Object value);
	
	
    /**
     * Returns a <code>DockingPortProps</code> instance associated with this <code>DockingPort</code>.
     * Developers implementing the <code>DockingPort</code> interface may or may not choose to 
     * provide their own <code>DockingPortProps</code> implementation for use with this method.
     * A default implementation is supplied by the framework and most <code>DockingPort</code> 
     * implementations, including all implementations provided by the framework, will return 
     * the default <code>DockingPortProps</code> via a call to 
     * <code>org.flexdock.docking.props.PropertyManager</code>.  Developers are encouraged to 
     * take advantage of this by calling <code>PropertyManager.getDockingPortProps(this)</code>.
     * 
     * @return the <code>DockingPortProps</code> associated with this <code>DockingPort</code>  
     * This method may not return a <code>null</code> reference.
     * @see org.flexdock.docking.props.DockingPortProps#
     * @see org.flexdock.docking.props.PropertyManager#getDockingPortProps(DockingPort)
     */
	public DockingPortProps getDockingProperties();
	
	/**
	 * Returns the <code>DockingStrategy</code> instance used by this <code>DockingPort</code>
	 * for docking operations.
	 * @see DockingStrategy
	 */
	public DockingStrategy getDockingStrategy();
	
	/**
	 * Returns a <code>Set</code> of all <code>Dockables</code> presently contained by 
	 * this <code>DockingPort</code>.
	 * 
	 * @return a <code>Set</code> of <code>Dockables</code> contained by this <code>DockingPort</code>.
	 * If the <code>DockingPort</code> contians no <code>Dockables</code>, and empty <code>Set</code>
	 * is returned.  This method may not return a <code>null</code> reference.
	 */
	public Set getDockables();

	/**
	 * Returns a boolean indicating whether or not this <code>DockingPort</code> is nested within
	 * another <code>DockingPort</code>.  If there are no other <code>DockingPorts</code> within
	 * this <code>DockingPort's</code> container ancestor hierarchy, then this method will return
	 * <code>true</code>.  Otherwise, this method will return <code>false</code>.  If the this 
	 * <code>DockingPort</code> is not validated and/or is not part of a container hierarchy, this
	 * method should return <code>true</code>. 
	 */
	public boolean isRoot();
	

	/**
	 * Examines a <code>LayoutNode</code> and constructs a corresponding component hierarchy
	 * to match the specified layout.  The supplied <code>LayoutNode</code> will contain metadata
	 * describing a layout of <code>Dockables</code>, including relative sizes, split 
	 * proportions, tabbing sequences, etc.  This <code>DockingPort</code> is reponsible for 
	 * constructing a valid <code>Dockable</code> component layout based upon the metadata 
	 * contained within the supplied <code>LayoutNode</code>
	 * 
	 * @param node the <code>LayoutNode</code> describing the layout to construct
	 * @see org.flexdock.docking.state.LayoutNode#
	 * @see #exportLayout()
	 */
	public void importLayout(LayoutNode node);
	
	/**
	 * Returns a <code>LayoutNode</code> containing metadata that describes the current layout
	 * contained within this <code>DockingPort</code>.  The returned <code>LayoutNode</code> 
	 * should be structured such that a subsequent call to <code>importLayout()</code> on the
	 * same <code>DockingPort</code> should construct a visual component layout identical to
	 * that which currently exists in this <code>DockingPort</code>
	 * 
	 * @return a <code>LayoutNode</code> representing the current layout state within this 
	 * <code>DockingPort</code>
	 * @see org.flexdock.docking.state.LayoutNode#
	 * @see #importLayout(LayoutNode)
	 */
	public LayoutNode exportLayout();
	
}
