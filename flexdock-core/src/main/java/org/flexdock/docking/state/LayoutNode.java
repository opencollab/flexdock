/*
 * Created on May 28, 2005
 */
package org.flexdock.docking.state;

import java.io.Serializable;

import javax.swing.tree.MutableTreeNode;

/**
 * @author Christopher Butler
 */
public interface LayoutNode extends MutableTreeNode, Cloneable, Serializable {

    Object getUserObject();

    Object getDockingObject();

    void add(MutableTreeNode child);

    Object clone();

}
