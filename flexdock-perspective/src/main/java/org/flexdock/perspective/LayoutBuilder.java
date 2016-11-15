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
package org.flexdock.perspective;

import java.awt.Component;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DockingSplitPane;
import org.flexdock.docking.state.LayoutNode;
import org.flexdock.docking.state.tree.DockableNode;
import org.flexdock.docking.state.tree.DockingPortNode;
import org.flexdock.docking.state.tree.SplitNode;
import org.flexdock.util.SwingUtility;

/**
 * @author Christopher Butler
 */
public class LayoutBuilder {
    private static final LayoutBuilder SINGLETON = new LayoutBuilder();

    public static LayoutBuilder getInstance() {
        return SINGLETON;
    }

    private LayoutBuilder() {

    }

    public LayoutNode createLayout(DockingPort port) {
        if(port==null) {
            return null;
        }
        return createLayoutImpl(port);
    }

    private LayoutNode createLayoutImpl(DockingPort port) {
        DockingPortNode node = new DockingPortNode();
        node.setUserObject(port);
        Component docked = port.getDockedComponent();
        link(node, docked);
        return node;
    }

    private LayoutNode createLayout(JSplitPane split) {
        String region = (String)SwingUtility.getClientProperty(split, DockingConstants.REGION);
        Component left = split.getLeftComponent();
        Component right = split.getRightComponent();

        float percent;
        if (split instanceof DockingSplitPane && ((DockingSplitPane) split).getPercent() != -1) {
            percent = (float) ((DockingSplitPane) split).getPercent();
        } else {
            percent = SwingUtility.getDividerProportion(split);
        }

        SplitNode node = new SplitNode(split.getOrientation(), 0, percent, null);
        node.setDockingRegion(region);

        link(node, left);
        link(node, right);

        return node;
    }

    private LayoutNode createLayout(Dockable dockable) {
        if(dockable==null) {
            return null;
        }

        DockableNode node = new DockableNode();
        node.setDockableId(dockable.getPersistentId());
        return node;
    }

    private LayoutNode[] createLayout(JTabbedPane tabs) {
        int len = tabs.getComponentCount();
        LayoutNode[] nodes = new LayoutNode[len];
        for(int i=0; i<len; i++) {
            Component comp = tabs.getComponent(i);
            Dockable dockable = DockingManager.getDockable(comp);
            nodes[i] = createLayout(dockable);
        }
        return nodes;
    }

    private void link(LayoutNode node, Component child) {
        if(child instanceof DockingPort) {
            LayoutNode childNode = createLayoutImpl((DockingPort)child);
            link(node, childNode);
        } else if(child instanceof JSplitPane) {
            LayoutNode childNode = createLayout((JSplitPane)child);
            link(node, childNode);
        } else if (child instanceof JTabbedPane) {
            LayoutNode[] children = createLayout((JTabbedPane)child);
            for(int i=0; i<children.length; i++) {
                link(node, children[i]);
            }
        } else {
            Dockable dockable = DockingManager.getDockable(child);
            LayoutNode childNode = createLayout(dockable);
            link(node, childNode);
        }
    }

    private void link(LayoutNode parent, LayoutNode child) {
        if(child!=null) {
            parent.add(child);
        }
    }
}
