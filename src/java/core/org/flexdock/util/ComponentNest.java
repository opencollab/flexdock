/*
 * Created on Mar 10, 2005
 */
package org.flexdock.util;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JRootPane;

import org.flexdock.docking.Dockable;

/**
 * @author cb8167
 */
public class ComponentNest {
	public Component searchSrc;
   	public Component child;
   	public Component parent;
   	
   	public static ComponentNest find(Component searchSrc, Class childClass, Class parentClass) {
   		if(searchSrc==null || childClass==null || parentClass==null)
   			return null;
   		
   	  	ComponentNest nest = new ComponentNest(searchSrc, null, null);

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
   		if(clazz==Dockable.class || obj instanceof JComponent) {
   			return ((JComponent)obj).getClientProperty(Dockable.DOCKABLE_INDICATOR)==Boolean.TRUE;
   		}
   		
   		return false;
   	}
   	
   	
	private ComponentNest(Component searchSrc, Component child, Component parent) {
		this.searchSrc = searchSrc;
		this.child = child;
		this.parent = parent;
	}
	
	public boolean isFull() {
		return child!=null && parent!=null;
	}
   	
   	public int hashCode() {
   		int h = searchSrc.hashCode(); 
   		h +=child==null? 0: child.hashCode();
   		h+= parent==null? 0: parent.hashCode();
   		return h;
   	}
   	
   	public boolean equals(Object obj) {
   		if(!(obj instanceof ComponentNest))
   			return false;
   		
   		ComponentNest other = (ComponentNest)obj;
   		return searchSrc==other.searchSrc && child==other.child && parent==other.parent;
   	}
}
