/*
 * Created on Mar 10, 2005
 */
package org.flexdock.util;

import java.awt.Component;

import javax.swing.JRootPane;

import org.flexdock.docking.Dockable;

/**
 * This is a utility class for small, short-lived object instances used to find nested 
 * <code>Components</code>.  It models a relationship between nested <code>Components</code>
 * within an AWT <code>Container</code> hierarchy such that, given a starting 
 * <code>Component</code> from which to search, it will find two <code>Components</code>
 * nested within each other of specific class types.  The "deeper" component will be a 
 * descendent of the "parent" component.
 * <br/>
 * For example, given a <code>JTextField</code> within a content frame, the application
 * logic may need to check to see if the text field resides within a certain 
 * application-specific container set.  Perhaps all <code>JTables</code> embedded within
 * <code>JSplitPanes</code> are significant within the particular application.  The 
 * <code>find(Component searchSrc, Class childClass, Class parentClass)</code> method
 * on this class will be able to return a <code>NestedComponents</code> instance
 * indicating whether the specified text field resides within a <code>JTable</code> that is
 * embedded within a <code>JSplitPane</code>.
 * <br/>
 * Although perhaps a bit contrived, this example shows a generic use for this class.
 * The FlexDock framework itself has a particular interest in <code>Dockable</code> components
 * that are embedded within <code>DockingPorts</code>, especially during drag operations.
 * As a <code>Dockable</code> is dragged over an <code>DockingPort</code>, this class allows
 * the framework to determine with a single object instance any <code>Dockables</code> currently
 * embedded within the target <code>DockingPort</code>, starting with the deepest
 * <code>Component</code> at the current mouse point during the drag.
 * <br/>
 * This classes' member fields are <code>public</code> and may be both accessed and modified by 
 * external code as needed within their particular usage context.  This is by design for
 * ease of use within the FlexDock framework.  Consequently, instances of this class should only
 * be used for short-lived operations.  Since its member fields may be modified publicly, 
 * instances of this class should not be cached, nor should its member values be indexed as they
 * are subject to arbitrary changes over the long term.
 * 
 * @author Christopher Butler
 */
public class NestedComponents {
	public Component searchSrc;
   	public Component child;
   	public Component parent;
   	
   	/**
   	 * Creates and returns a new <code>NestedComponents</code> instance, searching the parent
   	 * <code>Container</code> hierarcy of the specified <code>searchSrc</code> for an 
   	 * ancestor of type <code>childClass</code> and a more senior ancestor of type 
   	 * <code>parentClass</code>.
   	 * <br/>
   	 * If either <code>searchSrc</code>, <code>childClass</code>, or <code>parentClass</code>
   	 * is <code>null</code>, this method returns <code>null</code>.
   	 * <br/>
   	 * If <code>searchSrc</code> is an instanceof <code>childClass</code>, then the 
   	 * <code>child</code> field on the resulting <code>NestedComponents</code> will be 
   	 * equal (==) to the <code>searchSrc</code> field.  If <code>searchSrc</code> is 
   	 * an instanceof <code>parentClass</code>, then the <code>parent</code> field on the 
   	 * resulting <code>NestedComponents</code> will be equal (==) to the <code>searchSrc</code> 
   	 * field.  If an instance of <code>parentClass</code> is found before <code>childClass</code>, 
   	 * this the resulting <code>NestedComponents</code> instance will have a <code>null</code>
   	 * <code>child</code> field.
   	 * 
   	 * @param searchSrc the <code>Component</code> from which to start searching for parent
   	 * <code>Containers</code>.
   	 * @param childClass the <code>Class</code> of the desired "child" <code>Component</code>
   	 * @param parentClass the <code>Class</code> of the desired "parent" <code>Component</code>
   	 * @return a new <code>NestedComponents</code> instance based upon the specified parameters.
   	 */
   	public static NestedComponents find(Component searchSrc, Class childClass, Class parentClass) {
   		if(searchSrc==null || childClass==null || parentClass==null)
   			return null;
   		
   	  	NestedComponents nest = new NestedComponents(searchSrc, null, null);

		Component c = searchSrc;
		while(c!=null && !(c instanceof JRootPane)) {
			if(nest.child==null && isInstanceOf(c, childClass)) {
				nest.child = c;
			}
			else  if(isParentContainer(c, parentClass)) {
				nest.parent = c;
				break;
			}
			c = c.getParent();
		}

    	return nest;
   	}
   	
   	private static boolean isParentContainer(Component c, Class parentClass) {
   		if(parentClass==RootWindow.class) {
   			return RootWindow.isValidRootContainer(c);
   		}
   		else
   			return parentClass.isAssignableFrom(c.getClass());
   	}
   	
   	private static boolean isInstanceOf(Object obj, Class clazz) {
   		if(clazz.isAssignableFrom(obj.getClass()))
   			return true;
   		
   		// special case
   		if(clazz==Dockable.class) {
   			return DockingUtility.isDockable(obj);
   		}
   		
   		return false;
   	}
   	
   	
	private NestedComponents(Component searchSrc, Component child, Component parent) {
		this.searchSrc = searchSrc;
		this.child = child;
		this.parent = parent;
	}
	
	/**
	 * Returns <code>true</code> if both <code>child</code> and <code>parent</code> fields
	 * are non-<code>null</code>; <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if both <code>child</code> and <code>parent</code> fields
	 * are non-<code>null</code>; <code>false</code> otherwise.
	 */
	public boolean isFull() {
		return child!=null && parent!=null;
	}
   	
	/**
	 * Overridden to match the <code>equals()</code> method.
	 * 
	 * @return a hash code value for this object.
	 * @see #equals(Object)
	 */
   	public int hashCode() {
   		int h = searchSrc.hashCode(); 
   		h +=child==null? 0: child.hashCode();
   		h+= parent==null? 0: parent.hashCode();
   		return h;
   	}
   	
   	/**
   	 * Returns <code>true</code> if the specified <code>Object</code> is a 
   	 * <code>NestedComponents</code> instance and all shares all of the same
   	 * field references (==) as this <code>NestedComponents</code> for field
   	 * <code>searchSrc</code>, <code>child</code>, and <code>parent</code>.
   	 * 
   	 * @param obj the <code>Object</code> to test for equality
   	 * @return <code>true</code> if the specified <code>Object</code> is "equal" to
   	 * this <code>NestedComponents</code> instance; <code>false</code> otherwise.
   	 */
   	public boolean equals(Object obj) {
   		if(!(obj instanceof NestedComponents))
   			return false;
   		
   		NestedComponents other = (NestedComponents)obj;
   		return searchSrc==other.searchSrc && child==other.child && parent==other.parent;
   	}
}
