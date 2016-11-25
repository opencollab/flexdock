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
package org.flexdock.dockbar;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.state.MinimizationManager;
import org.flexdock.plaf.common.border.SlideoutBorder;


/**
 * @author Christopher Butler
 */
public class Dockbar extends JPanel {
    protected int orientation;
    protected DockbarManager manager;
    protected ArrayList mDocks = new ArrayList();

    static {
        // make sure DockbarLabel is initialized
        Class c = DockbarLabel.class;
    }

    public static int getValidOrientation(int orient) {
        switch (orient) {
            case MinimizationManager.LEFT:
                return MinimizationManager.LEFT;
            case MinimizationManager.RIGHT:
                return MinimizationManager.RIGHT;
            case MinimizationManager.BOTTOM:
                return MinimizationManager.BOTTOM;
            default:
                return MinimizationManager.LEFT;
        }
    }

    public Dockbar(DockbarManager manager, int orientation) {
        this.manager = manager;
        setBorder(new SlideoutBorder());
        setOrientation(orientation);
    }

    void undock(Dockable dockable) {
        DockbarLabel label = getLabel(dockable);

        remove(label);
        mDocks.remove(label);
        getParent().validate();
        repaint();
    }

    public DockbarLabel getLabel(Dockable dockable) {
        if(dockable==null) {
            return null;
        }

        for (Iterator docks = mDocks.iterator(); docks.hasNext();) {
            DockbarLabel label = (DockbarLabel) docks.next();

            if (label.getDockable() == dockable) {
                return label;
            }
        } // for

        return null;
    }

    public boolean contains(Dockable dockable) {
        return getLabel(dockable)!=null;
    }

    public void dock(Dockable dockable) {
        if(dockable==null) {
            return;
        }

        DockbarLabel currentLabel = getLabel(dockable);
        if (currentLabel!=null) {
            currentLabel.setActive(false);
            return;
        }

        DockbarLabel newLabel = new DockbarLabel(dockable.getPersistentId(), getOrientation());
        add(newLabel);
        mDocks.add(newLabel);

        getParent().validate();
        repaint();
    }

    public int getOrientation() {
        return orientation;
    }

    private void setOrientation(int orientation) {
        orientation = getValidOrientation(orientation);
        this.orientation = orientation;

        Border border = getBorder();
        if(border instanceof SlideoutBorder) {
            ((SlideoutBorder)border).setOrientation(orientation);
        }

        int boxConstraint = orientation==MinimizationManager.TOP ||
                            orientation==MinimizationManager.BOTTOM? BoxLayout.LINE_AXIS: BoxLayout.PAGE_AXIS;
        setLayout(new BoxLayout(this, boxConstraint));
    }

    @Override
    public Dimension getPreferredSize() {
        if(mDocks.isEmpty()) {
            return new Dimension(0,0);
        }

        DockbarLabel label = (DockbarLabel)getComponent(0);
        return label.getPreferredSize();
    }

    void activate(String dockableId, boolean lock) {
        if(manager!=null) {
            manager.setActiveDockable(dockableId);
            if(lock) {
                manager.getActivationListener().lockViewpane();
            }
        }
    }
}
