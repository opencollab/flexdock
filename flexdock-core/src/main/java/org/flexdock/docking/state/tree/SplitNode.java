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

import java.awt.Component;

import javax.swing.JSplitPane;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.DockingStrategy;
import org.flexdock.docking.state.LayoutNode;

/**
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class SplitNode extends DockingNode implements DockingConstants {

    private int orientation;
    private int region;
    private float percentage;
    private String siblingId;
    private String dockingRegion;

    public SplitNode(int orientation, int region, float percentage, String siblingId) {
        this.orientation = orientation;
        this.region = region;
        this.percentage = percentage;
        this.siblingId = siblingId;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public String getSiblingId() {
        return siblingId;
    }

    public void setSiblingId(String siblingId) {
        this.siblingId = siblingId;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("SplitNode[");
        sb.append("orient=").append(getOrientationDesc()).append("; ");
        sb.append("region=").append(getRegionDesc()).append("; ");
        sb.append("percent=").append(percentage).append("%;");
        sb.append("]");
        return sb.toString();
    }

    public String getRegionDesc() {
        switch(region) {
            case TOP:
                return "top";
            case BOTTOM:
                return "bottom";
            case RIGHT:
                return "right";
            default:
                return "left";
        }
    }

    public String getOrientationDesc() {
        return orientation==VERTICAL? "vertical": "horizontal";
    }

    public Object clone() {
        return new SplitNode(orientation, region, percentage, siblingId);
    }

    public String getDockingRegion() {
        return dockingRegion;
    }
    public void setDockingRegion(String dockingRegion) {
        this.dockingRegion = dockingRegion;
    }

    public Object getDockingObject() {
        if(dockingRegion==null) {
            return null;
        }

        if(!(getParent() instanceof DockingPortNode)) {
            return null;
        }

        DockingPortNode superNode = (DockingPortNode)getParent();
        Object userObj = superNode.getUserObject();
        if(!(userObj instanceof DockingPort)) {
            return null;
        }

        DockingPort superPort = (DockingPort)userObj;
        DockingStrategy strategy = superPort.getDockingStrategy();
        return strategy.createSplitPane(superPort, dockingRegion, percentage);
    }

    public JSplitPane getSplitPane() {
        return (JSplitPane)getUserObject();
    }

    public Component getLeftComponent() {
        return getChildComponent(0);
    }

    public Component getRightComponent() {
        return getChildComponent(1);
    }

    private Component getChildComponent(int indx) {
        LayoutNode child = getChild(indx);
        return child==null? null: (Component)child.getUserObject();
    }

    private LayoutNode getChild(int indx) {
        if(indx >= getChildCount()) {
            return null;
        }
        return (LayoutNode)getChildAt(indx);
    }

    protected DockingNode shallowClone() {
        SplitNode clone = new SplitNode(orientation, region, percentage, siblingId);
        clone.dockingRegion = dockingRegion;
        return clone;
    }
}
