/*
 * Created on May 28, 2005
 */
package org.flexdock.docking.state.tree;

import javax.swing.tree.TreeNode;

import org.flexdock.docking.DockingPort;
import org.flexdock.docking.DockingStrategy;


/**
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class DockingPortNode extends DockingNode {

    public Object getDockingObject() {
        TreeNode parent = getParent();
        if(!(parent instanceof SplitNode))
            return null;

        TreeNode grandParent = parent.getParent();
        if(!(grandParent instanceof DockingPortNode))
            return null;

        DockingPort superPort = (DockingPort)((DockingPortNode)grandParent).getUserObject();
        DockingStrategy strategy = superPort.getDockingStrategy();
        return strategy.createDockingPort(superPort);
    }

    public DockingPort getDockingPort() {
        return (DockingPort)getUserObject();
    }

    public boolean isSplit() {
        int cnt = getChildCount();
        if(cnt!=1)
            return false;

        return getChildAt(0) instanceof SplitNode;
    }

    protected DockingNode shallowClone() {
        return new DockingPortNode();
    }
}
