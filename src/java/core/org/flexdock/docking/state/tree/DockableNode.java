/*
 * Created on May 28, 2005
 */
package org.flexdock.docking.state.tree;

import javax.swing.tree.MutableTreeNode;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;

/**
 * @author Christopher Butler
 */
public class DockableNode extends DockingNode {

    private String dockableId;

    public DockableNode() {
    }

    private DockableNode(String id) {
        dockableId = id;
    }

    public String getDockableId() {
        return dockableId;
    }

    public void setDockableId(String dockableId) {
        this.dockableId = dockableId;
    }

    public Dockable getDockable() {
        return DockingManager.getDockable(dockableId);
    }

    public void add(MutableTreeNode newChild) {
        // noop
    }

    public Object getDockingObject() {
        return getDockable();
    }

    protected DockingNode shallowClone() {
        return new DockableNode(dockableId);
    }

}
