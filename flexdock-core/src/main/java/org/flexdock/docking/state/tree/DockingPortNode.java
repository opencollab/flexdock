/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.flexdock.docking.state.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.flexdock.docking.DockingPort;
import org.flexdock.docking.DockingStrategy;


/**
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class DockingPortNode extends DockingNode {

    @Override
    public Object getDockingObject() {
        TreeNode parent = getParent();
        if(!(parent instanceof SplitNode)) {
            return null;
        }

        TreeNode grandParent = parent.getParent();
        if(!(grandParent instanceof DockingPortNode)) {
            return null;
        }

        DockingPort superPort = (DockingPort)((DefaultMutableTreeNode)grandParent).getUserObject();
        DockingStrategy strategy = superPort.getDockingStrategy();
        return strategy.createDockingPort(superPort);
    }

    public DockingPort getDockingPort() {
        return (DockingPort)getUserObject();
    }

    public boolean isSplit() {
        int cnt = getChildCount();
        if(cnt!=1) {
            return false;
        }

        return getChildAt(0) instanceof SplitNode;
    }

    @Override
    protected DockingNode shallowClone() {
        return new DockingPortNode();
    }
}
