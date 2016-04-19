/*
 * Created on May 28, 2005
 */
package org.flexdock.docking.state.tree;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.state.LayoutNode;

/**
 * @author Christopher Butler
 */
public abstract class DockingNode extends DefaultMutableTreeNode implements LayoutNode, DockingConstants {

    public Object getUserObject() {
        Object obj = super.getUserObject();
        if(obj==null) {
            obj = getDockingObject();
            setUserObject(obj);
        }
        return obj;
    }

    public abstract Object getDockingObject();

    protected abstract DockingNode shallowClone();

    public Object clone() {
        return deepClone();
    }

    public DockingNode deepClone() {
        DockingNode clone = shallowClone();
        for(Enumeration en=children(); en.hasMoreElements();) {
            DockingNode child = (DockingNode)en.nextElement();
            clone.add(child.deepClone());
        }
        return clone;
    }

}
