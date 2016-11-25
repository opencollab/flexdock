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
package org.flexdock.dockbar.event;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.flexdock.dockbar.DockbarManager;
import org.flexdock.dockbar.ViewPane;
import org.flexdock.dockbar.layout.DockbarLayout;
import org.flexdock.dockbar.layout.DockbarLayoutManager;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.props.DockablePropertySet;
import org.flexdock.docking.state.MinimizationManager;
import org.flexdock.util.RootWindow;

/**
 * @author Christopher Butler
 */
public class ResizeListener extends MouseAdapter implements MouseMotionListener {
    private DockbarManager manager;
    private Dockable dockable;

    private JPanel dragGlassPane;
    private Component cachedGlassPane;
    private RootWindow rootWindow;

    public ResizeListener(DockbarManager mgr) {
        manager = mgr;
        dragGlassPane = new JPanel();
        dragGlassPane.setOpaque(false);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // noop
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dockable = manager.getActiveDockable();
        rootWindow = manager.getWindow();
        cachedGlassPane = rootWindow.getGlassPane();
        rootWindow.setGlassPane(dragGlassPane);
        dragGlassPane.setCursor(manager.getResizeCursor());
        dragGlassPane.setVisible(true);
        manager.setDragging(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dockable = null;
        dragGlassPane.setVisible(false);
        manager.setDragging(false);

        if(rootWindow!=null && cachedGlassPane!=null) {
            rootWindow.setGlassPane(cachedGlassPane);
            cachedGlassPane = null;
            rootWindow = null;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(dockable!=null) {
            handleResizeEvent(e);
        }
    }

    private void handleResizeEvent(MouseEvent me) {
        ViewPane viewPane = manager.getViewPane();
        Point p = SwingUtilities.convertPoint((Component)me.getSource(), me.getPoint(), viewPane.getParent());
        Rectangle viewArea = DockbarLayoutManager.getManager().getViewArea(manager, dockable);

        p.x = Math.max(p.x, 0);
        p.x = Math.min(p.x, viewArea.width);
        p.y = Math.max(p.y, 0);
        p.y = Math.min(p.y, viewArea.height);

        int orientation = manager.getActiveEdge();
        int loc = orientation==MinimizationManager.LEFT || orientation==MinimizationManager.RIGHT? p.x: p.y;
        int dim = orientation==MinimizationManager.LEFT || orientation==MinimizationManager.RIGHT? viewArea.width: viewArea.height;

        if(orientation==MinimizationManager.RIGHT || orientation==MinimizationManager.BOTTOM) {
            loc = dim - loc;
        }

        float percent = loc/(float)dim;
        float minPercent = DockbarLayout.MINIMUM_VIEW_SIZE/(float)dim;
        percent = Math.max(percent, minPercent);

        DockablePropertySet props = dockable.getDockingProperties();
        props.setPreviewSize(percent);
        manager.revalidate();
    }

}
