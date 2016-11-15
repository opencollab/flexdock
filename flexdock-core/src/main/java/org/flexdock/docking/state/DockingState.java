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
package org.flexdock.docking.state;

import java.awt.Point;
import java.io.Serializable;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;

/**
 *
 * @author Mateusz Szczap
 */
@SuppressWarnings(value = { "serial" })
public class DockingState implements Cloneable, Serializable, DockingConstants {

    private String dockableId;

    private String relativeParentId;

    private String region = UNKNOWN_REGION;

    private float splitRatio = UNINITIALIZED_RATIO;

    private String floatingGroup;

    //if the view is minimized we store the dockbar edge to which it is minimized
    private int minimizedConstraint = MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT;

    private DockingPath dockingPath;

    private int centerX = DockingConstants.UNINITIALIZED;

    private int centerY = DockingConstants.UNINITIALIZED;

    public DockingState(String dockableId) {
        this.dockableId = dockableId;
    }

    public Dockable getDockable() {
        return DockingManager.getDockable(this.dockableId);
    }

    public String getDockableId() {
        return this.dockableId;
    }

    public float getSplitRatio() {
        return this.splitRatio;
    }

    public void setSplitRatio(float ratio) {
        this.splitRatio = ratio;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getMinimizedConstraint() {
        return minimizedConstraint;
    }

    public String getFloatingGroup() {
        return this.floatingGroup;
    }

    public boolean isFloating() {
        return this.floatingGroup!=null;
    }

    public boolean isMinimized() {
        return this.minimizedConstraint!=MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT;
    }

    public boolean hasDockingPath() {
        return this.dockingPath!=null;
    }

    public DockingPath getPath() {
        return this.dockingPath;
    }

    public void setPath(DockingPath path) {
        this.dockingPath = path;
    }

    public void setMinimizedConstraint(int constraint) {
        this.minimizedConstraint = constraint;
        if(constraint!=MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT) {
            this.floatingGroup = null;
        }
    }

    public void setFloatingGroup(String group) {
        this.floatingGroup = group;
        if(group!=null) {
            this.minimizedConstraint = MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT;
        }
    }

    public Dockable getRelativeParent() {
        return DockingManager.getDockable(this.relativeParentId);
    }

    public String getRelativeParentId() {
        return this.relativeParentId;
    }

    public void setRelativeParent(Dockable parent) {
        String parentId = parent==null? null: parent.getPersistentId();
        setRelativeParentId(parentId);
    }

    public void setRelativeParentId(String relativeParentId) {
        this.relativeParentId = relativeParentId;
    }

    public String toString() {
        return "DockingState[id=" + this.dockableId +
               "; center=[" + centerX + "%," + centerY + "%]" +
               "; parent=" + this.relativeParentId +
               "; region=" + this.region + "; ratio=" + this.splitRatio +
               "; float=" + this.floatingGroup + "; minimization=" + this.minimizedConstraint + "; ]";
    }

    public int getCenterX() {
        return centerX;
    }
    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }
    public int getCenterY() {
        return centerY;
    }
    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public void setCenter(Point p) {
        centerX = p==null? 0: p.x;
        centerY = p==null? 0: p.y;
    }

    public Point getCenterPoint() {
        if (this.centerX == DockingConstants.UNINITIALIZED || this.centerY == DockingConstants.UNINITIALIZED) {
            return null;
        }
        return new Point(this.centerX, this.centerY);
    }

    public boolean hasCenterPoint() {
        return (centerX != DockingConstants.UNINITIALIZED && centerY != DockingConstants.UNINITIALIZED);
    }

    public Object clone() {
        DockingState dockingStateClone = new DockingState(this.dockableId);

        dockingStateClone.relativeParentId = this.relativeParentId;
        dockingStateClone.region = this.region;
        dockingStateClone.splitRatio = this.splitRatio;
        dockingStateClone.floatingGroup = this.floatingGroup;
        dockingStateClone.minimizedConstraint = this.minimizedConstraint;
        dockingStateClone.dockingPath = this.dockingPath==null? null: (DockingPath)this.dockingPath.clone();
        dockingStateClone.centerX = centerX;
        dockingStateClone.centerY = centerY;

        return dockingStateClone;
    }

}
